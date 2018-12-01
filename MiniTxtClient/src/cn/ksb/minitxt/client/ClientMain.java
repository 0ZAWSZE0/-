package cn.ksb.minitxt.client;

import java.io.Serializable;

import cn.ksb.minitxt.common.constants.Constants;

public class ClientMain {
	/**
	 * 客户端入口
	 */
	public static void main(String[] args) {
		new ClientMain().startClient();
	}
	
	public void startClient() {
		Service<? extends Serializable> service = ServiceFactory.getService(Constants.COMMAND_START);
		while (true) {
			if(service == null) {
				System.out.println("退出应用.");
				break;
			}
			service = service.execute();
		}
		
	}

}
