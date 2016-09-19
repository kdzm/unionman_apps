/* //device/libs/android_runtime/android_dvb_DVB.cpp
*/
#define LOG_NDEBUG 0
#define LOG_TAG "DVB"

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

// ----------------------------------------------------------------------------

using namespace android;

// ----------------------------------------------------------------------------

struct fields_t {
    jfieldID    context;
    jmethodID   post_event;
};
static fields_t fields;

static Mutex sLock;


static sp<Dvbstack> getDVB(JNIEnv* env, jobject thiz)
{
    Mutex::Autolock l(sLock);
    Dvbstack* const p = (Dvbstack*)env->GetIntField(thiz, fields.context);
    return sp<Dvbstack>(p);
}

static sp<Dvbstack> setDVB(JNIEnv* env, jobject thiz, const sp<Dvbstack>& dvb)
{
    LOGV("setDVB was called");
    Mutex::Autolock l(sLock);
    sp<Dvbstack> old = (Dvbstack*)env->GetIntField(thiz, fields.context);
    if (dvb.get()) {
        dvb->incStrong(thiz);
    }
    if (old != 0) {
        old->decStrong(thiz);
    }
    env->SetIntField(thiz, fields.context, (int)dvb.get());

    LOGV("setDVB dvb obj was set to context %d", (int)dvb.get());
    return old;
}

// This function gets some field IDs, which in turn causes class initialization.
// It is called from a static block in DVB, which won't run until the
// first time an instance of this class is used.
static void android_dvb_DVB_native_init(JNIEnv *env)
{
    jclass clazz;

    clazz = env->FindClass("com/um/dvbstack/DVB");
    if (clazz == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find com/um/dvbstack/DVB");
        return;
    }

    fields.context = env->GetFieldID(clazz, "mNativeContext", "I");
    if (fields.context == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find DVB.mNativeContext");
        return;
    }

}

//This function is used to associate dvb java object and c++ object. 
//set the listener for the dvb object, and also set the dvb obj in the java "context" field.

static void android_dvb_DVB_native_setup(JNIEnv *env, jobject thiz, jobject weak_this)
{
    LOGV("native_setup");
    env->SetIntField(thiz, fields.context, 0);
    
    if (!Dvbstack::isServiceAlive()) {
        jniThrowException(env, "java/lang/IllegalStateException", "Dvbstack service is not ready");
        return;        
    }
    
    sp<Dvbstack> dvb = new Dvbstack();
	
    if (dvb == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Out of memory");
        return;
    }

    // Stow our new C++ DVB in an opaque field in the Java object.
    setDVB(env, thiz, dvb);
}

static void
android_dvb_DVB_release(JNIEnv *env, jobject thiz)
{
    LOGV("release");
    sp<Dvbstack> dvb = setDVB(env, thiz, 0);
	dvb.clear();
}

static void
android_dvb_DVB_native_finalize(JNIEnv *env, jobject thiz)
{
    LOGV("native_finalize");
    android_dvb_DVB_release(env, thiz);
}

static void
android_dvb_DVB_enableStatusListener(JNIEnv *env, jobject thiz, jboolean enable)
{
    LOGV("enableStatusListener(): do nothing now...");
    //sp<Dvbstack> dvb = getDVB(env, thiz);
	//dvb->enableStatusListener(enable);
}

// ----------------------------------------------------------------------------
//the native method need to be registered
static JNINativeMethod gMethods[] = {
    {"native_init",                 "()V",                        (void *)android_dvb_DVB_native_init},
    {"native_setup",                "(Ljava/lang/Object;)V",			(void *)android_dvb_DVB_native_setup},
    {"native_finalize",     "()V",                              (void *)android_dvb_DVB_native_finalize},    
  	{"native_release",     "()V",                              (void *)android_dvb_DVB_release},  
  	{"native_enableStatusListener",     "(Z)V",             (void *)android_dvb_DVB_enableStatusListener}, 
};

// This function only registers the native methods
static int register_android_dvb_DVB(JNIEnv *env)
{
    LOGV("register_android_dvb_DVB was called");
    return AndroidRuntime::registerNativeMethods(env,
                "com/um/dvbstack/DVB", gMethods, NELEM(gMethods));
}

extern int register_android_dvb_Tuner(JNIEnv *env);
extern int register_android_dvb_Search(JNIEnv *env);
extern int register_android_dvb_ProgManage(JNIEnv *env);
extern int register_android_dvb_Prog(JNIEnv *env);
extern int register_android_dvb_Status(JNIEnv *env);
extern int register_android_dvb_Ca(JNIEnv *env);
extern int register_android_dvb_Upgrade(JNIEnv *env);

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;

    LOGV("JNI_OnLoad was called");

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("ERROR: GetEnv failed\n");
        goto bail;
    }
    assert(env != NULL);

    if (register_android_dvb_DVB(env) < 0) {
        LOGE("ERROR: DVB native registration failed\n");
        goto bail;
    }

    if (register_android_dvb_ProgManage(env) < 0) {
            LOGE("ERROR: DVB native register_android_dvb_Search failed\n");
            goto bail;
        }

	 if (register_android_dvb_Tuner(env) < 0) {
        LOGE("ERROR: DVB native registration failed\n");
        goto bail;
		
    }
	 
	if (register_android_dvb_Search(env) < 0) {
        LOGE("ERROR: DVB native register_android_dvb_Search failed\n");
        goto bail;
		
    }
	if (register_android_dvb_Prog(env) < 0) {
        LOGE("ERROR: DVB native register_android_dvb_Search failed\n");
        goto bail;
		
    }    
    
	if (register_android_dvb_Status(env) < 0) {
        LOGE("ERROR: DVB native register_android_dvb_Status failed\n");
        goto bail;
		
    }  
    

    if(register_android_dvb_Ca(env) < 0){
        LOGE("ERROR: DVB native register_android_dvb_Search failed\n");
        goto bail;
    }

    if(register_android_dvb_Upgrade(env) < 0){
        LOGE("ERROR: DVB native register_android_dvb_Upgrade failed\n");
        goto bail;
    }
    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

bail:
    return result;
}


