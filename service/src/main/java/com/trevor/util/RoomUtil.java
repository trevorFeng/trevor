package com.trevor.util;

import com.google.common.primitives.Ints;
import com.trevor.bo.RoomPoke;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trevor
 * @date 2019/3/13 17:38
 */
public class RoomUtil {

    /**
     * key为roomRecord的id，key为每一句的对局情况
     */
    public static final Map<Long , List<RoomPoke>> roomPokes = new ConcurrentHashMap<>(2<<15);

    public static void main(String[] args) {
        List<Integer> qianQu = new ArrayList<>();
        qianQu.add(6);
        qianQu.add(11);
        qianQu.add(14);
        qianQu.add(16);
        qianQu.add(19);
        qianQu.add(23);
        qianQu.add(24);
        qianQu.add(25);
        qianQu.add(28);
        qianQu.add(29);
        qianQu.add(31);
        qianQu.add(35);

        List<Integer> houQu = new ArrayList<>();
        houQu.add(1);
        houQu.add(6);
        houQu.add(4);
        houQu.add(9);

        for (int i=0;i<5;i++) {
            int[] qian = randomArray(0 ,qianQu.size()-1 ,4);
            List<Integer> nums = Ints.asList(qian);
            int[] hou = randomArray(0 ,houQu.size()-1 ,2);
            List<Integer> nums1 = Ints.asList(hou);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(21);
            stringBuilder.append(",");
            for (Integer num : nums) {
                stringBuilder.append(qianQu.get(num));
                stringBuilder.append(",");
            }
            stringBuilder.append("+");
            for (Integer num1 : nums1) {
                stringBuilder.append(houQu.get(num1));
                stringBuilder.append(",");
            }
            System.out.println(stringBuilder.toString());
        }




    }

    public static int[] randomArray(int min,int max,int n){
        int len = max-min+1;

        if(max < min || n > len){
            return null;
        }

        //初始化给定范围的待选数组
        int[] source = new int[len];
        for (int i = min; i < min+len; i++){
            source[i-min] = i;
        }

        int[] result = new int[n];
        Random rd = new Random();
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            //待选数组0到(len-2)随机一个下标
            index = Math.abs(rd.nextInt() % len--);
            //将随机到的数放入结果集
            result[i] = source[index];
            //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
            source[index] = source[len];
        }
        return result;
    }

}
