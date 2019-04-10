package com.semisky.multimedia.media_video.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.common.base_view.BaseFragment;
import com.semisky.multimedia.common.interfaces.OnItemHighLightChangeCallback;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_video.adapter.VideoListAdapter;
import com.semisky.multimedia.media_video.presenter.VideoListPresenter;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class VideoListFragment extends BaseFragment<IVideoListView<VideoInfo> ,VideoListPresenter<IVideoListView<VideoInfo>>> implements
        IVideoListView<VideoInfo>,
        OnItemClickListener,
        OnItemHighLightChangeCallback{
    private static final String TAG = Logutil.makeTagLog(VideoListFragment.class);
    private VideoListAdapter mVideoListAdapter;
    private ListView lv_video;
    private TextView tv_alert_list_empty;

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_video_list;
    }

    @Override
    protected void initViews() {
        mVideoListAdapter = new VideoListAdapter(getActivity());
        lv_video = (ListView) mContentView.findViewById(R.id.lv_video);
        tv_alert_list_empty = (TextView)mContentView.findViewById(R.id.tv_alert_list_empty);
    }

    @Override
    protected void setListener() {
        mPresenter.setTitleToUI(this.getClass().getName(),getString(R.string.status_bar_video_list_title_text));
        lv_video.setAdapter(mVideoListAdapter);
        lv_video.setOnItemClickListener(this);
        mVideoListAdapter.addCallback(this);
    }

    @Override
    protected void onLoadData() {
        if(isBindPresenter()){
            mPresenter.onLoadData();
        }
    }

    @Override
    protected VideoListPresenter<IVideoListView<VideoInfo>> createPresenter() {
        return new VideoListPresenter();
    }

    @Override
    public void onFirstRefreshData(List<VideoInfo> dataList) {
        if(isBindPresenter()){
            Logutil.i(TAG,"onFirstRefreshData() ...");
            mVideoListAdapter.updateList(dataList);
            mVideoListAdapter.notifyItemHighLightChange();
        }
    }

    @Override
    public void onRefreshData(List<VideoInfo> dataList) {
        if(isBindPresenter()){
            Logutil.i(TAG,"onRefreshData() ...");
            mVideoListAdapter.updateList(dataList);
            mVideoListAdapter.notifyItemHighLightChange();
        }
    }

    @Override
    public void onAlertEmptyListTextVisible(boolean enable) {
        int isVisible = enable ? View.VISIBLE:View.GONE;
        tv_alert_list_empty.setVisibility(isVisible);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = ((VideoInfo)mVideoListAdapter.getItem(position)).getFileUrl();
        if(isBindPresenter()){
            mPresenter.onPlayList(url);
        }
    }

    @Override
    public void onItemHighLightChange() {
        int item = mVideoListAdapter.getmPositionWithCurrentPlayItem();
        lv_video.setSelection(item);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            mPresenter.setTitleToUI(this.getClass().getName(),getString(R.string.status_bar_video_list_title_text));
        }
    }

    @Override
    public void onDestroyView() {
        mVideoListAdapter.onRelease();
        super.onDestroyView();
    }
}
