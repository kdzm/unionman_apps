package com.um.tv.menu.model;

import java.text.DecimalFormat;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.hisilicon.android.tvapi.CusFactory;
import com.hisilicon.android.tvapi.Picture;
import com.hisilicon.android.tvapi.impl.PictureImpl;
import com.hisilicon.android.tvapi.impl.SourceManagerImpl;
import com.hisilicon.android.tvapi.vo.ADCCalibrationInfo;
import com.hisilicon.android.tvapi.vo.ColorTempInfo;
import com.hisilicon.android.tvapi.vo.NLAPointInfo;
import com.hisilicon.android.tvapi.vo.RectInfo;
import com.um.tv.menu.R;
import com.um.tv.menu.ui.FactoryWindow;
import com.um.tv.menu.utils.Utils;

public class RangeModel extends FunctionModel {
    private static final String TAG = "RangeModel";

    public static final int TypeADCRGain = 0;
    public static final int TypeADCGGain = 1;
    public static final int TypeADCBGain = 2;
    public static final int TypeADCROffset = 3;
    public static final int TypeADCGOffset = 4;
    public static final int TypeADCBOffset = 5;

    public static final int TypeWhiteBlanceRGain = 6;
    public static final int TypeWhiteBlanceGGain = 7;
    public static final int TypeWhiteBlanceBGain = 8;
    public static final int TypeWhiteBlanceROffset = 9;
    public static final int TypeWhiteBlanceGOffset = 10;
    public static final int TypeWhiteBlanceBOffset = 11;

    public static final int TypeOverscanHSize = 12;
    public static final int TypeOverscanHPosition = 13;
    public static final int TypeOverscanVSize = 14;
    public static final int TypeOverscanVPosition = 15;

    public static final int TypePictureBrightness = 16;
    public static final int TypePictureContrast = 17;
    public static final int TypePictureSaturation = 18;
    public static final int TypePictureHue = 19;
    public static final int TypePictureSharpness = 20;
    public static final int TypePictureBacklight = 21;

    public static final int TypeNonLinearOSD0 = 22;
    public static final int TypeNonLinearOSD25 = 23;
    public static final int TypeNonLinearOSD50 = 24;
    public static final int TypeNonLinearOSD75 = 25;
    public static final int TypeNonLinearOSD100 = 26;

    public static final int TypeSSCDDREneable = 27;
    public static final int TypeSSCDDRModulation = 28;
    public static final int TypeSSCDDRPercentage = 29;
    public static final int TypeSSCLVDSEneable = 30;
    public static final int TypeSSCLVDSModulation = 31;
    public static final int TypeSSCLVDSPercentage = 32;
    public static final int TypeSSCPanelSwing = 33;

    public static final int TypeAVCThreshold = 34;
    
    public static final int TypeLvdsSpreadRatio = 35;
    public static final int TypeLvdsSpreadFreq = 36;
    public static final int TypeLvdsDrvCurrent = 37;
    public static final int TypeLvdsComVoltage = 38;
    
    public static final int TypeDdrSpreadRatio = 39;
    public static final int TypeDdrSpreadFreq = 40;
    
    public static final int TypeVBOSpreadRatio = 41;
    public static final int TypeVBOSpreadFreq = 42;
    public static final int TypeVBODrvCurrent = 43;

    public static final int TypeGMACSpreadRatio = 44;
    public static final int TypeGMACSpreadFreq = 45;
    public static final int TypeGMACDrvCurrent = 46;
    
    public static final int TypeUSBSpreadRatio = 47;
    public static final int TypeUSBSpreadFreq = 48;
    public static final int TypeUSBDrvCurrent = 49;

    private int mType = -1;
    private int mSourceId = 0;
    private int mMaxValue = 0;
    private int mMinValue = 0;
    private int mDigits = 0;

    private NumberSystem mNumber = NumberSystem.Decimal;

    enum NumberSystem {
        Binary,
        Decimal,
        Hexadecimal
    }

    private TextView mTvValue = null;
    private int mCurrentValue = 0;

    public RangeModel(Context context, FactoryWindow window, CusFactory factory, int type) {
        super(context, window, factory);
        // TODO Auto-generated constructor stub
        mType = type;
        init();
    }

    public void changeSourece(int id) {
        mSourceId = id;
        mWindow.invalidateViews();
    }

    @Override
    public View getView(Context context, int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null || !convertView.getTag().equals(ViewTagRange)) {
            convertView = LayoutInflater.from(context).inflate(R.layout.range_layout, null);
            convertView.setTag(ViewTagRange);
        }
        init();
        TextView tvName = (TextView)convertView.findViewById(R.id.tv_name);
        tvName.setText(mName);
        mTvValue = (TextView)convertView.findViewById(R.id.tv_value);
        mTvValue.setText(getValueStr());
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void init() {
        ADCCalibrationInfo adcInfo = null;
        ColorTempInfo wbInfo = null;
        RectInfo overscanInfo = null;
        PictureImpl pic = PictureImpl.getInstance();
        NLAPointInfo nl = mFactory.getNLAPoint(mSourceId);

        switch (mType) {
            case TypeADCRGain:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                mCurrentValue = adcInfo.getrGain();
                Log.d(TAG,"init()--->ADC RGain:" + Integer.toHexString(mCurrentValue) + "    SourceID:" + mSourceId);
                mMaxValue = Utils.ADCRGainMax;
                mMinValue = Utils.ADCRGainMin;
                mDigits = 0;
                mNumber = NumberSystem.Hexadecimal;
                break;
            case TypeADCGGain:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                mCurrentValue = adcInfo.getgGain();
                Log.d(TAG,"init()--->ADC GGain:" + Integer.toHexString(mCurrentValue) + "    SourceID:" + mSourceId);
                mMaxValue = Utils.ADCGGainMax;
                mMinValue = Utils.ADCGGainMin;
                mDigits = 0;
                mNumber = NumberSystem.Hexadecimal;
                break;
            case TypeADCBGain:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                mCurrentValue = adcInfo.getbGain();
                Log.d(TAG,"init()--->ADC BGain:" + Integer.toHexString(mCurrentValue) + "    SourceID:" + mSourceId);
                mMaxValue = Utils.ADCBGainMax;
                mMinValue = Utils.ADCBGainMin;
                mDigits = 0;
                mNumber = NumberSystem.Hexadecimal;
                break;
            case TypeADCROffset:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                mCurrentValue = adcInfo.getrOffset();
                Log.d(TAG,"init()--->ADC ROffset:" + Integer.toHexString(mCurrentValue) + "    SourceID:" + mSourceId);
                mMaxValue = Utils.ADCROffsetMax;
                mMinValue = Utils.ADCROffsetMin;
                mDigits = 0;
                mNumber = NumberSystem.Hexadecimal;
                break;
            case TypeADCGOffset:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                mCurrentValue = adcInfo.getgOffset();
                Log.d(TAG,"init()--->ADC GOffset:" + Integer.toHexString(mCurrentValue) + "    SourceID:" + mSourceId);
                mMaxValue = Utils.ADCGOffsetMax;
                mMinValue = Utils.ADCGOffsetMin;
                mDigits = 0;
                mNumber = NumberSystem.Hexadecimal;
                break;
            case TypeADCBOffset:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                mCurrentValue = adcInfo.getbOffset();
                Log.d(TAG,"init()--->ADC BOffset:" + Integer.toHexString(mCurrentValue) + "    SourceID:" + mSourceId);
                mMaxValue = Utils.ADCBOffsetMax;
                mMinValue = Utils.ADCBOffsetMin;
                mDigits = 0;
                mNumber = NumberSystem.Hexadecimal;
                break;

            case TypeWhiteBlanceRGain:
                wbInfo = mFactory.getColorTemp(mSourceId);
                mCurrentValue = wbInfo.getrGain();
                mMaxValue = Utils.WhiteBlanceRGainMax;
                mMinValue = Utils.WhiteBlanceRGainMin;
                mDigits = 0;
                break;
            case TypeWhiteBlanceGGain:
                wbInfo = mFactory.getColorTemp(mSourceId);
                mCurrentValue = wbInfo.getgGain();
                mMaxValue = Utils.WhiteBlanceGGainMax;
                mMinValue = Utils.WhiteBlanceGGainMin;
                mDigits = 0;
                break;
            case TypeWhiteBlanceBGain:
                wbInfo = mFactory.getColorTemp(mSourceId);
                mCurrentValue = wbInfo.getbGain();
                mMaxValue = Utils.WhiteBlanceBGainMax;
                mMinValue = Utils.WhiteBlanceBGainMin;
                mDigits = 0;
                break;
            case TypeWhiteBlanceROffset:
                wbInfo = mFactory.getColorTemp(mSourceId);
                mCurrentValue = wbInfo.getrOffset();
                mMaxValue = Utils.WhiteBlanceROffsetMax;
                mMinValue = Utils.WhiteBlanceROffsetMin;
                mDigits = 0;
                break;
            case TypeWhiteBlanceGOffset:
                wbInfo = mFactory.getColorTemp(mSourceId);
                mCurrentValue = wbInfo.getgOffset();
                mMaxValue = Utils.WhiteBlanceGOffsetMax;
                mMinValue = Utils.WhiteBlanceGOffsetMin;
                mDigits = 0;
                break;
            case TypeWhiteBlanceBOffset:
                wbInfo = mFactory.getColorTemp(mSourceId);
                mCurrentValue = wbInfo.getbOffset();
                mMaxValue = Utils.WhiteBlanceBOffsetMax;
                mMinValue = Utils.WhiteBlanceBOffsetMin;
                mDigits = 0;
                break;

            case TypeOverscanHSize:
                overscanInfo = mFactory.getOverscan();
                mCurrentValue = overscanInfo.getW();
                Log.d(TAG,"init()--->OverscanHSize:" + mCurrentValue + "    SourceID:" + SourceManagerImpl.getInstance().getCurSourceId(0));
                mMaxValue = Utils.OverscanHSizeMax;
                mMinValue = Utils.OverscanHSizeMin;
                mDigits = 0;
                break;
            case TypeOverscanHPosition:
                overscanInfo = mFactory.getOverscan();
                mCurrentValue = overscanInfo.getX();
                Log.d(TAG,"init()--->OverscanHPosition:" + mCurrentValue + "    SourceID:" + SourceManagerImpl.getInstance().getCurSourceId(0));
                mMaxValue = Utils.OverscanHPositionMax;
                mMinValue = Utils.OverscanHPositionMin;
                mDigits = 0;
                break;
            case TypeOverscanVSize:
                overscanInfo = mFactory.getOverscan();
                mCurrentValue = overscanInfo.getH();
                Log.d(TAG,"init()--->OverscanVSize:" + mCurrentValue + "    SourceID:" + SourceManagerImpl.getInstance().getCurSourceId(0));
                mMaxValue = Utils.OverscanVSizeMax;
                mMinValue = Utils.OverscanVSizeMin;
                mDigits = 0;
                break;
            case TypeOverscanVPosition:
                overscanInfo = mFactory.getOverscan();
                mCurrentValue = overscanInfo.getY();
                Log.d(TAG,"init()--->OverscanVSize:" + mCurrentValue + "    SourceID:" + SourceManagerImpl.getInstance().getCurSourceId(0));
                mMaxValue = Utils.OverscanVPositionMax;
                mMinValue = Utils.OverscanVPositionMin;
                mDigits = 0;
                break;

            case TypePictureBrightness:
                pic = PictureImpl.getInstance();
                mCurrentValue = pic.getBrightness();
                mMaxValue = Utils.PictureModeBrightnessMax;
                mMinValue = Utils.PictureModeBrightnessMin;
                mDigits = 0;
                break;
            case TypePictureContrast:
                pic = PictureImpl.getInstance();
                mCurrentValue = pic.getContrast();
                mMaxValue = Utils.PictureModeContrastMax;
                mMinValue = Utils.PictureModeContrastMin;
                mDigits = 0;
                break;
            case TypePictureSaturation:
                pic = PictureImpl.getInstance();
                mCurrentValue = pic.getSaturation();
                mMaxValue = Utils.PictureModeSaturationMax;
                mMinValue = Utils.PictureModeSaturationMin;
                mDigits = 0;
                break;
            case TypePictureHue:
                pic = PictureImpl.getInstance();
                mCurrentValue = pic.getHue();
                mMaxValue = Utils.PictureModeHueMax;
                mMinValue = Utils.PictureModeHueMin;
                mDigits = 0;
                break;
            case TypePictureSharpness:
                pic = PictureImpl.getInstance();
                mCurrentValue = pic.getSharpness();
                mMaxValue = Utils.PictureModeSharpnessMax;
                mMinValue = Utils.PictureModeSharpnessMin;
                mDigits = 0;
                break;
            case TypePictureBacklight:
                pic = PictureImpl.getInstance();
                mCurrentValue = pic.getBacklight();
                mMaxValue = Utils.PictureModeBacklightMax;
                mMinValue = Utils.PictureModeBacklightMin;
                mDigits = 0;
                break;

            case TypeNonLinearOSD0:
                nl = mFactory.getNLAPoint(mSourceId);
                mCurrentValue = nl.getX0();
                mMaxValue = getOSDRange(mSourceId, true);
                mMinValue = getOSDRange(mSourceId, false);
                mDigits = 0;
                break;
            case TypeNonLinearOSD25:
                nl = mFactory.getNLAPoint(mSourceId);
                mCurrentValue = nl.getX25();
                mMaxValue = getOSDRange(mSourceId, true);
                mMinValue = getOSDRange(mSourceId, false);
                mDigits = 0;
                break;
            case TypeNonLinearOSD50:
                nl = mFactory.getNLAPoint(mSourceId);
                mCurrentValue = nl.getX50();
                mMaxValue = getOSDRange(mSourceId, true);
                mMinValue = getOSDRange(mSourceId, false);
                mDigits = 0;
                break;
            case TypeNonLinearOSD75:
                nl = mFactory.getNLAPoint(mSourceId);
                mCurrentValue = nl.getX75();
                mMaxValue = getOSDRange(mSourceId, true);
                mMinValue = getOSDRange(mSourceId, false);
                mDigits = 0;
                break;
            case TypeNonLinearOSD100:
                nl = mFactory.getNLAPoint(mSourceId);
                mCurrentValue = nl.getX100();
                mMaxValue = getOSDRange(mSourceId, true);
                mMinValue = getOSDRange(mSourceId, false);
                mDigits = 0;
                break;

            case TypeSSCDDREneable:
                mCurrentValue = mFactory.isDdrSpreadEnable() ? 1 : 0;
                mMaxValue = 1;
                mMinValue = 0;
                mDigits = 0;
                break;
            case TypeSSCDDRModulation:
                mCurrentValue = mFactory.getDdrSpreadRatio();
                mMaxValue = Utils.SSCDDRModulationMax;
                mMinValue = Utils.SSCDDRModulationMin;
                mDigits = 0;
                break;
            case TypeSSCDDRPercentage:
                mCurrentValue = mFactory.getDdrSpreadFreq();
                mMaxValue = Utils.SSCDDRPercentageMax;
                mMinValue = Utils.SSCDDRPercentageMin;
                mDigits = 3;
                break;
            case TypeSSCLVDSModulation:
                mCurrentValue = mFactory.getLvdsSpreadRatio();
                mMaxValue = Utils.SSCLVDSModulationMax;
                mMinValue = Utils.SSCLVDSModulationMin;
                mDigits = 0;
                break;
            case TypeSSCLVDSPercentage:
                mCurrentValue = mFactory.getLvdsSpreadFreq();
                mMaxValue = Utils.SSCLVDSPercentageMax;
                mMinValue = Utils.SSCLVDSPercentageMin;
                mDigits = 3;
                break;
            case TypeSSCPanelSwing:
               // mCurrentValue = mFactory.getPanelSwing();
                mMaxValue = Utils.SSCPanelSwingMax;
                mMinValue = Utils.SSCPanelSwingMin;
                mDigits = 0;
                mNumber = NumberSystem.Hexadecimal;
                break;

            case TypeAVCThreshold:
                mCurrentValue = mFactory.getAVCThreshold();
                mMaxValue = Utils.AVCThresholdMax;
                mMinValue = Utils.AVCThresholdMin;
                mDigits = 2;
                break;
                
            case TypeLvdsSpreadRatio:
                mCurrentValue = mFactory.getLvdsSpreadRatio();
                mMaxValue = Utils.LvbsSpreadRatioMax;
                mMinValue = Utils.LvbsSpreadRatioMin;
                break;
            case TypeLvdsSpreadFreq:
                mCurrentValue = mFactory.getLvdsSpreadFreq();
                mMaxValue = Utils.LvdsSpreadFreqMax;
                mMinValue = Utils.LvdsSpreadFreqMin;
                break;
            case TypeLvdsDrvCurrent:
                mCurrentValue = mFactory.getLvdsDrvCurrent();
                mMaxValue = Utils.LvdsSpreadDrvCurrentMax;
                mMinValue = Utils.LvdsSpreadDrvCurrentMin;
                break;
            case TypeLvdsComVoltage:
                mCurrentValue = mFactory.getLvdsComVoltage();
                mMaxValue = Utils.LvdsSpreadComVoltageMax;
                mMinValue = Utils.LvdsSpreadComVoltageMin;
                break;
                
            case TypeDdrSpreadRatio:
                mCurrentValue = mFactory.getDdrSpreadRatio();
                mMaxValue = Utils.DdrSpreadRatioMax;
                mMinValue = Utils.DdrSpreadRatioMin;
                break;
            case TypeDdrSpreadFreq:
                mCurrentValue = mFactory.getDdrSpreadFreq();
                mMaxValue = Utils.DdrSpreadFreqMax;
                mMinValue = Utils.DdrSpreadFreqMin;
                break;
                
            case TypeVBOSpreadRatio:
                mCurrentValue = mFactory.getVBOSpreadRadio();
                mMaxValue = Utils.VBOSpreadRatioMax;
                mMinValue = Utils.VBOSpreadRatioMin;
                break;
            case TypeVBOSpreadFreq:
                mCurrentValue = mFactory.getVBOSpreadFreq();
                mMaxValue = Utils.VBOSpreadFreqMax;
                mMinValue = Utils.VBOSpreadFreqMin;
                break;
            case TypeVBODrvCurrent:
                mCurrentValue = mFactory.getVBODrvCur();
                mMaxValue = Utils.VBODrvCurrentMax;
                mMinValue = Utils.VBODrvCurrentMin;
                break;
                
            case TypeGMACSpreadRatio:
                mCurrentValue = mFactory.getGmacSpreadRadio();
                mMaxValue = Utils.GMACSpreadRatioMax;
                mMinValue = Utils.GMACSpreadRatioMin;
                break;
            case TypeGMACSpreadFreq:
                mCurrentValue = mFactory.getGmacSpreadFreq();
                mMaxValue = Utils.GMACSpreadFreqMax;
                mMinValue = Utils.GMACSpreadFreqMin;
                break;
            case TypeGMACDrvCurrent:
                mCurrentValue = mFactory.getGmacDrvCur();
                mMaxValue = Utils.GMACDrvCurrentMax;
                mMinValue = Utils.GMACDrvCurrentMin;
                break;
                
            case TypeUSBSpreadRatio:
                mCurrentValue = mFactory.getUSB3SpreadRadio();
                mMaxValue = Utils.USB3SpreadRatioMax;
                mMinValue = Utils.USB3SpreadRatioMin;
                break;
            case TypeUSBSpreadFreq:
                mCurrentValue = mFactory.getUSB3SpreadFreq();
                mMaxValue = Utils.USB3SpreadFreqMax;
                mMinValue = Utils.USB3SpreadFreqMin;
                break;
            case TypeUSBDrvCurrent:
                mCurrentValue = mFactory.getUSB3DrvCur();
                mMaxValue = Utils.USB3DrvCurrentMax;
                mMinValue = Utils.USB3DrvCurrentMin;
                break;
        }
    }

    @Override
    public void changeValue(int direct, int position, View view) {
        // TODO Auto-generated method stub
        ADCCalibrationInfo adcInfo = null;
        ColorTempInfo wbInfo = null;
        RectInfo ri = null;
        Picture pic = null;
        NLAPointInfo nl = null;

        if (direct < 0) {
            mCurrentValue--;
            if (mCurrentValue < mMinValue) {
                mCurrentValue = mMaxValue;
            }
        } else if (direct > 0) {
            mCurrentValue++;
            if (mCurrentValue > mMaxValue) {
                mCurrentValue = mMinValue;
            }
        }

        TextView tvValue = (TextView)view.findViewById(R.id.tv_value);
        if(tvValue != null){
            tvValue.setText(getValueStr());
        }

        switch (mType) {
            case TypeADCRGain:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                adcInfo.setrGain(mCurrentValue);
                mFactory.setADCGainOffset(mSourceId, adcInfo);
                break;
            case TypeADCGGain:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                adcInfo.setgGain((int)mCurrentValue);
                mFactory.setADCGainOffset(mSourceId, adcInfo);
                break;
            case TypeADCBGain:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                adcInfo.setbGain((int)mCurrentValue);
                mFactory.setADCGainOffset(mSourceId, adcInfo);
                break;
            case TypeADCROffset:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                adcInfo.setrOffset((int)mCurrentValue);
                mFactory.setADCGainOffset(mSourceId, adcInfo);
                break;
            case TypeADCGOffset:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                adcInfo.setgOffset((int)mCurrentValue);
                mFactory.setADCGainOffset(mSourceId, adcInfo);
                break;
            case TypeADCBOffset:
                adcInfo = mFactory.getADCGainOffset(mSourceId);
                adcInfo.setbOffset((int)mCurrentValue);
                mFactory.setADCGainOffset(mSourceId, adcInfo);
                break;

            case TypeWhiteBlanceRGain:
                wbInfo = mFactory.getColorTemp(mSourceId);
                wbInfo.setrGain((int)mCurrentValue);
                mFactory.setColorTemp(mSourceId, wbInfo);
                break;
            case TypeWhiteBlanceGGain:
                wbInfo = mFactory.getColorTemp(mSourceId);
                wbInfo.setgGain((int)mCurrentValue);
                mFactory.setColorTemp(mSourceId, wbInfo);
                break;
            case TypeWhiteBlanceBGain:
                wbInfo = mFactory.getColorTemp(mSourceId);
                wbInfo.setbGain((int)mCurrentValue);
                mFactory.setColorTemp(mSourceId, wbInfo);
                break;
            case TypeWhiteBlanceROffset:
                wbInfo = mFactory.getColorTemp(mSourceId);
                wbInfo.setrOffset((int)mCurrentValue);
                mFactory.setColorTemp(mSourceId, wbInfo);
                break;
            case TypeWhiteBlanceGOffset:
                wbInfo = mFactory.getColorTemp(mSourceId);
                wbInfo.setgOffset((int)mCurrentValue);
                mFactory.setColorTemp(mSourceId, wbInfo);
                break;
            case TypeWhiteBlanceBOffset:
                wbInfo = mFactory.getColorTemp(mSourceId);
                wbInfo.setbOffset(mCurrentValue);
                mFactory.setColorTemp(mSourceId, wbInfo);
                break;

            case TypeOverscanHSize:
                ri = mFactory.getOverscan();
                ri.setW(mCurrentValue);
                mFactory.setOverscan(ri);
                break;
            case TypeOverscanHPosition:
                ri = mFactory.getOverscan();
                ri.setX(mCurrentValue);
                mFactory.setOverscan(ri);
                break;
            case TypeOverscanVSize:
                ri = mFactory.getOverscan();
                ri.setH(mCurrentValue);
                mFactory.setOverscan(ri);
                break;
            case TypeOverscanVPosition:
                ri = mFactory.getOverscan();
                ri.setY(mCurrentValue);
                mFactory.setOverscan(ri);
                break;

            case TypePictureBrightness:
                pic = PictureImpl.getInstance();
                pic.setBrightness(mCurrentValue);
                break;
            case TypePictureContrast:
                pic = PictureImpl.getInstance();
                pic.setContrast(mCurrentValue);
                break;
            case TypePictureSaturation:
                pic = PictureImpl.getInstance();
                pic.setSaturation(mCurrentValue);
                break;
            case TypePictureHue:
                pic = PictureImpl.getInstance();
                pic.setHue(mCurrentValue);
                break;
            case TypePictureSharpness:
                pic = PictureImpl.getInstance();
                pic.setSharpness(mCurrentValue);
                break;
            case TypePictureBacklight:
                pic = PictureImpl.getInstance();
                pic.setBacklight(mCurrentValue);
                break;

            case TypeNonLinearOSD0:
                nl = mFactory.getNLAPoint(mSourceId);
                nl.setX0(mCurrentValue);
                mFactory.setNLAPoint(mSourceId, nl);
                break;
            case TypeNonLinearOSD25:
                nl = mFactory.getNLAPoint(mSourceId);
                nl.setX25(mCurrentValue);
                mFactory.setNLAPoint(mSourceId, nl);
                break;
            case TypeNonLinearOSD50:
                nl = mFactory.getNLAPoint(mSourceId);
                nl.setX50(mCurrentValue);
                mFactory.setNLAPoint(mSourceId, nl);
                break;
            case TypeNonLinearOSD75:
                nl = mFactory.getNLAPoint(mSourceId);
                nl.setX75(mCurrentValue);
                mFactory.setNLAPoint(mSourceId, nl);
                break;
            case TypeNonLinearOSD100:
                nl = mFactory.getNLAPoint(mSourceId);
                nl.setX100(mCurrentValue);
                mFactory.setNLAPoint(mSourceId, nl);
                break;

            case TypeSSCDDREneable:
                mFactory.enableDdrSpread(mCurrentValue == 1 ? true : false);
                break;
            case TypeSSCDDRModulation:
                mFactory.setDdrSpreadRatio(mCurrentValue);
                break;
            case TypeSSCDDRPercentage:
                mFactory.setDdrSpreadFreq(mCurrentValue);
                break;
            case TypeSSCLVDSEneable:
                mFactory.enableLvdsSpread(mCurrentValue == 1 ? true : false);
                break;
            case TypeSSCLVDSModulation:
                mFactory.setLvdsSpreadRatio(mCurrentValue);
                break;
            case TypeSSCLVDSPercentage:
                mFactory.setLvdsSpreadFreq(mCurrentValue);
                break;
            case TypeSSCPanelSwing:
                //mFactory.setPanelSwing(mCurrentValue);
                break;
            case TypeAVCThreshold:
                mFactory.setAVCThreshold(mCurrentValue);
                break;
                
            case TypeLvdsSpreadRatio:
                mFactory.setLvdsSpreadRatio(mCurrentValue);
                Log.d(TAG,"----------------------set---LVDS-----SpreadRatio:" + mCurrentValue);
                break;
            case TypeLvdsSpreadFreq:
                mFactory.setLvdsSpreadFreq(mCurrentValue);
                break;
            case TypeLvdsDrvCurrent:
                mFactory.setLvdsDrvCurrent(mCurrentValue);
                break;
            case TypeLvdsComVoltage:
                mFactory.setLvdsComVoltage(mCurrentValue);
                break;
                
            case TypeVBOSpreadRatio:
                mFactory.setVBOSpreadRadio(mCurrentValue);
                break;
            case TypeVBOSpreadFreq:
                mFactory.setVBOSpreadFreq(mCurrentValue);
                break;
            case TypeVBODrvCurrent:
                mFactory.setVBODrvCur(mCurrentValue);
                break;
            
            case TypeUSBSpreadRatio:
                mFactory.setUSB3SpreadRadio(mCurrentValue);
                break;
            case TypeUSBSpreadFreq:
                mFactory.setUSB3SpreadFreq(mCurrentValue);
                break;
            case TypeUSBDrvCurrent:
                mFactory.setUSB3DrvCur(mCurrentValue);
                break;

            case TypeDdrSpreadRatio:
                mFactory.setDdrSpreadRatio(mCurrentValue);
                break;
            case TypeDdrSpreadFreq:
                mFactory.setDdrSpreadFreq(mCurrentValue);
                break;
                
            case TypeGMACSpreadRatio:
                mFactory.setGmacSpreadRadio(mCurrentValue);
                break;
            case TypeGMACSpreadFreq:
                mFactory.setGmacSpreadFreq(mCurrentValue);
                break;
            case TypeGMACDrvCurrent:
                mFactory.setGmacDrvCur(mCurrentValue);
                break;

            default:
                break;
        }
    }

    private CommandModel.CommandChangeListener mCommandChangeListener = new CommandModel.CommandChangeListener() {

        @Override
        public void onStatusChanged(int status) {
            ADCCalibrationInfo adcInfo = null;
            switch(mType) {
                case TypeADCRGain:
                    adcInfo = mFactory.getADCGainOffset(mSourceId);
                    mCurrentValue = adcInfo.getrGain();
                    break;
                case TypeADCGGain:
                    adcInfo = mFactory.getADCGainOffset(mSourceId);
                    mCurrentValue = adcInfo.getgGain();
                    break;
                case TypeADCBGain:
                    adcInfo = mFactory.getADCGainOffset(mSourceId);
                    mCurrentValue = adcInfo.getbGain();
                    break;
                case TypeADCROffset:
                    adcInfo = mFactory.getADCGainOffset(mSourceId);
                    mCurrentValue = adcInfo.getrOffset();
                    break;
                case TypeADCGOffset:
                    adcInfo = mFactory.getADCGainOffset(mSourceId);
                    mCurrentValue = adcInfo.getgOffset();
                    break;
                case TypeADCBOffset:
                    adcInfo = mFactory.getADCGainOffset(mSourceId);
                    mCurrentValue = adcInfo.getbOffset();
                    break;
                default:
                    break;
            }
            mWindow.invalidateViews();
        }
    };

    public CommandModel.CommandChangeListener getCommandChangeListener() {
        return mCommandChangeListener;
    }

    private ChoiceModel.SourceChangeListener mSourceChangeListener = new ChoiceModel.SourceChangeListener() {

        @Override
        public void onSourceChanged(int srcId) {
            // TODO Auto-generated method stub
            mSourceId = srcId;
        }
    };

    public ChoiceModel.SourceChangeListener getSourceChangeListener() {
        return mSourceChangeListener;
    }

    private String getValueStr() {
        String result = "" + mCurrentValue;
        if (mDigits > 0) {
            double value = Float.valueOf("" + mCurrentValue);
            int scale = 1;
            String format = "0.";
            for (int i = 0; i < mDigits; i++) {
                format += "0";
                scale *= 10;
            }
            DecimalFormat df = new DecimalFormat(format);
            result = df.format(value / scale);
        }


        if (mNumber == NumberSystem.Binary) {

        } else if (mNumber == NumberSystem.Decimal) {

        } else if (mNumber == NumberSystem.Hexadecimal) {
            result = Integer.toHexString(Integer.valueOf(result)).toUpperCase(Locale.getDefault());
            result = "0x" + result;
        }
        return result;
    }

    private int getOSDRange(int sourceId, boolean max){
        int range = 0;
        switch(sourceId){
            case 0:    // Volume
                range = max ? 100 : 0;
                break;
            case 1:    // Brightness
            case 2:    // Contrast
            case 3:    // Saturation
            case 4:    // Sharpness
            case 5:    // Hue
            case 6:    // Backlight
                range = max ? 255 : 0;
                break;
            default:
                break;
        }
        return range;
    }
}
