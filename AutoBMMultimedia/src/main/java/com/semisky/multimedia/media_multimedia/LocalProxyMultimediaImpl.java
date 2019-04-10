package com.semisky.multimedia.media_multimedia;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;

import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.IProxyMultimedia;
import com.semisky.multimedia.common.base_view.ToastCustom;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.InterruptEventManager;
import com.semisky.multimedia.common.manager.PreferencesManager;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.USBCheckUtil;
import com.semisky.multimedia.media_usb.model.IMediaStorageDataModel;
import com.semisky.multimedia.media_usb.model.MediaStorageDataModel;
import com.semisky.multimedia.media_usb.view.MediaScanDialog;

import java.io.File;

/**
 * Created by LiuYong on 2018/9/3.
 */

public class LocalProxyMultimediaImpl extends IProxyMultimedia.Stub {
    private static final String TAG = Logutil.makeTagLog(LocalProxyMultimediaImpl.class);
    private static LocalProxyMultimediaImpl _INSTANCE;
    private Context mContext;
    private IMediaStorageDataModel mMediaStorageDataModel;
    private MediaScanDialog mMediaScanDialog;
    private Handler _handler = new Handler(Looper.getMainLooper());

    private LocalProxyMultimediaImpl() {
        mMediaStorageDataModel = new MediaStorageDataModel();

    }

    public static LocalProxyMultimediaImpl getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new LocalProxyMultimediaImpl();
        }
        return _INSTANCE;
    }

    public void init(Context ctx) {
        this.mContext = ctx;
    }

    @Override
    public int getMultimediaAppFlag() throws RemoteException {
        int validAppFlag = -1;
        Logutil.i(TAG, "getMultimediaAppFlag() !!! " + validAppFlag);
        if (isMountedUsb()) {
            validAppFlag = getAppFlag();
            boolean isValidAppFlag = hasValidAppFlagWith(validAppFlag);
            validAppFlag = isValidAppFlag ? validAppFlag : -1;
            Logutil.i(TAG, "getMultimediaAppFlag() ,isValidAppFlag=" + isValidAppFlag);
        }
        return validAppFlag;
    }

    @Override
    public boolean hasValidAppFlagWith(int appFlag) throws RemoteException {
        int size = -1;
        //bug U盘没有挂载，mode切换也能打开音乐，U盘没有挂载，直接返回false
        if (!isMountedUsb()){
            Logutil.i(TAG,"usb not unMount");
            return false;
        }
        SemiskyIVIManager.getInstance().setAllowMusicPlay();//允许多媒体播放，如果是收音机在前台，断电后，插入U盘不会跳转到多媒体。
        switch (appFlag) {
            case Definition.AppFlag.TYPE_MUSIC:
                size = mMediaStorageDataModel.queryMusicsSize(Definition.FLAG_USB1);
                break;
            case Definition.AppFlag.TYPE_VIDEO:
                size = mMediaStorageDataModel.queryVideosSize(Definition.FLAG_USB1);
                break;
            case Definition.AppFlag.TYPE_PHOTO:
                size = mMediaStorageDataModel.queryPhotosSize(Definition.FLAG_USB1);
                break;
            default:
                Logutil.w(TAG, "INVALID APP FLAG !!!");
                break;
        }
        boolean isValidAppFlag = (size > 0) ? true : false;
        Logutil.i(TAG, "");
        return isValidAppFlag;
    }


    @Override
    public void onLaunchMultimedia() throws RemoteException {
        Logutil.i("lcc","launcher: "+InterruptEventManager.getInstance().getIsFormLauncherStart());
        if (isMountedUsb() && InterruptEventManager.getInstance().getIsFormLauncherStart()) {
            int lastAppFlag = getAppFlag();// 获取断点应用标识
            //应用标识是否有效（对应应用标识媒体数据大于0为有效）
            boolean isValidAppFlag = hasValidAppFlagWith(lastAppFlag);
            // 对应应用标识媒体记忆是否有效（断点记忆URL存在为有效）
            boolean isValidUrl = hasValidUrlWith(lastAppFlag);
            // 获取有效应用标识
            int validAppFlag = (isValidAppFlag && isValidUrl) ? lastAppFlag : 4;
            Logutil.i(TAG, "================");
            Logutil.i(TAG, "onLaunchMultimedia() isValidAppFlag=" + isValidAppFlag);
            Logutil.i(TAG, "onLaunchMultimedia() isValidUrl=" + isValidUrl);
            Logutil.i(TAG, "onLaunchMultimedia() validAppFlag=" + validAppFlag);
            Logutil.i(TAG, "================");
            AppUtil.enterPlayerView(validAppFlag);
        } else {
            Logutil.i(TAG, "USB Unmounted !!!");
            // 弹窗显示“请插入U盘”
            dismissDialogWithTimeout();
        }
    }

    @Override
    public void onLaunchMusicPlayService() throws RemoteException {
        Logutil.i(TAG, "onLaunchMusicPlayService() ...");
        //后台切到音乐播放，U盘挂载，必须存在音乐数据
        if (isMountedUsb() && hasMusicFile()){
            Logutil.i(TAG,"back play music");
            AppUtil.resumeBackgroundMusicPlay(mContext); //启动音乐服务，后台播放
            SemiskyIVIManager.getInstance().setAllowMusicPlay();//允许多媒体播放，如果是收音机在前台，断电后，插入U盘不会跳转到多媒体。

        }
    }

    // utils

    // 对应应用标识媒体记忆是否有效（断点记忆URL存在为有效）
    private boolean hasValidUrlWith(int appFlag) {
        boolean isvalidLastUrl = false;
        String lastUrl = null;
        switch (appFlag) {
            case Definition.AppFlag.TYPE_MUSIC:
                lastUrl = PreferencesManager.getLastMusicUrl();
                break;
            case Definition.AppFlag.TYPE_VIDEO:
                lastUrl = PreferencesManager.getLastVideoUrl();
                break;
            case Definition.AppFlag.TYPE_PHOTO:
                lastUrl = PreferencesManager.getLastPhotoUrl();
                break;
            default:
                Logutil.w(TAG, "INVALID APP FLAG !!!");
                break;
        }
        isvalidLastUrl = (null != lastUrl && new File(lastUrl).exists());
        return isvalidLastUrl;
    }

    // utils
    // 延时关闭弹窗
    private void dismissDialogWithTimeout() {
        _handler.post(new Runnable() {
            @Override
            public void run() {
                ToastCustom.showSingleMsg(mContext, R.string.tv_scan_status_unmount_usb_text, ToastCustom.DELAY_TIME_5S);
            }
        });
    }

    // 获取断点应用标识
    private int getAppFlag() {
        int lastAppFlag = PreferencesManager.getLastAppFlag();
        Logutil.i(TAG, "getAppFlag() ..." + lastAppFlag);
        return lastAppFlag;
    }

    // U盘是否挂载
    public boolean isMountedUsb() {
        boolean isMountedUsb = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
        Logutil.i(TAG, "isMountedUsb() ..." + isMountedUsb);
        return isMountedUsb;
    }
    // 2018/10/9 查询是否有音乐文件
    public boolean hasMusicFile(){
       int  size = mMediaStorageDataModel.queryMusicsSize(Definition.FLAG_USB1);
        return size > 0 ? true : false;
    }
    // 2018/10/9 查询是否有视频文件
    public boolean hasVideoFile(){
        int  size  = mMediaStorageDataModel.queryVideosSize(Definition.FLAG_USB1);
        return size > 0 ? true : false;
    }
    // 2018/10/9 查询是否有视频文件
    public boolean hasPictureFile(){
        int  size  = mMediaStorageDataModel.queryPhotosSize(Definition.FLAG_USB1);
        return size > 0 ? true : false;
    }

}
