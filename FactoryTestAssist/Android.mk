LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)


LOCAL_PACKAGE_NAME := FactoryTestAssist

LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_FLAG_FILES := proguard.flags
#LOCAL_PROGUARD_ENABLED := full
LOCAL_PROGUARD_ENABLED := disabled

LOCAL_STATIC_JAVA_LIBRARIES := Hitv

LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR_APPS)

include $(BUILD_PACKAGE)

# Use the following include to make our test apk.    
include $(call all-makefiles-under,$(LOCAL_PATH))  
 
