package cn.ksb.minitxt.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Init {
	private static Properties props = new Properties();
	
	static {
		InputStream is = null;
		try {
			is = new FileInputStream("config\\server.properties");
			props.load(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static String getProperty(String key) {
		return props.getProperty(key);
	}

}
