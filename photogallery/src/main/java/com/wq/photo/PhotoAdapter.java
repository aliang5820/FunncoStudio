package com.wq.photo;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by wangqiong on 15/3/27.
 */
public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int CHOSE_MODE_SINGLE = 0;
    public static final int CHOSE_MODE_MULTIPLE = 1;
    //可选择图片熟练
    public int max_chose_count = 9;
    //试图加载器
    LayoutInflater inflater;
    //所有图片的地址
    List<String> imageses;
    //上下文环境
    Context context;
    public static RecyclerView.LayoutParams params;
    //选中的图片
    LinkedHashMap hashmap;
    //图片选择类型（多选/单选）
    public int currentChoseMode;
    //默认在第一个位置实现相机
    boolean isNeedCamera = true;
    //屏幕宽度
    int sWidthPix;
    public PhotoAdapter(Context context, List<String> imageses, int chosemode) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.imageses = imageses;
        sWidthPix = context.getResources().getDisplayMetrics().widthPixels;
        //图片进行3列显示 并且是高宽相等
        params = new RecyclerView.LayoutParams(sWidthPix / 3, sWidthPix / 3);
        int dp3 = dip2px(context, 1);
        params.setMargins(dp3, dp3, dp3, dp3);
        currentChoseMode = chosemode;
        //得到上一级Activity存放图片的链表集合
        hashmap = ((MediaChoseActivity) context).getImageChoseMap();
    }

    /**
     * 返回当前选中的照片集合的对象
     * @return
     */
    public LinkedHashMap getCHoseImages() {
        return hashmap;
    }

    public void setmax_chose_count(int max_chose_count) {
        this.max_chose_count = max_chose_count;
    }

    String imgdir;

    public void setDir(String dir) {
        imgdir = dir;
    }


    public String getDir() {
        if (imgdir.equals("")) {
            return "";
        } else {
            return imgdir + "/";
        }
    }

    /**
     * 是否在第一个item现实相机
     *
     * @param isNeedCamera
     */
    public void setNeedCamera(boolean isNeedCamera) {
        this.isNeedCamera = isNeedCamera;
    }

    /**
     * DIP转换成PX
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        //第一步获得屏幕的密度
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        //修改---首相移除拍照功能
//        if (viewType == TYPE_CAMERA) {
//            holder = new CameraViewHolder(inflater.inflate(R.layout.item_photo_camera_layout, parent, false));
//        } else {
            holder = new ImageViewHolder(inflater.inflate(R.layout.item_photo_image_layout, parent, false));
//        }
        return holder;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //根据不同类型进行布局---修改过后仅是显示图片而没有拍照功能
        if (getItemViewType(position) == TYPE_IMAGE) {//
            final ImageViewHolder ivholder = (ImageViewHolder) holder;
            //得到图片文件路径
            final String images = getDir() + imageses.get(position);
            //使用Paicaoo进行图片的高效率显示
            displayImage(images, ivholder.iv_image);
            if (currentChoseMode == CHOSE_MODE_MULTIPLE) {//多选
                ivholder.checkBox.setVisibility(View.VISIBLE);//选择框可见
                ivholder.checkBox.setOnClickListener(new View.OnClickListener() {//选择按钮添加单击事件
                    @Override
                    public void onClick(View v) {
                        if (hashmap.containsKey(images)) {
                            hashmap.remove(images);
                            ivholder.alpha_view.setVisibility(View.GONE);
                            ivholder.checkBox.setSelected(false);
                        } else {
                            if (hashmap.size() >= max_chose_count) {
                                Toast.makeText(context, "你最多只能选择" + max_chose_count + "张照片", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //选中照片加入到集合中。。。
                            hashmap.put(images, images);
                            ivholder.alpha_view.setVisibility(View.VISIBLE);
                            ivholder.checkBox.setSelected(true);
                        }
                        ((Activity) context).invalidateOptionsMenu();
                    }
                });
                if (hashmap.containsKey(images)) {
                    ivholder.alpha_view.setVisibility(View.VISIBLE);
                    ivholder.checkBox.setSelected(true);
                } else {
                    ivholder.alpha_view.setVisibility(View.GONE);
                    ivholder.checkBox.setSelected(false);
                }
                ivholder.alpha_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MediaChoseActivity) context).starPriview(getCHoseImages(), images);
                    }
                });
            } else {//单选情况
                ivholder.checkBox.setVisibility(View.GONE);
                ivholder.alpha_view.setVisibility(View.GONE);
                ivholder.iv_image.setClickable(true);
                ivholder.iv_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getCHoseImages().clear();
                        getCHoseImages().put(images, images);
                        //将选中该的照片传回主Activity
                        ((MediaChoseActivity) context).starPriview(getCHoseImages(), images);
                    }
                });
            }
        }else{//进行拍照
            CameraViewHolder holder1= (CameraViewHolder) holder;
            holder1.camera_lin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(currentChoseMode==CHOSE_MODE_MULTIPLE){//多选进行拍照
                        if(getCHoseImages().size()<max_chose_count){
                            ((MediaChoseActivity)context).sendStarCamera();
                        }else{
                            Toast.makeText(context, "你最多只能选择" + max_chose_count + "张照片", Toast.LENGTH_SHORT).show();
                        }
                    }else{//单选时进行拍照
                        if(getCHoseImages().size()>0){
                            getCHoseImages().clear();
                        }
                        ((MediaChoseActivity)context).sendStarCamera();
                    }
                }
            });
        }
    }

    /**
     * imageScaleType:
     * EXACTLY :图像将完全按比例缩小的目标大小
     * EXACTLY_STRETCHED:图片会缩放到目标大小完全
     * IN_SAMPLE_INT:图像将被二次采样的整数倍
     * IN_SAMPLE_POWER_OF_2:图片将降低2倍，直到下一减少步骤，使图像更小的目标大小
     * NONE:图片不会调整
     *
     * @param url
     * @param view
     */
    public void displayImage(String url, ImageView view) {
//        com.wq.photo.ImageLoader.getInstance(3, com.wq.photo.ImageLoader.Type.LIFO)
//                .loadImage(url, view,sWidthPix/3,sWidthPix/3);
        // 显示图片
        Picasso.with(context)
                .load("file://"+url)
                .resize(sWidthPix/3,sWidthPix/3)
                .placeholder(R.drawable.loadfaild)
//                        .error(R.drawable.default_error)
                .centerCrop()
                .into(view);
    }

    public static int TYPE_IMAGE = 10;
    public static int TYPE_CAMERA = 11;

    @Override
    public int getItemViewType(int position) {
        //修改
//        if (position == 0 && isNeedCamera) {
//            return TYPE_CAMERA;
//        }
        return TYPE_IMAGE;
    }

    @Override
    public int getItemCount() {
//        if (imageses.size() == 0) {//修改 去掉第一个拍照的item
//            return 1;
//        }
        return imageses.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        ImageButton checkBox;
        View alpha_view;

        public ImageViewHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(params);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            alpha_view = itemView.findViewById(R.id.alpha_view);
            checkBox = (ImageButton) itemView.findViewById(R.id.checkimages);
        }
    }
    public static class CameraViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout camera_lin;
        public CameraViewHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(params);
            camera_lin= (LinearLayout) itemView.findViewById(R.id.camera_lin);
        }
    }


}
