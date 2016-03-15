package com.wq.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.wq.photo.utils.BitmapUtils;
import com.wq.photo.utils.ScreenUtils;
import com.wq.photo.widget.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class CropImageActivity extends ActionBarActivity {
    private String filePath;
    private String outFilePath;
    private Context mContext;
    private Activity mActivity;

    /**
     * 上传图片大小
     */
    public int crop_image_w = 0;
    public int crop_image_h = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        setContentView(R.layout.activity_crop_image);
        getSupportActionBar().setTitle("裁剪图片");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            crop_image_w = getIntent().getIntExtra("crop_image_w", 300);
            crop_image_h = getIntent().getIntExtra("crop_image_h", 300);
            Uri uri = getIntent().getData();
            if ("file".equals(uri.getScheme())) {
                filePath = uri.getPath();
                outFilePath = getIntent().getStringExtra(MediaStore.EXTRA_OUTPUT);
                cropImage();
            }
        } catch (Exception e) {
            Log.e("-----裁剪图片异常-----","-----裁剪图片异常-----");
//            finish();
        }
    }

    CropImageView mCropImage;
    private void cropImage() {
        mCropImage = (CropImageView) findViewById(R.id.cropImg);
        mCropImage.setDrawingCacheEnabled(false);
        if (TextUtils.isEmpty(filePath) || filePath.length() < 2){
            return;
        }
        Bitmap bitmap = BitmapUtils.getCompressBitmap(filePath, ScreenUtils.getScreenWidth(mContext), ScreenUtils.getScreenHeight(mContext));
        if (bitmap == null){
            return;
        }
//        Drawable drawable = Drawable.createFromPath(filePath);
        Drawable drawable = new BitmapDrawable(bitmap);
        mCropImage.setDrawable(drawable, crop_image_w, crop_image_h);
        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("裁剪完成，，，进行保存！！！","");
                        File f = save2Storage(mCropImage.getCropImage(), outFilePath);
                        Intent intent = new Intent();
                        intent.putExtra("crop_path", outFilePath);
                        setResult(RESULT_OK, intent);
                        finishOk();
                    }
                });
            }
        });
    }

    public static File save2Storage(Bitmap bitmap, String path) {
        try {
            File filename = new File(path);
            if (!filename.exists()) {
                filename.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filename);
            Log.e("funnco-----","图片裁剪后保存的大小是："+bitmap.getByteCount());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);//png 0-9  jpeg 0=100
            fos.flush();
            fos.close();
            return filename;
        } catch (Exception e) {
            return null;
        }
    }

    public File getCropFile() {
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "crop.jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
//                Intent intent = new Intent(this, MainAc)
                Log.e("裁剪的Activity中点击了Home键","");
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void finishOk(){
        mCropImage = null;
        System.gc();//提醒系统回收
        finish();
    }
}
