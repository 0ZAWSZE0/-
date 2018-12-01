package cn.ksb.minitxt.services;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Scanner;

import cn.ksb.minitxt.client.BaseServiceImpl;
import cn.ksb.minitxt.client.Init;
import cn.ksb.minitxt.client.Service;
import cn.ksb.minitxt.client.ServiceFactory;
import cn.ksb.minitxt.clientutils.DefaultCommunitatorImpl;
import cn.ksb.minitxt.common.constants.Constants;
import cn.ksb.minitxt.common.constants.UserConstants;
import cn.ksb.minitxt.common.entity.DataTransfer;
import cn.ksb.minitxt.common.entity.User;

public class LoginService extends BaseServiceImpl<Serializable>{
	private String OUTPUT_TEXT_USERNAMEE = "请输入登录名：";
	private String OUTPUT_TEXT_PASSWORD = "请输入密码：";
	private String OUTPUT_TEXT_SUCCESS = "登录成功！";
	private String OUTPUT_TEXT_FAILED = "用户名或密码错误，请重新输入！";
	

	@Override
	public Service<? extends Serializable> execute() {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println(OUTPUT_TEXT_USERNAMEE);
			String username = scanner.nextLine().trim();
			System.out.println(OUTPUT_TEXT_PASSWORD);
			String password = scanner.nextLine().trim();
			
			if(username.length() == 0 || password.length() == 0) {
				System.out.println(OUTPUT_TEXT_INVALIDINPUT);
				continue;
			}
			
			User user = new User();
			user.setUsername(username);
			user.setPassword(password);
			
			DataTransfer<User> dto = new DataTransfer<User>();
			dto.setData(user);
			dto.setKey(Constants.COMMAND_LOGIN);
			
			DefaultCommunitatorImpl<User, ?> comm = new DefaultCommunitatorImpl<>();
			DataTransfer<?> response = null;
			try {
				comm.init(Init.getProperty("socket.server.ip"), Integer
						.parseInt(Init.getProperty("socket.server.port")));
				response = comm.communicate(dto);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				System.out.println(OUTPUT_TEXT_ERROR);
				System.exit(1);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.out.println(OUTPUT_TEXT_ERROR);
				System.exit(1);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(OUTPUT_TEXT_SERVERERROR);
				return ServiceFactory.getService(Constants.COMMAND_START);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.out.println(OUTPUT_TEXT_ERROR);
				System.exit(1);
			} finally {
				try {
					comm.destroy();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(UserConstants.SUCCESS == response.getResult()) {
				System.out.println(OUTPUT_TEXT_SUCCESS);
				
				return ServiceFactory.getService(Constants.COMMAND_GETCLASSES);
			}else if(UserConstants.USERNAME_NOT_EXSITS == response.getResult() 
					|| UserConstants.PASSWORD_INVALID == response.getResult()){
				System.out.println(OUTPUT_TEXT_FAILED);
				continue;
			}else {
				System.out.println(OUTPUT_TEXT_SERVERERROR);
				continue;
			}
		}
	}

}
