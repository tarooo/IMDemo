package com.taro.imdemo.model;

/**
 * Created by taro on 2018/4/26.
 */

public class ImageMessage extends Message {
    public String imgUrl;

    public ImageMessage(int status, String imgUrl) {
        this.imgUrl = imgUrl;
        this.status = status;
    }
}
