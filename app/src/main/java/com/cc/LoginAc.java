package com.cc;

import interfac.CallInt;

import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import net.MSG;
import net.MSGSender;
import service.NetService;
import util.tools.AndroidTools;
import util.tools.MyJson;
import util.tools.MySP;
import util.Tools;
import util.tools.picasso.NetImage;
import util.view.ClearEditText;
import adapter.AdapterLvIds;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class LoginAc extends BaseAc implements OnClickListener, TextWatcher {

	ClearEditText cetId, cetPwd;
	ImageView ivdown, ivprofile;
	Button blogin;
	TextView tvcannotlogin, tvnewuser;
	View rllogin, llloginmove;
	boolean keyon = false;

	TextView tvpay;
	
	static int cc = 0;
	@Override
	public void callback(String jsonstr) {
	//	out("get. " + jsonstr);

		int cmd = MyJson.getCmd(jsonstr);
		String value = "", value1 = "", value2 = "", value3, value4,value5,value6,value7,  res = "";
		switch (cmd) {
		case MSG.PROFILE_PATH_BY_ID:// 从服务器返回的图片信息
			final String path = MyJson.getValue0(jsonstr);
			if(!Tools.testNull(path)){
		    	 NetImage.loadProfile(this, path, ivprofile);
		    	 
		    	 //out("path:." + cc++);
			}

			
			
			break;
		case MSG.LOGIN_BY_ID_PWD:
			this.closeLoading();

			value = MyJson.getValue0(jsonstr);
		
			if(value.equals( "true")){
				try {
					JSONObject jb = new JSONObject(jsonstr);
					value1 = jb.getString("value1");
					value2 = jb.getString("value2");
					value3 = jb.getString("value3");
					value4 = jb.getString("value4");
					value5 = jb.getString("value5");
					value6 = jb.getString("value6");
					value7 = jb.getString("value7");
					//登陆成功， 下一步
					Tools.toast(this, "登陆成功"); 
					Constant.offlineMode = 0;

					Constant.id = value1; 
					Constant.username = value2;//保存用户名密码，头像
					Constant.profilepath = value3;
					Constant.sign = value4;
					Constant.sex = value5;
					Constant.profilepathwall = value6;
					Constant.email = value7;
					Constant.ivdoll =  Tools.parseInt(jb.getString("value8"));
					
					AndroidTools.putIfLogin(getApplicationContext(), "true");
					
					Map<String, Object> map = sqlDao.queryOne("select * from " + Constant.LOGIN_USER + " where id=? ", Constant.id);
					if(map == null)	//插入或者更新
						this.sqlDao.execSQL("insert into " + Constant.LOGIN_USER + " values(?, ?, ?) ", Constant.id, Constant.pwd, Constant.profilepath);
					else
						this.sqlDao.execSQL("update  " + Constant.LOGIN_USER + " set pwd=?,profilepath=? where id=? ",  Constant.pwd, Constant.profilepath, Constant.id);
					
					MySP.put(this, "userid", Constant.id);
					MySP.put(this, "userpwd", Constant.pwd);
					
					startActivity(new Intent(LoginAc.this, MainAc.class));
					this.finish();
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
 				Tools.toast(this, "登陆失败."+ value1); 
				out("登陆失败."+ value1);
			}
			break;
		case MSG.TOAST:
		case MSG.OK:
		case MSG.ERROR:
		case MSG.CLOSE:
			Tools.toast(this, MyJson.getValue0(jsonstr));
			break;
		}

	}
 
 

	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_login);
		rllogin = (View) findViewById(R.id.rllogin);
		llloginmove = (View) findViewById(R.id.llloginmove);
		cetId = (ClearEditText) findViewById(R.id.id);
		cetPwd = (ClearEditText) findViewById(R.id.pwd);
		ivdown = (ImageView) findViewById(R.id.ivdown);
		ivprofile = (ImageView) findViewById(R.id.ivprofile);
		blogin = (Button) findViewById(R.id.blogin);
		tvcannotlogin = (TextView) findViewById(R.id.tvcannotlogin);
		tvnewuser = (TextView) findViewById(R.id.tvnewuser);
		
		
		


		
		
		
		
		rllogin.setOnClickListener(this);
		cetId.setOnClickListener(this);
		cetId.setOnClick(new CallInt() {
			@Override
			public void call(int id) {
				cetPwd.setText("");
			}
		});
		cetId.addTextChangedListener(this);

		cetPwd.setOnClickListener(this);

		ivdown.setOnClickListener(this);
		blogin.setOnClickListener(this);
		tvcannotlogin.setOnClickListener(this);
		tvcannotlogin.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View arg0) {
				final EditText inputServer = new EditText(LoginAc.this);
				inputServer.setText(Constant.serverIp);
		        AlertDialog.Builder builder = new AlertDialog.Builder(LoginAc.this);
		        builder.setTitle("设置服务器IP").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)  .setNegativeButton("Cancel", null);
		        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	String ip = inputServer.getText().toString();
		            	Constant.serverIp = ip;
		            	LoginAc.this.startService(new Intent(LoginAc.this, NetService.class).putExtra("socket", ip));
		             }
		        });
		        builder.show();
				return true;
			}
		});
		tvnewuser.setOnClickListener(this);
 
		// 动画效果
		Animation myAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_login_move);
		llloginmove.startAnimation(myAnimation);
		
		//初始化账号密码
		String id = MySP.get(this, "userid", "");
		String pwd = MySP.get(this, "userpwd", "");

		AndroidTools.putIfLogin(getApplicationContext(), "false");
		
		cetId.setText(id);
		cetPwd.setText(pwd);
	  
		//MSGSender.beats(this);
	} 

	 
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		/////////////////
		case R.id.ivdown:
			if (popupWindow == null)
				OpenIvDown(v);
			else
				CloseIvDown();
			closeKeyboard(rllogin);
			break;
		case R.id.blogin:
			ClickLogin();
			break;
		case R.id.tvnewuser:
			//创建用户跳转RegisteAc,之后再回来登录
			startActivity(new Intent(LoginAc.this, RegisteAc.class));
			this.finish();
			
			break;
		case R.id.tvcannotlogin:
			Constant.offlineMode = 1;
			startActivity(new Intent(LoginAc.this, MainAc.class));
			this.finish();
		
			break;
		case R.id.rllogin:
			closeKeyboard(rllogin);
			break;
		}
	}

	private void ClickLogin() {

		String id = cetId.getText().toString();
		String pwd = cetPwd.getText().toString();
		if (!Tools.testNull(id) && !Tools.testNull(pwd)) {
			//合法账号密码，发送登陆请求,并且本地放入本地临时账户信息记录
			Constant.id = id;
			Constant.pwd = pwd;
			MSGSender.loginByIdPwd(this, id, pwd);
			//登陆中 提示
			this.openLoading();
		} else {
			out("请输入有效账号和密码");
		}

	}

	public void openKeyboard() {
		keyon = true;
	}

	public void closeKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager) getApplicationContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0); // 关闭软键盘
		keyon = false;
	}

	PopupWindow popupWindow = null; // 下拉窗口
	List<Map<String, Object>>  liststr = null; // listview的数据集合
	AdapterLvIds mAdapter = null;// listview的适配器

	public void CloseIvDown() {
		popupWindow.dismiss();
		popupWindow = null;
		liststr = null;
		mAdapter = null;
	}

	private void OpenIvDown(View view) {
		// TODO 自动生成的方法存根
		// 构造需要显示的数据
		liststr = this.sqlDao.queryList("select * from " + Constant.LOGIN_USER + " ");
		if (liststr == null)
			return;

		View contentView = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.listview_popup, null);
		ListView lvids = (ListView) contentView.findViewById(R.id.lvids);// 取出临时布局中的空间lv
		// 设置 事件回调处理
		mAdapter = new AdapterLvIds(this, liststr) {
			@Override
			public boolean OnOk(Object obj) {
				// TODO 自动生成的方法存根
				Map<String, Object> res = (Map<String, Object>)obj;
				out("chose id=" + res.get("id") + " pwd=" + res.get("pwd"));
				cetId.setText( res.get("id").toString());
				cetPwd.setText(res.get("pwd").toString());
				popupWindow.dismiss();// 关闭
				return false;
			}

			@Override
			public boolean OnDel(Object obj) {
				Map<String, Object> res = (Map<String, Object>)obj;
				out("del:" + res.get("id"));
				sqlDao.execSQL("delete from " + Constant.LOGIN_USER + " where id=? ", res.get("id"));
				liststr.remove(obj);	//上下转后 这个对象还是不是list中的那个呢？能否删除
				mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
				// popupWindow.dismiss();//关闭，
				// ClickIvDown(ivdown);//重启下拉菜单
				return false;
			}

		};
		lvids.setAdapter(mAdapter);

		popupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		popupWindow.setTouchable(true);

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAsDropDown(view);

	}

	public void out(String str) {
		Tools.out("LoginAc." + str);
	}

	// id文本输入监控
	@Override
	public void afterTextChanged(Editable e) {
		Map<String, Object> llu = sqlDao.queryOne("select * from " + Constant.LOGIN_USER + " where id=? ",  cetId.getText().toString());
		String getpwd = "", getpath = "", getid = "";
		if (llu != null){
			getid = llu.get("id").toString();
			getpwd = llu.get("pwd").toString();
			getpath = llu.get("profilepath").toString();
			if (!Tools.testNull(getpwd)) {
				cetPwd.setText(getpwd);
				if (!Tools.testNull(getpath)) {
					NetImage.loadProfile(this, getpath, this.ivprofile);
				} 
			} else {
				cetPwd.setText("");
			}
		} else{
			cetPwd.setText("");
			if(!Tools.testNull(cetId.getText().toString()))
				MSGSender.getProfileByPath(this,  cetId.getText().toString());
		}
		
		

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO 自动生成的方法存根

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO 自动生成的方法存根

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
		
		this.exitApp();//退出app
		return false;	
	}

}
