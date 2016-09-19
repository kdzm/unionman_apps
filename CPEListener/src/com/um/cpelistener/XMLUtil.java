package com.um.cpelistener;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class XMLUtil {
	/** Pull方式，创建 XML  */
	public static boolean  pullXMLCreate(List<String> packages,OutputStream outStream){
		try {
			
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlSerializer xmlSerializer = factory.newSerializer();
			
			xmlSerializer.setOutput(outStream, "utf-8");
			
			xmlSerializer.startDocument("utf-8", null);		// <?xml version='1.0' encoding='UTF-8' standalone='yes' ?> 
			
			xmlSerializer.startTag("", "packages");
			
			for(int i=0; i<packages.size(); i++) {
				xmlSerializer.startTag("", "package");
				xmlSerializer.text(packages.get(i));
				xmlSerializer.endTag("", "package");
			}
			
			xmlSerializer.endTag("", "packages");
			xmlSerializer.endDocument();
			outStream.flush();
		    outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/** Pull方式，解析 XML  */
	public static List<String> pullXMLResolve(InputStream mInputStream){
		List<String> packages = new ArrayList<String>();		// 保存xml的person节点
		String ele = null;		// Element flag
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			
			xpp.setInput(mInputStream, "utf-8");
			
			int eventType = xpp.getEventType();
			while(XmlPullParser.END_DOCUMENT != eventType) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
					
				case XmlPullParser.START_TAG:
					if("package".equals(xpp.getName())) {
						ele = "package";
					} 
					break;
					
				case XmlPullParser.TEXT:
					if(null != ele) {
						if("package".equals(ele)) {
							packages.add(xpp.getText());
						} 
					}
					break;
					
				case XmlPullParser.END_TAG:
					ele = null;
					break;
				}
				
				eventType = xpp.next();		// 下一个事件类型
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return packages;		
	}
}
