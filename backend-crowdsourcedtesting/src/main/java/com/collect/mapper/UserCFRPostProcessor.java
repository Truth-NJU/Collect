package com.collect.mapper;

import com.collect.po.UserWorkList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Order(2)
public class UserCFRPostProcessor extends TaskPostProcessor {

    @Autowired
    WorkMapper workMapper;

    @PostConstruct
    void init() {
        id = 2;
        name = "用户-用户协同过滤推荐";
        desc = "使用用户参与的任务比较用户之间相似度进行任务推荐";
    }

    /**
     * 用户协同过滤，查询做过任务集合中候选任务的全部用户，获取其全部任务，
     * 计算各用户与目标用户的相似度，根据相似度排名，取前topK用户，根据相似度为各候选任务评分，评分对topK取平均
     *
     * @param tasks，任务id集合
     * @param userId, 用户id
     * @param score，任务前一次处理的得分，初始为0，得分应该在0-1之间
     */
    @Override
    public void process(List<Integer> tasks, int userId, Map<Integer, Double> score) {
        Map<Integer, Integer> userTaskMapping = new HashMap<>();
        // 查询目标用户领取过的任务，计算 ||A||
        double aa = 0;
        for (int taskId : workMapper.selectPartTasksByUserId(userId)) {
            userTaskMapping.put(taskId, 1);
            aa += 1;
        }
        // 查询做过任务集合中候选任务的全部用户，获取其全部任务
        List<UserWorkList> userWorkLists = workMapper.findUsersTaskPartIn(tasks);
        // 计算各用户与目标用户的相似度
        Map<Integer, Double> similar = new HashMap<>();
        for (UserWorkList userWork : userWorkLists) {
            double ab = 0, bb = 0;
            //对用户的做过的全部任务作为向量
            for (int taskId : userWork.getTaskId()) {
                // 计算 A·B
                ab += userTaskMapping.getOrDefault(taskId, 0);
                // 计算 ||B||
                bb += 1;
            }
            // 计算相似度
            similar.put(userWork.getUserId(), ab / (Math.sqrt(aa) * Math.sqrt(bb)));
        }
        // 将和自己的相似度设为0
        similar.put(userId, 0.0);
        // 根据相似度排序，取前topK用户，若数目不足topK，则取全部
        final int topK = 10;
        userWorkLists.sort(Comparator.comparingDouble(value -> similar.get(value.getUserId())));
        // 对topK用户计算各任务分值
        for (int s = Math.max(0, userWorkLists.size() - topK), cnt = userWorkLists.size() - s; s < userWorkLists.size(); s++) {
//            System.err.println((similar.get(userWorkLists.get(s).getUserId()) / cnt));
            for (int taskId : userWorkLists.get(s).getTaskId()) {
                final int finalS = s;
                score.computeIfPresent(taskId, (k, v) -> v += similar.get(userWorkLists.get(finalS).getUserId()) / cnt);
            }
        }
    }

}
