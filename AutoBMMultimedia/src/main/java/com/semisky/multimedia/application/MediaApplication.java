package com.semisky.multimedia.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_usb.model.MediaStorageAccessProxyModel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anter on 2018/7/28.
 */

public class MediaApplication extends Application {
    private static final String TAG = Logutil.makeTagLog(MediaApplication.class);
    /**
     * 维护Activity 的list
     */
    public static List<Activity> mActivitys = Collections.synchronizedList(new LinkedList<Activity>());
    private static Context mContext;
    private static boolean isStartMainActivityT = false; // 是否启动到launcher
    public static void setIsStartMainActivity(boolean isStartMainActivity){
        isStartMainActivityT = isStartMainActivity;
    }


    public static Context getContext() {
        return mContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Logutil.i(TAG, "onCreate() ...");
        mContext = this.getApplicationContext();
        registerActivityListener();
        MediaStorageAccessProxyModel.getInstance().onAttachContext(mContext);
        if (!MediaStorageAccessProxyModel.getInstance().isConnection()) {
            MediaStorageAccessProxyModel.getInstance().bindService();
        }
    }

    /**
     * @param activity 作用说明 ：添加一个activity到管理里
     */
    public void pushActivity(Activity activity) {
        mActivitys.add(activity);
        Logutil.d(TAG, "activityList:size:" + mActivitys.size());
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    public void popActivity(Activity activity) {
        mActivitys.remove(activity);
        Logutil.d(TAG, "activityList:size:" + mActivitys.size());
    }


    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public static Activity currentActivity() {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return null;
        }
        Activity activity = mActivitys.get(mActivitys.size() - 1);
        return activity;
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public static void finishCurrentActivity() {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        Activity activity = mActivitys.get(mActivitys.size() - 1);
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        if (activity != null) {
            mActivitys.remove(activity);
            activity.finish();
            activity = null;
            Logutil.d(TAG, "finishActivity() activityList:size:" + mActivitys.size());
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        for (Activity activity : mActivitys) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                break;
            }
        }
    }

    /**
     * 按照指定类名找到activity
     *
     * @param cls
     * @return
     */
    public static Activity findActivity(Class<?> cls) {
        Activity targetActivity = null;
        if (mActivitys != null) {
            for (Activity activity : mActivitys) {
                if (activity.getClass().equals(cls)) {
                    targetActivity = activity;
                    break;
                }
            }
        }
        return targetActivity;
    }

    /**
     * @return 作用说明 ：获取当前最顶部activity的实例
     */
    public Activity getTopActivity() {
        Activity mBaseActivity = null;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size);
        }
        return mBaseActivity;

    }

    /**
     * @return 作用说明 ：获取当前最顶部的acitivity 名字
     */
    public String getTopActivityName() {
        Activity mBaseActivity = null;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size);
        }
        return mBaseActivity.getClass().getName();
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        if (mActivitys == null) {
            return;
        }
        Logutil.i(TAG, "finishAllActivity() ..." + mActivitys.size());
        for (Activity activity : mActivitys) {
            Logutil.i(TAG, "mActivitys=" + activity.getClass().getSimpleName());
            activity.finish();
        }
        mActivitys.clear();
        if (isStartMainActivityT){
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            mContext.startActivity(homeIntent);
            isStartMainActivityT = false ;
        }

    }

    /**
     * 退出应用程序
     */
    public static void appExit() {
        try {
            Logutil.e(TAG, "app exit");
            finishAllActivity();
        } catch (Exception e) {
        }
    }

    private void registerActivityListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
                @Override
                public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                    /**
                     *  监听到 Activity创建事件 将该 Activity 加入list
                     */
                    pushActivity(activity);

                }

                @Override
                public void onActivityStarted(Activity activity) {

                }

                @Override
                public void onActivityResumed(Activity activity) {

                }

                @Override
                public void onActivityPaused(Activity activity) {

                }

                @Override
                public void onActivityStopped(Activity activity) {

                }

                @Override
                public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

                }

                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (null == mActivitys && mActivitys.isEmpty()) {
                        return;
                    }
                    if (mActivitys.contains(activity)) {
                        /**
                         *  监听到 Activity销毁事件 将该Activity 从list中移除
                         */
                        popActivity(activity);
                    }
                }
            });
        }
    }

}
