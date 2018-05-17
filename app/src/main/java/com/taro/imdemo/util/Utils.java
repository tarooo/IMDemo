package com.taro.imdemo.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.name;
import static android.R.attr.path;

/**
 * Created by taro on 2018/4/27.
 */

public class Utils {

    public static int dp2px(Context context,float dp){
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density +0.5F);
    }

    public static String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyMMddHHmmss");
        return sdf.format(System.currentTimeMillis());
    }

    public static String getCurrentDayTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }

    //毫秒转秒
    public static String long2String(long time){
        int sec = (int) (time / 1000);
        int min = sec / 60;
        sec = sec % 60;
        if (min < 10){
            if (sec < 10){
                return "0"+min+":0"+sec;
            } else {
                return "0"+min+":"+sec;
            }
        } else {
            if (sec < 10){
                return min+":0"+sec;
            } else {
                return min+":"+sec;
            }
        }
    }

    public static SpannableString getEmotionContent(Context context, TextView tv,String source){
        SpannableString spannableString = new SpannableString(source);
        Resources res = context.getResources();

        String regexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
        Pattern patternEmotion = Pattern.compile(regexEmotion);
        Matcher matcherEmotion = patternEmotion.matcher(spannableString);

        while (matcherEmotion.find()){
            String key = matcherEmotion.group();
            int start = matcherEmotion.start();
            Integer imgRes = EmotionUtils.EMOTION_STATIC_MAP.get(key);
            if (imgRes != null){
                int size = (int) (tv.getTextSize() * 13 / 8);
                Bitmap bitmap = BitmapFactory.decodeResource(res,imgRes);
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap,size,size,true);
                ImageSpan span = new ImageSpan(context,scaleBitmap);
                spannableString.setSpan(span,start,start+key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannableString;
    }

    public static String formatTime(Long ms){
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0){
            sb.append(day+"d");
        }
        if (hour > 0){
            sb.append(hour+"h");
        }
        if (minute > 0){
            sb.append(minute + "'");
        }
        if (second > 0){
            sb.append(second+"\"");
        }
        return sb.toString();
    }

    public static boolean isExist(Context context,String path){
        File file = new File(path);
        if (file.exists()){
            return true;
        } else {
            Toast.makeText(context,"文件不存在",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static boolean isFileExist(String path){
        File file = new File(path);
        if (!file.exists()){
            if (file.mkdirs()){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0 || str.equalsIgnoreCase("null");
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
