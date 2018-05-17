package com.taro.imdemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.xselector.XSelector;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.taro.imdemo.EmotionInputDetector;
import com.taro.imdemo.R;
import com.taro.imdemo.adapter.ChatAdapter;
import com.taro.imdemo.adapter.CommonFragmentPagerAdapter;
import com.taro.imdemo.enity.FullImageInfo;
import com.taro.imdemo.enity.MessageInfo;
import com.taro.imdemo.model.Message;
import com.taro.imdemo.ui.fragment.ChatEmotionFragment;
import com.taro.imdemo.ui.fragment.ChatFunctionFragment;
import com.taro.imdemo.util.Constants;
import com.taro.imdemo.util.GlobalOnItemClickManagerUtils;
import com.taro.imdemo.util.Utils;
import com.taro.imdemo.view.NoScrollViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {

    private EasyRecyclerView msgRv;
    private ChatAdapter adapter;
    private List<MessageInfo> messages = new ArrayList<>();

    private ImageView voiceImg;
    private EditText msgEdt;
    private TextView pressTv;
    private ImageView iconImg;
    private ImageView addImg;
    private TextView sendTv;
    private RelativeLayout bottomRl;
    private NoScrollViewPager bottomVp;

    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        initRecyclerView();

        initBottomView();

        initFragment();
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);

        pagerAdapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        bottomVp.setAdapter(pagerAdapter);
        bottomVp.setCurrentItem(0);
    }

    private void initBottomView() {
        voiceImg = (ImageView) findViewById(R.id.img_voice);
        msgEdt = (EditText) findViewById(R.id.edt_msg);
        pressTv = (TextView) findViewById(R.id.tv_press);
        iconImg = (ImageView) findViewById(R.id.img_icon);
        addImg = (ImageView) findViewById(R.id.img_add);
        sendTv = (TextView) findViewById(R.id.tv_send);
        bottomRl = (RelativeLayout) findViewById(R.id.rl_bottom);
        bottomVp = (NoScrollViewPager) findViewById(R.id.vp_bottom);

        XSelector.shapeSelector().radius(5f).defaultBgColor(R.color.colorPrimary).pressedBgColor(R.color.colorPrimaryDark).into(sendTv);

        mDetector = EmotionInputDetector.with(this)
                .bindToContent(msgRv)
                .bindToAddButton(addImg)
                .bindToSendButton(sendTv)
                .bindToVoiceButton(voiceImg)
                .bindToVoiceText(pressTv)
                .setBottomView(bottomRl)
                .bindToEmotionButton(iconImg)
                .setViewPager(bottomVp)
                .bindToEditText(msgEdt)
                .bindToRvContent(adapter)
                .build();

//        mDetector.scrollRvBottom();
        GlobalOnItemClickManagerUtils globalOnItemClickManagerUtils = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickManagerUtils.attachToEditText(msgEdt);
    }

    private void initRecyclerView() {
        msgRv = (EasyRecyclerView) findViewById(R.id.rv_message);
        adapter = new ChatAdapter(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        msgRv.setLayoutManager(layoutManager);
        msgRv.setAdapter(adapter);
        msgRv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        adapter.handler.removeCallbacksAndMessages(null);
                        adapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        adapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }
        });
        adapter.addItemClickListener(itemClickListener);
        loadData();
    }

    private void loadData() {
        String imgUrl1 = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=862591842,2864954084&fm=27&gp=0.jpg";
        String imgUrl2 = "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2119206760,2895156510&fm=27&gp=0.jpg";

        MessageInfo messageInfo6 = new MessageInfo();
        messageInfo6.setContent("14.04");
        messageInfo6.setType(Constants.CHAT_ITEM_TYPE_Tip);
        messages.add(messageInfo6);

        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setContent("在吗？");
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT_TEXT);
        messages.add(messageInfo);

        MessageInfo messageInfo1 = new MessageInfo();
        messageInfo1.setImageUrl(imgUrl1);
        messageInfo1.setType(Constants.CHAT_ITEM_TYPE_LEFT_IMG);
        messages.add(messageInfo1);

        MessageInfo messageInfo5 = new MessageInfo();
        messageInfo5.setFilepath(Environment.getExternalStorageDirectory() + "/imdemo/record/20180507090013.amr");
        messageInfo5.setVoiceTime(2000L);
        messageInfo5.setType(Constants.CHAT_ITEM_TYPE_LEFT_VOICE);
        messages.add(messageInfo5);

        MessageInfo messageInfo3 = new MessageInfo();
        messageInfo3.setContent("你喜欢吃水果吗？");
        messageInfo3.setType(Constants.CHAT_ITEM_TYPE_RIGHT_TEXT);
        messages.add(messageInfo3);

        MessageInfo messageInfo4 = new MessageInfo();
        messageInfo4.setImageUrl(imgUrl2);
        messageInfo4.setType(Constants.CHAT_ITEM_TYPE_RIGHT_IMG);
        messages.add(messageInfo4);

        adapter.addAll(messages);
    }

    private ChatAdapter.OnItemClickListener itemClickListener = new ChatAdapter.OnItemClickListener() {
        @Override
        public void onHeaderClick(int position) {

        }

        @Override
        public void onImageClick(View view, int position, String url) {
            ImageView iv = (ImageView) view;
            int location[] = new int[2];
            iv.getLocationOnScreen(location);
            FullImageInfo fullImageInfo = new FullImageInfo();
            fullImageInfo.setLocationX(location[0]);
            fullImageInfo.setLocationY(location[1]);
            fullImageInfo.setWidth(iv.getWidth());
            fullImageInfo.setHeight(iv.getHeight());
            fullImageInfo.setImageUrl(url);
            EventBus.getDefault().postSticky(fullImageInfo);
            startActivity(new Intent(MainActivity.this, FullImageActivity.class));
            overridePendingTransition(0, 0);
        }

        @Override
        public void onVoiceClick(ImageView imageView, int position, String filePath) {

        }
    };

    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MessageEventBus(MessageInfo messageInfo) {
        MessageInfo messageInfo6 = new MessageInfo();
        messageInfo6.setContent(Utils.getCurrentDayTime());
        messageInfo6.setType(Constants.CHAT_ITEM_TYPE_Tip);
        messages.add(messageInfo6);
        adapter.add(messageInfo6);

        messages.add(messageInfo);
        adapter.add(messageInfo);
        msgRv.scrollToPosition(adapter.getCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }
}
