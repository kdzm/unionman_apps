package com.um.upgrade.util;

import android.util.Log;
import android.util.Xml;

import com.um.upgrade.data.UpgradeInfoBean;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziliang.nong on 14-6-19.
 */
public class ParseConfigUtil {
    private static String TAG = ParseConfigUtil.class.getSimpleName();
    private static boolean LOG_EN = true;

    private static boolean upgradeFlag = false;    /* ���־������־Ϊtrueʱ�����ؽ��������򷵻ؿ� */
    private static String upgradeKey = "upgrade";  /* �������ļ��Ĺؼ��֣���⵽������ַ��˵��������� */

    private enum SegmentType {SERIAL_SEGMENT, SOFTWARE_SEGMENT, OTHERS_SEGMENT};

    public static List<UpgradeInfoBean> getUpgradeInfo(InputStream inputStream) throws Exception
    {   
        List<UpgradeInfoBean> packageList = null;
        UpgradeInfoBean mUpgradeInfoBean = null;
        XmlPullParser parser = Xml.newPullParser();   
        parser.setInput(inputStream, "UTF-8");

        SegmentType segmentType = SegmentType.OTHERS_SEGMENT;
        int event = parser.getEventType();
        while(event!=XmlPullParser.END_DOCUMENT){
            switch(event)
            {   
                case XmlPullParser.START_DOCUMENT:
                    if (LOG_EN) Log.v(TAG, "XmlPullParser.START_DOCUMENT: "+parser.getName());
                    packageList = new ArrayList<UpgradeInfoBean>();
                    break;
                case XmlPullParser.START_TAG:
                    if (LOG_EN) Log.v(TAG, "XmlPullParser.START_TAG: "+parser.getName());
                    if (parser.getName().equals(upgradeKey)) {
                        if (LOG_EN) Log.d(TAG, "detect upgrade file, set upgradeFlag = true");
                        upgradeFlag = true;
                    }
                    if("package".equals(parser.getName()))
                    {
                        if (LOG_EN) Log.v(TAG, "parsing: "+parser.getName());
                        mUpgradeInfoBean = new UpgradeInfoBean();
                        mUpgradeInfoBean.setProductModel(parser.getAttributeValue(0));
                        mUpgradeInfoBean.setVendor(parser.getAttributeValue(1));
                    }
                    if(mUpgradeInfoBean!=null){
                        if("target-machine".equals(parser.getName()))
                        {
                            if (LOG_EN) Log.v(TAG, "parsing: "+parser.getName());
                        }
                        else if("software".equals(parser.getName()))
                        {
                            if (LOG_EN) Log.v(TAG, "parsing: "+parser.getName());
                            segmentType = SegmentType.SOFTWARE_SEGMENT;
                            String min = parser.getAttributeValue(0);
                            String max = parser.getAttributeValue(1);
                            if (LOG_EN) Log.v(TAG, "software: min = "+min+"  max = "+max);
                            mUpgradeInfoBean.setSoftMinVersion(min);
                            mUpgradeInfoBean.setSoftMaxVersion(max);
                        }
                        else if("serial".equals(parser.getName()))
                        {
                            if (LOG_EN) Log.v(TAG, "parsing: "+parser.getName());
                            segmentType = SegmentType.SERIAL_SEGMENT;
                        }
                        else if("segment".equals(parser.getName()))
                        {
                            if (LOG_EN) Log.v(TAG, "parsing: " + parser.getName());
                            if (segmentType == SegmentType.SOFTWARE_SEGMENT)
                            {
                                if (LOG_EN) Log.v(TAG, "segmentType == SegmentType.SOFTWARE_SEGMENT");
                                String from = parser.getAttributeValue(0);
                                String to = parser.getAttributeValue(1);
                                if (LOG_EN) Log.v(TAG, "software: from = " + from + "  to = " + to);
                                mUpgradeInfoBean.setSoftware(from, to);
                            }
                            else if (segmentType == SegmentType.SERIAL_SEGMENT)
                            {
                                if (LOG_EN) Log.v(TAG, "segmentType == SegmentType.SERIAL_SEGMENT");
                                int length = Integer.parseInt(parser.getAttributeValue(2));
                                String from = parser.getAttributeValue(0).substring(0, length);
                                String to = parser.getAttributeValue(1).substring(0, length);
                                if (LOG_EN) Log.v(TAG, "segment: from = "+parser.getAttributeValue(0)+
                                        "  to = "+parser.getAttributeValue(1)+"  length = "+length);
                                mUpgradeInfoBean.setSerial(from, to, length);
                            }
                            else
                            {
                                if (LOG_EN) Log.e(TAG, "Unknown segment type: "+event);
                            }
                        }
                        else if("source".equals(parser.getName()))
                        {
                            if (LOG_EN) Log.v(TAG, "parsing: "+parser.getName());
                            for(int i = 0; i < parser.getAttributeCount(); i++)
                            {
                                String tagName = parser.getAttributeName(i);
                                if (LOG_EN) Log.v(TAG, "parsing: "+tagName);
                                if("version".equalsIgnoreCase(tagName))
                                {
                                    mUpgradeInfoBean.setVersion(parser.getAttributeValue(i));
                                    if (LOG_EN) Log.v(TAG, "version: "+mUpgradeInfoBean.getVersion());
                                }
                                else if("description".equalsIgnoreCase(tagName))
                                {
                                    mUpgradeInfoBean.setDescription(parser.getAttributeValue(i));
                                    if(mUpgradeInfoBean.getDescription() != null)
                                    {
                                        if (LOG_EN) Log.v(TAG, "description: " + mUpgradeInfoBean.getDescription());
                                    }
                                }
                            }
                        }
                        else if("packet".equals(parser.getName()))
                        {
                            if (LOG_EN) Log.v(TAG, "parsing: "+parser.getName());
                            String type = parser.getAttributeValue(0);
                            String url = parser.getAttributeValue(1);
                            if (LOG_EN) Log.v(TAG, "upgrade type: "+type);

                            if (type.equalsIgnoreCase(UpgradeInfoBean.Packet.ZIP_TYPE)) {
                                mUpgradeInfoBean.setPacket(type, url);
                                if (LOG_EN) Log.v(TAG, "packet: type="+type+"  url="+url);
                            } else if (type.equalsIgnoreCase(UpgradeInfoBean.Packet.APK_TYPE)) {
                                String packageName = parser.getAttributeValue(2);
                                String displayName = parser.getAttributeValue(3);
                                String detail = parser.getAttributeValue(4);
                                String packageVersion = parser.getAttributeValue(5);

                                mUpgradeInfoBean.setPacket(type, url, packageName, displayName, detail, packageVersion);
                                if (LOG_EN) Log.v(TAG, "packet: type = "+type+"  url = "+url+"  packageName = "+packageName+
                                        "  displayName = "+displayName+"  detail = "+detail+"  packageVersion = "+packageVersion);
                            }
                        }
                        else if("control".equals(parser.getName()))
                        {
                            if (LOG_EN) Log.v(TAG, "parsing: " + parser.getName());
                        }
                        else if("mode".equals(parser.getName()))
                        {
                            if (LOG_EN) Log.v(TAG, "parsing: " + parser.getName());
                            mUpgradeInfoBean.setUpdateMode(parser.getAttributeValue(0));
                        }
                        else if("hardware".equals(parser.getName()))
                        {
                            if (LOG_EN) Log.v(TAG, "parsing: " + parser.getName());
                            mUpgradeInfoBean.setHardVersion(parser.getAttributeValue(0));
                        }
                        else
                        {
                            if (LOG_EN) Log.w(TAG, "Other XmlPullParser State: " + event);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (LOG_EN) Log.v(TAG, "XmlPullParser.END_TAG: "+parser.getName());
                    if ("software".equals(parser.getName()) || "serial".equals(parser.getName()))
                    {
                        segmentType = SegmentType.OTHERS_SEGMENT;
                    }
                    else if ("package".equals(parser.getName()))
                    {
                        packageList.add(mUpgradeInfoBean);
                        mUpgradeInfoBean = null;
                    }
                    break;
                default:
                    break;
            }   
            event = parser.next();
        }
        if (upgradeFlag) {
            if (LOG_EN) Log.d(TAG, "return the parsed result, clear upgradeFlag");
            upgradeFlag = false;
            return packageList;
        } else {
            return null;
        }
    } 
}
