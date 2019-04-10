package com.semisky.multimedia.media_video.model;

import com.semisky.multimedia.aidl.video.VideoInfo;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/7.
 */

public interface IVideoDataModel {
    void onLoadVideoInfoList(OnLoadDataListener listener);

    void registerOnRefreshDataListener(OnRefreshDataListener listener);

    void unregisterOnRefreshDataListener();


    interface OnLoadDataListener<E> {
        void onLoadData(List<E> dataList);
    }

    interface OnRefreshDataListener {
        void onUpdateData(List<VideoInfo> dataList);
    }


}
