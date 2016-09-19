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

static sp<Search> getSearch(JNIEnv* env, jobject thiz)
{
//neil ,need add the auto lock mutex
   LOGV("getSearch was called");
   
    Dvbstack* const p = (Dvbstack*)env->GetIntField(thiz, fields.context);
    if(NULL == p)
    {
       LOGV("could not get the dvb obj from context");
        return NULL;
    }
    return p->getSearch();
}


static jint android_dvb_AutoSearch(JNIEnv *env, jobject thiz, jint type, jint bandwidth,jint Freq, jint SymbolRate, jint QamType)
{
	sp<Search> search = getSearch(env, thiz);
    LOGV("android_dvb_AutoSearch %d %d %d\n", Freq, SymbolRate, QamType);
	return search->autoSearch(type,bandwidth,Freq, SymbolRate, QamType);
}


static jint android_dvb_ManualSearch(JNIEnv *env, jobject thiz,  jint type, jint bandwidth,jint Freq, jint SymbolRate, jint QamType)
{
	sp<Search> search = getSearch(env, thiz);
    LOGV("android_dvb_ManualSearch %d %d %d\n", Freq, SymbolRate, QamType);
	return search->manualSearch(type,bandwidth,Freq, SymbolRate, QamType);
}


static jint android_dvb_FullBandSearch(JNIEnv *env, jobject thiz,  jint type, jint bandwidth,jint SymbolRate, jint QamType)
{
	sp<Search> search = getSearch(env, thiz);
    LOGV("android_dvb_ManualSearch %d %d\n", SymbolRate, QamType);
	return search->fullSearch(type,bandwidth,SymbolRate, QamType);
}

static jint android_dvb_StopSearch(JNIEnv *env, jobject thiz)
{
	sp<Search> search = getSearch(env, thiz);
    LOGV("android_dvb_StopSearch ");
	return search->stopSearch();
}

// ----------------------------------------------------------------------------

static JNINativeMethod gMethods[] = {
    {"AutoSearch",                 "(IIIII)I",                 (void *)android_dvb_AutoSearch},
    {"ManualSearch",               "(IIIII)I",                 (void *)android_dvb_ManualSearch},
    {"FullBandSearch",             "(IIII)I",                 (void *)android_dvb_FullBandSearch},
    {"StopSearch",             	   "()I",                   (void *)android_dvb_StopSearch},
};

static int find_field(JNIEnv *env)
{
    jclass clazz;

    clazz = env->FindClass("com/um/dvbstack/DvbStackSearch");
    if (clazz == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find com/um/dvbstack/DvbStackSearch");
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
int register_android_dvb_Search(JNIEnv *env)
{
	LOGV("register_android_dvb_Search was called");
	if (find_field(env) < 0)
	{
		LOGV("register_android_dvb_Search failed, could not find the filed");
	        return -1;
        }
    return AndroidRuntime::registerNativeMethods(env,
                "com/um/dvbstack/DvbStackSearch", gMethods, NELEM(gMethods));
	
}
	



