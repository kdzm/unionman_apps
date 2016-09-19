LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_MODULE_PATH := $(TARGET_OUT)/vendor/dvb/install_packages/default_city

LOCAL_JAVA_LIBRARIES :=
LOCAL_STATIC_JAVA_LIBRARIES := jazzlib

LOCAL_JNI_SHARED_LIBRARIES := libosdjni
LOCAL_PACKAGE_NAME := UmOsd


LOCAL_CERTIFICATE := shared
LOCAL_REQUIRED_MODULES := libosdjni

LOCAL_PROGUARD_ENABLED := full
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)


include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR_APPS)

LOCAL_JAVA_LIBRARIES :=
LOCAL_STATIC_JAVA_LIBRARIES := jazzlib

LOCAL_JNI_SHARED_LIBRARIES := libosdjni
LOCAL_PACKAGE_NAME := UmOsdPreInstall


LOCAL_CERTIFICATE := shared
LOCAL_REQUIRED_MODULES := libosdjni

LOCAL_PROGUARD_ENABLED := full
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.    
include $(call all-makefiles-under,$(LOCAL_PATH))  
 
