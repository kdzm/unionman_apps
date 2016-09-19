package com.um.dvbstack;

import android.os.Parcel;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.format.Time;
import android.util.Log;

public class Ca {	
	public  int mNativeContext; // accessed by native methods
	
	
	public Ca(DVB dvb)
	{
		mNativeContext = dvb.mNativeContext;
	}	
	
	/**
	\brief ��ȡCA������
	\attention \n
	��
	\param[in]  CaID :CaID��ţ���0��ʼ
	\param[out]  Card_No:��ȡ�����ܿ���������
	\retval ::0: �ɹ� 
	 		����ʧ��
	\see \n
	::
	*/

	
	public final native int CaGetIcNo(int CaID,Card_No CardNo);

	public static class Card_No {
		public byte [] cardno = new byte[60]; // 60��Ԫ��INT ����
	}
	
	/**
	\brief ��ȡCA�����Ӧ״̬
	\attention \n
	��
	\param[in]  CaID :CaID��ţ���0��ʼ
	\retval ::0: ���ܿ��뵱ǰ��ж�Ӧ, 
			0x80000029�����ܿ�û�ж�Ӧ�κλ�� ,
	 		0x80000028: ���ܿ��Ѿ���Ӧ��Ļ��,
	 		����û�����ܿ�
	\see \n
	::
	*/
	public final native int CaGetPairStatus(int CaID);
	public final native int CaGetCardStatus(boolean [] cardStatus);
	
	public final native int CaGetUpdateProgress(byte[] progress, int[] len);
	/**
	\brief ��ȡCA�汾��
	\attention \n
	��
	\param[in]  CaID :CaID��ţ���0��ʼ
	\param[in]  Ca_Version:��ȡ�����ܿ��汾������
	\retval ::0: �ɹ� 
	 		����ʧ��
	\see \n
	::
	*/
	public final native int CaGetVersion(Ca_Version CaVersion);

	public static class  Ca_Version {
		public byte [] caversion = new byte[50]; // 50��Ԫ��INT ����
	}
	
	/**
	\brief ��ȡ�ۿ�����
	\attention \n
	��
	\param[in]  CaID :CaID��ţ���0��ʼ
	\param[out]  Ca_Rating:��ȡ�����ܿ��汾������
	\retval ::0: �ɹ� 
	 		����ʧ��
	\see \n
	::
	*/
	public final native int CaGetRating(Ca_Rating CaRating);

	public static class  Ca_Rating {
		public int [] carating = new int[1]; //1��Ԫ��INT ����
	}
	
	public final native int CaGetEmailIcon(Ca_EmailIcon CaEmailIcon);

	public static class  Ca_EmailIcon {
		public int [] caEmailIcon = new int[1]; //1��Ԫ��INT ����
	}
	
	public final native int CaGetDetitleIcon(Ca_DetitleIcon CaDetitleIcon);

	public static class  Ca_DetitleIcon {
		public int [] caDetitleIcon = new int[1]; //1��Ԫ��INT ����
	}
	
	/**����ʱ���**/
	
	final int CAEVENT_PACKET_LEN = 36;

	private int byte2int(byte a, byte b)
	{
		String hex = "";
		hex += Integer.toHexString(a&0xff);
		hex += Integer.toHexString(b&0xff);
		
		return Integer.valueOf(hex, 16);
	}
	
	private int byte4int(byte a, byte b, byte c, byte d)
	{
		String hex = "";
		
		hex += Integer.toHexString(a&0xff);
		hex += Integer.toHexString(b&0xff);
		hex += Integer.toHexString(c&0xff);	
		hex += Integer.toHexString(d&0xff);	
		
		return Integer.valueOf(hex, 32);
	}
	private native int CagetWorkingTime(byte[] buf, int[] len);
	
	public CaEvent CaparseWorkingTime()
	{
		int start_year	= 0;
		int start_month	= 0;
		int start_day = 0;
		int start_hour = 0;
		int start_min = 0;
		int start_sec = 0;
		int end_year = 0;
		int end_month = 0;
		int end_day = 0;
		int end_hour = 0;
		int end_min = 0;
		int end_sec = 0;
		int pwd_len = 0;
		
		Time s;
		Time e;
		byte []pwd = new byte[8];
		
		int []buff_len = {1024};
		byte []buff = new byte[buff_len[0]];
		
		int ret = CagetWorkingTime(buff, buff_len);
		System.out.printf("CagetWorkingTime,ret:%d,buff_len:%d\n", ret, buff_len[0]); 
		if((0 == ret) && (0 != buff_len[0])){
			String jsonStr = new String(buff, 0, buff_len[0]);
			System.out.println("jsonStr:"+jsonStr); //输出字符串
	 
		 try {
			  	JSONObject jsonObject = new JSONObject(jsonStr);
			  
				start_year = jsonObject.getInt("stimeYear");
				start_month = jsonObject.getInt("stimeMonth");
				start_day = jsonObject.getInt("stimeDay");
				start_hour = jsonObject.getInt("stimeHour");
				start_min = jsonObject.getInt("stimeMin");
				start_sec = jsonObject.getInt("stimeSec");
				end_year = jsonObject.getInt("etimeYear");
				end_month = jsonObject.getInt("etimeMonth");
				end_day = jsonObject.getInt("etimeDay");
				end_hour = jsonObject.getInt("etimeHour");
				end_min = jsonObject.getInt("etimeMin");
				end_sec = jsonObject.getInt("etimeSec");
				pwd_len = jsonObject.getInt("pinLen");
			} catch (JSONException ex) {  
					System.out.println("get JSONObject fail");// 异常处理代码  
			}  
		}else{
			Log.e("CA","CagetWorkingTime,fail");
			
			return null;
		}
		
			
		s = new Time();
		e = new Time();
		s.set(start_sec, start_min, start_hour, start_day, start_month, start_year);
		e.set(end_sec, end_min, end_hour, end_day, end_month, end_year);
		Log.i("CA","SET TIME");
		CaEvent caevt = new CaEvent();
		caevt.CaEventSetWortime(s,e,pwd,pwd_len);
		Log.i("CA","CaEventSetWortime");
		return caevt;
		
	}

	
	public final native int CaGetPlatformID();	
	public final native int CaVerifyPin(byte[] pin,int pinlen);
	public final native int CaChangePin(byte[] newpin,byte[] oldpin,int pinlen);
	public final native int CaSetRate(int rate,byte[] pwd,int pinlen);
	public final native int CaSetWorkTime(byte[] starttime,byte[] endtime,byte[] pwd, int pin_len);
	
	public final native int CaGetOperID(int[] operid, int[] opernum);
	public final native int CaGetOperatorInfo(int operid, byte[] operinfo);
	
	public final native int CaGetEntitles(int operid, byte[] buff, int[] buff_len);
	public final native int CaGetAllIpps(int operid, byte[] buff, int[] buff_len);
	public final native int CaGetWallets(int operid, byte[] buff, int[] buff_len);
	public final native int CaDelDetitleChecknum(int operid, int checknum);
	public final native int CaGetDetitleChecknum(int operid,int[] buff, int buff_len);
	public final native int CaGetDetitleReaded(int operid);
	public final native int CaGetOperatorChildStatus(int operid,byte[] buff, int[] buff_len);
	public final native int CaReadFeeddataFromParent(int operid,byte[] feeddata, int[] data_len);
	public final native int CaWriteFeeddataToChild(int operid,byte[] feeddata, int data_len);
	public final native int CaDeleteEmailById(int u32id);
	public final native int CaReadEmail(int u32id);
	public final native int CaGetEigenvalue(int operid,int[] array);
	public final native int CaGetPairInfoCheck(byte[] buff,int[] len);
	public final native int CaGetUpdateStatus(byte[] buff,int[] len);
	public final native int CaSetUpdateStatus(byte[] buff,int len);
	public final native int CaGetEmailheads(byte[] buff,int[] len);
	public final native int CaGetEmailContentByIndex(int index,byte[] buff, int[] buff_len);
	public final native int CaDeleteEmailByIndex(int index);
	public final native int CaDeleteAllEmail();	
	public final native int CaGeIpppopInfo(byte[] buff, int[] buff_len);
	public final native int CaBookIpp(Parcel info);
	public final native int CaInquireBookIppsOver(int ecmPid);
	public final native int CaGetMotherInfo(int operatorid, byte[] buff,int[] len);
	public final native int CaRestoreMsgSend();
	public final native int CaCmdProcess(int cmdType, int lparam, int rparam);
	public final native int CaGetCardId(int[] cardId);
	public final native int CaGetScCosVer(int[] scCosVer);
	public final native int CaGetStbCasVer(byte[] stbCasVer, int len);
	public final native int CaGetManuName(byte[] caManuName, int[] bufLen);
	public final native int CaGetAreaInfo(byte[] areainfoBuff, int[] bufLen);
	public final native int CaGetPinState(int[] caPinstate);
	public final native int CaGetViewedIpps(byte[] entitleBuf, int[] bufLen);
	public final native int CaOsdmessageCompleted(int duration);
	public final native int CaGetMainFreq(int[] mainFreq);
}

