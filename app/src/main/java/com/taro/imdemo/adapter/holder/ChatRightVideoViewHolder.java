package com.taro.imdemo.adapter.holder;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.taro.imdemo.R;
import com.taro.imdemo.enity.MessageInfo;
import com.taro.imdemo.ui.activity.PlayerActivity;
import com.taro.imdemo.ui.activity.VideoRecorderActivity;
import com.taro.imdemo.util.Utils;

/**
 * Created by taro on 2018/5/17.
 */

public class ChatRightVideoViewHolder extends BaseViewHolder<MessageInfo> {
    private ImageView thumbImg;
    private ProgressBar progressBar;
    private RelativeLayout contentRl;
    public ChatRightVideoViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_video_message_to);
        thumbImg = $(R.id.img_chat_video);
        progressBar = $(R.id.progress);
        contentRl = $(R.id.rl_content);
    }

    @Override
    public void setData(final MessageInfo data) {
        if (data != null){
            thumbImg.setImageURI(Uri.parse(data.getImageUrl()));
            contentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!Utils.isExist(getContext(),data.getFilepath())) return;
                    Intent intent = new Intent(getContext(),PlayerActivity.class);
                    intent.putExtra("path",data.getFilepath());
                    getContext().startActivity(intent);
                }
            });
        }
    }
}
