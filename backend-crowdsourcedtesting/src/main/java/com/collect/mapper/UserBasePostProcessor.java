package com.collect.mapper;

import com.collect.annotation.Calculate;
import com.collect.exception.MyException;
import com.collect.po.Attribute;
import com.collect.po.UserWorkList;
import com.collect.vo.http.RuleHttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.*;

@Component
@Order(1)
public class UserBasePostProcessor extends TaskPostProcessor {

    @Autowired
    WorkMapper workMapper;

    @Autowired
    AttributeMapper attributeMapper;

    // 掩码，计算相似度时仅取bit为1的属性，默认为全选 0b111111111111
    private int attributeMask;

    // 属性总数目
    private int num;

    private List<String> attributeDescription;

    @Override
    public Map<String, Object> getExtra() {
        Map<String, Object> map = new HashMap<>();
        map.put("attributeMask", attributeMask);
        map.put("attributeDescription", attributeDescription);
        return map;
    }

    @Override
    public void update(Map<String, Object> extra) {
        int mask = (Integer) extra.get("attributeMask");
        if (mask < 0 || mask >= (1 << (num + 1))) {
            throw MyException.of(RuleHttpStatus.RULE_MASK_ERROR);
        }
        attributeMask = mask;
    }

    @PostConstruct
    void init() {
        disable();
        id = 1;
        name = "用户特征相似度推荐";
        desc = "使用多维用户特征比较用户之间的相似度";
        Field[] fields = Attribute.class.getDeclaredFields();
        attributeDescription = new ArrayList<>();
        for (Field f : fields) {
            Calculate calculate;
            if ((calculate = f.getAnnotation(Calculate.class)) != null) {
                attributeDescription.add(calculate.desc());
            }
        }
        // 初始化attributeMask和rule相关信息
        num = attributeDescription.size();
        attributeMask = 0;
        for (int i = 0; i < num; i++) {
            attributeMask |= (1 << i);
        }
    }

    private List<Double> getAttribute(Attribute attribute) {
        Field[] fields = attribute.getClass().getDeclaredFields();
        List<Double> res = new ArrayList<>();
        for (Field f : fields) {
            Calculate calculate;
            if ((calculate = f.getAnnotation(Calculate.class)) != null) {
                f.setAccessible(true);
                double number;
                try {
                    Object raw = f.get(attribute);
                    if (raw instanceof Integer) {
                        number = (int) raw;
                    } else {
                        number = (double) raw;
                    }
                    res.add(Math.pow((number + calculate.add()) * calculate.mul(), calculate.pow()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }

    @Override
    void process(List<Integer> tasks, int userId, Map<Integer, Double> score) {
        Map<String, Object> selectByUserId = new HashMap<>();
        selectByUserId.put("userId", userId);
        // 查询目标用户的属性，计算自身向量
        Attribute target = attributeMapper.selectByMap(selectByUserId).get(0);
        List<Double> targetAttributes = getAttribute(target);
        int sum = target.getSum();
        double aa = 0.0;
        for (int i = 0; i < targetAttributes.size(); i++) {
            if ((attributeMask & (1 << i)) != 0) {
                aa += Math.pow(targetAttributes.get(i) / sum, 2);
            }
        }
        // 查询做过任务集合中候选任务的全部用户，获取其全部任务
        List<UserWorkList> userWorkLists = workMapper.findUsersTaskPartIn(tasks);
        // 计算各用户与目标用户的相似度
        Map<Integer, Double> similar = new HashMap<>();
        for (UserWorkList userWork : userWorkLists) {
            selectByUserId.put("userId", userWork.getUserId());
            Attribute other = attributeMapper.selectByMap(selectByUserId).get(0);
            List<Double> otherAttributes = getAttribute(other);
            int sumOther = other.getSum();
            double bb = 0.0, ab = 0.0;
            for (int i = 0; i < targetAttributes.size(); i++) {
                if ((attributeMask & (1 << i)) != 0) {
                    bb += Math.pow(otherAttributes.get(i) / sumOther, 2);
                    ab += (targetAttributes.get(i) / sum) * (otherAttributes.get(i) / sumOther);
                }
            }
            similar.put(userWork.getUserId(), ab / (Math.sqrt(aa) * Math.sqrt(bb)));
        }
        // 将和自己的相似度设为0
        similar.put(userId, 0.0);
        // 根据相似度排序，取前topK用户，若数目不足topK，则取全部
        final int topK = 10;
        userWorkLists.sort(Comparator.comparingDouble(value -> similar.get(value.getUserId())));
        // 对topK用户计算各任务分值
        for (int s = Math.max(0, userWorkLists.size() - topK), cnt = userWorkLists.size() - s; s < userWorkLists.size(); s++) {
            for (int taskId : userWorkLists.get(s).getTaskId()) {
                final int finalS = s;
                score.computeIfPresent(taskId, (k, v) -> v += similar.get(userWorkLists.get(finalS).getUserId()) / cnt);
            }
        }
    }
}
