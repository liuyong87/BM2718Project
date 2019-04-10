package com.semisky.multimedia.media_photo.view;

import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.R;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.common.base_view.BaseFragment;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_photo.adapter.PhotoListAdapter;
import com.semisky.multimedia.media_photo.presenter.PhotoListPresenter;

import java.util.List;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class PhotoListFragment extends BaseFragment<IPhotoListView<PhotoInfo>, PhotoListPresenter<IPhotoListView<PhotoInfo>>> implements IPhotoListView<PhotoInfo>, AdapterView.OnItemClickListener {
    private static final String TAG = Logutil.makeTagLog(PhotoListFragment.class);
    private PhotoListAdapter mPhotoListAdapter;
    private GridView grid_view;
    private TextView tv_alert_list_empty;

    @Override
    protected PhotoListPresenter<IPhotoListView<PhotoInfo>> createPresenter() {
        return new PhotoListPresenter();
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.fragment_photo_list;
    }

    @Override
    protected void initViews() {
        mPhotoListAdapter = new PhotoListAdapter(getActivity());
        grid_view = (GridView)mContentView.findViewById(R.id.grid_view);
        tv_alert_list_empty = (TextView)mContentView.findViewById(R.id.tv_alert_list_empty);
    }

    @Override
    protected void setListener() {
        mPresenter.setTitleToUI(this.getClass().getName(),getString(R.string.status_bar_photo_list_title_text));

        grid_view.setAdapter(mPhotoListAdapter);
        grid_view.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isBindPresenter()) {
            Logutil.i(TAG, "onItemClick() position=" + position);
            String url = ((PhotoInfo) mPhotoListAdapter.getItem(position)).getFileUrl();
            Logutil.i(TAG, "onItemClick() url=" + url);
            mPresenter.onListPlay(url);
        }
    }

    @Override
    protected void onLoadData() {
        if (isBindPresenter()) {
            mPresenter.onLoadData();
        }
    }

    @Override
    public void onRefreshData(List<PhotoInfo> dataList) {
        if (isBindPresenter()) {
            mPhotoListAdapter.updateList(dataList);
        }
    }

    @Override
    public void onAlertEmptyListTextVisible(boolean enable) {
        int isVisible = enable ? View.VISIBLE : View.GONE;
        tv_alert_list_empty.setVisibility(isVisible);
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            mPresenter.setTitleToUI(this.getClass().getName(),getString(R.string.status_bar_photo_list_title_text));
        }
    }
}
