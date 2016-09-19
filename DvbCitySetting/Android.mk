LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_STATIC_JAVA_LIBRARIES += DisplaySetting reboot_factory DvbStorage NativeProvider


LOCAL_PACKAGE_NAME := DvbCitySetting

LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := full
LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR_APPS)

LOCAL_JNI_SHARED_LIBRARIES := libdvbservermanager_jni
LOCAL_REQUIRED_MODULES := libdvbservermanager_jni

#LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.    
include $(call all-makefiles-under,$(LOCAL_PATH))  
 
