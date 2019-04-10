package com.semisky.multimedia.media_music.view;

import com.semisky.multimedia.aidl.music.MusicInfo;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public interface IMusicListView<E> {
    /**
     * 首次刷新数据
     *
     * @param dataList
     */
    void onFirstRefreshData(List<E> dataList);

    /**
     * 动态刷新数据
     *
     * @param dataList
     */
    void onRefreshData(List<E> dataList);

    /**
     * 当集合空数据时警示提示
     *
     * @param enable
     */
    void onAlertEmptyListTextVisible(boolean enable);
}
