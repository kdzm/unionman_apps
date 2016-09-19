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
#include "android_os_Parcel.h"

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
void printfbuff(UM_U8 *buff,UM_U32 len)
{
    UM_U32 i=0;
    for(i=0; i<len; i++)
    {
        LOGV("0x%x 0x%x 0x%x 0x%x 0x%x 0x%x 0x%x 0x%x",buff[i],buff[i+1],buff[i+2],buff[i+3],buff[i+4],buff[i+5],buff[i+6],buff[i+7]);
        i += 8;
    }
}

static sp<Ca> getCa(JNIEnv* env, jobject thiz)
{
//neil ,need add the auto lock mutex
    LOGV("getCa was called");

    Dvbstack* const p = (Dvbstack*)env->GetIntField(thiz, fields.context);
    if(NULL == p)
    {
        LOGV("getCa could not get the dvb obj from context");
        return NULL;
    }
    if(p->getCa() == NULL)
    {
        LOGV("getCa p->getCa() ...== NULL");
    }

    return p->getCa();
}

static jint android_dvb_Ca_get_ic_no(JNIEnv *env, jobject thiz, jint CaID,jobject jvCardNo)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_ic_no was called");

    UM_CARD_NO_S stCardNo;
    UM_S32 ret = ca->getIcNo(CaID, &stCardNo);

    jclass clazz = env->FindClass("com/um/dvbstack/Ca$Card_No");
    jfieldID cardno = env->GetFieldID(clazz, "cardno", "[B");

    jbyteArray jarr = (jbyteArray) env->GetObjectField(jvCardNo, cardno);
    env->SetByteArrayRegion(jarr, 0, CARD_NUMBER, (const jbyte*)(stCardNo.u8Car_no));

    return UM_SUCCESS;
}

static jint android_dvb_Ca_get_pair_status(JNIEnv *env, jobject thiz, jint CaID)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_CARD_NO_S stCardNo;
    UM_S32 ret = ca->getPairStatus(CaID);
    LOGV("Ca_get_pair_status ret:%d\n",ret);

    return ret;
}

static jint android_dvb_Ca_get_ca_version(JNIEnv *env, jobject thiz, jobject jvCaVersion)
{
	
	sp<Ca> ca = getCa(env,thiz);
	
	if(ca == NULL)
	{
		jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
		return UNKNOWN_ERROR;
	}

	UM_S32 ret;
	UM_CA_VERSION_S ca_version;
	ret = ca->getVersion(&ca_version);
	LOGV("Ca_get_pair_status u8ca_version:%s\n",ca_version.u8ca_version);

	jclass clazz = env->FindClass("com/um/dvbstack/Ca$Ca_Version");

	jfieldID caversion = env->GetFieldID(clazz, "caversion", "[B");
	jbyteArray jarr = (jbyteArray) env->GetObjectField(jvCaVersion, caversion); 

	env->SetByteArrayRegion(jarr, 0, CA_VERSION, (const jbyte*)(ca_version.u8ca_version));
    
	return ret;
}

static jint android_dvb_Ca_get_ca_rating(JNIEnv *env, jobject thiz, jobject jvCaRating)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_ca_rating was called");

    UM_CA_RATING_S stCaRating;
    UM_S32 ret = ca->getRating(&stCaRating);

    jclass clazz = env->FindClass("com/um/dvbstack/Ca$Ca_Rating");
    jfieldID carating = env->GetFieldID(clazz, "carating", "[I");

    jintArray jarr = (jintArray) env->GetObjectField(jvCaRating, carating);
    env->SetIntArrayRegion(jarr, 0, CA_RATING, (const jint*)(stCaRating.u32rating));


    return UM_SUCCESS;
}

static jint android_dvb_Ca_get_ca_email_icon(JNIEnv *env, jobject thiz, jobject jvCaEmailIcon)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_ca_email_icon was called");

    UM_CA_EMAIL_ICON_S stCaEmailIcon;
    UM_S32 ret = ca->getEmailIcon(&stCaEmailIcon);

    jclass clazz = env->FindClass("com/um/dvbstack/Ca$Ca_EmailIcon");
    jfieldID caEmailIcon = env->GetFieldID(clazz, "caEmailIcon", "[I");

    jintArray jarr = (jintArray) env->GetObjectField(jvCaEmailIcon, caEmailIcon);
    env->SetIntArrayRegion(jarr, 0, CA_EMAILICON, (const jint*)(stCaEmailIcon.u32emailIcon));


    return UM_SUCCESS;
}

static jint android_dvb_Ca_get_ca_detitle_icon(JNIEnv *env, jobject thiz, jobject jvCaDetitleIcon)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_ca_detitle_icon was called");

    UM_CA_DETITLE_ICON_S stCaDetitleIcon;
    UM_S32 ret = ca->getDetitleIcon(&stCaDetitleIcon);

    jclass clazz = env->FindClass("com/um/dvbstack/Ca$Ca_DetitleIcon");
    jfieldID caDetitleIcon = env->GetFieldID(clazz, "caDetitleIcon", "[I");

    jintArray jarr = (jintArray) env->GetObjectField(jvCaDetitleIcon, caDetitleIcon);
    env->SetIntArrayRegion(jarr, 0, CA_DETITLEICON, (const jint*)(stCaDetitleIcon.u32detitleIcon));


    return UM_SUCCESS;
}	

static jint android_dvb_Ca_restore_msg_send(JNIEnv *env, jobject thiz)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_restore_msg_send was called");

    UM_S32 ret = ca->restoreMsgSend();
	
    return ret;
}	

static jint android_dvb_Ca_get_ca_card_status(JNIEnv *env, jobject thiz, jbooleanArray jvCaCardStatus)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_ca_card_status was called");

	UM_S32 ret = UM_FAILURE;
    UM_BOOL caCardStatus;
    ret = ca->getCardStatus(&caCardStatus);

	if(ret == UM_SUCCESS)
    {  
        LOGV("android_dvb_Ca_get_ca_card_status card status:%d\n", caCardStatus);
        (env)->SetBooleanArrayRegion(jvCaCardStatus,0,1,(const jboolean*)&caCardStatus);
    }

    return ret;
}

static jint android_dvb_Ca_get_work_time(JNIEnv *env, jobject thiz, jbyteArray jworkTimeBuff, jintArray jbuffLen)
{

	UM_S32 ret = UM_FAILURE;
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));

	LOGV("android_dvb_Ca_get_work_time buff_len:%d\n", buff_len);	

	UM_U8 *buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_work_time malloc fail\n");
		return ret;
	}
	memset(buff, 0, buff_len);
		
    ret =  ca->getWorkTime(buff, &buff_len);
    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jworkTimeBuff, 0, buff_len,(const jbyte *)buff);
    }
	
	free(buff);
	buff = NULL;

    return ret;
}

static jint android_dvb_Ca_get_pair_info_check(JNIEnv *env, jobject thiz, jbyteArray jpairInfoBuff, jintArray jbuffLen)
{
	UM_S32 ret = UM_FAILURE; 
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }

	UM_U8 *buff = NULL;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_pair_info_check malloc fail\n");
		return ret;
	}
	
	memset(buff, 0, buff_len);	
    ret = ca->getPairInfo(buff, &buff_len);

    if((CA_SUCCESS  == ret)||(CA_ERR_CARD_PAIROTHER == ret) ||(CA_ERR_CARD_NOPAIR == ret))
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jpairInfoBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return ret;
}

static jint android_dvb_Ca_get_platform_id(JNIEnv *env, jobject thiz)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_S32 platform_id = ca->getPlatformId();
    LOGV("Ca_get_pair_status platform_id:%d\n",platform_id);

    return platform_id;
}

static jint android_dvb_Ca_verify_pin(JNIEnv *env, jobject thiz, jbyteArray jvCaPin, jint pinlen)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_S32 ret=UM_FAILURE;
    UM_U32 i=0;

    UM_CA_PIN_S ca_pin;
    ca_pin.u32Len = pinlen;
    jbyte *pin = (env)->GetByteArrayElements(jvCaPin,0);
    memcpy(ca_pin.au8Pswd,pin,MAX_PINPWD_LEN);

	for(i=0; i<(UM_U32)pinlen; i++)
    {
        ca_pin.au8Pswd[i] -= ASCII_0;
    }

    ret = ca->verifyPin(&ca_pin);
    LOGV("Ca_verify_pin ret:%d\n",ret);

    return ret;
}

static jint android_dvb_Ca_change_pin(JNIEnv *env, jobject thiz, jbyteArray jvNewPin, jbyteArray jvOldPin, jint pinlen)
{
    sp<Ca> ca = getCa(env,thiz);
    LOGV("android_dvb_Ca_change_pin :\n");
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_S32 ret=UM_FAILURE;
    UM_U32 i=0;

    UM_CA_CHANGE_PIN ca_pin;
    ca_pin.u32len = pinlen;

    jbyte *newpin = (env)->GetByteArrayElements(jvNewPin,0);
    jbyte *oldpin = (env)->GetByteArrayElements(jvOldPin,0);

    memcpy(ca_pin.new_pin,newpin,pinlen);

    memcpy(ca_pin.old_pin,oldpin,pinlen);
    for(i=0; i<(UM_U32)pinlen; i++)
    {
        ca_pin.new_pin[i] -= ASCII_0;
        ca_pin.old_pin[i] -= ASCII_0;
    }

    ret = ca->changePin(&ca_pin);
    LOGV("Ca_change_pin ret:%d\n",ret);

    return ret;
}

static jint android_dvb_Ca_set_rating(JNIEnv *env, jobject thiz, jint rating, jbyteArray jvPwd, jint pinlen)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_S32 ret=UM_FAILURE;
    UM_U32 i=0;

    UM_CA_SET_RATING ca_set_rate;/*4-18*/
    ca_set_rate.rating = rating;
    jbyte *pwd = (env)->GetByteArrayElements(jvPwd,0);
    memcpy(ca_set_rate.pwd,(UM_U8*)pwd,MAX_PINPWD_LEN);
    for(i=0; i<(UM_U32)pinlen; i++)
    {
        ca_set_rate.pwd[i] -= ASCII_0;
    }

    ca_set_rate.len = pinlen;
    ret = ca->setRating(&ca_set_rate);

    LOGV("Ca_set_rating ret:%d\n",ret);

    return ret;
}

static jint android_dvb_Ca_Set_working_time(JNIEnv *env, jobject thiz, jbyteArray jvSworktime, jbyteArray jvEworktime,jbyteArray jvpin,jint len)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_S32 ret=UM_FAILURE;
    UM_U32 i=0;
    jbyte *swork_time = (env)->GetByteArrayElements(jvSworktime,0);

    jbyte *ework_time = (env)->GetByteArrayElements(jvEworktime,0);

    UM_CA_WORK_TIME work_time;/*23:59 Ҫ���ƣ�������00*/
    memset(&work_time,0,sizeof(UM_CA_WORK_TIME));
    work_time.u8shour = (swork_time[0]-ASCII_0)*10+(swork_time[1]-ASCII_0);
    work_time.u8smin = (swork_time[2]-ASCII_0)*10+(swork_time[3]-ASCII_0);
	work_time.u8ssec = (swork_time[4]-ASCII_0)*10+(swork_time[5]-ASCII_0);
    work_time.u8ehour = (ework_time[0]-ASCII_0)*10+(ework_time[1]-ASCII_0);
    work_time.u8emin = (ework_time[2]-ASCII_0)*10+(ework_time[3]-ASCII_0);
	work_time.u8esec = (ework_time[4]-ASCII_0)*10+(ework_time[5]-ASCII_0);

	LOGV("Set_working_time,start,h:%d,m:%d,s:%d\n", work_time.u8shour, work_time.u8smin, work_time.u8ssec);
	LOGV("Set_working_time,end,h:%d,m:%d,s:%d\n", work_time.u8ehour, work_time.u8emin, work_time.u8esec);
	
    jbyte *pwd = (env)->GetByteArrayElements(jvpin,0);
    memcpy(work_time.pwd,(UM_U8*)pwd,MAX_PINPWD_LEN);
    for(i=0; i<(UM_U32)len; i++)
    {
        work_time.pwd[i] -= ASCII_0;
    }

    work_time.len = len;
    ret = ca->setWorkTime(&work_time);

    return ret;
}

static jint android_dvb_Ca_get_operatorids(JNIEnv *env, jobject thiz, jintArray jvOperatorid, jintArray jvoperatornum)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    LOGV("android_dvb_Ca_get_operatorids \n");

    UM_CA_OPERATORID operatorid;
    UM_S32 ret=UM_FAILURE;

    ret = ca->getOperatorIds(&operatorid);
    LOGV("android_dvb_Ca_get_operatorids ret:%d\n",ret);
    if(ret == UM_SUCCESS)
    {
        LOGV("android_dvb_Ca_get_operatorids success\n");
        (env)->SetIntArrayRegion(jvOperatorid,0,CA_MAX_OPERATOR_COUNT,(const jint*)operatorid.operator_id);
        LOGV("android_dvb_Ca_get_operatorids operator_id:%d\n",operatorid.operator_id[0]);
        LOGV("android_dvb_Ca_get_operatorids count:%d\n",operatorid.count);
        (env)->SetIntArrayRegion(jvoperatornum,0,1,(const jint*)&operatorid.count);
    }

    return ret;
}

static jint android_dvb_Ca_get_operator_info(JNIEnv *env, jobject thiz, jint Operatorid, jbyteArray jvoperatorinfo)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_CA_OPERATOR_INFO operator_info;
    UM_S32 ret=UM_FAILURE;

    ret = ca->getOperatorInfo((UM_U32)Operatorid,&operator_info);
    if(ret == UM_SUCCESS)
    {
        (env)->SetByteArrayRegion(jvoperatorinfo,0,CA_TVS_INFO_LEN_MAX,(const jbyte*)operator_info.as8TvsPriInfo);
    }

    return ret;
}

/*
static jint android_dvb_Ca_get_dvn_entitles(JNIEnv *env, jobject thiz,jbyteArray jentitleBuf, jintArray jbuffLen)
{
    sp<Ca> ca = getCa(env,thiz);
	
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }
	
    UM_S32 ret = UM_FAILURE;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));

	LOGV("android_dvb_Ca_get_dvn_entitles,buff_len:%d\n", buff_len);	

	UM_U8 *buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_dvn_entitles malloc fail\n");
		return ret;
	}
	memset(buff, 0, buff_len);
	LOGV("android_dvb_Ca_get_dvn_entitles2222,buff_len:%d\n", buff_len);	
    ret =UM_SUCCESS;//  ca->Ca_get_dvn_entitles( buff, &buff_len);
    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jentitleBuf, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return ret;
}
*/

static jint android_dvb_Ca_get_entitles(JNIEnv *env, jobject thiz, jint operid, jbyteArray jentitleBuf, jintArray jbuffLen)
{
    sp<Ca> ca = getCa(env,thiz);
	
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }
	
    UM_S32 ret = UM_FAILURE;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));

	LOGV("android_dvb_Ca_get_work_time,buff_len:%d\n", buff_len);	

	UM_U8 *buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_entitles malloc fail\n");
		return ret;
	}
	memset(buff, 0, buff_len);
		
    ret =  ca->getEntitles((UM_U32) operid, buff, &buff_len);
    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jentitleBuf, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return ret;
}

static jint android_dvb_Ca_get_all_ipps(JNIEnv *env, jobject thiz, jint operid, jbyteArray jippsBuff, jintArray jbuffLen)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }
    UM_S32 ret = UM_FAILURE;
	UM_U32 buff_len = 0;
	UM_U8 *buff = NULL;
	
	jint *plen =  (env)->GetIntArrayElements(jbuffLen, 0);
	memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_all_ipps buff_len:%d\n", buff_len);
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_wallets malloc fail\n");
		return ret;
	}
	memset(buff, 0, buff_len);
    ret =  ca->getAllIpps((UM_U32) operid, buff, &buff_len);

	if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jippsBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;
	
    return ret;
}

static jint android_dvb_Ca_get_wallets(JNIEnv *env, jobject thiz, jint operid, jbyteArray jwalletsBuff, jintArray jbuffLen)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }
    UM_S32 ret = UM_FAILURE;
	UM_U32 buff_len = 0;
    jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));

	LOGV("android_dvb_Ca_get_wallets buff_len:%d\n", buff_len);	

	UM_U8 *buff = (UM_U8 *)malloc(buff_len);

	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_wallets malloc fail\n");
		return ret;
	}
	memset(buff, 0, buff_len);
		
    ret =  ca->getWallets((UM_U32) operid, buff, &buff_len);
	LOGV("android_dvb_Ca_get_wallets Ca_get_wallets, buff_len:%d\n", buff_len);	
    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jwalletsBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return ret;
}

static jint android_dvb_Ca_del_detitle_checknum(JNIEnv *env, jobject thiz, jint u16TvsId, jint detitle_chknum)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }
    UM_S32 ret = UM_FAILURE;

    ret = ca->delDetitleChecknum((UM_U16)u16TvsId, (UM_U32)detitle_chknum);
    return ret;
}

static jint android_dvb_Ca_get_detitle_checknum(JNIEnv *env, jobject thiz, jint u16TvsId, jintArray jdetitlechknum, jint bufflen)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_S32 ret = UM_FAILURE;
    UM_U32 *detitle_chknum_buff = (UM_U32 *)malloc((UM_U32)bufflen);
    ret = ca->getDetitleChecknum((UM_U16)u16TvsId, detitle_chknum_buff,bufflen);
    LOGV("android_dvb_Ca_get_detitle_checknum ret:%d\n",ret);

    if(ret == UM_SUCCESS)
    {
        (env)->SetIntArrayRegion(jdetitlechknum,0,bufflen,(const jint *)detitle_chknum_buff);
    }

    free(detitle_chknum_buff);

    return ret;
}

static jint android_dvb_Ca_get_detitle_readed(JNIEnv *env, jobject thiz, jint u16TvsId)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_S32 ret = UM_FAILURE;
    ret = ca->getDetitleReaded((UM_U16)u16TvsId);

    return ret;
}

static jint android_dvb_Ca_get_operator_child_status(JNIEnv *env, jobject thiz, jint jTvsId, jbyteArray jchildStatusBuff, jintArray jbuffLen)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

	UM_S32 ret = UM_FAILURE;
	UM_U32 buff_len = 0;
	UM_U8 *buff = NULL;
	
	jint *plen =  (env)->GetIntArrayElements(jbuffLen, 0);
	memcpy(&buff_len, plen, sizeof(int));
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_operator_child_status malloc fail\n");
		return ret;
	}
	memset(buff, 0, buff_len);
    ret =  ca->getOperatorChildStatus((UM_U32) jTvsId, buff, &buff_len);

	if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jchildStatusBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return ret;
}
static jint android_dvb_Ca_read_feeddata_from_parent(JNIEnv *env, jobject thiz, jint operid, jbyteArray jfeedData, jintArray jlen)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_S32 ret = UM_FAILURE;
    UM_CA_FEED_DATA_INFO ca_feed_data;
    ret = ca->readFeeddataFromParent(operid,&ca_feed_data);
    if(ret == UM_SUCCESS)
    {
        (env)->SetByteArrayRegion(jfeedData,0,ca_feed_data.pu32Len,(const jbyte *)ca_feed_data.pu8FeedData);
        (env)->SetIntArrayRegion(jlen,0,1,(const jint *)&ca_feed_data.pu32Len);
    }
    return ret;
}

static jint android_dvb_Ca_write_feeddata_to_child(JNIEnv *env, jobject thiz, jint operid, jbyteArray jbfeedata, jint jlen)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_S32 ret = UM_FAILURE;
    UM_CA_FEED_DATA_INFO ca_feed_data;

    jbyte *feed_data = (env)->GetByteArrayElements(jbfeedata,0);
    memcpy(ca_feed_data.pu8FeedData,(UM_U8*)feed_data,1024);
    ca_feed_data.pu32Len = (UM_U32)jlen;

    ret = ca->writeFeeddataToChild((UM_U16)operid,&ca_feed_data);
    return ret;
}

static jint android_dvb_Ca_read_email(JNIEnv *env, jobject thiz, jint ju32id)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }
    UM_S32 ret = UM_FAILURE;

    ret =  ca->readEmail((UM_U32)ju32id);

    return ret;
}

static jint android_dvb_Ca_get_eigenvalue(JNIEnv *env, jobject thiz,jint operid, jintArray pacArray)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }
    UM_U32 pacarray[CA_PACARRAY_MAX];
    //UM_U32 *pacarray = (UM_U32 *)malloc(CA_PACARRAY_MAX*sizeof(UM_U32));
    //memset(pacarray,0,CA_PACARRAY_MAX);
    UM_S32 ret = UM_FAILURE;
    LOGV("android_dvb_Ca_get_eigenvalue wan");
    ret = ca->getEigenvalue((UM_U16)operid,&pacarray[0]);
    LOGV("android_dvb_Ca_get_eigenvalue after");
    UM_U32 i;
    for(i=0; i<18; i++)
    {
        LOGV("Casvr:Ca_get_eigenvalue:0x%x\n",pacarray[i]);
    }
    if(ret == UM_SUCCESS)
    {
        LOGV("android_dvb_Ca_get_eigenvalue operid pacArray[2]");
        (env)->SetIntArrayRegion(pacArray, 0, CA_PACARRAY_MAX, (const jint*)pacarray);
        LOGV("android_dvb_Ca_get_eigenvalue operid pacArray[3]");
    }
    //free(pacarray);
    //pacarray = NULL;
    return ret;
}

static jint android_dvb_Ca_get_update_status(JNIEnv *env, jobject thiz, jbyteArray jupdateBuff, jintArray jbuffLen)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }

	UM_S32 ret = UM_FAILURE;
	UM_U32 buff_len = 0;
	UM_U8 *buff = NULL;
	
	jint *plen =  (env)->GetIntArrayElements(jbuffLen, 0);
	memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_all_ipps buff_len:%d\n", buff_len);
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_wallets malloc fail\n");
		return ret;
	}
	memset(buff, 0, buff_len);
    ret =  ca->getUpdateStatus(buff, &buff_len);

	if(UM_SUCCESS == ret)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jupdateBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return UM_SUCCESS;
}

static jint android_dvb_Ca_set_update_status(JNIEnv *env, jobject thiz,jbyteArray buf,jint size)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }

	UM_U32 bufflen = size;
    jbyte *updatebuf= (env)->GetByteArrayElements(buf,0);
    UM_U8 *updatestatusbuf = (UM_U8 *)malloc(bufflen);
    memcpy(updatestatusbuf,(UM_U8*)updatebuf,bufflen);

	for(UM_U32 i = 0; i < 16; i++)
	{
		LOGV("android_dvb_Ca_set_update_status updatestatusbuf = %d/n!",updatestatusbuf[i]);
	}
	
    UM_S32 ret = ca->setUpdateStatus(updatestatusbuf,bufflen);
    return UM_SUCCESS;
	
}

static jint android_dvb_Ca_get_emaiheads(JNIEnv *env, jobject thiz, jbyteArray headBuff,jintArray jbuffLen)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }

	UM_S32 ret = UM_FAILURE;
	UM_U32 buff_len = 0;
	UM_U8 *buff = NULL;
	
	jint *plen =  (env)->GetIntArrayElements(jbuffLen, 0);
	memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_all_ipps buff_len:%d\n", buff_len);
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_wallets malloc fail\n");
		return ret;
	}
	memset(buff, 0, buff_len);
    ret =  ca->getEmailHeads(buff, &buff_len);

	if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(headBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return UM_SUCCESS;
}


static jint android_dvb_Ca_get_update_progresss(JNIEnv *env, jobject thiz, jbyteArray jupdateProgresssBuff, jintArray jbuffLen)
{
	UM_S32 ret = UM_FAILURE;

    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }

	UM_U8 *buff = NULL;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_update_progresss buff_len:%d\n", buff_len);	
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_update_progresss malloc fail\n");
		return ret;
	}
	
	memset(buff, 0, buff_len);	
    ret = ca->getUpdateProgress(buff, &buff_len);

    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jupdateProgresssBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return ret;

}

static jint android_dvb_Ca_get_emailcontent_by_index(JNIEnv *env, jobject thiz, jint index, jbyteArray jemailBuff, jintArray jbuffLen)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }
	UM_S32 ret = UM_FAILURE;
	UM_U32 buff_len = 0;
	UM_U8 *buff = NULL;
	
	jint *plen =  (env)->GetIntArrayElements(jbuffLen, 0);
	memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_emailcontent_by_index buff_len:%d\n", buff_len);
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_emailcontent_by_index malloc fail\n");
		return ret;
	}
	memset(buff, 0, buff_len);
    ret =  ca->getEmailContentByIndex((UM_U32) index, buff, &buff_len);
	LOGV("ca->Ca_get_emailcontent_by_index:ret:%d,buff_len:%d\n", ret, buff_len);
	if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jemailBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

	return ret;
}

static jint android_dvb_Ca_delete_email_by_index(JNIEnv *env, jobject thiz, jint index)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }
    UM_S32 ret = UM_FAILURE;

    LOGV("android_dvb_Ca_get_emailcontent_by_index before ret!");
    ret = ca->deleteEmailByIndex((UM_U32)index);
    LOGV("android_dvb_Ca_get_emailcontent_by_index ret SUCCESS!");

    return ret;
}

static jint android_dvb_Ca_delete_all_email(JNIEnv *env, jobject thiz)
{
    sp<Ca> ca = getCa(env,thiz);
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }
    UM_S32 ret = UM_FAILURE;

    LOGV("android_dvb_Ca_delete_all_email before ret!");
    ret = ca->deleteAllEmail();
    LOGV("android_dvb_Ca_delete_all_email ret SUCCESS!");

    return ret;
}

static jint android_dvb_Ca_get_ipppop_info(JNIEnv *env, jobject thiz, jbyteArray jippPopBuff, jintArray jbuffLen)
{
	UM_S32 ret = UM_FAILURE;
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
	UM_U8 *buff = NULL;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_ipppop_info buff_len:%d\n", buff_len);	
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_ipppop_info malloc fail\n");
		return ret;
	}
	
	memset(buff, 0, buff_len);	
    ret = ca->getIpppopInfo(buff, &buff_len);

    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jippPopBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return ret;
}
/*
static jint android_dvb_Ca_book_ipp(JNIEnv *env, jobject thiz, jint priceCode,jint price, jboolean buyProgram, jint ecmPid, jbyteArray jvPwd, jint pinlen)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_S32 ret=UM_FAILURE;
    UM_U32 i=0;

    UM_CA_BOOK_IPP ca_book_ipp;

	memset(&ca_book_ipp, 0, sizeof(UM_CA_BOOK_IPP));
	ca_book_ipp.u32PriceCode = priceCode;
	ca_book_ipp.u16Price = price;
    ca_book_ipp.bBuyProgram = (UM_BOOL)buyProgram;
	ca_book_ipp.u16EcmPid = ecmPid;
	LOGV("android_dvb_Ca_book_ipp u32PriceCode = %d",ca_book_ipp.u32PriceCode);
	LOGV("android_dvb_Ca_book_ipp u16Price = %d",ca_book_ipp.u16Price);
	LOGV("android_dvb_Ca_book_ipp bBuyProgram = %d",ca_book_ipp.bBuyProgram);
	LOGV("android_dvb_Ca_book_ipp u16EcmPid = 0x%x",ca_book_ipp.u16EcmPid);
	
	
    jbyte *pwd = (env)->GetByteArrayElements(jvPwd,0);
    memcpy(ca_book_ipp.pin,(UM_U8*)pwd,CAS_PIN_LEN_MAX);
    for(i=0; i<(UM_U32)pinlen; i++)
    {
        ca_book_ipp.pin[i] -= ASCII_0;
        LOGV("android_dvb_Ca_book_ipp pin = %d",ca_book_ipp.pin[i]);
    }

    ca_book_ipp.pin_len = pinlen;
	LOGV("android_dvb_Ca_book_ipp pin_len = %d",ca_book_ipp.pin_len);
    ret = ca->Ca_book_ipp(&ca_book_ipp);

    LOGV("Ca_book_ipp ret:0x%x\n",ret);
    return ret;
}
*/

static jint android_dvb_Ca_book_ipp(JNIEnv *env, jobject thiz, jobject ippinfo)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }

    UM_S32 ret=UM_FAILURE;
	
    Parcel *data = parcelForJavaObject(env, ippinfo);

    ret = ca->bookIpp(data);

    LOGV("Ca_book_ipp ret:0x%x\n",ret);
    return ret;
}

static jint android_dvb_Ca_inquire_book_ipps_over(JNIEnv *env, jobject thiz, jint jecmPid)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_inquire_book_ipps_over jecmPid:0x%x", jecmPid);

	UM_S32 ret = UM_FAILURE;

    ret = ca->inquireBookIppsOver(jecmPid);

    return ret;
}

static jint android_dvb_Ca_get_ca_cardid(JNIEnv *env, jobject thiz, jintArray jvCaCardid)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_ca_cardid was called");

	UM_S32 ret = UM_FAILURE;
    UM_U32 stCaCardId;
    ret = ca->getManuInfoCardId(&stCaCardId);

	if(ret == UM_SUCCESS)
    {
        LOGV("android_dvb_Ca_get_ca_cardid cardid:%d\n", stCaCardId);
        (env)->SetIntArrayRegion(jvCaCardid,0,1,(const jint*)&stCaCardId);
    }

    return ret;
}

static jint android_dvb_Ca_get_stb_atr(JNIEnv *env, jobject thiz, jbyteArray jvStbAtr,jintArray jvAtrLen)
{
	UM_S32 ret = UM_FAILURE;

    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_stb_atr was called");

	UM_U8 *buff = NULL;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jvAtrLen,0);
	memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_stb_atr buff_len:%d\n", buff_len);	
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_stb_atr malloc fail\n");
		return ret;
	}

	memset(buff, 0, buff_len);	
    ret = ca->getManuInfoatr(buff, &buff_len);
    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jvAtrLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jvStbAtr, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;
	
    return ret;
}

static jint android_dvb_Ca_get_accountno(JNIEnv *env, jobject thiz, jbyteArray jvaccountno,jintArray jvLen)
{
	UM_S32 ret = UM_FAILURE;

    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_accountno was called");

	UM_U8 *buff = NULL;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jvLen,0);
	memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_accountno buff_len:%d\n", buff_len);	
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_accountno malloc fail\n");
		return ret;
	}

	memset(buff, 0, buff_len);	
    ret = ca->getaccountno(buff, &buff_len);
    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jvLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jvaccountno, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;
	
    return ret;
}

static jint android_dvb_Ca_get_money(JNIEnv *env, jobject thiz, jbyteArray jvaccountno,jintArray jvLen)
{
	UM_S32 ret = UM_FAILURE;

    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_money was called");

	UM_U8 *buff = NULL;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jvLen,0);
	memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_money buff_len:%d\n", buff_len);	
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_money malloc fail\n");
		return ret;
	}

	memset(buff, 0, buff_len);	
    ret = ca->getdvnmoney(buff, &buff_len);
    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jvLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jvaccountno, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;
	
    return ret;
}

static jint android_dvb_Ca_get_dvnpair(JNIEnv *env, jobject thiz, jbyteArray jvaccountno,jintArray jvLen)
{
	UM_S32 ret = UM_FAILURE;

    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("Ca_get_manu_info_dvnpair was called");

	UM_U8 *buff = NULL;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jvLen,0);
	memcpy(&buff_len, plen, sizeof(int));
	LOGV("Ca_get_manu_info_dvnpair buff_len:%d\n", buff_len);	
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("Ca_get_manu_info_dvnpair malloc fail\n");
		return ret;
	}

	memset(buff, 0, buff_len);	
	ret = ca->getdvnpair(buff, &buff_len);
	if(ret ==  UM_SUCCESS)
	{
		(env)->SetIntArrayRegion(jvLen, 0, 1,(const jint *)&buff_len);
		(env)->SetByteArrayRegion(jvaccountno, 0, buff_len,(const jbyte *)buff);
	}
	free(buff);
	buff = NULL;
	
    return ret;
}




static jint android_dvb_Ca_get_ca_sccosver(JNIEnv *env, jobject thiz, jintArray jvCaSccosver)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_ca_sccosver was called");

	UM_S32 ret = UM_FAILURE;
    UM_U32 stCaSccosver;
    ret = ca->getManuInfoScCosVer(&stCaSccosver);

	if(ret == UM_SUCCESS)
    {
        LOGV("android_dvb_Ca_get_ca_sccosver sccosver:%d\n", stCaSccosver);
        (env)->SetIntArrayRegion(jvCaSccosver,0,1,(const jint*)&stCaSccosver);
    }

    return ret;
}

static jint android_dvb_Ca_get_ca_stbcasver(JNIEnv *env, jobject thiz, jbyteArray jvCaStbcasver, jint jbuffLen)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_ca_stbcasver was called, jbuffLen:%d", jbuffLen);

	UM_S32 ret = UM_FAILURE;
	UM_U8 *stCaStbcasver = NULL;
	
	stCaStbcasver = (UM_U8 *)malloc(jbuffLen);
	if(NULL == stCaStbcasver)
	{
		LOGV("android_dvb_Ca_get_ca_stbcasver malloc fail\n");
		return ret;
	}
	
	memset(stCaStbcasver, 0, jbuffLen);	
    ret = ca->getManuInfoStbCasVer(stCaStbcasver, jbuffLen);

    if(ret ==  UM_SUCCESS)
    {
        (env)->SetByteArrayRegion(jvCaStbcasver, 0, jbuffLen,(const jbyte *)stCaStbcasver);
    }
	free(stCaStbcasver);
	stCaStbcasver = NULL;

    return ret;
}

static jint android_dvb_Ca_get_ca_manuname(JNIEnv *env, jobject thiz, jbyteArray jvCaManuName, jintArray jbuffLen)
{
	UM_S32 ret = UM_FAILURE;
	
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }

	UM_U8 *buff = NULL;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_ca_manuname buff_len:%d\n", buff_len);	
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_ca_manuname malloc fail\n");
		return ret;
	}
	
	memset(buff, 0, buff_len);	
    ret = ca->getManuInfoManuName(buff, &buff_len);

    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jvCaManuName, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return ret;

}

static jint android_dvb_Ca_get_area_info(JNIEnv *env, jobject thiz, jbyteArray jareainfoBuff, jintArray jbuffLen)
{
	UM_S32 ret = UM_FAILURE;

    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }

	UM_U8 *buff = NULL;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_area_info buff_len:%d\n", buff_len);	
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_area_info malloc fail\n");
		return ret;
	}
	
	memset(buff, 0, buff_len);	
    ret = ca->getAreaInfo(buff, &buff_len);

    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jareainfoBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return ret;

}

static jint android_dvb_Ca_get_mother_info(JNIEnv *env, jobject thiz, jint joperatorid, jbyteArray jareainfoBuff, jintArray jbuffLen)
{
	UM_S32 ret = UM_FAILURE;

    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }

	UM_U8 *buff = NULL;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Ca_get_area_info buff_len:%d\n", buff_len);	
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_area_info malloc fail\n");
		return ret;
	}
	
	memset(buff, 0, buff_len);	
    ret = ca->getMotherInfo(joperatorid, buff, &buff_len);

    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jareainfoBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return ret;

}

static jint android_dvb_Ca_get_pin_state(JNIEnv *env, jobject thiz, jintArray jvCaPinstate)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_pin_state was called");

	UM_S32 ret = UM_FAILURE;
    UM_U32 stCaPinstate;
    ret = ca->getPinState(&stCaPinstate);

	if(ret == UM_SUCCESS)
    {
        LOGV("android_dvb_Ca_get_pin_state stCaPinstate:%d\n", stCaPinstate);
        (env)->SetIntArrayRegion(jvCaPinstate,0,1,(const jint*)&stCaPinstate);
    }

    return ret;
}

static jint android_dvb_Ca_get_viewed_ipps(JNIEnv *env, jobject thiz, jbyteArray jentitleBuf, jintArray jbuffLen)
{
    sp<Ca> ca = getCa(env,thiz);
	
    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception",NULL);
        return UNKNOWN_ERROR;
    }
	
    UM_S32 ret = UM_FAILURE;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));

	LOGV("android_dvb_Ca_get_work_time,buff_len:%d\n", buff_len);	

	UM_U8 *buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Ca_get_entitles malloc fail\n");
		return ret;
	}
	memset(buff, 0, buff_len);
		
    ret =  ca->getViewedIpps(buff, &buff_len);
    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jentitleBuf, 0, buff_len,(const jbyte *)buff);
    }
	
	free(buff);
	buff = NULL;
	
	return ret;
}


static jint android_dvb_Ca_cmd_process(JNIEnv *env, jobject thiz, jint jcmdType, jint jlparam, jint jrparam)
{
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_cmd_process was called");

	UM_S32 ret = UM_FAILURE;

	UM_CAS_PROCESS ca_process;

	memset(&ca_process, 0, sizeof(ca_process));
	ca_process.type = jcmdType;
	ca_process.lparam = jlparam;
	ca_process.rparam = jrparam;
	
    ret = ca->cmdProcess(&ca_process);

    return ret;
}

static jint android_dvb_Irdca_cmd_process(JNIEnv *env, jobject thiz, jbyteArray jareainfoBuff, jintArray jbuffLen)
{
	UM_S32 ret = UM_FAILURE;

    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }

	UM_U8 *buff = NULL;
	UM_U32 buff_len = 0;
	
	jint *plen = (env)->GetIntArrayElements(jbuffLen,0);
    memcpy(&buff_len, plen, sizeof(int));
	LOGV("android_dvb_Irdca_cmd_process buff_len:%d_xinhua\n", buff_len);	
	buff = (UM_U8 *)malloc(buff_len);
	if(NULL == buff)
	{
		LOGV("android_dvb_Irdca_cmd_process malloc fail_xinhua\n");
		return ret;
	}
	
	memset(buff, 0, buff_len);	
    //ret = ca->cmdProcess(buff, &buff_len);
    ret = ca->irdcmdProcess(buff, &buff_len);
    if(ret ==  UM_SUCCESS)
    {
    	(env)->SetIntArrayRegion(jbuffLen, 0, 1,(const jint *)&buff_len);
        (env)->SetByteArrayRegion(jareainfoBuff, 0, buff_len,(const jbyte *)buff);
    }
	free(buff);
	buff = NULL;

    return ret;
}

static jint android_dvb_Ca_osdmessage_completed(JNIEnv *env, jobject thiz, jint jduration)
{
	LOGV("android_dvb_Ca_osdmessage_completed was called");
    sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("jduration:%d\n", jduration);

	UM_S32 ret = UM_FAILURE;
	
    ret = ca->osdmessageCompleted(jduration);

    return ret;
}

static jint android_dvb_Ca_get_main_freq(JNIEnv *env, jobject thiz, jintArray jmainFreq)
{
	sp<Ca> ca = getCa(env,thiz);

    if(ca == NULL)
    {
        jniThrowException(env,"java/lang/IllegalStateEXception ca",NULL);
        return UNKNOWN_ERROR;
    }
    LOGV("android_dvb_Ca_get_main_freq was called");

	UM_S32 ret = UM_FAILURE;
    UM_U32 mainFreq = 0;
    ret = ca->getMainFreq(&mainFreq);

	if(ret == UM_SUCCESS)
    {  
        LOGV("android_dvb_Ca_get_main_freq mainFreq:%d\n", mainFreq);
        (env)->SetIntArrayRegion(jmainFreq,0,1,(const jint*)&mainFreq);
    }

    return ret;
}



// ----------------------------------------------------------------------------

static JNINativeMethod gMethods[] = {
    {"CaGetIcNo",                 "(ILcom/um/dvbstack/Ca$Card_No;)I", (void *)android_dvb_Ca_get_ic_no},
    {"CaGetPairStatus",                 "(I)I", (void *)android_dvb_Ca_get_pair_status},
    {"CaGetVersion",                 "(Lcom/um/dvbstack/Ca$Ca_Version;)I",                 (void *)android_dvb_Ca_get_ca_version},    
	{"CaGetRating",                 "(Lcom/um/dvbstack/Ca$Ca_Rating;)I", (void *)android_dvb_Ca_get_ca_rating},
    {"CagetWorkingTime", 			"([B[I)I", (void *)android_dvb_Ca_get_work_time},
    {"CaGetPlatformID",                 "()I", (void *)android_dvb_Ca_get_platform_id},
    {"CaVerifyPin",                 "([BI)I", (void *)android_dvb_Ca_verify_pin},
    {"CaChangePin",                 "([B[BI)I", (void *)android_dvb_Ca_change_pin},
    {"CaSetRate",                 "(I[BI)I", (void *)android_dvb_Ca_set_rating},
    {"CaSetWorkTime",                 "([B[B[BI)I", (void *)android_dvb_Ca_Set_working_time},
    {"CaGetOperID",				  "([I[I)I", (void *)android_dvb_Ca_get_operatorids},
    {"CaGetOperatorInfo",			  "(I[B)I", (void *)android_dvb_Ca_get_operator_info},
//    {"CaGetDvnEntitles",			  "([B[I)I", (void *)android_dvb_Ca_get_dvn_entitles},
	{"CaGetEntitles",			  "(I[B[I)I", (void *)android_dvb_Ca_get_entitles},
    {"CaGetAllIpps",			  "(I[B[I)I", (void *)android_dvb_Ca_get_all_ipps},
    {"CaGetWallets",			  "(I[B[I)I", (void *)android_dvb_Ca_get_wallets},
    {"CaDelDetitleChecknum",	  "(II)I", (void *)android_dvb_Ca_del_detitle_checknum},
    {"CaGetDetitleChecknum",	  "(I[II)I", (void *)android_dvb_Ca_get_detitle_checknum},
    {"CaGetDetitleReaded",	  "(I)I", (void *)android_dvb_Ca_get_detitle_readed},
    {"CaGetOperatorChildStatus",			  "(I[B[I)I",	(void *)android_dvb_Ca_get_operator_child_status},
    {"CaReadFeeddataFromParent",			  "(I[B[I)I",	(void *)android_dvb_Ca_read_feeddata_from_parent},
    {"CaWriteFeeddataToChild",			  "(I[BI)I",	(void *)android_dvb_Ca_write_feeddata_to_child},
    {"CaReadEmail",			  		  "(I)I",	(void *)android_dvb_Ca_read_email},
    {"CaGetEigenvalue",                                "(I[I)I", (void *)android_dvb_Ca_get_eigenvalue},
    {"CaGetPairInfoCheck", 			"([B[I)I", (void *)android_dvb_Ca_get_pair_info_check},
    {"CaGetUpdateStatus",                 "([B[I)I", (void *)android_dvb_Ca_get_update_status},
    {"CaSetUpdateStatus",                 "([BI)I", (void *)android_dvb_Ca_set_update_status},
    {"CaGetEmailheads",                 "([B[I)I", (void *)android_dvb_Ca_get_emaiheads},
    {"CaGetEmailContentByIndex",			  "(I[B[I)I", (void *)android_dvb_Ca_get_emailcontent_by_index},
    {"CaDeleteEmailByIndex",                 "(I)I", (void *)android_dvb_Ca_delete_email_by_index},
    {"CaDeleteAllEmail",			  "()I", (void *)android_dvb_Ca_delete_all_email},
    {"CaGeIpppopInfo",                 "([B[I)I", (void *)android_dvb_Ca_get_ipppop_info},
    {"CaBookIpp",                 "(Landroid/os/Parcel;)I", (void *)android_dvb_Ca_book_ipp},
    {"CaGetUpdateProgress",        "([B[I)I",                    (void *)android_dvb_Ca_get_update_progresss},
	{"CaGetCardStatus",              "([Z)I", (void *)android_dvb_Ca_get_ca_card_status},
	{"CaGetEmailIcon",              "(Lcom/um/dvbstack/Ca$Ca_EmailIcon;)I", (void *)android_dvb_Ca_get_ca_email_icon},
    {"CaGetDetitleIcon",              "(Lcom/um/dvbstack/Ca$Ca_DetitleIcon;)I", (void *)android_dvb_Ca_get_ca_detitle_icon},
    {"CaGetCardId",                 "([I)I", (void *)android_dvb_Ca_get_ca_cardid},
    {"CaGetScCosVer",                 "([I)I", (void *)android_dvb_Ca_get_ca_sccosver},
    {"CaGetStbCasVer",                 "([BI)I", (void *)android_dvb_Ca_get_ca_stbcasver},
    {"CaGetManuName",                 "([B[I)I", (void *)android_dvb_Ca_get_ca_manuname},
    {"CaGetAreaInfo",        "([B[I)I", (void *)android_dvb_Ca_get_area_info},
    {"CaGetMotherInfo",        "(I[B[I)I", (void *)android_dvb_Ca_get_mother_info},
    {"CaGetPinState",        "([I)I", (void *)android_dvb_Ca_get_pin_state},
    {"CaGetViewedIpps",        "([B[I)I", (void *)android_dvb_Ca_get_viewed_ipps},
	{"CaCmdProcess",        "(III)I", (void *)android_dvb_Ca_cmd_process},
	{"IrdcaCmdProcess",        "([B[I)I", (void *)android_dvb_Irdca_cmd_process},
	{"CaInquireBookIppsOver", "(I)I", (void *)android_dvb_Ca_inquire_book_ipps_over},
	{"CaOsdmessageCompleted", "(I)I", (void *)android_dvb_Ca_osdmessage_completed},
	{"CaGetMainFreq",              "([I)I", (void *)android_dvb_Ca_get_main_freq},
	{"CaRestoreMsgSend",              "()I", (void *)android_dvb_Ca_restore_msg_send},
    {"CaGetStbAtr",                 "([B[I)I", (void *)android_dvb_Ca_get_stb_atr},
    {"CaGetAccountno",                 "([B[I)I", (void *)android_dvb_Ca_get_accountno},
    {"CaGetMoney",                 "([B[I)I", (void *)android_dvb_Ca_get_money},
    {"CaGetdvnpair",                 "([B[I)I", (void *)android_dvb_Ca_get_dvnpair},
};	

/*{"CaBookIpp",                 "(IIZI[BI)I", (void *)android_dvb_Ca_book_ipp},*/

static int find_field(JNIEnv *env)
{
    jclass clazz;

    clazz = env->FindClass("com/um/dvbstack/Ca");
    if (clazz == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find com/um/dvbstack/Ca");
        return -1;
    }

    fields.context = env->GetFieldID(clazz, "mNativeContext", "I");
    if (fields.context == NULL) {
        jniThrowException(env, "java/lang/RuntimeException", "Can't find Ca.mNativeContext");
        return -1;
    }

    return 0;
}

// This function only registers the native methods
int register_android_dvb_Ca(JNIEnv *env)
{

    LOGV("register_android_dvb_Ca was called");
    if(find_field(env) < 0)
    {
        LOGV("register_android_dvb_Ca failed, could not find the field");
        return -1;
    }
    return AndroidRuntime::registerNativeMethods(env,
            "com/um/dvbstack/Ca",gMethods, NELEM(gMethods));

    return 0;
}
// ----------------------------------------------------------------------------


