LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files) \
    src/com/um/controller/frequentprog/FrequentCounterInterface.aidl

LOCAL_MODULE_PATH := $(TARGET_OUT)/vendor/dvb/install_packages/default_city

LOCAL_JAVA_LIBRARIES :=
LOCAL_STATIC_JAVA_LIBRARIES := CrashHandler DisplaySetting DvbStorage DvbPlayer Hitv NativeProvider jazzlib

LOCAL_JNI_SHARED_LIBRARIES := libdvbjni

LOCAL_PACKAGE_NAME := UmDvb


LOCAL_CERTIFICATE := platform
LOCAL_REQUIRED_MODULES := libdvbjni

LOCAL_PROGUARD_ENABLED := full
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)


include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files) \
    src/com/um/controller/frequentprog/FrequentCounterInterface.aidl

LOCAL_MODULE_PATH := $(TARGET_OUT_VENDOR_APPS)

LOCAL_JAVA_LIBRARIES :=
LOCAL_STATIC_JAVA_LIBRARIES := CrashHandler DisplaySetting DvbStorage DvbPlayer Hitv NativeProvider jazzlib

LOCAL_JNI_SHARED_LIBRARIES := libdvbjni

LOCAL_PACKAGE_NAME := UmDvbPreInstall


LOCAL_CERTIFICATE := platform
LOCAL_REQUIRED_MODULES := libdvbjni

LOCAL_PROGUARD_ENABLED := full
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

include $(LOCAL_PATH)/jni/Android.mk
include vendor/unionman/prebuilts/Android.mk
# Use the folloing include to make our test apk.    
include $(call all-makefiles-under,$(LOCAL_PATH))  
 
