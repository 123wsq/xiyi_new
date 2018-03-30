package com.example.wsq.android.utils;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wsq on 2018/1/10.
 */

public class DataFormat {

    /**
     * 将字符串转换成数字
     * @param str
     * @return
     */
    public static int onStringForInteger(String str){

       try {
           double d = Double.parseDouble(str);
           return (int) d;
       }catch (Exception e){
           e.printStackTrace();
       }
       return -1;
    }

    /**
     * 将字符串数据转换成Long类型的数字
     * @param str
     * @return
     */
    public static long onStringForLong(String str){

        if (TextUtils.isEmpty(str)){
            return -1;
        }

//        if (!ValidateDataFormat.isNumber(str)){
//
//            return -1;
//        }
        long num = Long.parseLong(str);
        return num;
    }

    /**
     * 将字符串转换成小数
     * @param str
     * @return
     */
    public static double onStringForFloat(String str){

        if (TextUtils.isEmpty(str)){
            Logger.d(str);
            return -1.0f;
        }

//        if (!ValidateDataFormat.isNumber(str)){
//            Logger.d(str);
//            return -1.0f;
//        }
        try {
            double d = Double.parseDouble(str);
            return d;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 1.0f;
    }



}
