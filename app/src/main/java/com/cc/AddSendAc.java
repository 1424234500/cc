package com.cc;

import interfac.CallInt;

import java.util.Map;

import net.MSGTYPE;
import net.MSGSender;

import util.AndroidTools;
import util.JsonMsg;
import util.JsonUtil;
import util.MapListUtil;
import util.Tools;
import util.picasso.NetImage;
import util.view.ClearEditText;
import util.view.TopPanelReturnTitleMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * @author Walker
 * @date 2017-3-5 上午11:22:49
 * Description: 好友添加，填写验证信息,同意添加/拒绝添加
 */
public class AddSendAc extends BaseAc implements OnClickListener, CallInt {

	Button bDelete;
	EditText etYanzhen;
	ClearEditText cetBeizhu;
	String type="";
	String groupid="";
	TopPanelReturnTitleMenu topTitle;
	TextView tvUsername,  tvId;
	ImageView ivSex, ivProfile;
	public void setByMap(final Map<String, Object> map){
		if(map != null){
			if(MapListUtil.getMap(map, "TYPE").toString().equals("user")){//加人，设置验证信息，备注
				type = "user";
				tvUsername.setText(MapListUtil.getMap(map, "USERNAME").toString());
				tvId.setText(MapListUtil.getMap(map, "ID").toString());
				NetImage.loadProfile(this, MapListUtil.getMap(map, "PROFILEPATH").toString(), ivProfile);
				NetImage.loadImage(this, MapListUtil.getMap(map, "SEX").toString().equals("男")?R.drawable.boy:R.drawable.girl, ivSex);
			}else if(MapListUtil.getMap(map, "TYPE").toString().equals("group")){//加群，设置验证信息 
				type = "group";
				tvUsername.setText(MapListUtil.getMap(map, "USERNAME").toString());
				tvId.setText(MapListUtil.getMap(map, "ID").toString());
				NetImage.loadImage(this, R.drawable.contact, ivSex);
				NetImage.loadProfile(this, MapListUtil.getMap(map, "PROFILEPATH").toString(), ivProfile);
			}else if(MapListUtil.getMap(map, "TYPE").toString().equals("adduser")){	//同意？加人,设置备注
				type = "user";
				tvUsername.setText(MapListUtil.getMap(map, "USERNAME").toString());
				tvId.setText(MapListUtil.getMap(map, "ID").toString());
				NetImage.loadImage(this, MapListUtil.getMap(map, "SEX").toString().equals("男")?R.drawable.boy:R.drawable.girl, ivSex);
				NetImage.loadProfile(this, MapListUtil.getMap(map, "PROFILEPATH").toString(), ivProfile);
				cetBeizhu.setText(MapListUtil.getMap(map, "YANZHEN").toString().substring(0, "我是".length()).equals("我是")?MapListUtil.getMap(map, "YANZHEN").toString().substring("我是".length()) : "" );
				etYanzhen.setText(MapListUtil.getMap(map, "YANZHEN").toString());
				bDelete.setVisibility(View.VISIBLE);
				bDelete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						MSGSender.resultUserGroupByTypeIdResultBeizhu(AddSendAc.this, type, tvId.getText().toString(), "拒绝", cetBeizhu.getText().toString()  );
						MainMsgAc.listSessions.remove( MapListUtil.getCountListByName( MainMsgAc.listSessions,"ID", MapListUtil.getMap(map, "ID").toString()));
						finish();
					}
				});
			}else if(MapListUtil.getMap(map, "TYPE").toString().equals("addgroup")){	//同意？加群,设置备注
				type = "group";
				groupid = MapListUtil.getMap(map, "GROUPID");

				tvUsername.setText(MapListUtil.getMap(map, "USERNAME").toString());
				tvId.setText(MapListUtil.getMap(map, "ID").toString());
				//NetImage.loadImage(this, MapListUtil.getMap(map, "SEX").equals("男")?R.drawable.boy:R.drawable.girl, ivSex);
				NetImage.loadProfile(this, MapListUtil.getMap(map, "PROFILEPATH").toString(), ivProfile);
				cetBeizhu.setText(MapListUtil.getMap(map, "YANZHEN").toString().substring(0, "我是".length()).equals("我是")?MapListUtil.getMap(map, "YANZHEN").toString().substring("我是".length()) : "" );
				etYanzhen.setText(MapListUtil.getMap(map, "YANZHEN").toString());
				bDelete.setVisibility(View.VISIBLE);
				bDelete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						MSGSender.resultUserGroupByTypeIdResultBeizhu(AddSendAc.this, type, tvId.getText().toString(), "拒绝", cetBeizhu.getText().toString() ,MapListUtil.getMap(map, "GROUPID") );
						MainMsgAc.listSessions.remove( MapListUtil.getCountListByName( MainMsgAc.listSessions,"ID", MapListUtil.getMap(map, "ID").toString()));
						finish();
					}
				});
			}
			
		}
	}
	@Override
	public void callback(String jsonstr) {

		int cmd = JsonMsg.getCmd(jsonstr);
		switch (cmd) { 
		case MSGTYPE.ADD_USER_GROP_BY_TYPE_ID_YANZHEN_NICKNAME:
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				toast("已发送添加请求");
				finish();
			}else{
				toast(JsonMsg.getValue0(jsonstr));
				finish();
			}
			break;
		}
	}
	
	
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_add_send);

		topTitle = (TopPanelReturnTitleMenu)findViewById(R.id.top_title);
		tvUsername = (TextView)findViewById(R.id.tvusername);
		tvId = (TextView)findViewById(R.id.tvid);
		ivSex = (ImageView)findViewById(R.id.ivsex);
		bDelete = (Button)findViewById(R.id.bdelete);
		bDelete.setVisibility(View.INVISIBLE);
		ivProfile = (ImageView)findViewById(R.id.ivprofile);
		
		etYanzhen = (EditText)findViewById(R.id.etyanzhen);
		cetBeizhu = (ClearEditText)findViewById(R.id.cetnickname);
		
		topTitle.setCallback(this);
		super.initTopPanel(topTitle);
		topTitle.setAlphaMode(true);
		
		Intent intent = this.getIntent();
		Map<String, Object> map = AndroidTools.getMapFromIntent(intent);
		setByMap(map);	//设置从上级传来的用户数据
		
	} 

	 
	@Override
	public void onClick(View v) {
		call(v.getId());
	}
	 
	@Override
	public boolean OnBackPressed() {
		return false;	//false关闭this ac 
	}
	
	 
	@Override
	public void call(int id) {
		switch(id){
		case R.id.tvreturn:
			this.finish();
			break;
		case R.id.tvtitle:
			break;
		case R.id.tvmenu: 
			if(topTitle.getMenuText().equals("发送")){
				MSGSender.addUserGroupByTypeIdYanZhengBeizhu(this, type, tvId.getText().toString(), etYanzhen.getText().toString(), cetBeizhu.getText().toString()  );
				this.openLoading();
			}else if(topTitle.getMenuText().equals("同意")){
				MSGSender.resultUserGroupByTypeIdResultBeizhu(this, type, tvId.getText().toString(), "同意", cetBeizhu.getText().toString() ,groupid );
				MainMsgAc.listSessions.remove( MapListUtil.getCountListByName( MainMsgAc.listSessions,"ID", this.tvId.getText().toString()));
				finish();
			}  
			break;
		case R.id.tvok:
			 
			break;
		
		}
	}

}
