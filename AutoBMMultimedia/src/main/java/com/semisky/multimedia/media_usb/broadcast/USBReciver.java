package com.semisky.multimedia.media_usb.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.semisky.multimedia.application.MediaApplication;
import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.manager.SemiskyIVIManager;
import com.semisky.multimedia.common.manager.USBManager;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel;

import java.io.File;

/**
 * Created by Anter on 2018/8/4.
 */

public class USBReciver extends BroadcastReceiver {
    private static final String TAG = Logutil.makeTagLog(USBReciver.class);
    private static boolean isAttached = false ;
    public static boolean getIsAttached(){
        return isAttached;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = getAction(intent);
        String usbPath = getPath(intent);

        Logutil.i(TAG, "===========USB STATE INFO START======================");
        Logutil.i(TAG, "onReceive() action = " + action);
        Logutil.i(TAG, "onReceive() usbPath = " + usbPath);
        Logutil.i(TAG, "===========USB STATE INFO END  ======================");

        if (Definition.ACTION_USB_DEVICE_DETACHED.equals(action)) {
           // handlerUSBDeviceDetached(intent, usbPath);
            if (USBManager.getInstance().isUSB(intent)){
                usbPath = "/storage/udisk0";
                if(!new File(usbPath).exists()){
                    usbPath = "/storage/udisk1";
                }
                handlerUSBURemoved(usbPath);
                isAttached = false ;
            }

        } else if (Definition.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            handlerUSBDeviceAttached(intent, usbPath);
        }

        if (!isUSBPath(usbPath)) {
            return;
        }

        if (Intent.ACTION_MEDIA_CHECKING.equals(action)) {// 插入外部存储装置事件
            // 清除数据库数据
            handlerUSBChecking(usbPath);
        } else if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {// U盘挂载
            // 启动U盘扫描服务
            handlerUSBMouted(usbPath);
        } else if (Intent.ACTION_MEDIA_REMOVED.equals(action)) {// U盘卸载
            // 停止U盘扫描服务
           // handlerUSBURemoved(usbPath);
        }
    }

    /**
     * 设备挂载
     *
     * @param intent
     * @param usbPath
     */
    private void handlerUSBDeviceAttached(Intent intent, String usbPath) {
        boolean isUsb = USBManager.getInstance().isUSB(intent);
        Logutil.i(TAG, "handlerUSBDeviceDetached() ..." + isUsb + ",usbPath=" + usbPath);
        if (isUsb) {
            isAttached = true ;
            SemiskyIVIManager.getInstance().closeScreenSave();//如果屏保时打开状态，关闭屏保
            USBManager.getInstance().notifyChangeUSBState(usbPath, USBManager.STATE_USB_DEVICE_ATTACHED);
        }
    }

    /**
     * 设备卸载
     *
     * @param intent
     * @param usbPath
     */
    private void handlerUSBDeviceDetached(Intent intent, String usbPath) {
        boolean isUsb = USBManager.getInstance().isUSB(intent);
        Logutil.i(TAG, "handlerUSBDeviceDetached() ..." + isUsb + ",usbPath=" + usbPath);
        if (isUsb) {
            USBManager.getInstance().notifyChangeUSBState(usbPath, USBManager.STATE_USB_DEVICE_DETACHED);
        }
    }

    /**
     * U盘挂载处理
     *
     * @param usbPath
     */
    private void handlerUSBMouted(String usbPath) {
        Logutil.i(TAG, "handlerUSBMouted() ..." + usbPath);

        // 设置首个挂载U盘标识
        if (null == USBManager.getInstance().getTempFirstMountedUsbPath()) {
            USBManager.getInstance().setTempFirstMountedUsbPath(usbPath);
        }
        USBManager.getInstance().notifyChangeUSBState(usbPath, USBManager.STATE_USB_MOUNTED);
        MediaStorageAccessProxyModel.getInstance().onStartScanPath(usbPath);

    }

    /**
     * U盘卸载处理
     *
     * @param usbPath
     */
    private void handlerUSBURemoved(String usbPath) {
//        MediaStorageAccessProxyModel.getInstance().onDeleteAllMedia(usbPath);
        MediaStorageAccessProxyModel.getInstance().onStopScanPath(usbPath);
        USBManager.getInstance().notifyChangeUSBState(usbPath, USBManager.STATE_USB_DEVICE_DETACHED);
        USBManager.getInstance().resetFirstMountedUsbPath();
    }

    /**
     * U盘挂载前的检查处理
     *
     * @param usbPath
     */
    private void handlerUSBChecking(String usbPath) {
        USBManager.getInstance().notifyChangeUSBState(usbPath, USBManager.STATE_USB_CHECKING);
        MediaStorageAccessProxyModel.getInstance().onDeleteAllMedia(usbPath);
    }

    /**
     * 获取U盘路径
     *
     * @param intent
     * @return
     */
    private String getPath(Intent intent) {
        if (null != intent) {
            Uri dataUri = intent.getData();
            if (null != dataUri) {
                return dataUri.getPath();
            }
        }
        return "";
    }

    /**
     * 获取广播意图
     *
     * @param intent
     * @return
     */
    private String getAction(Intent intent) {
        if (null != intent) {
            return intent.getAction();
        }
        return "";
    }

    private boolean isUSBPath(String usbPath) {
        boolean isUSBpath = false;
        if (Definition.PATH_USB1.equals(usbPath)) {
            isUSBpath = true;
        } else if (Definition.PATH_USB2.equals(usbPath)) {
            isUSBpath = true;
        }
        Logutil.i(TAG, "isUSBpath=" + isUSBpath);
        return isUSBpath;
    }
}
