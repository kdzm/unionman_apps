LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := UMLauncher

LOCAL_CERTIFICATE := platform

#LOCAL_PROGUARD_FLAG_FILES := proguard.flags
#LOCAL_PROGUARD_ENABLED := full
ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)
include $(BUILD_PACKAGE)
###############################################################
include $(CLEAR_VARS)
include $(BUILD_MULTI_PREBUILT)
# Use the folloing include to make our test apk.    
include $(call all-makefiles-under,$(LOCAL_PATH))  
 
