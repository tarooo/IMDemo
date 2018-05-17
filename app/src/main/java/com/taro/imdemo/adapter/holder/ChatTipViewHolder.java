package com.taro.imdemo.adapter.holder;

import android.view.ViewGroup;
import android.widget.TextView;

import com.android.xselector.XSelector;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.taro.imdemo.R;
import com.taro.imdemo.enity.MessageInfo;

/**
 * Created by taro on 2018/5/7.
 */

public class ChatTipViewHolder extends BaseViewHolder<MessageInfo> {

    private TextView tipTv;
    public ChatTipViewHolder(ViewGroup parent) {
        super(parent, R.layout.item_tip_message);
        tipTv = $(R.id.tv);
        XSelector.shapeSelector()
                .defaultBgColor("#dddddd")
                .radius(5f).into(tipTv);
    }

    @Override
    public void setData(MessageInfo data) {
        if (data != null){
            tipTv.setText(data.getContent());
        }
    }
}
