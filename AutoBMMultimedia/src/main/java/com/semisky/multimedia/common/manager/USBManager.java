package com.semisky.multimedia.common.manager;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.semisky.multimedia.common.constants.Definition;
import com.semisky.multimedia.common.utils.AppUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.common.utils.USBCheckUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiuYong on 2018/8/27.
 */

public class USBManager {
    private static final String TAG = Logutil.makeTagLog(USBManager.class);
    private List<OnUSBStateChangeListener> mUSBObserverList = new ArrayList<OnUSBStateChangeListener>();
    private static USBManager _INSTANCE;
    private static final int DEV_USB_ID = 0x08;// USB设备ID

    public static final int STATE_USB_CHECKING = 10;// U盘准备挂载状态
    public static final int STATE_USB_MOUNTED = 11;// U盘挂载
    public static final int STATE_USB_UNMOUNTED = 12;// U盘卸载
    public static final int STATE_USB_REMOVED = 13;// U盘移除
    public static final int STATE_USB_DEVICE_ATTACHED = 21;// 设备挂载
    public static final int STATE_USB_DEVICE_DETACHED = 22;// 设备卸载

    private String mFirstMountedUsbPath = null;
    private String mTempFirstMountedUsbPath = null;


    private USBManager() {

    }

    public static USBManager getInstance() {
        if (null == _INSTANCE) {
            _INSTANCE = new USBManager();
        }
        return _INSTANCE;
    }

    /**
     * USB状态监听接口
     */
    public interface OnUSBStateChangeListener {
        void onChangeState(String usbPath, int stateCode);
    }

    public void registerOnUSBStateChangeListener(OnUSBStateChangeListener l) {
        if (null != l && !mUSBObserverList.contains(l)) {
            mUSBObserverList.add(l);
        }
    }

    public void unregisterOnUSBStateChangeListener(OnUSBStateChangeListener l) {
        if (null != l && mUSBObserverList.contains(l)) {
            mUSBObserverList.remove(l);
        }
    }

    public void notifyChangeUSBState(String usbPath, int stateCode) {
        if (null != mUSBObserverList && mUSBObserverList.size() > 0) {
            for (OnUSBStateChangeListener l : mUSBObserverList) {
                l.onChangeState(usbPath, stateCode);
            }
        }
    }

    /**
     * 首个插入U盘是否挂载
     *
     * @return
     */
    public boolean isFirstUsbMounted() {
        return USBCheckUtil.isUdiskExist(getFirstMountedUsbPath());
    }

    /**
     * 判断是否为USB设备
     *
     * @param intent
     * @return
     */
    public boolean isUSB(Intent intent) {
        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if(device.getInterfaceCount() == 0) {
            return false; 
        }
        int devId = device.getInterface(0).getInterfaceClass();
        Logutil.i(TAG, "devId=" + devId);
        if (DEV_USB_ID == devId) {
            return true;
        }
        return false;
    }

    /**
     * 获取临时首个挂载U盘路径
     *
     * @return
     */
    public String getTempFirstMountedUsbPath() {
        return this.mTempFirstMountedUsbPath;
    }

    /**
     * 设置临时首个挂载U盘路径
     *
     * @param usbPath
     */
    public void setTempFirstMountedUsbPath(String usbPath) {
        this.mTempFirstMountedUsbPath = usbPath;
        this.mFirstMountedUsbPath = usbPath;
    }

    /**
     * 获取首个挂载U盘标识
     *
     * @return
     */
    public int getFirstMountedUsbFlag() {
        return AppUtil.conversionUsbPathToUsbFlag(getFirstMountedUsbPath());
    }

    /**
     * 获取首个挂载U盘路径
     */
    public String getFirstMountedUsbPath() {

        if (null == mFirstMountedUsbPath) {
            String lastFirstMountedUsbPath = getLastUsbPath();
            if (null != lastFirstMountedUsbPath) {
                mFirstMountedUsbPath = lastFirstMountedUsbPath;
            } else {
                String defMountedUsbPath = getDefaultMountedUSB();
                if (null != defMountedUsbPath) {
                    mFirstMountedUsbPath = defMountedUsbPath;
                }
            }
        }
        return mFirstMountedUsbPath;
    }

    // 获取有效历史挂载USB路径
    private String getLastUsbPath() {
        int usbFlag = PreferencesManager.getFirstMountedUsbFlag();
        boolean isMounted = false;
        String lastFirstMountedUsbPath = null;
        switch (usbFlag) {
            case Definition.FLAG_USB1:
                isMounted = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
                if (isMounted) {
                    lastFirstMountedUsbPath = Definition.PATH_USB1;
                }
                break;
            case Definition.FLAG_USB2:
                isMounted = USBCheckUtil.isUdiskExist(Definition.PATH_USB2);
                if (isMounted) {
                    lastFirstMountedUsbPath = Definition.PATH_USB2;
                }
                break;
        }
        return lastFirstMountedUsbPath;
    }

    // 获取默认已挂载USB路径
    private String getDefaultMountedUSB() {
        boolean isMountedUsb1 = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
        boolean isMountedUsb2 = USBCheckUtil.isUdiskExist(Definition.PATH_USB1);
        String defFirstMountedUsbPath = null;
        if (isMountedUsb1) {
            defFirstMountedUsbPath = Definition.PATH_USB1;
        } else if (isMountedUsb2) {
            defFirstMountedUsbPath = Definition.PATH_USB1;
        }
        return defFirstMountedUsbPath;
    }

    /**
     * 设置首个挂载U盘路径
     *
     * @param usbPath
     */
    public void setFirstMountedUsbPath(String usbPath) {
        if (null == mFirstMountedUsbPath) {
            this.mFirstMountedUsbPath = usbPath;
            int usbFlag = AppUtil.conversionUsbPathToUsbFlag(usbPath);
            PreferencesManager.saveFirstMountedUsbFlag(usbFlag);
        }
    }

    /**
     * 重置首个U盘路径
     */
    public void resetFirstMountedUsbPath() {
        this.mFirstMountedUsbPath = null;
        this.mTempFirstMountedUsbPath = null;
        PreferencesManager.saveFirstMountedUsbFlag(-1);
    }


}
