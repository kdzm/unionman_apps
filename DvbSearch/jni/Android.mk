LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES:= \
    android_dvb_Tuner.cpp \
    android_dvb_Search.cpp \
    android_dvb.cpp \
	android_dvb_progmanage.cpp\
    android_dvb_status.cpp

LOCAL_SHARED_LIBRARIES := \
    libandroid_runtime \
    libnativehelper \
    libutils \
    libbinder \
    libui \
    libcutils \
    libicuuc

LOCAL_STATIC_LIBRARIES := libdvbstack

LOCAL_PRELINK_MODULE := false

LOCAL_C_INCLUDES += \
    vendor/unionman/frameworks/dvb/prebuilt/dvbstack/inc \
    vendor/unionman/frameworks/dvb/dvbstack/include \
    vendor/unionman/frameworks/dvb/dvbstack/libdvbstack/include \
    frameworks/base/core/jni \
    frameworks/base/media/libDVB \
    external/icu4c/common \
    $(JNI_H_INCLUDE)

#LOCAL_MODULE_PATH:= $(OUT)/temp
LOCAL_MODULE_PATH:= $(TARGET_OUT_VENDOR_SHARED_LIBRARIES)

LOCAL_MODULE:= libdvbsearch_jni

include $(BUILD_SHARED_LIBRARY)

