package com.funnco.funnco.utils.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by user on 2015/7/9.
 */
public class MediaPlayerUtils {

    /**
     * 使用MediaPlayer进行播放音乐
     * @param context
     * @param rawId raw 文件夹下的音频文件
     * @param isLooping 是否循环
     */
    public static void mediaPlayMusic(Context context, int rawId,boolean isLooping){
        MediaPlayer mPlayer  = MediaPlayer.create(context, rawId);
        mPlayer.setLooping(isLooping);
        mPlayer.start();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();
            }
        });
    }

    public static void mediaPlayMusic(Context context, int rawId){
        mediaPlayMusic(context,rawId,false);
    }

    /**
     * 使用MediaPlay播放本地音频文件
     * @param context
     * @param path 音频文件的绝对地址
     */
    public static void mediaPlayMusic(Context context, String path){
        MediaPlayer mPlayer  = new MediaPlayer();
        //set audio file path
        try {
            mPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Prepare mediaplayer
        try {
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //start mediaPlayer
        mPlayer.start();
    }

    /**
     * 使用SoundPoolMusic 播放音频文件
     * @param context
     * @param rawId
     * @param maxMusicCount
     */
    public static void SoundPoolMusic(Context context,int rawId,int maxMusicCount){
        int maxCount = 1;
        if (maxMusicCount > 1) {
            maxCount = maxMusicCount;
        }
        SoundPool soundPool = new SoundPool(maxCount, AudioManager.STREAM_MUSIC, 100);

        HashMap<Integer, Integer> soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(1, soundPool.load(context, rawId, 1));

        AudioManager mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);

        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent/streamVolumeMax;
        soundPool.play(soundPoolMap.get(0), volume, volume, 1, 0, 1f);
        //参数：1、Map中取值2、当前音量3、最大音量4、优先级5、重播次数6、播放速度
    }

}
