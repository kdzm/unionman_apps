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
#include "android_util_Binder.h"
#include <binder/Parcel.h>

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

static sp<Tuner> getTuner(JNIEnv* env, jobject thiz)
{
//neil ,need add the auto lock mutex
   LOGV("getTuner was called");
   
    Dvbstack* const p = (Dvbstack*)env->GetIntField(thiz, fields.context);
    if(NULL == p)
    {
       LOGV("could not get the dvb obj from context");
        return NULL;
    }
       LOGV("getTuner was called finish!");

    return p->getTuner();
}


static jint android_dvb_Tuner_Lock(JNIEnv *env, jobject thiz, jint TunerID, jint Freq, jint SymbolRate, jint QamType)
{
	sp<Tuner> tuner = getTuner(env, thiz);
    LOGV("android_dvb_Tuner_Lock %d %d %d %d\n", TunerID, Freq, SymbolRate, QamType);
	return tuner->lock(TunerID, Freq, SymbolRate, QamType);
}




static jint android_dvb_Tuner_GetInfo(JNIEnv *env, jobject thiz, jint TunerID, jobject jvTunerInfo)
{

	sp<Tuner> tuner = getTuner(env, thiz);
	if (tuner == NULL ) {
        jniThrowException(env, "java/lang/IllegalStateException", NULL);
        return UNKNOWN_ERROR;
    }

     LOGV("android_dvb_Tuner_GetInfo, 0");

    static UM_TUNER_TINFO_S stTunerInfo;
    UM_S32 ret = tuner->getInfo(TunerID, &stTunerInfo);

    if(UM_SUCCESS != ret)
    {
         LOGV("android_dvb_Tuner_GetInfo, ERROR!!!");

  //      jniThrowException(env, "java/lang/RuntimeException", "GetInfo error");
 //       return UNKNOWN_ERROR;
    }

    /* set field start */
    jclass clazz =env->FindClass("com/um/dvbstack/Tuner$TunerInfo"); 
    
    jfieldID Quality = env->GetFieldID(clazz, "Quality", "I");
    jfieldID Strength = env->GetFieldID(clazz, "Strength", "I");
    jfieldID Ber = env->GetFieldID(clazz, "Ber", "[I");
    jintArray jarr = (jintArray) env->GetObjectField(jvTunerInfo, Ber); 
    jfieldID CurrFrq = env->GetFieldID(clazz, "CurrFrq", "I");
    jfieldID CurrSym = env->GetFieldID(clazz, "CurrSym", "I");
    jfieldID CurrQam = env->GetFieldID(clazz, "CurrQam", "I");
    jfieldID CurrLockFlag = env->GetFieldID(clazz, "CurrLockFlag", "I");

    
    env->SetIntField(jvTunerInfo, Quality, stTunerInfo.u32Quality); 
    env->SetIntField(jvTunerInfo, Strength, stTunerInfo.u32Strength);
    env->SetIntArrayRegion(jarr, 0, 3, (jint*)(stTunerInfo.u32Ber));
    env->SetIntField(jvTunerInfo, CurrFrq, stTunerInfo.u32CurrFrq); 
    env->SetIntField(jvTunerInfo, CurrSym, stTunerInfo.u32CurrSym); 
    env->SetIntField(jvTunerInfo, CurrQam, stTunerInfo.u32CurrQam); 
    env->SetIntField(jvTunerInfo, CurrLockFlag, stTunerInfo.bCurrLockFlag); 
    /* set field end */

    LOGV("TUNER INFO,QUALITY:%d,strength:%d,ber:%d\n",stTunerInfo.u32Quality,stTunerInfo.u32Strength,stTunerInfo.u32Ber[0]);
    
    return ret;
}

static jint android_dvb_Tuner_GetLockStatus(JNIEnv *env, jobject thiz, jint TunerID ,jintArray LockFlag, jlongArray Freq)
{
	 LOGV("android_dvb_Tuner_GetLockStatus was called");

	sp<Tuner> tuner = getTuner(env, thiz);

	if (tuner == NULL ) {
	 LOGV("android_dvb_Tuner_GetLockStatus,could not get the dvb obj from context");
        jniThrowException(env, "java/lang/IllegalStateException", NULL);
        return UNKNOWN_ERROR;
    }

    jint *pLockFlag = (jint *)env->GetIntArrayElements(LockFlag, 0);
    if(NULL == pLockFlag)
    {
        jniThrowException(env, "java/lang/RuntimeException", "GetArray error");
        return UNKNOWN_ERROR;
    }

    jlong *pFreq = (jlong *)env->GetLongArrayElements(Freq, 0);
    if(NULL == pFreq)
    {
        jniThrowException(env, "java/lang/RuntimeException", "GetArray error");
        return UNKNOWN_ERROR;
    }

    UM_U32  tunerFreq;
    UM_BOOL  tunerLockflag;
   status_t ret = tuner->getLockStatus((UM_U32)TunerID, &tunerLockflag, &tunerFreq); 
   *pLockFlag = (jint)tunerLockflag;
   *pFreq = (jlong)tunerFreq;
    
    env->ReleaseIntArrayElements(LockFlag, pLockFlag, JNI_ABORT);
    env->ReleaseLongArrayElements(Freq, pFreq, JNI_ABORT);

    return ret;
}

static jint android_dvb_Tuner_DispalayPanel(JNIEnv *env, jobject thiz,jbyteArray char_array,  jint len)
{
	 LOGV("android_dvb_Tuner_DispalayPanel");
	UM_U32  ret;
	UM_U32 i;
	sp<Tuner> tuner = getTuner(env, thiz);
	if (tuner == NULL ) {
	 	LOGV("android_dvb_Tuner_DispalayPanel,could not get the dvb obj from context");
        jniThrowException(env, "java/lang/IllegalStateException", NULL);
        return UNKNOWN_ERROR;
    }
	jbyte* buffer = env->GetByteArrayElements(char_array,0);
	buffer[len]='\0';
	LOGV("buffer is :%s\n",buffer);
	ret	 = tuner->displayPanel((UM_U8*)buffer,len); 
	return ret;
}

static jint android_dvb_Tuner_GetType(JNIEnv *env, jobject thiz)
{
	UM_U32  ret;
	sp<Tuner> tuner = getTuner(env, thiz);
	if (tuner == NULL ) {
		LOGV("android_dvb_Tuner_GetType,could not get the dvb obj from context");
        jniThrowException(env, "java/lang/IllegalStateException", NULL);
        return UNKNOWN_ERROR;
    }
	ret = tuner->getType();
	return ret;
}
static jint android_dvb_Tuner_SetType(JNIEnv *env, jobject thiz, jint type)
{
    UM_U32  ret;
	sp<Tuner> tuner = getTuner(env, thiz);
	if (tuner == NULL ) {
		LOGV("android_dvb_Tuner_SetType,could not get the dvb obj from context");
        jniThrowException(env, "java/lang/IllegalStateException", NULL);
        return UNKNOWN_ERROR;
    }
	ret = tuner->setType(type);
	return ret;
}
// ----------------------------------------------------------------------------

static JNINativeMethod gMethods[] = {
    {"Lock",                 "(IIII)I",                 (void *)android_dvb_Tuner_Lock},
    {"GetInfo",              "(ILcom/um/dvbstack/Tuner$TunerInfo;)I", (void *)android_dvb_Tuner_GetInfo},
    {"GetLockStatus",        "(I[I[J)I",                    (void *)android_dvb_Tuner_GetLockStatus},
    {"DisplayPanel",        "([BI)I",                    (void *)android_dvb_Tuner_DispalayPanel},
    {"GetType",              "()I",                 (void *)android_dvb_Tuner_GetType},
    {"SetType",              "(I)I",                 (void *)android_dvb_Tuner_SetType},
};

static int find_field(JNIEnv *env)
{
    jclass clazz;

    clazz = env->FindClass("com/um/dvbstack/Tuner");
    if (clazz == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find com/um/dvbstack/Tuner");
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
int register_android_dvb_Tuner(JNIEnv *env)
{
	LOGV("register_android_dvb_Tuner was called");
	if (find_field(env) < 0)
	{
		LOGV("register_android_dvb_Tuner failed, could not find the filed");
	        return -1;
        }
    return AndroidRuntime::registerNativeMethods(env,
                "com/um/dvbstack/Tuner", gMethods, NELEM(gMethods));
	
}
	


