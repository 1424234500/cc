package com.cc;

import net.MSGSender;
import interfac.CallInt;
import util.tools.AndroidTools;
import util.Tools;
import util.tools.picasso.NetImage;
import util.view.BottomControlPanel;
import util.view.SlidingMenu;
import util.view.TopPanelImageTitleMenu;
import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class MainAc extends ActivityGroup implements View.OnClickListener{

	private BottomControlPanel bottomControlPanel;
	private TopPanelImageTitleMenu topControlPanel;
	
	public RelativeLayout  container ;// 装载sub Activity的容器
 
	SlidingMenu mSlidingMenu;//滑动侧边菜单

	static View viewMsg, viewContact, viewDollar;//模块主页
	
	
	
	
	//用户菜单信息
	TextView tvUserName, tvSign;
	ImageView ivProfile, ivSex;
	View llchange,lllogin, llprofile;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mSlidingMenu = new SlidingMenu(this, LayoutInflater.from(this) .inflate(R.layout.ac_main, null), LayoutInflater.from( this).inflate(R.layout.ac_slidingmenu, null));
		setContentView(mSlidingMenu); 
		
		lllogin = (View) this.findViewById(R.id.lllogin);
		llchange = (View) this.findViewById(R.id.llchange);
		llprofile = (View) this.findViewById(R.id.llprofile);
		lllogin.setOnClickListener(this);
		llchange.setOnClickListener(this);
		llprofile.setOnClickListener(this);
		
		tvUserName = (TextView) this.findViewById(R.id.tvusername);
		tvSign = (TextView) this.findViewById(R.id.tvsign);
		ivProfile = (ImageView) this.findViewById(R.id.ivprofile);
		ivSex = (ImageView) this.findViewById(R.id.ivsex);
		
		initMsgMenu();
		initDollMenu();
		
		bottomControlPanel = (BottomControlPanel) this.findViewById(R.id.bottomcontrolpanel);
		bottomControlPanel.setCallback( new CallInt() {
			@Override
			public void call(int id) {
				SwitchActivity(id);
			}
		});
		topControlPanel = (TopPanelImageTitleMenu) this.findViewById(R.id.topcontrolpanel);
		topControlPanel.setCallback( new CallInt() {
			@Override
			public void call(int id) {
				if(id == R.id.ipprofile && !mSlidingMenu.isOpen()){
					mSlidingMenu.open();
				}else if(id == R.id.ipprofile && mSlidingMenu.isOpen()){
					mSlidingMenu.close();
				}
			}
		});
		
		container = (RelativeLayout) findViewById(R.id.container);
		//都打开一次，初始化，注册广播接收器，ac进入暂停模式，也可以接收数据并更新视图，跳转执行onpause onresume
	  	SwitchActivity(R.id.itdollar); 
	  	SwitchActivity(R.id.itcontact); 
		SwitchActivity(R.id.itmsg);//默认打开第0页
		MSGSender.getContact(this);	//获取联系人列表
		MSGSender.getSessions(this);	//获取会话列表
		MSGSender.getDollRooms(this);	//获取匿名聊天室列表
		 count = 0;
		listenExit();
	}
	
	public void listenExit(){
		thread.start();
	}
	Thread thread =	new Thread(){
		public void run(){
			while(AndroidTools.getIfLogin(getApplicationContext()).equals("true")){
				try {
					sleep(2000);
					//Tools.tip("等待退出登录");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(count == 0){
				Tools.log("线程" + count++ +"由于断线异常退出到登录，并自动发送消息失败导致重连");
				startActivity(new Intent(MainAc.this, LoginAc.class));
				finish();
			}
		}
	};
	static int count = 0;
	 
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			thread.stop();
			thread.destroy();
		} catch (Exception e) {

		}
		count = 1;
	}

	private void initMsgMenu() {
		//弹出对话框：创建群聊，加好友/群，扫一扫，我的二维码
	   popupMenu = new PopupMenu(MainAc.this, findViewById(R.id.tvmenu));  
        menu = popupMenu.getMenu();  
  
        // 通过代码添加菜单项  
        menu.add(Menu.NONE, Menu.FIRST + 0, 0, "创建群聊");  
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "加好友/群");  
//        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "扫一扫");  
//        menu.add(Menu.NONE, Menu.FIRST + 3, 3, "我的二维码");  
  
     // 通过XML文件添加菜单项  
      //  MenuInflater menuInflater = getMenuInflater();  
       // menuInflater.inflate(R.menu.popupmenu, menu);  
  
        // 监听事件  
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {  
            @Override  
            public boolean onMenuItemClick(MenuItem item) {  
                switch (item.getItemId()) {  
//	                case R.id.news:  
//	                    Toast.makeText(MainAc.this, "新建",   Toast.LENGTH_LONG).show();  
//	                    break;  
                case Menu.FIRST + 0:  
                	Intent i = new Intent(MainAc.this, GroupCreateAc.class);
					i.putExtra("returntitle", "消息");
					i.putExtra("title", "创建群聊");
					i.putExtra("menu", "完成");
					i.putExtra("type", "group");

					i.putExtra("name", "");
					i.putExtra("num", "32");
					i.putExtra("check", "false");

					//菜单栏，name:群名字，num:群人数，check:是否验证
					startActivity(i);
                	break;  
                case Menu.FIRST + 1:  	//加好友
                	Intent ii = new Intent(MainAc.this, AddAc.class);
					ii.putExtra("returntitle", "联系人");
					ii.putExtra("title", "搜索");
					ii.putExtra("menu", "");
					startActivity(ii);
                	break;  
                case Menu.FIRST + 2:  	//扫二维码
              	  
                break;
                case Menu.FIRST + 3:  	//生成二维码
                  break;
                  
                default:  
                    break;  
                }  
                return false;  
            }  
        });  
	
	}
	PopupMenu popupMenu;  
    Menu menu;  

	private void initDollMenu() {
		//弹出对话框：创建群聊，加好友/群，扫一扫，我的二维码
		popupMenuDoll = new PopupMenu(MainAc.this, findViewById(R.id.tvmenu));  
        menuDoll = popupMenuDoll.getMenu();  
  
        // 通过代码添加菜单项  
        menuDoll.add(Menu.NONE, Menu.FIRST + 0, 0, "创建匿名房间");  
//        menuDoll.add(Menu.NONE, Menu.FIRST + 1, 1, "加好友/群");  
//        menuDoll.add(Menu.NONE, Menu.FIRST + 2, 2, "扫一扫");  
//        menuDoll.add(Menu.NONE, Menu.FIRST + 3, 3, "我的二维码");  
        // 监听事件  
        popupMenuDoll.setOnMenuItemClickListener(new OnMenuItemClickListener() {  
            @Override  
            public boolean onMenuItemClick(MenuItem item) {  
                switch (item.getItemId()) {  
                case Menu.FIRST + 0:  
                	Intent i = new Intent(MainAc.this, GroupCreateAc.class);
					i.putExtra("returntitle", "Dollars");
					i.putExtra("title", "创建匿名聊天房间");
					i.putExtra("menu", "完成");
					i.putExtra("type", "doll");

					i.putExtra("name", "");
					i.putExtra("num", "32");
					i.putExtra("check", "false");
					//菜单栏，name:群名字，num:群人数，check:是否验证
					startActivity(i);
                	break;  
//                case Menu.FIRST + 1:  
//                	Intent ii = new Intent(MainAc.this, AddAc.class);
//					ii.putExtra("returntitle", "联系人");
//					ii.putExtra("title", "搜索");
//					ii.putExtra("menu", "");
//					startActivity(ii);
//                	break;  
                default:  
                    break;  
                }  
                return false;  
            }  
        });  
	}
	PopupMenu popupMenuDoll;  
    Menu menuDoll;  
	 //根据ID打开指定的Activity
	void SwitchActivity(int id) {
		switch(id){	//改变顶部导航栏和其监听
		case R.id.itdollar:
			topControlPanel.setProfile(Constant.getDrawableByIvProfile(Constant.ivdoll));
			topControlPanel.setTitleAndMenu("Dollars", "选项", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					popupMenuDoll.show();
				}
			});
			break;
		case R.id.itcontact:
			topControlPanel.setProfile(Constant.profilepath);
			topControlPanel.setTitleAndMenu("联系人", "添加", new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Intent i = new Intent(MainAc.this, AddAc.class);
					i.putExtra("returntitle", "联系人");
					i.putExtra("title", "添加");
					startActivity(i);
				}
			});
			break;
		case R.id.itmsg:
			topControlPanel.setProfile(Constant.profilepath);
			topControlPanel.setTitleAndMenu("消息", " ＋ ", new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					popupMenu.show();
				}
			});
			break; 
		default :
			return;
		}
		nowpanel = id;
		container.removeAllViews();//必须先清除容器中所有的View
		//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//??
		container.addView(getAcView(id), LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);//容器添加View
	}
	int nowpanel = 0;
	
	@Override
	protected void onResume() {
		super.onResume();
		
		
		tvUserName.setText(Constant.username);
		tvSign .setText(Constant.sign);
		NetImage.loadProfile(this, Constant.profilepath, ivProfile);
		NetImage.loadImage(this, Constant.sex.equals("男")?R.drawable.boy:R.drawable.girl, ivSex);
		if(nowpanel !=   R.id.itdollar){
			topControlPanel.setProfile(Constant.profilepath);
		}else{
			topControlPanel.setProfile(Constant.getDrawableByIvProfile(Constant.ivdoll));
		}
		
		
	}


	//只构造一次主页AC view
	public  View getAcView(int id){
		//View viewMsg, viewContact, viewDollar;
		Intent intent = null;
		Window subAc = null;
		switch(id){
		case R.id.itdollar:
			//if(viewDollar == null){
				intent = new Intent(MainAc.this, MainDollarAc.class);
				//Activity 转为 View
				subAc = getLocalActivityManager().startActivity( "subActivityD", intent);
				viewDollar = subAc.getDecorView();
			//}
			return viewDollar;
		case R.id.itcontact:
			//if(viewContact == null){
				intent = new Intent(MainAc.this, MainContactAc.class);
				subAc = getLocalActivityManager().startActivity( "subActivityC", intent);
				viewContact = subAc.getDecorView();
			//}
			return viewContact;
		case R.id.itmsg:
			//if(viewMsg == null){
				intent = new Intent(MainAc.this, MainMsgAc.class);
				subAc = getLocalActivityManager().startActivity( "subActivityM", intent);
				viewMsg = subAc.getDecorView();
			//}
			//getLocalActivityManager().dispatchResume();	//activitygroup分发更新resume？

			return viewMsg;
		}
		return null;
	}
	
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
		case R.id.llprofile:
		case R.id.llchange:
			SwitchActivity(R.id.itmsg);//默认打开第0页
			mSlidingMenu.close();
			Intent i = new Intent(MainAc.this, UserDetailAc.class);
			i.putExtra("USERNAME", Constant.username);
			i.putExtra("NAME", Constant.username);
			i.putExtra("SIGN", Constant.sign);
			i.putExtra("ID", Constant.id);
			i.putExtra("EMAIL", Constant.email);
			i.putExtra("SEX", Constant.sex);
			i.putExtra("NAME", Constant.username);
			i.putExtra("PROFILEPATH", Constant.profilepath);
			i.putExtra("PROFILEPATHWALL", Constant.profilepathwall);
			i.putExtra("IFADD", "self");
			i.putExtra("menu", "编辑");
			i.putExtra("title", "个人信息");
			i.putExtra("mode", "alpha");
			startActivity(i);
			break;
		case R.id.lllogin:
			MSGSender.close(MainAc.this);//发送关闭给服务器 
			//stopService(new Intent(MainAc.this, NetService.class));//关闭service，自动关闭net
			startActivity(new Intent(MainAc.this, LoginAc.class));
			MainMsgAc.listSessions.clear();
			MainContactAc.listItems.clear();
			MainContactAc.listType.clear();
			finish();
			break;
		
		}
	
	}
	
}