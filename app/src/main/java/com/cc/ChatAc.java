package com.cc;

import fragm.FragmentEmoji;
import fragm.FragmentMore;
import fragm.FragmentPhoto;
import fragm.FragmentVoice;
import interfac.CallInt;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.MSGSender;
import net.MSGTYPE;

import okhttp3.Call;
import util.AndroidTools;
import util.JsonMsg;
import util.JsonUtil;
import util.MapListUtil;
import util.picasso.NetImage;
import util.EmotionKeyboard;
import util.MyFile;
import util.MyImage;
import util.MyMediaPlayer;
import util.RobotTuling;
import util.Tools;
import util.view.ChatView;
import util.view.ChatView.OnControl;
import util.view.DialogImageShow;
import util.view.VoiceListener.OnVoice;
import adapter.AdapterLvChat;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONML;


/**
 * @author Walker
 * @date 2017-3-8 下午1:56:24
 * Description: 聊天会话界面 main 核心！！
 */
public class ChatAc extends BaseAc implements CallInt, View.OnClickListener{
 
	@Override
	public void callback(String jsonstr) {
		Map map = JsonUtil.getMap(jsonstr);
		int cmd = MapListUtil.getMap(map, "cmd", 0);
		String value = MapListUtil.getMap(map, "value0", "false");
		if(value.equals("false")){
			toast("异常:" + MapListUtil.getMap(map, "value1"));
			return;
		}
		List list;
		switch (cmd) {
		case MSGTYPE.GET_USER_GROUP_CHAT_BY_TYPE_ID_START://查询消息记录 服务器决定一次多x<10>条消息
            swipeRefreshLayout.setRefreshing(false);
			list = MapListUtil.getMap(map, "value1", new ArrayList());
			//需要适配好友 私聊，群聊，非好友，为了复用代码，多传输部分冗余数据
			//本地添加会话记录
			//适配器需要username,time,msg,type,profilepath
			//消息记录fromid,toid,type,time,msg
			//会话列表type <user,group>,toid id,username,profilepath,nickname,name,   msg,time,status <在线,离线>
			listChatMsg.addAll(0, list);
			downLoadFile(list);    //下载需要用的文件####################
			adapterLvChat.notifyDataSetChanged();
			lvChat.setSelection(list.size());    //选中刷新出来的数据的最新一条

			updateSession(this.toid);
			break;
		case MSGTYPE.SEND_CHATMSG_BY_GTYPE_TOID_TYPE_TIME_MSG://在线时收到一些条消息，判定，是此会话的fromid和toid才显示在当前界面
			//AndroidTools.systemVoiceToast(this);//已经在外层提示过，此处只做限制显示

			list = MapListUtil.getMap(map, "value1", new ArrayList());
			//Tools.out("当前selfid:"+Constant.id + " toid:" + this.toid);
			//Tools.out(Tools.list2string(list));
			//我和目标用户之间的对话消息，此处一条消息，只能是别人发给我，或者别人发给群
				if(this.type.equals("user")){
					if( (MapListUtil.getList(list, 0, "FROMID").equals(this.toid) && MapListUtil.getList(list, 0, "TOID").equals(Constant.id)) ){
						//目标用户发给我，
						if(listChatMsg.size() >= Constant.maxChatNum){
		            		//toast("更多的消息在聊天记录中查看");
							for(int i = 0; i < list.size() && list.size()>0; i++)
								listChatMsg.remove(0);
		            	}
						listChatMsg.addAll(listChatMsg.size(), list);
						downLoadFile(list);	//下载需要用的文件####################
			
						adapterLvChat.notifyDataSetChanged();
					    lvChat.setSelection(listChatMsg.size());	//选中最新一条，滚动到底部
			          //  updateSession(this.toid);
					   // Tools.out("当前用户user消息");
					}
				}else{//任何用户发给我加入的正在聊天的群
					if( ( MapListUtil.getList(list, 0, "TOID").equals(this.toid)) ){
						listChatMsg.addAll(listChatMsg.size(), list);
						downLoadFile(list);	//下载需要用的文件####################
			
						adapterLvChat.notifyDataSetChanged();
					    lvChat.setSelection(listChatMsg.size());	//选中最新一条，滚动到底部
			            //updateSession(this.toid);
					 //   Tools.out("当前群group消息");
					}
				}
			
		break;
		case MSGTYPE.UPDATE_NICKNAME_BY_ID_NICKNAME_GROUPID:

			if(JsonMsg.getValue0(jsonstr).equals("true")){
				String newId = JsonMsg.getValue1(jsonstr);
				String newNickname = JsonMsg.getValue2(jsonstr);
				if(toid.equals(newId)){
					tvTitleOne.setText(newNickname);
				}
			} 
			break;		
		}
		
		
	}

	
	//布局绑定view
	View viewContent;
	ListView lvChat;
	//username,time,text,profilepath
	AdapterLvChat adapterLvChat;
	//fromid toid msg type time
	List<Map<String, Object>> listChatMsg; 	//聊天记录条数
	
	 
	TextView tvReturn,tvTitleOne,tvTitleTwo, tvSend;
	ImageView ivMenu;
	EditText etSend;
	String toid;	//聊天对方id
	String type;	//类型 群，人
	SwipeRefreshLayout swipeRefreshLayout;//下拉控件

	LinearLayout viewFill;	//填充隐藏布局
	ChatView cvChat;		//菜单栏
	
    //语音，图片，相机，更多选择
	FragmentEmoji fEmoji;
	FragmentVoice fVoice;
	FragmentPhoto fPhoto;
	FragmentMore fMore;
 
	MyMediaPlayer media = MyMediaPlayer.getInstance();
	
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ac_chat);
		//View contentView = LayoutInflater.from(this).inflate(R.layout.ac_chat, null);
		// setContentView(contentView);
		tvReturn = (TextView)findViewById(R.id.tvreturn);
		tvTitleOne = (TextView)findViewById(R.id.tvtitleone);
		tvTitleTwo = (TextView)findViewById(R.id.tvtitletwo);
		ivMenu = (ImageView)findViewById(R.id.ivmenu);
		cvChat = (ChatView)findViewById(R.id.cvchat);
		viewFill = (LinearLayout)findViewById(R.id.viewfill);
		tvReturn.setOnClickListener(this);
		ivMenu.setOnClickListener(this);
		//send button edittext
		etSend = (EditText)findViewById(R.id.etsend);
		tvSend = (TextView)findViewById(R.id.tvsend);
		tvSend.setOnClickListener(this);
		//listview
		lvChat = (ListView)findViewById(R.id.lvchat);
		listChatMsg = new ArrayList<Map<String,Object>>();
		adapterLvChat = new AdapterLvChat(this, listChatMsg);
		lvChat.setAdapter(adapterLvChat);
		lvChat.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int scrollState) {
				  switch(scrollState){  
			        case OnScrollListener.SCROLL_STATE_IDLE://空闲状态  
			        	Tools.out("listview 空闲，开始加载图片");
			            NetImage.resume(getContext());
			        	break;
			        case OnScrollListener.SCROLL_STATE_FLING://滚动状态  
			        	Tools.out("listview 滚动，暂停加载图片");
			            NetImage.pause(getContext());
			            break;  
			        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸后滚动  
			                  
			            break;  
				  }
			}
			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				
			}
		});
		adapterLvChat.setOnChose(new CallInt() {
			@Override
			public void call(final int position) {

				Map<String,Object> map = listChatMsg.get(position);
				AndroidTools.log(map.toString());

				//点中某个用户/群组，进入详情界面显示
				String type = MapListUtil.getMap(map, "TYPE").toString();
				if(type.equals("text")){	//文本消息点击,头像点击
					 etSend.setText("@"+MapListUtil.getMap(map, "USERNAME"));
					 etSend.requestFocus();
					 etSend.setSelection(etSend.getText().length()); 
					 ekb.openSoft();
				}else if(type.equals("voice")){	//语音消息点击,播放暂停
					if(MapListUtil.getMap(map, "isplay").equals("true")){//停止播放
						listChatMsg.get(position).put("isplay", "false");
						media.stop();
						AndroidTools.log("isplay true");
					}else{//开始播放
						AndroidTools.log("isplay false");
						//关闭其它正在播放的 动画
						for(int i = 0;i  < listChatMsg.size(); i++){
							if(listChatMsg.get(i).get("TYPE").toString().equals("voice")){
								listChatMsg.get(i).put("isplay", "false");
							}
						}
						listChatMsg.get(position).put("isplay", "true");

						media.play(Constant.dirVoice + MapListUtil.getMap(map, "MSG"),new MyMediaPlayer.OnPlay() {
							@Override
							public void onPlayEnd(MediaPlayer mediaPlayerr) {
								listChatMsg.get(position).put("isplay", "false");
								adapterLvChat.notifyDataSetChanged();
								//media.reset();
							}
						} );
					}
					adapterLvChat.notifyDataSetChanged();
					
				}else if(type.equals("photo")){	//图片消息点击
					//携带参数跳转
					String path = Constant.dirPhoto + MapListUtil.getMap(map, "MSG");
					DialogImageShow dis = new DialogImageShow(ChatAc.this, path);
					dis.show();
					dis.setCancelable(true);
				}else if(type.equals("file")){	//文件消息点击,提示打开
					
					String id_filename = MapListUtil.getMap(map, "MSG");	//101OTJOTOTxxx.doc
					String filename = id_filename.substring(id_filename.split(Constant.split)[0].length() + Constant.split.length());	//xxx.doc
					String path = Constant.dirFile + filename;
					AndroidTools.openFile(ChatAc.this, path);
				} 

				
				
			}
		});
		//设置从上级传来的用户数据
		setByMap( AndroidTools.getMapFromIntent(this.getIntent()));
		
		//服务器添加会话记录
		MSGSender.addChatSession(this, type, toid);
		//获取分页聊天记录,现在时间之前的""表示服务器最新x条，否则按照当前信息最老一条的前面x条
		MSGSender.getChatMsgByTypeIdStarttime(this, type, toid, "");
	 
	    etSend.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { 	}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
			@Override
			public void afterTextChanged(Editable arg0) {
				if(! etSend.getText().toString().equals("")){
					tvSend.setClickable(true);
					tvSend.setBackgroundResource( R.drawable.selector_button_login);
				}else{
					tvSend.setClickable(false);
					tvSend.setBackgroundResource(R.drawable.selector_button_login_disable);
				}
				
			}
		});

	    cvChat.setOnControl(new OnControl() {
			@Override
			public void onOpen(int id) {
				switch (id){
				case R.id.ivvoice:
					showFragment("fVoice"); 
					break;
				case R.id.ivphoto:
					showFragment("fPhoto"); 
					break;
				case R.id.ivgraph:	//关闭输入框，清空选中状态，并拍照，
					takeGraph();
					cvChat.clearId();
					ekb.close();
					break;
				case R.id.ivemoji:
					showFragment("fEmoji"); 
					break;
				case R.id.ivmore:
					showFragment("fMore"); 
					break;
				}
				if(id != R.id.ivgraph){//除了不需要打开输入框的都手动打开输入框
					ekb.open();
				}
			}
			@Override
			public void onClose() {//手动关闭输入框
				ekb.close();
			}
		});

	  
	
		  swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl);
	         //设置刷新时动画的颜色，可以设置4个
	         swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
		     swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
	            @Override
	            public void onRefresh() {
	            	//获取分页聊天记录,现在时间之前的""表示服务器最新x条，否则按照当前信息最老一条的前面x条
	            	if(listChatMsg.size() >= Constant.maxChatNum){
	            		toast("更多的消息在聊天记录中查看");
	                    swipeRefreshLayout.setRefreshing(false);
	            		return;
	            	}
	            	String time = "";
	            	if(listChatMsg.size() > 0){
	            		time = MapListUtil.getList(listChatMsg, 0, "TIME");
	            	}
	        		MSGSender.getChatMsgByTypeIdStarttime(ChatAc.this, type, toid, time);
	            } 
	     });
			
	     ekb = EmotionKeyboard.with(this)
	                .setFillView(viewFill)//绑定表情面板
	                .bindToContent(swipeRefreshLayout)//绑定内容view lvChat -> 下拉布局,需要weight 1来配合ekb解决闪屏
	                .bindToChatView(cvChat)//绑定chatview，当点击文本输入则清空id选中
	                .bindToEditText(etSend )//判断绑定那种EditView
	                .build();
		    
	    initFragment();
	} 
	
	
	EmotionKeyboard ekb;
	
	
	String nowFragment = "";
	//添加所有fragment
	public void initFragment(){
		initVoice();initPhoto();initEmoji();initMore();
		  FragmentManager fragmentManager = getFragmentManager();  
		  fragmentManager.beginTransaction().add(R.id.id_content, fVoice, "fVoice").commit();  
		  fragmentManager.beginTransaction().add(R.id.id_content, fPhoto, "fPhoto").commit();  
		//fragmentManager.beginTransaction().add(R.id.container, fVoice, "fGraph").commit();  
		  fragmentManager.beginTransaction().add(R.id.id_content, fEmoji, "fEmoji").commit();  
		  fragmentManager.beginTransaction().add(R.id.id_content, fMore, "fMore").commit();  
		  fragmentManager.beginTransaction().hide(fVoice).commit();  
		  fragmentManager.beginTransaction().hide(fPhoto).commit();  
		  fragmentManager.beginTransaction().hide(fEmoji).commit();  
		  fragmentManager.beginTransaction().hide(fMore).commit();  

		
	}
	public void showFragment(String name){
		  if (name.equals(nowFragment)){  
	            return;  
	        }  
		  FragmentManager fragmentManager = getFragmentManager();  
		  if( ! nowFragment.equals("")  )
			  fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(nowFragment)).commit();  
		  nowFragment = name;  
		  fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(nowFragment)).commit();  
	}
	public void fragmentGoto(Fragment frag, String name){
		nowFragment = name;
	    FragmentManager fm = getFragmentManager();  
        FragmentTransaction tx = fm.beginTransaction();  
        tx.replace(R.id.id_content, frag, name);  
        tx.addToBackStack(null);  
        tx.commit(); 
	}
	private void initEmoji() {
	   if (fEmoji == null)     {  
		   fEmoji = new FragmentEmoji();  
		   fEmoji.setCall(new FragmentEmoji.Call() {
				@Override
				public void onCall(SpannableString spannableString) {
	                etSend.getText().insert(etSend.getSelectionStart(), spannableString);
				}
		   });  
        }  
       // fragmentGoto( fEmoji, "fEmoji");  
    }
	private void initVoice() {
	    if (fVoice == null)  {  
		   fVoice = new FragmentVoice();  
		   fVoice.setCall( new OnVoice() {
				@Override
				public void onSend(String file) {
					sendVoice(file);
				}
		   });  
        }  
      //  fragmentGoto( fVoice, "fVoice");  
	}
	private void initPhoto() {
	    if (fPhoto == null)  {  
	    	fPhoto = new FragmentPhoto();  
	    	fPhoto.setCall( new FragmentPhoto.Call() {
				@Override
				public void onCall(List<String> files) {
					sendPhotos(files);
					ekb.close();
					cvChat.clearId();
				}
			}); 
        }  
	  //  fragmentGoto( fPhoto, "fPhoto");  
	}
	private void initMore() {
	    if (fMore == null)  {  
	    	fMore = new FragmentMore();  
	    	  fMore.setCall( new FragmentMore.Call() {
	  			@Override
	  			public void onChoseFile(String path) {
	  				sendFile(path);
	  			}
	  			 
	  		}); 
    	  }  
	  
	}

	
	
	// 相机  结果
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
         if (requestCode == ACTIVITY_RESULT_CAMERA && resultCode == Activity.RESULT_OK  ) {   
        	AndroidTools.log("拍照结果"+ChatAc.TAKEPHOTO);
        	List<String> list = new ArrayList<String>();
			list.add(ChatAc.TAKEPHOTO);
			sendPhotos(list);
        } 
        
	}  
	
	static int ACTIVITY_RESULT_PHOTP = 1;
	static int ACTIVITY_RESULT_CAMERA = 2;
	static int ACTIVITY_RESULT_FILE = 3;
	public static String TAKEPHOTO = "";
	public void takeGraph(){
		//设置图片的保存路径,作为全局变量

		ChatAc.TAKEPHOTO =  Constant.dirCamera + Constant.id + "-" + Tools.getCurrentTime()+".png";
		
		AndroidTools.takePhoto(this, ChatAc.TAKEPHOTO, ACTIVITY_RESULT_CAMERA);
		
	} 

	private void setByMap(Map<String, Object> map) {
		if(map != null){
			tvTitleOne.setText(MapListUtil.getMap(map, "NAME").toString());
			tvTitleTwo.setText(MapListUtil.getMap(map, "STATUS").toString().equals("null")?"":MapListUtil.getMap(map, "STATUS").toString());
			toid = MapListUtil.getMap(map, "ID").toString();
			type = MapListUtil.getMap(map, "TYPE").toString();
		}
	} 
	@Override
	public boolean OnBackPressed() {
		if(ekb.isInputOrEmojiShow()){
			cvChat.clearId();
			ekb.close();
			return true;
		}
		return false;	
	}
	//自定义组件点击响应传递id于此处理
	@Override
	public void call(int id) { 
		switch(id){
		case R.id.tvreturn:
			this.finish();
			break;
		case R.id.ivmenu://用户详情
			Intent intent ;
			if(type.equals("user")){
				intent = new Intent(this, UserDetailAc.class);
			}else{
				intent = new Intent(this, GroupDetailAc.class);
			}
			intent.putExtra("returntitle", "返回");
			intent.putExtra("title", " ");
			intent.putExtra("mode", "alpha"); 

			AndroidTools.putMapToIntent(intent, MainContactAc.listItems.get( MapListUtil.getCountListByName(MainContactAc.listItems, "ID", this.toid)));
			intent.putExtra("IFADD", "temp");	//从聊天界面转到详情，不应该显示发消息或者加好友，临时屏蔽按钮的方式

			startActivity(intent);
			break;
		case R.id.tvsend: //点击发送消息
			if(Tools.notNull( etSend.getText().toString())){
				sendText();
			} 
			break;
		}
	}
  //自己发送消息时更新本地会话列表
	public void updateSession(String toid){
		int c = MapListUtil.getCountListByName(MainMsgAc.listSessions, "ID", toid);
		if(c >= 0){
			if(listChatMsg.size() > 0){
				MainMsgAc.listSessions.get(c).put("MSG", MapListUtil.getList(listChatMsg, listChatMsg.size() - 1, "msg"));
				MainMsgAc.listSessions.get(c).put("TYPE", MapListUtil.getList(listChatMsg, listChatMsg.size() - 1, "type"));
				MainMsgAc.listSessions.get(c).put("TIME", MapListUtil.getList(listChatMsg, listChatMsg.size() - 1, "time"));
			}
		}
	} 
	@Override
	public void OnDestroy() {
		MSGSender.getChatMsgByTypeIdStarttime(this, type, toid, "");

		int c = MapListUtil.getCountListByName(MainMsgAc.listSessions, "ID", toid);
		if(c >= 0){
			 MainMsgAc.listSessions.get(c).put("NUM", "0");	//从该聊天返回清空会话列表该会话的未读数量
		}
	}
	//ac里面表层的标签监听
	@Override
	public void onClick(View view) {
		call(view.getId());
	}

	
	public void sendPhotos(final List<String> list){
		if(list == null ) return;

		AndroidTools.thread(new Runnable() {
			@Override
			public void run() {
				
				for(int i = 0; i < list.size(); i++){
			         String filename = Constant.id + "-" + Tools.getCurrentTime() + "-" + i + ".png";// + Tools.getFileTypeByLocalPath(list.get(i));//重命名图片名字保留后缀

					Map<String,Object> map = new HashMap<String, Object>();
					map.put("TIME", Tools.getNowTimeS());
					map.put("TYPE", "photo");	//类型，文本，语音，图片，文件
					map.put("FROMID", Constant.id);	//自己id
					map.put("USERNAME", Constant.username);	//自己username
					map.put("TOID",  toid);	//目标id，user/group,根据此判定是否自己，来分别使用两种布局？
					map.put("PROFILEPATH", Constant.profilepath);
			
					map.put("MSG", filename/*Tools.getFileNameByLocalPath(list.get(i))*/);
					
					map.put("isok", "true");	//该文件的下载状态
					listChatMsg.add(listChatMsg.size(), map);


				  // 复制到指定目录
			         //  MyFile.copyFile(list.get(i), Constant.dirPhoto + filename);
			         //压缩图片文件，并复制到指定目录再，
			          MyImage.savePNG_After(MyImage.getBitmapByDecodeFile(list.get(i)), Constant.dirPhoto + filename );
						
			          //上传文件到服务器，
			         AndroidTools.uploadPhoto(filename, Constant.dirPhoto + filename);
			         

			         //并发送到服务器转发
			         //fromid,toid,type,time,msg
			         AndroidTools.sleep(Constant.sleepUpload);
			         MSGSender.sendChatMsgByGtypeToidTypeTimeMsg(ChatAc.this,type, toid, "photo", Tools.getNowTimeL(), filename);
			}

		          update();
			}
		}
		);
	        
	        
	}
	public void update(){
		AndroidTools.post(ivMenu, new Runnable() {
			@Override
			public void run() {
				 adapterLvChat.notifyDataSetChanged();
			     lvChat.setSelection(listChatMsg.size());	//选中最新一条，滚动到底部
			     updateSession(toid);
			}
		});
	}
	//发送文件
	public void sendFile(final String file){
        final String filename = Constant.id + Constant.split +Tools.getFileNameByLocalPath(file);//  copy文件到目标文件夹下并改名

		AndroidTools.thread(new Runnable() {
			@Override
			public void run() {
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("TIME", Tools.getNowTimeS());
				map.put("FROMID", Constant.id);	//自己id
				map.put("USERNAME", Constant.username);	//自己username
				map.put("TOID", toid);	//目标id，user/group,根据此判定是否自己，来分别使用两类布局？
				map.put("PROFILEPATH", Constant.profilepath);
				
				map.put("TYPE", "file");	//类型，文本，语音，图片，文件
				map.put("MSG", filename);	//声音文件名	本地路径+name.amr

				map.put("isok", "true");	//该文件的下载
				listChatMsg.add(listChatMsg.size(), map);
				

				
				
		        //发送到本地显示，并发送到服务器转发
		        //fromid,toid,type,time,msg
		        // /sdcard/mycc/file/100-xxx.doc
		       //只发送名字,服务器回传后，先判断本地是否存在，再下载并解析,更新视图
		        AndroidTools.uploadFile(filename, file);	//发送源文件
		       
				String localname = filename.substring(filename.split(Constant.split)[0].length() + Constant.split.length());	//xxx.doc
		        MyFile.copyFile(file, Constant.dirFile + localname);	//复制备份到文件接收目录
				update();

		        AndroidTools.sleep(Constant.sleepUpload);
		     
		        MSGSender.sendChatMsgByGtypeToidTypeTimeMsg(ChatAc.this,type, toid, "file", Tools.getNowTimeL(), filename);
			
			}
		}); 
        
	
	}
	//发送语音
	public void sendVoice(final String file){

		AndroidTools.thread(new Runnable() {
	
		@Override
		public void run() {

		Map<String,Object> map = new HashMap<String, Object>();
		map.put("TIME", Tools.getNowTimeS());
		map.put("FROMID", Constant.id);	//自己id
		map.put("USERNAME", Constant.username);	//自己username
		map.put("TOID", toid);	//目标id，user/group,根据此判定是否自己，来分别使用两类布局？
		map.put("PROFILEPATH", Constant.profilepath);
		
		map.put("TYPE", "voice");	//类型，文本，语音，图片，文件
		map.put("MSG", Tools.getFileNameByLocalPath(file));	//声音文件名	本地路径+name.amr
		
		map.put("count", Tools.calcTime(media.getDutation(file)));	//该文件的时长
		map.put("isplay", "false");	//该文件的播放状态
		map.put("isok", "true");	//该文件的下载

		listChatMsg.add(listChatMsg.size(), map);
		update();
		
         //发送到本地显示，并发送到服务器转发
        //fromid,toid,type,time,msg
        // /sdcard/mycc/record/100-101020120120120.amr
        String filename =Tools.getFileNameByLocalPath(file);	//只发送名字,服务器回传后，先判断本地是否存在，再下载并解析,更新视图
        AndroidTools.uploadVoice(filename, file);
	   
        AndroidTools.sleep(Constant.sleepUpload / 2);
		
        MSGSender.sendChatMsgByGtypeToidTypeTimeMsg(ChatAc.this,type, toid, "voice", Tools.getNowTimeL(), filename);
 
	}
	}	);
		
	}
	//发送文本
	public void sendText(){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("TIME", Tools.getNowTimeS());
		map.put("TYPE", "text");	//类型，文本，语音，图片，文件
		map.put("FROMID", Constant.id);	//自己id
		map.put("USERNAME", Constant.username);	//自己username
		map.put("TOID", this.toid);	//目标id，user/group,根据此判定是否自己，来分别使用两种布局？
		map.put("PROFILEPATH", Constant.profilepath);

		map.put("MSG", etSend.getText().toString());

		listChatMsg.add(listChatMsg.size(), map);
	    adapterLvChat.notifyDataSetChanged();
	    lvChat.setSelection(listChatMsg.size()-1);	//选中最新一条，滚动到底部
	    updateSession(this.toid);
	     //发送到本地显示，并发送到服务器转发
	    //fromid,toid,type,time,msg

	    if(Constant.offlineMode == 1){
	 	   //离线模式专用开启线程来发起网络请求,知道为何 okhttp 和网上通用httpUtil都无法获取结果
	        new Thread(new Runnable(){
	            @Override
	            public void run() {
	                HttpURLConnection connection=null;
	                try {
	                	String res;
	                    URL url=new URL(RobotTuling.getHttp() + etSend.getText().toString());
	                    connection =(HttpURLConnection) url.openConnection();
	                    connection.setRequestMethod("GET");
	                    connection.setConnectTimeout(8000);
	                    connection.setReadTimeout(8000);
	                    java.io.InputStream in=connection.getInputStream();
	                    //下面对获取到的输入流进行读取
	                    java.io.BufferedReader reader=new java.io.BufferedReader(new java.io.InputStreamReader(in));
	                    StringBuilder response=new StringBuilder();
	                    String line;
	                    while((line=reader.readLine())!=null){
	                        response.append(line);
	                    }
	                    
                    res=RobotTuling.makeMsg(response.toString());
                    
                    Message message = new Message();
                    Bundle b = new Bundle();
                    b.putString("res", res);
                    message.setData(b);
                    handler.sendMessage(message);
                } catch(Exception e){
                    e.printStackTrace();
                }finally{
                    if(connection!=null){
                        connection.disconnect();
                    }
                }
	            }
	             
	        }).start();
	    }else{ 
	    	MSGSender.sendChatMsgByGtypeToidTypeTimeMsg(this,type, toid, "text", Tools.getNowTimeL(), etSend.getText().toString());
	    }
	    
	    etSend.setText("");

	}
	//离线模式专用handler异步刷新界面
    Handler handler = new Handler(){
	          public void handleMessage(Message msg) {
	        	  Bundle b = msg.getData();
	        	  String res = b.getString("res");
	        	  Map<String,Object> mapd = new HashMap<String, Object>();
	       			mapd.put("USERNAME", "CC");
	       			mapd.put("TYPE", "text");	//类型，文本，语音，图片，文件
	       			mapd.put("PROFILEPATH", "http://img03.tooopen.com/images/20131111/sy_46708898917.jpg");
	       			mapd.put("FROMID", toid);
	       			mapd.put("TOID", Constant.id);
	       			mapd.put("TIME", Tools.getNowTimeS());
					mapd.put("MSG", res) ;
	       	    	listChatMsg.add(listChatMsg.size(), mapd);
	       		    adapterLvChat.notifyDataSetChanged();
	       		    lvChat.setSelection(listChatMsg.size()-1);	//选中最新一条，滚动到底部
	       		    updateSession(toid);
	       		    AndroidTools.log("robot res=" + res);
	          }
	    };
	 
	 
	//根据获取到的消息map，下载相应文件
	public void downLoadFile(Map<String, Object> map){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list.add(map);
		downLoadFile(list);
	}
	public void downLoadFile(List<Map<String, Object>> list){

		for(int i = 0; i < list.size(); i++){
			final Map<String, Object> map = list.get(i);

			try{
				if(MapListUtil.getMap(map, "TYPE").toString().equals("text")){

				}else if(MapListUtil.getMap(map, "TYPE").toString().equals("voice")){
					final String filename = MapListUtil.getMap(map, "MSG") ;//检测本地存在
					if(AndroidTools.fileExist(Constant.dirVoice + filename)){

						Tools.out("声音文件:" + Constant.dirVoice + filename + "已存在");
						map.put("isok", "true");	//该文件的可用状态
						map.put("count", Tools.calcTime(media.getDutation(Constant.dirVoice + filename)));	//该文件的时长
					}else{
						Tools.out("声音文件:" + Constant.dirVoice + filename + "不存在，开始下载");
						AndroidTools.downloadVoice(filename, new FileCallBack(Constant.dirVoice , filename) { 
					        @Override
					        public void onError(Call request, Exception e, int arg2)   {
					           AndroidTools.log("声音文件:" + Constant.dirVoice + filename + "onError :" + e.getMessage());
								map.put("isok", "error");	//该文件的可用状态
								adapterLvChat.notifyDataSetChanged();	
					        }
					        @Override
					        public void onResponse(File file, int arg1) {
					        	AndroidTools.log( "声音文件:" + Constant.dirVoice + filename + "onResponse :" + file.getAbsolutePath() + " int:" + arg1);
								map.put("isok", "true");	//该文件的可用状态
					        	map.put("count", Tools.calcTime(media.getDutation(Constant.dirVoice + filename)));	//该文件的时长
								adapterLvChat.notifyDataSetChanged();	
					        } 
					    });
						map.put("isok", "false");	//该文件的可用状态
					}
					map.put("isplay", "false");	//该文件的播放状态
				} else if(MapListUtil.getMap(map, "TYPE").toString().equals("photo")){ 
					final String filename = MapListUtil.getMap(map, "MSG").toString();//检测本地存在
					if(AndroidTools.fileExist(Constant.dirPhoto + filename)){
						Tools.out("图片文件:" + Constant.dirPhoto + filename + "已存在");
						map.put("isok", "true");	
					}else{
						Tools.out("图片文件:" + Constant.dirVoice + filename + "不存在，开始下载");
						AndroidTools.downloadPhoto(filename, new FileCallBack(Constant.dirPhoto , filename) { 
					        @Override
					        public void onError(Call request, Exception e, int arg2)   {
					           AndroidTools.log("图片文件:" + Constant.dirVoice + filename + "下载失败" + e.getMessage());
								map.put("isok", "false");	
								adapterLvChat.notifyDataSetChanged();	
					        }
					        @Override
					        public void onResponse(File file, int arg1) {
					        	AndroidTools.log( "图片文件下载结果onResponse :" + file.getAbsolutePath() + " int:" + arg1);
								map.put("isok", "true");	//该文件的可用状态，！！此处map是否能够更新listadpater里面的数据？
								adapterLvChat.notifyDataSetChanged();	
					        } 
					    });
						map.put("isok", "false");	//该文件的下载
					}
					
				} else if(MapListUtil.getMap(map, "TYPE").toString().equals("file")){
					final String id_filename = MapListUtil.getMap(map, "MSG").toString();//检测本地存在
					final String filename = id_filename.substring(id_filename.split(Constant.split)[0].length() + Constant.split.length());	//xxx.doc
					//关于同个用户重复发送某同名文件，文件覆盖不重复?, id????????????
					if(AndroidTools.fileExist(Constant.dirFile + filename)){ ///下载的文件并重命名，去掉102OTOTO服务器上传下载编制前缀
						Tools.out("文件:" + Constant.dirFile + filename + "已存在");
						map.put("isok", "true");
					}else{
						Tools.out("文件:" + Constant.dirFile + filename + "不存在，开始下载");
						AndroidTools.downloadFile(id_filename, new FileCallBack(Constant.dirFile , filename) { 
					        @Override
					        public void onError(Call request, Exception e, int arg2)   {
					           AndroidTools.log("文件:" + Constant.dirFile + filename + "下载失败onError." + e.getMessage());
								map.put("isok", "error");
					        	adapterLvChat.notifyDataSetChanged();	
					        }
					        @Override
					        public void onResponse(File file, int arg1) {
					        	AndroidTools.log( "文件下载结果onResponse :" + file.getAbsolutePath() + " int:" + arg1);
					        	//下载的文件并重命名，去掉102OTOTO服务器上传下载编制前缀
					        	file.renameTo(new File(Constant.dirFile+filename));
								map.put("isok", "true");
								adapterLvChat.notifyDataSetChanged();	
					        } 
					    });
						map.put("isok", "false");	//该文件的下载
					}
				  } 
				}catch(Exception e){
					toast( MapListUtil.getMap(map, "MSG")+" 下载失败");
					e.printStackTrace();
			}
			
		}
	
	
	}
	    
	    
}
