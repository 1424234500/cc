package com.cc;

import interfac.CallMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.MSGTYPE;
import net.MSGSender;

import util.AndroidTools;
import util.AndroidTools;
import util.JsonMsg;
import util.JsonUtil;
import util.MapListUtil;
import adapter.AdapterLvSession;
import util.Tools;

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
		int i ;
		Map map = JsonUtil.getMap(jsonstr);
		int cmd = MapListUtil.getMap(map, "cmd", 0);
		String value = MapListUtil.getMap(map, "value0", "false");
		if(value.equals("false")){
			toast("异常:" + MapListUtil.getMap(map, "value1"));
			return;
		}
		List<Map<String, Object>>  list;

		switch (cmd) {
		case MSGTYPE.TURN_DELETE_RELEATIONSHIP_BY_GROUPNAME:	//被踢出群
			toast("群组:"+ JsonMsg.getValue0(jsonstr) + " 抛弃了您");
			break;
		case MSGTYPE.TURN_DELETE_RELEATIONSHIP_BY_FRIENDNAME:	//被删好友
			toast("用户:"+ JsonMsg.getValue0(jsonstr)+" 与您解除好友关系");
			break;
		case MSGTYPE.SEND_CHATMSG_BY_GTYPE_TOID_TYPE_TIME_MSG://在线时收到一些条消息，对会话列表的影响
			AndroidTools.systemVoiceToast(this);
			
			list = JsonMsg.getList(jsonstr);
			//若此处没有该会话，则添加，若有则更新msg
			//消息记录fromid,toid,type,time,msg
			//会话列表
			//TIME=22:16, MSGTYPE=1516, NAME=小孩, PROFILEPATH=http: , ID=100, MSGTYPE=text, USERNAME=小海, STATUS=[在线], NUM=0, TYPE=user, NICKNAME=小孩
//			//消息发送传输
			//username,u.profilepath,um.fromid,um.toid,um.type,time,MSGTYPE
			//把收到的一些条消息转化为会话列表
			if(list == null || list.size() <= 0)return;
			map = list.get(list.size() - 1);
			if(MapListUtil.getMap(map, "SESSIONTYPE").equals("user")){
				//判断是否有会话， fromid to me，
				i = MapListUtil.getCountListByName(listSessions, "ID", MapListUtil.getMap(map, "FROMID"));
				if(i >= 0){//有了，则更新
					listSessions.get(i).put("TYPE", MapListUtil.getMap(map, "TYPE"));
					listSessions.get(i).put("MSG", MapListUtil.getMap(map, "MSG"));
					listSessions.get(i).put("TIME", MapListUtil.getMap(map, "TIME"));
					listSessions.get(i).put("NUM",(Tools.parseInt(MapListUtil.getList(listSessions, i, "NUM"))+1) +"") ;

				}else{	//否则添加一条会话
					map.put("MSG", MapListUtil.getMap(map, "TYPE"));	//消息类型变为msgtype
					map.put("TYPE", MapListUtil.getMap(map, "SESSIONTYPE"));	//群/用户类型变为TYPE
					map.put("NAME", MapListUtil.getMap(map, "USERNAME"));
					map.put("ID", MapListUtil.getMap(map, "FROMID"));
					map.put("NUM", (Tools.parseInt(MapListUtil.getList(listSessions, i, "NUM"))+1) +"") ;

					listSessions.add(0, map);
				}
			}else{
				//判断是否有会话 fromid  groupid to me
				i = MapListUtil.getCountListByName(listSessions, "ID", MapListUtil.getMap(map, "TOID"));
				if(i >= 0){//有了，则更新
					listSessions.get(i).put("TYPE", MapListUtil.getMap(map, "TYPE"));
					listSessions.get(i).put("MSG", MapListUtil.getMap(map, "MSG"));
					listSessions.get(i).put("TIME", MapListUtil.getMap(map, "TIME"));
					listSessions.get(i).put("NUM",(Tools.parseInt(MapListUtil.getList(listSessions, i, "NUM"))+1) +"") ;

				}else{	//否则添加一条会话
					map.put("MSG", MapListUtil.getMap(map, "TYPE"));	//消息类型变为msgtype
					map.put("TYPE", MapListUtil.getMap(map, "SESSIONTYPE"));	//群/用户类型变为TYPE
					map.put("NAME", MapListUtil.getMap(map, "USERNAME"));
					map.put("ID", MapListUtil.getMap(map, "GROUPID"));
					map.put("NUM", (Tools.parseInt(MapListUtil.getList(listSessions, i, "NUM"))+1) +"") ;

					listSessions.add(0, map);
				}
			}
		
			
			adapterLvSession.notifyDataSetChanged();

		break;
		case MSGTYPE.GET_CHAT_SESSIONS:  //会话列表
			AndroidTools.log("MainMsgAc:会话列表");
            swipeRefreshLayout.setRefreshing(false);
            if(value.equals("false")){
            	toast(MapListUtil.getMap(map, "value1"));
			}else{
				list = MapListUtil.getMap(map, "value1", new ArrayList());

				listSessions.clear();
				listSessions.addAll(list);
				if(adapterLvSession != null){
					adapterLvSession.notifyDataSetChanged();
				}
				for(i = 0;  i < listSessions.size(); i++){
					if(Tools.parseInt( MapListUtil.getList(listSessions, i, "NUM")) > 0){//有未读消息
						AndroidTools.systemVoiceToast(this);
						break;
					}
				}
			}

			break;  
		case MSGTYPE.DELETE_RELEATIONSHIP_BY_TYPE_ID:
			AndroidTools.log("MainMsgAc:删除关系");

			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				i = MapListUtil.getCountListByName(listSessions, "ID", JsonMsg.getValue1(jsonstr));
				if(i >= 0){
					listSessions.remove(i);
					adapterLvSession.notifyDataSetChanged();
				}
			} 
			break;
		case MSGTYPE.UPDATE_GROUP_BY_ID_NAME_SIGN_NUM_CHECK:
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				//1:id,2:name,sign,num,check
				i = MapListUtil.getCountListByName(listSessions, "ID", JsonMsg.getValueI(jsonstr, 1));
				if(i >= 0){//有了，则更新
					listSessions.get(i).put("NUM", JsonMsg.getValueI(jsonstr, 4)) ;
					listSessions.get(i).put("SIGN", JsonMsg.getValueI(jsonstr, 3)) ;
					listSessions.get(i).put("USERNAME", JsonMsg.getValueI(jsonstr, 2)) ;
					listSessions.get(i).put("NAME", JsonMsg.getValueI(jsonstr, 2)) ;
					listSessions.get(i).put("NICKNAME", JsonMsg.getValueI(jsonstr, 2)) ;
					adapterLvSession.notifyDataSetChanged();
				}
				
			}
			break;
		case MSGTYPE.UPDATE_NICKNAME_BY_ID_NICKNAME_GROUPID:
			AndroidTools.log("MainMsgAc:更新备注 " + JsonMsg.getValue1(jsonstr) + " " + JsonMsg.getValue2(jsonstr));
			
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				String newId = JsonMsg.getValue1(jsonstr);
				String newNickname = JsonMsg.getValue2(jsonstr);
				i = MapListUtil.getCountListByName(listSessions, "ID", newId);
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
		 
		case MSGTYPE.CLOSE:
			AndroidTools.toast(this, JsonMsg.getValue0(jsonstr));
			AndroidTools.putIfLogin(getApplicationContext(), "false");
			AndroidTools.log("MainMsgAc收到close,iflogin sp=" +  AndroidTools.getIfLogin(getApplicationContext()));
			 AndroidTools.toast(this, "需要重新登录"  );
			break;
		case MSGTYPE.TOAST:
		case MSGTYPE.OK:
		case MSGTYPE.ERROR:
//			AndroidTools.toast(this, JsonMsg.getValue0(jsonstr));
			break;
		}
	

	}
 
  

	@Override
	public void OnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ac_main_msg);
		AndroidTools.log("msg oncrate");
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
			map.put("MSG","text");	//消息类型变为msgtype
			map.put("TYPE", "user");	//群/用户类型变为TYPE
			map.put("USERNAME", "CC");
			map.put("NAME", "CC");
			map.put("STATUS", "[在线]");
			map.put("PROFILEPATH", "http://img03.tooopen.com/images/20131111/sy_46708898917.jpg");
			map.put("ID", "12345");
			map.put("TIME", Tools.getNowTime());
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
		AndroidTools.log("msg OnStart");
	} 
	@Override
	public void OnResume() {
		this.adapterLvSession.notifyDataSetChanged(); 
		AndroidTools.log("msg OnResume");
	} 
	@Override
	public void OnPause() {
		AndroidTools.log("msg OnPause");
	} 
	@Override
	public void OnStop() {
		AndroidTools.log("msg OnStop");
	} 
	@Override
	public void OnDestroy() {
		AndroidTools.log("msg OnDestroy");
	}



	@Override
	public void call(Map<String, Object> map) {
		//点中会话列表中某个用户/群组，进入聊天对话界面
		//系统消息，提示，好友添加请求
		//点击发消息，跳转到聊天界面等待 获取与目标用户的聊天记录10条/分页查询 ,并删除气泡提示，更新为已读
		
		String type = MapListUtil.getMap(map, "type");
		Intent intent = null;

		if(type.equals("user")){
			listSessions.get( MapListUtil.getCountListByName(listSessions, "ID", MapListUtil.getMap(map,"ID").toString())).put("NUM", 0);
			adapterLvSession.notifyDataSetChanged();
			
			intent = new Intent(this, ChatAc.class);
		}else if(type.equals("group")){
			listSessions.get( MapListUtil.getCountListByName(listSessions, "ID", MapListUtil.getMap(map,"ID").toString())).put("NUM", 0);
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
			
		} else{
			AndroidTools.toast(getContext(), "该会话类型异常？" + type);
			return;
		}
		
		AndroidTools.putMapToIntent(intent, map);
		startActivity(intent);
	}

	private void removeSession(int position) {
		Map<String, Object> map = listSessions.get(position);
		MSGSender.removeChatSessionById(this, MapListUtil.getMap(map, "ID"));
		listSessions.remove(position);
    	adapterLvSession.notifyDataSetChanged();
    	
	}  
}
