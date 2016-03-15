package com.funnco.funnco.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funnco.funnco.R;
import com.funnco.funnco.bean.ImageBucket;
import com.funnco.funnco.bean.ImageItem;
import com.funnco.funnco.com.funnco.funnco.callback.ImageCallBack;
import com.funnco.funnco.impl.Post;
import com.funnco.funnco.utils.bimp.Bimp;
import com.funnco.funnco.utils.bimp.BitmapCache;
import com.funnco.funnco.utils.log.LogUtils;

import java.util.List;

/**
 * Created by user on 2015/6/1.
 */
public class BucketChooseAdatper extends BaseAdapter {

    private Context context;
    private Intent intent;
    private DisplayMetrics dm;
    private BitmapCache cache;
    private List<ImageBucket> contentList;

    final String TAG = getClass().getSimpleName();


    public BucketChooseAdatper(Context context, List<ImageBucket> contentList){
        cache = new BitmapCache();
        this.contentList  = contentList;
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        intent = ((Activity) context).getIntent();
        dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public int getCount() {
        return contentList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    ImageCallBack callback = new ImageCallBack() {
        @Override
        public void imageLoad(ImageView imageView, Bitmap bitmap,
                              Object... params) {
            if (imageView != null && bitmap != null) {
                String url = (String) params[0];
                if (url != null && url.equals((String) imageView.getTag())) {
                    ((ImageView) imageView).setImageBitmap(bitmap);
                } else {
                    LogUtils.e(TAG, "callback, bmp not match");
                }
            } else {
                LogUtils.e(TAG, "callback, bmp null");
            }
        }
    };

    Holder holder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item_bucketchoose, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        //如果相册不为空  则取第一张照片作为封面
        String path;
        if (contentList.get(position).imageList != null){
            //封面图片路径
            path = contentList.get(position).imageList.get(0).imagePath;
            //设置文件夹名
            holder.tvFolderName.setText(contentList.get(position).bucketName);
            //设置文件数量
            holder.tvFileCount.setText(contentList.get(position).count+"");
        }else{
            path = "bucket_no_picture";
        }
        if (path.contains("bucket_no_picture")){
            holder.imageView.setImageResource(R.mipmap.plugin_camera_no_pictures);
        }else{
            ImageItem item = contentList.get(position).imageList.get(0);
            holder.imageView.setTag(item.imagePath);
            cache.displayBmp(holder.imageView, item.thumbnailPath, item.imagePath, callback);
        }
        convertView.setOnClickListener(new RelativeLayoutClickListener(position, (RelativeLayout) convertView));
        return convertView;
    }

    class Holder{
        ImageView imageView;//封面
        TextView tvFolderName;//文件夹名称
        TextView tvFileCount;//图片数量
        public Holder(View convertView){
            imageView = (android.widget.ImageView) convertView.findViewById(R.id.iv_bucketchoose_face);
            tvFolderName = (TextView) convertView.findViewById(R.id.tv_bucketchoose_bucketname);
            tvFileCount = (TextView) convertView.findViewById(R.id.tv_bucketchoose_imagecount);
        }
    }

    //为每一个文件夹创建监听器
    private class RelativeLayoutClickListener implements View.OnClickListener {
        int position;
        RelativeLayout relativeLayout;
        public RelativeLayoutClickListener(int position, RelativeLayout relativeLayout){
            this.position = position;
            this.relativeLayout = relativeLayout;
        }

        @Override
        public void onClick(View v) {
//            Bimp.tempSelectBitmap.clear();
            Bimp.tempCheckedBucketBitmap.clear();
            Bimp.tempCheckedBucketBitmap.addAll(contentList.get(position).imageList);
            if (post != null) {
                post.post(position);
            }
//            Intent intent = new Intent();
//            String folderName = contentList.get(position).bucketName;
//            intent.putExtra("folderName", folderName);
//            intent.setClass(context, AddWorkActivity.class);
//            context.startActivity(intent);
        }
    }
   Post post;
    public void setPost(Post post){
        this.post = post;
    }
}
