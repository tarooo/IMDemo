package com.taro.imdemo.adapter.holder;

import android.os.Handler;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.xselector.XSelector;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.taro.imdemo.R;
import com.taro.imdemo.enity.MessageInfo;
import com.taro.imdemo.util.Utils;
import com.taro.imdemo.view.GifTextView;

/**
 * Created by taro on 2018/5/7.
 */

public class ChatRightTextViewHolder extends BaseViewHolder<MessageInfo> {

    private GifTextView tv;
    private ImageView headerImg;
    private Handler handler;
    public ChatRightTextViewHolder(ViewGroup parent,Handler handler) {
        super(parent, R.layout.item_text_message_to);
        this.handler = handler;
        tv = $(R.id.tv);
        headerImg = $(R.id.img_header);
        XSelector.shapeSelector()
                .defaultBgColor("#00cd00")
                .defaultStrokeColor("#eeeeee")
                .strokeWidth(1)
                .tlRadius(20f)
                .blRadius(20f)
                .brRadius(20f)
                .into(tv);
    }

    @Override
    public void setData(MessageInfo data) {
        if (data != null){
//            tv.setSpanText(handler,data.getContent(),true);
            tv.setText(Utils.getEmotionContent(getContext(),tv,data.getContent()));
        }
    }
}
