package net.impl.sock;

import interfac.CallString;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.cc.Constant;

import android.util.Log;

import util.tools.MyJson;
import util.tools.Tools;

import net.Client;
import net.MSG;
 

/**
 * @author Walker
 * @date 2017-2-18 下午2:38:38
 * Description: 手机端网络工具 socket 基础实现方式
 */
public abstract    class ClientImpl implements Client,CallString{
	
	public String serverIp = "";
	public int serverPort = -1;
	public String serverDeviceName = "";

	public String localMac = "";
	public String localIp = "";
	public int localPort = -1;
	public String localDeviceName = "";

	public String getString() {
		String res = "";
		res += "#LocalIp=" + localIp + ", LocalPort=" + localPort
				+ ", LocalDevicename=" + localDeviceName + ", mac=" + localMac + " ## " + "ServerIp="
				+ serverIp + ", ServerPort=" + serverPort
				+ ", ServerDevicename=" + serverDeviceName + " # ";
		return res;
	}

	Socket socket;
	//Handler handler;
	BufferedReader reader;
	PrintWriter writer;

	public boolean boolListenMsg = true;
	 
	
	// 接收服务器数据的读取线程
	public void startReadThread() {
		if(threadReader.isAlive()){
			threadReader.stop();
		}
		boolListenMsg = true;
		threadReader.start();

	}
	Thread threadReader =	new Thread() {
		public void run() {
			out("链接服务器监听线程开启");// ##########
			String str = null;
			while (boolListenMsg) {
				try {
					// ############################阻塞,关于读取行？读取字节？拼接问题，socket协议是否会自动拼接
					str = reader.readLine();

					if (str != null) {
						if(str.length() > 0){
							onReceive(str);
							str = null;	
						}
					}
					// sleep(0);	//是否休眠？ 传输效率问题
				} catch (Exception e) {
					e.printStackTrace();
					boolListenMsg = false;// 退出线程
					out("服务器关闭" + e.toString());
					onReceive(MyJson.makeJson( MSG.ERROR, "You have been offline... "));
					CreateConnection();	//读取异常后再次尝试链接
				}
			}
			out(":::>>读取线程结束，退出");// ##

		}
	};
	//连接服务器，初始化socket和read writer
	public void CreateConnection(  ) {
		if(thread.isAlive()){
			thread.stop();
		}
		reconnect = true;
		thread.start();

	}
	boolean reconnect = true;

	Thread thread =	new Thread() {// 连接过程阻塞,所以新开线程
		public void run() {
			out("thread run CreateConnection");
			while(reconnect){
				 serverIp = Constant.serverIp;
				 serverPort = Constant.serverPort;
				
				try {
					if(socket == null)
						socket = new Socket(); // 这是本地socket， 再未链接之前不选哟ip/port目标
					if(!socket.isConnected()){
						SocketAddress socAddress = new InetSocketAddress(serverIp, serverPort);
						socket.connect(socAddress, 5000); // 5s最大时差
						if (socket != null) {
							serverDeviceName = socket.getInetAddress() .getHostName();
							localPort = socket.getLocalPort();
							 // 初始化输入出流
							reader = new BufferedReader(new InputStreamReader( socket.getInputStream()));
							writer = new PrintWriter(socket.getOutputStream(), false);
							//连接成功后，自动开启读取线程
							ClientImpl.this.startReadThread();
							reconnect = false;
							onReceive(MyJson.makeJson( MSG.OK, "连接服务器成功"));
						} else {
							onReceive(MyJson.makeJson(  MSG.ERROR, "连接服务器失败"));
							sleep(1000);
						}
					}
				} catch (Exception e) { 
					onReceive(MyJson.makeJson( MSG.ERROR, "连接服务器失败"));
					out("连接服务器失败 " + e.toString());
				}
			
				try {
					sleep(Constant.ReconnectTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} 
	};
	
	
	
	
	@Override
	public void reconnect(String ip) {
	//	stop();
		
	//	CreateConnection();
		
		
	}
	public ClientImpl(String ip, int port){
		this.serverIp = ip;
		this.serverPort = port; 
		CreateConnection( );
	}
 	
	
	@Override
	public void out(String str) {
		Log.d("client", str);
		//Tools.out("clientImpl.socket." + str);
	}
	
	@Override
	public boolean start() {


		return false;
	}

	@Override
	public boolean stop() {
		// TODO 自动生成的方法存根
		try { 
			if(writer != null)
		 	writer.close();
			if(reader != null)
		 	reader.close();
		 	writer = null;
		 	reader = null;
			
			this.boolListenMsg = false;
			if(socket != null){
			 	socket.shutdownInput();
			 	socket.shutdownOutput();
			 	socket.close();
				socket = null;
			} 
		 	
		} catch (Exception e) {
			out("socket client impl stop error ");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public void send(String jsonstr) {
		try {
			if (jsonstr == null || writer == null)
				return;
			
			writer.println(jsonstr);// 发送、写入到输出流，输出对象是服务端
			writer.flush();
			out("Send to Server<< " + jsonstr);
		} catch (Exception e) {
			e.printStackTrace();
			out("send error: " + jsonstr + e.toString());
		}
	}
 
	@Override
	public void onReceive(String str) {
		out("get<<"+str);
		 callback(str);
		
	}

 
	
	

}