/* //device/libs/android_runtime/android_dvb_DVB.cpp
*/
#define LOG_NDEBUG 0
#define LOG_TAG "DvbServerManager_jni"

#include "utils/Log.h"

#include <stdio.h>
#include <assert.h>
#include <limits.h>
#include <unistd.h>
#include <fcntl.h>
#include <utils/threads.h>
#include "jni.h"
#include "JNIHelp.h"
#include "android_runtime/AndroidRuntime.h"
#include "utils/Errors.h"  // for status_t
#include "utils/KeyedVector.h"
#include "utils/String8.h"


#include "DvbServerManager.h"

// ----------------------------------------------------------------------------

using namespace android;

// ----------------------------------------------------------------------------

struct fields_t {
    jfieldID    context;
    jmethodID   post_event;
};
static fields_t fields;

static Mutex sLock;



static void
android_DvbServerManager_killService(JNIEnv *env, jobject thiz, jboolean enable)
{
    ALOGV("android_DvbServerManager_killService()");
    DvbServerManager::killService();
}

// ----------------------------------------------------------------------------
//the native method need to be registered
static JNINativeMethod gMethods[] = {
    {"native_killService",                 "()V",                        (void *)android_DvbServerManager_killService},
};

// This function only registers the native methods
static int register_android_DvbServerManager(JNIEnv *env)
{
    ALOGV("register_android_dvb_DVB was called");
    return AndroidRuntime::registerNativeMethods(env,
                "com/unionman/dvbserver/DvbServerManager", gMethods, NELEM(gMethods));
}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;

    ALOGV("JNI_OnLoad was called");

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("ERROR: GetEnv failed\n");
        goto bail;
    }
    assert(env != NULL);

    if (register_android_DvbServerManager(env) < 0) {
        ALOGE("ERROR: DVB native registration failed\n");
        goto bail;
    }

    
    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

bail:
    return result;
}


