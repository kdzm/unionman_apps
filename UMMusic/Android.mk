LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_STATIC_JAVA_LIBRARIES := \
                                um-music-pinyin4j-2.5.0\
                                um.com.springsource.org.apache.commons.httpclient-3.1.0\
                                HiMediaPlayer
LOCAL_STATIC_JAVA_LIBRARIES += SDKInvoke
LOCAL_STATIC_JAVA_LIBRARIES += Hitv

LOCAL_SRC_FILES := $(call all-java-files-under, src) \
     src/com/um/music/IMediaPlaybackService.aidl

LOCAL_PACKAGE_NAME := UMMusic
ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)
LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
                                        um-music-pinyin4j-2.5.0:lib/pinyin4j-2.5.0.jar\
    um.com.springsource.org.apache.commons.httpclient-3.1.0:lib/com.springsource.org.apache.commons.httpclient-3.1.0.jar\



include $(BUILD_MULTI_PREBUILT)
# Use the folloing include to make our test apk.

include $(call all-makefiles-under,$(LOCAL_PATH))
