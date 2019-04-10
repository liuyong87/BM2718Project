package com.semisky.multimedia.media_music.model;


import android.os.RemoteException;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.aidl.usb.IMediaScannerStateListener;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_music.presenter.IMusicListPresenter;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel.OnServiceConnectionCompletedListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/8.
 */

public class MusicDataModel implements IMusicDataModel {
    private static final String TAG = Logutil.makeTagLog(MusicDataModel.class);
    private MediaStorageAccessProxyModel mediaStorageAccessProxyModel;
    private OnRefreshDataListener mOnRefreshDataListener;

    public MusicDataModel() {
        mediaStorageAccessProxyModel = MediaStorageAccessProxyModel.getInstance();
        mediaStorageAccessProxyModel.registerOnMediaScannerStateListener(mIMediaScannerStateListener);
        mediaStorageAccessProxyModel.registerOnServiceConnectionCompletedListener(mOnServiceConnectionCompletedListener);
    }

    @Override
    public void registerOnRefreshDataListener(OnRefreshDataListener listener) {
        this.mOnRefreshDataListener = listener;
    }

    @Override
    public void unregisterOnRefreshDataListener() {
        this.mOnRefreshDataListener = null;
        mediaStorageAccessProxyModel.unregisterOnMediaScannerStateListener(mIMediaScannerStateListener);
        mediaStorageAccessProxyModel.unregisterOnServiceConnectionCompletedListener();
    }

    @Override
    public void onLoadMusicInfoList(final OnLoadDataListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MusicInfo> musicInfos = mediaStorageAccessProxyModel.queryAllMusics(Definition.FLAG_USB1);
                if (null != listener) {
                    listener.onLoadData(musicInfos);
                }
            }
        }).start();
    }

    @Override
    public void onLoadFavoriteList(final OnLoadDataListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MusicInfo> musicInfos = mediaStorageAccessProxyModel.queryAllFavoriteMusics(Definition.FLAG_USB1);
                musicInfos = getValidMediaInfos(musicInfos);
                if (null != listener) {
                    listener.onLoadData(musicInfos);
                }
            }
        }).start();
    }

    // 获取有效收藏列表
    private List<MusicInfo> getValidMediaInfos(List<MusicInfo> infos) {

        if (null == infos || infos.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<MusicInfo> validMusicInfoList = new ArrayList<MusicInfo>();
        List<String> invalidMusicInfoList = new ArrayList<String>();
        for (MusicInfo info : infos) {
            if ((new File(info.getUrl()).exists())) {
                validMusicInfoList.add(info);
                continue;
            }
            invalidMusicInfoList.add(info.getUrl());
        }

        if(invalidMusicInfoList.size() > 0){
            onBatchDeleteFavoriteWith(invalidMusicInfoList);
        }
        Logutil.i(TAG,"getValidMediaInfos() validMusicInfoList.Size()="+validMusicInfoList.size());
        Logutil.i(TAG,"getValidMediaInfos() invalidMusicInfoList.Size()="+invalidMusicInfoList.size());
        return validMusicInfoList;
    }


    private IMediaScannerStateListener.Stub mIMediaScannerStateListener = new IMediaScannerStateListener.Stub() {
        @Override
        public void onScannerStart(int usbFlag) throws RemoteException {
            Logutil.i(TAG, "onScannerStart() ...");

        }

        @Override
        public void onScanning(int usbFlag) throws RemoteException {
            Logutil.i(TAG, "onScanning() ...");
            refreshData();
        }

        @Override
        public void onScannerStoped(int usbFlag) throws RemoteException {
            Logutil.i(TAG, "onScannerStoped() ...");
        }

        @Override
        public void onScannerDone(int usbFlag) throws RemoteException {
            Logutil.i(TAG, "onScannerDone() ...");
            refreshData();
        }
    };

    private OnServiceConnectionCompletedListener mOnServiceConnectionCompletedListener = new OnServiceConnectionCompletedListener() {
        @Override
        public void onServiceConnectionCompleted() {
            Logutil.i(TAG, "onServiceConnectionCompleted() ...");
            refreshData();
        }
    };

    // 刷新媒体数据
    private void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MusicInfo> musicInfos = mediaStorageAccessProxyModel.queryAllMusics(Definition.FLAG_USB1);
                if (null != mOnRefreshDataListener) {
                    mOnRefreshDataListener.onUpdateData(musicInfos);
                }
            }
        }).start();
    }

    @Override
    public void onChangeMusicFavoriteState(OnFavoriteChangeListener listener, MusicInfo info) {
        if (null == info) {
            Logutil.e(TAG, "onChangeMusicFavoriteState() FAIL ，ADD INFO EMPTY !!!");
            return;
        }

        boolean isFavorite = isFavorite(info.getUsbFlag(), info.getUrl());
        Logutil.i(TAG, "================");
        Logutil.i(TAG, "onChangeMusicFavoriteState() URL=" + info.getUrl());
        Logutil.i(TAG, "onChangeMusicFavoriteState() isFavorite=" + isFavorite);
        Logutil.i(TAG, "================");
        if (isFavorite) {
            // 取消收藏
            boolean isDelFavorite = delFavorite(info.getUsbFlag(), info.getUrl());
            // 取消收藏成功
            if (isDelFavorite) {
                listener.onChangeFavoriteState(false);

            }
        }
        // 未收藏過，收藏文件
        else {
            // 添加收藏
            info.setFavorite(1);
            boolean isAddFavorite = addFavoriteToDB(info);
            Logutil.i(TAG, "onChangeMusicFavoriteState() ...isAddFavorite=" + isAddFavorite);
            // 添加收藏成功
            if (isAddFavorite) {
                listener.onChangeFavoriteState(true);
            }
        }
    }

    @Override
    public void onDeleteMusicFavoriteState(OnFavoriteChangeListener l, MusicInfo info) {
        if (null == info) {
            Logutil.e(TAG, "onChangeMusicFavoriteState() FAIL ，ADD INFO EMPTY !!!");
            return;
        }

        boolean isFavorite = isFavorite(info.getUsbFlag(), info.getUrl());
        if (isFavorite){
            l.onChangeFavoriteState(!isFavorite);
        }

    }

    // 取消收藏
    private boolean delFavorite(int usbFlag, String url) {
        long delFavoriteRow = mediaStorageAccessProxyModel.deleteFavoriteWithMusicUrl(usbFlag, url);
        Logutil.i(TAG, "delFavorite() ..." + delFavoriteRow);
        if (delFavoriteRow > 0) {
            return true;
        }
        return false;
    }

    // 添加音乐收藏到数据库
    private boolean addFavoriteToDB(MusicInfo info) {
        if (null == info) {
            return false;
        }
        long insertFavoriteRow = mediaStorageAccessProxyModel.insertFavoriteMusic(info);
        Logutil.i(TAG, "addFavoriteToDB() ..." + insertFavoriteRow);
        if (insertFavoriteRow > 0) {
            return true;
        }
        return false;
    }

    // 是否收藏
    private boolean isFavorite(int usbFlag, String url) {
        Logutil.i(TAG, "isFavorite() usbFlag=" + usbFlag + " , url=" + url);
        if (Definition.FLAG_USB1 == usbFlag || Definition.FLAG_USB2 == usbFlag) {
            return mediaStorageAccessProxyModel.isFavoriteWithSpecifyMusicUrl(usbFlag, url);
        }
        return false;
    }

    @Override
    public void onLoadFavoriteState(final OnFavoriteChangeListener l, final int usbFlag, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isFavorite = false;
                if (Definition.FLAG_USB1 == usbFlag || Definition.FLAG_USB2 == usbFlag) {
                    isFavorite = mediaStorageAccessProxyModel.isFavoriteWithSpecifyMusicUrl(usbFlag, url);
                }
                Logutil.i(TAG, "onLoadFavoriteState() ..." + isFavorite);
                Logutil.i(TAG, "onLoadFavoriteState() ..." + usbFlag);
                Logutil.i(TAG, "onLoadFavoriteState() ..." + url);
                if (null != l) {
                    l.onChangeFavoriteState(isFavorite);
                }
            }
        }).start();
    }

    @Override
    public void onCancelFavoriteWith(final OnFavoriteChangeListener l, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isDelFavorite = delFavorite(AppUtil.getUsbFlagFrom(url), url);
                l.onChangeFavoriteState(isDelFavorite);
            }
        }).start();
    }

    @Override
    public void onBatchDeleteFavoriteWith(List<String> list) {
        mediaStorageAccessProxyModel.deleteBatchFavorite(list);
    }
}
