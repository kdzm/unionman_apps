package com.cvte.tv.at.api.tvapi;

import android.content.Context;

import com.cvte.tv.at.R;
import com.cvte.tv.at.api.SysProp;
import com.cvte.tv.at.api.tvapi.hisilicon.EntityChannel;
import com.cvte.tv.at.api.tvapi.hisilicon.TvManagerHelper;
import com.cvte.tv.at.util.Utils;
import com.cvte.tv.at.util.Utils.EnumChannelType;
import com.cvte.tv.at.util.Utils.EnumFactoryDataType;
import com.cvte.tv.at.util.Utils.EnumInputSourceCategory;
import com.cvte.tv.at.util.Utils.EnumInputStatus;
import com.cvte.tv.at.util.Utils.EnumKeyPad;
import com.cvte.tv.at.util.Utils.EnumLedStatus;
import com.cvte.tv.at.util.Utils.KEYDATA_E;
import com.cvte.tv.at.util.Utils.SourceEnum;
import com.hisilicon.android.tvapi.AtvChannel;
import com.hisilicon.android.tvapi.HitvManager;
import com.hisilicon.android.tvapi.constant.EnumSoundChannel;
import com.hisilicon.android.tvapi.constant.EnumSourceIndex;
import com.hisilicon.android.tvapi.impl.AtvChannelImpl;
import com.hisilicon.android.tvapi.vo.TvProgram;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2015/11/24.
 */
public class TVAPI {

    private final Object mutex = new Object();
    private static TVAPI tvapi = null;
    private static Context sContext;
    private static HisiFunAPI sHisiapi = null;

    public static TVAPI getInstance(Context context) {

        if (tvapi == null) {
            Utils.LOG("FacAPI.getInstance = " + context.getClass());
            tvapi = new TVAPI(context);
        }
        if (sHisiapi == null)
            sHisiapi = new HisiFunAPI(sContext);
        return tvapi;
    }

    public TVAPI(Context context) {
        setmContext(context);
    }

    public static Context getmContext() {
        return sContext;
    }

    public static void setmContext(Context context) {
        TVAPI.sContext = context;
    }

    public boolean eventSystemDebugUartSetEnable(boolean status) {
        Utils.LOG("Init Uart Debug,status=" + status);
        //Renlijia.20150307 add for AT Test
        if (status) {
            Utils.LOG("Init Uart Debug Leave");
            FacAPI.SystemCmd("InitATUart");
        } else {
            Utils.LOG("Init Uart Debug,status=" + status);
            FacAPI.SystemCmd("DeInitATUart");
        }
        return false;
    }

    public String eventSystemInformationGetFirmwareVersion() {
        return FacAPI.GetCheckSum();
    }

    public byte[] eventSystemMacGetValue() {
        String wmac = Utils.MAC_EMPTY;//wyfac.getLanMacAddr();//
        Utils.LOG("WY MAC = " + wmac);
        if (wmac == null)
            wmac = Utils.MAC_EMPTY;

        wmac = wmac.toLowerCase();
        if (wmac.length() != Utils.MAC_DEFAULT.length())
            wmac = Utils.MAC_LENGTH_NG;

        String MACStr[] = wmac.split(":");//wmac.split(":");
        int[] MACinit = new int[Utils.MACLen];
        byte[] MACByte = new byte[Utils.MACLen];
        for (int i = 0; i < Utils.MACLen; i++) {
            MACinit[i] = Integer.parseInt(MACStr[i], 16) & 0xFF;
            MACByte[i] = (byte) (MACinit[i] & 0xff);
            Utils.LOG("MACinit[" + i + "]:" + Integer.toHexString(MACinit[i]));
        }

        return MACByte;
    }

    public String eventSystemHdcp1xKeyGetKeyName() {
        long HDCPID = 0;
        Utils.LOG("Get HDCP ID = " + HDCPID);
        return String.valueOf(HDCPID);
    }

    public String eventSystemHdcp2xKeyGetKeyName() {
        long HDCPID = 0;
        Utils.LOG("Get HDCP ID = " + HDCPID);
        return String.valueOf(HDCPID);
    }

    public String eventSystemCiPlusKeyGetName() {
        long CIID = 0;
        Utils.LOG("Get CI ID = " + CIID);
        return String.valueOf(CIID);
    }

    // Write Security Key
    public boolean eventSystemHdcpKeyBurningByWY(KEYDATA_E type, byte[] bs, int len) {
        int rel = 1;//1 is false
        switch (type) {
            case Option_HDCPKey:
                return (rel == 0);//0 is true
            case Option_HDCPKey20G:
            case Option_HDCPKey22G:
                return (rel == 0);//0 is true
        }
        return false;
    }

    public boolean eventSystemHdcp1xKeyBurning(String id, byte[] data, int len) {
        return false;
    }

    public boolean eventSystemHdcp2xKeyBurning(String id, byte[] data, int len) {
        return false;
    }

    public boolean eventSystemCiPlusKeyBurning(String id, byte[] data, int len) {
        return false;
    }

    public boolean eventSystemMacSetValue(byte[] mac) {
        Formatter formatter = new Formatter();
        String macstr = formatter.format("%02x:%02x:%02x:%02x:%02x:%02x",
                mac[0], mac[1], mac[2], mac[3], mac[4], mac[5])
                .toString()
                .toUpperCase();
        Utils.LOG("macstr=" + macstr);
//        Utils.LOG("setLanMacAddr=" + wyfac.setLanMacAddr(macstr));

        return true;
    }

    public EnumKeyPad eventSystemKeyPadGetPressingKey() {
        String KeypadStatus = SysProp.get(Utils.CVTE_PRESS_KEY, "7");
        Utils.LOG("<CVTE-AT> eventSystemKeyPadGetPressingKey KeypadStatus:" + KeypadStatus);
        SysProp.set(Utils.CVTE_PRESS_KEY, "7");
        return EnumKeyPad.values()[Integer.parseInt(KeypadStatus)];
    }

    private static final String CMD_USBStatus = "USBStatus";
    private static final String CMD_SendData = "SendData";
    private static final String CMD_CatchID = "CatchID";
    private static String CMD = Utils.NoFun;
    private static int cmdtyppe_global = -1;
    private static String cmdstr_global = Utils.NoFun;
    private static int catchid_global_val = -1;

    public boolean eventSystemSetFactoryData(EnumFactoryDataType factoryDataTypeSn, String str) {
        switch (factoryDataTypeSn) {
            case FACTORY_DATA_TYPE_SN:
                return false;
            case FACTORY_DATA_TYPE_UUID:
                Utils.LOG("<TvApi> Save FACTORY_DATA_TYPE_UUID:" + str);
                if (str.startsWith(HEAD_CMD)) {
                    String strarray[] = str.split(":");
                    cmdtyppe_global = -1;
                    cmdstr_global = Utils.NoFun;

                    //"CMD:" + rec
                    if (strarray[1].equals(CMD_USBStatus) && (strarray.length == 2)) {
                        CMD = CMD_USBStatus;
                        return true;
                    }
                    //"CMD:" + rec + ":" + cmdtype + ":" + ArrayInt2String(cmddata)
                    else if (strarray[1].equals(CMD_SendData) && (strarray.length == 4)) {
                        cmdtyppe_global = Integer.parseInt(strarray[2]);
                        cmdstr_global = strarray[3];
                        CMD = CMD_SendData;
                        Utils.LOG("cmdtyppe_global=" + cmdtyppe_global + " cmdstr_global=" + cmdstr_global);
                        return true;
                    }
                }
                break;
            case FACTORY_DATA_TYPE_DEVICEID:
                break;
        }

        return false;
    }

    private final static String HEAD_CMD = "CMD:";
    private final static String HEAD_BurnData = "BurnData:";
    private final static String HEAD_CatchID = "CatchID:";

    public String eventSystemGetFactoryData(EnumFactoryDataType factoryDataTypeSn) {
        switch (factoryDataTypeSn) {
            case FACTORY_DATA_TYPE_SN:
                String str = Utils.NoStr;//wyfac.getSN();
                if (str == null)
                    str = Utils.NoStr;
                return str;
            case FACTORY_DATA_TYPE_UUID:
                if (CMD.equals(CMD_SendData)) {
                    String rec = Utils.NoFun;
                    Utils.LOG("Get cmdtyppe_global=" + cmdtyppe_global + " cmdstr_global=" + cmdstr_global);
                    if ((cmdtyppe_global != -1) && (!Utils.NoFun.equals(cmdstr_global))) {
                        rec = "REC:" + cmdtyppe_global + ":";//REC:type:String:reltype
                        rec += SendCmd(cmdtyppe_global, cmdstr_global);
                    }
                    Utils.LOG("rec==>" + rec);
                    CMD = Utils.NoFun;
                    return rec;
                }
                break;
            case FACTORY_DATA_TYPE_DEVICEID:
                break;
        }
        return "SNError";
    }

    private String RelBool(boolean stat) {
        return (stat ? "1" : "0") + Utils.RelBool;
    }

    private String RelOnlyOne(String str) {
        return str + Utils.RelOnlyOne;
    }

    private String RelSameDef(String str) {
        return str + Utils.RelSameDef;
    }

    public String SendCmd(int cmdtyppe_global, String cmdstr_global) {
        String str = Utils.NoFun;
        return str;
    }


    public byte[] eventSystemHdcp1xKeyGetKSV() {
//        byte[] KSVBuf = new byte[Utils.HDCP_KSV_Space];
//        byte[] HDCPBuf = wyfac.getHDCPKey();
//        for (int i = 0; i < Utils.HDCP_KSV_Space; i++)
//            KSVBuf[i] = HDCPBuf[i];
        return Utils.HDCP_KSV_def;
    }

    public boolean eventCheckWifiAutoTest() {
        Utils.LOG("<AT> Start eventCheckWifiAutoTest");
        String WIFIState = Utils.FAIL;
        FacAPI.getInstance(getmContext()).StartWifiTest();
        try {
            Utils.LOG("<AT> wait 1000");
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            WIFIState = FacAPI.getInstance(getmContext()).CheckWifiTestStatus();
            if (Utils.OK.equals(WIFIState)) {
                Utils.LOG("<AT> CheckWifiTestStatus = OK");
                return true;
            } else if (Utils.FAIL.equals(WIFIState)) {
                Utils.LOG("<AT> CheckWifiTestStatus = FAIL");
                return false;
            }
        }
    }

    public int eventUsbGetStorageDeviceInsertedCount() {
        return FacAPI.USBCount();
    }

    public boolean eventCheckBluetoothAT() {
        Utils.LOG("<AT> Start eventCheckBluetoothAT");
        String BluetoothTestState = Utils.FAIL;
        FacAPI.getInstance(getmContext()).StartBluetoothTest();
        while (true) {
            BluetoothTestState = FacAPI.getInstance(getmContext()).CheckBluetoothTestState();
            if (Utils.OK.equals(BluetoothTestState)) {
                Utils.LOG("<AT> BluetoothTestState = OK");
                return true;
            } else if (Utils.FAIL.equals(BluetoothTestState)) {
                Utils.LOG("<AT> BluetoothTestState = FAIL");
                return false;
            }
        }
    }

    public boolean eventSystemDebugPermissionEnable() {
        return true;
    }


    private List<EntityInputSource> inputSrcList = null;
    private static EntityInputSource currentSrc = null;  //record the current source's EntityInputSource
    private ArrayList<Integer> mSourceList = new ArrayList<Integer>();

    public void generateInputSourceList() {
//        Utils.LOG("===>>> generateInputSourceList ==>>> ");
        if ((HitvManager.getInstance().getSourceManager().getCurSourceId(0) >= EnumSourceIndex.SOURCE_AUTO) || (currentSrc == null)) {
//            Utils.LOG("Init Input Source List");
//            HitvManager.getInstance().getSourceManager().deselectSource(HitvManager.getInstance().getSourceManager().getCurSourceId(0), true);
//            HitvManager.getInstance().getSourceManager().selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
            currentSrc = new EntityInputSource(EnumSourceIndex.SOURCE_ATV, getmContext().getResources().getString(R.string.SOURCE_ATV),
                    EnumInputStatus.INPUT_STATUS_CONNECTED,
                    EnumInputSourceCategory.INPUTSOURCE_ATV);
        }

        mSourceList = HitvManager.getInstance().getSourceManager().getSourceList();
        int sourceIndex = 0, avaIndex = 0;
        EnumInputStatus mInputStatus = EnumInputStatus.INPUT_STATUS_DISCONNECT;
//        Utils.LOG("mSourceList is " + mSourceList.toString());
//        Utils.LOG("mSourceList.size() = " + mSourceList.size());

        if (inputSrcList != null && inputSrcList.size() > 0) {
//            Utils.LOG("==>> inputSrc Not null, no need to get.. ");
            return;
        }
        inputSrcList = new ArrayList<EntityInputSource>();
        mInputStatus = EnumInputStatus.INPUT_STATUS_CONNECTED;
        for (sourceIndex = 0; sourceIndex < mSourceList.size(); sourceIndex++) {

            if (mSourceList.get(sourceIndex) == EnumSourceIndex.SOURCE_ATV) {
                inputSrcList.add(new EntityInputSource(mSourceList.get(sourceIndex), getmContext().getResources().getString(R.string.SOURCE_ATV),
                        mInputStatus,
                        EnumInputSourceCategory.INPUTSOURCE_ATV));
            }
            if (mSourceList.get(sourceIndex) == EnumSourceIndex.SOURCE_DTMB) {
                inputSrcList.add(new EntityInputSource(mSourceList.get(sourceIndex), getmContext().getResources().getString(R.string.SOURCE_DTV),
                        mInputStatus,
                        EnumInputSourceCategory.INPUTSOURCE_DTMB));
            }
            if (mSourceList.get(sourceIndex) == EnumSourceIndex.SOURCE_CVBS1) {
                inputSrcList.add(new EntityInputSource(mSourceList.get(sourceIndex), getmContext().getResources().getString(R.string.SOURCE_AV1),
                        mInputStatus,
                        EnumInputSourceCategory.INPUTSOURCE_AV));
            }
            if (mSourceList.get(sourceIndex) == EnumSourceIndex.SOURCE_CVBS2) {
                inputSrcList.add(new EntityInputSource(mSourceList.get(sourceIndex), getmContext().getResources().getString(R.string.SOURCE_AV2),
                        mInputStatus,
                        EnumInputSourceCategory.INPUTSOURCE_AV));
            }
            if (mSourceList.get(sourceIndex) == EnumSourceIndex.SOURCE_VGA) {
                inputSrcList.add(new EntityInputSource(mSourceList.get(sourceIndex), getmContext().getResources().getString(R.string.SOURCE_VGA),
                        mInputStatus,
                        EnumInputSourceCategory.INPUTSOURCE_PC));
            }

            if (mSourceList.get(sourceIndex) == EnumSourceIndex.SOURCE_YPBPR1) {
                inputSrcList.add(new EntityInputSource(mSourceList.get(sourceIndex), getmContext().getResources().getString(R.string.SOURCE_YPBPR),
                        mInputStatus,
                        EnumInputSourceCategory.INPUTSOURCE_YPBPR));
            }
            if (mSourceList.get(sourceIndex) == EnumSourceIndex.SOURCE_YPBPR2) {
                inputSrcList.add(new EntityInputSource(mSourceList.get(sourceIndex), getmContext().getResources().getString(R.string.SOURCE_YPBPR2),
                        mInputStatus,
                        EnumInputSourceCategory.INPUTSOURCE_YPBPR));
            }
            if (mSourceList.get(sourceIndex) == EnumSourceIndex.SOURCE_HDMI1) {
                inputSrcList.add(new EntityInputSource(mSourceList.get(sourceIndex), getmContext().getResources().getString(R.string.SOURCE_HDMI1),
                        mInputStatus,
                        EnumInputSourceCategory.INPUTSOURCE_HDMI));
            }
            if (mSourceList.get(sourceIndex) == EnumSourceIndex.SOURCE_HDMI2) {
                inputSrcList.add(new EntityInputSource(mSourceList.get(sourceIndex), getmContext().getResources().getString(R.string.SOURCE_HDMI2),
                        mInputStatus,
                        EnumInputSourceCategory.INPUTSOURCE_HDMI));
            }
            if (mSourceList.get(sourceIndex) == EnumSourceIndex.SOURCE_HDMI3) {
                inputSrcList.add(new EntityInputSource(mSourceList.get(sourceIndex), getmContext().getResources().getString(R.string.SOURCE_HDMI3),
                        mInputStatus,
                        EnumInputSourceCategory.INPUTSOURCE_HDMI));
            }
            if (mSourceList.get(sourceIndex) == EnumSourceIndex.SOURCE_MEDIA) {
                sourceIndex++;//source media2 will crush
            }

//            int curSourceId = HitvManager.getInstance().getSourceManager().getCurSourceId(0);
            //int bootSrc = HitvManager.getInstance().getSourceManager().getBootSource();
            //Utils.LOG("==>> bootSrc === " + bootSrc);

            //if (bootSrc == mSourceList.get(sourceIndex)) {
            //if (mSourceList.get(sourceIndex) != EnumSourceIndex.SOURCE_MEDIA) {
            //    currentSrc = inputSrcList.get(inputSrcList.size() - 1);//source media2 will crush
            //    Utils.LOG("==>>> Now the curSrc id = " + currentSrc.id + "; name = " + currentSrc.name);
            //}
            //}
        }
//        Utils.LOG("inputSrcList.size=" + inputSrcList.size());
//        Utils.LOG("CurSrc is " + HitvManager.getInstance().getSourceManager().getCurSourceId(0));
        int size = inputSrcList.size();
        for (int i = 0; i < size; i++) {
//            Utils.LOG("inputSrcList[" + i + "]" + inputSrcList.get(i).category);

        }
    }


    public List<EntityInputSource> eventSystemInputSourceGetList() {
//        Utils.LOG("eventSystemInputSourceGetList");
        if (inputSrcList == null)
            generateInputSourceList();
//        Utils.LOG("inputSrcList.size=" + inputSrcList.size());
        return inputSrcList;
    }

    public boolean eventSystemInputSourceSetInputSource(int targetSrc) {
        if ((inputSrcList == null) || (currentSrc == null)) {
            generateInputSourceList();
        }
        Utils.LOG("eventSystemInputSourceSetInputSource targetSrc=" + targetSrc);
        Utils.LOG("cur source is " + currentSrc.id + "    set source to " + targetSrc);
        int curSource = TvManagerHelper.getInstance(getmContext()).getCurrentSource();
        Utils.LOG("++++==>> curSource === " + curSource);

        if (curSource == targetSrc) {
            Utils.LOG("==>>> sourceId is equals target SourceId, Not need to change source.");
            return true;
        }

        int tempSrc;
        for (tempSrc = 0; tempSrc < inputSrcList.size(); tempSrc++) {
            if (inputSrcList.get(tempSrc).id == targetSrc) {
                Utils.LOG("==>> Now update the currentSrc, Not include playback ==>> " + currentSrc.id);
                currentSrc = inputSrcList.get(tempSrc);
            }
        }
        HitvManager.getInstance().getSourceManager().deselectSource(currentSrc.id, true);
        HitvManager.getInstance().getSourceManager().selectSource(targetSrc, 0);
        HitvManager.getInstance().getSourceManager().setBootSource(targetSrc);

        if (targetSrc == EnumSourceIndex.SOURCE_DTMB) {
            if (sHisiapi == null)
                sHisiapi = new HisiFunAPI(sContext);
            sHisiapi.dtvEvnInit();
            eventTVChannelsGetChannelList(EnumInputSourceCategory.INPUTSOURCE_DTMB);
        } else {
            if (sHisiapi == null)
                sHisiapi = new HisiFunAPI(sContext);
            sHisiapi.dtvEvnDeinit();
            if (targetSrc == EnumSourceIndex.SOURCE_ATV) {
                eventTVChannelsGetChannelList(EnumInputSourceCategory.INPUTSOURCE_ATV);
            }
        }

        Utils.LOG("==>>> start to notifySystemInputSourceChange");
        eventTVChannelsGetChannel();
        return true;
    }

    public EntityInputSource eventSystemInputSourceGetInputSource() {
//        Utils.LOG("eventSystemInputSourceGetInputSource");
//        if ((inputSrcList == null) || (currentSrc == null))
        generateInputSourceList();
//        Utils.LOG("eventSystemInputSourceGetInputSource; ->curSrc.id = " + currentSrc.id + "; curSrc.name = " + currentSrc.name);
        return currentSrc;
    }

    public int perSrc = -1;

    public boolean eventSystemInputSourceEnable(boolean status) {
        int curSource = TvManagerHelper.getInstance(getmContext()).getCurrentSource();
        Utils.LOG("<pattern> eventSystemInputSourceEnable status=" + status + " curSource=" + curSource);
        if ((curSource == EnumSourceIndex.SOURCE_ATV) || (curSource == EnumSourceIndex.SOURCE_DTMB))
            ;
        else {
            if (status) {
                if (curSource == EnumSourceIndex.SOURCE_MEDIA) {
                    if (perSrc == -1)
                        perSrc = curSource;
                    curSource = perSrc;
                }
//                HitvManager.getInstance().getSourceManager().deselectSource(curSource, true);
                HitvManager.getInstance().getSourceManager().deselectSource(curSource, true);
                HitvManager.getInstance().getSourceManager().selectSource(curSource, 0);
            } else {

                if (curSource != EnumSourceIndex.SOURCE_MEDIA) {
                    perSrc = curSource;
                }
                Utils.LOG("<pattern> eventSystemInputSourceEnable status=" + status + " EnumSourceIndex.SOURCE_MEDIA=" + EnumSourceIndex.SOURCE_MEDIA);
//                HitvManager.getInstance().getSourceManager().deselectSource(curSource, true);
                HitvManager.getInstance().getSourceManager().selectSource(EnumSourceIndex.SOURCE_MEDIA, 0);
            }
        }
        return true;
    }

    public boolean eventSoundSpeakerOutSetVolume(int i) {
        HitvManager.getInstance().getAudio().setVolume(EnumSoundChannel.CHANNEL_SPEAKER, i);
        HitvManager.getInstance().getAudio().setVolume(EnumSoundChannel.CHANNEL_HEADPHONE, i);

        HitvManager.getInstance().getAudio().setMute(EnumSoundChannel.CHANNEL_SPEAKER, false);
        HitvManager.getInstance().getAudio().setMute(EnumSoundChannel.CHANNEL_HEADPHONE, false);
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
        return true;
    }

    //要注意静态变量的维护
    public static List<EntityChannel> channelList = null; //每次在频道列表上移动光标时，都重新获取了所有频道信息，因此使用静态变量用于记录信息。提高唤出频道列表速度
    public static EntityChannel mEntityChannel = null; //因为tvsetting频繁获取频道数据，所以使用静态变量用于记录频道信息。
    private Map<Integer, Integer> chid2Lcn = new HashMap<Integer, Integer>();
    private Map<Integer, EntityChannel> chid2EntityChannel = new HashMap<Integer, EntityChannel>();  // Use to store.

    private int mChidMax = -1, mChidMin = 10000; //用于CH+ CH-切台
    private int oldChId_atv = -1, curChId_atv = -1; //用于CHANNEL_RECALL
    private int oldChId_dtv = -1, curChId_dtv = -1; //用于CHANNEL_RECALL

    private long before = 0;

    public void inimPlayer() {
    }

    public List<EntityChannel> eventTVChannelsGetChannelList(EnumInputSourceCategory enumInputSourceCategory) {

        Utils.LOG("invoke eventTVChannelsGetChannelList: " + enumInputSourceCategory);

//        int ChId = 0;
        int channelId = 0;
        int tvCount = 0;

        synchronized (mutex) {
            channelList = new ArrayList<EntityChannel>();
            chid2Lcn.clear();
            chid2EntityChannel.clear();
            channelList.clear();

            switch (enumInputSourceCategory) {
                case INPUTSOURCE_DTMB:
                    break;
                case INPUTSOURCE_ATV:
                    ArrayList<TvProgram> ProgList = AtvChannelImpl.getInstance().getProgList();
                    tvCount = ProgList.size();
                    if (tvCount == 0) {
                        break;
                    }
                    Utils.LOG("INPUTSOURCE_ATV tvCount=" + tvCount);
                    channelId = 0;
                    for (int k = 0; k < tvCount; k++) {
                        TvProgram tvProgram = ProgList.get(k);
                        if (tvProgram == null) continue;

                        EntityChannel entityChannel = new EntityChannel();
                        entityChannel.serviceName = tvProgram.getStrName();
                        entityChannel.serviceType = EnumChannelType.CHANNEL_TYPE_VIDEO;
                        entityChannel.tvCategory = EnumInputSourceCategory.INPUTSOURCE_ATV;
                        entityChannel.channelSkiped = getATVSkipFlag(tvProgram);
                        entityChannel.channelLocked = false;
                        entityChannel.channelFavoriteGroup = getATVFavFlag(tvProgram);
                        entityChannel.channelScrambled = false;

                        // Not show the ATV service name.
//                        if ("".equals(entityChannel.serviceName) || null == entityChannel.serviceName) {
//                            entityChannel.serviceName = "CH" + (900 + k);
//                        }

                        channelId = tvProgram.getiId();
                        entityChannel.channelId = channelId;

//                        entityChannel.LCN = tvProgram.getiId();
                        entityChannel.LCN = k + 1; //Not use zero

                        chid2Lcn.put(entityChannel.channelId, channelId);   //根据chid存放，到时可根据chid读取

                        chid2EntityChannel.put(entityChannel.channelId, entityChannel);

                        if (mChidMin > entityChannel.channelId)
                            mChidMin = entityChannel.channelId;//用于切台
                        if (mChidMax < entityChannel.channelId) mChidMax = entityChannel.channelId;

                        Utils.LOG("entityChannel.LCN  = " + entityChannel.LCN);
                        Utils.LOG("entityChannel.serviceName111 = " + entityChannel.serviceName);
                        channelList.add(entityChannel);
                    }
                    break;
                default:
                    break;
            }
            return channelList;
        }
    }

    void printfEntityChannel(EntityChannel mInfo) {
        Utils.LOG("mEntityChannel.channelId =  " + mInfo.channelId);
        Utils.LOG("mEntityChannel.LCN =  " + mInfo.LCN);
        Utils.LOG("mEntityChannel.serviceName = " + mInfo.serviceName);
        Utils.LOG("mEntityChannel.serviceType =  " + mInfo.serviceType);
        Utils.LOG("mEntityChannel.tvCategory =  " + mInfo.tvCategory);
        Utils.LOG("mEntityChannel.channelSkiped =  " + mInfo.channelSkiped);
        Utils.LOG("mEntityChannel.channelLocked = " + mInfo.channelLocked);
        Utils.LOG("mEntityChannel.channelFavoriteGroup = " + mInfo.channelFavoriteGroup);
        Utils.LOG("mEntityChannel.channelScrambled = " + mInfo.channelScrambled);
    }

    public EntityChannel eventTVChannelsGetChannel() {

        Utils.LOG("eventTVChannelsGetChannel");
        inimPlayer();
        // chid2EntityChannel   will have value after call eventTVChannelsGetChannelList, should not use its size  in this.
        // Because TvSetting will call eventTVChannelsGetChannel to decide whether to popup scanning prompt.

        if (mEntityChannel != null && (chid2EntityChannel.get(mEntityChannel.channelId) != null)
                && mEntityChannel.serviceName.equals(chid2EntityChannel.get(mEntityChannel.channelId).serviceName)) //不再重复获取
        {
//            Utils.LOG("oldChId=" + oldChId + " curChId=" + curChId);
            return mEntityChannel;
        }
        final int inputSouce = HitvManager.getInstance().getSourceManager().getCurSourceId(0);
        int channelId = 0;

        EntityChannel entityChannel = new EntityChannel();
        entityChannel.channelId = 0;
        entityChannel.serviceName = "";
        entityChannel.LCN = 0;  // Not Use LCN, Use it to be channelNum in here.

        switch (inputSouce) {
            case EnumSourceIndex.SOURCE_ATV:
                AtvChannel atvChannel = HitvManager.getInstance().getAtvChannel();
                if (atvChannel != null) {
                    channelId = atvChannel.getCurProgNumber();
                    int currpro = atvChannel.getCurProgNumber();
                    TvProgram tvProgram = atvChannel.getProgInfo(currpro);
                    entityChannel.LCN = tvProgram.getiId();
                    entityChannel.tvCategory = EnumInputSourceCategory.INPUTSOURCE_ATV;
                    entityChannel.serviceType = EnumChannelType.CHANNEL_TYPE_VIDEO;
                    entityChannel.serviceName = tvProgram.getStrName();
                    entityChannel.channelScrambled = false;
                    entityChannel.channelFavoriteGroup = getATVFavFlag(tvProgram);
                    entityChannel.channelSkiped = getATVSkipFlag(tvProgram);
                }
                break;
            case EnumSourceIndex.SOURCE_DTMB:
                break;
            default:
                Utils.LOG("==>>> Not the Tv Source... inputSource = " + inputSouce);
                break;
        }

        entityChannel.channelLocked = false;
        entityChannel.channelId = channelId;
        if (currentSrc.id == EnumSourceIndex.SOURCE_ATV) {
            curChId_atv = channelId;
        } else if (currentSrc.id == EnumSourceIndex.SOURCE_DTMB) {
            curChId_dtv = channelId;
        }
        mEntityChannel = entityChannel;
        return entityChannel;
    }

    public boolean eventTVChannelsTuneChannelById(SourceEnum nowSrc, int channelId, List<EntityChannel> chdata) {

        synchronized (mutex) {
            Utils.LOG("eventTVChannelsTuneChannelById channelId=" + channelId);
            inimPlayer();
            channelId = channelId - 1;
            if (channelId < 0)
                channelId = 0;
            mEntityChannel = chdata.get(channelId); //get first time
            Utils.LOG("LCN=" + mEntityChannel.LCN);

            if (mEntityChannel == null) {
                Utils.LOG("mEntityChannel == eventTVChannelsGetChannel().tvCategory=" + eventTVChannelsGetChannel().tvCategory);
//                eventTVChannelsGetChannelList(eventTVChannelsGetChannel().tvCategory);
                mEntityChannel = eventTVChannelsGetChannelList(eventTVChannelsGetChannel().tvCategory).get(channelId); //get next time
            }
            if (mEntityChannel == null) {
                Utils.LOG("mEntityChannel is null ");
                return false;
            }

            if (SourceEnum.isDTV(nowSrc)) {
                oldChId_dtv = curChId_dtv;
                curChId_dtv = channelId;
            } else if (SourceEnum.isATV(nowSrc)) {
                AtvChannelImpl.getInstance().selectProg(channelId + 1);
                oldChId_atv = curChId_atv;
                curChId_atv = channelId;
            }

            return true;
        }
    }

    private int getATVFavFlag(TvProgram tvProgram) {
        if (tvProgram != null && tvProgram.getStChannelAttr() != null) {
            boolean favFlag = tvProgram.getStChannelAttr().isbFavorite();
            Utils.LOG("==>> ATV is favFlag ==>> " + favFlag);
            if (favFlag) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private boolean getATVSkipFlag(TvProgram tvProgram) {
        boolean bSkip = false;
        if (tvProgram != null && tvProgram.getStChannelAttr() != null) {
            bSkip = tvProgram.getStChannelAttr().isbSkip();
            Utils.LOG("==>>> ATV skip Flag = " + bSkip);
        }
        return bSkip;
    }

    public boolean eventSoundSpeakerOutIsMute() {
//        mIsMute = mAudioManager.isStreamMute(AudioManager.STREAM_MUSIC);
        return HitvManager.getInstance().getAudio().getMute(EnumSoundChannel.CHANNEL_SPEAKER);
    }

    public boolean eventSoundSpeakerOutSetMute(boolean b) {
        //Set sound mute type for user speaker
        Utils.LOG("eventSoundSpeakerOutSetMute");
//        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, b);
        HitvManager.getInstance().getAudio().setMute(EnumSoundChannel.CHANNEL_SPEAKER, b);
//        mIsMute = b;
        return false;
    }

    public void eventSystemLedLightSetStatus(EnumLedStatus stat) {
//        CusEx mCusEx = HitvManager.getInstance().getCusEx();
//        if (mCusEx != null) {
//            mCusEx.cus_setLEDLightConfig(mCusEx.cus_getLEDLightConfig());
//            Utils.LOG("cus_getLEDMode=" + mCusEx.cus_getLEDMode());
//            switch (stat) {
//                case LED_STATUS_OFF:
//                    Utils.LOG("cus_LEDLightOff=" + mCusEx.cus_LEDLightOff());
//                    break;
//                case LED_STATUS_GREEN_ON:
//                    break;
//                case LED_STATUS_RED_ON:
//                    break;
//                case LED_STATUS_FULL_ON:
//                    break;
//            }
//        }
    }

}
