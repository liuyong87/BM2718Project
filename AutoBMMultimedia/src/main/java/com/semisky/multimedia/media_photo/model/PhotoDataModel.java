package com.semisky.multimedia.media_photo.model;

import android.os.RemoteException;

import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.aidl.usb.IMediaScannerStateListener;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel.OnServiceConnectionCompletedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anter on 2018/8/4.
 */

public class PhotoDataModel implements IPhotoDataModel {
    private static final String TAG = Logutil.makeTagLog(PhotoDataModel.class);
    private MediaStorageAccessProxyModel mediaStorageAccessProxyModel;
    private OnRefreshDataListener mOnRefreshDataListener;


    public PhotoDataModel() {
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
    public void onLoadPhotoInfoList(final OnLoadDataListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (null != listener) {
                    List<PhotoInfo> photoInfos = mediaStorageAccessProxyModel.queryAllPhotos(Definition.FLAG_USB1);
                    //无数据时也需要回调此函数，否则无数据时图片界面没有提示
                    listener.onLoadData(photoInfos);

                }
            }
        }).start();
    }


    private IMediaScannerStateListener.Stub mIMediaScannerStateListener = new IMediaScannerStateListener.Stub() {
        @Override
        public void onScannerStart(int usbFlag) throws RemoteException {

        }

        @Override
        public void onScanning(int usbFlag) throws RemoteException {
            refreshData();
        }

        @Override
        public void onScannerStoped(int usbFlag) throws RemoteException {

        }

        @Override
        public void onScannerDone(int usbFlag) throws RemoteException {
            refreshData();
        }
    };

    private OnServiceConnectionCompletedListener mOnServiceConnectionCompletedListener = new OnServiceConnectionCompletedListener() {
        @Override
        public void onServiceConnectionCompleted() {
            refreshData();
        }
    };

    private void refreshData() {
        if (null != mOnRefreshDataListener) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<PhotoInfo> photoInfos = mediaStorageAccessProxyModel.queryAllPhotos(Definition.FLAG_USB1);
                    if (null != photoInfos && null != mOnRefreshDataListener) {
                        mOnRefreshDataListener.onUpdateData(photoInfos);
                    }
                }
            }).start();
        }
    }

}
