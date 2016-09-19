#define LOG_NDEBUG 0
#define LOG_TAG "DvbSearch"

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


#include "Dvbstack.h"


#define NEIL_TEST 1

// ----------------------------------------------------------------------------

using namespace android;

// ----------------------------------------------------------------------------

struct fields_t {
    jfieldID    context;
};
static fields_t fields;

static Mutex sLock;
// ----------------------------------------------------------------------------

static sp<Upgrade> getUpgrade(JNIEnv* env, jobject thiz)
{
//neil ,need add the auto lock mutex
   LOGV("getUpgrade was called");
   
    Dvbstack* const p = (Dvbstack*)env->GetIntField(thiz, fields.context);
    if(NULL == p)
    {
       LOGV("could not get the dvb obj from context");
        return NULL;
    }
    return p->getUpgrade();
}


static jint android_dvb_UpgradeStart(JNIEnv *env, jobject thiz)
{
	sp<Upgrade> upgrade = getUpgrade(env, thiz);
    LOGV("android_dvb_UpgradeStart !\n");
	return upgrade->startUpgrade();
}


static jint android_dvb_UpgradeProcess(JNIEnv *env, jobject thiz, jint type, jint bupgrade,jint freq,jint symbol,jint qam,jint pid)
{
	sp<Upgrade> upgrade = getUpgrade(env, thiz);
    LOGV("android_dvb_UpgradeProcess %d %d %d %d %d %d\n", type, bupgrade,freq,symbol,qam,pid);
	return upgrade->startUpgrade(type,bupgrade,freq,symbol,qam,pid);
}


// ----------------------------------------------------------------------------

static JNINativeMethod gMethods[] = {
    {"UpgradeStart",                 "()I",                 (void *)android_dvb_UpgradeStart},
    {"UpgradeProcess",               "(IIIIII)I",                 (void *)android_dvb_UpgradeProcess},
};

static int find_field(JNIEnv *env)
{
    jclass clazz;

    clazz = env->FindClass("com/um/dvbstack/Upgrade");
    if (clazz == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find com/um/dvbstack/Upgrade");
        return -1;
    }

    fields.context = env->GetFieldID(clazz, "mNativeContext", "I");
    if (fields.context == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find Tuner.mNativeContext");
        return -1;
    }

	return 0;
}

// This function only registers the native methods
int register_android_dvb_Upgrade(JNIEnv *env)
{
	LOGV("register_android_dvb_Upgrade was called");
	if (find_field(env) < 0)
	{
		LOGV("register_android_dvb_Upgrade failed, could not find the filed");
	        return -1;
        }
    return AndroidRuntime::registerNativeMethods(env,
                "com/um/dvbstack/Upgrade", gMethods, NELEM(gMethods));
	
}
	




