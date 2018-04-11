package com.cc;

import interfac.CallInt;

import java.util.Map;

import net.MSG;
import net.MSGSender;
import util.tools.AndroidTools;
import util.tools.MyJson;
import util.Tools;
import util.view.TopPanelReturnTitleMenu;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * @author Walker
 * @date 2017-3-22 下午8:41:58
 * Description: 用户更多，删除好友，修改备注，修改分组
 */
public class GroupMoreAc extends BaseAc implements OnClickListener, CallInt {

	TopPanelReturnTitleMenu topTitle;

	EditText etname, etsign;
	Spinner snum;
	CheckBox cbok;
	TextView tvid;
	String id;
	Button bDelete;
	 
	@Override
	public void callback(String jsonstr) { 
		int cmd = MyJson.getCmd(jsonstr); 
		int i = 0; 
		switch (cmd) { 
		case MSG.DELETE_RELEATIONSHIP_BY_TYPE_ID:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				toast("删除成功");
				this.finish();
			}else{
				toast("删除失败：" + MyJson.getValue0(jsonstr));
			}

			break;
		case MSG.UPDATE_GROUP_BY_ID_NAME_SIGN_NUM_CHECK:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				toast("更新信息成功");
				this.finish();
			}
			
			break;
		}
	}
	
	
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_group_more);
	  
		topTitle = (TopPanelReturnTitleMenu)findViewById(R.id.top_title);
		
		super.initTopPanel(topTitle);	//有toptitle的可以通过父类初始化
		topTitle.setCallback(this);	
		//topTitle.setMenu("");
		topTitle.setAlphaMode(true);
		Map<String, Object> map =	AndroidTools.getMapFromIntent(this.getIntent());
		etname = (EditText)findViewById(R.id.etname);
		etname.setText(Tools.getMap(map, "USERNAME"));
		etsign = (EditText)findViewById(R.id.etsign);
		etsign.setText(Tools.getMap(map, "SIGN"));
		snum = (Spinner)findViewById(R.id.snum);
		String[] nums = getResources().getStringArray(R.array.group_num);
		String num = Tools.getMap(map, "NUM");
		int count = Tools.getCount(nums, num);
		if(count >= 0){
			snum.setSelection(count);
		}
		cbok = (CheckBox)findViewById(R.id.cbok);
		cbok.setChecked(Tools.getMap(map, "CHECKED").equals("true") );
	
		tvid	 = (TextView)findViewById(R.id.tvid);
		tvid.setText(Tools.getMap(map, "ID"));
		
		bDelete = (Button)findViewById(R.id.bdelete);
		bDelete.setOnClickListener(this); 
		
		if(Tools.getMap(map, "CREATORID").equals(Constant.id)){
			bDelete.setText("解散该群");
			topTitle.setMenu("更新");
			etname.setClickable(true);
			snum.setClickable(true);
			cbok.setClickable(true);
			etsign.setClickable(true);
		}else{
			bDelete.setText("退出该群");
			topTitle.setMenu("");
			etname.setClickable(false);
			etname.setEnabled(false);
			etsign.setEnabled(false);
			snum.setClickable(false);
			cbok.setClickable(false);
			etsign.setClickable(false);
			
			
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
			this.topTitle.setAlphaMode(true);	//回退设置的透明top panel

			this.finish();
			break;
		case R.id.tvtitle:
			break;
		case R.id.tvmenu:	//更新
			if(topTitle.getMenuText().equals("更新")){
				MSGSender.updateGroupByNameSignNumCheck(this,tvid.getText().toString(), etname.getText().toString(), etsign.getText().toString(), snum.getSelectedItem().toString(),cbok.isChecked()?"true":"false");
				this.openLoading();
			}
			
			break;
		case R.id.bdelete:	//删除群
			 MSGSender.deleteReleationship(this, "group", tvid.getText().toString());
			break;
		
		}
	}

}
