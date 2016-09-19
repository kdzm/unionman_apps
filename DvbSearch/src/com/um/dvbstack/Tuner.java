package com.um.dvbstack;

public class Tuner {

    public final static int UM_TRANS_SYS_TYPE_SAT = 1;
    public final static int UM_TRANS_SYS_TYPE_CAB = 2;
    public final static int UM_TRANS_SYS_TYPE_TER = 3;

	private final static String TAG = "DVBSTACK";
	public  int mNativeContext; // accessed by native methods
	
	private static Tuner m_instance;
	
	public Tuner(DVB dvb)
	{
		mNativeContext = dvb.mNativeContext;
	}	

	public static Tuner GetInstance(DVB dvb)
	{
		if(m_instance == null)
		{
			m_instance = new Tuner(dvb);		
		}
		
		return m_instance;
	}
	
	/**
	\brief tuner�����
	\attention \n
	��
	\param[in] Input:  TunerID :Tuner��ţ���0��ʼ
	\param[in] Input:  Freq  Ƶ��  KHZ(279000)
	\param[in] Input:  Symbol ����� Ksym/s(6875000)
	\param[in] Input:  QamType ���Ʒ�ʽ

	\retval ::0: sucess, other, failed
	\see \n
	::HI_ADP_TUNER_DEV_TYPE_E
	*/	
	public final native int Lock(int TunerID, int Freq, int SymbolRate, int QamType);


	/**
	\brief ��ȡtuner��Ϣ
	\attention \n
	��
	\param[in]  TunerID       tuner id.
	\param[out]TunerInfo      �������Ƶ����Ϣ.
	\retval ::0: sucess, other failed
	\see \n
	*/	
	
	public final native int GetInfo(int TunerID, TunerInfo TunerInfo);

	/**
	\brief ��ȡtuner��״̬
	\attention \n
	��
	\param[in]  TunerID :Tuner��ţ���0��ʼ
	\param[out] LockFlag   ��״̬ ,
	\param[out] LockFreq  ��״̬��Ӧ��Ƶ��
	\retval ::0: sucess, other ,failed
	\see \n
	::HI_TUNER_TPINFO_S
	*/
	public final native int GetLockStatus(int TunerID,int[]LockFlag, long[]Freq);
	public final native int DisplayPanel(byte[] ch,int len);

    public final native int GetType();

    public final native int SetType(int type);

	public static class TunerInfo {

		public int Quality;

		public int Strength;

		public int[] Ber = new int[3]; // 3��Ԫ��INT ����

		public int CurrFrq;

		public int CurrSym;

		public int CurrQam;

		public int CurrLockFlag;
	}
}

