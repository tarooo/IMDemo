package com.taro.imdemo.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.taro.imdemo.R;
import com.taro.imdemo.base.HBaseFragment;
import com.taro.imdemo.enity.MessageInfo;
import com.taro.imdemo.ui.activity.VideoRecorderActivity;
import com.taro.imdemo.util.Constants;
import com.taro.imdemo.util.Utils;
import com.werb.permissionschecker.PermissionChecker;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.bean.MediaBean;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;

/**
 * Created by taro on 2018/4/28.
 */

public class ChatFunctionFragment extends HBaseFragment implements View.OnClickListener {

    private View rootView;
    private TextView photoFunctionTv;
    private TextView photographFunctionTv;

    private PermissionChecker permissionChecker;
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_chat_function, container, false);
            initView();
        }
        return rootView;
    }

    private void initView() {
        photoFunctionTv = (TextView) rootView.findViewById(R.id.tv_function_photo);
        photographFunctionTv = (TextView) rootView.findViewById(R.id.tv_function_photograph);
        photoFunctionTv.setOnClickListener(this);
        photographFunctionTv.setOnClickListener(this);

        permissionChecker = new PermissionChecker(getActivity());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_function_photo:
                openMulti();
                break;
            case R.id.tv_function_photograph:
                if (permissionChecker.isLackPermissions(PERMISSIONS)) {
                    permissionChecker.requestPermissions();
                } else {
                    startVideo();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionChecker.PERMISSION_REQUEST_CODE:
                if (permissionChecker.hasAllPermissionsGranted(grantResults)) {
                    startVideo();
                } else {
                    permissionChecker.showDialog();
                }
                break;
        }
    }

    private void startVideo() {
        startActivity(new Intent(getActivity(), VideoRecorderActivity.class));
        getActivity().overridePendingTransition(0, 0);
    }

    private void openMulti() {
        RxGalleryFinal.with(getActivity())
                .image()
                .multiple()
                .maxSize(4)
                .crop(false)
                .imageLoader(ImageLoaderType.GLIDE)
                .subscribe(new RxBusResultDisposable<ImageMultipleResultEvent>() {
                    @Override
                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                        List<MediaBean> images = imageMultipleResultEvent.getResult();
                        MessageInfo messageInfo;
                        for (int i = 0; i < images.size(); i++) {
                            messageInfo = new MessageInfo();
                            messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT_IMG);
                            messageInfo.setImageUrl(images.get(i).getOriginalPath());
                            EventBus.getDefault().post(messageInfo);
                        }
                    }
                })
                .openGallery();
    }
}
