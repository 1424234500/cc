package com.cc;

import interfac.CallInt;

import net.MSG;
import net.MSGSender;

import util.tools.MyJson;
import util.Tools;
import util.view.TopPanelReturnTitleMenu;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;


/**
 * @author Walker
 * @date 2017-5-1 下午8:21:20
 * Description: 更新/创建群 需要 菜单栏，intent. name:群名字，num:群人数，check:是否验证
 */
public class GroupCreateAc extends BaseAc implements OnClickListener, CallInt {

	TopPanelReturnTitleMenu topTitle;


	EditText etname;
	Spinner snum;
	CheckBox cbok;
	
	String id;
	@Override
	public void callback(String jsonstr) { 
		int cmd = MyJson.getCmd(jsonstr); 
		switch (cmd) { 
		case MSG.CREATE_GROUP_BY_NAME_NUM_CHECK:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				toast(MyJson.getValue1(jsonstr));
				this.finish();
			}else{
				toast(  MyJson.getValue0(jsonstr));
			}
			break;
		case MSG.DOLL_CREATE_BY_NAME_NUM:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				toast("匿名房间创建成功");
				this.finish();
			}else{
				toast(  MyJson.getValue0(jsonstr));
			}
			break;
		}
	}
	
	//需要 菜单栏，name:群名字，num:群人数，check:是否验证
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_create_group);
	  
		topTitle = (TopPanelReturnTitleMenu)findViewById(R.id.top_title);
		super.initTopPanel(topTitle);	//有toptitle的可以通过父类初始化
		topTitle.setCallback(this);	

		etname = (EditText)findViewById(R.id.etname);
		etname.setText(this.getIntent().getExtras().getString("name"));
		snum = (Spinner)findViewById(R.id.snum);
		String[] nums = getResources().getStringArray(R.array.group_num);
		String num = this.getIntent().getExtras().getString("num");
		int count = Tools.getCount(nums, num);
		if(count >= 0){
			snum.setSelection(count);
		}
		cbok = (CheckBox)findViewById(R.id.cbok);
		cbok.setChecked(! this.getIntent().getExtras().getString("check").equals("true") );
		cbok.setEnabled(this.getIntent().getExtras().getString("type").equals("group") );
		
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
			if(! Tools.testNull(etname.getText().toString(), snum.getSelectedItem())){
				String name = etname.getText().toString();
				String num = snum.getSelectedItem().toString();
				String check = cbok.isChecked()? "true": "false";
				
				if(this.getIntent().getExtras().getString("type").equals("group")){
					MSGSender.createGroupByNameNumCheck(this, name,num,check);
				}else{
					MSGSender.createDollByNameNum(this, name, num) ;
				}
				
				openLoading();
			}else{
				toast("需要填写名称");
			}
			break;
	 
		}
	}

}
