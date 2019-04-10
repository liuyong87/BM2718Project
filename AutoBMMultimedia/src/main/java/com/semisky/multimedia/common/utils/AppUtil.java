package com.semisky.multimedia.common.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.semisky.autoservice.manager.AutoManager;
import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.base_view.ToastCustom;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.constants.Definition.MediaListConst;
import com.semisky.multimedia.media_list.MultimediaListActivity;
import com.semisky.multimedia.media_music.view.MusicPlayerActivity;
import com.semisky.multimedia.media_photo.view.PhotoPlayerActivity;
import com.semisky.multimedia.media_usb.broadcast.USBReciver;
import com.semisky.multimedia.media_usb.mediascan.ConstantsMediaSuffix;
import com.semisky.multimedia.media_usb.mediascan.ConstantsMediaSuffix.MediaSuffixType;
import com.semisky.multimedia.media_video.view.VideoPlayerActivity;

import java.util.logging.Logger;

/**
 * Created by Anter on 2018/8/6.
 */

public class AppUtil {
    private static final String TAG = Logutil.makeTagLog(AppUtil.class);

    /**
     * 是否为忽略的文件后缀
     *
     * @param suffixType
     * @param url
     * @return
     */
    public static boolean isIgonreScanFileSuffix(MediaSuffixType suffixType, String url) {
        switch (suffixType) {
            case SUFFIX_TYPE_AUDIO:
                return false;
            case SUFFIX_TYPE_VIDEO:
                if (null == url) {
                    return false;
                }
                String suffixStr = url.substring(url.lastIndexOf(".")).toLowerCase();
                for (String suffix : ConstantsMediaSuffix.IGNORE_SUFFIX_ARRAY_VIDEO) {
                    if (suffix.equals(suffixStr)) {
                        return true;
                    }
                }
                return false;
            case SUFFIX_TYPE_PHOTO:
                return false;
        }
        return false;
    }

    /**
     * 恢复后台音乐播放
     *
     * @param ctx
     */
    public static void resumeBackgroundMusicPlay(Context ctx) {
        Logutil.i(TAG, "resumeBackgroundMusicPlay() ...");
        Intent intent = new Intent();
        intent.setClassName(Definition.MediaCtrlConst.SERVICE_PKG, Definition.MediaCtrlConst.SERVICE_CLZ);
        intent.setAction(Definition.MediaCtrlConst.ACTION_SERVICE_MUSIC_PLAY_CONTROL);
        intent.putExtra(Definition.MediaCtrlConst.PARAM_CMD, Definition.MediaCtrlConst.CMD_RESUME_PLAY);
        ctx.startService(intent);
    }

    /**
     * 从资源路径获取U盘标识
     *
     * @param url
     * @return
     */
    public static int getUsbFlagFrom(String url) {
        int usbFlag = -1;
        if (null != url) {
            if (url.startsWith(Definition.PATH_USB1)) {
                usbFlag = Definition.FLAG_USB1;
            } else if (url.startsWith(Definition.PATH_USB2)) {
                usbFlag = Definition.FLAG_USB2;
            }
        }
        Logutil.i(TAG, "getUsbFlagFrom() ..." + usbFlag);
        return usbFlag;
    }

    /**
     * 获取当前平台USB路径
     *
     * @param usbFlag usb 标识
     * @return
     */
    public static String getUsbPathWithCurrentPlatform(int usbFlag) {
        String usbPath = null;
        // 当前平台USB路径
        if (isCurrentPlatform()) {

            switch (usbFlag) {
                case Definition.FLAG_USB1:
                    usbPath = Definition.PATH_USB1_BM2718_PLATFORM;
                    break;
                case Definition.FLAG_USB2:
                    usbPath = Definition.PATH_USB2_BM2718_PLATFORM;
                    break;
            }
            Logutil.i(TAG, "getUsbPathWithCurrentPlatform() PATH_USB_BM2718_PLATFORM =" + usbPath);
            return usbPath;
        }
        // 其它平台USB路径
        else {

            switch (usbFlag) {
                case Definition.FLAG_USB1:
                    usbPath = Definition.PATH_USB1_OTHER_PLATFORM;
                    break;
                case Definition.FLAG_USB2:
                    usbPath = Definition.PATH_USB2_OTHER_PLATFORM;
                    break;
            }
            Logutil.i(TAG, "getUsbPathWithCurrentPlatform() PATH_USB_OTHER_PLATFORM =" + usbPath);
            return usbPath;
        }
    }

    /**
     * 是否为当前平台
     *
     * @return
     */
    public static boolean isCurrentPlatform() {
        String platform = SystemPropertiesUtils.get("ro.build.description");
        Logutil.d(TAG, "isCurrentPlatform()->platform: " + platform);
        if (null != platform && platform.contains(Definition.CURRENT_PLATFORM_KEYWORDS)) {
            return true;
        }
        return false;
    }

    /**
     * 进入指定播放器视图界面
     *
     * @param appFlag
     */
    public static void enterPlayerView(int appFlag, String url) {
        Intent intent = new Intent();
        switch (appFlag) {
            case Definition.AppFlag.TYPE_MUSIC:
                intent.setClass(MediaApplication.getContext(), MusicPlayerActivity.class);

                break;
            case Definition.AppFlag.TYPE_VIDEO:
                intent.setClass(MediaApplication.getContext(), VideoPlayerActivity.class);
                break;
            case Definition.AppFlag.TYPE_PHOTO:
                intent.setClass(MediaApplication.getContext(), PhotoPlayerActivity.class);
                break;
        }
        intent.putExtra("url", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MediaApplication.getContext().startActivity(intent);
    }

    /**
     * 进入指定播放器视图界面
     *
     * @param appFlag
     */
    public static void enterPlayerView(int appFlag) {
        if (!USBReciver.getIsAttached()) {
            //当扫描过程中拔出U盘，并且拔出U盘事件执行已经完成，启动APP代码已经执行，会再次启动多媒体
            return;
        }
        Intent intent = new Intent();
        switch (appFlag) {
            case Definition.AppFlag.TYPE_MUSIC:
                intent.setClass(MediaApplication.getContext(), MusicPlayerActivity.class);
                break;
            case Definition.AppFlag.TYPE_VIDEO:
                intent.setClass(MediaApplication.getContext(), VideoPlayerActivity.class);
                break;
            case Definition.AppFlag.TYPE_PHOTO:
                intent.setClass(MediaApplication.getContext(), PhotoPlayerActivity.class);
                break;
            case Definition.AppFlag.TYPE_LIST:
                intent.setClass(MediaApplication.getContext(), MultimediaListActivity.class);
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MediaApplication.getContext().startActivity(intent);
        ToastCustom.closeDialog();//进入Activity后，立即关闭显示的toast 提示信息
    }

    /**
     * 进入指定媒体列表界面
     *
     * @param fragmentFlag
     */
    public static void enterList(int fragmentFlag) {
        if (MediaApplication.getContext() == null) {
            Logutil.e(TAG, "enterList() ...context empty !!!");
            return;
        }
        Intent intent = new Intent();
        intent.setClassName(Definition.MEDIA_LIST_PKG, Definition.Media_LSIT_CLZ);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MediaListConst.FRAGMENT_FLAG, fragmentFlag);
        MediaApplication.getContext().startActivity(intent);
    }

    /**
     * U盘路径转换U盘标识
     */
    public static int conversionUsbPathToUsbFlag(String usbPath) {
        if (usbPath.endsWith(Definition.PATH_USB1)) {
            return Definition.FLAG_USB1;
        } else if (usbPath.endsWith(Definition.PATH_USB2)) {
            return Definition.FLAG_USB2;
        }
        return -1;
    }

    /**
     * 判断多媒体界面是否在栈顶
     */
    public static boolean getActivityIsTop(Context context, String activityName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(MediaApplication.getContext().ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        if (name.equals(activityName)) {
            return true;
        }
        return false;
    }


}
