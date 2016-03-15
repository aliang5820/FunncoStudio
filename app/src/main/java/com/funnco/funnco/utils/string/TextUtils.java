package com.funnco.funnco.utils.string;

/**
 * 字符串判断
 * Created by user on 2015/6/25.
 */
public class TextUtils {

    /**
     * 是否为空
     * @param text
     * @return
     */
    public static boolean isNull(String text){
        return android.text.TextUtils.isEmpty(text) || android.text.TextUtils.equals("null",text.toLowerCase());
    }

    /**
     * 对单个手机号进行校验
     * @param text
     * @return 是否为电话号码
     */
    public static boolean isPhoneNumber(String text){
//        if (isNull(text)){
//            return false;
//        }
        text = text.trim();
        if (!text.matches("1[0-9]{10}") || !text.startsWith("1")){
            return false;
        }
        return true;
    }

    /**
     * 对多个电话号码的字符串进行校验
     * @param text 需要剪切的电话号码字符串
     * @param spilt 分隔符
     * @return 是否为全部为电话号码
     */
    public static boolean isPhoneNumber(String text,String spilt){
        if (isNull(text) || isNull(spilt)){
            return false;
        }
        spilt = spilt.trim();
        String []tels = text.split(spilt);
        for (String tel : tels){
            if (isPhoneNumber(tel)){
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 判断两个对象是否相同
     * @param s1
     * @param s2
     * @return
     */
    public static boolean equals(CharSequence s1,CharSequence s2){
        return android.text.TextUtils.equals(s1,s2);
    }
}
