package com.cc;

import interfac.CallInt;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import net.MSGTYPE;
import net.MSGSender;

import util.AndroidTools;
import util.JsonMsg;
import util.MapListUtil;
import util.MyImage;
import util.Tools;
import util.picasso.NetImage;
import util.view.TopPanelReturnTitleMenu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
			if(MapListUtil.getMap(map,"ID").toString().equals(Constant.id)){
				topTitle.setMenu("编辑");
			}else{
				topTitle.setMenu("更多");
			}
			
			if( MapListUtil.getMap(map,"NAME").toString().equals(MapListUtil.getMap(map,"USERNAME").toString())){
				tvUsername.setText(MapListUtil.getMap(map,"NAME").toString());
			}else{
				tvUsername.setText(MapListUtil.getMap(map,"NAME").toString()+"（"+MapListUtil.getMap(map,"USERNAME").toString()+"）");
			} 
			
			tvSign.setText(MapListUtil.getMap(map,"SIGN").toString());
			tvId.setText(MapListUtil.getMap(map,"ID").toString());
			tvEmail.setText(MapListUtil.getMap(map,"EMAIL").toString());
			NetImage.loadProfile(this, MapListUtil.getMap(map,"PROFILEPATH").toString(), ivProfile);
			NetImage.loadProfileWall(this, MapListUtil.getMap(map,"PROFILEPATHWALL").toString(), ivProfileWall);
			NetImage.loadImage(this, MapListUtil.getMap(map,"SEX").toString().equals("男")?R.drawable.boy:R.drawable.girl, ivSex);
			if(MapListUtil.getMap(map,"IFADD").equals("true")){
				bSendOrAdd.setText("发消息");
				bSendOrAdd.setBackgroundResource(R.drawable.selector_button_login);
				bSendOrAdd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						//点击发消息，跳转到聊天界面等待消息，传递相关数据给server，获取与目标用户的聊天记录10条/分页查询 并在server中插入/更新会话列表
						//type <user,group>,toid id,username,profilepath,nickname,name,   msg,time,status <在线,离线>
						//添加会话列表
						int c = MapListUtil.getCountListByName(MainMsgAc.listSessions, "ID", MapListUtil.getMap(map,"ID").toString());
						if(c < 0){
							Map<String, Object> session = new HashMap<String, Object>();
							session.put("ID", MapListUtil.getMap(map,"ID").toString());
							session.put("USERNAME", MapListUtil.getMap(map,"USERNAME").toString());
							session.put("PROFILEPATH", MapListUtil.getMap(map,"PROFILEPATH").toString());
							session.put("NICKNAME", MapListUtil.getMap(map,"NICKNAME").toString());
							session.put("NAME", MapListUtil.getMap(map,"NAME").toString());
							session.put("STATUS", MapListUtil.getMap(map,"STATUS").toString());
							session.put("TYPE", MapListUtil.getMap(map,"TYPE").toString());
							session.put("MSG", "");
							session.put("NUM", "0");
							session.put("TIME", "");
							MainMsgAc.listSessions.add(0, session);
							MSGSender.getSessions(getContext());
						}
						c = MapListUtil.getCountListByName(MainContactAc.listItems, "ID", MapListUtil.getMap(map,"ID").toString());
						if(c >= 0){
							Intent intent = new Intent(UserDetailAc.this, ChatAc.class);
							AndroidTools.putMapToIntent(intent, MainContactAc.listItems.get(c));
							startActivity(intent);
						}
						
					}
				});
			}else if(MapListUtil.getMap(map,"IFADD").equals("false")){
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
		if(MapListUtil.getMap(map,"ID").toString().equals(Constant.id)){  //若是自己
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

		if(MapListUtil.getMap(map, "creatorid").equals(Constant.id)  ){//存在群主创建id则表示从 群成员列表过来，并附加了群号groupid，并map中附加了group nickname
			//群主，不能踢自己，自己去更多选项中解散该群
			if(! MapListUtil.getMap(map, "ID").equals(Constant.id)){
				//额外显示踢出选项
				bremove.setVisibility(View.VISIBLE);
				bremove.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						MSGSender.deleteUserFromGroup(UserDetailAc.this, MapListUtil.getMap(map, "ID"), MapListUtil.getMap(map, "groupid"));
						openLoading();
					}
				});
			}
			viewnickname.setVisibility(View.VISIBLE);
			tvnickname.setText(MapListUtil.getMap(map, "GROUPNICKNAME"));
			viewnickname.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					//更改群备注昵称
					Intent intent = new Intent(UserDetailAc.this, UserNicknameAc.class);
					intent.putExtra("nickname", tvnickname.getText());
					intent.putExtra("menu", "更新");
					
					intent.putExtra("id", tvId.getText());
					intent.putExtra("groupid", MapListUtil.getMap(map, "groupid"));
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
		int cmd = JsonMsg.getCmd(jsonstr);
		switch (cmd) { 
		case MSGTYPE.UPDATE_NICKNAME_BY_ID_NICKNAME_GROUPID:
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				Map<String,Object> map = AndroidTools.getMapFromIntent(this.getIntent());

				String newNickname = JsonMsg.getValue2(jsonstr);
				if(!Tools.notNull(JsonMsg.getValue3(jsonstr))){	//没有回传groupid，表示非修改群昵称而是 好友昵称
					this.getIntent().putExtra("NICKNAME", newNickname);	//存入，才能在又修改备注时显示更新
					if(newNickname.equals("")){
						tvUsername.setText(MapListUtil.getMap(map,"USERNAME").toString());
					}else{
						tvUsername.setText(newNickname + "（"+MapListUtil.getMap(map,"USERNAME").toString()+"）");
					}
				}else{//群昵称，修改位置
					tvnickname.setText(newNickname);
				}
			} 
			break;	
		case MSGTYPE.DELETE_RELEATIONSHIP_BY_GROUPID_USERID:
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				finish();
			}
			break;
		case MSGTYPE.DELETE_RELEATIONSHIP_BY_TYPE_ID:
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				finish();
			} 

			break;
		case MSGTYPE.UPDATE_BY_USERNAME_SIGN_SEX_OLDPWD_NEWPWD:
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("true")){
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
		case MSGTYPE.UPDATE_PROFILE_BY_ID_TYPE:
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				toast("修改成功");
				AndroidTools.log("修改图片"+ JsonMsg.getValue1(jsonstr)+":"+ JsonMsg.getValue2(jsonstr));
				if(JsonMsg.getValue1(jsonstr).equals("profile")){
					NetImage.clear(this, Constant.profileHttp() + Constant.profilepath);
					Constant.profilepath = JsonMsg.getValue2(jsonstr);
					NetImage.loadProfile(this, Constant.profilepath, ivProfile);
				}else if(JsonMsg.getValue1(jsonstr).equals("profilewall")) {
					NetImage.clear(this, Constant.profileWallHttp() + Constant.profilepathwall);
					Constant.profilepathwall = JsonMsg.getValue2(jsonstr);
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
			AndroidTools.log("操作取消");
			return;
		}
        if (requestCode == ACTIVITY_RESULT_PROFILE  ) {               
        	 //data中自带有返回的uri  
             Uri uri = data.getData();
             if(uri == null){
     			AndroidTools.log("uri == null ?");
    			return;
    		}
             makePhoto =  Constant.dirProfile + Constant.id + ".png";
             AndroidTools.cutPhoto(this, uri,makePhoto, ACTIVITY_RESULT_CUT);
             
        } else if (requestCode == ACTIVITY_RESULT_PROFILEWALL  ) {             
        	 Uri uri = data.getData(); 
        	 if(uri == null){
     			AndroidTools.log("uri == null ?");
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
		if(Tools.notNull(str)){
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
