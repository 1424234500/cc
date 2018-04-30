package com.cc;

import interfac.CallInt;
import interfac.CallMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.MSGTYPE;
import net.MSGSender;

import util.AndroidTools;
import util.JsonMsg;
import util.JsonUtil;
import util.MapListUtil;
import util.Tools;
import util.view.TopPanelReturnTitleMenu;
import adapter.AdapterLvAddFind;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


/**
 * @author Walker
 * @date 2017-05-06 13:54:46
 * Description: 群成员列表，及其对自己或他人的操作
 */
public class GroupUserListAc extends BaseAc implements CallInt, View.OnClickListener, CallMap{
 
	@Override
	public void callback(String jsonstr) {

		int cmd = JsonMsg.getCmd(jsonstr);
		switch (cmd) {
		case MSGTYPE.UPDATE_NICKNAME_BY_ID_NICKNAME_GROUPID:
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				if(Tools.notNull(JsonMsg.getValue3(jsonstr))){	//没有回传groupid，表示非修改群昵称而是 好友昵称
				}else{//群昵称，修改位置
					int i = MapListUtil.getCountListByName(listAddFind, "ID", JsonMsg.getValue1(jsonstr));
					 if(i >= 0){
						 listAddFind.get(i).put("GROUPNICKNAME",  JsonMsg.getValue2(jsonstr) );
					 }
				}
			} 
			break;	
		
		case MSGTYPE.FIND_USERS_BY_GROUPID:// 从服务器返回的查询结果
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("")){
				listAddFind.clear();
				listAddFind.addAll( JsonUtil.getList(jsonstr));
				//Tools.out( (Tools.list2string( listAddFind)));
				if(adapterLvAddFind != null) {
					adapterLvAddFind.notifyDataSetChanged();
				}
			}else{
				toast(JsonMsg.getValue0(jsonstr));
			}
			break;
		case MSGTYPE.DELETE_RELEATIONSHIP_BY_GROUPID_USERID:	//踢出某用户
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				int i = MapListUtil.getCountListByName(listAddFind, "ID", JsonMsg.getValue1(jsonstr));
				if(i >= 0){
					listAddFind.remove(i);
					adapterLvAddFind.notifyDataSetChanged();
				}
				toast("已踢出用户:"+ JsonMsg.getValue1(jsonstr));
			}else{
				toast(JsonMsg.getValue0(jsonstr));
			}
			break;
			
			
		}

	}
	ListView lvResult;
	AdapterLvAddFind adapterLvAddFind;
	List<Map<String, Object>> listAddFind  ; 
	
	TopPanelReturnTitleMenu topTitle;
	
	
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ac_group_userlist);

		topTitle = (TopPanelReturnTitleMenu)findViewById(R.id.top_title);
		lvResult=  (ListView)findViewById(R.id.lvresult);
		topTitle.setCallback(this);
		
		super.initTopPanel(topTitle);
		
		listAddFind  = new ArrayList<Map<String,Object>>();
		adapterLvAddFind = new AdapterLvAddFind(this, listAddFind) ;
		lvResult.setAdapter(adapterLvAddFind);
		lvResult.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				call(listAddFind.get(arg2));
			}
		});
		
		MSGSender.findByGroupId(this, this.getIntent().getExtras().getString("ID"));
		
	} 

	  

	@Override
	public boolean OnBackPressed() {
		return false;	
	}


	//自定义组件点击响应传递id于此处理
	@Override
	public void call(int id) { 
		switch(id){
		case R.id.tvreturn:
			this.finish();
			break;
		case R.id.tvtitle:
			break;
		case R.id.tvmenu:
			break;
		 
		
		}
	}
 
	//ac里面表层的标签监听
	@Override
	public void onClick(View view) {
		call(view.getId());
	}


	//适配器里面的item点击响应
	@Override
	public void call(Map<String, Object> map) {
		//点中某个用户/群组，进入详情界面显示
		String type = MapListUtil.getMap(map, "TYPE").toString();
		
		//MSGSender.getUserGroupDetailByTypeId(this, type, Tools.getMap(map, "ID").toString() );
		Intent intent  = null;
		if(MapListUtil.getMap(map, "ID").toString().equals(Constant.id)){ //自己，跳转自己的详情和修改界面
			intent = new Intent( this, UserDetailAc.class);
			intent.putExtra("menu", "编辑");
		}else{
			if(type.equals("user")){	//用户展示
				intent = new Intent( this, UserDetailAc.class);
				intent.putExtra("menu", "更多");
			} 
		}
		AndroidTools.putMapToIntent(intent, map);		//该map并附加group nickname, 
		
		intent.putExtra("returntitle", "返回");
		intent.putExtra("title", " ");
		intent.putExtra("mode", "alpha"); 
		intent.putExtra("creatorid", "" + this.getIntent().getExtras().getString("CREATORID"));//加入群主id
		intent.putExtra("groupid", "" + this.getIntent().getExtras().getString("ID"));//加入群主id

		
		startActivity(intent);
	}


 

}
