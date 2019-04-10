package com.semisky.multimedia.media_usb.mediascan;

import android.content.ContentValues;
import android.media.MediaMetadataRetriever;


import com.semisky.multimedia.common.utils.EncodingUtil;
import com.semisky.multimedia.common.utils.HanziToPinyinUtil;
import com.semisky.multimedia.common.utils.Logutil;
import com.semisky.multimedia.media_usb.db.DBConfiguration;

import java.io.File;

/**
 * 媒体解析实现类
 * @author liuyong
 *
 */
public final class MediaParser implements IMediaParser {
	private static final String TAG = Logutil.makeTagLog(MediaParser.class);
	private static MediaParser _instance;
	
	private MediaParser(){
		
	}
	
	public static MediaParser getInstance(){
		if(null == _instance){
			_instance = new MediaParser();
		}
		return _instance;
	}
	

	@Override
	public ContentValues getContentValuesByMusic(int usbFlag,String filePath) {
		ContentValues contentValues = null;
		MediaMetadataRetriever mmr = null;

		String musicUrl = "Unknown";
		String musicFolderUrl = "Unknown";
		String musicTitle = "Unknown";
		String musicTitlePinYing = "Unknown";
		String musicArtist = "Unknown";
		String musicArtistPinYing = "";
		String musicAlbum = "Unknown";
		int musicDuration = 0;

		// 实例化媒体解析类
		mmr = new MediaMetadataRetriever();

		try {
			if (new File(filePath).exists()) {
				mmr.setDataSource(filePath);
			} else {
				Logutil.e(TAG, "parserMusic() URL NO EXISTS !!! " + filePath);
				safeCloseMediaMetadataRetriever(mmr);
				return null;
			}
		} catch (Exception e) {
			Logutil.e(TAG, "MediaScanner:setDataSource() error !!! url: " + filePath);
			safeCloseMediaMetadataRetriever(mmr);
			return null;
		}

		// duration
		String sDuration = "0";

		try {
			sDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		} catch (Exception e) {
			Logutil.e(TAG, "Get Total time fail , url: " + filePath);
		}
		try {
			musicDuration = Integer.valueOf((sDuration != null ? sDuration : "0"));// 时长有可能是乱码，防止类型转换异常
		} catch (Exception e) {
			musicDuration = 0;
			Logutil.e(TAG, "Total time format fail , sDuration: " + sDuration + ", url: " + filePath);
		}
		if (musicDuration == 0) {
			musicDuration = 0;
		}

		// url
		musicUrl = filePath;
		// folder url
		musicFolderUrl = musicUrl.substring(0, musicUrl.lastIndexOf(File.separator));

		// title
		// 获得带后缀的名字
		musicTitle = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
		// title pinying
		// 获得带后缀名字拼音
		try {
			musicTitlePinYing = HanziToPinyinUtil.getIntance().getSortKey(musicTitle,
					HanziToPinyinUtil.FullNameStyle.CHINESE);
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		// 获得歌手名字
		try {
			musicArtist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			if (null != musicArtist) {
				EncodingUtil encodingUtil = new EncodingUtil();
				musicArtist = encodingUtil.getEncodeString(musicArtist, null);
			}
		} catch (Exception e1) {
			Logutil.e(TAG, "Get artist fail !!! , url=" + filePath);
		}
		if (musicArtist == null || musicArtist != null && musicArtist.length() <= 0 || "".equals(musicArtist)) {
			musicArtist = "Unknown";
		}

		// 获取歌手拼音
		try {
			musicArtistPinYing = HanziToPinyinUtil.getIntance().getSortKey(musicArtist,
					HanziToPinyinUtil.FullNameStyle.CHINESE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 获得Album
		try {
			musicAlbum = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
			// add 2018-4-20
			if (null != musicAlbum) {
				EncodingUtil encodingUtil = new EncodingUtil();
				musicAlbum = encodingUtil.getEncodeString(musicAlbum, null);
			}
		} catch (Exception e1) {
			musicAlbum = null;
		}
		if (musicAlbum == null || musicAlbum != null && musicAlbum.length() <= 0) {
			musicAlbum = "Unknown";
		}

		safeCloseMediaMetadataRetriever(mmr);

		contentValues = new ContentValues();
		contentValues.put(DBConfiguration.FILE_TYPE, DBConfiguration.FLAG_MUSIC);
		contentValues.put(DBConfiguration.MusicConfiguration.USB_FLAG, usbFlag);
		contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_URL, musicUrl);
		contentValues.put(DBConfiguration.MusicConfiguration.FILE_TYPE, DBConfiguration.FLAG_MUSIC);
		contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_FOLDER_URL, musicFolderUrl);
		contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_TITLE, musicTitle);
		contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_TITLE_PINYING, musicTitlePinYing);
		contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_ARTIST, musicArtist);
		contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_ARTIST_PINYING, musicArtistPinYing);
		contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_ALBUM, musicAlbum);
		contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_ALBUM_PINYIN, "");
		contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_DURATION, musicDuration);
		contentValues.put(DBConfiguration.MusicConfiguration.MUSIC_FAVORITE, 0);// 0:未收藏,1:表示收藏
		return contentValues;
	}

	private void safeCloseMediaMetadataRetriever(MediaMetadataRetriever mmr) {
		if (null != mmr) {
			try {
				mmr.release();
				mmr = null;
			} catch (Exception e) {
				Logutil.w(TAG, "----------------------safeCloseMediaMetadataRetriever()");
			}
		}
	}

	// 分离出字符串最后斜杠后的字符串
	private String splitLastSeparatorString(String str) {
		String newStr = str.substring(str.lastIndexOf(File.separator) + 1);
		return newStr;
	}

	@Override
	public ContentValues getContentValuesByVideo(int usbFlag,String filePath) {
		ContentValues contentValues = null;
		String videoFolderUrl = null;

		if (filePath == null) {
			return null;
		}

		try {
			contentValues = new ContentValues();
			videoFolderUrl = "NA";
			String videoTitle = splitLastSeparatorString(filePath);
			String videoTitlePinYing = HanziToPinyinUtil.getIntance().getSortKey(videoTitle,
					HanziToPinyinUtil.FullNameStyle.CHINESE);
			videoTitlePinYing = videoTitlePinYing.toLowerCase();// 将所有的视频名称拼音转换为小写,便于排序（因字母大写与小写ASCII码是不一样的,会导致排序错误）

			contentValues.put(DBConfiguration.FILE_TYPE, DBConfiguration.FLAG_VIDEO);
			contentValues.put(DBConfiguration.VideoConfiguration.FILE_TYPE, DBConfiguration.FLAG_VIDEO);
			contentValues.put(DBConfiguration.VideoConfiguration.USB_FLAG, usbFlag);
			contentValues.put(DBConfiguration.VideoConfiguration.FILE_URL, filePath);
			contentValues.put(DBConfiguration.VideoConfiguration.FILE_NAME, videoTitle);
			contentValues.put(DBConfiguration.VideoConfiguration.FILE_NAME_PINYIN, videoTitlePinYing);
			contentValues.put(DBConfiguration.VideoConfiguration.FILE_FOLDER_URL, videoFolderUrl);
		} catch (Exception e) {
			Logutil.d(TAG, "getContentValuesByVideo() fail, filePath : " + filePath);
		}
		return contentValues;
	}

	@Override
	public ContentValues getContentValuesByPhoto(int usbFlag,String filePath) {
		ContentValues contentValues = null;
		String pictureUrl = null;
		String pictureFolderUrl = null;

		if (filePath == null) {
			return null;
		}

		try {
			pictureUrl = filePath;
			pictureFolderUrl = "NA";
			contentValues = new ContentValues();
			String videoTitle = splitLastSeparatorString(filePath).trim();
			String videoTitlePinYing = HanziToPinyinUtil.getIntance().getSortKey(videoTitle,
					HanziToPinyinUtil.FullNameStyle.CHINESE);

			contentValues.put(DBConfiguration.FILE_TYPE, DBConfiguration.FLAG_PHOTO);
			contentValues.put(DBConfiguration.PhotoConfiguration.FILE_TYPE, DBConfiguration.FLAG_PHOTO);
			contentValues.put(DBConfiguration.PhotoConfiguration.USB_FLAG, usbFlag);
			contentValues.put(DBConfiguration.PhotoConfiguration.FILE_URL, pictureUrl);
			contentValues.put(DBConfiguration.PhotoConfiguration.FILE_NAME, videoTitle);
			contentValues.put(DBConfiguration.PhotoConfiguration.FILE_NAME_PINYIN, videoTitlePinYing);
			contentValues.put(DBConfiguration.PhotoConfiguration.FILE_FORDER_URL, pictureFolderUrl);
		} catch (Exception e) {
			Logutil.e(TAG, ": getContentValuesByPhoto() fali !!! filePath: " + filePath);
		}
		return contentValues;
	}

}
