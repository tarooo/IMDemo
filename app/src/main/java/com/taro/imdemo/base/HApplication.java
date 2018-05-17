package com.taro.imdemo.base;

import android.app.Application;
import android.util.DisplayMetrics;

import com.android.xselector.XSelector;

/**
 * Created by taro on 2018/4/26.
 */

public class HApplication extends Application {

    /*屏幕宽度*/
    public static int screenWidth;
    /*屏幕高度*/
    public static int screenHeight;
    /*屏幕密度*/
    public static float screenDensity;
    @Override
    public void onCreate() {
        super.onCreate();
        XSelector.init(this);
        initScreenSize();
    }

    private void initScreenSize() {
        DisplayMetrics curMetrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = curMetrics.widthPixels;
        screenHeight = curMetrics.heightPixels;
        screenDensity = curMetrics.density;
    }
}
