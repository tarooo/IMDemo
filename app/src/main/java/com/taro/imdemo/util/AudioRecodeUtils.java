package com.taro.imdemo.util;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.io.IOException;

import static android.os.Build.VERSION_CODES.BASE;

/**
 * Created by taro on 2018/4/28.
 */

public class AudioRecodeUtils {
    /*文件路径*/
    private String filePath;
    /*文件夹路径*/
    private String folderPath;
    
    private MediaRecorder mMediaRecorder;
    public static final int MAX_LENGTH = 1000 * 60;//最长录音时间
    
    private OnAudioStatusUpdateListener audioStatusUpdateListener;

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener){
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    private long startTime;
    private long endTime;

    public AudioRecodeUtils() {
        this(Environment.getExternalStorageDirectory() + "/imdemo/voice/");
    }

    public AudioRecodeUtils(String filePath) {
        File path = new File(filePath);
        if (!path.exists()) path.mkdirs();
        this.folderPath = filePath;
    }

    public void startRecord(Context context){
        if (!CheckPermissionUtils.isHasPermission(context)) {
            audioStatusUpdateListener.onError();
            return;
        }
        //开始录音
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置麦克风
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//设置音频文件编码
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置输出文件的格式

            filePath = folderPath + Utils.getCurrentTime() + ".amr";
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();

            mMediaRecorder.start();
            startTime = System.currentTimeMillis();
            updateMicStatus();
        } catch (Exception e) {
            audioStatusUpdateListener.onError();
        }
    }

    private int BASE = 1;
    private int SPACE = 100;//间隔取样时间

    /*更新麦克风状态*/
    private void updateMicStatus() {
        if (mMediaRecorder != null){
            double ratio = mMediaRecorder.getMaxAmplitude() / BASE;
            double db = 0;//分贝
            if (ratio > 1){
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener){
                    audioStatusUpdateListener.onUpdate(db,System.currentTimeMillis() - startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer,SPACE);
        }
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        @Override
        public void run() {
            updateMicStatus();
        }
    };

    /*停止录音*/
    public long stopRecord(){
        if (mMediaRecorder == null) return 0L;
        endTime = System.currentTimeMillis();

        //设置后不会崩溃
        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.setPreviewDisplay(null);
        try {
            mMediaRecorder.stop();
        } catch (Exception e) {

        }
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        long time = endTime - startTime;
        audioStatusUpdateListener.onStop(time,filePath);
        filePath = "";
        return endTime - startTime;
    }

    /*取消录音*/
    public void cancelRecord(){
        if (mMediaRecorder != null){
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        File file = new File(filePath);
        if (file.exists()) file.delete();
        filePath = "";
    }

    public interface OnAudioStatusUpdateListener {
        /**
         *
         * @param db 声音分贝
         * @param time
         */
        public void onUpdate(double db, long time);

        /**
         *
         * @param time
         * @param filePath
         */
        public void onStop(long time,String filePath);
        public void onError();
    }
}
