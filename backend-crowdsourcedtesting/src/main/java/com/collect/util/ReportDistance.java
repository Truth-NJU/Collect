package com.collect.util;

import com.collect.dto.ReportDTO;

import java.util.*;

public class ReportDistance {

    private static final double small = 0.0001;
    private static final double alpha = 0.8;
    private static final double beta = 2;
    private static final double big = 100000000;

    public static double reportDistance(ReportDTO r1, ReportDTO r2, Map<String,Double> table){
        double wordDis=calWordDis(r1,r2,table);
        //double wordDis=calWordDis(r1,r2);
        double photoDis=calPhotoDis(r1,r2);
        System.out.println(wordDis+" "+photoDis);
        if (Math.abs(wordDis)<small){
            return 0;
        }else if (Math.abs(photoDis)<small){
            return alpha*wordDis;
        }else{
            return (1+beta*beta)*(wordDis*photoDis)/(wordDis+beta*beta*photoDis);
        }
    }

    private static double calPhotoDis(ReportDTO r1, ReportDTO r2) {
        double dis= PhotoSimilarUtil.comparePhotos(r1.getPaths(),r2.getPaths());
        if (dis==0){
            return big;
        }else {
            return -Math.log(dis);
        }
    }

    private static double calWordDis(ReportDTO r1, ReportDTO r2) {
        double dis= HanlpUtil.calculateSimilarity(r1.getNote(),r2.getNote());
        if (dis==0){
            return big;
        }else {
            return -Math.log(dis);
        }
    }

    private static double calWordDis(ReportDTO r1, ReportDTO r2, Map<String,Double> table) {
        String tar1=AnnotationUtil.convert(r1.getNote()).concat(AnnotationUtil.convert(r1.getSteps())).replaceAll("/","");
        String tar2=AnnotationUtil.convert(r2.getNote()).concat(AnnotationUtil.convert(r2.getSteps())).replaceAll("/","");
        Map<String,Double> map1=getW(tar1,table);
        Map<String,Double> map2=getW(tar2,table);
        Set<String> setAll=new HashSet<>(map1.keySet());
        setAll.addAll(map2.keySet());
        double ans=0;
        for (String s : setAll) {
            ans+=Math.pow(map1.getOrDefault(s,0.0)-map2.getOrDefault(s,0.0),2);
        }
        ans=Math.sqrt(ans);
        return ans;
    }

    private static Map<String, Double> getW(String tar, Map<String,Double> table) {
        Map<String,Integer> map1=HanlpUtil.getVNA(tar);
        Map<String,Double> ans=new HashMap<>();
        for (Map.Entry<String, Integer> entry : map1.entrySet()) {
            ans.put(entry.getKey(),Math.log(1+entry.getValue())*Math.log(1+table.get(entry.getKey())));
        }
        return ans;
    }
}
