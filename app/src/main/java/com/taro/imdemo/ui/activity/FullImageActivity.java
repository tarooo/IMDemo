package com.taro.imdemo.ui.activity;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.taro.imdemo.R;
import com.taro.imdemo.enity.FullImageInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FullImageActivity extends AppCompatActivity {

    private ImageView fullImg;
    private LinearLayout fullLl;

    private int mLeft;
    private int mTop;
    private float mScaleX;
    private float mScaleY;
    private Drawable mBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        initView();
        EventBus.getDefault().register(this);
    }

    private void initView() {
        fullImg = (ImageView) findViewById(R.id.img_full);
        fullLl = (LinearLayout) findViewById(R.id.activity_full_image);

        fullImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityEnterAnim(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        overridePendingTransition(0,0);
                    }
                });
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onDataSynEvent(FullImageInfo fullImageInfo){
        final int left = fullImageInfo.getLocationX();
        final int top = fullImageInfo.getLocationY();
        final int width = fullImageInfo.getWidth();
        final int height = fullImageInfo.getHeight();
        mBackground = new ColorDrawable(Color.BLACK);
        fullLl.setBackground(mBackground);
        fullImg.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                fullImg.getViewTreeObserver().removeOnPreDrawListener(this);
                int location[] = new int[2];
                fullImg.getLocationOnScreen(location);
                mLeft = left - location[0];
                mTop = top - location[1];
                mScaleX = width * 1.0f / fullImg.getWidth();
                mScaleY = height * 1.0f / fullImg.getHeight();
                activityEnterAnim();
                return true;
            }
        });
        Glide.with(this).load(fullImageInfo.getImageUrl()).into(fullImg);
    }

    private void activityEnterAnim() {
        fullImg.setPivotX(0);
        fullImg.setPivotY(0);
        fullImg.setScaleX(mScaleX);
        fullImg.setScaleY(mScaleY);
        fullImg.setTranslationX(mLeft);
        fullImg.setTranslationY(mTop);
        fullImg.animate().scaleX(1).scaleY(1).translationX(0).translationY(0).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mBackground,"alpha",0,255);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(250);
        objectAnimator.start();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void activityEnterAnim(Runnable runnable) {
        fullImg.setPivotX(0);
        fullImg.setPivotY(0);
        fullImg.animate().scaleX(mScaleX).scaleY(mScaleY).translationX(mLeft).translationY(mTop).withEndAction(runnable).setDuration(250).setInterpolator(new DecelerateInterpolator()).start();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(mBackground,"alpha",255,0);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.setDuration(250);
        objectAnimator.start();
    }

    @Override
    public void onBackPressed() {
        activityEnterAnim(new Runnable() {
            @Override
            public void run() {
                finish();
                overridePendingTransition(0,0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
