package cn.ksb.minitxt.server;

import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ServiceFactory {
	private static final String path = Init.getProperty("server.config.service");
	private static Map<String, String> services = new HashMap<String, String>();
	
	static {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc = null;
		
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(new FileInputStream(path));
			NodeList serviceNodes = doc.getElementsByTagName("service");
			for (int i = 0; i < serviceNodes.getLength(); i++) {
				Node node = serviceNodes.item(i);
				services.put(node.getAttributes().getNamedItem("key").getNodeValue().trim(),
						node.getFirstChild().getNodeValue().trim());
				
			}
		} catch (Exception e) {
 			e.printStackTrace();
 			throw new RuntimeException(e);
		} 
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static <T extends Serializable> Service<T> getService(String key) {
		String className = services.get(key);
		if(className == null)
			throw new RuntimeException("ÎÞÐ§¹Ø¼ü×Ö");
		
		try {
			return (Service<T>) Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	
}

