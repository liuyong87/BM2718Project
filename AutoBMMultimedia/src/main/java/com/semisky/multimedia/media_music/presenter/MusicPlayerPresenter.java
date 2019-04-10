package com.semisky.multimedia.media_music.presenter;

import android.content.Intent;
import android.os.RemoteException;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.aidl.music.IProxyProgramChangeCallback;
import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.FormatTimeUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_list.MultimediaListManger;
import com.semisky.multimedia.media_music.model.ProxyMusicPlayerModel;
import com.semisky.multimedia.media_music.view.IMusicPlayerView;
import com.semisky.multimedia.media_music.view.MusicPlayerActivity;

/**
 * 媒体播放器表示层
 * Created by Anter on 2018/7/30.
 */

public class MusicPlayerPresenter<V extends IMusicPlayerView> extends BasePresenter<V> implements IMusicPlayerPresenter {
    private static final String TAG = Logutil.makeTagLog(MusicPlayerPresenter.class);
    private ProxyMusicPlayerModel mProxyMusicPlayerModel;
    private int mUserDragSeekBarProgress = 0;// 用户拖动进度条的进度记录
    private boolean mStopTouchEventWhenNextProgram = true;// 当下一个节目时，禁止下一个按键控件的触摸事件
    private boolean mStopTouchEventWhenPrevProgram = true;// 当上一个节目时，禁止上一个按键控件的触摸事件

    public MusicPlayerPresenter() {
        this.mProxyMusicPlayerModel = ProxyMusicPlayerModel.getInstance();
        this.mProxyMusicPlayerModel.registerOnServiceConnectCompletedListener(mConnectedServiceListener);
    }

    @Override
    public SeekBar.OnSeekBarChangeListener getOnSeekBarChangeListener() {
        return this.mOnSeekBarChangeListener;
    }

    @Override
    public void bindService() {
        if (isBindView()) {
            Logutil.i(TAG, "bindService() ...");
            mProxyMusicPlayerModel.bindService(mViewRef.get().getContext());
        }
    }

    @Override
    public void unbindService() {
        if (isBindView()) {
            Logutil.i(TAG, "unbindService() ...");
            mProxyMusicPlayerModel.unbindService(mViewRef.get().getContext());
        }
    }

    @Override
    public void onHandlerIntent(Intent intent) {
        if (null == intent) {
            return;
        }

        String url = intent.getStringExtra("url");
        if (null != url) {
            mProxyMusicPlayerModel.onListPlay(url);
            bindService();
        } else {
            mProxyMusicPlayerModel.startService(mViewRef.get().getContext());
            bindService();
        }
        Logutil.i(TAG, "onHandlerIntent() ..." + url);
    }

    @Override
    public void onFastForwardProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchFastForward();
        }
    }

    @Override
    public void onStopFastForwardProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchStopFastForward();
        }
    }

    @Override
    public void onFastBackwardProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchFastBackward();
        }
    }

    @Override
    public void onStopFastBackwardProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchStopFastBackward();
        }
    }

    @Override
    public void onSwitchPlayMode() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchPlayMode();
        }
    }

    @Override
    public void onSwitchPlayOrPause() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchPlayOrPause();
        }
    }

    @Override
    public void onPrevProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchPrev();
        }
    }

    @Override
    public void onNextProgram() {
        if (isBindView()) {
            mProxyMusicPlayerModel.onSwitchNext();
        }
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (isBindView()) {
                mUserDragSeekBarProgress = progress;
                if (fromUser) {
                    mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(progress));
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mProxyMusicPlayerModel.onUpdateProgressWithThreadEnabled(false);// 暂停更新进度线程
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (isBindView()) {
                // 用户抬起手停止拖动进度条时，将当前拖动的进度设置到播放器
                mProxyMusicPlayerModel.onSeekTo(mUserDragSeekBarProgress);// 设置播放进度到播放器
                mProxyMusicPlayerModel.onUpdateProgressWithThreadEnabled(true);// 启动更新进度线程
                mProxyMusicPlayerModel.onSwitchResumePlay();// 恢复播放操作
                mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(mUserDragSeekBarProgress));
                mUserDragSeekBarProgress = 0;
            }
        }
    };

    @Override
    public void onEnterList() {
        if (isBindView()) {
            AppUtil.enterList(Definition.MediaListConst.FRAGMENT_LIST_MUISC);
        }
    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        mProxyMusicPlayerModel.unregisterCallback(mProxyProgramChangeCallback);
    }

    private ProxyMusicPlayerModel.OnServiceConnectCompletedListener mConnectedServiceListener = new ProxyMusicPlayerModel.OnServiceConnectCompletedListener() {
        @Override
        public void onServiceConnectCompleted() {
            mProxyMusicPlayerModel.registerCallback(mProxyProgramChangeCallback);
            updateMediaInfo();
        }
    };

    private void updateMediaInfo() {
        Logutil.i(TAG, "updateMediaInfo() ...");
        _handler.post(new Runnable() {
            @Override
            public void run() {
                if (isBindView()) {
                    String numOfprogram = mProxyMusicPlayerModel.getCurProgramPos();// 当前节目播放序号
                    String songName = mProxyMusicPlayerModel.getSongName();// 歌曲名
                    String artistName = mProxyMusicPlayerModel.getArtistName();// 歌手名
                    String albumName = mProxyMusicPlayerModel.getAlbumName();// 专辑名
                    int totalProgress = mProxyMusicPlayerModel.getTotalPorgress();// 总进度
                    int curProgress = mProxyMusicPlayerModel.getCurrentProgress();// 当前进度
                    int playMode = mProxyMusicPlayerModel.getPlayMode();// 播放模式
                    boolean isPlaying = mProxyMusicPlayerModel.isPlaying();
                    boolean isFavorite = mProxyMusicPlayerModel.isFavorite();// 收藏状态

                    String sTotalTime = FormatTimeUtil.makeFormatTime(totalProgress);// 总进度格式化指定格式时间
                    String sCurTime = FormatTimeUtil.makeFormatTime(curProgress);// 当前进度格式化指定格式时间

                    Logutil.i(TAG, "================");
                    Logutil.i(TAG, "updateMediaInfo() numOfprogram=" + numOfprogram);
                    Logutil.i(TAG, "updateMediaInfo() songName=" + songName);
                    Logutil.i(TAG, "updateMediaInfo() artistName=" + artistName);
                    Logutil.i(TAG, "updateMediaInfo() albumName=" + albumName);
                    Logutil.i(TAG, "updateMediaInfo() playMode=" + playMode);
                    Logutil.i(TAG, "updateMediaInfo() isFavorite=" + isFavorite);
                    Logutil.i(TAG, "updateMediaInfo() totalProgress=" + sTotalTime);
                    Logutil.i(TAG, "updateMediaInfo() curProgress=" + sCurTime);
                    Logutil.i(TAG, "updateMediaInfo() isPlaying=" + isPlaying);
                    Logutil.i(TAG, "================");

                    mViewRef.get().onShowProgramPosition(numOfprogram);
                    mViewRef.get().onShowProgramName(songName);
                    mViewRef.get().onShowProgramArtistName(artistName);
                    mViewRef.get().onShowProgramAlbumName(albumName);
                    mViewRef.get().onChangePlayMode(playMode);

                    mViewRef.get().onUpdateDuration(totalProgress);
                    mViewRef.get().onShowProgramTotalTime(sTotalTime);
                    mViewRef.get().onShowProgramProgress(curProgress);
                    mViewRef.get().onShowProgramCurrentTime(sCurTime);

                    mViewRef.get().onChangePlayState(isPlaying);
                    mViewRef.get().onSwitchFavorateView(isFavorite);

                }
            }
        });
    }

    private IProxyProgramChangeCallback.Stub mProxyProgramChangeCallback = new IProxyProgramChangeCallback.Stub() {
        @Override
        public void onChangeSongName(final String songName) throws RemoteException {
            Logutil.i(TAG, "onChangeSongName() ..." + (null != songName ? songName : "unkown"));

            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().onShowProgramName(songName);
                    }
                }
            });

        }

        @Override
        public void onChangeArtistName(final String artistName) throws RemoteException {
            Logutil.i(TAG, "onChangeArtistName() ..." + (null != artistName ? artistName : "unkown"));
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        String newArtistName = null;
                        if (null != artistName) {
                            newArtistName = artistName.length() == 0 ? null : artistName;
                        }
                        mViewRef.get().onShowProgramArtistName(newArtistName);
                    }
                }
            });
        }

        @Override
        public void onChangeAlbumName(final String albumName) throws RemoteException {
            Logutil.i(TAG, "onChangeAlbumName() ..." + (null != albumName ? albumName : "unkown"));
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        String newAlbumName = null;

                        if (null != albumName) {
                            newAlbumName = albumName.length() == 0 ? null : albumName;
                        }
                        mViewRef.get().onShowProgramAlbumName(newAlbumName);
                    }
                }
            });
        }

        @Override
        public void onChangeTotalProgress(final int progress) throws RemoteException {
            Logutil.i(TAG, "onChangeTotalProgress() ..." + progress);
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().onUpdateDuration(progress);
                        mViewRef.get().onShowProgramTotalTime(FormatTimeUtil.makeFormatTime(progress));
                    }
                }
            });
        }
        int progress_ = -1;// progress 播放进度
        int lastProgress=-1;// 播放进度的前一次进度
        @Override
        public void onChangeCurrentProgress( int progress) throws RemoteException {
            //            Logutil.i(TAG, "onChangeCurrentProgress() ..." + progress);
            progress_ =progress;
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        //bug 快进完成的时候显示当前时间总会回退一秒再播放下一曲
                        //根据进度判断是否需要更新进度条，具体原因未能找到
                        //后者条件：当重新播放歌曲时，progress可能不为0,小于500毫秒的情况视为从头开始播放
                        if(progress_ > lastProgress || progress_ < 500){
                            lastProgress = progress_;
                            mViewRef.get().onShowProgramProgress(progress_);
                            mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(progress_));
                        }else if (lastProgress > progress_ + 500 || progress_ <500){
                            //快退
                            lastProgress = progress_;
                            mViewRef.get().onShowProgramProgress(progress_);
                            mViewRef.get().onShowProgramCurrentTime(FormatTimeUtil.makeFormatTime(progress_));
                        }
                    }
                }
            });
        }
        @Override
        public void onChangeCurProgramPos(final String programPos) throws RemoteException {
            Logutil.i(TAG, "onChangeCurProgramPos() ..." + programPos);
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().onShowProgramPosition(programPos);
                    }
                }
            });
        }

        @Override
        public void onChangePlayMode(final int playMode) throws RemoteException {
            Logutil.i(TAG, "onChangePlayMode() ..." + playMode);
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().onChangePlayMode(playMode);
                    }
                }
            });
        }

        @Override
        public void onChangePlayStatus(final boolean playStatus) throws RemoteException {
            Logutil.i(TAG, "onChangePlayStatus() ..." + playStatus);
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().onChangePlayState(playStatus);
                    }
                }
            });
        }

        @Override
        public void onChangeFavorite(final boolean isFavorite) throws RemoteException {
            Logutil.i(TAG, "onChangeFavorite() ..." + isFavorite);
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (isBindView()) {
                        mViewRef.get().onSwitchFavorateView(isFavorite);
                    }
                }
            });
        }

        @Override
        public void onMediaPrepareCompleted() throws RemoteException {
            if(isBindView()){
                mViewRef.get().onSwitchPlayProgramExceptionWarningView(false);
            }
        }

        @Override
        public void onPlayError(int what) throws RemoteException {
            if(isBindView()){
                mViewRef.get().onSwitchPlayProgramExceptionWarningView(true);
            }
        }

        @Override
        public void onChangeUrl(String url) throws RemoteException {
            if(Definition.DEBUG_ENG){
                Logutil.i(TAG,"onChangeUrl() ..."+url);
            }
        }


    };

    @Override
    public void onSwitchFavorite() {
        if (isBindView()) {
            Logutil.i(TAG, "onSwitchFavorite() ...");
            mProxyMusicPlayerModel.onSwitchFavorite();
        }
    }

    @Override
    public void setTitleToStatusBar(String clz, String title,int state) {
        SemiskyIVIManager.getInstance().setAppStatus(clz, title, state);
    }

    @Override
    public void setStopTouchEventEnableWhenNextProgram(boolean enable) {
        if (isBindView()) {
            this.mStopTouchEventWhenNextProgram = enable;
        }
    }

    @Override
    public boolean getStopTouchEventEnableWhenNextProgram() {
        if (isBindView()) {
            return this.mStopTouchEventWhenNextProgram;
        }
        return false;
    }

    @Override
    public void setStopTouchEventEnableWhenPrevProgram(boolean enable) {
        if (isBindView()) {
            this.mStopTouchEventWhenPrevProgram = enable;
        }
    }

    @Override
    public boolean getStopTouchEventEnableWhenPrevProgram() {
        if (isBindView()) {
            return this.mStopTouchEventWhenPrevProgram;
        }
        return false;
    }

    @Override
    public void onResumeRequestAudio() {
        if (isBindView()){
            mProxyMusicPlayerModel.notifyServiceRequestAudio();
        }
    }
}
