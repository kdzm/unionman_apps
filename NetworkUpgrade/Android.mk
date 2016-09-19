
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_JAVA_LIBRARIES := framework

LOCAL_PACKAGE_NAME := NetworkUpgrade 

LOCAL_CERTIFICATE := platform
LOCAL_STATIC_JAVA_LIBRARIES := util 
LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := full

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)


LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := util:libs/xUtils-2.6.14.jar 
										
include $(BUILD_MULTI_PREBUILT)

