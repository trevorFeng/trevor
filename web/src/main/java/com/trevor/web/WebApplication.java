package com.trevor.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

/**
 * @author trevor
 * @date 2019/3/1 11:40
 */
@SpringBootApplication
public class WebApplication {

//    public static void main(String[] args) {
//        SpringApplication.run(WebApplication.class, args);
//    }

    public static void main(String[] arges){
        int  m = 0;
        int  n = 13;
        int card = ((byte)m) << 4 | (byte)n;

        int  tmp=0;
        for (int i = 1; i <6 ; i++) {
            for (int j = 1; j <14 ; j++) {
                tmp = ((byte)i) << 4 | (byte)j;
                String hex = Integer.toHexString(tmp);
                System.out.print(hex+" ");

            }
        }

        //System.out.println(card);
    }

}
