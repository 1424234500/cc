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
import android.os.IBinder;
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
		String type = msg.get("type", "");	//消息的类型 文本 推送
		if(type.equals("broadcast")){
			mNotificationManager.notify(0, makeBroad(msg).build());
		}
		localBroadcastManager.sendBroadcast(new Intent(MSGTYPE.broadcastUrl).putExtra("msg", jsonstr)); //发送应用内广播

	}
	public NotificationCompat.Builder makeBroad(Msg msg){
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle("有人")//设置通知栏标题
				.setContentText(msg.get("res", "")) //设置通知栏显示内容
	//	.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
	//  .setNumber(number) //设置通知集合的数量
				.setTicker("有可疑人物出现！") //通知首次出现在通知栏，带上升动画效果的
				.setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
			.setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
			    .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
				.setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
				.setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
	//.Notification.DEFAULT_ALL// Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
				.setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
		return mBuilder;
	}



	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	} 

}
