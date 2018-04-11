package com.cc;

import interfac.CallInt;

import net.MSG;
import net.MSGSender;

import util.tools.MyJson;
import util.view.TopPanelReturnTitleMenu;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;


/**
 * @author Walker
 * @date 2017-3-22 下午9:28:17
 * Description: 修改备注
 */
public class UserNicknameAc extends BaseAc implements OnClickListener, CallInt {

	TopPanelReturnTitleMenu topTitle;


	EditText etNickname;
	String id = "";
	String groupid = "";
	@Override
	public void callback(String jsonstr) { 
		int cmd = MyJson.getCmd(jsonstr); 
		switch (cmd) { 
		case MSG.UPDATE_NICKNAME_BY_ID_NICKNAME_GROUPID:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				toast("修改成功");
				this.finish();
			}else{
				toast("修改失败：" + MyJson.getValue0(jsonstr));
			}

			break;
		}
	}
	
	
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ac_user_nickname);
	  
		topTitle = (TopPanelReturnTitleMenu)findViewById(R.id.top_title);
		super.initTopPanel(topTitle);	//有toptitle的可以通过父类初始化
		topTitle.setCallback(this);	

		etNickname = (EditText)findViewById(R.id.etnickname);
		
		
		etNickname.setText(this.getIntent().getExtras().getString("nickname"));
		id = this.getIntent().getExtras().getString("id");
		groupid = this.getIntent().getExtras().getString("groupid");
		if(groupid == null){
			groupid = "";
		}
			
		
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
		case R.id.tvmenu:
			String nick = this.etNickname.getText().toString();
			int i;
			for(i = 0;i < nick.length(); i++){
				if(nick.charAt(i) != ' '){
					break;
				}
			}
			nick = nick.substring(i);
			
			MSGSender.updateNickname(this, this.id, nick, groupid);
			openLoading();
			break;
	 
		}
	}

}
