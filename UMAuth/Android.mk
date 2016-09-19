LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := UMAuth

LOCAL_CERTIFICATE := platform

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

include $(BUILD_PACKAGE)
ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)
