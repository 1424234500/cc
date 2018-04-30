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
import util.view.ClearEditText;
import util.view.TopPanelReturnTitleMenu;
import adapter.AdapterLvAddFind;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;




/**
 * @author Walker
 * @date 2017-3-3 下午2:25:12
 * Description: 添加好友/群组界面，两个适配器，服务器数据查询
 */
public class AddAc extends BaseAc implements CallInt, View.OnClickListener, CallMap{
 
	@Override
	public void callback(String jsonstr) {
		int cmd = JsonMsg.getCmd(jsonstr);
		switch (cmd) {
		case MSGTYPE.FIND_USERS_GROUPS_BY_ID:// 从服务器返回的查询结果
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
		}

	}
	ListView lvResult;
	AdapterLvAddFind adapterLvAddFind;
	List<Map<String, Object>> listAddFind  ; 
	
	TopPanelReturnTitleMenu topTitle;
	TextView tvOk;
	ClearEditText cetSearch;
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ac_add);

		cetSearch = (ClearEditText)findViewById(R.id.cetsearch);

		topTitle = (TopPanelReturnTitleMenu)findViewById(R.id.top_title);
		lvResult=  (ListView)findViewById(R.id.lvresult);
		tvOk =  (TextView)findViewById(R.id.tvok);
		topTitle.setCallback(this);
		tvOk.setOnClickListener(this);
		
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
		case R.id.tvok:
			//搜索
			if(Tools.notNull( cetSearch.getText().toString())){
				MSGSender.findById(this, cetSearch.getText().toString());
				this.openLoading();
			}else{
				toast("请输入查询条件后搜索");
			}
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
		
		//MSGSender.getUserGroupDetailByTypeId(this, type, MapListUtil.getMap(map, "ID").toString() );
		Intent intent  = null;
		if(MapListUtil.getMap(map, "ID").toString().equals(Constant.id)){
			//自己，跳转自己的详情和修改界面
			intent = new Intent( this, UserDetailAc.class);
			intent.putExtra("menu", "编辑");

		}else{
			if(type.equals("user")){	//用户展示
				intent = new Intent( this, UserDetailAc.class);
				intent.putExtra("menu", "更多");
			}else{		//群组展示
				intent = new Intent( this, GroupDetailAc.class);
				
				if(MapListUtil.getMap(map, "CREATORID").toString().equals(Constant.id)){
					intent.putExtra("menu", "编辑");	//自己创建的群,进入修改群名称
				}else{
					intent.putExtra("menu", "更多");	//非自己创建的群,进入退群
				}
			}


		}
		AndroidTools.putMapToIntent(intent, map);
		
		intent.putExtra("returntitle", "返回");
		intent.putExtra("title", " ");
		intent.putExtra("mode", "alpha"); 
		startActivity(intent);
	}


 

}
