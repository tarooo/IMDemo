package com.taro.imdemo.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by taro on 2018/4/28.
 */

public class PopupWindowFactory {

    private Context mContext;
    private PopupWindow pop;

    public PopupWindowFactory(Context mContext, View view) {
        this(mContext,view, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public PopupWindowFactory(Context mContext,View view,int width,int height) {
        init(mContext,view,width,height);
    }

    private void init(Context mContext, View view, int width, int height) {
        this.mContext = mContext;

        view.setFocusable(true);
        view.setFocusableInTouchMode(true);

        pop = new PopupWindow(view,width,height,true);
        pop.setFocusable(true);

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){
                    pop.dismiss();
                    return true;
                }
                return false;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (pop != null && pop.isShowing()){
                    pop.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    public void showAtLocation(View parent,int gravity,int x,int y){
        if (pop.isShowing()) return;
        pop.showAtLocation(parent,gravity,x,y);
    }

    public void dismiss(){
        if (pop.isShowing()){
            pop.dismiss();
        }
    }
}
