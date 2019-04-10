package com.semisky.multimedia.common.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.PlayMode;


/**
 * 偏好管理
 *
 * @author liuyong
 */
public class PreferencesManager {
    private static final String TAG = Logutil.makeTagLog(PreferencesManager.class);
    private static final String SHARE_NAME = "MultimediaPreferences";

    private static SharedPreferences getSP() {
        if (null != MediaApplication.getContext()) {
            return MediaApplication.getContext().getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
        }
        return null;
    }

    // 保存首个挂载U盘标识
    public static void saveFirstMountedUsbFlag(int usbFlag) {
        if (null != getSP()) {
            getSP().edit().putInt("firstMountedUsbFlag", usbFlag).commit();
        }
    }

    // 获取首个挂载U盘标识
    public static int getFirstMountedUsbFlag() {
        if (null != getSP()) {
            return getSP().getInt("firstMountedUsbFlag", -1);
        }
        return -1;
    }


    // 保存应用界面标识
    public static void saveLastAppFlag(int appFlag) {
        if (null != getSP()) {
            getSP().edit().putInt("appFlag", appFlag).commit();
        }
    }

    // 获取应用界面标识
    public static int getLastAppFlag() {
        if (null != getSP()) {
            return getSP().getInt("appFlag", -1);
        }
        return -1;
    }

    // 保存音乐URL
    public static void saveLastMusicUrl(String url) {
        if (null != getSP()) {
            getSP().edit().putString("musicUrl", url).commit();
        }
    }

    // 获取音乐URL
    public static String getLastMusicUrl() {
        if (null != getSP()) {
            return getSP().getString("musicUrl", "");
        }
        return "";
    }

    // 保存音乐播放进度
    public static void saveLastMusicProgress(int progress) {
        if (null != getSP()) {
            getSP().edit().putInt("musicProgress", progress).commit();
        }
    }

    // 获取音乐播放进度
    public static int getLastMusicProgress() {
        if (null != getSP()) {
            return getSP().getInt("musicProgress", 0);
        }
        return 0;
    }

    // 保存音乐播放模式
    public static void saveLastPlayMode(int playMode) {
        if (null != getSP()) {
            getSP().edit().putInt("playMode", playMode).commit();
        }
    }

    // 获取音乐播放模式
    public static int getLastPlayMode() {
        if (null != getSP()) {
            return getSP().getInt("playMode", PlayMode.LOOP);
        }
        return PlayMode.LOOP;
    }

    // 保存扫描到的首个音乐URL
    public static void saveScanFirstMusicUrl(int usbFlag, String url) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstMusicUrlOfUsb1", url).commit();
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstMusicUrlOfUsb2", url).commit();
                }
                break;
        }
    }

    // 获取扫描到的首个音乐URL
    public static String getScanFirstMusicUrl(int usbFlag) {

        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    return getSP().getString("scanFirstMusicUrlOfUsb1", "");
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    return getSP().getString("scanFirstMusicUrlOfUsb2", "");
                }
                break;
        }
        return "";
    }

    // VIDEO

    // 保存视频URL
    public static void saveLastVideoUrl(String url) {
        if (null != getSP()) {
            Logutil.i(TAG, "saveLastVideoUrl() ..." + url);
            getSP().edit().putString("videoUrl", url).commit();
        }
    }

    // 获取视频URL
    public static String getLastVideoUrl() {
        if (null != getSP()) {
            return getSP().getString("videoUrl", "");
        }
        return "";
    }

    // 保存视频播放进度
    public static void saveLastVideoProgress(int progress) {
        if (null != getSP()) {
            getSP().edit().putInt("videoProgress", progress).commit();
        }
    }

    // 获取视频播放进度
    public static int getLastVideoProgress() {
        if (null != getSP()) {
            return getSP().getInt("videoProgress", 0);
        }
        return 0;
    }

    // 保存扫描到的首个视频URL
    public static void saveScanFirstVideoUrl(int usbFlag, String url) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstVideoUrlOfUsb1", url).commit();
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstVideoUrlOfUsb2", url).commit();
                }
                break;
        }
    }

    // 保存扫描到的首个视频URL
    public static String getScanFirstVideoUrl(int usbFlag) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    return getSP().getString("scanFirstVideoUrlOfUsb1", "");
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    return getSP().getString("scanFirstVideoUrlOfUsb2", "");
                }
                break;
        }
        return "";
    }

    // 保存图片URL
    public static void saveLastPhotoUrl(String url) {
        if (null != getSP()) {
            getSP().edit().putString("photoUrl", url).commit();
        }
    }

    public static String getLastPhotoUrl() {
        if (null != getSP()) {
            return getSP().getString("photoUrl", "");
        }
        return "";
    }

    // 保存扫描到的首个图片URL
    public static void saveScanFirstPhotoUrl(int usbFlag, String url) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstPhotoUrlOfUsb1", url).commit();
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    getSP().edit().putString("scanFirstPhotoUrlOfUsb2", url).commit();
                }
                break;
        }
    }

    // 保存扫描到的首个图片URL
    public static String getScanFirstPhotoUrl(int usbFlag) {
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                if (null != getSP()) {
                    return getSP().getString("scanFirstPhotoUrlOfUsb1", null);
                }
                break;
            case Definition.FLAG_USB2:
                if (null != getSP()) {
                    return getSP().getString("scanFirstPhotoUrlOfUsb2", null);
                }
                break;
        }
        return null;
    }

}
