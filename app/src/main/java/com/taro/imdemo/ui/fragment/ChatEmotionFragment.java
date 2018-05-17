package com.taro.imdemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.taro.imdemo.R;
import com.taro.imdemo.adapter.EmotionGridViewAdapter;
import com.taro.imdemo.adapter.EmotionPagerAdapter;
import com.taro.imdemo.base.HApplication;
import com.taro.imdemo.base.HBaseFragment;
import com.taro.imdemo.util.EmotionUtils;
import com.taro.imdemo.util.GlobalOnItemClickManagerUtils;
import com.taro.imdemo.util.Utils;
import com.taro.imdemo.view.IndicatorView;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

/**
 * Created by taro on 2018/4/27.
 */

public class ChatEmotionFragment extends HBaseFragment {

    private View rootView;
    private ViewPager viewPager;
    private IndicatorView fragmentChatGroup;
    private EmotionPagerAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null){
            rootView = inflater.inflate(R.layout.fragment_chat_emotion,container,false);
            initView();
        }
        return rootView;
    }

    private void initView() {
        viewPager = (ViewPager) rootView.findViewById(R.id.fragment_chat_vp);
        fragmentChatGroup = (IndicatorView) rootView.findViewById(R.id.fragment_chat_group);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int oldPagerPos = 0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                fragmentChatGroup.playByStartPointToNext(oldPagerPos,position);
                oldPagerPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initEmotion();
    }

    private void initEmotion() {
        int screenWidth = HApplication.screenWidth;
        /*item的间距*/
        int spacing = Utils.dp2px(getActivity(),5);
        /*动态计算item的宽度高度*/
        int itemWidth = (screenWidth - spacing * 8) /7;
        /*动态计算gridview的总高度*/
        int gvHeight = itemWidth * 3 + spacing * 6;

        List<GridView> emotionViews = new ArrayList<>();
        List<String> emotionNames = new ArrayList<>();
        for (String emotionName : EmotionUtils.EMOTION_STATIC_MAP.keySet()) {
            emotionNames.add(emotionName);
            //每20个表情作为一组，同时添加到ViewPager对应的View集合中
            if (emotionNames.size() == 23){
                GridView gv = createEmotionGridView(emotionNames,screenWidth,spacing,itemWidth,gvHeight);
                emotionViews.add(gv);
                //添加完一组表情，重新创建一个表情名字集合
                emotionNames = new ArrayList<>();
            }
        }

        //判断最后是否有不足23个表情的剩余情况
        if (emotionNames.size() >0){
            GridView gv = createEmotionGridView(emotionNames,screenWidth,spacing,itemWidth,gvHeight);
            emotionViews.add(gv);
        }

        //初始化指示器
        fragmentChatGroup.initIndicator(emotionViews.size());
        //将多个GridView添加显示到ViewPager中
        adapter = new EmotionPagerAdapter(emotionViews);
        viewPager.setAdapter(adapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth,gvHeight);
        viewPager.setLayoutParams(params);
    }

    private GridView createEmotionGridView(final List<String> emotionNames, int gvWidth, int padding, int itemWidth, int gvHeight) {
        GridView gv = new GridView(getActivity());
        gv.setSelector(android.R.color.transparent);
        gv.setNumColumns(8);
        gv.setPadding(padding,padding,padding,padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding *2);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gvWidth,gvHeight);
        gv.setLayoutParams(params);
        EmotionGridViewAdapter adapter = new EmotionGridViewAdapter(getActivity(),emotionNames,itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(GlobalOnItemClickManagerUtils.getInstance(getActivity()).getOnItemClickListener());
        return gv;
    }
}
