package service;

import com.cc.Constant;

import interfac.CallString;
import net.Client;
import net.MSGTYPE;
import net.Msg;

import util.AndroidTools;
import util.Tools;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class NetService extends Service implements CallString {
 
	public Client client;//网络工具
	LocalBroadcastManager localBroadcastManager;
	@Override
	public void onCreate() {
		super.onCreate();
		out("   onCreate    ");
		
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
        jsonstr = msg.getDataJson();

		localBroadcastManager.sendBroadcast(new Intent(MSGTYPE.broadcastUrl).putExtra("msg", jsonstr)); //发送应用内广播
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	} 

}
