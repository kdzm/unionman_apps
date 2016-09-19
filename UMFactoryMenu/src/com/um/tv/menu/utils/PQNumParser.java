package com.um.tv.menu.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PQNumParser {
	public List<PQNumInfo> parse(InputStream is) throws Exception {
		List<PQNumInfo> pqinfos = new ArrayList<PQNumInfo>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	//取得DocumentBuilderFactory实例
		DocumentBuilder builder = factory.newDocumentBuilder();	//从factory获取DocumentBuilder实例
		Document doc = builder.parse(is);	//解析输入流 得到Document实例
		Element rootElement = doc.getDocumentElement();
		
		/*NodeList itemsdef = rootElement.getElementsByTagName("default");

			PQNumInfo PQNumInfodef = new PQNumInfo();
			Node itemdef = itemsdef.item(0);
	
			PQNumInfodef.setId("default");
			NodeList propertiesdef = itemdef.getChildNodes();
				
			for (int j = 0; j < propertiesdef.getLength(); j++) {
				Node propertydef = propertiesdef.item(j);
				String nodeNamedef = propertydef.getNodeName();		
			
				if (nodeNamedef.equals("pqname")) {
					Log.i("pq", "pqname1value:"+propertydef.getAttributes().item(0).getNodeValue()); 
					Log.i("pq", "pqname2value:"+propertydef.getAttributes().item(1).getNodeValue()); 
				}
			
				if (nodeNamedef.equals("project_id")) {
					
					PQNumInfodef.setProject_id(Integer.parseInt(propertydef.getAttributes().item(0).getNodeValue()));
				
				} else if (nodeNamedef.equals("panelindex")) {
					PQNumInfodef.setPanelindex(propertydef.getAttributes().item(0).getNodeValue());

				} else if (nodeNamedef.equals("pqname")) {
			
					PQNumInfodef.setPqvalue(propertydef.getAttributes().item(0).getNodeValue());
					PQNumInfodef.setPqsrc(propertydef.getAttributes().item(1).getNodeValue());
					PQNumInfodef.setPqdes(propertydef.getAttributes().item(2).getNodeValue());
		
				}else if (nodeNamedef.equals("ininame")) {
					PQNumInfodef.setInivalue(propertydef.getAttributes().item(0).getNodeValue());
					PQNumInfodef.setInisrc(propertydef.getAttributes().item(1).getNodeValue());
					PQNumInfodef.setInivalue(propertydef.getAttributes().item(2).getNodeValue());
		
				}
			}
			pqinfos.add(PQNumInfodef);*/
		
		NodeList items = rootElement.getElementsByTagName("feature");
		for (int i = 0; i < items.getLength(); i++) {
			PQNumInfo PQNumInfo = new PQNumInfo();
			Node item = items.item(i);
			Element eleitem = (Element) item;
			String itemid = eleitem.getAttribute("id");
			String itemdescribe = eleitem.getAttribute("describe");
			
			PQNumInfo.setId(itemid);
			PQNumInfo.setDescribe(itemdescribe);
			
			NodeList properties = item.getChildNodes();
			for (int j = 0; j < properties.getLength(); j++) {
				Node property = properties.item(j);
				String nodeName = property.getNodeName();
				if (nodeName.equals("project_id")) {
					PQNumInfo.setProject_id(property.getAttributes().item(0).getNodeValue());
				} else if (nodeName.equals("panelindex")) {
					PQNumInfo.setPanelindex(property.getAttributes().item(0).getNodeValue());
				} else if (nodeName.equals("pqname")) {
					PQNumInfo.setPqvalue(property.getAttributes().item(0).getNodeValue());
					PQNumInfo.setPqsrc(property.getAttributes().item(1).getNodeValue());
					PQNumInfo.setPqdes(property.getAttributes().item(2).getNodeValue());
					
				}else if (nodeName.equals("ininame")) {
					PQNumInfo.setInivalue(property.getAttributes().item(0).getNodeValue());
					PQNumInfo.setInisrc(property.getAttributes().item(1).getNodeValue());
					PQNumInfo.setInides(property.getAttributes().item(2).getNodeValue());
				}
			}
			pqinfos.add(PQNumInfo);
		}
		return pqinfos;
	}

	//输出为xml
	/*public String serialize(List<PQNumInfo> pqinfos) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();	//由builder创建新文档
		
		Element rootElement = doc.createElement("pqinfos");

		for (PQNumInfo PQNumInfo : pqinfos) {
			Element bookElement = doc.createElement("PQNumInfo");
			bookElement.setAttribute("id", PQNumInfo.getId() + "");
			
			Element nameElement = doc.createElement("name");
			nameElement.setTextContent(PQNumInfo.getName());
			bookElement.appendChild(nameElement);
			
			Element priceElement = doc.createElement("price");
			priceElement.setTextContent(PQNumInfo.getPrice() + "");
			bookElement.appendChild(priceElement);
			
			rootElement.appendChild(bookElement);
		}
		
		doc.appendChild(rootElement);
        
        TransformerFactory transFactory = TransformerFactory.newInstance();//取得TransformerFactory实例
        Transformer transformer = transFactory.newTransformer();	//从transFactory获取Transformer实例
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");			// 设置输出采用的编码方式
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");				// 是否自动添加额外的空白
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");	// 是否忽略XML声明
        
        StringWriter writer = new StringWriter();
        
        Source source = new DOMSource(doc);	//表明文档来源是doc
        Result result = new StreamResult(writer);//表明目标结果为writer
        transformer.transform(source, result);	//开始转换
        
		return writer.toString();
	}
*/

}
