package com.semisky.multimedia.aidl.usb;

interface IMediaScannerStateListener {
	/**
	 * 媒体文件扫描开始
	 * 
	 * @param usbFlag
	 */
	void onScannerStart(int usbFlag);

	/**
	 * 媒体文件正常扫描
	 * 
	 * @param usbFlag
	 */
	void onScanning(int usbFlag);

	/**
	 * 媒体文件扫描停止
	 * 
	 * @param usbFlag
	 */
	void onScannerStoped(int usbFlag);

	/**
	 * 媒体文件扫描完成
	 * 
	 * @param usbFlag
	 */
	void onScannerDone(int usbFlag);
}
