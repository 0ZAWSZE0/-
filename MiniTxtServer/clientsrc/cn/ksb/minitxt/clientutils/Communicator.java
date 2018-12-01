package cn.ksb.minitxt.clientutils;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;

import cn.ksb.minitxt.common.entity.DataTransfer;

public interface Communicator<T extends Serializable, S extends Serializable> {
	//创建连接
	public void init(String host, int port) throws UnknownHostException, IOException;
	
	//数据通讯
	//object需修改
	public DataTransfer<S> communicate(DataTransfer<T> input) throws IOException, ClassNotFoundException;
	
	//释放资源
	public void destroy() throws IOException;
}
