package com.cc;

import interfac.CallInt;

import java.util.HashMap;
import java.util.Map;

import net.MSGTYPE;
import net.MSGSender;
import util.AndroidTools;
import util.JsonMsg;
import util.JsonUtil;
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
public class GroupDetailAc extends BaseAc implements OnClickListener, CallInt {

	private static final int ACTIVITY_RESULT_PROFILE = 0;
	private static final int ACTIVITY_RESULT_PROFILE_CUT = 1;
	TopPanelReturnTitleMenu topTitle;
	TextView tvUsername, tvSign, tvId, tvNum;
	ImageView   ivProfileWall;
	Button bSendOrAdd;
	View viewgroupusers;	//群成员
	View viewchathistory;	//聊天记录
	
	public void setByMap(final Map<String, Object> map){
		if(map != null){
			if(MapListUtil.getMap(map, "CREATORID" ).equals(Constant.id)){
				this.topTitle.setMenu( "编辑"); 
			}else{
				this.topTitle.setMenu( "更多"); 
			}
			tvUsername.setText(MapListUtil.getMap(map,"USERNAME").toString());
			tvSign.setText(MapListUtil.getMap(map,"SIGN").toString());
			tvId.setText(MapListUtil.getMap(map,"ID").toString());
			tvNum.setText(MapListUtil.getMap(map,"NOWNUM")+"/"+MapListUtil.getMap(map,"NUM").toString());
			 NetImage.loadProfileWallGroup(this, MapListUtil.getMap(map,"PROFILEPATH").toString(), ivProfileWall);
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
						}
						c = MapListUtil.getCountListByName(MainContactAc.listItems, "ID", MapListUtil.getMap(map,"ID").toString());
						if(c >= 0){
							Intent intent = new Intent(GroupDetailAc.this, ChatAc.class);
							AndroidTools.putMapToIntent(intent, MainContactAc.listItems.get(c));
							startActivity(intent);
						}
						
					}
				});
		
			}else if(MapListUtil.getMap(map,"IFADD").equals("false")){
				bSendOrAdd.setText("申请加群");
				bSendOrAdd.setBackgroundResource(R.drawable.selector_ll_qwhite_press);
				bSendOrAdd.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(GroupDetailAc.this, AddSendAc.class);
						intent = AndroidTools.putMapToIntent(intent, map);
						intent.putExtra("returntitle", "取消");
						intent.putExtra("title", "申请加群");
						intent.putExtra("menu", "发送");
						//intent.putExtra("mode", "alpha"); 
						startActivity(intent);
					}
				});
				this.topTitle.setMenu("");
		
			}else {
				bSendOrAdd.setVisibility(View.GONE);
			}
			
			if(this.topTitle.getMenuText().equals("编辑") ||this.topTitle.getMenuText().equals("更多")){
				viewchathistory.setVisibility(View.VISIBLE);
				viewchathistory.setClickable(true);
				viewgroupusers.setClickable(true);
			}else{
				viewchathistory.setVisibility(View.GONE);
				viewchathistory.setClickable(false);
				viewgroupusers.setClickable(false);
			}
		}
	}
	@Override
	public void callback(String jsonstr) {
		int cmd = JsonMsg.getCmd(jsonstr);
		switch (cmd) { 
		case MSGTYPE.GET_USER_GROUP_DETAIL_BY_TYPE_ID:
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("false")){
				toast("查询失败"); //查找失败
			}else{
			//	setByMap(JsonUtil.getMap(jsonstr));
			} 
			break;
		case MSGTYPE.UPDATE_PROFILE_BY_ID_TYPE:
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				toast("更新头像成功");
				NetImage.clear(this, Constant.profileHttp()+getIntent().getExtras().getString("PROFILEPATH"));
				NetImage.loadProfileWallGroup(this, JsonMsg.getValue2(jsonstr), ivProfileWall);
				String path = JsonMsg.getValue2(jsonstr);
				int i = MapListUtil.getCountListByName(MainContactAc.listItems, "ID", tvId.getText().toString());
				if(i >= 0){
					MainContactAc.listItems.get(i).put("PROFILEPATH", path);
					this.getIntent().putExtra("PROFILEPATH", path);
				}
				
			}else{
				toast(JsonMsg.getValue0(jsonstr));
			}
			break;
		case MSGTYPE.UPDATE_GROUP_BY_ID_NAME_SIGN_NUM_CHECK:
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				//1:id,2:name,sign,num,check
				tvNum.setText(JsonMsg.getValueI(jsonstr, 4));
				tvUsername.setText(JsonMsg.getValueI(jsonstr, 2));
				tvSign.setText(JsonMsg.getValueI(jsonstr, 3));
			}
			break;
		case MSGTYPE.DELETE_RELEATIONSHIP_BY_TYPE_ID:
			this.closeLoading();
			if(JsonMsg.getValue0(jsonstr).equals("true")){
				//toast("删除成功");
				this.finish();
			}else{
				//toast("删除失败：" + JsonUtil.getValue0(jsonstr));
			}
			break;
		}
	}
	
	
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ac_group_detail);
	  
		topTitle = (TopPanelReturnTitleMenu)findViewById(R.id.top_title);
		bSendOrAdd = (Button)findViewById(R.id.bsendoradd);
		tvUsername = (TextView)findViewById(R.id.tvusername);
		tvSign = (TextView)findViewById(R.id.tvsign);
		tvId = (TextView)findViewById(R.id.tvid);
		tvNum = (TextView)findViewById(R.id.tvnum);
		ivProfileWall = (ImageView)findViewById(R.id.ivprofilewall);
		viewgroupusers = (View)findViewById(R.id.viewgroupusers);
		viewgroupusers.setOnClickListener(this);
		viewchathistory = (View)findViewById(R.id.viewchathistory);
		viewchathistory.setOnClickListener(this);

		topTitle.setCallback(this);
		super.initTopPanel(topTitle);
		
		setByMap( AndroidTools.getMapFromIntent(this.getIntent()));	//设置从上级传来的用户数据
		 
		if(topTitle.getMenuText().equals("编辑")){
			ivProfileWall.setClickable(true);
			ivProfileWall.setOnClickListener(this);
		}else{
			ivProfileWall.setClickable(false);
		}
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
		Intent intent;
		
		switch(id){
		case R.id.tvreturn:
			this.topTitle.setAlphaMode(true);	//回退设置的透明top panel

			this.finish();
			break;
		case R.id.tvtitle:
			break;
		case R.id.tvmenu:
			if(topTitle.getMenuText().equals("编辑")){
				//群主，群信息选项，删除群，修改名称
				intent = new Intent( this, GroupMoreAc.class);
				intent.putExtra("menu", "更新");
				AndroidTools.putMapToIntent(intent, AndroidTools.getMapFromIntent(this.getIntent()));
				intent.putExtra("returntitle", "取消");
				intent.putExtra("title", "编辑群信息");
				//intent.putExtra("mode", "alpha"); 
				startActivity(intent);
				
			}else if(topTitle.getMenuText().equals("更多")){
				//退出该群
				intent = new Intent( this, GroupMoreAc.class);
				AndroidTools.putMapToIntent(intent, AndroidTools.getMapFromIntent(this.getIntent()));
				intent.putExtra("returntitle", "返回");
				intent.putExtra("title", "更多");
				intent.putExtra("menu", "");
				//intent.putExtra("mode", "alpha"); 
				startActivity(intent);
				  
			} 
			break;
		case R.id.tvok: 
			break;
		case R.id.viewgroupusers://群成员，群主修改所有成员群昵称，剔除群成员，普通成员修改自己群昵称
			//跳转到成员列表ac，再请求列表
			intent = new Intent( this, GroupUserListAc.class);
			AndroidTools.putMapToIntent(intent, AndroidTools.getMapFromIntent(this.getIntent()));
			intent.putExtra("title", this.getIntent().getExtras().getString("ID") + "-群成员列表");
			intent.putExtra("menu", "");
			intent.putExtra("mode", "");
			startActivity(intent);
			break;
		case R.id.viewchathistory://聊天记录 
			//跳转到聊天记录 ac，再请求聊天记录 
			intent = new Intent( this, ChatHistoryAc.class);
			AndroidTools.putMapToIntent(intent, AndroidTools.getMapFromIntent(this.getIntent()));
			intent.putExtra("title", this.getIntent().getExtras().getString("USERNAME") + "-聊天记录");
			intent.putExtra("menu", "");
			intent.putExtra("mode", "");
			
			intent.putExtra("ID", this.getIntent().getExtras().getString("ID"));
			intent.putExtra("TYPE", "group");

			startActivity(intent);
			break;
		case R.id.ivprofilewall://更新群头像
			if(topTitle.getMenuText().equals("编辑")){
				 intent = new Intent(Intent.ACTION_GET_CONTENT);  
	             intent.addCategory(Intent.CATEGORY_OPENABLE);  
	             intent.setType("image/*");  
	             startActivityForResult(Intent.createChooser(intent, "选择头像"), ACTIVITY_RESULT_PROFILE);   
			
			}
			break;
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
        	 Uri uri = data.getData(); 
        	 if(uri == null){
				 AndroidTools.log("uri == null ?");
     			return;
     		}
             makePhoto = Constant.dirProfile + tvId.getText() + ".png";
             AndroidTools.cutPhoto(this, uri, makePhoto,ACTIVITY_RESULT_PROFILE_CUT, 1000, 650);
        } 
        else if (requestCode == ACTIVITY_RESULT_PROFILE_CUT  ) {             
//	         Bundle bundle = data.getExtras();
//	         if (bundle != null) {
//	             Bitmap bitmap = bundle.getParcelable("data");
//	             String file = Constant.dirProfileWall + tvId.getText() + ".png";
//	             MyImage.savePNG_After(bitmap, file);
//	             choseProfile(file);
//	         } 
            choseProfile(makePhoto);

       } 
	}  
	public void choseProfile(String path){
	    String filename =  tvId.getText() + "." + Tools.getFileTypeByLocalPath(path);//重命名图片名字保留后缀
        MyImage.savePNG(path);	//转码压缩png
        //fromid,toid,type,time,msg
        AndroidTools.uploadProfile(this, filename, path);
       //上传文件到服务器，并复制到指定目录, 头像的话已经在裁剪的时候存入了目标文件夹
       //  MyFile.copyFile(path, Constant.dirProfile + filename);
         MSGSender.updateProfile(this, tvId.getText().toString(), "profile");
        this.openLoading();
		
	}
}
