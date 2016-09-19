#
# Copyright (C) 2008 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# This makefile shows how to build a shared library and an activity that
# bundles the shared library and calls it using JNI.

TOP_LOCAL_PATH:= $(call my-dir)

# Build activity

LOCAL_PATH:= $(TOP_LOCAL_PATH)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-subdir-java-files)

#LOCAL_SRC_FILES += $(call all-java-files-under,gen)
#LOCAL_SRC_FILES += com/cvte/tv/at/service/IRemoteService.aidl
#LOCAL_SRC_FILES += $(call all-subdir-Iaidl-files)

LOCAL_PACKAGE_NAME := cvte-factory-autotest3

#LOCAL_JNI_SHARED_LIBRARIES := libatserialjni
LOCAL_REQUIRED_MODULES := libatserialjni

LOCAL_PROGUARD_ENABLED := disabled

LOCAL_PRIVILEGED_MODULE := true

LOCAL_DEX_PREOPT := false

LOCAL_CERTIFICATE := platform

#LOCAL_SDK_VERSION := current

#import mtk lib
LOCAL_STATIC_JAVA_LIBRARIES := gson
LOCAL_STATIC_JAVA_LIBRARIES +=  Hitv

$(shell rm -rf $(TARGET_OUT)/etc/CVTE_COMMOM_CHANNEL_TABLE)
$(shell mkdir -p $(TARGET_OUT)/etc/CVTE_COMMOM_CHANNEL_TABLE)
$(shell cp -rf $(LOCAL_PATH)/db/* $(TARGET_OUT)/etc/CVTE_COMMOM_CHANNEL_TABLE/)

$(shell rm -rf $(TARGET_OUT)/bin/sys_control)
$(shell mkdir -p $(TARGET_OUT)/bin)
$(shell cp -rf $(LOCAL_PATH)/libs/sys_control $(TARGET_OUT)/bin/sys_control)

#$(shell rm -rf $(TARGET_OUT)/bin/atusbmount_um)
#$(shell mkdir -p $(TARGET_OUT)/bin)
#$(shell cp -rf $(LOCAL_PATH)/libs/atusbmount_um $(TARGET_OUT)/bin/atusbmount_um)

$(shell rm -rf $(TARGET_OUT)/etc/cvteat_key_pad.xml)
$(shell mkdir -p $(TARGET_OUT)/bin)
$(shell cp -rf $(LOCAL_PATH)/libs/cvteat_key_pad.xml $(TARGET_OUT)/etc/cvteat_key_pad.xml)

include $(BUILD_PACKAGE)


################# Add Third Lib #################
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES += gson:libs/gson-2.3.jar
include $(BUILD_MULTI_PREBUILT)

# ============================================================

# Also build all of the sub-targets under this one: the shared library.

define all-libs-under
$(shell cd $(1);find . -name "*.so"; )
endef

define get-lib-names
$(shell find $(1) -name "*.so" -exec basename {} \; | cut -d . -f 1)
endef

include $(CLEAR_VARS)
#$(shell cp -rf $(LOCAL_PATH)/lib/libatserialjni.so $(TARGET_OUT)/lib/libatserialjni.so)
ALL_DEFAULT_INSTALLED_MODULES += $(call get-lib-names, $(LOCAL_PATH))
LOCAL_PREBUILT_LIBS := $(call all-libs-under, $(LOCAL_PATH))
include $(BUILD_MULTI_PREBUILT)
