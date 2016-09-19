LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_PACKAGE_NAME := DvbProviderTest

LOCAL_STATIC_JAVA_LIBRARIES := DvbStorage

LOCAL_REQUIRED_MODULES := DvbStorage

LOCAL_CERTIFICATE := shared

#LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.    
include $(call all-makefiles-under,$(LOCAL_PATH))  
 
