// IProxyMultimedia.aidl
package com.semisky.multimedia.aidl;


interface IProxyMultimedia {
    /**
    * 获取多媒体应用标识
    **/
    int getMultimediaAppFlag();
    /**
    * 是否为有效的应用标识
    * */
    boolean hasValidAppFlagWith(int appFlag);
    /**
    * 启动多媒体
    **/
    void onLaunchMultimedia();
    /**
    * 启动音乐多媒体音乐播放服务
    **/
    void onLaunchMusicPlayService();

}
