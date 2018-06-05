package com.cc;

import database.BaseDao;
import database.BaseDaoImpl;
import service.NetService;
import util.AndroidTools;
import util.Tools;
import util.view.DialogBeats;
import util.view.TopPanelReturnTitleMenu;
import interfac.CallString;
import net.MSGTYPE;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * @author Walker
 * @date 2017-2-18 下午1:46:55
 * Description: 生命周期体系，需要用到网络的activity抽象基类，实现广播接收器，把常用状态函数变为抽象
 */
public abstract class BaseAc extends Activity implements CallString {
	//通过广播获取到的从service中传来的服务器信息
	public Context getContext(){
		return this;
	}
	public abstract   void OnCreate(Bundle savedInstanceState);
	public    void OnStart(){}//111111
	public    void OnResume(){}//回来唤醒


	public    void OnPause(){} //离开暂停
	public    void OnStop(){} //1111111
	public    void OnDestroy(){}//finish销毁

	public  abstract boolean OnBackPressed();	//回退键处理，返回false则执行finish，否则不处理
	
	LocalBroadcastManager localBroadcastManager;
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//Tools.out("BaseAc.receive." + intent.getExtras().getString("msg"));
			callback(intent.getExtras().getString("msg"));
		}
	};

	//数据库处理
	public BaseDao sqlDao ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//registerReceiver(broadcastReceiver, new IntentFilter(MSGTYPE.broadcastUrl));
		//registerReceiver(mBroadcastReceiver, intentFilter);
		//注册应用内广播接收器
		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(MSGTYPE.broadcastUrl));
		sqlDao = new BaseDaoImpl(this);
		
		OnCreate(savedInstanceState);
	} 
	@Override
	protected void onStart() {
		
		super.onStart();
		OnStart();
	}
	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		OnResume();
	}
	
	@Override
	protected void onPause() {
		OnPause();
		super.onPause();
	}
	@Override
	protected void onStop() {
		OnStop();
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		OnDestroy();
		//unregisterReceiver(mBroadcastReceiver);
		//取消注册应用内广播接收器
		localBroadcastManager.unregisterReceiver(broadcastReceiver);
		//此处只是关闭当前ac，不会关闭net service socket
		super.onDestroy();
	}

	
	
	private static DialogBeats beatsDialog;
	public void openLoading( ) {
		beatsDialog = new DialogBeats(this);
		//beatsDialog.init();
		beatsDialog.show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				closeLoading();
			}
		}, 6000);
		beatsDialog.setCancelable(true);
		//beatsDialog.setCancelable(false);
	}
	public void closeLoading() {
		if (beatsDialog != null) {
			if (beatsDialog.isShowing()) {
				beatsDialog.dismiss();
				beatsDialog = null;
			}
		} 
		
	}

	public void toast(Object...objects){
		Toast.makeText(this, Tools.objects2string(objects), Toast.LENGTH_SHORT).show();
	}
	public void toast(String str){
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	
	@Override
	public void onBackPressed() {	//false则finish，true不处理
		if(!OnBackPressed()){
			super.onBackPressed();
		}
	}

	
	//退出程序
	public void exitApp(){
		
		//MSGSender.exitApp(this);//发送关闭给服务器 
		stopService(new Intent(this, NetService.class));//关闭service，自动关闭net
		
	}

	public void initTopPanel(TopPanelReturnTitleMenu topTitle) {
		Intent intent = this.getIntent();
		Bundle bundle  = intent.getExtras();
		String str ;
		str = bundle.getString("returntitle");
		if(str!=null){
			topTitle.setReturnTitle(str);
		}else{
			topTitle.setReturnTitle("返回");
		}
		str = bundle.getString("title");
		if(str!=null){
			topTitle.setTitle(str);
		}else{
			topTitle.setTitle("");
		}
		str = bundle.getString("menu");
		if(str!=null){
			topTitle.setMenu(str);
		}else{
			topTitle.setTitle("菜单");
		}
		str = bundle.getString("mode");
		if( ! Tools.notNull(str)){
			topTitle.setAlphaMode(false);
		}else{
			topTitle.setAlphaMode(true);
		}
		
		
	}

	public void out(Object...objects){
		AndroidTools.out(this.getClass().getName() + "." + Tools.objects2string(objects));
	}
	
}