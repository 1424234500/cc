package com.cc;

import fragm.FragmentEmoji;
import fragm.FragmentMore;
import fragm.FragmentPhoto;
import fragm.FragmentVoice;
import interfac.CallInt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.MSGTYPE;
import net.MSGSender;
import okhttp3.Call;
import util.AndroidTools;
import util.EmotionKeyboard;
import util.JsonMsg;
import util.JsonUtil;
import util.MapListUtil;
import util.MyFile;
import util.MyImage;
import util.MyMediaPlayer;
import util.Tools;
import util.picasso.NetImage;
import util.view.ChatView;
import util.view.ChatView.OnControl;
import util.view.DialogImageShow;
import util.view.VoiceListener.OnVoice;
import adapter.AdapterLvChatDoll;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.zhy.http.okhttp.callback.FileCallBack;



 
/**
 * @author Walker
 * @date 2017-05-11 15:15:31
 * Description: 匿名聊天会话界面 main 核心！！
 */
public class ChatAcDoll extends BaseAc implements CallInt, View.OnClickListener{
 
	@Override
	public void callback(String jsonstr) {
		Map<String, Object>  map;
		int cmd = JsonMsg.getCmd(jsonstr);
		String v, v1, v2;
		int j;
		switch (cmd) {
		case MSGTYPE.DOLL_IN_OR_OUT_BY_NAME_TYPE:
			v = JsonMsg.getValue0(jsonstr);
			v1 =  JsonMsg.getValue1(jsonstr);
			v2 =  JsonMsg.getValue2(jsonstr);
			if(! v.equals(this.toid)){
				return;
			}
				
			Tools.out(v + "房间有人" + v1);
			map = new HashMap<String, Object>();
			map.put("TIME", Tools.getNowTimeS());
			map.put("TYPE", "text");	//类型，文本，语音，图片，文件
			map.put("FROMID", "-1");	// 非自己id
			map.put("TOID", this.toid);	//目标id，user/group,根据此判定是否自己，来分别使用两种布局？
			map.put("PROFILEPATH", v2);
			map.put("TIME",Tools.getNowTimeS());

			j = Tools.parseInt(getIntent().getExtras().getString("NOWNUM"));
			if(v1.equals("in")){
				j ++;
				map.put("MSG", "加入房间");
			}else{
				j --;
				map.put("MSG", "退出房间");
			}
			tvTitleTwo.setText(j + "/" + getIntent().getExtras().getString("MAXNUM"));
			
			listChatMsg.add(listChatMsg.size(), map);
		    adapterLvChat.notifyDataSetChanged();
		    lvChat.setSelection(listChatMsg.size()-1);	//选中最新一条，滚动到底部
			break;
		case MSGTYPE.DOLL_CHAT_BY_TONAME_TYPE_MSG://在线时收到一些条消息，判定，是此会话的fromid和toid才显示在当前界面
			
			map = JsonUtil.getMap(jsonstr);
			//Tools.out("当前selfid:"+Constant.id + " toid:" + this.toid);
			//我和目标用户之间的对话消息，此处一条消息，只能是别人发给我，或者别人发给群
			if( ( MapListUtil.getMap(map,  "TOID").equals(this.toid)) ){
				
				if(listChatMsg.size() >= Constant.maxChatNum){
            		//toast("更多的消息在聊天记录中查看");
					listChatMsg.remove(0);
            	}
				
				listChatMsg.add(listChatMsg.size(), map);
				downLoadFile(map);	//下载需要用的文件####################
	
				adapterLvChat.notifyDataSetChanged();
			    lvChat.setSelection(listChatMsg.size());	//选中最新一条，滚动到底部
			}
			
			break;
		}
	}

	
	//布局绑定view
	View viewContent;
	ListView lvChat;
	//username,time,text,profilepath
	AdapterLvChatDoll adapterLvChat;
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
 
	 
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ac_chat);
		tvReturn = (TextView)findViewById(R.id.tvreturn);
		tvTitleOne = (TextView)findViewById(R.id.tvtitleone);
		tvTitleTwo = (TextView)findViewById(R.id.tvtitletwo);
		ivMenu = (ImageView)findViewById(R.id.ivmenu);
		cvChat = (ChatView)findViewById(R.id.cvchat);
		viewFill = (LinearLayout)findViewById(R.id.viewfill);
		tvReturn.setOnClickListener(this);
		//ivMenu.setOnClickListener(this);
		ivMenu.setVisibility(View.INVISIBLE);
		//send button edittext
		etSend = (EditText)findViewById(R.id.etsend);
		tvSend = (TextView)findViewById(R.id.tvsend);
		tvSend.setOnClickListener(this);
		//listview
		lvChat = (ListView)findViewById(R.id.lvchat);
		listChatMsg = new ArrayList<Map<String,Object>>();
		adapterLvChat = new AdapterLvChatDoll(this, listChatMsg);
		lvChat.setAdapter(adapterLvChat);
		lvChat.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int scrollState) {
				  switch(scrollState){  
			        case OnScrollListener.SCROLL_STATE_IDLE://空闲状态  
			        	Tools.out("listview 空闲，开始加载图片");
			            NetImage.resume(ChatAcDoll.this);
			        	break;
			        case OnScrollListener.SCROLL_STATE_FLING://滚动状态  
			        	Tools.out("listview 滚动，暂停加载图片");
			            NetImage.pause(ChatAcDoll.this);
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
						MyMediaPlayer.getInstance().stop();
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

						MyMediaPlayer.getInstance().play(Constant.dirVoice + MapListUtil.getMap(map, "MSG"),new MyMediaPlayer.OnPlay() {
							@Override
							public void onPlayEnd(MediaPlayer mediaPlayerr) {
								listChatMsg.get(position).put("isplay", "false");
								adapterLvChat.notifyDataSetChanged();
							}
						} );
					}
					adapterLvChat.notifyDataSetChanged();
					
				}else if(type.equals("photo")){	//图片消息点击
					//携带参数跳转
					String path = Constant.dirPhoto + MapListUtil.getMap(map, "MSG");
					DialogImageShow dis = new DialogImageShow(ChatAcDoll.this, path);
					dis.show();
					dis.setCancelable(true);
				}else if(type.equals("file")){	//文件消息点击,提示打开
					
					String id_filename = MapListUtil.getMap(map, "MSG");	//101OTJOTOTxxx.doc
					String filename = id_filename.substring(id_filename.split(Constant.split)[0].length() + Constant.split.length());	//xxx.doc
					String path = Constant.dirFile + filename;
					AndroidTools.openFile(ChatAcDoll.this, path);
				} 

				
				
			}
		});
		//设置从上级传来的用户数据
		setByMap( AndroidTools.getMapFromIntent(this.getIntent()));
		
	 
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
 	     swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
	     swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(false);
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
		   fVoice.setFileBegin(this.toid+"-");	//先设置，再构造？
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
        	AndroidTools.log("拍照结果"+ChatAcDoll.TAKEPHOTO);
        	List<String> list = new ArrayList<String>();
			list.add(ChatAcDoll.TAKEPHOTO);
			sendPhotos(list);
        } 
        
	}  
	
	static int ACTIVITY_RESULT_PHOTP = 1;
	static int ACTIVITY_RESULT_CAMERA = 2;
	static int ACTIVITY_RESULT_FILE = 3;
	public static String TAKEPHOTO = "";
	public void takeGraph(){
		//设置图片的保存路径,作为全局变量

		ChatAcDoll.TAKEPHOTO = this.toid + "-" +  Constant.dirCamera + Constant.id + "-" + Tools.getCurrentTime()+".png";
		
		AndroidTools.takePhoto(this, ChatAcDoll.TAKEPHOTO, ACTIVITY_RESULT_CAMERA);
		
	} 

	private void setByMap(Map<String, Object> map) {
		if(map != null){
			//"result":{"NOWNUM":2,"MAXNUM":100,"NAME":"Default","IVROOM":24,"MASTERID":"123456"}
			tvReturn.setText("退出");
			tvTitleOne.setText(MapListUtil.getMap(map, "NAME").toString());
			tvTitleTwo.setText(MapListUtil.getMap(map, "NOWNUM") + "/" + MapListUtil.getMap(map, "MAXNUM").toString());
			toid = MapListUtil.getMap(map, "NAME").toString();
			type = "doll";
		}
	} 
	@Override
	public boolean OnBackPressed() {
		MSGSender.exitDoll(this);
		return false;	
	}
	//自定义组件点击响应传递id于此处理
	@Override
	public void call(int id) { 
		switch(id){
		case R.id.tvreturn:
			MSGSender.exitDoll(this);
			this.finish();
			break;
		case R.id.ivmenu://用户详情
			break;
		case R.id.tvsend: //点击发送消息
			if(Tools.notNull( etSend.getText().toString())){
				sendText();
			} 
			break;
		}
	}
   
	@Override
	public void OnDestroy() {
	}
	//ac里面表层的标签监听
	@Override
	public void onClick(View view) {
		call(view.getId());
	}

	public void update(){
		AndroidTools.post(ivMenu, new Runnable() {
			@Override
			public void run() {
				 adapterLvChat.notifyDataSetChanged();
			     lvChat.setSelection(listChatMsg.size());	//选中最新一条，滚动到底部
			}
		});
	}
	public void sendPhotos(final List<String> list){
		if(list == null ) return;


		AndroidTools.thread(new Runnable() {
			@Override
			public void run() {
				
				for(int i = 0; i < list.size(); i++){
			         String filename =  toid + "-" + Constant.id + "-" + Tools.getCurrentTime() + "-" + i + ".png";// + Tools.getFileTypeByLocalPath(list.get(i));//重命名图片名字保留后缀

					Map<String,Object> map = new HashMap<String, Object>();
					map.put("TIME", Tools.getNowTimeS());
					map.put("TYPE", "photo");	//类型，文本，语音，图片，文件
					map.put("FROMID", Constant.id);	//自己id
					map.put("USERNAME", Constant.username);	//自己username
					map.put("TOID",  toid);	//目标id，user/group,根据此判定是否自己，来分别使用两种布局？
					map.put("PROFILEPATH", Constant.ivdoll);
			
					map.put("MSG", filename/*Tools.getFileNameByLocalPath(list.get(i))*/);
					
					map.put("isok", "true");	//该文件的下载状态
					
					listChatMsg.add(listChatMsg.size(), map);

				   
			        //上传文件到服务器，并复制到指定目录
			        // MyFile.copyFile(list.get(i), Constant.dirPhoto + filename);
			         //压缩图片文件，并复制到指定目录再，上传文件到服务器，
			         MyImage.savePNG_After(MyImage.getBitmapByDecodeFile(list.get(i)), Constant.dirPhoto + filename );
						

			         
			         AndroidTools.uploadPhoto(filename, Constant.dirPhoto + filename);
			     
			         update();

			         AndroidTools.sleep(Constant.sleepUpload);
  
			         
			         //发送到本地显示，并发送到服务器转发
			         //fromid,toid,type,time,msg
			    	MSGSender.chatDollByTypeMsg(getContext(), toid, "photo",  filename);
				}
				
		         update();

			}
		});
		
	
	 
	        
	        
	}
	
	//发送文件
	public void sendFile(final String file){

		AndroidTools.thread(new Runnable() {
			@Override
			public void run() {
				
				   String filename =  toid + "-" +Constant.id + Constant.split +Tools.getFileNameByLocalPath(file);//  copy文件到目标文件夹下并改名
			        
					Map<String,Object> map = new HashMap<String, Object>();
					map.put("TIME", Tools.getNowTimeS());
					map.put("FROMID", Constant.id);	//自己id
					map.put("USERNAME", Constant.username);	//自己username
					map.put("TOID", toid);	//目标id，user/group,根据此判定是否自己，来分别使用两类布局？
					map.put("PROFILEPATH", Constant.ivdoll);
					
					map.put("TYPE", "file");	//类型，文本，语音，图片，文件
					map.put("MSG", filename);	//声音文件名	本地路径+name.amr
					
					map.put("isok", "true");	//该文件的下载

					listChatMsg.add(listChatMsg.size(), map);
			        AndroidTools.uploadFile(filename, file);	//发送 文件
					String localname = filename.substring(filename.split(Constant.split)[0].length() + Constant.split.length());	//xxx.doc
				
			        MyFile.copyFile(file, Constant.dirFile + localname);	//复制备份到文件接收目录
					update();

			        AndroidTools.sleep(Constant.sleepUpload);

			        // 发送到服务器转发
			        //fromid,toid,type,time,msg
			        // /sdcard/mycc/file/100-xxx.doc
			       //只发送名字,服务器回传后，先判断本地是否存在，再下载并解析,更新视图
			    	MSGSender.chatDollByTypeMsg(getContext(), toid, "file",  filename);

			}
		});

     
	}
	//发送语音
	public void sendVoice(String file){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("TIME", Tools.getNowTimeS());
		map.put("FROMID", Constant.id);	//自己id
		map.put("USERNAME", Constant.username);	//自己username
		map.put("TOID", toid);	//目标id，user/group,根据此判定是否自己，来分别使用两类布局？
		map.put("PROFILEPATH", Constant.ivdoll);
		
		map.put("TYPE", "voice");	//类型，文本，语音，图片，文件
		map.put("MSG", Tools.getFileNameByLocalPath(file));	//声音文件名	本地路径+name.amr
		
		map.put("count", Tools.calcTime(MyMediaPlayer.getInstance().getDutation(file)));	//该文件的时长
		map.put("isplay", "false");	//该文件的播放状态
		map.put("isok", "true");	//该文件的下载

		listChatMsg.add(listChatMsg.size(), map);
	 
         //发送到本地显示，并发送到服务器转发
        //fromid,toid,type,time,msg
        // /sdcard/mycc/record/100-101020120120120.amr
        String filename =Tools.getFileNameByLocalPath(file);	//只发送名字,服务器回传后，先判断本地是否存在，再下载并解析,更新视图
    	
        AndroidTools.uploadVoice(filename, file);
        
        AndroidTools.sleep(Constant.sleepUpload / 2);

    	MSGSender.chatDollByTypeMsg(this, toid, "voice",  filename);

    	
	}
	//发送文本
	public void sendText(){
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("TIME", Tools.getNowTimeS());
		map.put("TYPE", "text");	//类型，文本，语音，图片，文件
		map.put("FROMID", Constant.id);	//自己id
		map.put("USERNAME", Constant.username);	//自己username
		map.put("TOID", this.toid);	//目标id，user/group,根据此判定是否自己，来分别使用两种布局？
		map.put("PROFILEPATH", Constant.ivdoll);
	
		map.put("MSG", etSend.getText().toString());
		
		listChatMsg.add(listChatMsg.size(), map);
	    adapterLvChat.notifyDataSetChanged();
	    lvChat.setSelection(listChatMsg.size()-1);	//选中最新一条，滚动到底部
	   // updateSession(this.toid);
	     //发送到本地显示，并发送到服务器转发
	    //fromid,toid,type,time,msg

    	MSGSender.chatDollByTypeMsg(this, toid, "text",  etSend.getText().toString());
	    
	    etSend.setText("");

	}
 
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
					if(new File(Constant.dirVoice + filename).exists()){
						Tools.out("声音文件:" + Constant.dirVoice + filename + "已存在");
						map.put("isok", "true");	//该文件的可用状态
						map.put("count", Tools.calcTime(MyMediaPlayer.getInstance().getDutation(Constant.dirVoice + filename)));	//该文件的时长
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
					        	map.put("count", Tools.calcTime(MyMediaPlayer.getInstance().getDutation(Constant.dirVoice + filename)));	//该文件的时长
								adapterLvChat.notifyDataSetChanged();	
					        } 
					    });
						map.put("isok", "false");	//该文件的可用状态
					}
					map.put("isplay", "false");	//该文件的播放状态
				} else if(MapListUtil.getMap(map, "TYPE").toString().equals("photo")){ 
					final String filename = MapListUtil.getMap(map, "MSG").toString();//检测本地存在
					if(new File(Constant.dirPhoto + filename).exists()){
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
					if(new File(Constant.dirFile + filename).exists()){////下载的文件并重命名，去掉102OTOTO服务器上传下载编制前缀
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
