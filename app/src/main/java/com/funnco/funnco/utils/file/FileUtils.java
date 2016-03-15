package com.funnco.funnco.utils.file;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.funnco.funnco.utils.log.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
	
	public static String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/CAMERA";


	/**
	 * 判断是否有SD卡
	 * @return
	 */
	public static boolean hasSDcard(){
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取根目录
	 * @return
	 */
	public static String getRootPath(){
		if (hasSDcard()){
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}

	/**
	 * 对文件进行删除
	 * @param path
	 */
	public static void deleFile(String path){
		if (isFileExist(path)){
			new File(path).delete();
		}
	}
	/**
	 * 获取Android的Cache目录，如果安装了SDCard，Cache目录会在SDCard上，否则会从内存获取
	 *
	 * @return
	 */
	public static File getCacheDir(Context context) {
		File cacheDir = null;
		if (hasSDcard()) {
			// SDCard 可读写情况下
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				cacheDir = getExternalCacheDir8(context);
			} else {
				cacheDir = getExternalCacheDir7(context);
			}
		} else {
			// 没有SDCard
			cacheDir = context.getCacheDir();
		}
		if (cacheDir != null && !cacheDir.exists()) {
			cacheDir.mkdirs();
		}

		if (cacheDir == null) {
			cacheDir = context.getCacheDir();
		}
		return cacheDir;
	}
	private static File getExternalCacheDir8(Context context) {
		return context.getExternalCacheDir();
	}

	/**
	 * 版本低于7的外部缓存路径的设置
	 *
	 * @param context
	 * @return
	 */
	private static final File getExternalCacheDir7(Context context) {
		return new File(Environment.getExternalStorageDirectory(), "/Android/data/"
				+ context.getApplicationInfo().packageName + "/cache/");
	}
	public static boolean isLocalUrl(String url){
		return (url != null && (url.startsWith("/") || url.toLowerCase().startsWith("file:")));
	}
	/**
	 * 将图片进行保存 目录是Funnco 目录,并插入的系统图库
	 * @param bm
	 * @param picName
	 * @return
	 */
	public static String saveBitmap(Context context,Bitmap bm, String picName) {
		File file = null;

		String fileName = null;
		String path;
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			fileName = picName + ".JPEG";
			file = new File(SDPATH, fileName);
			if (file.exists()) {
				file.delete();
			}
			FileOutputStream out = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		path = file.getAbsolutePath();
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
		LogUtils.e("------的绝对路径是：" + path, "+++++++");
		return path;
	}

	public static void saveBitmap(Context context,Bitmap bitmap,String imgName,String description){
		MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, imgName, description);
	}

	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}
	/**
	 * 获取文件大小
	 * @param file
	 * @return the length of this file in bytes
	 */
	public static long getFileSize(File file) {
		if (!isFileExist(file)){
			return 0;
		}
		long size = 0;
		if (file.isFile()) {
			size = file.length();
		} else {
			for (File f : file.listFiles()) {
				size += getFileSize(f);
			}
		}
		return size;
	}
	/**
	 * 判断文件是否存在
	 * @param fileName 文件路径
	 * @return
	 */
	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		if(!isFileExist(file)){
			file = new File(fileName);
			return isFileExist(file);
		}else{
			return true;
		}
	}
	/**
	 * 判断文件是否存在
	 * @param file 文件对象
	 * @return
	 */
	public static boolean isFileExist(File file){
		if (file == null)
			return false;
		return file.exists();
	}

	/**
	 * 删除文件
	 * @param fileName
	 */
	public static void delFile(String fileName){
		File file = new File(SDPATH + fileName);
		if(file.isFile()){
			file.delete();
        }
		file.exists();
	}

	/**
	 * 删除文件夹
	 */
	public static void deleteDir() {
		File dir = new File(SDPATH);
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); 
			else if (file.isDirectory())
				deleteDir(); 
		}
		dir.delete();
	}
}
