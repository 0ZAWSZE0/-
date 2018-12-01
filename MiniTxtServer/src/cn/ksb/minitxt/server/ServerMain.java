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
	 * ���������
	 * @author Administrator
	 *
	 */
	public static void main(String[] args) {
		new ServerMain().startServer();
	}
	
	public <E extends Serializable> void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(Integer.parseInt(Init.getProperty("socket.server.port")));
			System.out.println("�����������ɹ�!");
			
			while(true) {
				//��������
				Socket socket = serverSocket.accept();
				System.out.println("�ͻ���:"+ socket.getInetAddress() + "��ȡ������");
				
				//��������
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				
				//��ȡ���������Ϣ
				@SuppressWarnings("unchecked")
				DataTransfer<E> dataTransfer = (DataTransfer<E>) in.readObject();
				System.out.println("����:"+dataTransfer.getKey());
				
//				Service<User> service = ServiceFactory.getService("login");
				//������صĹ���ʵ��
				Service<E> service = ServiceFactory.getService(dataTransfer.getKey());
				//��ʵ����ʼ��
				service.init(socket, in, out, dataTransfer.getData());
				//���������߳�
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
