package net.impl.mina;


import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * <b>function:</b> 客户端消息处理类
 */
public class ClientMessageHandler extends IoHandlerAdapter   {
	ClientImpl mClient;
	
	public ClientMessageHandler(ClientImpl mClient) {
		this.mClient = mClient;
	}

	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String content = (String)message;
		out("Get<<" + content);
		mClient.onReceive(content);
	}

	public void messageSent(IoSession session, Object message) throws Exception {
		out("Send>>" + (String)message);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		out("@@服务器发生异常： " + cause.getMessage());
		mClient.exceptionCaught(cause.getMessage());
	}
	
	public static void  out(String str) {
		// TODO Auto-generated method stub
	//	Log.e("log", "CH."+Tools.tooLongCut(str));
	}
}