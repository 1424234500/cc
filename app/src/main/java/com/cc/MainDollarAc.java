package com.cc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.MSG;
import net.MSGSender;
import util.tools.AndroidTools;
import util.tools.MyJson;
import util.Tools;
import adapter.AdapterLvDoll;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainDollarAc extends BaseAc  {
	static AdapterLvDoll adapterLv;
	static  List<Map<String, Object>> listItems = new ArrayList<Map<String,Object>>(); 		
	static ListView lv;

	@Override
	public void callback(String jsonstr) {
		int cmd = MyJson.getCmd(jsonstr);
		int i , j;
		List<Map<String, Object>>  list;
		Map<String, Object>  map;
		String v, v1;
		switch (cmd) {
		case MSG.DOLL_ROOM_LIST:
            swipeRefreshLayout.setRefreshing(false);

			list = MyJson.getList(jsonstr);
			listItems.clear();
			listItems.addAll(list);
			adapterLv.notifyDataSetChanged();
			break;
		case MSG.DOLL_INTO_BY_NAME:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				map = MyJson.getMap(jsonstr);
				onClickItem(map);
			}else{
				toast(  MyJson.getValue0(jsonstr));
			}
			break;
		case MSG.DOLL_CREATE_BY_NAME_NUM:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				//toast("匿名房间创建成功");
				//this.finish();
				map = MyJson.getMap(jsonstr);
				onClickItem(map);
			}else{
				toast(  MyJson.getValue0(jsonstr));
			}
			break;
		case MSG.DOLL_IN_OR_OUT_BY_NAME_TYPE:
			//cr.name, "out");
			v = MyJson.getValue0(jsonstr);
			v1 =  MyJson.getValue1(jsonstr);
			Tools.out(v + "房间有人" + v1);
			i = Tools.getCountListByName(listItems, "NAME", v);
			if(i >= 0){
				j = Tools.parseInt(Tools.getList(listItems, i, "NOWNUM"));
				if(v1.equals("in")){
					j ++;
				}else{
					j --;
				}
				if(j <= 0){
					listItems.remove(i);
				}else{
					listItems.get(i).put("NOWNUM", j);
				}
				adapterLv.notifyDataSetChanged();
			}
			
			break;
			
			
			
		}
			

	}
 
 
	SwipeRefreshLayout swipeRefreshLayout;

	@Override
	public void OnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ac_main_dollar);
		Tools.log("dollar OnCreate");
		lv = (ListView)findViewById(R.id.lv);
		adapterLv = new AdapterLvDoll(this, listItems);
		lv.setAdapter(adapterLv);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, 	long arg3) {
				//onClickItem(listItems.get(arg2));
				MSGSender.intoDollByName(MainDollarAc.this, Tools.getList(listItems, arg2, "NAME"));
				
			}
			
		});
		lv.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				if (lv.getLastVisiblePosition() < (lv .getCount() - 1)) {
					MSGSender.getDollRooms(MainDollarAc.this);
				}
			}
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			}
		});
			
		
		  swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl);
	         //设置刷新时动画的颜色，可以设置4个
	         swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
		     swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
	            @Override
	            public void onRefresh() {
	               // tv.setText("正在刷新");
	        		MSGSender.getDollRooms(MainDollarAc.this);	//获取会话列表
	            }
	        });
			
		
	} 
	//进入map房间
	public void onClickItem(Map<String,Object> map){
		Tools.out("进入房间:" + map);
		
		
		Intent intent = new Intent(this, ChatAcDoll.class);
		AndroidTools.putMapToIntent(intent, map);
		startActivity(intent);
	}
	  

	@Override
	public boolean OnBackPressed() {
		
		//this.exitApp();//退出app
		return true;	
	}



	@Override
	public void OnStart() {
		Tools.log("dollar OnStart");
	} 
	@Override
	public void OnResume() {
		Tools.log("dollar OnResume");
		
	} 
	@Override
	public void OnPause() {
		Tools.log("dollar OnPause");

	} 
	@Override
	public void OnStop() {
		Tools.log("dollar OnStop");
	} 
	@Override
	public void OnDestroy() {
		Tools.log("dollar OnDestroy");
	}

}
