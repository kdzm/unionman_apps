LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

#before lollipop
ifneq (1,$(filter 1,$(shell echo "$$(( $(PLATFORM_SDK_VERSION) >= 21 ))" )))
    ifeq ($(CFG_HI_CHIP_TYPE),hi3751av500)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a-v19
    else ifeq ($(CFG_HI_CHIP_TYPE),hi3751v500)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a-v19
    else ifeq ($(CFG_HI_CHIP_TYPE),hi3751av320)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a-v19
    else ifeq ($(CFG_HI_CHIP_TYPE),hi3751v320)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a-v19
    else ifeq ($(CFG_HI_CHIP_TYPE),hi3751av510)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a-v19
    else ifeq ($(CFG_HI_CHIP_TYPE),hi3751v510)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a-v19
    else
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a-v19_v600
    endif
else
    ifeq ($(CFG_HI_CHIP_TYPE),hi3751av500)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a
    else ifeq ($(CFG_HI_CHIP_TYPE),hi3751v500)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a
    else ifeq ($(CFG_HI_CHIP_TYPE),hi3751av320)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a
    else ifeq ($(CFG_HI_CHIP_TYPE),hi3751v320)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a
    else ifeq ($(CFG_HI_CHIP_TYPE),hi3751av510)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a
    else ifeq ($(CFG_HI_CHIP_TYPE),hi3751v510)
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a
    else
        LIB_PATH := $(LOCAL_PATH)/libs/armeabi-v7a_v600
    endif
endif

$(shell mkdir -p $(TARGET_OUT)/lib)
$(shell cp -rf $(LIB_PATH)/libgallerycore.so $(TARGET_OUT)/lib/libgallerycore.so)
$(shell cp -rf $(LIB_PATH)/liboffscreenobject.so $(TARGET_OUT)/lib/liboffscreenobject.so)

LOCAL_SRC_FILES := $(call all-java-files-under,src)

LOCAL_PACKAGE_NAME := UMGalleryL
LOCAL_CERTIFICATE := platform
LOCAL_32_BIT_ONLY := true

ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_PACKAGE_NAME)

#LOCAL_SDK_VERSION := current
#LOCAL_JNI_SHARED_LIBRARIES := libgallerycore
#LOCAL_JNI_SHARED_LIBRARIES += liboffscreenobject
LOCAL_STATIC_JAVA_LIBRARIES := gallerycore

LOCAL_MULTILIB := 32

LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := gallerycore:libs/GalleryCore.jar
include $(BUILD_MULTI_PREBUILT)
