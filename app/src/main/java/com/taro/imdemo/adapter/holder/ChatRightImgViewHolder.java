package com.taro.imdemo.adapter.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.taro.imdemo.R;
import com.taro.imdemo.adapter.ChatAdapter;
import com.taro.imdemo.enity.MessageInfo;

/**
 * Created by taro on 2018/5/7.
 */

public class ChatRightImgViewHolder extends BaseViewHolder<MessageInfo> {

    private ImageView contentImg;
    private ImageView headerImg;
    private ChatAdapter.OnItemClickListener onItemClickListener;
    public ChatRightImgViewHolder(ViewGroup parent, ChatAdapter.OnItemClickListener onItemClickListener) {
        super(parent, R.layout.item_img_message_to);
        this.onItemClickListener = onItemClickListener;
        contentImg = $(R.id.img_content);
        headerImg = $(R.id.img_header);
    }

    @Override
    public void setData(final MessageInfo data) {
        if (data != null){
            final String url = data.getImageUrl();
            Glide.with(getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.5f).into(contentImg);
            contentImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) onItemClickListener.onImageClick(contentImg,getDataPosition(),url);
                }
            });
        }
    }
}
