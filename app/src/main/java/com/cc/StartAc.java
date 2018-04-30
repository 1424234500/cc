package com.cc;

import java.io.File;

import net.MSGTYPE;

import service.NetService;
import util.JsonMsg;
import util.JsonMsg;
import util.MySP;
import util.Tools;
import net.MSGTYPE;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;


/**
 * @author Walker
 * @date 2017-2-18 下午1:06:34
 * Description: 程序入口，根据状态转发目标地点
 */
public class StartAc extends BaseAc {

	@Override
	public void OnCreate(Bundle savedInstanceState) {
		//setContentView(R.layout.ac_start);
		setContentView(R.layout.ac_start);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Constant.screenH = dm.heightPixels;
		Constant.screenW = dm.widthPixels;

		//初始化sharedPreference
		initSP();
		//初始化数据库表
		initDatabaseTable();
		initFileDir();
		//初始化时启动网络后台服务
		startService();
		Tools.out("StartAc.oncreate");
		//逻辑处理，若没有登陆账号则跳转到 登陆界面
	 
		
		startActivity(new Intent(StartAc.this, LoginAc.class));
		this.finish(); 
		
	}
	public void initSP(){
		Constant.systemKey = MySP.get(getContext(), "syskey", "raspberrypi");
		Constant.systemId = MySP.get(getContext(), "sysid", "walker");
		Constant.systemPwd = MySP.get(getContext(), "syspwd", "0");
	}
	public void initFileDir(){
//		public static final String root = Environment.getExternalStorageDirectory() + "/mycc/";  
//		public static final String dirVoice = root + "record/";  
//		public static final String dirPhoto =  root + "photo/";  
//		public static final String dirFile =  root + "file/";  
//		public static final String dirCamera = root +  "camera/";  
//		public static final String dirProfile = root +  "profile/";  
//		public static final String dirProfileWall = root +  "profilewall/";
		String dirs[] =  {Constant.dirVoice, Constant.dirPhoto, Constant.dirFile, 
				Constant.dirCamera, Constant.dirProfile, Constant.dirProfileWall  };
		for(String str: dirs){
			File file = new File(str);
			if(!file.exists()){
				file.mkdirs();
			}
		}
		
		
	}

	 
	
	//初始化数据库表
	public void initDatabaseTable(){
		 
		//sqlDao.execSQL("drop table login_user");
		sqlDao.execSQL("create table if not exists login_user (id varchar(30) primary key, pwd varchar(50), profilepath varchar(200) ) ");
		 
		
		
	}
	// 开启网络服务后台
	public void startService() {
		Intent i = new Intent(this,  NetService.class);
		startService(i);
	}
	
	@Override
	public void callback(String jsonstr) { 
		Tools.out("StartAc.callback."+jsonstr);
		
		//Tools.toast(this, JsonMsg.getValue0(jsonstr));
		
		switch (JsonMsg.getCmd(jsonstr)) {
		case MSGTYPE.OK:
			startActivity(new Intent(StartAc.this, LoginAc.class));
			this.finish(); 
			break;
		
		}
		
		
	}


 

	@Override
	public boolean OnBackPressed() { 
		this.exitApp();
		return false;
	}



 
}
