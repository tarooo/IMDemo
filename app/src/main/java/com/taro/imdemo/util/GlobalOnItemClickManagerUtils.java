package com.taro.imdemo.util;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.taro.imdemo.adapter.EmotionGridViewAdapter;

/**
 * Created by taro on 2018/5/2.
 */

public class GlobalOnItemClickManagerUtils {
    private static GlobalOnItemClickManagerUtils instance;
    private EditText mEditText;
    private static Context mContext;
    public static GlobalOnItemClickManagerUtils getInstance(Context context){
        mContext = context;
        if (instance == null) {
            synchronized (GlobalOnItemClickManagerUtils.class){
                if (instance == null) {
                    instance = new GlobalOnItemClickManagerUtils();
                }
            }
        }
        return instance;
    }

    public void attachToEditText(EditText editText){
        mEditText = editText;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener(){
        return new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object itemAdapter = adapterView.getAdapter();
                if (itemAdapter instanceof EmotionGridViewAdapter){
                    EmotionGridViewAdapter emotionGridViewAdapter = (EmotionGridViewAdapter) itemAdapter;
                    if (position == emotionGridViewAdapter.getCount() - 1){
                        mEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_DEL));
                    } else {
                        String emotionName = emotionGridViewAdapter.getItem(position);
                        int curPosition = mEditText.getSelectionStart();
                        StringBuilder sb = new StringBuilder(mEditText.getText().toString());
                        sb.insert(curPosition,emotionName);
                        mEditText.setText(Utils.getEmotionContent(mContext,mEditText,sb.toString()));
                        mEditText.setSelection(curPosition+emotionName.length());
                    }
                }
            }
        };
    }
}
