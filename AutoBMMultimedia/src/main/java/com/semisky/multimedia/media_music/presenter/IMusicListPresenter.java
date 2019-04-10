package com.semisky.multimedia.media_music.presenter;

/**
 * Created by LiuYong on 2018/8/9.
 */

public interface IMusicListPresenter {

    void onLoadData();
    /**
     * 列表播放
     *
     * @param url
     */
    void onListPlay(String url);
    /**
     * 设置title
     */
    void setTitleToUI(String name,String title);


}
