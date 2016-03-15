package com.funnco.funnco.utils.file;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.funnco.funnco.bean.ImageBucket;
import com.funnco.funnco.bean.ImageItem;
import com.funnco.funnco.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2015/5/27.
 */
public class AlbumHelper {

    final String TAG = getClass().getSimpleName();
    private Context context;
    private ContentResolver cr;

    //存放
    private HashMap<String, String> thumbnailList = new HashMap<String, String>();

    private HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();


    private static AlbumHelper instance;

    private AlbumHelper() {
    }

    public static AlbumHelper getHelper() {
        if (instance == null) {
            instance = new AlbumHelper();
        }
        return instance;
    }

    //获得内容接受器
    public void init(Context context){
        if (this.context == null){
            this.context = context;
            cr = context.getContentResolver();
        }
    }

    /**
     * 根据指定的内容（列名）从系统内容提供者 查询多媒体资源
     */
    public void getThumbnail(){
        String[] projection = {
                MediaStore.Images.Thumbnails._ID,//文件ID
                MediaStore.Images.Thumbnails.IMAGE_ID,//图片ID
                MediaStore.Images.Thumbnails.DATA// 图片文件
        };
        //获得系统查询结果
        Cursor c = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
        getThumbnailColumnData(c);
    }

    /**
     * 将查询到的图片的ImageId和路径Path 存入集合
     * @param c
     */
    public void getThumbnailColumnData(Cursor c){
        if(c.moveToFirst()){
            int _id;
            int image_id;
            String image_path;
            //获得表中列名（即字段对应的列数）
            int _idColumn = c.getColumnIndex(MediaStore.Images.Thumbnails._ID);
            int image_idColumn = c.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
            int dataColumn = c.getColumnIndex(MediaStore.Images.Thumbnails.DATA);

            //分别获取字段值
            do {
                _id = c.getInt(_idColumn);
                image_id = c.getInt(image_idColumn);
                image_path = c.getString(dataColumn);
                //将图片的image_id 以及对应的图片路径存入map集合
                thumbnailList.put(""+image_id, image_path);
            }while (c.moveToNext());
        }
    }


    boolean hasBuildImagesBucketList = false;

    //获取相册列表及列表中内容
    void buildImagesBucketList() {
        long startTime = System.currentTimeMillis();

        getThumbnail();

        String columns[] = new String[] { MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,//直接包含该图片文件的文件夹ID，防止在不同下的文件夹重名
                MediaStore.Images.Media.PICASA_ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME// 直接包含该图片文件的文件
        };
        Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
                null);
        if (cur.moveToFirst()) {
            int photoIDIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int photoNameIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int photoTitleIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int photoSizeIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            int bucketDisplayNameIndex = cur
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
            int picasaIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.PICASA_ID);
            int totalNum = cur.getCount();

            do {
                String _id = cur.getString(photoIDIndex);
                String name = cur.getString(photoNameIndex);
                String path = cur.getString(photoPathIndex);
                String title = cur.getString(photoTitleIndex);
                String size = cur.getString(photoSizeIndex);
                String bucketName = cur.getString(bucketDisplayNameIndex);
                String bucketId = cur.getString(bucketIdIndex);
                String picasaId = cur.getString(picasaIdIndex);

                LogUtils.e(TAG, _id + ", bucketId: " + bucketId + ", picasaId: "
                        + picasaId + " name:" + name + " path:" + path
                        + " title: " + title + " size: " + size + " bucket: "
                        + bucketName + "---");

                ImageBucket bucket = bucketList.get(bucketId);
                if (bucket == null) {
                    bucket = new ImageBucket();
                    bucketList.put(bucketId, bucket);
                    bucket.imageList = new ArrayList<ImageItem>();
                    bucket.bucketName = bucketName;
                }
                bucket.count++;
                ImageItem imageItem = new ImageItem();
                imageItem.imageId = _id;
                imageItem.imagePath = path;
                imageItem.thumbnailPath = thumbnailList.get(_id);
                bucket.imageList.add(imageItem);

            } while (cur.moveToNext());
        }

        Iterator<Map.Entry<String, ImageBucket>> itr = bucketList.entrySet()
                .iterator();
        while (itr.hasNext()) {
            Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr
                    .next();
            ImageBucket bucket = entry.getValue();
            LogUtils.e(TAG, entry.getKey() + ", " + bucket.bucketName + ", "
                    + bucket.count + " ---------- ");
            for (int i = 0; i < bucket.imageList.size(); ++i) {
                ImageItem image = bucket.imageList.get(i);
                LogUtils.e(TAG, "----- " + image.imageId + ", " + image.imagePath
                        + ", " + image.thumbnailPath);
            }
        }
        hasBuildImagesBucketList = true;
        long endTime = System.currentTimeMillis();
        LogUtils.e(TAG, "use time: " + (endTime - startTime) + " ms");
    }

    /**
     * 返回所有相册
     * @param refresh
     * @return
     */
    public List<ImageBucket> getImagesBucketList(boolean refresh) {
        if (refresh || (!refresh && !hasBuildImagesBucketList)) {
            buildImagesBucketList();
        }
        List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
        Iterator<Map.Entry<String, ImageBucket>> itr = bucketList.entrySet()
                .iterator();
        while (itr.hasNext()) {
            Map.Entry<String, ImageBucket> entry = (Map.Entry<String, ImageBucket>) itr
                    .next();
            tmpList.add(entry.getValue());
        }
        return tmpList;
    }

    String getOriginalImagePath(String image_id) {
        String path = null;
        LogUtils.e(TAG, "---(^o^)----" + image_id);
        String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                MediaStore.Images.Media._ID + "=" + image_id, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

        }
        return path;
    }


}
