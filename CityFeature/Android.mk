LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

SOURCE_DIR := $(LOCAL_PATH)/../../frameworks/dvb/prebuilt/svr2_0/lib/$(TARGET_PRODUCT)
TARGET_DIR := $(LOCAL_PATH)/assets/libs
$(shell mkdir -p $(TARGET_DIR))
$(shell rm -rf $(TARGET_DIR)/*)
$(shell cp -u -rf $(SOURCE_DIR)/lib_umdvb.so $(TARGET_DIR)/)
$(shell cp -u -rf $(SOURCE_DIR)/func_cfg.ini $(TARGET_DIR)/)
$(shell cp -u -rf $(SOURCE_DIR)/debug_cfg.ini $(TARGET_DIR)/)
$(shell cp -u -rf $(SOURCE_DIR)/key_cfg.ini $(TARGET_DIR)/)
LOCAL_ADDITIONAL_DEPENDENCIES := $(ANDROID_UM_CONFIG_FILE)
ifdef CFG_UM_DVB_EXTERNAL_DEMO
$(shell cp -u -rf $(SOURCE_DIR)/umapi_configure_extdemo.ini $(TARGET_DIR)/umapi_configure.ini)
else
$(shell cp -u -rf $(SOURCE_DIR)/umapi_configure.ini $(TARGET_DIR)/)
endif

include $(BUILD_MULTI_PREBUILT)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_MODULE_PATH := $(TARGET_OUT)/vendor/dvb/install_packages/default_city

LOCAL_CERTIFICATE := platform
LOCAL_PACKAGE_NAME := CityFeature
LOCAL_MODULE_TAGS := optional

LOCAL_PROGUARD_FLAG_FILES := proguard.flags
LOCAL_PROGUARD_ENABLED := full

include $(BUILD_PACKAGE)


include $(call all-makefiles-under,$(LOCAL_PATH)) 
