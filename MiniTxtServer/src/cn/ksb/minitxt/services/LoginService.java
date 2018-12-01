package cn.ksb.minitxt.services;

import java.io.IOException;

import cn.ksb.minitxt.common.entity.DataTransfer;
import cn.ksb.minitxt.common.entity.User;
import cn.ksb.minitxt.server.BaseServiceImpl;
import cn.ksb.minitxt.utils.UserUtils;

public class LoginService extends BaseServiceImpl<User> {

	public void execute() throws IOException {
		//TODO 登录相关的代码
		int result = UserUtils.doLogin(getData());
		//登陆
		//1.查询小说分类,作为返回数据发送回客户端
		//2.返回之后,客户端重新发送请求,获取小说分类
		DataTransfer<?> dto = new DataTransfer<>();
		dto.setResult(result);
		getOut().writeObject(dto);
	}

}
