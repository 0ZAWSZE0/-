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

public class RegisterService extends BaseServiceImpl<Serializable> {
	private String OUTPUT_TEXT_USERNAME = "�������¼��:";
	private String OUTPUT_TEXT_PASSWORD = "����������:";
	private String OUTPUT_TEXT_PASSWORD2 = "���ٴ���������:";
	private String OUTPUT_TEXT_USEREXIST = "�û����Ѵ��ڣ�������ע�ᣡ";
	private String OUTPUT_TEXT_PASSWORDNOTEQUAL = "�������벻һ����";
	private String OUTPUT_USER_SAVESUCESS = "�û�ע��ɹ������¼��";
	private String OUTPUT_USER_SAVEFAIL = "�û�ע��ʧ�ܣ�������ע�ᣡ";

	@Override
	public Service<? extends Serializable> execute() {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println(OUTPUT_TEXT_USERNAME);
			String username = scanner.nextLine().trim();
			System.out.println(OUTPUT_TEXT_PASSWORD);
			String password = scanner.nextLine().trim();
			System.out.println(OUTPUT_TEXT_PASSWORD2);
			String repwd = scanner.nextLine().trim();
			
			if(username.length() == 0 || password.length() == 0) {
				System.out.println(OUTPUT_TEXT_INVALIDINPUT);
				continue;
			}
			if(!password.equals(repwd)) {
				System.out.println(OUTPUT_TEXT_PASSWORDNOTEQUAL);
				continue;
			}
			
			User user = new User();
			user.setUsername(username);
			user.setPassword(password);
			
			DataTransfer<User> dto = new DataTransfer<User>();
			dto.setData(user);
			dto.setKey(Constants.COMMAND_REGISTER);
			
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
				System.out.println(OUTPUT_USER_SAVESUCESS);
				return ServiceFactory.getService(Constants.COMMAND_LOGIN);
			}else if (UserConstants.USERNAME_EXSITS == response.getResult()) {
				System.out.println(OUTPUT_TEXT_USEREXIST);
				continue;
			}else {
				System.out.println(OUTPUT_USER_SAVEFAIL);
				continue;
			}
		}
		
	}

}
