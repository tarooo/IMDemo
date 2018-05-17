package com.taro.imdemo.ui.activity;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.android.xselector.XSelector;
import com.taro.imdemo.R;
import com.taro.imdemo.enity.MessageInfo;
import com.taro.imdemo.util.Constants;
import com.taro.imdemo.util.SDCardUtils;
import com.taro.imdemo.util.Utils;
import com.taro.imdemo.view.SendView;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by taro on 2018/5/16.
 */

public class PlayerActivity extends AppCompatActivity {

    private TextureView surfaceView;
    private String pathUri;
    private String thumbPath;
    private SendView sendView;

    private MediaPlayer mediaPlayer;
    private boolean isEdit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        pathUri = getIntent().getStringExtra("path");
        isEdit= getIntent().getBooleanExtra("isEdit",false);

        surfaceView = (TextureView) findViewById(R.id.surface_video_play);
        sendView = (SendView) findViewById(R.id.view_send);

        sendView.selectRl.setOnClickListener(selectListener);
        sendView.backRl.setOnClickListener(backListener);

        XSelector.shapeSelector()
                .setShape(GradientDrawable.OVAL)
                .defaultBgColor("#ffffff").into(sendView.backRl);
        XSelector.shapeSelector()
                .setShape(GradientDrawable.OVAL)
                .defaultBgColor("#ffffff").into(sendView.selectRl);

        surfaceView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                Surface s = new Surface(surfaceTexture);
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(pathUri);
                    mediaPlayer.setSurface(s);
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();
                        }
                    });
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.seekTo(0);
                            mediaPlayer.start();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });

        if (!isEdit){
            surfaceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

    }


    private View.OnClickListener selectListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            thumbPath = Constants.CHAT_VIDEO_PATH + Utils.getCurrentTime()+".png";
            EventBus.getDefault().post("select");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (SDCardUtils.saveBitmapToSDCardPrivateDir(PlayerActivity.this,getVideoThumb(pathUri),thumbPath)){
                        handler.sendMessage(handler.obtainMessage(1));
                    }
                }
            }).start();

        }
    };

    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EventBus.getDefault().post("back");
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (isEdit)
            handler.sendMessageDelayed(handler.obtainMessage(0),250);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                sendView.startAnim();
            } else if (msg.what == 1){
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setFilepath(pathUri);
                messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT_VIDEO);
                messageInfo.setImageUrl(thumbPath);
                EventBus.getDefault().post(messageInfo);
                finish();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private Bitmap getVideoThumb(String path){
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        return mediaMetadataRetriever.getFrameAtTime();
    }

    private Bitmap getVideoThumb2(String path,int kind){
        return ThumbnailUtils.createVideoThumbnail(path,kind);
    }

    private Bitmap getVideoThumb3(String path){
        return getVideoThumb2(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
    }
}
