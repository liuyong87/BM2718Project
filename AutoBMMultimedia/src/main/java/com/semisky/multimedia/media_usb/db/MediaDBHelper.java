package com.semisky.multimedia.media_usb.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.semisky.multimedia.common.utils.Logutil;


public class MediaDBHelper extends SQLiteOpenHelper{
	private static final String TAG = Logutil.makeTagLog(MediaDBHelper.class);
	
	private static final String CREATE_MUSIC_TABLE =  "create table "
            +DBConfiguration.MusicConfiguration.TABLE_NAME+"("
            +DBConfiguration.MusicConfiguration._ID
            +" integer primary key autoincrement ,"
            +DBConfiguration.MusicConfiguration.USB_FLAG + " integer ,"
            +DBConfiguration.MusicConfiguration.FILE_TYPE + " integer ,"
            +DBConfiguration.MusicConfiguration.MUSIC_URL + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_FOLDER_URL + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_TITLE + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_TITLE_PINYING + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_ARTIST + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_ARTIST_PINYING + " text,"
            +DBConfiguration.MusicConfiguration.MUSIC_ALBUM + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_ALBUM_PINYIN + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_DURATION + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_FAVORITE + " integer"
            +")";
	
	private static final String CREATE_MUSIC_FAVORITE_TABLE =  "create table "
            +DBConfiguration.MusicFavoriteConfiguration.TABLE_NAME+"("
            +DBConfiguration.MusicFavoriteConfiguration._ID
            +" integer primary key autoincrement ,"
            +DBConfiguration.MusicConfiguration.USB_FLAG + " integer ,"
            +DBConfiguration.MusicConfiguration.FILE_TYPE + " integer ,"
            +DBConfiguration.MusicConfiguration.MUSIC_URL + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_FOLDER_URL + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_TITLE + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_TITLE_PINYING + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_ARTIST + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_ARTIST_PINYING + " text,"
            +DBConfiguration.MusicConfiguration.MUSIC_ALBUM + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_ALBUM_PINYIN + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_DURATION + " text ,"
            +DBConfiguration.MusicConfiguration.MUSIC_FAVORITE + " integer"
            +")";

	
    private static final String CREATE_PICTURE_TABLE = "create table "
            +DBConfiguration.PhotoConfiguration.TABLE_NAME+"("
            +DBConfiguration.PhotoConfiguration._ID
            +" integer primary key autoincrement ,  "
            +DBConfiguration.PhotoConfiguration.USB_FLAG+" integer ,"
            +DBConfiguration.PhotoConfiguration.FILE_TYPE + " integer ,"
            +DBConfiguration.PhotoConfiguration.FILE_URL+" text ,"
            +DBConfiguration.PhotoConfiguration.FILE_FORDER_URL+" text ,"
            +DBConfiguration.PhotoConfiguration.FILE_NAME + " text,"
            +DBConfiguration.PhotoConfiguration.FILE_NAME_PINYIN + " text"
            +")";

    private static final String CREATE_VIDEO_TABLE = "create table "
            +DBConfiguration.VideoConfiguration.TABLE_NAME+"("
            +DBConfiguration.VideoConfiguration._ID
            +" integer primary key autoincrement  , "
            +DBConfiguration.VideoConfiguration.USB_FLAG + " integer ,"
            +DBConfiguration.VideoConfiguration.FILE_TYPE + " integer ,"
            +DBConfiguration.VideoConfiguration.FILE_URL + " text ,"
            +DBConfiguration.VideoConfiguration.FILE_NAME + " text ,"
            +DBConfiguration.VideoConfiguration.FILE_NAME_PINYIN + " text,"
            +DBConfiguration.VideoConfiguration.FILE_FOLDER_URL+" text ,"
            +DBConfiguration.VideoConfiguration.FILE_DURATION+ " integer"
            +")";

    public MediaDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Logutil.i(TAG, "-------------->MediaDBHelper() init ...");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logutil.i(TAG, "-------------->onCreate()");
        db.execSQL(CREATE_MUSIC_TABLE);
        db.execSQL(CREATE_PICTURE_TABLE);
        db.execSQL(CREATE_VIDEO_TABLE);
        db.execSQL(CREATE_MUSIC_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Logutil.i(TAG, "-------------->onUpgrade()");
        db.execSQL("DROP TABLE IF EXISTS "+DBConfiguration.MusicConfiguration.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ DBConfiguration.VideoConfiguration.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBConfiguration.PhotoConfiguration.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBConfiguration.MusicFavoriteConfiguration.TABLE_NAME);
        onCreate(db);
    }
    
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//    	super.onDowngrade(db, oldVersion, newVersion);
    	Logutil.i(TAG, "-------------->onDowngrade()");
    	db.execSQL("DROP TABLE IF EXISTS "+DBConfiguration.MusicConfiguration.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ DBConfiguration.VideoConfiguration.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBConfiguration.PhotoConfiguration.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DBConfiguration.MusicFavoriteConfiguration.TABLE_NAME);//pictures
        onCreate(db);
    }

}
