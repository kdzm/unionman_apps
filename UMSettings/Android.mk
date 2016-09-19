LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_JAVA_LIBRARIES := framework

LOCAL_STATIC_JAVA_LIBRARIES += DisplaySetting 
LOCAL_STATIC_JAVA_LIBRARIES += HiAoService
LOCAL_STATIC_JAVA_LIBRARIES += Hitv
LOCAL_STATIC_JAVA_LIBRARIES += HuanClientAuth
LOCAL_PACKAGE_NAME := UMSettings

LOCAL_CERTIFICATE := platform


LOCAL_PROGUARD_ENABLED := disabled
ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)
include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.    
include $(call all-makefiles-under,$(LOCAL_PATH))  
 
