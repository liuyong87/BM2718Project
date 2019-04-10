#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)



LOCAL_MODULE_TAGS := eng optional
LOCAL_STATIC_JAVA_LIBRARIES := android-support-v4 glide_3.7.0 autoService

#LOCAL_SRC_FILES := $(call all-java-files-under, src/main/java/)
#LOCAL_SRC_FILES += $(call all-Iaidl-files-under, src/main/aidl/)

		
src_dirs = src/main/java
aidl_dirs = src/main/aidl
LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs)) \
                   src/main/java/com/semisky/multimedia/aidl/folder/FolderInfo.java \
                   src/main/java/com/semisky/multimedia/aidl/music/MusicInfo.java \
                   src/main/java/com/semisky/multimedia/aidl/photo/PhotoInfo.java \
                   src/main/java/com/semisky/multimedia/aidl/video/VideoInfo.java \
				   src/main/aidl/com/semisky/multimedia/aidl/usb/IMediaScannerStateListener.aidl \
				   src/main/aidl/com/semisky/multimedia/aidl/usb/IMediaStorageServiceProxy.aidl \
				   src/main/aidl/com/semisky/multimedia/aidl/music/IProxyMusicPlayer.aidl \
				   src/main/aidl/com/semisky/multimedia/aidl/music/IProxyProgramChangeCallback.aidl \
				   src/main/aidl/com/semisky/multimedia/aidl/IProxyMultimedia.aidl
				   

LOCAL_AIDL_INCLUDES  += $(LOCAL_PATH)/$(aidl_dirs)

##需要编译的 AndroidManifest.xml 文件
LOCAL_MANIFEST_FILE := src/main/AndroidManifest.xml
#编译的资源文件文件路径
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/src/main/res
#LOCAL_SDK_VERSION := current
LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_ENABLED:= disabled
LOCAL_PACKAGE_NAME := AutoBMMultimedia

include $(BUILD_PACKAGE)
##################################################
include $(CLEAR_VARS)

LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := glide_3.7.0:libs/glide-3.7.0.jar

include $(BUILD_MULTI_PREBUILT)

