package cn.ksb.minitxt.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.ksb.minitxt.common.constants.NovelConstants;
import cn.ksb.minitxt.common.entity.Classification;
import cn.ksb.minitxt.common.entity.Novel;
import cn.ksb.minitxt.server.Init;

public class NovelUtils {
	private static final String path = Init.getProperty("server.config.class");
	private static Map<String, Classification> classes = new HashMap<String, Classification>();
	private static Map<String, Map<String, Novel>> novels = new HashMap<>();
	private static Document doc = null;
	private static String model;
	//加载小说分类信息
	//加载某个分类下的小说信息
	static {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
			try {
				db = dbf.newDocumentBuilder();
				doc = db.parse(new FileInputStream(path));
				
				NodeList classnames = doc.getElementsByTagName("classname");
				NodeList catalogs = doc.getElementsByTagName("catalog");
				NodeList configs = doc.getElementsByTagName("config");
				Classification cls = null;
				for (int i = 0; i < classnames.getLength(); i++) {
					cls = new Classification();
					cls.setClassname(classnames.item(i).getFirstChild().getNodeValue().trim());
					cls.setCatalog(catalogs.item(i).getFirstChild().getNodeValue().trim());
					cls.setConfig(configs.item(i).getFirstChild().getNodeValue().trim());
					classes.put(cls.getClassname(), cls);
				}
				
				for(Classification clz : classes.values()) {
					doc = db.parse(new FileInputStream(clz.getConfig()));
					NodeList names = doc.getElementsByTagName("name");
					NodeList authors = doc.getElementsByTagName("author");
					NodeList descriptions = doc.getElementsByTagName("description");
					NodeList filenames = doc.getElementsByTagName("filename");
					Map<String, Novel> nls = new HashMap<String, Novel>();
					Novel novel = null;
					for(int i = 0; i < names.getLength(); i++) {
						novel = new Novel();
						novel.setName(names.item(i).getFirstChild().getNodeValue().trim());
						novel.setAuthor(authors.item(i).getFirstChild().getNodeValue().trim());
						novel.setDesc(descriptions.item(i).getFirstChild().getNodeValue().trim());
						novel.setFilename(filenames.item(i).getFirstChild().getNodeValue().trim());
						novel.setCls(clz);
						nls.put(novel.getName(), novel);
					}
					novels.put(clz.getClassname(), nls);
				}
				File modelFile = new File(Init.getProperty("server.novel.model"));
				BufferedReader modelReader = null;
				StringBuilder modelBuilder = new StringBuilder();
				try {
					modelReader = new BufferedReader(new FileReader(modelFile));
					String line = null;
					while ((line = modelReader.readLine())!=null) {
						modelBuilder.append(line).append("\n");
					}
				} finally {
					if(modelReader != null) {
						modelReader.close();
					}
				}
				model = modelBuilder.toString();
			} catch (SAXException | IOException | ParserConfigurationException e) {
				e.printStackTrace();
				throw new RuntimeException("小说配置信息加载失败!",e);
			}
		
	}
	
	public static Classification[] getClasses() {
		return classes.values().toArray(new Classification[0]);
	}
	
	public static Novel[] getNovels(Classification cls) {
		return novels.get(cls.getClassname()).values().toArray(new Novel[0]);
	}
	
	public static String getPreview(Novel novel) throws FileNotFoundException,IOException {
		String preview = novels.get(novel.getCls()
				.getClassname()).get(novel.getName()).getPreview();
		if(preview != null) {
			return preview;
		}
		File file = getNovelFile(novel);
		Reader reader = new FileReader(file);
		char[] content = new char[100];
		reader.read(content);
		preview = new String(content);
		novels.get(novel.getCls().getClassname())
		.get(novel.getName()).setPreview(preview);
		return preview;
	}
	
	public static File getNovelFile(Novel novel) {
		String path = classes.get(novel.getCls()
				.getClassname()).getCatalog();
		if(!path.endsWith(File.separator)) {
			path += File.separator;
		}
		String filename = novels.get(novel.getCls().getClassname())
				.get(novel.getName()).getFilename();
		
		File file = new File(path + filename);
		return file;
	}
	
	public static boolean exists(Novel novel) {
		return novels.get(novel.getCls().getClassname())
				.containsKey(novel.getName());
	}
	
	public static synchronized int saveNovel(Novel novel) {
		if(exists(novel)) {
			return NovelConstants.FILE_EXISTS;
		}
		novel.setFilename(novel.getName()+".txt");
		File configFile = new File(classes.get(novel.getCls()
				.getClassname()).getConfig());
		String newNovel = String.format(model, novel.getName(),
				novel.getAuthor(),novel.getDesc(),novel.getFilename());
		BufferedReader reader = null;
		StringBuilder builder = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(configFile));
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		int index = builder.lastIndexOf("</");
		String content = builder.insert(index, newNovel).toString();
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(configFile);
			writer.write(content);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		novels.get(novel.getCls().getClassname()).put(novel.getName(), novel);
		return NovelConstants.SAVE_SUCCESS;
		
	}
	
	public static File makeNovelFile(Novel novel) {
		String path = classes.get(novel.getCls()
				.getClassname()).getCatalog();
		if(!path.endsWith(File.separator)) {
			path += File.separator;
		}
		String fileName = novel.getName()+".txt";
		return new File(path + fileName);
	}
}
