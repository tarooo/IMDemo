package com.taro.imdemo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.easyrecyclerview.EasyRecyclerView;
import com.taro.imdemo.adapter.ChatAdapter;
import com.taro.imdemo.enity.MessageInfo;
import com.taro.imdemo.util.AudioRecodeUtils;
import com.taro.imdemo.util.Constants;
import com.taro.imdemo.util.Utils;
import com.taro.imdemo.view.PopupWindowFactory;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by taro on 2018/4/26.
 */

public class EmotionInputDetector {

    public static final String SHARE_PREFERENCE_NAME = "com.taro.emotioninputdetector";
    public static final String SHARE_PREFERENCE_TAG = "soft_input_height";
    private Activity activity;
    private InputMethodManager inputMethodManager;
    private SharedPreferences sp;

    private EasyRecyclerView mContentView;
    private EditText mEditText;
    private View mSendButton;
    private View mAddButton;
    private TextView mVoiceTextView;
    private View mBottomLayout;
    private ViewPager mViewPager;

    private AudioRecodeUtils mAudioRecodeUtils;
    private PopupWindowFactory mVoicePop;
    private TextView mPopVoiceText;
    private ChatAdapter adapter;

    private boolean isShowEmotion = false;
    private boolean isShowAdd = false;

    public static EmotionInputDetector with(Activity activity) {
        EmotionInputDetector emotionInputDetector = new EmotionInputDetector();
        emotionInputDetector.activity = activity;
        emotionInputDetector.inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        emotionInputDetector.sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return emotionInputDetector;
    }

    public EmotionInputDetector build() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hideSoftInput();
        mAudioRecodeUtils = new AudioRecodeUtils();

        View view = View.inflate(activity, R.layout.pop_microphone, null);
        mVoicePop = new PopupWindowFactory(activity, view);
        final ImageView imageView = (ImageView) view.findViewById(R.id.img_recording_icon);
        final TextView timeTv = (TextView) view.findViewById(R.id.tv_recording_time);
        mPopVoiceText = (TextView) view.findViewById(R.id.tv_recording_text);

        mAudioRecodeUtils.setOnAudioStatusUpdateListener(new AudioRecodeUtils.OnAudioStatusUpdateListener() {
            @Override
            public void onUpdate(double db, long time) {
                imageView.getDrawable().setLevel((int) (3000 + 6000 * db / 100));
                timeTv.setText(Utils.long2String(time));
            }

            @Override
            public void onStop(long time, String filePath) {
                timeTv.setText(Utils.long2String(0));
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT_VOICE);
                messageInfo.setFilepath(filePath);
                messageInfo.setVoiceTime(time);
                EventBus.getDefault().post(messageInfo);
            }

            @Override
            public void onError() {
            }
        });
        return this;
    }

    public EmotionInputDetector bindToContent(EasyRecyclerView contentView) {
        mContentView = contentView;
        return this;
    }

    public EmotionInputDetector bindToRvContent(ChatAdapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public EmotionInputDetector bindToSendButton(View sendButton) {
        mSendButton = sendButton;
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAddButton.setVisibility(View.VISIBLE);
                mSendButton.setVisibility(View.GONE);
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setContent(mEditText.getText().toString());
                messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT_TEXT);
                EventBus.getDefault().post(messageInfo);
                mEditText.setText("");
            }
        });
        return this;
    }

    public EmotionInputDetector bindToVoiceText(TextView voiceText) {
        mVoiceTextView = voiceText;
        mVoiceTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int x = (int) motionEvent.getX();
                int y = (int) motionEvent.getY();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mVoicePop.showAtLocation(view, Gravity.CENTER, 0, 0);
                        mVoiceTextView.setText("松开结束");
                        mPopVoiceText.setText("手指上滑，取消发送");
                        mVoiceTextView.setTag("1");
                        mAudioRecodeUtils.startRecord(activity);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (wantToCancle(x, y)) {
                            mVoiceTextView.setText("松开结束");
                            mPopVoiceText.setText("松开手指，取消发送");
                            mVoiceTextView.setTag("2");
                        } else {
                            mVoiceTextView.setText("松开结束");
                            mPopVoiceText.setText("手指上滑，取消发送");
                            mVoiceTextView.setTag("1");
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mVoicePop.dismiss();
                        if (mVoiceTextView.getTag().equals("2")) {
                            //取消录音
                            mAudioRecodeUtils.cancelRecord();
                        } else {
                            //结束录音
                            mAudioRecodeUtils.stopRecord();
                        }
                        mVoiceTextView.setText("按住说话");
                        mVoiceTextView.setTag("3");
                        break;
                }
                return true;
            }
        });
        return this;
    }

    private boolean wantToCancle(int x, int y) {
        if (x < 0 || x > mVoiceTextView.getWidth())
            return true;
        if (y < -50 || y > mVoiceTextView.getHeight() + 50)
            return true;
        return false;
    }

    public EmotionInputDetector setBottomView(View bottomView) {
        mBottomLayout = bottomView;
        return this;
    }

    public EmotionInputDetector setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        return this;
    }

    public EmotionInputDetector bindToVoiceButton(View voiceButton) {
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideEmotionLayout(false);
                hideSoftInput();
                mVoiceTextView.setVisibility(mVoiceTextView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                mEditText.setVisibility(mEditText.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        return this;
    }

    public EmotionInputDetector bindToEmotionButton(View emotionButton) {
        emotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVoiceTextView.setVisibility(View.GONE);
                mEditText.setVisibility(View.VISIBLE);
                if (mBottomLayout.isShown()) {
                    if (isShowAdd) {
                        mViewPager.setCurrentItem(0);
                        isShowEmotion = true;
                        isShowAdd = false;
                    } else {
                        lockContentHeight();
                        hideEmotionLayout(true);
                        isShowEmotion = false;
                        unlockContentHeightDelayed();
                    }
                } else {
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();
                    }
                    mViewPager.setCurrentItem(0);
                    isShowEmotion = true;
                }
                scrollRvBottom();
            }
        });
        return this;
    }

    public EmotionInputDetector bindToAddButton(View addButton) {
        mAddButton = addButton;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVoiceTextView.setVisibility(View.GONE);
                mEditText.setVisibility(View.VISIBLE);
                if (mBottomLayout.isShown()) {
                    if (isShowEmotion) {
                        mViewPager.setCurrentItem(1);
                        isShowAdd = true;
                        isShowEmotion = false;
                    } else {
                        lockContentHeight();
                        hideEmotionLayout(true);
                        isShowAdd = false;
                        unlockContentHeightDelayed();
                    }
                } else {
                    if (isSoftInputShown()) {
                        lockContentHeight();
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        showEmotionLayout();
                    }
                    mViewPager.setCurrentItem(1);
                    isShowAdd = true;
                }
                scrollRvBottom();
            }
        });
        return this;
    }

    public EmotionInputDetector bindToEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP && mBottomLayout.isShown()) {
                    lockContentHeight();
                    hideEmotionLayout(true);
                    unlockContentHeightDelayed();
                }
                mEditText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollRvBottom();
                    }
                }, 200L);
                return false;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    mAddButton.setVisibility(View.GONE);
                    mSendButton.setVisibility(View.VISIBLE);
                } else {
                    mAddButton.setVisibility(View.VISIBLE);
                    mSendButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return this;
    }

    private void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                inputMethodManager.showSoftInput(mEditText, 0);
            }
        });
    }

    public void scrollRvBottom() {
//        mContentView.scrollToPosition(adapter.getCount() - 1);
    }

    public void hideSoftInput() {
        inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        int screenHeight = activity.getWindow().getDecorView().getRootView().getHeight();
        int softInputHeight = screenHeight - r.bottom;
        if (Build.VERSION.SDK_INT >= 20) {
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }
        if (softInputHeight < 0) {

        }
        if (softInputHeight > 0) {
            sp.edit().putInt(SHARE_PREFERENCE_TAG, softInputHeight).apply();
        }
        return softInputHeight;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /*返回键操作*/
    public boolean interceptBackPress() {
        if (mBottomLayout.getVisibility() == View.VISIBLE) {
            hideEmotionLayout(false);
            return true;
        }
        return false;
    }

    public void hideEmotionLayout(boolean showSoftInput) {
        if (mBottomLayout.isShown()) {
            mBottomLayout.setVisibility(View.GONE);
            if (showSoftInput) showSoftInput();
        }
    }

    private void showEmotionLayout() {
        hideSoftInput();
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = sp.getInt(SHARE_PREFERENCE_TAG, 787);
        }
        mBottomLayout.getLayoutParams().height = softInputHeight;
        mBottomLayout.setVisibility(View.VISIBLE);
    }

    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

}
