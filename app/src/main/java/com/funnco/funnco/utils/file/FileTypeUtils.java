package com.funnco.funnco.utils.file;

import com.funnco.funnco.utils.string.TextUtils;

/**
 * 根据图片路径返回图片类型
 * Created by user on 2015/5/29.
 * @author Shawn
 */
public class FileTypeUtils {
    public static  String prc = "image/";
    public static String getType(String imagePath){
        if (TextUtils.isNull(imagePath)){
            return "";
        }else{
            return prc + imagePath.substring(imagePath.lastIndexOf(".") + 1, imagePath.length());
        }
    }
}
