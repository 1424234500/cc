package com.cc;

import interfac.CallInt;
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
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cc.R;
 


 
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
