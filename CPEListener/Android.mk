
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_JAVA_LIBRARIES := framework

LOCAL_PACKAGE_NAME := CPEListener 

LOCAL_CERTIFICATE := platform
LOCAL_STATIC_JAVA_LIBRARIES := xUtils jsch
LOCAL_STATIC_JAVA_LIBRARIES += Hitv
LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := disabled
ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)


LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
    xUtils:libs/xUtils-2.6.14.jar \
    jsch:libs/com.jcraft.jsch.jar
										
include $(BUILD_MULTI_PREBUILT)

