package com.semisky.multimedia.media_music.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.common.base_view.BaseFragment;
import com.semisky.multimedia.common.interfaces.OnItemHighLightChangeCallback;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_music.adpter.MusicListAdapter;
import com.semisky.multimedia.media_music.presenter.MusicListPresenter;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class MusicListFragment extends BaseFragment<IMusicListView<MusicInfo>,MusicListPresenter<IMusicListView<MusicInfo>>> implements
        IMusicListView<MusicInfo>,
        OnItemClickListener,
        OnItemHighLightChangeCallback{
    private static final String TAG = Logutil.makeTagLog(MusicListFragment.class);
    private MusicListAdapter mMusicListAdapter;
    private ListView lv_music;
    private TextView tv_alert_list_empty;


    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_music_list;
    }

    @Override
    protected void initViews() {
        mMusicListAdapter = new MusicListAdapter(getActivity());
        tv_alert_list_empty = (TextView)mContentView.findViewById(R.id.tv_alert_list_empty);
        lv_music = (ListView)mContentView.findViewById(R.id.lv_music);
    }

    @Override
    protected void setListener() {
        mPresenter.setTitleToUI(this.getClass().getName(),getString(R.string.status_bar_music_list_title_text));
        this.lv_music.setAdapter(mMusicListAdapter);
        mMusicListAdapter.addCallback(this);
        this.lv_music.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(isBindPresenter()){
            String url = ((MusicInfo)mMusicListAdapter.getItem(position)).getUrl();
            mPresenter.onListPlay(url);
        }
    }

    @Override
    protected void onLoadData() {
        if(isBindPresenter()){
            mPresenter.onLoadData();
        }
    }

    @Override
    protected MusicListPresenter<IMusicListView<MusicInfo>> createPresenter() {
        return new MusicListPresenter();
    }

    @Override
    public void onFirstRefreshData(List<MusicInfo> dataList) {
        if(isBindPresenter()){
            this.mMusicListAdapter.updateList(dataList);
            mMusicListAdapter.onItemHighLightChange();
        }
    }

    @Override
    public void onRefreshData(List<MusicInfo> dataList) {
        if(isBindPresenter()){
            this.mMusicListAdapter.updateList(dataList);
//            mMusicListAdapter.onItemHighLightChange(); // bug 7398 . 如果数据正在更新中，滑动列表时将会自动显示到高亮的位置
        }
    }

    @Override
    public void onAlertEmptyListTextVisible(boolean enable) {
        int isVisible = enable ? View.VISIBLE:View.GONE;
        tv_alert_list_empty.setVisibility(isVisible);
    }

    @Override
    public void onItemHighLightChange() {
        int item = mMusicListAdapter.getmPositionWithCurrentPlayItem();
        lv_music.setSelection(item);

    }

    @Override
    public void onDestroyView() {
        if(null !=  mMusicListAdapter){
            mMusicListAdapter.onRelease();
        }
        super.onDestroyView();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            mPresenter.setTitleToUI(this.getClass().getName(),getString(R.string.status_bar_music_list_title_text));
        }
    }
}
