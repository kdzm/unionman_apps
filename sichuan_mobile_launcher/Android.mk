
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_JAVA_LIBRARIES := 

LOCAL_PACKAGE_NAME := sichuan_mobile_launcher

LOCAL_OVERRIDES_PACKAGES := Launcher3 

LOCAL_CERTIFICATE := platform


#LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := full
ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)
include $(BUILD_PACKAGE)

