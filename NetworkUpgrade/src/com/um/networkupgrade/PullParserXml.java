package com.um.networkupgrade;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class PullParserXml {
	private static String TAG="NetworkUpgrade--PullParserXml";
	/**
	 * 解析升级配置信息
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
    public static List<UpgradeInfo> getPackage(InputStream inputStream) throws Exception
    {   
        List<UpgradeInfo> packageList = null;   
        UpgradeInfo mPackage = null;   
        XmlPullParser parser = Xml.newPullParser();   
        parser.setInput(inputStream, "UTF-8");   
           
        int event = parser.getEventType();
        while(event!=XmlPullParser.END_DOCUMENT){   
            switch(event)
            {   
            case XmlPullParser.START_DOCUMENT:   
            	packageList = new ArrayList<UpgradeInfo>(); 
                break;   
            case XmlPullParser.START_TAG: 
                if("upgrade".equals(parser.getName()))
                {  
                	mPackage = new UpgradeInfo();   
                	mPackage.setProductModel(parser.getAttributeValue(0));  
                	Logger.i(TAG, "ProductModel="+parser.getAttributeValue(0));
                }   
                if(mPackage!=null){   
                    if("source".equals(parser.getName()))
                    { 
                    	mPackage.setVersion(parser.getAttributeValue(0));   
                    	Logger.i(TAG, "Version="+parser.getAttributeValue(0));
                    }  
                    else if("packet".equals(parser.getName()))
                    { 
                    	mPackage.setPacket(parser.getAttributeValue(0), parser.getAttributeValue(1),parser.getAttributeValue(2),parser.getAttributeValue(3));
                    	Logger.i(TAG,"packet"+" url="+parser.getAttributeValue(0)+"   type="+parser.getAttributeValue(1)+" update="+parser.getAttributeValue(2)+" md5="+parser.getAttributeValue(3));
                    } 
                    else if("way".equals(parser.getName()))
                    { 
                    	mPackage.setDownloadWay(parser.getAttributeValue(0)); 
                    	Logger.i(TAG, "DownloadWay="+parser.getAttributeValue(0));
                    } 
                    else if("mode".equals(parser.getName()))
                    { 
                    	mPackage.setUpdateMode(parser.getAttributeValue(0)); 
                    	Logger.i(TAG, "UpdateMode="+parser.getAttributeValue(0));
                    } 
                    else if("hardware".equals(parser.getName()))
                    { 
                    	mPackage.setHardVersion(parser.getAttributeValue(0));
                    	Logger.i(TAG, "HardVersion="+parser.getAttributeValue(0));
                    } 
                }   
                break;   
            case XmlPullParser.END_TAG:
                if("upgrade".equals(parser.getName()))
                { 
                	packageList.add(mPackage);
                	mPackage = null;   
                }   
                break; 
            default:
                break;
            }   
            event = parser.next();
        }  
        return packageList;   
    } 
    
    /**
     * 解析基本配置信息
     * @param inStream
     * @return
     * @throws Exception
     */
    public static List<BaseInfo> parseBasesConfigFile(InputStream inStream) throws Exception
    {
    	BaseInfo infoMap;
    	List<BaseInfo> infos=new ArrayList<BaseInfo>();
    	// 实例化一个文档构建器工厂
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // 通过文档构建器工厂获取一个文档构建器
        DocumentBuilder builder = factory.newDocumentBuilder();
        // 通过文档通过文档构建器构建一个文档实例
        Document document = builder.parse(inStream);
        //获取XML文件根节点
        Element root = document.getDocumentElement();
        //获得子节点
        NodeList childNodes = root.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++)
        {
            //遍历子节点
            Node childNode = (Node) childNodes.item(j);
            
            if (childNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element childElement = (Element) childNode;
                if ("base".equals(childElement.getNodeName()))
                {
                    
                	NodeList list=childElement.getElementsByTagName("info");
                    for(int i=0;i<list.getLength();i++){
                    	Element e=(Element)list.item(i);
                    	infoMap = new BaseInfo();
                    	infoMap.setTimeStep(Long.parseLong(e.getAttribute("timestep")));
                    	infos.add(infoMap);
                    }
                }
              
            }
        }
        return infos;
        
    }
}
