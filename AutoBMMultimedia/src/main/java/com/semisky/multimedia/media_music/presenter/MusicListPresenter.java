package com.semisky.multimedia.media_music.presenter;

import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.common.base_presenter.BasePresenter;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.AppActivityManager;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_music.model.IMusicDataModel;
import com.semisky.multimedia.media_music.model.IMusicDataModel.OnLoadDataListener;
import com.semisky.multimedia.media_music.model.MusicDataModel;
import com.semisky.multimedia.media_music.view.IMusicListView;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class MusicListPresenter<V extends IMusicListView<MusicInfo>> extends BasePresenter<V> implements IMusicListPresenter {
    private static final String TAG = Logutil.makeTagLog(MusicListPresenter.class);
    private IMusicDataModel mMusicDataModel;


    public MusicListPresenter() {
        this.mMusicDataModel = new MusicDataModel();
        mMusicDataModel.registerOnRefreshDataListener(mOnRefreshDataListener);

    }

    @Override
    public void onLoadData() {
        mMusicDataModel.onLoadMusicInfoList(new OnLoadDataListener<MusicInfo>() {
            @Override
            public void onLoadData(List<MusicInfo> dataList) {
                refreshFirstData(dataList);
            }
        });
    }

    private IMusicDataModel.OnRefreshDataListener mOnRefreshDataListener = new IMusicDataModel.OnRefreshDataListener() {
        @Override
        public void onUpdateData(final List<MusicInfo> dataList) {
            refreshData(dataList);
        }
    };

    // 刷新媒体数据列表
    private void refreshData(final List<MusicInfo> dataList) {
        if (isBindView()) {
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mViewRef == null || mViewRef.get() ==null){
                        //bug 极限操作 可能造成mViewRef 为空
                        return;
                    }
                    if (null != dataList && dataList.size() > 0) {
                        mViewRef.get().onRefreshData(dataList);
                        mViewRef.get().onAlertEmptyListTextVisible(false);
                        return;
                    }
                    mViewRef.get().onAlertEmptyListTextVisible(true);
                }
            });
        }

    }
    // 刷新首次媒体数据列表
    private void refreshFirstData(final List<MusicInfo> dataList) {
        if (isBindView()) {
            _handler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != dataList && dataList.size() > 0) {
                        mViewRef.get().onFirstRefreshData(dataList);
                        mViewRef.get().onAlertEmptyListTextVisible(false);
                        return;
                    }
                    mViewRef.get().onAlertEmptyListTextVisible(true);
                }
            });
        }

    }

    @Override
    public void onListPlay(String url) {
        if (isBindView()) {
            Logutil.i(TAG, "onListPlay() ..." + url);
            AppActivityManager
                    .getInstance()
                    .onStopColseAcitvityWith(Definition.AppFlag.TYPE_MUSIC)
                    .onCloseOrtherActivity();
            AppUtil.enterPlayerView(Definition.AppFlag.TYPE_MUSIC, url);
        }
    }

    @Override
    public void setTitleToUI(String name, String title) {
        SemiskyIVIManager.getInstance().setTitleName(name,title);
    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        mMusicDataModel.unregisterOnRefreshDataListener();
    }


}
