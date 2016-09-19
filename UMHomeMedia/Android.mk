LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_JAVA_LIBRARIES :=


LOCAL_PACKAGE_NAME := UMHomeMedia

LOCAL_CERTIFICATE := platform


#LOCAL_PROGUARD_ENABLED := disabled
ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)
include $(BUILD_PACKAGE)

# Use the folloing include to make our test apk.    
include $(call all-makefiles-under,$(LOCAL_PATH))  
 
