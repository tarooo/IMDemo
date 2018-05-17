package com.taro.imdemo.util;

import android.os.Environment;

/**
 * Created by taro on 2018/5/7.
 */

public class Constants {
    /** 0x001-接受消息  0x002-发送消息**/
    public static final int CHAT_ITEM_TYPE_LEFT = 0x001;
    public static final int CHAT_ITEM_TYPE_RIGHT = 0x002;
    public static final int CHAT_ITEM_TYPE_Tip = 0x006;
    public static final int CHAT_ITEM_TYPE_LEFT_TEXT = 0x007;
    public static final int CHAT_ITEM_TYPE_LEFT_IMG = 0x008;
    public static final int CHAT_ITEM_TYPE_LEFT_VOICE = 0x009;
    public static final int CHAT_ITEM_TYPE_RIGHT_TEXT = 0x010;
    public static final int CHAT_ITEM_TYPE_RIGHT_IMG = 0x011;
    public static final int CHAT_ITEM_TYPE_RIGHT_VOICE = 0x012;
    public static final int CHAT_ITEM_TYPE_LEFT_VIDEO = 0x013;
    public static final int CHAT_ITEM_TYPE_RIGHT_VIDEO = 0x014;


    /** 0x003-发送中  0x004-发送失败  0x005-发送成功**/
    public static final int CHAT_ITEM_SENDING = 0x003;
    public static final int CHAT_ITEM_SEND_ERROR = 0x004;
    public static final int CHAT_ITEM_SEND_SUCCESS = 0x005;


    public static final String CHAT_VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/imdemo/video/";
    public static final String CHAT_VOICE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/imdemo/voice/";
}
