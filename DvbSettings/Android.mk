LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_MODULE_PATH := $(TARGET_OUT)/vendor/dvb/install_packages/default_city

#LOCAL_JAVA_LIBRARIES :=

LOCAL_JNI_SHARED_LIBRARIES := libdvbsettings_jni
LOCAL_PACKAGE_NAME := DvbSettings

LOCAL_CERTIFICATE := platform
LOCAL_STATIC_JAVA_LIBRARIES := CrashHandler DisplaySetting DvbStorage DvbPlayer Hitv NativeProvider jazzlib  

LOCAL_REQUIRED_MODULES := DvbStorage libdvbsettings_jni


LOCAL_PROGUARD_ENABLED := full
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)


include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)
LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR_APPS)

#LOCAL_JAVA_LIBRARIES :=

LOCAL_JNI_SHARED_LIBRARIES := libdvbsettings_jni
LOCAL_PACKAGE_NAME := DvbSettingsPreInstall

LOCAL_CERTIFICATE := platform
LOCAL_STATIC_JAVA_LIBRARIES := CrashHandler DisplaySetting DvbStorage DvbPlayer Hitv NativeProvider jazzlib

LOCAL_REQUIRED_MODULES := DvbStorage libdvbsettings_jni


LOCAL_PROGUARD_ENABLED := full
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)


include $(LOCAL_PATH)/jni/Android.mk
# Use the folloing include to make our test apk.    
include $(call all-makefiles-under,$(LOCAL_PATH))  
 
