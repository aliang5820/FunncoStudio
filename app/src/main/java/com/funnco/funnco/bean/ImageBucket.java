package com.funnco.funnco.bean;

import java.util.List;

/**
 * Created by user on 2015/5/27.
 * 相册
 * count 相册容量
 * backetName 相册名字
 * imageList 相册中图片列表
 */
public class ImageBucket {
    /**
     * 图片的文件夹路径
     */
    private String dir;
    /**
     * 相册容量
     */
    public int count = 0;
    /**
     * 相册名字
     */
    public String bucketName;
    /**
     * 相册第一张图片地址
     */
    private String firstImagePath;
    /**
     * 相册中所有图片对象的集合
     */
    public List<ImageItem> imageList;
}
