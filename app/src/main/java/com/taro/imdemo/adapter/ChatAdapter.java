package com.taro.imdemo.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.taro.imdemo.adapter.holder.ChatLeftImgViewHolder;
import com.taro.imdemo.adapter.holder.ChatLeftTextViewHolder;
import com.taro.imdemo.adapter.holder.ChatLeftVideoViewHolder;
import com.taro.imdemo.adapter.holder.ChatLeftVoiceViewHolder;
import com.taro.imdemo.adapter.holder.ChatRightImgViewHolder;
import com.taro.imdemo.adapter.holder.ChatRightTextViewHolder;
import com.taro.imdemo.adapter.holder.ChatRightVideoViewHolder;
import com.taro.imdemo.adapter.holder.ChatRightVoiceViewHolder;
import com.taro.imdemo.adapter.holder.ChatTipViewHolder;
import com.taro.imdemo.enity.MessageInfo;
import com.taro.imdemo.util.Constants;

/**
 * Created by taro on 2018/5/7.
 */

public class ChatAdapter extends RecyclerArrayAdapter<MessageInfo> {

    private OnItemClickListener onItemClickListener;
    public Handler handler;
    public ChatAdapter(Context context) {
        super(context);
        handler = new Handler();
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder viewHolder = null;
        switch (viewType){
            case Constants.CHAT_ITEM_TYPE_LEFT:
//                viewHolder = new ChatAcceptViewHolder(parent,onItemClickListener,handler);
                break;
            case Constants.CHAT_ITEM_TYPE_RIGHT:
//                viewHolder = new ChatSendViewHolder(parent,onItemClickListener,handler);
                break;
            case Constants.CHAT_ITEM_TYPE_Tip:
                viewHolder = new ChatTipViewHolder(parent);
                break;
            case Constants.CHAT_ITEM_TYPE_LEFT_TEXT:
                viewHolder = new ChatLeftTextViewHolder(parent,handler);
                break;
            case Constants.CHAT_ITEM_TYPE_LEFT_IMG:
                viewHolder = new ChatLeftImgViewHolder(parent,onItemClickListener);
                break;
            case Constants.CHAT_ITEM_TYPE_LEFT_VOICE:
                viewHolder = new ChatLeftVoiceViewHolder(parent);
                break;
            case Constants.CHAT_ITEM_TYPE_LEFT_VIDEO:
                viewHolder = new ChatLeftVideoViewHolder(parent);
                break;
            case Constants.CHAT_ITEM_TYPE_RIGHT_TEXT:
                viewHolder = new ChatRightTextViewHolder(parent,handler);
                break;
            case Constants.CHAT_ITEM_TYPE_RIGHT_IMG:
                viewHolder = new ChatRightImgViewHolder(parent,onItemClickListener);
                break;
            case Constants.CHAT_ITEM_TYPE_RIGHT_VOICE:
                viewHolder = new ChatRightVoiceViewHolder(parent);
                break;
            case Constants.CHAT_ITEM_TYPE_RIGHT_VIDEO:
                viewHolder = new ChatRightVideoViewHolder(parent);
                break;
        }
        return viewHolder;
    }

    @Override
    public int getViewType(int position) {
        return getAllData().get(position).getType();
    }

    public void addItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onHeaderClick(int position);
        void onImageClick(View view,int position,String url);
        void onVoiceClick(ImageView imageView,int position,String filePath);
    }
}
