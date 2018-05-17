package com.taro.imdemo.model;

/**
 * Created by taro on 2018/5/3.
 */

public class VoiceMessage extends Message {
    public String filePath;
    public long voiceTime;

    public VoiceMessage(String filePath, long voiceTime) {
        this.filePath = filePath;
        this.voiceTime = voiceTime;
    }

    public VoiceMessage(int status, String filePath, long voiceTime) {
        this.status = status;
        this.filePath = filePath;
        this.voiceTime = voiceTime;
    }
}
