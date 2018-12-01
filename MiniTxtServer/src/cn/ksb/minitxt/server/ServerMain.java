package cn.ksb.minitxt.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import cn.ksb.minitxt.common.entity.DataTransfer;

public class ServerMain {
	/**
	 * 服务器入口
	 * @author Administrator
	 *
	 */
	public static void main(String[] args) {
		new ServerMain().startServer();
	}
	
	public <E extends Serializable> void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(Integer.parseInt(Init.getProperty("socket.server.port")));
			System.out.println("服务器启动成功!");
			
			while(true) {
				//接受请求
				Socket socket = serverSocket.accept();
				System.out.println("客户端:"+ socket.getInetAddress() + "获取了连接");
				
				//获得相关流
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				
				//提取请求相关信息
				@SuppressWarnings("unchecked")
				DataTransfer<E> dataTransfer = (DataTransfer<E>) in.readObject();
				System.out.println("请求:"+dataTransfer.getKey());
				
//				Service<User> service = ServiceFactory.getService("login");
				//创建相关的功能实例
				Service<E> service = ServiceFactory.getService(dataTransfer.getKey());
				//对实例初始化
				service.init(socket, in, out, dataTransfer.getData());
				//启动功能线程
				new Thread(service).start();
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
