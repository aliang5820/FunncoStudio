package com.wq.photo;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * 调用媒体选择库
 * 需要在inten中传递2个参数
 * 1. 选择模式 chose_mode  0  //单选 1多选
 * 2. 选择张数 max_chose_count  多选模式默认 9 张
 */
public class MediaChoseActivity extends ActionBarActivity {

    public static final int CHOSE_MODE_SINGLE = 0;//单选
    public static final int CHOSE_MODE_MULTIPLE = 1;//多选  模式
    public int max_chose_count = 1;  //最大选择数
    public LinkedHashMap imasgemap = new LinkedHashMap();
    public LinkedHashSet imagesChose = new LinkedHashSet();
    PhotoGalleryFragment photoGalleryFragment;
    int chosemode = CHOSE_MODE_MULTIPLE;//当前选择模式

    boolean isneedCrop = false;//是否需要裁剪
    int crop_image_w, crop_image_h;//裁剪的宽和高

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_chose);
        FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        //从上一个Activity获得选择类型（单选/多选/单切）
        chosemode = getIntent().getIntExtra("chose_mode", CHOSE_MODE_MULTIPLE);
        if (chosemode == CHOSE_MODE_MULTIPLE) {
            max_chose_count = getIntent().getIntExtra("max_chose_count", 9);
        }
        //是否需要剪裁
        isneedCrop = getIntent().getBooleanExtra("crop", false);
        if (isneedCrop) {
            chosemode = CHOSE_MODE_SINGLE;
            max_chose_count=1;
            crop_image_w = getIntent().getIntExtra("crop_image_w", 720);
            crop_image_h = getIntent().getIntExtra("crop_image_h", 720);
        }
        //将选择图片单张或多张的类型传递过去   以及传递最大的图片选择数
        photoGalleryFragment = PhotoGalleryFragment.newInstance(chosemode, max_chose_count);
        //fragment显示在该容器中
        fragmentTransaction.add(R.id.container, photoGalleryFragment, PhotoGalleryFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }
    boolean isPriview = false;
    public void starPriview(LinkedHashMap map, String currentimage) {
        if(isneedCrop&&!isCropOver){
//            sendStarCrop(currentimage);//修改
            Intent intent = new Intent();
            intent.putExtra("currentimage",currentimage);
            setResult(RESULT_OK, intent);
            finish();
        }else{
            Set<String> keys = map.keySet();
            ArrayList<String> ims = new ArrayList<>();
            int pos = 0;
            int i = 0;
            for (String s : keys) {
                ims.add((String) map.get(s));
                if (map.get(s).equals(currentimage)) {
                    pos = i;
                }
                i++;
            }
            FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container, ImagePreviewFragemnt.newInstance(ims, pos), ImagePreviewFragemnt.class.getSimpleName());
            fragmentTransaction.addToBackStack("con");
            fragmentTransaction.commit();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            isPriview = true;
            invalidateOptionsMenu();
        }
    }

    public Fragment getCurrentFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public LinkedHashMap getImageChoseMap() {
        return imasgemap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_gallery_menu, menu);
        if (isPriview && (chosemode == CHOSE_MODE_MULTIPLE)) {
            menu.findItem(R.id.menu_photo_delete).setVisible(true);
        } else {
            menu.findItem(R.id.menu_photo_delete).setVisible(false);
        }
        if (imasgemap.size() < 1) {
            menu.findItem(R.id.menu_photo_count).setEnabled(false);
            menu.findItem(R.id.menu_photo_count).setVisible(false);
        } else {
            menu.findItem(R.id.menu_photo_count).setEnabled(true);
            menu.findItem(R.id.menu_photo_count).setVisible(true);
            if (chosemode == CHOSE_MODE_MULTIPLE) {
                menu.findItem(R.id.menu_photo_count).setTitle("发送(" + imasgemap.size() + "/" + max_chose_count + ")");
            } else {
                menu.findItem(R.id.menu_photo_count).setTitle("发送(1)");
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            popFragment();
        } else if (item.getItemId() == R.id.menu_photo_delete) {
            ImagePreviewFragemnt fragemnt = (ImagePreviewFragemnt) getCurrentFragment(ImagePreviewFragemnt.class.getSimpleName());
            if (fragemnt != null) {
                String img = fragemnt.delete();
                Iterator iterator = imasgemap.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    if (imasgemap.get(key).equals(img)) {
                        iterator.remove();
                    }
                }
                invalidateOptionsMenu();
            }
        } else if (item.getItemId() == R.id.menu_photo_count) {
            sendImages();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        FragmentManager fm = getSupportFragmentManager();
        if (keyCode == KeyEvent.KEYCODE_BACK && fm.getBackStackEntryCount() > 0) {
            popFragment();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void log(String msg) {
        Log.i("gallery", msg);
    }

    public void popFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStackImmediate();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        isPriview = false;
        invalidateOptionsMenu();
        if (photoGalleryFragment != null && chosemode == CHOSE_MODE_MULTIPLE) {
            photoGalleryFragment.notifyDataSetChanged();
        }
    }
    boolean isCropOver = false;
    public void sendImages() {
        if (isneedCrop && !isCropOver) {
            Iterator iterator = imasgemap.keySet().iterator();
            File file = new File(iterator.next().toString());
            if (!file.exists()) {
                Toast.makeText(this, "获取文件失败", Toast.LENGTH_SHORT).show();
            }
           sendStarCrop(file.getAbsolutePath());
        } else {
            Intent intent = new Intent();
            ArrayList<String> img = new ArrayList<>();
            Iterator iterator = imasgemap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                img.add((String) imasgemap.get(key));
            }
            intent.putExtra("data", img);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    /**
     * 选择/裁剪图片后回调的方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //裁剪图片后的返回
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CROP && (chosemode == CHOSE_MODE_SINGLE)) {
            Intent intent = new Intent();
            ArrayList<String> img = new ArrayList<>();
            //得到裁剪图片存储路径
            String crop_path=data.getStringExtra("crop_path");
            isCropOver=true;
            //存储裁剪路径以及裁剪图片都存在的情况下  将存放图片路径的集合返回给上一级Activity
            if(crop_path!=null && new File(crop_path)!=null){
                img.add(crop_path);
                intent.putExtra("data", img);
                setResult(RESULT_OK, intent);
                finish();
            }else{
                Toast.makeText(this, "截取图片失败", Toast.LENGTH_SHORT).show();
            }
            //单选并且是  拍照  获取照片
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA && (chosemode == CHOSE_MODE_SINGLE)) {
            if(currentfile!=null&&currentfile.exists()&&currentfile.length()>10){
                if(isneedCrop&&!isCropOver){//进行裁剪
                    sendStarCrop(currentfile.getAbsolutePath());
                }else{//否则将图片传回上一级Activity-----同样传递回去的是集合
                    Intent intent = new Intent();
                    ArrayList<String> img = new ArrayList<>();
                    img.add(currentfile.getAbsolutePath());
                    intent.putExtra("data", img);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                insertImage(currentfile.getAbsolutePath());
            }else{
                Toast.makeText(MediaChoseActivity.this,"获取图片失败",Toast.LENGTH_SHORT).show();
            }
            //多张图片选择后的返回结果
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CAMERA && (chosemode == CHOSE_MODE_MULTIPLE)) {

            //File.length()  获得文件字节数
            if(currentfile!=null&&currentfile.exists()&&currentfile.length()>10){
                //将图片存入链表
                getImageChoseMap().put(currentfile.getAbsolutePath(), currentfile.getAbsolutePath());
                invalidateOptionsMenu();//初始化菜单项
                //将图片存入多媒体
                insertImage(currentfile.getAbsolutePath());
            }else{
                Toast.makeText(MediaChoseActivity.this,"获取图片失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 将图片插入到多媒体文件中
     * @param fileName
     */
    public void insertImage(String fileName){
        try {
            MediaStore.Images.Media.insertImage(getContentResolver(),
                    fileName, new File(fileName).getName(),
                    new File(fileName).getName());
            //程序通过发送下面的Intent启动MediaScanner服务扫描指定的文件或目录
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(fileName));
            intent.setData(uri);
            sendBroadcast(intent);
            MediaScannerConnection.scanFile(this, new String[]{fileName}, new String[]{"image/jpeg"}, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                }
                @Override
                public void onScanCompleted(String path, Uri uri) {
//                    Log.e("扫描完成","path---:"+path+",uri---："+uri);
                    photoGalleryFragment.addCaptureFile(path);
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动相机进行拍照
     */
    public static final int REQUEST_CODE_CAMERA = 2001;
    public static final int REQUEST_CODE_CROP = 2002;
    File currentfile;
    public void sendStarCamera() {
        currentfile = getTempFile();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentfile));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * 启动裁剪的Activity  传递的参数有：裁剪的高宽，裁剪后的图片存放的绝对路径
     * @param path
     */
    public void sendStarCrop(String path) {
        Intent intent = new Intent(this, CropImageActivity.class);
        intent.setData(Uri.fromFile(new File(path)));
        intent.putExtra("crop_image_w", crop_image_w);
        intent.putExtra("crop_image_h",crop_image_h);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,getCropFile().getAbsolutePath());
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }



    public File getTempFile() {
        String str = null;
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        date = new Date(System.currentTimeMillis());
        str = format.format(date);
        return  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "IMG_"+str+".jpg");
    }

    /**
     * 得到外存储器存放图片的公公路径
     * 图片存放的标准目录  Environment.DIRECTORY_PICTURES
     * @return
     */
    public File getCropFile() {
        return  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), ".crop.jpg");
    }

}
