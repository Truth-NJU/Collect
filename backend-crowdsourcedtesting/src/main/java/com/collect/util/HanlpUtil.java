package com.collect.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

import java.util.*;
import java.util.stream.Collectors;

public class HanlpUtil {
    public static double calculateSimilarity(String s1,String s2) {
        List<String> sent1Words = getSplitWords(s1);
        List<String> sent2Words = getSplitWords(s2);
        List<String> allWords = mergeList(sent1Words, sent2Words);
        int[] statistic1 = statistic(allWords, sent1Words);
        int[] statistic2 = statistic(allWords, sent2Words);
        double dividend = 0;
        double divisor1 = 0;
        double divisor2 = 0;
        for (int i = 0; i < statistic1.length; i++) {
            dividend += statistic1[i] * statistic2[i];
            divisor1 += Math.pow(statistic1[i], 2);
            divisor2 += Math.pow(statistic2[i], 2);
        }
        return dividend / (Math.sqrt(divisor1) * Math.sqrt(divisor2));
    }


    public static Map<String,Integer> getVNA(String sentence){
        List<String> temp=HanLP.segment(sentence).stream().map(Term::toString)
                .filter(a->a.matches("(\\S*/n\\S*)|(\\S*/a)")).collect(Collectors.toList());
        for (int i = 0; i < temp.size(); i++) {
            temp.set(i,temp.get(i).substring(0,temp.get(i).indexOf('/')));
        }
        Map<String,Integer> ans=new HashMap<>();
        for (String s : temp) {
            ans.put(s,ans.getOrDefault(s,0)+1);
        }
        return ans;
    }

    private static List<String> mergeList(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<>();
        result.addAll(list1);
        result.addAll(list2);
        return result.stream().distinct().collect(Collectors.toList());
    }

    private static int[] statistic(List<String> allWords, List<String> sentWords) {
        int[] result = new int[allWords.size()];
        for (int i = 0; i < allWords.size(); i++) {
            result[i] = Collections.frequency(sentWords, allWords.get(i));
        }
        return result;
    }

    private static List<String> getSplitWords(String sentence) {
        return HanLP.segment(sentence).stream().map(a -> a.word).filter(s -> !"`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？ ".contains(s)).collect(Collectors.toList());
    }


    public static void main(String[] args) {
        System.out.println(HanLP.segment("打开成功"));
    }

}
