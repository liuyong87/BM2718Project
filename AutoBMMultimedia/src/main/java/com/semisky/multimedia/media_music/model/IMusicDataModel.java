package com.semisky.multimedia.media_music.model;

import com.semisky.multimedia.aidl.music.MusicInfo;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/8.
 */

public interface IMusicDataModel {
    void onLoadMusicInfoList(OnLoadDataListener listener);

    void onLoadFavoriteList(OnLoadDataListener listener);

    void registerOnRefreshDataListener(OnRefreshDataListener listener);

    void unregisterOnRefreshDataListener();


    interface OnLoadDataListener<E> {
        void onLoadData(List<E> dataList);
    }

    interface OnRefreshDataListener {
        void onUpdateData(List<MusicInfo> dataList);
    }

    /**
     * 收藏状态变化监听接口
     */
    interface OnFavoriteChangeListener {
        void onChangeFavoriteState(boolean isFavorite);
    }

    /**
     * 添加收藏
     *
     * @param info
     */
    void onChangeMusicFavoriteState(OnFavoriteChangeListener l, MusicInfo info);
    /**
     * 删除收藏
     *
     * @param info
     */
    void onDeleteMusicFavoriteState(OnFavoriteChangeListener l, MusicInfo info);

    /**
     * 加载指定URL收藏状态
     *
     * @param l       收藏状态变化监听接口
     * @param usbFlag U盘标识
     * @param url     资源路径
     */
    void onLoadFavoriteState(OnFavoriteChangeListener l, int usbFlag, String url);

    /**
     * 取消收藏资源URL
     *
     * @param l
     * @param url
     */
    void onCancelFavoriteWith(OnFavoriteChangeListener l, String url);

    /**
     * 批量删除收藏资源文件
     *
     * @param list
     */
    void onBatchDeleteFavoriteWith(List<String> list);

    /**
     * 收藏状态变化监听接口
     */
    interface OnFavoriteDeleteListener {
        void onChangeFavoriteState(boolean isFavorite);
    }

}
