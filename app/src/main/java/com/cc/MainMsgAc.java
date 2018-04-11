package com.cc;

import interfac.CallMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.MSG;
import net.MSGSender;
import util.tools.AndroidTools;
import util.tools.MyJson;
import util.Tools;
import adapter.AdapterLvSession;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class MainMsgAc extends BaseAc implements CallMap  {
 
	static ListView lvSession;
	//name,msg,time,profilepath
	//type <user,group>,toid id,username,profilepath,nickname,name,   msg,time,status <在线,离线>
	static AdapterLvSession adapterLvSession;
	static  List<Map<String, Object>> listSessions = new ArrayList<Map<String,Object>>(); 		
	View rlSearch;
	SwipeRefreshLayout swipeRefreshLayout;
	
	
	@Override
	public void callback(String jsonstr) {
		int cmd = MyJson.getCmd(jsonstr);
		int i ;

		List<Map<String, Object>>  list;
		Map<String, Object>  map;
		
		switch (cmd) {
		case MSG.TURN_DELETE_RELEATIONSHIP_BY_GROUPNAME:	//被踢出群
			toast("群组:"+MyJson.getValue0(jsonstr) + " 抛弃了您");
			break;
		case MSG.TURN_DELETE_RELEATIONSHIP_BY_FRIENDNAME:	//被删好友
			toast("用户:"+MyJson.getValue0(jsonstr)+" 与您解除好友关系");
			break;
		case MSG.SEND_CHATMSG_BY_GTYPE_TOID_TYPE_TIME_MSG://在线时收到一些条消息，对会话列表的影响
			AndroidTools.systemVoiceToast(this); 
			
			list = MyJson.getList(jsonstr);
			//若此处没有该会话，则添加，若有则更新msg
			//消息记录fromid,toid,type,time,msg
			//会话列表
			//TIME=22:16, MSG=1516, NAME=小孩, PROFILEPATH=http: , ID=100, MSGTYPE=text, USERNAME=小海, STATUS=[在线], NUM=0, TYPE=user, NICKNAME=小孩
//			//消息发送传输
			//username,u.profilepath,um.fromid,um.toid,um.type,time,MSG
			//把收到的一些条消息转化为会话列表
			if(list == null || list.size() <= 0)return;
			map = list.get(list.size() - 1);
			if(Tools.getMap(map, "SESSIONTYPE").equals("user")){
				//判断是否有会话， fromid to me，
				i = Tools.getCountListByName(listSessions, "ID", Tools.getMap(map, "FROMID"));
				if(i >= 0){//有了，则更新
					listSessions.get(i).put("MSGTYPE", Tools.getMap(map, "TYPE"));
					listSessions.get(i).put("MSG", Tools.getMap(map, "MSG"));
					listSessions.get(i).put("TIME", Tools.getMap(map, "TIME"));
					listSessions.get(i).put("NUM",(Tools.parseInt(Tools.getList(listSessions, i, "NUM"))+1) +"") ;

				}else{	//否则添加一条会话
					map.put("MSGTYPE", Tools.getMap(map, "TYPE"));	//消息类型变为msgtype
					map.put("TYPE", Tools.getMap(map, "SESSIONTYPE"));	//群/用户类型变为TYPE
					map.put("NAME", Tools.getMap(map, "USERNAME"));
					map.put("ID", Tools.getMap(map, "FROMID"));
					map.put("NUM", (Tools.parseInt(Tools.getList(listSessions, i, "NUM"))+1) +"") ;

					listSessions.add(0, map);
				}
			}else{
				//判断是否有会话 fromid  groupid to me
				i = Tools.getCountListByName(listSessions, "ID", Tools.getMap(map, "TOID"));
				if(i >= 0){//有了，则更新
					listSessions.get(i).put("MSGTYPE", Tools.getMap(map, "TYPE"));
					listSessions.get(i).put("MSG", Tools.getMap(map, "MSG"));
					listSessions.get(i).put("TIME", Tools.getMap(map, "TIME"));
					listSessions.get(i).put("NUM",(Tools.parseInt(Tools.getList(listSessions, i, "NUM"))+1) +"") ;

				}else{	//否则添加一条会话
					map.put("MSGTYPE", Tools.getMap(map, "TYPE"));	//消息类型变为msgtype
					map.put("TYPE", Tools.getMap(map, "SESSIONTYPE"));	//群/用户类型变为TYPE
					map.put("NAME", Tools.getMap(map, "USERNAME"));
					map.put("ID", Tools.getMap(map, "GROUPID"));
					map.put("NUM", (Tools.parseInt(Tools.getList(listSessions, i, "NUM"))+1) +"") ;

					listSessions.add(0, map);
				}
			}
		
			
			adapterLvSession.notifyDataSetChanged();

		break;
		case MSG.GET_CHAT_SESSIONS:  //会话列表
			Tools.log("MainMsgAc:会话列表");
            swipeRefreshLayout.setRefreshing(false);

			listSessions.clear();
			listSessions.addAll(MyJson.getList(jsonstr));
 			
 			if(adapterLvSession != null){
 				adapterLvSession.notifyDataSetChanged();
 			}
 			for(i = 0;  i < listSessions.size(); i++){
 				if(Tools.parseInt( Tools.getList(listSessions, i, "NUM")) > 0){//有未读消息
 					AndroidTools.systemVoiceToast(this);
 					break;
 				}
 			}
			break;  
		case MSG.DELETE_RELEATIONSHIP_BY_TYPE_ID:
			Tools.log("MainMsgAc:删除关系");

			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				i = Tools.getCountListByName(listSessions, "ID", MyJson.getValue1(jsonstr));
				if(i >= 0){
					listSessions.remove(i);
					adapterLvSession.notifyDataSetChanged();
				}
			} 
			break;
		case MSG.UPDATE_GROUP_BY_ID_NAME_SIGN_NUM_CHECK:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				//1:id,2:name,sign,num,check
				i = Tools.getCountListByName(listSessions, "ID", MyJson.getValueI(jsonstr, 1));
				if(i >= 0){//有了，则更新
					listSessions.get(i).put("NUM",MyJson.getValueI(jsonstr, 4)) ;
					listSessions.get(i).put("SIGN",MyJson.getValueI(jsonstr, 3)) ;
					listSessions.get(i).put("USERNAME",MyJson.getValueI(jsonstr, 2)) ;
					listSessions.get(i).put("NAME",MyJson.getValueI(jsonstr, 2)) ;
					listSessions.get(i).put("NICKNAME",MyJson.getValueI(jsonstr, 2)) ;
					adapterLvSession.notifyDataSetChanged();
				}
				
			}
			break;
		case MSG.UPDATE_NICKNAME_BY_ID_NICKNAME_GROUPID:
			Tools.log("MainMsgAc:更新备注 " + MyJson.getValue1(jsonstr) + " " + MyJson.getValue2(jsonstr));
			
			if(MyJson.getValue0(jsonstr).equals("true")){
				String newId = MyJson.getValue1(jsonstr);
				String newNickname = MyJson.getValue2(jsonstr);
				i = Tools.getCountListByName(listSessions, "ID", newId);
				if(i >= 0){	//若有会话则更
					if( listSessions.get(i).get("NICKNAME").toString().equals("")){
						if(!newNickname.equals("")){	//本来没有备注，之后有了备注
							listSessions.get(i).put("NAME", newNickname);
							listSessions.get(i).put("NICKNAME", newNickname);
						} 
					}else{	//本来就有备注，修改备注
						if(!newNickname.equals("")){	//新备注不为空
							listSessions.get(i).put("NAME", newNickname);
							listSessions.get(i).put("NICKNAME", newNickname);
						} else{	//新备注为空，则删除,并设置显示名为用户名
							listSessions.get(i).put("NAME", listSessions.get(i).get("USERNAME").toString());
							listSessions.get(i).put("NICKNAME", "");
						}
					}
					adapterLvSession.notifyDataSetChanged();
				}
			} 
			break;	
		 
		case MSG.CLOSE:
			Tools.toast(this, MyJson.getValue0(jsonstr));
			AndroidTools.putIfLogin(getApplicationContext(), "false");
			Tools.log("MainMsgAc收到close,iflogin sp=" +  AndroidTools.getIfLogin(getApplicationContext()));
			 Tools.toast(this, "需要重新登录"  );
			break;
		case MSG.TOAST:
		case MSG.OK:
		case MSG.ERROR:
			Tools.toast(this, MyJson.getValue0(jsonstr));
			break;
		}
	

	}
 
  

	@Override
	public void OnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ac_main_msg);
		Tools.log("msg oncrate");
		rlSearch = (View)findViewById(R.id.rlsearch);
		rlSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(MainMsgAc.this, AddAc.class);
				i.putExtra("returntitle", "联系人");
				i.putExtra("title", "搜索");
				i.putExtra("menu", "");
				startActivity(i);
			}
		});
		lvSession = (ListView)findViewById(R.id.lvsession);
		
		
		listSessions = new ArrayList<Map<String,Object>>();
		adapterLvSession = new AdapterLvSession(this, listSessions);
		lvSession.setAdapter( adapterLvSession);
		lvSession.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				call(listSessions.get(arg2));
			}
		});
		lvSession.setOnItemLongClickListener(new OnItemLongClickListener(){  
            @Override  
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  int arg2, long arg3) {  
            	removeSession(arg2);
            	return true;  
            }
        });  
		  swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl);
         //设置刷新时动画的颜色，可以设置4个
         swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
	     swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
               // tv.setText("正在刷新");
        		MSGSender.getSessions(MainMsgAc.this);	//获取会话列表
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                       // tv.setText("刷新完成");
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                }, 2000);
            }
        });
		
		
		
		if(Constant.offlineMode == 1){
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("MSGTYPE","text");	//消息类型变为msgtype
			map.put("TYPE", "user");	//群/用户类型变为TYPE
			map.put("USERNAME", "CC");
			map.put("NAME", "CC");
			map.put("STATUS", "[在线]");
			map.put("PROFILEPATH", "http://img03.tooopen.com/images/20131111/sy_46708898917.jpg");
			map.put("ID", "12345");
			map.put("TIME", Tools.getNowTimeS());
			map.put("NUM", 1) ;
			map.put("MSG", "消息") ;

			listSessions.add(0, map);
		}
	} 

	  

	@Override
	public boolean OnBackPressed() { 
		//this.exitApp();//退出app
		return true;	
	}


  
 



	@Override
	public void OnStart() {
		Tools.log("msg OnStart");
	} 
	@Override
	public void OnResume() {
		this.adapterLvSession.notifyDataSetChanged(); 
		Tools.log("msg OnResume");
	} 
	@Override
	public void OnPause() {
		Tools.log("msg OnPause");
	} 
	@Override
	public void OnStop() {
		Tools.log("msg OnStop");
	} 
	@Override
	public void OnDestroy() {
		Tools.log("msg OnDestroy");
	}



	@Override
	public void call(Map<String, Object> map) {
		//点中会话列表中某个用户/群组，进入聊天对话界面
		//系统消息，提示，好友添加请求
		//点击发消息，跳转到聊天界面等待 获取与目标用户的聊天记录10条/分页查询 ,并删除气泡提示，更新为已读
		
		String type = map.get("TYPE").toString();
		Intent intent = null;

		if(type.equals("user")){
			listSessions.get( Tools.getCountListByName(listSessions, "ID", map.get("ID").toString())).put("NUM", 0);
			adapterLvSession.notifyDataSetChanged();
			
			intent = new Intent(this, ChatAc.class);
		}else if(type.equals("group")){
			listSessions.get( Tools.getCountListByName(listSessions, "ID", map.get("ID").toString())).put("NUM", 0);
			adapterLvSession.notifyDataSetChanged();
			
			intent = new Intent(this, ChatAc.class);
		} else if(type.equals("adduser")){
			intent = new Intent(this, AddSendAc.class);
			intent.putExtra("menu", "同意");
			intent.putExtra("title", "添加好友");
			intent.putExtra("return", "消息");
			
		}  else if(type.equals("addgroup")){
			intent = new Intent(this, AddSendAc.class);
			intent.putExtra("menu", "同意");
			intent.putExtra("title", "添加群");
			intent.putExtra("return", "消息");
			
		} 
		
		AndroidTools.putMapToIntent(intent, map);
		startActivity(intent);
	}

	private void removeSession(int position) {
		Map<String, Object> map = listSessions.get(position);
		MSGSender.removeChatSessionById(this, Tools.getMap(map, "ID"));
		listSessions.remove(position);
    	adapterLvSession.notifyDataSetChanged();
    	
	}  
}
