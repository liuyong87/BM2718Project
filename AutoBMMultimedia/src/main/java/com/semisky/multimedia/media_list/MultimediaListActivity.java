package com.semisky.multimedia.media_list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.R;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.MediaListConst;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_folder.view.FolderListFragment;
import com.semisky.multimedia.media_music.view.MusicFavoriteListFragment;
import com.semisky.multimedia.media_music.view.MusicListFragment;
import com.semisky.multimedia.media_photo.view.PhotoListFragment;
import com.semisky.multimedia.media_video.view.VideoListFragment;

/**
 * Created by LiuYong on 2018/8/9.
 */

public class MultimediaListActivity extends FragmentActivity implements OnClickListener {
    private static final String TAG = Logutil.makeTagLog(MultimediaListActivity.class);

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Fragment[] mFragments = new Fragment[5];// Fragments array
    private String[] mFragmentsTag = new String[5];// Fragments tag array
    private ImageView[] mFragmentListTabUI = new ImageView[5];
    private int mCurrentFragmentIndex = 0;
    // Fragments tag
    private static final String FRAGMENT_MUSIC_LIST_TAG = MusicListFragment.class.getSimpleName();
    private static final String FRAGMENT_VIDEO_LIST_TAG = VideoListFragment.class.getSimpleName();
    private static final String FRAGMENT_PHOTO_LIST_TAG = PhotoListFragment.class.getSimpleName();
    private static final String FRAGMENT_FAVORITE_LIST_TAG = MusicFavoriteListFragment.class.getSimpleName();
    private static final String FRAGMENT_FOLDER_LIST_TAG = FolderListFragment.class.getSimpleName();
    // Fragments index in the array
    private static final int FRAGMENT_MUSIC_LIST_INDEX = MediaListConst.FRAGMENT_LIST_MUISC;
    private static final int FRAGMENT_VIDEO_LIST_INDEX = MediaListConst.FRAGMENT_LIST_VIDEO;
    private static final int FRAGMENT_PHOTO_LIST_INDEX = MediaListConst.FRAGMENT_LIST_PHOTO;
    private static final int FRAGMENT_FAVORITE_LIST_INDEX = MediaListConst.FRAGMENT_LIST_FAVORITE;
    private static final int FRAGMENT_FOLDER_LIST_INDEX = MediaListConst.FRAGMENT_LIST_FOLDER;

    private ImageView
            iv_music_list_tab,
            iv_video_list_tab,
            iv_photo_list_tab,
            iv_favorite_list_tab,
            iv_folder_list_tab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multimedia_list);
        Log.i(TAG, "onCreate() ...");
        initViews();
        setListener();
        handlerIntent();
        registerReceiver(home, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //title显示需添加媒体类型的标志 图片列表，视频列表，音乐列表
//        String title = getString(R.string.status_bar_list_title_text);
//        SemiskyIVIManager.getInstance().setAppStatus(this.getClass().getName(),title, AutoConstants.AppStatus.RUN_FOREGROUND);
    }

    private void initViews() {
        iv_music_list_tab = (ImageView)findViewById(R.id.iv_music_list_tab);
        iv_video_list_tab = (ImageView)findViewById(R.id.iv_video_list_tab);
        iv_photo_list_tab = (ImageView)findViewById(R.id.iv_photo_list_tab);
        iv_favorite_list_tab = (ImageView)findViewById(R.id.iv_favorite_list_tab);
        iv_folder_list_tab = (ImageView)findViewById(R.id.iv_folder_list_tab);

    }

    private void setListener() {
        iv_music_list_tab.setOnClickListener(this);
        iv_video_list_tab.setOnClickListener(this);
        iv_photo_list_tab.setOnClickListener(this);
        iv_favorite_list_tab.setOnClickListener(this);
        iv_folder_list_tab.setOnClickListener(this);

        mFragmentListTabUI[FRAGMENT_MUSIC_LIST_INDEX] = iv_music_list_tab;
        mFragmentListTabUI[FRAGMENT_VIDEO_LIST_INDEX] = iv_video_list_tab;
        mFragmentListTabUI[FRAGMENT_PHOTO_LIST_INDEX] = iv_photo_list_tab;
        mFragmentListTabUI[FRAGMENT_FAVORITE_LIST_INDEX] = iv_favorite_list_tab;
        mFragmentListTabUI[FRAGMENT_FOLDER_LIST_INDEX] = iv_folder_list_tab;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_music_list_tab:
                switchFragment(FRAGMENT_MUSIC_LIST_INDEX);
                break;
            case R.id.iv_video_list_tab:
                switchFragment(FRAGMENT_VIDEO_LIST_INDEX);
                break;
            case R.id.iv_photo_list_tab:
                switchFragment(FRAGMENT_PHOTO_LIST_INDEX);
                break;
            case R.id.iv_favorite_list_tab:
                switchFragment(FRAGMENT_FAVORITE_LIST_INDEX);
                break;
            case R.id.iv_folder_list_tab:
                switchFragment(FRAGMENT_FOLDER_LIST_INDEX);
                break;
        }
    }

    /**
     * 处理意图
     */
    private void handlerIntent() {
        Intent intent = super.getIntent();
        int fragmentFlag = intent.getIntExtra(MediaListConst.FRAGMENT_FLAG, MediaListConst.FRAGMENT_LIST_MUISC);
        initShowFragment(fragmentFlag);
    }

    /**
     * 初始显示页面列表
     *
     * @param fragmentFlag
     */
    private void initShowFragment(int fragmentFlag) {
        this.mCurrentFragmentIndex = fragmentFlag;
        this.mFragmentManager = getSupportFragmentManager();
        this.mFragmentTransaction = mFragmentManager.beginTransaction();
        // Fragment tag add to array
        mFragmentsTag[FRAGMENT_MUSIC_LIST_INDEX] = FRAGMENT_MUSIC_LIST_TAG;
        mFragmentsTag[FRAGMENT_VIDEO_LIST_INDEX] = FRAGMENT_VIDEO_LIST_TAG;
        mFragmentsTag[FRAGMENT_PHOTO_LIST_INDEX] = FRAGMENT_PHOTO_LIST_TAG;
        mFragmentsTag[FRAGMENT_FAVORITE_LIST_INDEX] = FRAGMENT_FAVORITE_LIST_TAG;
        mFragmentsTag[FRAGMENT_FOLDER_LIST_INDEX] = FRAGMENT_FOLDER_LIST_TAG;
        // Fragment instance add to array
        mFragments[FRAGMENT_MUSIC_LIST_INDEX] = mFragmentManager.findFragmentByTag(FRAGMENT_MUSIC_LIST_TAG);
        mFragments[FRAGMENT_VIDEO_LIST_INDEX] = mFragmentManager.findFragmentByTag(FRAGMENT_VIDEO_LIST_TAG);
        mFragments[FRAGMENT_PHOTO_LIST_INDEX] = mFragmentManager.findFragmentByTag(FRAGMENT_PHOTO_LIST_TAG);
        mFragments[FRAGMENT_FAVORITE_LIST_INDEX] = mFragmentManager.findFragmentByTag(FRAGMENT_FAVORITE_LIST_TAG);
        mFragments[FRAGMENT_FOLDER_LIST_INDEX] = mFragmentManager.findFragmentByTag(FRAGMENT_FOLDER_LIST_TAG);
        // Fragment instance is equal to empty creation instanc
        if (null == mFragments[FRAGMENT_MUSIC_LIST_INDEX]) {
            this.mFragments[FRAGMENT_MUSIC_LIST_INDEX] = new MusicListFragment();
        }
        if (null == mFragments[FRAGMENT_VIDEO_LIST_INDEX]) {
            this.mFragments[FRAGMENT_VIDEO_LIST_INDEX] = new VideoListFragment();
        }
        if (null == mFragments[FRAGMENT_PHOTO_LIST_INDEX]) {
            this.mFragments[FRAGMENT_PHOTO_LIST_INDEX] = new PhotoListFragment();
        }
        if (null == mFragments[FRAGMENT_FAVORITE_LIST_INDEX]) {
            this.mFragments[FRAGMENT_FAVORITE_LIST_INDEX] = new MusicFavoriteListFragment();
        }
        if (null == mFragments[FRAGMENT_FOLDER_LIST_INDEX]) {
            mFragments[FRAGMENT_FOLDER_LIST_INDEX] = new FolderListFragment();
        }

        if (!this.mFragments[fragmentFlag].isAdded()) {
            mFragmentTransaction.add(R.id.fragment_container,mFragments[fragmentFlag], mFragmentsTag[fragmentFlag]);
            mFragmentTransaction.commitAllowingStateLoss();
            mFragmentManager.executePendingTransactions();
            setTitleName(fragmentFlag);
        }
        this.mFragmentListTabUI[fragmentFlag].setSelected(true);
    }

    /**
     * 用户切换列表
     *
     * @param position
     */
    private void switchFragment(int position) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        if(position == mCurrentFragmentIndex){
            return;
        }

        if (!mFragments[position].isAdded() && null == mFragmentManager.findFragmentByTag(mFragmentsTag[position])) {
            mFragmentTransaction.add(R.id.fragment_container, mFragments[position], mFragmentsTag[position]);
        }
        mFragmentTransaction.hide(mFragments[mCurrentFragmentIndex]).show(mFragments[position]);
        mFragmentTransaction.commitAllowingStateLoss();
        mFragmentManager.executePendingTransactions();
        this.mFragmentListTabUI[mCurrentFragmentIndex].setSelected(false);
        this.mFragmentListTabUI[position].setSelected(true);
        this.mCurrentFragmentIndex = position;

    }
    /**
     * 初始化用户列表时显示title类型列表
     */
     private void setTitleName(int flag){
         if (flag == Definition.AppFlag.TYPE_MUSIC){
             String title = getString(R.string.status_bar_music_list_title_text);
             SemiskyIVIManager.getInstance().setAppStatus(this.getClass().getName(),title, AutoConstants.AppStatus.RUN_FOREGROUND);
         }else if (flag == Definition.AppFlag.TYPE_VIDEO){
             String title = getString(R.string.status_bar_video_list_title_text);
             SemiskyIVIManager.getInstance().setAppStatus(this.getClass().getName(),title, AutoConstants.AppStatus.RUN_FOREGROUND);
         } else if (flag == Definition.AppFlag.TYPE_PHOTO) {
             String title = getString(R.string.status_bar_photo_list_title_text);
             SemiskyIVIManager.getInstance().setAppStatus(this.getClass().getName(),title, AutoConstants.AppStatus.RUN_FOREGROUND);
         }else {
             String title = getString(R.string.status_bar_list_title_text);
             SemiskyIVIManager.getInstance().setAppStatus(this.getClass().getName(),title, AutoConstants.AppStatus.RUN_FOREGROUND);
         }
     }
    BroadcastReceiver home = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
               finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(home);
    }
}
