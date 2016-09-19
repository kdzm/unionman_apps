package com.cvte.tv.at.listener;

public interface IUartDeviceListener {
    public int[] OnGetTVChecksumListener();

    public int[] OnGetTVFixedChecksumListener();

    public boolean OnSetTVSourceListener(int source);

    public int OnGetTVSourceListener();

    public boolean OnSetVolumeListener(int volume);

    public boolean OnChangeTVNumListener(int num);

    public int[] OnGetTVMacAddrListener();

    public int[] OnGetTVConfigMacAddrListener(int type);

    public long OnGetLongData_IDListener(int type);
//
//    public long OnGetTVHDCP20G_IDListener();
//
//    public long OnGetTVCIPlus_IDListener();

    public boolean OnSaveLongDataToSystemListener(int type, int[] key, int len, int fileId);

    //    public boolean OnSaveMACAddrToTVListener(int[] macAddr);
    public boolean OnSaveMACAddrToTVListener(int mac0, int mac1, int mac2, int mac3, int mac4, int mac5);

    public boolean OnSaveConfigMACAddrToTVListener(int type, int mac0, int mac1, int mac2, int mac3, int mac4, int mac5);

    public boolean OnCtrlIOLevelListener(int pin, int level);

    public int OnGetCurrentKeyPadStatusListener();

    public boolean OnSaveBarcodeToFlashListener(int[] barcode);

    public int[] OnGetBarcodeFromFlashListener();

    public int[] OnGetHDCPKSVCodeListener();

    public boolean OnSetAntennaAirCableListener(int type);

    public boolean OnSetATSCProNumberListener(int major, int minor);

    public int OnGetCIFunctionStatusListener();

    public int[] OnGetIPAddrListener();

    public boolean OnGetWifiTestResultStartListener();

    public int OnGetUSBConnectCountListener();

    public int[] OnGetUSBConnectStatusListener();

    public boolean OnSendCVTEFACIRKeyListener(int key_code);

    public boolean OnCheckBluetoothStartListener();

    public void OnSendCommonJNIToSystemListener(int cmdtype, int[] cmddata);

    public int[] OnSendCommonSystemToJNIListener();

}
