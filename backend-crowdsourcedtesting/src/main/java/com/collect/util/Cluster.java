package com.collect.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cluster {

    /**
     * 聚类的算法，采用的距离算法是计算平均距离
     * @param dis 长度为聚类点的个数，dis是一个对称的二维矩阵，dis[i][j]表示i到j的距离
     * @param d 阈值，大于d不予合并
     * @return
     */
    public static List<Set<Integer>> cluster(double[][] dis, double d){
        int[] b=new int[dis.length];
        for (int i=0;i<dis.length;i++){
            b[i]=i;
        }
        while (true){
            Set<Integer> set=new HashSet<>();
            for (int i=0;i<dis.length;i++){
                set.add(find(b,i));
            }
            List<Integer> list=new ArrayList<>(set);
            if (list.size()==1){
                break;
            }
            double min=Double.MAX_VALUE;
            int ra=-1,rb=-1;
            for (int i=0;i<list.size();i++){
                for (int j=i+1;j<list.size();j++){
                    double dTotal=0,count=0;
                    for (int p=0;p<dis.length;p++){
                        if (find(b, p).equals(list.get(i))){
                            for (int q=0;q<dis.length;q++){
                                if (find(b, q).equals(list.get(j))){
                                    dTotal+=dis[p][q];
                                    count++;
                                }
                            }
                        }
                    }
                    double cd=dTotal/count;
                    if (min>cd){
                        min=cd;
                        ra=list.get(i);
                        rb=list.get(j);
                    }
                }
            }
            if (min>d){
                break;
            }else{
                b[rb]=ra;
            }
        }
        List<Set<Integer>> ans=new ArrayList<>();
        Set<Integer> set=new HashSet<>();
        for (int i=0;i<dis.length;i++){
            set.add(find(b,i));
        }
        for (int i : set) {
            ans.add(new HashSet<>());
            for (int j=0;j<dis.length;j++){
                if (find(b,j)==i){
                    ans.get(ans.size()-1).add(j);
                }
            }
        }
        return ans;
    }

    private static Integer find(int[] b, int t) {
        return b[t]==t?t:(b[t]=find(b,b[t]));
    }

}
