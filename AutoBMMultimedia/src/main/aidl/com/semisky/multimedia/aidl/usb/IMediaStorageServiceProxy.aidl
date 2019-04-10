package com.semisky.multimedia.aidl.usb;

import java.util.List;
import java.lang.String;
import com.semisky.multimedia.aidl.music.MusicInfo;
import com.semisky.multimedia.aidl.video.VideoInfo;
import com.semisky.multimedia.aidl.photo.PhotoInfo;
import com.semisky.multimedia.aidl.folder.FolderInfo;
import com.semisky.multimedia.aidl.usb.IMediaScannerStateListener;

interface IMediaStorageServiceProxy {
	/**
	 * 注册媒体文件扫描状态监听
	 * 
	 * @param listener
	 */
	void registerOnMediaScannerStateListener(IMediaScannerStateListener listener);
	/**
	 * 反注册媒体文件扫描状态监听
	 * 
	 * @param listener
	 */
	void unregisterOnMediaScannerStateListener(IMediaScannerStateListener listener);
	/**
	 * 插入收藏音乐
	 * 
	 * @param musicInfo
	 * @return
	 */
	long insertFavoriteMusic(in MusicInfo musicInfo);
	 /**
     * 指定音乐URL是否已收藏
     *
     * @param usbFlag
     * @param url
     * @return
     */
     boolean isFavoriteWithSpecifyMusicUrl(int usbFlag, String url);
	/**
	 * 查询所有收藏音乐文件
	 * 
	 * @param usbFlag
	 * @return
	 */
	List<MusicInfo> queryAllFavoriteMusics(int usbFlag);

	/**
	 * 获取全部音乐
	 * 
	 * @param usbFlag
	 */
	List<MusicInfo> queryAllMusics(int usbFlag);
	/**
	 * 查询所有媒体视频
	 * 
	 * @param usbFlag
	 *            U盘标识
	 * @return 视频集合
	 */
	List<VideoInfo> queryAllVideos(int usbFlag);

	/**
	 * 查询所有媒体图片
	 * 
	 * @param usbFlag
	 * @return 图片集合
	 */
	List<PhotoInfo> queryAllPhotos(int usbFlag);
	
	/**
	 * 查询指定目录信息
	 * @param curDir 当前切换目录
	 * @return 目录信息集合
	 */
	List<FolderInfo> querySpecifyDirectoryUnder(String curDir);

	/**
	 * 获取媒体音乐总数
	 * 
	 * @param usbFlag
	 * @return
	 */
	int queryMusicsSize(int usbFlag);

	/**
	 * 获取媒体视频总数
	 * 
	 * @param usbFlag
	 * @return
	 */
	int queryVideosSize(int usbFlag);

	/**
	 * 获取媒体图片总数
	 * 
	 * @param usbFlag
	 * @return
	 */
	int queryPhotosSize(int usbFlag);
	/**
	 * 删除所有音乐
	 * 
	 * @param usbFlag
	 * @return
	 */
	long deleteAllMusics(int usbFlag);

	/**
	 * 删除所有视频
	 * 
	 * @param usbFlag
	 * @return
	 */
	long deleteAllVideos(int usbFlag);

	/**
	 * 删除所有图片
	 * 
	 * @param usbFlag
	 * @return
	 */
	long deleteAllPhotos(int usbFlag);
	/**
	 * 删除收藏音乐
	 * 
	 * @param id 自增长ID
	 * @return
	 */
	long deleteFavoriteMusic(int id);
	/**
     * 删除指定收藏音乐
     *
     * @param usbFlag U盘标识
     * @param url     媒体路径
     * @return
     */
    long deleteFavoriteWithMusicUrl(int usbFlag, String url);
    /**
     * 删除批量收藏媒体资源
     *
     * @param list
     */
    void deleteBatchFavorite(in List<String> list);
}
