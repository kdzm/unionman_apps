#define LOG_NDEBUG 0
#define LOG_TAG "PROG"

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

#include <string.h>


#include "Dvbstack.h"

#define NEIL_TEST 1

// ----------------------------------------------------------------------------

using namespace android;

// ----------------------------------------------------------------------------

struct fields_t {
    jfieldID    context;
};
static fields_t fields;


// ----------------------------------------------------------------------------

static sp<Epg> getProg(JNIEnv* env, jobject thiz)
{
//neil ,need add the auto lock mutex
   LOGV("getProg was called");
   
    Dvbstack* const p = (Dvbstack*)env->GetIntField(thiz, fields.context);
    if(NULL == p)
    {
       LOGV("could not get the dvb obj from context");
        return NULL;
    }
    return p->getEpg();
}

static UM_U32 gettick()
{
	struct timeval now;
	gettimeofday(&now, NULL);
	return now.tv_sec*1000000LL + now.tv_usec;
}


static jint android_dvb_Prog_getPF(JNIEnv *env, jobject thiz,jint progid,jbyteArray buf,jint size)
{
	sp<Epg> prog = getProg(env, thiz);

	UM_U32 pflen = size;
	UM_U8 *pfbuf = (UM_U8 *)malloc(pflen);
	if(pfbuf == NULL)
	{
		LOGE("android_dvb_Prog_getPF pfbuf=null\n");
		return -1;
	}

	UM_S32 ret = prog->getPFEvent(progid,pfbuf,pflen);

	if(pfbuf == UM_NULL)
	{
		LOGV("android_dvb_Prog_getPF getPFEvent pbuf is UM_NULL");
	}
	else
	{
		(env)->SetByteArrayRegion(buf, 0, pflen, (const jbyte*)pfbuf);
	}
	
	free(pfbuf);
	pfbuf = NULL;
	//size = pflen;

	return ret;
}

static jint android_dvb_Prog_getWeek(JNIEnv *env, jobject thiz,jint progid,jbyteArray buf,jint size, jint mjd)
{
	sp<Epg> prog = getProg(env, thiz);

	UM_U32 pflen = size;
	UM_U8 *pfbuf = (UM_U8 *)malloc(pflen);
	if(pfbuf == NULL)
	{
		return -1;
	}
	UM_S32 ret = prog->getSchEvent(progid,pfbuf,pflen,mjd);

	(env)->SetByteArrayRegion(buf, 0, pflen, (const jbyte*)pfbuf);

	free(pfbuf);
	pfbuf = NULL;
	size = pflen;
	return ret;
}


static jint android_dvb_Prog_getLocalTime(JNIEnv *env, jobject thiz,jobject LocalTime)
{
	sp<Epg> prog = getProg(env, thiz);

	static UMDVB_TIME_INFO_S tmpDateTime;
	memset(&tmpDateTime, 0, sizeof(tmpDateTime));
	LOGV("android_dvb_Prog_getLocalTime being called");
	UM_S32 ret = prog->getLocalTime(&tmpDateTime);
	jclass clazz =env->FindClass("com/um/dvbstack/Prog$Epg_LocalTime");  
    jfieldID mjd = env->GetFieldID(clazz, "mjd", "I");
    jfieldID year = env->GetFieldID(clazz, "year", "I");
	jfieldID month = env->GetFieldID(clazz, "month", "I");
	jfieldID day = env->GetFieldID(clazz, "day", "I");
	jfieldID weekday = env->GetFieldID(clazz, "weekday", "I");
	jfieldID hour = env->GetFieldID(clazz, "hour", "I");
	jfieldID min = env->GetFieldID(clazz, "min", "I");
	jfieldID sec = env->GetFieldID(clazz, "sec", "I");
	LOGV("android_dvb_Prog_getLocalTime was called");

	env->SetIntField(LocalTime, mjd, tmpDateTime.mjd); 
	env->SetIntField(LocalTime, year, tmpDateTime.year); 
	env->SetIntField(LocalTime, month, tmpDateTime.month);
	env->SetIntField(LocalTime, day, tmpDateTime.day);
	env->SetIntField(LocalTime, weekday, tmpDateTime.weekday);
	env->SetIntField(LocalTime, hour, tmpDateTime.hour); 
	env->SetIntField(LocalTime, min, tmpDateTime.min); 
	env->SetIntField(LocalTime, sec, tmpDateTime.sec); 
	 
	return ret;
}


// ----------------------------------------------------------------------------

static JNINativeMethod gMethods[] = {
    {"getPFEvent",                 "(I[BI)I",                 (void *)android_dvb_Prog_getPF},
    {"getSchEvent",                "(I[BII)I",                 (void *)android_dvb_Prog_getWeek},
	{"getLocalTime",     "(Lcom/um/dvbstack/Prog$Epg_LocalTime;)I",     (void *)android_dvb_Prog_getLocalTime},
};

static int find_field(JNIEnv *env)
{
    jclass clazz;

    clazz = env->FindClass("com/um/dvbstack/Prog");
    if (clazz == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find com/um/dvbstack/Prog");
        return -1;
    }

    fields.context = env->GetFieldID(clazz, "mNativeContext", "I");
    if (fields.context == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find Prog.mNativeContext");
        return -1;
    }

	return 0;
}

// This function only registers the native methods
int register_android_dvb_Prog(JNIEnv *env)
{
	LOGV("register_android_dvb_Prog was called");
	if (find_field(env) < 0)
	{
		LOGV("register_android_dvb_Prog failed, could not find the filed");
	        return -1;
        }
    return AndroidRuntime::registerNativeMethods(env,
                "com/um/dvbstack/Prog", gMethods, NELEM(gMethods));
	
}
	




