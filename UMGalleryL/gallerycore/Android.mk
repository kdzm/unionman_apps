
LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)

LOCAL_MODULE := gallerycore
LOCAL_SRC_FILES := $(call all-java-files-under, src)
#LOCAL_SDK_VERSION := current

include $(BUILD_STATIC_JAVA_LIBRARY)
