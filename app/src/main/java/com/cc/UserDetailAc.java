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
import util.tools.MyFile;
import util.tools.MyImage;
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
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.squareup.picasso.Picasso;

/**
 * @author Walker
 * @date 2017-3-4 下午9:09:34
 * Description: 用户查看详细界面,添加好友或者发消息
 */
public class UserDetailAc extends BaseAc implements OnClickListener, CallInt {

	protected static final int ACTIVITY_RESULT_PROFILEWALL = 0;
	protected static final int ACTIVITY_RESULT_PROFILE = 1;
	protected static final int ACTIVITY_RESULT_CUT = 2;
	protected static final int ACTIVITY_RESULT_CUT_WALL = 3;
	TopPanelReturnTitleMenu topTitle;
	TextView tvUsername, tvSign, tvId, tvEmail;
	ImageView ivSex, ivProfile, ivProfileWall;
	Button bSendOrAdd;
	Button bremove;	//踢出群组
	View viewnickname;	//备注修改
	TextView tvnickname;
	View viewchathistory;	//聊天记录

	public void setByMap(final Map<String, Object> map){
		if(map != null){
			if(map.get("ID").toString().equals(Constant.id)){
				topTitle.setMenu("编辑");
			}else{
				topTitle.setMenu("更多");
			}
			
			if( Tools.getMap(map,"NAME").toString().equals(Tools.getMap(map,"USERNAME").toString())){
				tvUsername.setText(Tools.getMap(map,"NAME").toString());
			}else{
				tvUsername.setText(Tools.getMap(map,"NAME").toString()+"（"+Tools.getMap(map,"USERNAME").toString()+"）");
			} 
			
			tvSign.setText(Tools.getMap(map,"SIGN").toString());
			tvId.setText(Tools.getMap(map,"ID").toString());
			tvEmail.setText(Tools.getMap(map,"EMAIL").toString());
			NetImage.loadProfile(this, Tools.getMap(map,"PROFILEPATH").toString(), ivProfile);
			NetImage.loadProfileWall(this, Tools.getMap(map,"PROFILEPATHWALL").toString(), ivProfileWall);
			NetImage.loadImage(this, Tools.getMap(map,"SEX").toString().equals("男")?R.drawable.boy:R.drawable.girl, ivSex);
			if(Tools.getMap(map,"IFADD").equals("true")){
				bSendOrAdd.setText("发消息");
				bSendOrAdd.setBackgroundResource(R.drawable.selector_button_login);
				bSendOrAdd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						//点击发消息，跳转到聊天界面等待消息，传递相关数据给server，获取与目标用户的聊天记录10条/分页查询 并在server中插入/更新会话列表
						//type <user,group>,toid id,username,profilepath,nickname,name,   msg,time,status <在线,离线>
						//添加会话列表
						int c = Tools.getCountListByName(MainMsgAc.listSessions, "ID", Tools.getMap(map,"ID").toString());
						if(c < 0){
							Map<String, Object> session = new HashMap<String, Object>();
							session.put("ID", Tools.getMap(map,"ID").toString());
							session.put("USERNAME", Tools.getMap(map,"USERNAME").toString());
							session.put("PROFILEPATH", Tools.getMap(map,"PROFILEPATH").toString());
							session.put("NICKNAME", Tools.getMap(map,"NICKNAME").toString());
							session.put("NAME", Tools.getMap(map,"NAME").toString());
							session.put("STATUS", Tools.getMap(map,"STATUS").toString());
							session.put("TYPE", Tools.getMap(map,"TYPE").toString());
							session.put("MSG", "");
							session.put("NUM", "0");
							session.put("TIME", "");
							MainMsgAc.listSessions.add(0, session);
						}
						c = Tools.getCountListByName(MainContactAc.listItems, "ID", Tools.getMap(map,"ID").toString());
						if(c >= 0){
							Intent intent = new Intent(UserDetailAc.this, ChatAc.class);
							AndroidTools.putMapToIntent(intent, MainContactAc.listItems.get(c));
							startActivity(intent);
						}
						
					}
				});
			}else if(Tools.getMap(map,"IFADD").equals("false")){
				bSendOrAdd.setText("加好友");
				bSendOrAdd.setBackgroundResource(R.drawable.selector_ll_qwhite_press);
				bSendOrAdd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(UserDetailAc.this, AddSendAc.class);
						intent = AndroidTools.putMapToIntent(intent, map);
						intent.putExtra("returntitle", "取消");
						intent.putExtra("title", "添加好友");
						intent.putExtra("menu", "发送");
						//intent.putExtra("mode", "alpha"); 
						startActivity(intent);
					}
				});
				this.topTitle.setMenu("");
			}else 			//自己信息界面
				bSendOrAdd.setVisibility(View.GONE);
		 
		}
		if(Tools.getMap(map,"ID").toString().equals(Constant.id)){  //若是自己
			ivProfile.setClickable(true);
			ivProfileWall.setClickable(true);
			ivProfile.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {//选择头像图片， 并裁剪
					 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
		             intent.addCategory(Intent.CATEGORY_OPENABLE);  
		             intent.setType("image/*");  
		             startActivityForResult(Intent.createChooser(intent, "选择头像"), ACTIVITY_RESULT_PROFILE);   
				}
			});
			ivProfileWall.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {//选择背景图片
					 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
		             intent.addCategory(Intent.CATEGORY_OPENABLE);  
		             intent.setType("image/*");  
		             startActivityForResult(Intent.createChooser(intent, "选择背景墙"), ACTIVITY_RESULT_PROFILEWALL);   
				}
			});
			
		}else{
			ivProfile.setClickable(false);
			ivProfileWall.setClickable(false);
		}
		
//		intent.putExtra("creatorid", "" + this.getIntent().getExtras().getString("CREATORID"));//加入群主id
//		intent.putExtra("groupid", "" + this.getIntent().getExtras().getString("ID"));//加入群主id

		if(Tools.getMap(map, "creatorid").equals(Constant.id)  ){//存在群主创建id则表示从 群成员列表过来，并附加了群号groupid，并map中附加了group nickname
			//群主，不能踢自己，自己去更多选项中解散该群
			if(! Tools.getMap(map, "ID").equals(Constant.id)){
				//额外显示踢出选项
				bremove.setVisibility(View.VISIBLE);
				bremove.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						MSGSender.deleteUserFromGroup(UserDetailAc.this, Tools.getMap(map, "ID"), Tools.getMap(map, "groupid"));
						openLoading();
					}
				});
			}
			viewnickname.setVisibility(View.VISIBLE);
			tvnickname.setText(Tools.getMap(map, "GROUPNICKNAME"));
			viewnickname.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//更改群备注昵称
					Intent intent = new Intent(UserDetailAc.this, UserNicknameAc.class);
					intent.putExtra("nickname", tvnickname.getText());
					intent.putExtra("menu", "更新");
					
					intent.putExtra("id", tvId.getText());
					intent.putExtra("groupid", Tools.getMap(map, "groupid"));
					startActivity(intent);
				}
			});
		}else{
			bremove.setVisibility(View.GONE);
			viewnickname.setVisibility(View.GONE);
		}
		
		
		
		if(this.topTitle.getMenuText().equals("更多")){
			viewchathistory.setVisibility(View.VISIBLE);
			viewchathistory.setClickable(true);
		}else{
			viewchathistory.setVisibility(View.GONE);
			viewchathistory.setClickable(false);
		}
		
	}
	
	@Override
	public void callback(String jsonstr) { 
		int cmd = MyJson.getCmd(jsonstr); 
		switch (cmd) { 
		case MSG.UPDATE_NICKNAME_BY_ID_NICKNAME_GROUPID:
			if(MyJson.getValue0(jsonstr).equals("true")){
				Map<String,Object> map = AndroidTools.getMapFromIntent(this.getIntent());

				String newNickname = MyJson.getValue2(jsonstr);
				if(Tools.testNull(MyJson.getValue3(jsonstr))){	//没有回传groupid，表示非修改群昵称而是 好友昵称
					this.getIntent().putExtra("NICKNAME", newNickname);	//存入，才能在又修改备注时显示更新
					if(newNickname.equals("")){
						tvUsername.setText(Tools.getMap(map,"USERNAME").toString());
					}else{
						tvUsername.setText(newNickname + "（"+Tools.getMap(map,"USERNAME").toString()+"）");
					}
				}else{//群昵称，修改位置
					tvnickname.setText(newNickname);
				}
			} 
			break;	
		case MSG.DELETE_RELEATIONSHIP_BY_GROUPID_USERID:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				finish();
			}
			break;
		case MSG.DELETE_RELEATIONSHIP_BY_TYPE_ID:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				finish();
			} 

			break;
		case MSG.UPDATE_BY_USERNAME_SIGN_SEX_OLDPWD_NEWPWD:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				try{
					JSONObject jb = new JSONObject(jsonstr);
 				    int i = 1;
	 				tvUsername.setText(jb.getString("value"+i++)); //保存用户名密码，头像
	 				tvSign.setText(jb.getString("value"+i++));
	 				NetImage.loadImage(this, jb.getString("value"+i++).equals("男")?R.drawable.boy:R.drawable.girl, ivSex);
				}catch (Exception e) {
				}
			}
			break;
		case MSG.UPDATE_PROFILE_BY_ID_TYPE:
			this.closeLoading();
			if(MyJson.getValue0(jsonstr).equals("true")){
				toast("修改成功");
				Tools.log("修改图片"+MyJson.getValue1(jsonstr)+":"+MyJson.getValue2(jsonstr));
				if(MyJson.getValue1(jsonstr).equals("profile")){
					NetImage.clear(this, Constant.profileHttp() + Constant.profilepath);
					Constant.profilepath = MyJson.getValue2(jsonstr);
					NetImage.loadProfile(this, Constant.profilepath, ivProfile);
				}else if(MyJson.getValue1(jsonstr).equals("profilewall")) {
					NetImage.clear(this, Constant.profileWallHttp() + Constant.profilepathwall);
					Constant.profilepathwall = MyJson.getValue2(jsonstr);
					NetImage.loadProfileWall(this, Constant.profilepathwall, ivProfileWall);
				}else{
					
				}
			}
			break;
			
			default:
				
		}
		
		
		
	}
	
	String makePhoto = "";
	//相册、相机、文件 选择结果
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if(resultCode != RESULT_OK || data == null){
			Tools.log("操作取消");
			return;
		}
        if (requestCode == ACTIVITY_RESULT_PROFILE  ) {               
        	 //data中自带有返回的uri  
             Uri uri = data.getData();
             if(uri == null){
     			Tools.log("uri == null ?");
    			return;
    		}
             makePhoto =  Constant.dirProfile + Constant.id + ".png";
             AndroidTools.cutPhoto(this, uri,makePhoto, ACTIVITY_RESULT_CUT);
             
        } else if (requestCode == ACTIVITY_RESULT_PROFILEWALL  ) {             
        	 Uri uri = data.getData(); 
        	 if(uri == null){
     			Tools.log("uri == null ?");
     			return;
     		}
        	  makePhoto =   Constant.dirProfileWall + Constant.id + ".png";
             AndroidTools.cutPhoto(this, uri,makePhoto, ACTIVITY_RESULT_CUT_WALL, 1000, 618);
        } 
        else if (requestCode == ACTIVITY_RESULT_CUT  ) {             
//	         Bundle bundle = data.getExtras();
//	         if (bundle != null) {
//	             Bitmap bitmap = bundle.getParcelable("data");
//	             String file = Constant.dirProfile + Constant.id + ".png";
//	             MyImage.savePNG_After(bitmap, file);
//	             choseProfile(file);
//	         } 
        	choseProfile(makePhoto);
       } 
        else if (requestCode == ACTIVITY_RESULT_CUT_WALL  ) {             
//	         Bundle bundle = data.getExtras();
//	         if (bundle != null) {
//	             Bitmap bitmap = bundle.getParcelable("data");
//	             String file = Constant.dirProfileWall + Constant.id + ".png";
//	             MyImage.savePNG_After(bitmap, file);
//	             choseProfileWall(file);
//	         } 
        	choseProfileWall(makePhoto);

      } 
	}  
	public void choseProfile(String path){
	    String filename = Constant.id + "." + Tools.getFileTypeByLocalPath(path);//重命名图片名字保留后缀
        //fromid,toid,type,time,msg
        MyImage.savePNG(path);	//转码压缩png
        AndroidTools.uploadProfile(this, filename, path);
       //上传文件到服务器，并复制到指定目录, 头像的话已经在裁剪的时候存入了目标文件夹
       //  MyFile.copyFile(path, Constant.dirProfile + filename);
        MSGSender.updateProfile(this, Constant.id, "profile");
        this.openLoading();
		
	}
	public void choseProfileWall(String path){
	     String filename = Constant.id  + "." +  Tools.getFileTypeByLocalPath(path);//重命名图片名字保留后缀
	        MyImage.savePNG(path);	//转码压缩png
	        //fromid,toid,type,time,msg
         AndroidTools.uploadProfileWall(this, filename, path);
        //上传文件到服务器，并复制到指定目录
         //MyFile.copyFile(path, Constant.dirProfileWall + filename);
         MSGSender.updateProfile(this, Constant.id, "profilewall");
         this.openLoading();
	}
	
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_user_detail);
	  
		topTitle = (TopPanelReturnTitleMenu)findViewById(R.id.top_title);
		bSendOrAdd = (Button)findViewById(R.id.bsendoradd);
		bremove = (Button)findViewById(R.id.bremove);
		viewnickname = (View)findViewById(R.id.viewnickname);
		tvnickname = (TextView)findViewById(R.id.tvnickname);

		tvUsername = (TextView)findViewById(R.id.tvusername);
		tvSign = (TextView)findViewById(R.id.tvsign);
		tvId = (TextView)findViewById(R.id.tvid);
		tvEmail = (TextView)findViewById(R.id.tvemail);
		ivSex = (ImageView)findViewById(R.id.ivsex);
		ivProfileWall = (ImageView)findViewById(R.id.ivprofilewall);
		ivProfile = (ImageView)findViewById(R.id.ivprofile);
		topTitle.setCallback(this);	
		viewchathistory = (View)findViewById(R.id.viewchathistory);
		viewchathistory.setOnClickListener(this);

		
		
		super.initTopPanel(topTitle);	//有toptitle的可以通过父类初始化
		setByMap( AndroidTools.getMapFromIntent(this.getIntent()));	//设置从上级传来的用户数据
		
	
		 
	} 
	
	
	@Override
	public void OnResume() {
		String str = this.getIntent().getExtras().getString("mode");
		if(!Tools.testNull(str)){
			topTitle.setAlphaMode(false);
		}
	}
	@Override
	public void OnPause() {
		topTitle.setAlphaMode(true);
	}
	@Override
	public void onClick(View v) {
		call(v.getId());
	}
	 
	@Override
	public boolean OnBackPressed() {
		this.topTitle.setAlphaMode(true);	//回退设置的透明top panel

		return false;	//false关闭this ac 
	}
	
	@Override
	public void OnDestroy() {
		super.OnDestroy();
	}
	@Override
	public void call(int id) {
		Intent intent  = null; 
		
		switch(id){
		case R.id.tvreturn:
			this.topTitle.setAlphaMode(true);	//回退设置的透明top panel

			this.finish();
			break;
		case R.id.tvtitle:
			break;
		case R.id.tvmenu:
			if(topTitle.getMenuText().equals("编辑")){
				//个人信息编辑界面 修改 昵称，性别，密码，签名
				
				intent = new Intent( this, UserUpdateAc.class);
				intent.putExtra("menu", "");
				AndroidTools.putMapToIntent(intent, AndroidTools.getMapFromIntent(this.getIntent()));
				intent.putExtra("returntitle", "取消");
				intent.putExtra("title", "修改个人信息");
				//intent.putExtra("mode", "alpha"); 
				startActivity(intent);
				
			}else if(topTitle.getMenuText().equals("更多")){
				//朋友信息选项，删除好友，更改备注
				intent = new Intent( this, UserMoreAc.class);
				intent.putExtra("menu", "");
				AndroidTools.putMapToIntent(intent, AndroidTools.getMapFromIntent(this.getIntent()));
				intent.putExtra("returntitle", "返回");
				intent.putExtra("title", "更多");
				//intent.putExtra("mode", "alpha"); 
				startActivity(intent);
			}
			break;
		case R.id.viewchathistory://聊天记录 
			//跳转到聊天记录 ac，再请求聊天记录 
			intent = new Intent( this, ChatHistoryAc.class);
			AndroidTools.putMapToIntent(intent, AndroidTools.getMapFromIntent(this.getIntent()));
			intent.putExtra("title", this.getIntent().getExtras().getString("USERNAME") + "-聊天记录");
			intent.putExtra("menu", "");
			intent.putExtra("mode", "");
			
			intent.putExtra("ID", this.getIntent().getExtras().getString("ID"));
			intent.putExtra("TYPE", "user");

			
			startActivity(intent);
			break;
		
		}
	}
	 
}
