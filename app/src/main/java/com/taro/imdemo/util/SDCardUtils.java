package com.taro.imdemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by taro on 2018/5/17.
 */

public class SDCardUtils {

    // 判断SD卡是否被挂载
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    //保存Bitmap图片到指定目录
    public static boolean saveBitmapToSDCardPrivateDir(Context context, Bitmap bitmap, String filePath) {
        if (isSDCardMounted()) {
            BufferedOutputStream bos = null;
            File file = new File(filePath);
            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                        bitmap.recycle();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
