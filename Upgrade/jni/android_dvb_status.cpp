#define LOG_NDEBUG 0
#define LOG_TAG "STATUS"

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
    jmethodID SendMessageMedthodID;
    jmethodID SendMessageMedthodIDData;
    jmethodID AllocByteMedthodIDData;

    int start;
};

static fields_t fields;

static Mutex sLock;

#define NEIL_TEST 1

// ----------------------------------------------------------------------------

class JNIStatusNotifier: public StatusNotifier
{
public:
    JNIStatusNotifier(JNIEnv* env, jobject thiz, jobject weak_thiz);
    ~JNIStatusNotifier();
    virtual void notify(UM_U32 type, UM_U32 param1, UM_U32 param2, UM_U8* ptr);
private:
    JNIStatusNotifier();
    jclass      mClass;     // Reference to Status class
    jobject     mObject;    // Weak ref to Status Java object to call on
};

JNIStatusNotifier::JNIStatusNotifier(JNIEnv* env, jobject thiz, jobject weak_thiz)
{
    ALOGD("=== JNIStatusNotifier constructor ===");
    // Hold onto the Status class for use in calling the static method
    // that posts events to the application thread.
    jclass clazz = env->GetObjectClass(thiz);
    if (clazz == NULL) 
    {
        ALOGE("Can't find com/um/dvbstack/Status");
        jniThrowException(env, "java/lang/Exception", NULL);
        return;
    }
    mClass = (jclass)env->NewGlobalRef(clazz);

    // We use a weak reference so the Status object can be garbage collected.
    // The reference is only used as a proxy for callbacks.
    mObject  = env->NewGlobalRef(weak_thiz);
}

JNIStatusNotifier::~JNIStatusNotifier()
{
    // remove global references
    ALOGD("=== JNIStatusNotifier destructor ===");
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    env->DeleteGlobalRef(mObject);
    env->DeleteGlobalRef(mClass);
}

void JNIStatusNotifier::notify(UM_U32 type, UM_U32 param1, UM_U32 param2, UM_U8* ptr)
{
    JNIEnv *env = AndroidRuntime::getJNIEnv();
    if (ptr == NULL) 
    {
        env->CallStaticVoidMethod(mClass, fields.SendMessageMedthodID, mObject, type, param1, param2);
    }
    else
    {
        jbyteArray barray = NULL;
        barray = (jbyteArray)(env)->CallStaticObjectMethod(mClass, fields.AllocByteMedthodIDData, param2);
        if(barray!=NULL)
		{
			(env)->SetByteArrayRegion(barray, 0, param2, (const jbyte*)ptr);
		}

		LOGV(" ListenStatusData jbyteArray is %p\n",barray);
		env->CallStaticVoidMethod(mClass, fields.SendMessageMedthodIDData, mObject, type, param1, barray, param2);
    }
    
    if (env->ExceptionCheck()) 
    {
        ALOGW("An exception occurred while notifying an event.");
        env->ExceptionClear();
    }
}

static void android_dvb_Status_StartListener(JNIEnv *env, jobject thiz, 
    jobject weak_thiz, jint context, jboolean flag)
{
	LOGV("Start Listening Status(%d)", flag);
    Dvbstack* const dvb = (Dvbstack*)context;
    sp<JNIStatusNotifier> notifier = NULL;
    if (dvb == NULL)
    {
        LOGE("context is null");
        return;
    }
    
    if (flag)
    {
        notifier = new JNIStatusNotifier(env, thiz, weak_thiz);
    }
    
    dvb->setStatusNotifier(notifier);    

	return ;
}

/*
void ListenStatus(int type, int param1, int param2)
{
	if(1 != fields.start)
	{
		return ;
	}
	
	JNIEnv *env = AndroidRuntime::getJNIEnv();

	if(NULL != env)
	{
		(env)->CallStaticVoidMethod(fields.mClass,fields.SendMessageMedthodID,type, param1, param2);
	}
}

void ListenStatusData(UM_U32 type,UM_U32 subtype, UM_U8* data, int len)
{
	if(1 != fields.start)
	{
		return ;
	}
	
	JNIEnv *env = AndroidRuntime::getJNIEnv();

	if(NULL != env)
	{
		jbyteArray barray = NULL;
		if(data!=NULL)
		{
			barray = (jbyteArray)(env)->CallStaticObjectMethod(fields.mClass,fields.AllocByteMedthodIDData,len);
			
			if(barray!=NULL)
			{
				(env)->SetByteArrayRegion(barray, 0, len, (const jbyte*)data);
			}
		}
		LOGV(" ListenStatusData jbyteArray is %p\n",barray);
		(env)->CallStaticVoidMethod(fields.mClass,fields.SendMessageMedthodIDData,type,subtype, barray, len);
	}
}
*/
// ----------------------------------------------------------------------------

static JNINativeMethod gMethods[] = {
    {"startListnerStatus",                 "(Ljava/lang/Object;IZ)V",                 (void *)android_dvb_Status_StartListener},

};

static int find_field(JNIEnv *env)
{
    jclass clazz;

    clazz = env->FindClass("com/um/dvbstack/Status");
    if (clazz == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find com/um/dvbstack/Status");
        return -1;
    }

    fields.SendMessageMedthodID = (env)->GetStaticMethodID(clazz, "SendMessage", 
					"(Ljava/lang/Object;III)V");
    if (fields.SendMessageMedthodID == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find SendMessage");
        return -1;
    }

    fields.SendMessageMedthodIDData = (env)->GetStaticMethodID(clazz, "SendMessage", 
					"(Ljava/lang/Object;II[BI)V");
    if (fields.SendMessageMedthodIDData == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find SendMessage");
        return -1;
    }

    fields.AllocByteMedthodIDData = (env)->GetStaticMethodID(clazz, "AllocData", 
					"(I)[B");
    if (fields.AllocByteMedthodIDData == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find AllocData");
        return -1;
    }


	return 0;
}

// This function only registers the native methods
int register_android_dvb_Status(JNIEnv *env)
{
	LOGV("register_android_dvb_Status was called");
	if (find_field(env) < 0)
	{
		LOGV("register_android_dvb_Status failed, could not find the filed");
	        return -1;
        }
    return AndroidRuntime::registerNativeMethods(env,
                "com/um/dvbstack/Status", gMethods, NELEM(gMethods));
	
}
	



