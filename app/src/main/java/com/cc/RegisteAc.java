package com.cc;

import interfac.CallString;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import net.MSG;
import net.MSGSender;
import util.tools.AndroidTools;
import util.tools.MyJson;
import util.tools.MySP;
import util.tools.Tools;
import util.view.ClearEditText;
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

public class RegisteAc extends BaseAc implements OnClickListener {

	ClearEditText cetUsername, cetEmail, cetPwd, cetRepwd;
	RadioGroup rgSex;
	Button bRegiste;
	TextView tvHelp, tvReturn;
 

	
	@Override
	public void callback(String jsonstr) {
		out("get. " + jsonstr);

		int cmd = MyJson.getCmd(jsonstr);
		String value0 = "", value1 = "", value2 = "", res = "";
		switch (cmd) { 
		case MSG.REGISTE_BY_USERNAME_EMAIL_SEX_PWD:
			this.closeLoading();

			value0 = MyJson.getValue0(jsonstr);
			value1 = MyJson.getValue1(jsonstr);
			if(value0.equals( "true")){
			//(MyJson.makeJson(MSG.REGISTE_BY_USERNAME_EMAIL_SEX_PWD, "false", "添加用户数据失败"));
				//注册成功， 下一步
				//toast( "注册成功"); 
			  
				MySP.put(this, "userid", value1);
				MySP.put(this, "userpwd", Constant.pwd);
				
				AndroidTools.dialogTip1(this, "注册成功，您的cc号是:" + value1, "返回登录", new CallString() {
					@Override
					public void callback(String str) {
						RegisteAc.this.startActivity(new Intent(RegisteAc.this, LoginAc.class));
						RegisteAc.this.finish();
					}
				}); 
			}else{
				//toast("注册失败."+ value1); 
				out("注册失败."+ value1);
				AndroidTools.dialogTip1(this, "注册失败，错误原因:" + value1, "确定", new CallString() {
					@Override
					public void callback(String str) {
						//RegisteAc.this.startActivity(new Intent(RegisteAc.this, LoginAc.class));
						//RegisteAc.this.finish();
					}
				}); 
			}
			break;
	 
		}

	}
 


		 

	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_registe);
	 
		cetUsername = (ClearEditText)findViewById(R.id.cetusername);
		cetPwd = (ClearEditText)findViewById(R.id.cetpwd);
		cetEmail = (ClearEditText)findViewById(R.id.cetemail);
		cetRepwd = (ClearEditText)findViewById(R.id.cetrepwd);
		
		rgSex = (RadioGroup)findViewById(R.id.rgsex);
		bRegiste = (Button)findViewById(R.id.bregiste);
		
		tvHelp = (TextView)findViewById(R.id.tvhelp);
		tvReturn = (TextView)findViewById(R.id.tvreturn);
		
		tvReturn.setOnClickListener(this);
		tvHelp.setOnClickListener(this);
		bRegiste.setOnClickListener(this);
		cetPwd.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {
				cetRepwd.setText("");
			}
		});
		
	} 

	 
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvreturn:
			startActivity(new Intent(this, LoginAc.class));
			this.finish();
			break;
		case R.id.tvhelp:
			break;
		case R.id.bregiste:
			ClickRegiste();
			break;
		 
		}
	}

	private void ClickRegiste() {

		String username = cetUsername.getText().toString();
		String pwd = cetPwd.getText().toString();
		String repwd = cetRepwd.getText().toString();
		String email = cetEmail.getText().toString();
		
	    RadioButton radioButton = (RadioButton)findViewById(rgSex.getCheckedRadioButtonId());
		String sex =  radioButton.getText() + "";
		
		if(!Tools.testNull(username,pwd,repwd,email,sex) ){
			if(repwd.equals(pwd)){
				Constant.pwd = pwd;

				this.openLoading( );
				
				MSGSender.registe(this, username, email, sex, pwd);
			}else{
				toast("两次密码不同");
				cetRepwd.setText("");
			}
		}else{
			toast("有数据未填写");
		}
		

	}

	  
	public void out(String str) {
		Tools.out("RegisteAc." + str);
	}

	 

	@Override
	public void OnStart() {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void OnResume() {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void OnPause() {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void OnStop() {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public void OnDestroy() {
		// TODO 自动生成的方法存根
		
	}



	@Override
	public boolean OnBackPressed() {
		//返回登录
		startActivity(new Intent(this, LoginAc.class));
		
		return false;	//false关闭this ac 
	}

}
