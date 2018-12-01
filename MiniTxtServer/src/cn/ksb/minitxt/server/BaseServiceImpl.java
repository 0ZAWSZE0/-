package cn.ksb.minitxt.server;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * 抽象实现类
 * @author Administrator
 *
 * @param <T>
 */
public abstract class BaseServiceImpl<T extends Serializable> implements Service<T> {
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private T data;
	
	//初始化
	public void init(Socket socket, ObjectInputStream in, ObjectOutputStream out, T data) {
		this.socket = socket;
		this.in = in;
		this.out = out;
		this.data = data;
		
	}
	
	abstract protected void execute() throws IOException;
	
	public void run() {
		try {
			execute();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				destroy();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//释放资源
	public void destroy() throws IOException {
		in.close();
		out.close();
		socket.close();
		
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ObjectInputStream getIn() {
		return in;
	}

	public void setIn(ObjectInputStream in) {
		this.in = in;
	}

	public ObjectOutputStream getOut() {
		return out;
	}

	public void setOut(ObjectOutputStream out) {
		this.out = out;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
	
	
	
}
