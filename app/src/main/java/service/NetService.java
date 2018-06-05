package service;

import com.cc.Constant;
import com.cc.R;

import interfac.CallString;
import net.Client;
import net.MSGTYPE;
import net.Msg;

import util.AndroidTools;
import util.Tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

public class NetService extends Service implements CallString {
 
	public Client client;//网络工具
	LocalBroadcastManager localBroadcastManager;	//本地的activity广播机制
	NotificationManager mNotificationManager;//推送栏广播
	@Override
	public void onCreate() {
		super.onCreate();
		out("   onCreate    ");
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);//推送栏广播



		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		client = new net.impl.mina.ClientImpl( Constant.serverIp){
			@Override
			public void callback(String str) {
				NetService.this.callback(str);
			}
			
		};
		client.start();
	}

	@Override
	public void onDestroy() {
		client.stop();		//关闭service时自动关闭net连接
		super.onDestroy();
		out("  onDestroy       ");
	}
	
	//之后多次startService方式来传递指令
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null){
			 Bundle bun = intent.getExtras();
			 if(bun != null)  {
				String jsonstr =bun.getString("msg");
				if(Tools.notNull( jsonstr)){
					out("  onStartCommand." + jsonstr);
					this.client.send(jsonstr);
				}else{
					 out("NetService. onStartCommand intent.getExtras().getString('msg') 为null ？？？");
				}
				if(Tools.notNull(bun.getString("socket"))){	//socket重连指令
					this.client.reconnect(bun.getString("socket"));
				}
				
			 }else{
				 out("NetService. onStartCommand intent.getExtras() 为null ？？？");
			 }
		}else{
			 out("NetService. onStartCommand intent 为null ？？？");
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	public void out(String str) {
//		AndroidTools.out(  "NetService." + str);
	}
  
	//获取client从服务器收到的消息串，包装作为广播发送给 activitys
	@Override
	public void callback(String jsonstr) {
		
//		switch(JsonUtil.getCmd(jsonstr)){
//			case MSGTYPE.CLOSE  :	//服务器传来的关闭net
//				//this.client.stop();
//				return;
//			case MSGTYPE.BEATS:		//服务器测试心跳
//				//this.client.send(JsonUtil.makeJson(MSGTYPE.BEATS, "1"));
//				return;
//		}

//		out("get."+jsonstr);

		Msg msg = new Msg(jsonstr);
        jsonstr = msg.getDataJson();	//消息体中的实际消息包 除去中转系统转发参数
		String type = msg.get("type", "");	//文本中转消息 中 的 推送提示消息
		if(type.equals("push")){
//			Looper.prepare();	//主线程中自动创建 子线程中需手动创建

			Bundle bundle = new Bundle();
			bundle.putString("title", msg.get("title","title"));
			bundle.putString("text", msg.get("text","text"));
			bundle.putString("ticker", msg.get("ticker","ticker"));
			Message message = new Message();
			message.setData(bundle);
		}

		switch(msg.getMsgType()){
            case Msg.DATA:
                localBroadcastManager.sendBroadcast(new Intent(MSGTYPE.broadcastUrl).putExtra("msg", jsonstr)); //发送应用内广播
                break;
            case Msg.BROADCAST_SYS:
            case Msg.BROADCAST:
                AndroidTools.toast(getBaseContext(), msg.get("info", ""));
                break;
        }

	}



	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	} 

}
