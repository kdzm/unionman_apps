#define LOG_NDEBUG 0
#define LOG_TAG "PROGMANAGE"

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
#include <android_os_Parcel.h>
#include "um_basic_types.h"

#include "unicode/ucnv.h"
#include "unicode/ustring.h"


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

static sp<ProgManage> getProgManage(JNIEnv* env, jobject thiz)
{
//neil ,need add the auto lock mutex
   LOGV("getProgManage was called");
   
    Dvbstack* const p = (Dvbstack*)env->GetIntField(thiz, fields.context);
    if(NULL == p)
    {
       LOGV("could not get the dvb obj from context");
        return NULL;
    }
    return p->getProgManage();
}

static UM_U32 gettick()
{
	struct timeval now;
	gettimeofday(&now, NULL);
	return now.tv_sec*1000000LL + now.tv_usec;
}

static int convert_gb2312_to_unicode(char *src, unsigned short *buff, int size)
{
    UConverter* conv;
    UErrorCode status = U_ZERO_ERROR; //ICU的错误码
    int len = -1;
    //设置转换器，如果不成功则conv== NULL status返回错误码
    conv = ucnv_open("gb2312", &status);
    if(U_FAILURE(status) || conv == NULL) 
    {
        return -1;
    }

    // 从conv设置的字符编码转换为unicode字符编码
    len = ucnv_toUChars(conv, buff, size, src, strlen(src), &status);
    if (U_FAILURE(status)) 
    {
        LOGV("FAIL: ucnv_convert() failed, error=%s", u_errorName(status));
        len = -1;
    }
    //判断是否转换成功
    //释放资源
    ucnv_close(conv);
    
    return len;
    
}

static int get_unistr_len(UM_U16 *str, UM_U32 size) 
{
    int i = 0;
    int max_len = size >> 1;
    while (*str++ && (i < max_len)) {
        i++;
    }
    return i;
}

static jint android_dvb_ProgManage_getDvbProgList(JNIEnv *env, jobject thiz, jobject proglist)
{
    LOGV("android_dvb_ProgManage_getDvbProgList \n");
    UM_PROG_LIST_S plist;
    UM_S32 ret;

	jclass ProgClazz;
	jclass ProgListClazz;
	jfieldID  progidID;
    jfieldID  service_type;
	jfieldID  prognameID;
	jfieldID  bouquetsID;
	jfieldID  tsidID;
	jfieldID  serviceidID;
	jobject progObject;    
	jobject prog;
	jobjectArray progarray;
	
	sp<ProgManage> pm = getProgManage(env, thiz);

	memset(&plist, 0, sizeof(plist));
	ret =  pm->getProgList(&plist);
	if(UM_SUCCESS == ret)
	{
        ProgListClazz = (env)->GetObjectClass(proglist);

        jmethodID addMethodID = (env)->GetMethodID(ProgListClazz, "add", 
        					"(Ljava/lang/Object;)Z");
        						
		if(addMethodID == NULL)
		{
       		 LOGE("addMethodID is NULL \n");
       		 return -1;
		}

        jclass cls = env->FindClass("com/um/dvbstack/ProgManage"); 

		if(cls == NULL)
		{
       		 LOGE("cls  is NULL \n");
    		 return -1;

		}
		
        jmethodID createMethodID = 
        	(env)->GetMethodID(cls, "CreateProgList","(I)[Lcom/um/dvbstack/Prog;");
        						
		if(createMethodID == NULL)
		{
       		 LOGE("createMethodID is NULL \n");
       		 return -1;
		}
		 LOGV("call createMethodID \n");

		 jobjectArray  progsObject = NULL;
		progsObject = 
			(jobjectArray)(env)->CallObjectMethod(thiz,createMethodID,(jsize)plist.u32count);
		if(progsObject == NULL)
		{
			LOGV("progsObject is NULL");
			return -1;
		}
		ProgClazz = env->FindClass("com/um/dvbstack/Prog");
		if(ProgClazz == NULL)
		{
			 LOGE("FIND com/um/dvbstack/ProgManage/Prog IS NULL" );
			return -1;
		}
        
        for (UM_U32 i = 0; i < plist.u32count; i++)
        {

       		//LOGI("Add obj %d \n",i);
			UM_PROG_INFO_S *prog_info = &plist.pstprog_info[i];

			progObject = (env)->GetObjectArrayElement( progsObject,i); 

			if(progObject == NULL)
			{
       			 LOGE("GetObjectArrayElement IS NULL" );
       			 return -1;
		    }
			progidID = env->GetFieldID(ProgClazz, "progid", "I");
			tsidID = env->GetFieldID(ProgClazz, "tsId", "I");
			serviceidID = env->GetFieldID(ProgClazz, "serviceId", "I");
			prognameID = env->GetFieldID(ProgClazz, "progname", "Ljava/lang/String;");
            service_type = env->GetFieldID(ProgClazz,"service_type","I");
		    env->SetIntField(progObject, progidID, (jint)prog_info->u32prog_id);
		    env->SetIntField(progObject, tsidID, (jint)prog_info->u16tsid);
		    env->SetIntField(progObject, serviceidID, (jint)prog_info->u16serviceid);
            env->SetIntField(progObject, service_type,(jint)prog_info->service_type);
            int namelen = get_unistr_len((UM_U16*)prog_info->u8servicename, sizeof(prog_info->u8servicename))*2;
			jbyteArray bytename=(env)->NewByteArray(namelen);
			(env)->SetByteArrayRegion(bytename, 0,
						namelen,
						(jbyte*)prog_info->u8servicename);   
						
	        jmethodID setNameMethodID = 
	        	(env)->GetMethodID(ProgClazz, "setName","([B)V");

			(env)->CallVoidMethod(progObject,setNameMethodID,bytename);
			(env)->DeleteLocalRef(bytename);

	        if (plist.bouquet_cnt > 0)
	        {
	            bouquetsID = env->GetFieldID(ProgClazz, "bouquetNames", "[Ljava/lang/String;");
                jclass objClass = env->FindClass("java/lang/String");
                jobjectArray bouquets = env->NewObjectArray(plist.bouquet_cnt, objClass, 0);
                
                //LOGV("plist.bouquet_cnt=%d, %s", plist.bouquet_cnt, plist.bouquet_name[0]);
                for (int j=0; j<plist.bouquet_cnt; j++)
                {
                	jstring jstr = NULL;
                    if (prog_info->bouquet_flag[j])
                    {                        
                        //LOGV("bouquet_flag[%d]=%d", j, prog_info->bouquet_flag[j]);
                        unsigned short unistr[512];
                        int len = convert_gb2312_to_unicode((char*)plist.bouquet_name[j], 
                            unistr, sizeof(unistr)/sizeof(unistr[0]));
                        LOGV("convert to unicode, len=%d", len);
                        if (len > 0)
                        {
                            jstr = env->NewString(unistr, len);
                        }
                        else
                        {
                            LOGV("convert to uni failed");
                        }
                    }
                    else
                    {
                        jstr = NULL;
                    }
                    
                    if (jstr != NULL) {
                    	env->SetObjectArrayElement(bouquets, j, jstr);
                    	env->DeleteLocalRef(jstr);
                    }

                }
			    
			    if (bouquets != NULL) {
			    	env->SetObjectField(progObject, bouquetsID, bouquets);
					env->DeleteLocalRef(bouquets);
                }
			}

			(env)->CallBooleanMethod(proglist,addMethodID,progObject);
			if (progObject != NULL) {
				env->DeleteLocalRef(progObject);
			}
		}
		if (ProgClazz != NULL) {
			env->DeleteLocalRef(ProgClazz);
		}
	}
	pm->destroyProgList(&plist);
	return ret;
}


static jint android_dvb_ProgManage_resetDvbProgList(JNIEnv *env, jobject thiz)
{
    LOGV("android_dvb_ProgManage_resetDvbProgList \n");
    UM_S32 ret;
    
	sp<ProgManage> pm = getProgManage(env, thiz);

	ret =  pm->resetProgList();
	LOGV("android_dvb_ProgManage_resetDvbProgList finished!\n");
	return ret;

	
}

static jint android_dvb_ProgManage_invoke(JNIEnv *env, jobject thiz,
                                 jobject java_request, jobject java_reply)
{
    UM_S32 ret;
    
	sp<ProgManage> pm = getProgManage(env, thiz);
	
    Parcel *request = parcelForJavaObject(env, java_request);
    Parcel *reply = parcelForJavaObject(env, java_reply);

    return /*pm->invoke(*request, reply)*/-1;
}

// ----------------------------------------------------------------------------

static JNINativeMethod gMethods[] = {
    {"getDvbProgList",                 "(Ljava/util/ArrayList;)I",                 (void *)android_dvb_ProgManage_getDvbProgList},
    {"resetDvbProgList",                 "()I",                 (void *)android_dvb_ProgManage_resetDvbProgList},
	//{"native_invoke",                 "(Landroid/os/Parcel;Landroid/os/Parcel;)I", (void *)android_dvb_ProgManage_invoke},
};

static int find_field(JNIEnv *env)
{
    jclass clazz;

    clazz = env->FindClass("com/um/dvbstack/ProgManage");
    if (clazz == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find com/um/dvbstack/ProgManage");
        return -1;
    }

    fields.context = env->GetFieldID(clazz, "mNativeContext", "I");
    if (fields.context == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find ProgManage.mNativeContext");
        return -1;
    }
    
	return 0;
}

// This function only registers the native methods
int register_android_dvb_ProgManage(JNIEnv *env)
{
	LOGV("register_android_dvb_ProgManage was called");
	if (find_field(env) < 0)
	{
		LOGV("register_android_dvb_ProgManage failed, could not find the filed");
	        return -1;
        }
    return AndroidRuntime::registerNativeMethods(env,
                "com/um/dvbstack/ProgManage", gMethods, NELEM(gMethods));
	
}
	



