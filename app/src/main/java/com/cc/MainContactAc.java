package com.cc;

import interfac.CallMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.MSG;
import net.MSGSender;
import util.tools.AndroidTools;
import util.tools.MyJson;
import util.Tools;
import adapter.AdapterContact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class MainContactAc extends BaseAc implements CallMap  {
 
	static ExpandableListView elvContact;
	static AdapterContact adapterContact;
//status 'true' ifadd, 'user' type,u.id, name,u.username,u.email,u.sex,u.profilepath,u.sign,u.profilepathwall,nickname 
	static  List<Map<String, Object>> listItems = new ArrayList<Map<String,Object>>(); 	
	static  List<Map<String, Object>> listType = new ArrayList<Map<String,Object>>(); 	
	static  boolean newContact = true;
	View rlSearch;
	SwipeRefreshLayout swipeRefreshLayout;

	@Override
	public void callback(String jsonstr) {
		int cmd = MyJson.getCmd(jsonstr);
		int i;
		switch (cmd) {
		case MSG.CONTACT_USER_GROUP_MAP:  //联系人列表
			//Tools.log("MainContactAc:联系人列表");
            swipeRefreshLayout.setRefreshing(false);

			listItems.clear();
			listItems.addAll(MyJson.getList(jsonstr));
			listType.clear();
			listType.addAll(MyJson.getListType(jsonstr));
 			if(adapterContact != null){
 				adapterContact.notifyDataSetChanged();	 
 			} 
			break;  
		case MSG.UPDATE_NICKNAME_BY_ID_NICKNAME_GROUPID:	//更改备注后更新用户列表
			Tools.log("MainContactAc:更新备注");
			if(MyJson.getValue0(jsonstr).equals("true")){
				String newId = MyJson.getValue1(jsonstr);
				String newNickname = MyJson.getValue2(jsonstr);
				i = Tools.getCountListByName(listItems, "ID", newId);
				if( listItems.get(i).get("NICKNAME").toString().equals("")){
					if(!newNickname.equals("")){	//本来没有备注，之后有了备注
						listItems.get(i).put("NAME", newNickname);
						listItems.get(i).put("NICKNAME", newNickname);
					} 
				}else{	//本来就有备注，修改备注
					if(!newNickname.equals("")){	//新备注不为空
						listItems.get(i).put("NAME", newNickname);
						listItems.get(i).put("NICKNAME", newNickname);
					} else{	//新备注为空，则删除,并设置显示名为用户名
						listItems.get(i).put("NAME", listItems.get(i).get("USERNAME").toString());
						listItems.get(i).put("NICKNAME", "");
					}
				}
				adapterContact.notifyDataSetChanged();
			} 
			break;
		case MSG.LINE_STATUS_ID_TYPE:	//上下线通知
			Tools.log("MainContactAc:某人上下线id:" + MyJson.getValue0(jsonstr) + "=" + MyJson.getValue1(jsonstr));
			
			i = Tools.getCountListByName(listItems, "ID", MyJson.getValue0(jsonstr));
			if(i >= 0){
				 listItems.get(i).put("STATUS", MyJson.getValue1(jsonstr).equals("true")? "[在线]" : "[离线]");
			}
			adapterContact.notifyDataSetChanged();
			break;
		case MSG.DELETE_RELEATIONSHIP_BY_TYPE_ID:
			Tools.log("MainContactAc:删除好友/群组id="+MyJson.getValue1(jsonstr));
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				if(MyJson.getValue2(jsonstr).equals("user")){
					i = Tools.getCountListByName(listItems, "ID", MyJson.getValue1(jsonstr));
					if(i >= 0 && i < listItems.size()){
						listType.get(0).put("NUM", Tools.parseInt(Tools.getList(listType, 0, "NUM").toString()) - 1);
						listType.get(1).put("START", Tools.parseInt(Tools.getList(listType, 1, "START").toString()) - 1);
						listItems.remove(i);
						//{ONNUM=0, START=0, USERNAME=我的好友, NUM=3}
						//{ONNUM=7, START=3, USERNAME=群组, NUM=7}
						int tempNum = 0;	//更新在线人数
						for(i = Tools.parseInt(listType.get(0).get("START").toString()); i < Tools.parseInt(listType.get(0).get("NUM").toString()) ; i++){
							if( ! listItems.get(i).get("STATUS").equals("[离线]")){
								tempNum++;
							}
						}
						listType.get(0).put("ONNUM", tempNum);
					}
				}else{
					i = Tools.getCountListByName(listItems, "ID", MyJson.getValue1(jsonstr));
					if(i >= 0 && i < listItems.size()){
						listItems.remove(i);
						//{ONNUM=0, START=0, USERNAME=我的好友, NUM=3}
						//{ONNUM=7, START=3, USERNAME=群组, NUM=7}
						listType.get(1).put("ONNUM", Tools.parseInt(listType.get(1).get("ONNUM").toString()) - 1);
						listType.get(1).put("NUM", Tools.parseInt(listType.get(1).get("NUM").toString()) - 1);
					}
				}
				 
				adapterContact.notifyDataSetChanged();
			}
			break;
			
		}

	}
 

	@Override
	public void OnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ac_main_contact);
		Tools.log("contact oncrate");
		rlSearch = (View)findViewById(R.id.rlsearch);
		rlSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(MainContactAc.this, AddAc.class);
				i.putExtra("returntitle", "联系人");
				i.putExtra("title", "搜索");
				i.putExtra("menu", "");
				startActivity(i);
			}
		});
		elvContact = (ExpandableListView)findViewById(R.id.elvcontact);
		
		adapterContact = new AdapterContact(this, listType, listItems);	//绑定的是引用，若更换引用指针则导致不能顺利更新
		elvContact.setAdapter( adapterContact);
		elvContact.setOnChildClickListener( new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView elv, View convertView, int parentPosition, int childPosition, long id) {
				call((Map<String, Object>) adapterContact.getChild(parentPosition, childPosition));
				return false;
			}
		});
		
		  swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl);
	         //设置刷新时动画的颜色，可以设置4个
	         swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
		     swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
	            @Override
	            public void onRefresh() {
	               // tv.setText("正在刷新");
	        		MSGSender.getContact(MainContactAc.this);	//获取会话列表
	            }
	        });
		
		
	} 

	  

	@Override
	public void OnStart() {
		Tools.log("contact OnStart");
	} 
	@Override
	public void OnResume() {
		Tools.log("contact OnResume");
	} 
	@Override
	public void OnPause() {
		Tools.log("contact OnPause");
	} 
	@Override
	public void OnStop() {
		Tools.log("contact OnStop");
	} 
	@Override
	public void OnDestroy() {
		Tools.log("contact OnDestroy");
	}
	
	@Override
	public boolean OnBackPressed() { 
		//this.exitApp();//退出app
		return true;	
	}
 
 
	@Override
	public void call(Map<String, Object> map) {
		//点中某个用户/群组，进入详情界面显示
		String type = map.get("TYPE").toString();
		//MSGSender.getUserGroupDetailByTypeId(this, type, map.get("ID").toString() );
		Intent intent  = null;
		if(map.get("ID").toString().equals(Constant.id)){
			//自己，跳转自己的详情和修改界面
			intent = new Intent( this, UserDetailAc.class);
		}else{
			if(type.equals("user")){	//用户展示
				intent = new Intent( this, UserDetailAc.class);
			}else{		//群组展示
				intent = new Intent( this, GroupDetailAc.class);
			}
		}
		AndroidTools.putMapToIntent(intent, map);
		
		intent.putExtra("returntitle", "返回");
		intent.putExtra("title", " ");
		intent.putExtra("mode", "alpha"); 
		startActivity(intent);
		
		
	}

	
	 
	
	
}
