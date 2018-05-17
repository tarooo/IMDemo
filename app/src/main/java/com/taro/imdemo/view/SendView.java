package com.taro.imdemo.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.android.xselector.XSelector;
import com.taro.imdemo.R;

/**
 * Created by taro on 2018/5/10.
 */

public class SendView extends RelativeLayout {
    public RelativeLayout backRl;
    public RelativeLayout selectRl;

    public SendView(Context context) {
        super(context);
        init(context);
    }

    public SendView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        RelativeLayout.LayoutParams params = new LayoutParams(getWidthPixels(context), dp2px(context, 180f));
        setLayoutParams(params);
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.widget_view_send_btn, null, false);
        layout.setLayoutParams(params);
        backRl = (RelativeLayout) layout.findViewById(R.id.rl_return);
        selectRl = (RelativeLayout) layout.findViewById(R.id.rl_select);
        XSelector.shapeSelector()
                .setShape(GradientDrawable.OVAL)
                .defaultStrokeColor("#e9e9e9")
                .into(backRl);
        XSelector.shapeSelector()
                .setShape(GradientDrawable.OVAL)
                .defaultStrokeColor("#e9e9e9")
                .into(selectRl);
        addView(layout);
        setVisibility(GONE);
    }

    public void startAnim() {
        setVisibility(VISIBLE);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(backRl, "translationX", 0, -150),
                ObjectAnimator.ofFloat(selectRl, "translationX", 0, 150)
        );
        set.setDuration(250).start();
    }

    public void stopAnim() {
        setVisibility(VISIBLE);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(
                ObjectAnimator.ofFloat(backRl, "translationX", -150, 0),
                ObjectAnimator.ofFloat(selectRl, "translationX", 150, 0)
        );
        set.setDuration(250).start();
        setVisibility(GONE);
    }

    public int getWidthPixels(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        Configuration cf = context.getResources().getConfiguration();
        int ori = cf.orientation;
        if (ori == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
            return displayMetrics.heightPixels;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {// 竖屏
            return displayMetrics.widthPixels;
        }
        return 0;
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
