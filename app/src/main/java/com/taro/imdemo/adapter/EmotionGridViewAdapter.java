package com.taro.imdemo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.taro.imdemo.R;
import com.taro.imdemo.util.EmotionUtils;

import java.util.List;

/**
 * Created by taro on 2018/4/27.
 */

public class EmotionGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> emotionNames;
    private int itemWith;

    public EmotionGridViewAdapter(Context context, List<String> emotionNames, int itemWith) {
        this.context = context;
        this.emotionNames = emotionNames;
        this.itemWith = itemWith;
    }

    @Override
    public int getCount() {
        return emotionNames.size() +1;
    }

    @Override
    public String getItem(int i) {
        return emotionNames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(context);
        imageView.setPadding(itemWith /8, itemWith /8, itemWith /8, itemWith /8);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(itemWith,itemWith);
        imageView.setLayoutParams(params);

        if (position == getCount() -1){
            imageView.setImageResource(R.drawable.compose_emotion_delete);
        } else {
            String emoyionName = emotionNames.get(position);
            imageView.setImageResource(EmotionUtils.EMOTION_STATIC_MAP.get(emoyionName));
        }
        return imageView;
    }
}
