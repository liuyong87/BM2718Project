package com.semisky.multimedia.media_music.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.semisky.multimedia.aidl.music.MusicInfo;

import java.util.List;

/**
 * Created by Anter on 2018/7/30.
 */

public interface IMusicPlayerView {
    Context getContext();

    /**
     * 处理界面意图
     *
     * @param intent
     */
    void onHandlerIntent(Intent intent);

    /**
     * 显示节目名字
     *
     * @param programName
     */
    void onShowProgramName(String programName);

    /**
     * 显示歌手名字
     *
     * @param artistName
     */
    void onShowProgramArtistName(String artistName);

    /**
     * 显示专辑名字
     *
     * @param albumName
     */
    void onShowProgramAlbumName(String albumName);

    /**
     * 预显示下一曲歌名
     *
     * @param programNextName
     */
    void onShowProgramNextName(String programNextName);

    /**
     * 显示专辑图片
     *
     * @param bmp
     */
    void onShowProgramAlbumThumbnail(Bitmap bmp);

    /**
     * 显示当前歌曲播放列表位置数据显示(如:8/15 表示:当前歌曲位置/总歌曲)
     *
     * @param position
     */
    void onShowProgramPosition(String position);

    /**
     * 显示当前播放时间进度
     *
     * @param curTime
     */
    void onShowProgramCurrentTime(String curTime);

    /**
     * 显示总播放时间进度
     *
     * @param totalTime
     */
    void onShowProgramTotalTime(String totalTime);

    /**
     * 更新播放进度
     *
     * @param progress
     */
    void onShowProgramProgress(int progress);

    /**
     * 媒体播放异常时的警示信息视图
     *
     * @param enable true:显示视图 ,false:隐藏视图
     */
    void onSwitchPlayProgramExceptionWarningView(boolean enable);

    /**
     * 收藏图片显示状态设置
     */
    void onSwitchFavorateView(boolean enable);

    /**
     * 更新进度条总进度
     *
     * @param duration
     */
    void onUpdateDuration(int duration);

    /**
     * 播放状态改变
     *
     * @param isPlay
     */
    void onChangePlayState(boolean isPlay);

    /**
     * 播放模式改变
     *
     * @param playMode 播放模式
     */
    void onChangePlayMode(int playMode);
}
