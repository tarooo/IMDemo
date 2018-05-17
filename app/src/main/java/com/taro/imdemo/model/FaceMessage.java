package com.taro.imdemo.model;

/**
 * Created by taro on 2018/4/26.
 */

public class FaceMessage extends Message {
    public String faceUrl;

    public FaceMessage(int status, String faceUrl) {
        this.faceUrl = faceUrl;
        this.status = status;
    }
}
