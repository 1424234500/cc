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
 * @date 2017-3-22 下午8:41:58
 * Description: 用户更多，删除好友，修改备注，修改分组
 */
public class UserMoreAc extends BaseAc implements OnClickListener, CallInt {

	TopPanelReturnTitleMenu topTitle;
	TextView tvUsername, tvNickname, tvId, tvEmail, tvGroup;
	Button bDelete;
	
	public void setByMap(final Map<String, Object> map){
		if(map != null){
			Tools.log(Tools.getMap(map, "NICKNAME")==null?"null":"not null");
			Tools.log(Tools.getMap(map, "NICKNAME").toString()==null?"toString null":"toString not null");
			//Tools.log("NICKNAME:'" + Tools.getMap(map, "NICKNAME").toString()  + "'");
			
			tvUsername.setText(Tools.getMap(map, "USERNAME").toString());
			tvNickname.setText(Tools.getMap(map, "NICKNAME").toString().equals(" ")?"":Tools.getMap(map, "NICKNAME").toString());
			tvId.setText(Tools.getMap(map, "ID").toString());
			tvEmail.setText(Tools.getMap(map, "EMAIL").toString());
			
		}
	}
	@Override
	public void callback(String jsonstr) { 
		int cmd = MyJson.getCmd(jsonstr); 
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
		case MSG.UPDATE_NICKNAME_BY_ID_NICKNAME_GROUPID:
			if(MyJson.getValue0(jsonstr).equals("true")){
				this.tvNickname.setText(MyJson.getValue2(jsonstr));
			} 
			break;
		}
	}
	
	
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_user_more);
	  
		topTitle = (TopPanelReturnTitleMenu)findViewById(R.id.top_title);
		
		super.initTopPanel(topTitle);	//有toptitle的可以通过父类初始化
		topTitle.setCallback(this);	
		topTitle.setMenu("");
		topTitle.setAlphaMode(true);

		tvUsername = (TextView)findViewById(R.id.tvusername);
		tvId = (TextView)findViewById(R.id.tvid);
		tvEmail = (TextView)findViewById(R.id.tvemail);
		tvNickname = (TextView)findViewById(R.id.tvnickname);
		bDelete = (Button)findViewById(R.id.bdelete);
		
		bDelete.setOnClickListener(this); 
		findViewById(R.id.viewnickname).setOnClickListener(this);
	//	findViewById(R.id.viewgroup).setOnClickListener(this);
			
		
		setByMap( AndroidTools.getMapFromIntent(this.getIntent()));	//设置从上级传来的用户数据
		
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
	//	case R.id.viewgroup:	//修改分组
	//		break;
		case R.id.viewnickname:	//修改昵称
			Intent intent  = null; 
			intent = new Intent( this, UserNicknameAc.class);
			intent.putExtra("nickname", this.tvNickname.getText().toString());
			intent.putExtra("id", this.tvId.getText().toString());
			
			intent.putExtra("menu", "完成");
			intent.putExtra("returntitle", "更多");
			intent.putExtra("title", "修改备注");
			//intent.putExtra("mode", "alpha"); 
			startActivity(intent);
			
			break;
		case R.id.bdelete:	//删除好友
			 MSGSender.deleteReleationship(this, "user", tvId.getText().toString());
			break;
		
		}
	}

}
