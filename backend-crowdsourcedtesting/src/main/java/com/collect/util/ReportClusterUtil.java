package com.collect.util;

import com.collect.dto.ReportDTO;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizers.ChineseWordTokenizer;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class ReportClusterUtil {

//    public static void main(String[] args) {
//        List<ReportDTO> list=new ArrayList<>();
//        list.add(new ReportDTO(1,1,new ArrayList<>(),"我吃饭","我玩手机","dsj"));
//        list.add(new ReportDTO(1,1,new ArrayList<>(),"我玩手机","我吃屎","dsj"));
//        reportCluster(list);
//    }

    public static List<List> reportCluster(List<ReportDTO> reportDTOs){
        int n=reportDTOs.size();
        //tars记录了所有的文本（去批注）
        List<String> tars=new ArrayList<>();
        for (ReportDTO reportDTO : reportDTOs) {
            tars.add(AnnotationUtil.convert(reportDTO.getNote()).concat(AnnotationUtil.convert(reportDTO.getSteps())).replaceAll("/",""));
        }
        Map<String,Double> DFij=new HashMap<>();
//        for (String tar : tars) {
//            Map<String,Integer> vnaTemp=HanlpUtil.getVNA(tar);
//            for (Map.Entry<String, Integer> entry : vnaTemp.entrySet()) {
//                if (!DFij.containsKey(entry.getKey())){
//                    int count=0;
//                    for (String s : tars) {
//                        if (s.contains(entry.getKey())){
//                            count++;
//                        }
//                    }
//                    DFij.put(entry.getKey(),(double)n/count);
//                }
//            }
//        }

        Map<String,Integer> vnaAll=new HashMap<>();
        for (String tar:tars){
            Map<String,Integer> vnaTemp=HanlpUtil.getVNA(tar);
            for (Map.Entry<String, Integer> entry : vnaTemp.entrySet()) {
                vnaAll.put(entry.getKey(), vnaAll.getOrDefault(entry.getKey(),0)+1);
            }
        }
        for (Map.Entry<String, Integer> entry : vnaAll.entrySet()) {
            DFij.put(entry.getKey(), (double)n/entry.getValue());
        }

        double[][] dis=new double[n][n];
        for (int i=0;i<n;i++){
            dis[i][i]=0;
            for (int j=i+1;j<n;j++){
                dis[i][j]=ReportDistance.reportDistance(reportDTOs.get(i),reportDTOs.get(j),DFij);
                dis[j][i]=dis[i][j];
                System.out.println(i+" "+j+" "+dis[i][j]);
            }
        }
        List<Set<Integer>> cluster_set=Cluster.cluster(dis,1.2);


        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(600);
        frequencyAnalyzer.setMinWordLength(2);
        frequencyAnalyzer.setWordTokenizer(new ChineseWordTokenizer());

        int i=0;
        List<String> paths=new ArrayList<>();
        String dirPath="file"+ File.separator+"TaskClusters"+File.separator+reportDTOs.get(0).getTaskId();
        File file=new File(dirPath);
        if (!file.exists())
            file.mkdirs();
        for (Set<Integer> set : cluster_set) {
            Map<String,Integer> vnaSAll=new HashMap<>();
            List<WordFrequency> wordFrequencies = new ArrayList<>();
            for (Integer integer : set) {
                Map<String,Integer> vnaTemp=HanlpUtil.getVNA(tars.get(integer));
                for (Map.Entry<String, Integer> entry : vnaTemp.entrySet()) {
                    vnaSAll.put(entry.getKey(), vnaAll.getOrDefault(entry.getKey(),0)+ entry.getValue());
                }
            }
            for (Map.Entry<String, Integer> entry : vnaSAll.entrySet()) {
                wordFrequencies.add(new WordFrequency(entry.getKey(), entry.getValue()));
            }
            java.awt.Font font = new java.awt.Font("STSong-Light", 2, 18);
            //设置图片分辨率
            Dimension dimension = new Dimension(350, 350);
            //此处的设置采用内置常量即可，生成词云对象
            WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
            //设置边界及字体
            wordCloud.setPadding(2);
            //因为我这边是生成一个圆形,这边设置圆的半径
            wordCloud.setBackground(new CircleBackground(170));
            wordCloud.setFontScalar(new SqrtFontScalar(12, 42));
            //设置词云显示的三种颜色，越靠前设置表示词频越高的词语的颜色
            wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));
            wordCloud.setKumoFont(new KumoFont(font));
            wordCloud.setBackgroundColor(new Color(255, 255, 255));
            wordCloud.build(wordFrequencies);
            //生成词云图路径
            String path=dirPath+File.separator+i++ +".png";
            paths.add(path);
            wordCloud.writeToFile(path);
        }
        List<List> ret=new ArrayList<>();
        ret.add(cluster_set);
        ret.add(paths);
        return ret;
    }

}
