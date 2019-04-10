package com.semisky.multimedia.media_music.view;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.*;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.semisky.autoservice.manager.AutoConstants;
import com.semisky.multimedia.R;
import com.semisky.multimedia.common.base_view.MarqueeTextView;
import com.semisky.multimedia.common.base_view.ToastCustom;
import com.semisky.multimedia.common.base_view.ToastCustomHint;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.PlayMode;

/**
 * Created by Anter on 2018/8/2.
 */

public class MusicPlayerActivity extends TemplateMusicPlayerActivity implements OnClickListener, OnLongClickListener, OnTouchListener {
    private static final String TAG = Logutil.makeTagLog(MusicPlayerActivity.class);
    private TextView tv_position,
            /*tv_songName,*/
            tv_artistName,
            tv_albumName,
            tv_curTime,
            tv_totalTime;
    private MarqueeTextView tv_songName;
    private ImageView iv_favorite, iv_list, iv_prev, iv_switch, iv_next, iv_play_mode;
    private SeekBar sb_music;


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    protected int getLayoutResID() {
        Logutil.i(TAG, "getLayoutResID() ...");
        return R.layout.activity_music_player;
    }


    @Override
    public void onHandlerIntent(Intent intent) {
        if (isBindPresenter()) {
            mPresenter.onHandlerIntent(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logutil.i(TAG, "Activity onResume() ...");
        if (isBindPresenter()) {
            mPresenter.onResumeRequestAudio();
            String title = getString(R.string.status_bar_music_title_text);
            mPresenter.setTitleToStatusBar(this.getClass().getName(), title, AutoConstants.AppStatus.RUN_FOREGROUND);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logutil.i(TAG, "Activity onPause() ...");
        //activity pause时，app状态设置为后台状态
        if (isBindPresenter()) {
            String title = getString(R.string.status_bar_music_title_text);
            mPresenter.setTitleToStatusBar(this.getClass().getName(), title, AutoConstants.AppStatus.RUN_BACKGROUND);
        }
    }

    @Override
    protected void initViews() {
        Logutil.i(TAG, "initViews() ...");
        tv_position = (TextView) findViewById(R.id.tv_position);
        tv_songName = (MarqueeTextView) findViewById(R.id.tv_songName);
        tv_artistName = (TextView) findViewById(R.id.tv_artistName);
        tv_albumName = (TextView) findViewById(R.id.tv_albumName);
        iv_favorite = (ImageView) findViewById(R.id.iv_favorite);
        tv_curTime = (TextView) findViewById(R.id.tv_curTime);
        tv_totalTime = (TextView) findViewById(R.id.tv_totalTime);
        sb_music = (SeekBar) findViewById(R.id.sb_music);

        iv_list = (ImageView) findViewById(R.id.iv_list);
        iv_prev = (ImageView) findViewById(R.id.iv_prev);
        iv_switch = (ImageView) findViewById(R.id.iv_switch);
        iv_next = (ImageView) findViewById(R.id.iv_next);
        iv_play_mode = (ImageView) findViewById(R.id.iv_play_mode);
    }

    @Override
    protected void setListener() {
        Logutil.i(TAG, "setListener() ...");
        sb_music.setOnSeekBarChangeListener(mPresenter.getOnSeekBarChangeListener());
        iv_list.setOnClickListener(this);
        iv_prev.setOnClickListener(this);
        iv_prev.setOnLongClickListener(this);
        iv_prev.setOnTouchListener(this);
        iv_switch.setOnClickListener(this);
        iv_next.setOnClickListener(this);
        iv_next.setOnLongClickListener(this);
        iv_next.setOnTouchListener(this);
        iv_play_mode.setOnClickListener(this);
        iv_favorite.setOnClickListener(this);
        tv_songName.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_list:
                if (isBindPresenter()) {
                    mPresenter.onEnterList();
                }
                break;
            case R.id.iv_prev:
                Logutil.i(TAG, "onClick() PREV ...");
                if (isBindPresenter()) {
                    mPresenter.onPrevProgram();
                }
                break;
            case R.id.iv_switch:
                if (isBindPresenter()) {
                    mPresenter.onSwitchPlayOrPause();
                }
                break;
            case R.id.iv_next:
                Logutil.i(TAG, "onClick() NEXT ...");
                if (isBindPresenter()) {
                    mPresenter.onNextProgram();
                }
                break;
            case R.id.iv_play_mode:
                if (isBindPresenter()) {
                    mPresenter.onSwitchPlayMode();
                }
                break;
            case R.id.iv_favorite:
                Logutil.i(TAG, "CLICK iv_favorite ...");
                if (isBindPresenter()) {
                    mPresenter.onSwitchFavorite();
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!isBindPresenter()) {
            Logutil.w(TAG, "onLongClick() ...");
            return true;
        }
        switch (v.getId()) {
            case R.id.iv_prev:
                Logutil.i(TAG, "onLongClick() PREV ...");

                mPresenter.setStopTouchEventEnableWhenPrevProgram(false);
                mPresenter.onFastBackwardProgram();// 节目快退
                break;
            case R.id.iv_next:
                Logutil.i(TAG, "onLongClick() NEXT ...");
                mPresenter.setStopTouchEventEnableWhenNextProgram(false);
                mPresenter.onFastForwardProgram();// 节目快进
                break;
        }
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (v.getId()) {
            case R.id.iv_prev:
                if (mPresenter.getStopTouchEventEnableWhenPrevProgram()) {

                } else if (MotionEvent.ACTION_DOWN == action) {
                    Logutil.i(TAG, "onTouch() ACTION_DOWN PREV ...");
                } else if (MotionEvent.ACTION_UP == action) {
                    Logutil.i(TAG, "onTouch() ACTION_UP PREV ...");
                    mPresenter.setStopTouchEventEnableWhenPrevProgram(true);
                    mPresenter.onStopFastBackwardProgram();
                }
                break;
            case R.id.iv_next:
                if (mPresenter.getStopTouchEventEnableWhenNextProgram()) {

                } else if (MotionEvent.ACTION_DOWN == action) {
                    Logutil.i(TAG, "onTouch() ACTION_DOWN NEXT ...");
                } else if (MotionEvent.ACTION_UP == action) {
                    Logutil.i(TAG, "onTouch() ACTION_UP NEXT ...");
                    mPresenter.setStopTouchEventEnableWhenNextProgram(true);
                    mPresenter.onStopFastForwardProgram();
                }
                break;
        }
        return false;
    }

    @Override
    public void onShowProgramPosition(String position) {
        super.onShowProgramPosition(position);
        tv_position.setText(position);
    }

    // IMusicPlayerView

    @Override
    public void onShowProgramName(String programName) {
        super.onShowProgramName(programName);
        programName = programName != null ? programName : getString(R.string.tv_songName_text);
        tv_songName.setText(programName);
    }

    @Override
    public void onShowProgramArtistName(String artistName) {
        super.onShowProgramArtistName(artistName);
        artistName = artistName != null ? artistName : getString(R.string.tv_artistName_text);
        tv_artistName.setText(artistName);
    }

    @Override
    public void onShowProgramAlbumName(String albumName) {
        super.onShowProgramAlbumName(albumName);
        if (albumName == null || albumName.length() <= 0){
            tv_albumName.setText(R.string.tv_albumName_text);
        }else {
            tv_albumName.setText(albumName);
        }
//        albumName = albumName != null ? albumName : getString(R.string.tv_albumName_text);

    }

    @Override
    public void onSwitchFavorateView(boolean enable) {
        super.onSwitchFavorateView(enable);
        int resId = enable ? R.drawable.music_favorite_icon_pressed : R.drawable.selector_music_favorite_ic;
        iv_favorite.setImageResource(resId);
    }

    @Override
    public void onShowProgramCurrentTime(String curTime) {
        super.onShowProgramCurrentTime(curTime);
        tv_curTime.setText(curTime);
    }

    @Override
    public void onShowProgramTotalTime(String totalTime) {
        super.onShowProgramTotalTime(totalTime);
        tv_totalTime.setText(totalTime);
    }

    @Override
    public void onUpdateDuration(int duration) {
        super.onUpdateDuration(duration);
        sb_music.setMax(duration);
    }

    @Override
    public void onShowProgramProgress(int progress) {
        super.onShowProgramProgress(progress);
        sb_music.setProgress(progress);
    }

    @Override
    public void onChangePlayState(boolean isPlay) {
        int resId = isPlay ? R.drawable.selector_btn_pause : R.drawable.selector_btn_play;
        iv_switch.setImageResource(resId);
    }

    @Override
    public void onChangePlayMode(int playMode) {
        int resId = R.drawable.selector_btn_playmode_cycle;
        switch (playMode) {
            case PlayMode.LOOP:
                resId = R.drawable.selector_btn_playmode_cycle;
                break;
            case PlayMode.SHUFFLE:
                resId = R.drawable.selector_btn_playmode_random;
                break;
            case PlayMode.SINGLE:
                resId = R.drawable.selector_btn_playmode_single;
                break;
        }
        iv_play_mode.setImageResource(resId);
    }

    @Override
    public void onSwitchPlayProgramExceptionWarningView(boolean enable) {
        if (enable) {
            int resText = R.string.tv_alert_media_play_error_text;
            ToastCustomHint.showSingleMsg(this, resText, ToastCustom.DELAY_TIME_5S);
        }
    }
}
