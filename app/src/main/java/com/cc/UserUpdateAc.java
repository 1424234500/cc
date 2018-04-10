package com.cc;

import interfac.CallInt;
import interfac.CallString;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import net.MSG;
import net.MSGSender;
import util.tools.AndroidTools;
import util.tools.MD5;
import util.tools.MyJson;
import util.tools.MySP;
import util.tools.Tools;
import util.tools.picasso.NetImage;
import util.view.ClearEditText;
import util.view.TopPanelReturnTitleMenu;
import adapter.AdapterLvIds;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.R;

public class UserUpdateAc extends BaseAc implements OnClickListener, CallInt  {
	TopPanelReturnTitleMenu topTitle;

	ClearEditText cetUsername, cetEmail, cetPwd, cetRepwd;
	RadioGroup rgSex;
	RadioButton rbMale, rbFemale;
	Button bRegiste;
	TextView tvHelp, tvReturn;
 

	public void setByMap(final Map<String, Object> map){
		if(map != null){
			cetUsername.setText(map.get("USERNAME").toString());
			
			cetEmail.setText(map.get("SIGN").toString());
			cetPwd.setText("");
			cetRepwd.setText("");
			if(map.get("SEX").toString().equals("男")){
				rbFemale.setSelected(false);
				rbMale.setSelected(true);
			}else{
				rbMale.setSelected(false);
				rbFemale.setSelected(true);
			}
			this.topTitle.setMenu("");
		}
	}
	@Override
	public void callback(String jsonstr) {

		int cmd = MyJson.getCmd(jsonstr);
		String value0 = "", value1 = "", value2 = "", res = "";
		switch (cmd) { 
		case MSG.UPDATE_BY_USERNAME_SIGN_SEX_OLDPWD_NEWPWD:
			this.closeLoading();
			
			value0 = MyJson.getValue0(jsonstr);
			if(value0.equals("true")){
				toast("修改个人信息成功");
				
			try{
				JSONObject jb = new JSONObject(jsonstr);
 				 int i = 1;
				Constant.username = jb.getString("value"+i++); //保存用户名密码，头像
				Constant.sign = jb.getString("value"+i++); 
				Constant.sex = jb.getString("value"+i++); 
			}catch (Exception e) {
				// TODO: handle exception
			}
			finish();
		}else{
			value1 = MyJson.getValue1(jsonstr); 
			toast("修改个人信息失败:" + value1);
		}
			
			
			break;
	 
		}

	}
 


		 

	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_user_update);
	 
		cetUsername = (ClearEditText)findViewById(R.id.cetusername);
		cetPwd = (ClearEditText)findViewById(R.id.cetpwd);
		cetEmail = (ClearEditText)findViewById(R.id.cetemail);
		cetRepwd = (ClearEditText)findViewById(R.id.cetrepwd);
		
		rgSex = (RadioGroup)findViewById(R.id.rgsex);
		rbMale = (RadioButton)findViewById(R.id.rbmale);
		rbFemale = (RadioButton)findViewById(R.id.rbfemale);

		bRegiste = (Button)findViewById(R.id.bregiste);
		
		
		bRegiste.setOnClickListener(this);
		 
		topTitle = (TopPanelReturnTitleMenu)findViewById(R.id.top_title);
		super.initTopPanel(topTitle);	//有toptitle的可以通过父类初始化
		topTitle.setAlphaMode(true);
		topTitle.setCallback(this);	

		setByMap( AndroidTools.getMapFromIntent(this.getIntent()));	//设置从上级传来的用户数据
	
	} 

	 
	@Override
	public void onClick(View v) {
		call(v.getId());
	}

	private void ClickRegiste() {

		String username = cetUsername.getText().toString();
		String email = cetEmail.getText().toString();
		RadioButton radioButton = (RadioButton)findViewById(rgSex.getCheckedRadioButtonId());
		String sex =  radioButton.getText() + "";		
		String oldpwd = cetPwd.getText().toString();
		String newpwd = cetRepwd.getText().toString();
		
		if(!Tools.testNull(username,sex) ){
			this.openLoading();
			if(!newpwd.equals("")){
				newpwd = MD5.make(Constant.id, newpwd);
				oldpwd = MD5.make(Constant.id, oldpwd);
			}
			MSGSender.update(this, username, email, sex, oldpwd,newpwd);
		}else{
			toast("昵称，性别不能为空");
		}
		

	} 

	@Override
	public boolean OnBackPressed() {
		return false;	//false关闭this ac 
	}
	@Override
	public void call(int id) {
		// TODO 自动生成的方法存根
		switch (id) {
		case R.id.tvreturn:
			this.finish();
			break;
			
		case R.id.bregiste:
			ClickRegiste();
			break;
		 
		}
	}

}
