package com.taro.imdemo.util;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by taro on 2018/5/14.
 */

public class VideoRecodeUtils implements SurfaceHolder.Callback, MediaRecorder.OnErrorListener {
    private Activity activity;
    private MediaRecorder mMediaRecorder;
    private CamcorderProfile profile;
    private Camera mCamera;
    private boolean isRecording;
    private boolean isZoomIn = false;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private int preViewWidth, preViewHeight;
    private GestureDetector mDetector;
    private int or = 90;
    private int cameraPosition = 1;//0表示前置摄像头，1表示后置摄像头
    private File targetFile;

    public VideoRecodeUtils(Activity activity) {
        this.activity = activity;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public String getTargetFile() {
        return targetFile.getPath();
    }

    public void setSurfaceView(SurfaceView view) {
        this.mSurfaceView = view;
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setFixedSize(preViewWidth, preViewHeight);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(this);
        mDetector = new GestureDetector(activity, new ZoomGestureListener());
        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    public void record() {
        if (isRecording) {
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException e) {
                targetFile.delete();
            }
            releaseMediaRecorder();
            mCamera.lock();
            isRecording = false;
        } else {
            startRecordThread();
        }
    }

    private void startRecordThread() {
        if (prepareRecord()) {
            try {
                mMediaRecorder.start();
                isRecording = true;
            } catch (Exception e) {
                releaseMediaRecorder();
            }
        }
    }

    private boolean prepareRecord() {
//        try {
        if (mMediaRecorder == null)
            mMediaRecorder = new MediaRecorder();
        else
            mMediaRecorder.reset();
        mCamera.stopPreview();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        //记录音频录入源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //视频图像录入源
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        //录制镜头方向
        if (cameraPosition == 0) {
            mMediaRecorder.setOrientationHint(270);
        } else {
            mMediaRecorder.setOrientationHint(or);
        }

        mMediaRecorder.setProfile(profile);

        //设置捕获视频图片的预览界面
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        //文件输出路径
        String filePath = Constants.CHAT_VIDEO_PATH;
        String name = System.currentTimeMillis() + ".mp4";
        if (Utils.isFileExist(filePath)) {
            targetFile = new File(filePath + name);
            mMediaRecorder.setOutputFile(filePath + name);
        }

        mMediaRecorder.setOnErrorListener(this);
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public void switchCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//摄像头个数
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每个摄像头信息
            if (cameraPosition == 1) {
                //后置变成前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//摄像头方位
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                    mCamera = Camera.open(i);
                    startPreView(mSurfaceHolder);
                    cameraPosition = 0;
                    break;
                }
            } else {
                //前置变后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                    mCamera = Camera.open(i);
                    startPreView(mSurfaceHolder);
                    cameraPosition = 1;
                    break;
                }
            }
        }
    }

    public boolean deleteTargetFile() {
        if (targetFile.exists()) {
            return targetFile.delete();
        } else {
            return false;
        }
    }

    public void stopRecordSave() {
        if (isRecording) {
            isRecording = false;
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException r) {

            } finally {
                releaseMediaRecorder();
            }
        }
    }

    public void stopRecordUnSave() {
        if (isRecording) {
            isRecording = false;
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException r) {
                if (targetFile.exists()) {
                    targetFile.delete();
                }
            } finally {
                releaseMediaRecorder();
            }
            if (targetFile.exists()) {
                targetFile.delete();
            }
        }
    }

    private void startPreView(SurfaceHolder mSurfaceHolder) {
        if (mCamera == null) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (mCamera != null) {
            mCamera.setDisplayOrientation(or);
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
                List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
                Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes, mSupportedPreviewSizes, mSurfaceView.getWidth(), mSurfaceView.getHeight());
                preViewWidth = optimalSize.width;
                preViewHeight = optimalSize.height;
                parameters.setPreviewSize(preViewWidth, preViewHeight);

                if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
                } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
                } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
                } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
                    profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                }

                profile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
                profile.videoBitRate = 5 * 1024 * 1024;
                profile.videoCodec = MediaRecorder.VideoEncoder.H264;
                profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
                profile.videoFrameWidth = optimalSize.width;
                profile.videoFrameHeight = optimalSize.height;
                profile.videoBitRate = 2 * optimalSize.width * optimalSize.height;
                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes != null) {
                    for (String mode : focusModes) {
                        mode.contains("continuous-video");
                        parameters.setFocusMode("continuous-video");
                    }
                }
                mCamera.setParameters(parameters);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception el) {
                el.printStackTrace();
            }
        }
    }

    @Override
    public void onError(MediaRecorder mediaRecorder, int i, int i1) {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
        isRecording = false;
        Toast.makeText(activity, "视频录制出错，请重试！", Toast.LENGTH_SHORT).show();
    }

    private class ZoomGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            super.onDoubleTap(e);
            if (!isZoomIn) {
                setZoom(20);
                isZoomIn = true;
            } else {
                setZoom(0);
                isZoomIn = false;
            }
            return true;
        }
    }

    private void setZoom(int zoomValue) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.isZoomSupported()) {
                int maxZoom = parameters.getMaxZoom();
                if (maxZoom == 0) {
                    return;
                }
                if (zoomValue > maxZoom) {
                    zoomValue = maxZoom;
                }
                parameters.setZoom(zoomValue);
                mCamera.setParameters(parameters);
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        startPreView(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mSurfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mSurfaceView = null;
        mSurfaceHolder = null;
        if (mCamera != null) {
            releaseCamera();
        }
        if (mMediaRecorder != null) {
            releaseMediaRecorder();
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}
