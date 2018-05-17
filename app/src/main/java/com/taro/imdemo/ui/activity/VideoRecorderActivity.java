package com.taro.imdemo.ui.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.xselector.XSelector;
import com.taro.imdemo.R;
import com.taro.imdemo.enity.MessageInfo;
import com.taro.imdemo.util.Constants;
import com.taro.imdemo.util.MediaManager;
import com.taro.imdemo.util.Utils;
import com.taro.imdemo.util.VideoRecodeUtils;
import com.taro.imdemo.view.SendView;
import com.taro.imdemo.view.VideoProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class VideoRecorderActivity extends AppCompatActivity {

    private boolean isCancel;
    private VideoProgressBar progressBar;
    private int mProgress;
    private TextView infoBtn;
    private TextView btn;
    private RelativeLayout recordLl, switchLl;
    private SurfaceView surfaceView;

    private VideoRecodeUtils mVideoRecodeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recorder);
        initMediaManager();
        initView();
        EventBus.getDefault().register(this);
    }

    private void initMediaManager() {
        surfaceView = (SurfaceView) findViewById(R.id.main_surface_view);
        mVideoRecodeUtils = new VideoRecodeUtils(this);
        mVideoRecodeUtils.setSurfaceView(surfaceView);
    }

    private void initView() {
        infoBtn = (TextView) findViewById(R.id.tv_info);
        btn = (TextView) findViewById(R.id.main_press_control);
        XSelector.shapeSelector()
                .setShape(GradientDrawable.OVAL)
                .defaultBgColor("#ffffff").into(btn);
        btn.setOnTouchListener(btnTouch);
        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, 0);
            }
        });
        recordLl = (RelativeLayout) findViewById(R.id.rl_record);
        switchLl = (RelativeLayout) findViewById(R.id.btn_switch);
        switchLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVideoRecodeUtils.switchCamera();
            }
        });
        progressBar = (VideoProgressBar) findViewById(R.id.main_progress_bar);
        progressBar.setOnProgressEndListener(listener);
        progressBar.setCancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setCancel(true);
    }

    View.OnTouchListener btnTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            boolean ret = false;
            float downY = 0;
            int action = motionEvent.getAction();
            switch (view.getId()) {
                case R.id.main_press_control: {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            mVideoRecodeUtils.record();
                            startView();
                            ret = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!isCancel) {
                                if (mProgress == 0) {
                                    stopView();
                                    break;
                                }
                                if (mProgress < 10) {
                                    mVideoRecodeUtils.stopRecordUnSave();
                                    Toast.makeText(VideoRecorderActivity.this, "时间太短", Toast.LENGTH_SHORT).show();
                                    stopView();
                                }
                                //停止录制
                                mVideoRecodeUtils.stopRecordSave();
                                stopView();
                                handler.sendMessage(handler.obtainMessage(1));
//                                startPlay();
                            } else {
                                //取消不保存
                                mVideoRecodeUtils.stopRecordUnSave();
                                Toast.makeText(VideoRecorderActivity.this, "取消保存", Toast.LENGTH_SHORT).show();
                                stopView();
                            }
                            ret = false;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float currentY = motionEvent.getY();
                            isCancel = downY - currentY > 10;
                            moveView();
                            break;
                    }
                }
            }
            return ret;
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    progressBar.setProgress(mProgress);
                    if (mVideoRecodeUtils.isRecording()) {
                        mProgress = mProgress + 1;
                        sendMessageDelayed(handler.obtainMessage(0), 100);
                    }
                    break;
                case 1:
                    startPlay();
                    break;
            }
        }
    };

    private void moveView() {
        if (isCancel) {
            infoBtn.setText("松手取消");
        } else {
            infoBtn.setText("上滑取消");
        }
    }

    private void stopView() {
        stopAnim();
        progressBar.setCancel(true);
        mProgress = 0;
        handler.removeMessages(0);
        infoBtn.setText("双击放大");
    }

    private void startPlay() {
        Intent intent = new Intent(VideoRecorderActivity.this,PlayerActivity.class);
        intent.putExtra("isEdit",true);
        intent.putExtra("path",mVideoRecodeUtils.getTargetFile());
        startActivity(intent);
    }

    private void startView() {
        startAnim();
        mProgress = 0;
        handler.removeMessages(0);
        handler.sendMessage(handler.obtainMessage(0));
    }

    VideoProgressBar.OnProgressEndListener listener = new VideoProgressBar.OnProgressEndListener() {
        @Override
        public void onProgressEndListener() {
            progressBar.setCancel(true);
            mVideoRecodeUtils.stopRecordSave();
        }
    };

    private void startAnim() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(btn, "scaleX", 1, 0.8f),
                ObjectAnimator.ofFloat(btn, "scaleY", 1, 0.8f),
                ObjectAnimator.ofFloat(progressBar, "scaleX", 1, 1.2f),
                ObjectAnimator.ofFloat(progressBar, "scaleY", 1, 1.2f)
        );
        set.setDuration(250).start();
    }

    private void stopAnim() {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(btn, "scaleX", 0.8f, 1f),
                ObjectAnimator.ofFloat(btn, "scaleY", 0.8f, 1f),
                ObjectAnimator.ofFloat(progressBar, "scaleX", 1.2f, 1f),
                ObjectAnimator.ofFloat(progressBar, "scaleY", 1.2f, 1f)
        );
        set.setDuration(250).start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(String info) {
        if ("back".equals(info)){
            mVideoRecodeUtils.deleteTargetFile();
        } else if ("select".equals(info)){
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mVideoRecodeUtils.setSurfaceView(surfaceView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }
}
