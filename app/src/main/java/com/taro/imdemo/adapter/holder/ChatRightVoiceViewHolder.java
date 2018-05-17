package com.taro.imdemo.adapter.holder;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.xselector.XSelector;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.taro.imdemo.R;
import com.taro.imdemo.enity.MessageInfo;
import com.taro.imdemo.util.MediaManager;
import com.taro.imdemo.util.Utils;

/**
 * Created by taro on 2018/5/7.
 */

public class ChatRightVoiceViewHolder extends BaseViewHolder<MessageInfo> {

    private ImageView headerImg;
    private int animationRes = 0;
    private int res = 0;
    private AnimationDrawable animationDrawable = null;
    private ImageView animView;
    private TextView timeTv;
    private LinearLayout contentLl;
    public ChatRightVoiceViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_voice_message_to);
        contentLl = $(R.id.ll_content);
        XSelector.shapeSelector()
                .defaultBgColor("#00cd00")
                .defaultStrokeColor("#eeeeee")
                .strokeWidth(1)
                .tlRadius(20f)
                .blRadius(20f)
                .brRadius(20f)
                .into(contentLl);
        animView = $(R.id.img_chat_voice);
        timeTv = $(R.id.tv_chat_voice_time);
    }

    @Override
    public void setData(MessageInfo data) {
        if (data != null){
            timeTv.setText(Utils.formatTime(data.getVoiceTime()));
            final String filePath = data.getFilepath();
            contentLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Utils.isExist(getContext(),filePath)) return;
                    animView.setImageResource(res);
                    animationRes = R.drawable.voice_right;
                    res = R.mipmap.icon_voice_right3;
                    animView.setImageResource(animationRes);
                    animationDrawable = (AnimationDrawable) animView.getDrawable();
                    animationDrawable.start();
                    MediaManager.playSound(filePath, new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            animView.setImageResource(res);
                        }
                    });
                }
            });
        }
    }
}
