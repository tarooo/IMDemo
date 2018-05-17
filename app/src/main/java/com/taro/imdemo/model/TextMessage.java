package com.taro.imdemo.model;

/**
 * Created by taro on 2018/4/26.
 */

public class TextMessage extends Message {
    public String text;

    public TextMessage() {
    }

    public TextMessage(int status, String text) {
        this.text = text;
        this.status = status;
    }


}
