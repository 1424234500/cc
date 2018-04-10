package net;
 

/**
 * @author Walker
 * @date 2017-2-18 下午2:38:38
 * Description: 手机端网络工具
 */
public interface Client {
	//构造函数中连接服务器初始化socket writer reader
	public void out(String str); 
	public boolean start();	//服务器连接成功后启动读取线程
	public boolean stop();	//关闭连接
	public void send(String jsonstr);		//发送消息
	public void onReceive(String str) ;		//收到消息后，不实现，交由包含此接口的类实现，以实现回功能
	public void reconnect(String string);
	
	

}