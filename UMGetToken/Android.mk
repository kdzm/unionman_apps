LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_JAVA_LIBRARIES := framework

LOCAL_STATIC_JAVA_LIBRARIES += DisplaySetting 
LOCAL_STATIC_JAVA_LIBRARIES += HiAoService
LOCAL_PACKAGE_NAME := UMGetToken

LOCAL_CERTIFICATE := platform


LOCAL_PROGUARD_ENABLED := disabled
include $(BUILD_PACKAGE)
ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)
# Use the folloing include to make our test apk.    
include $(call all-makefiles-under,$(LOCAL_PATH))  
 
