package com.cc;

import interfac.CallInt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.MSGTYPE;
import net.MSGSender;
import okhttp3.Call;
import util.AndroidTools;
import util.JsonMsg;
import util.JsonUtil;
import util.MapListUtil;
import util.MyMediaPlayer;
import util.Tools;
import util.picasso.NetImage;
import util.view.DialogImageShow;
import adapter.AdapterLvChat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.zhy.http.okhttp.callback.FileCallBack;



 
/**
 * 
 * @author Walker
 * @date 2017-5-8 下午6:42:44
 * Description:聊天记录, 传入 标题栏 , ID, TYPE user/group
 */
public class ChatHistoryAc extends BaseAc implements CallInt, View.OnClickListener{
 
	@Override
	public void callback(String jsonstr) {
		Map map = JsonUtil.getMap(jsonstr);
		int cmd = MapListUtil.getMap(map, "cmd", 0);
		String value = MapListUtil.getMap(map, "value0", "");
		if(value.equals("false")){
			toast("异常:" + MapListUtil.getMap(map, "value1"));
			return;
		}
		List list;
		switch (cmd) {
		case MSGTYPE.GET_USER_GROUP_CHAT_BY_TYPE_ID_START_HISTORY://查询消息记录 服务器决定一次多x<10>条消息
			closeLoading();
			list = MapListUtil.getMap(map, "value1", new ArrayList());

			//需要适配好友 私聊，群聊，非好友，为了复用代码，多传输部分冗余数据
			//本地添加会话记录
			//适配器需要username,time,msg,type,profilepath
			//消息记录fromid,toid,type,time,msg
			//会话列表type <user,group>,toid id,username,profilepath,nickname,name,   msg,time,status <在线,离线>
			if(list.size() <= 0){
				listtime.remove(0);
				toast("没有更旧的聊天记录了");
			}else{
	            listChatMsg.clear();
	            listChatMsg.addAll(0, list);
				downLoadFile(list);	//下载需要用的文件####################
				adapterLvChat.notifyDataSetChanged();
	            lvChat.setSelection(list.size());	//选中刷新出来的数据的最新一条
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
	
	 
	TextView tvReturn,tvTitleOne,tvTitleTwo, tvpre, tvnext;//, tvSend;
	String toid;	//聊天对方id
	String type;	//类型 群，人
 
	 
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		setContentView(R.layout.ac_chat_history);
		tvReturn = (TextView)findViewById(R.id.tvreturn);
		tvTitleOne = (TextView)findViewById(R.id.tvtitleone);
		tvpre = (TextView)findViewById(R.id.tvpre);
		tvnext = (TextView)findViewById(R.id.tvnext);
		tvpre.setOnClickListener(this);
		tvnext.setOnClickListener(this);
		
		tvTitleTwo = (TextView)findViewById(R.id.tvtitletwo);
		tvReturn.setOnClickListener(this);
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
					DialogImageShow dis = new DialogImageShow(ChatHistoryAc.this, path);
					dis.show();
					dis.setCancelable(true);
				}else if(type.equals("file")){	//文件消息点击,提示打开
					String id_filename = MapListUtil.getMap(map, "MSG");	//101OTJOTOTxxx.doc
					String filename = id_filename.substring(id_filename.split(Constant.split)[0].length() + Constant.split.length());	//xxx.doc
					String path = Constant.dirFile + filename;
					AndroidTools.openFile(ChatHistoryAc.this, path);
				} 
			}
		});
		//设置从上级传来的用户数据
		setByMap( AndroidTools.getMapFromIntent(this.getIntent()));
		
		//服务器添加会话记录
		MSGSender.addChatSession(this, type, toid);
		//获取分页聊天记录,现在时间之前的""表示服务器最新x条，否则按照当前信息最老一条的前面x条
		MSGSender.getChatMsgByTypeIdStarttimeHistory(this, type, toid, "");
		openLoading();
		listtime.add("");
	}
	private void setByMap(Map<String, Object> map) {
		if(map != null){
			tvTitleOne.setText(MapListUtil.getMap(map, "title").toString());
			tvTitleTwo.setText("");
			tvReturn.setText("返回");
			toid = MapListUtil.getMap(map, "ID").toString();
			type = MapListUtil.getMap(map, "TYPE").toString();
		}
	} 
	@Override
	public boolean OnBackPressed() {
		return false;	
	}
	//自定义组件点击响应传递id于此处理
	@Override
	public void call(int id) { 
		String time = "";
		switch(id){
		case R.id.tvreturn:
			this.finish();
			break;
		case R.id.tvpre:
        	time = "";
        	if(listChatMsg.size() > 0){
        		time = MapListUtil.getList(listChatMsg, 0, "TIME");
        		listtime.add(0, time);
        		MSGSender.getChatMsgByTypeIdStarttimeHistory(ChatHistoryAc.this, type, toid, time );
        		openLoading();
        	}else{
   			 toast("没有更旧的聊天记录了");
        	}
    
			break;
		case R.id.tvnext:
		  	time = "";
    		 if(listtime.size() <= 1){
    			 toast("当前已是最近记录");return;
    		 }else{ 
    			 listtime.remove(0);
    			 time = listtime.get(0);
    			 MSGSender.getChatMsgByTypeIdStarttimeHistory(ChatHistoryAc.this, type, toid, time );
    	    		openLoading();
    		 }
    		
			break;
		}
	}
	
	ArrayList<String> listtime = new ArrayList<String>();
	@Override
	public void OnDestroy() {
	}
	//ac里面表层的标签监听
	@Override
	public void onClick(View view) {
		call(view.getId());
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
