package com.taro.imdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.taro.imdemo.R;
import com.taro.imdemo.util.Utils;

import java.util.ArrayList;

/**
 * Created by taro on 2018/4/27.
 */

public class IndicatorView extends LinearLayout {

    private Context mContext;
    private ArrayList<View> mImageViews;
    private int size = 6;
    private int marginSize = 15;
    private int pointSize;//指示器的大小
    private int marginLeft;//间距
    public IndicatorView(Context context) {
        super(context);
        init(context);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    private void init(Context context) {
        mContext = context;
        pointSize = Utils.dp2px(context,size);
        marginLeft = Utils.dp2px(context,marginSize);
    }

    /*初始化指示器*/
    public void initIndicator(int count){
        mImageViews = new ArrayList<>();
        this.removeAllViews();
        LayoutParams lp;
        for (int i = 0; i < count; i++) {
            View v = new View(mContext);
            lp = new LayoutParams(pointSize,pointSize);
            if (i != 0) lp.leftMargin = marginLeft;
            v.setLayoutParams(lp);
            if (i == 0){
                v.setBackgroundResource(R.drawable.bg_circle_white);
            } else {
                v.setBackgroundResource(R.drawable.bg_circle_gary);
            }
            mImageViews.add(v);
            this.addView(v);
        }
    }

    /*页面移动时切换指示器*/
    public void playByStartPointToNext(int startPosition, int nextPosition){
        if (startPosition < 0 || nextPosition < 0 || nextPosition == startPosition){
            startPosition = nextPosition = 0;
        }
        View viewStart = mImageViews.get(startPosition);
        View viewNext = mImageViews.get(nextPosition);
        viewNext.setBackgroundResource(R.drawable.bg_circle_white);
        viewStart.setBackgroundResource(R.drawable.bg_circle_gary);
    }
}
