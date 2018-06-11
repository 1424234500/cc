package adapter;

import interfac.CallInt;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import util.EmotionUtils;
import util.MapListUtil;
import util.Tools;
import util.picasso.NetImage;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cc.Constant;
import com.cc.R;


 
 
/**
 * @author Walker
 * @date 2017-3-28 下午1:00:37
 * Description: 聊天消息适配器,统一群聊 私聊
 */
public   class AdapterLvChat extends  BaseAdapter      {
	private Context context; // 运行上下文
	private List<Map<String, Object>>  listItems = null; // listview的数据集合
	private LayoutInflater listContainer; // 视图容器
	//控件集合实例
	private static ViewHolderTextSelf viewHolderTextSelf ;
	private  static ViewHolderTextOther viewHolderTextOther ;
	private  static ViewHolderVoiceSelf viewHolderVoiceSelf ;
	private  static ViewHolderVoiceOther viewHolderVoiceOther ;
	private  static ViewHolderPhotoSelf viewHolderPhotoSelf ;
	private  static ViewHolderPhotoOther viewHolderPhotoOther ;
	private  static ViewHolderFileSelf viewHolderFileSelf ;
	private  static ViewHolderFileOther viewHolderFileOther ;
	
	//布局类型
    final int TYPE_TEXT_SELF = 0;	//自己发的文本
    final int TYPE_TEXT_OTHER = 1;	//收到的别人的文本   
    final int TYPE_VOICE_SELF = 2;	//自己发的语音
    final int TYPE_VOICE_OTHER = 3;	//收到的别人的语音
    final int TYPE_PHOTO_SELF = 4;	//自己发的图片
    final int TYPE_PHOTO_OTHER = 5;	//收到的别人的图片
    final int TYPE_FILE_SELF = 6;	//自己发的文件
    final int TYPE_FILE_OTHER = 7;	//收到的别人的文件
    
    // // 自定义控件集合  布局类型
    //文本消息，头像，用户名，时间，信息
    private final class ViewHolderTextSelf {
		public ImageView ivprofile;  
		public TextView tvusername; 
		public TextView tvtime;
		public TextView tvtext;
	}	
    private final class ViewHolderTextOther {
		public ImageView ivprofile;  
		public TextView tvusername; 
		public TextView tvtime;
		public TextView tvtext;
	} 
    private final class ViewHolderVoiceSelf {
 		public ImageView ivprofile;  
 		public TextView tvusername; 
 		public TextView tvtime;
 		public TextView tvtext;
 		public ImageView ivvoice;  
 	}	
     private final class ViewHolderVoiceOther {
 		public ImageView ivprofile;  
 		public TextView tvusername; 
 		public TextView tvtime;
 		public TextView tvtext;
 		public ImageView ivvoice;  
 	} 
     private final class ViewHolderPhotoSelf {
  		public ImageView ivprofile;  
  		public TextView tvusername; 
  		public TextView tvtime;
  		//public TextView tvtext;
  		public ImageView ivphoto;  
  	}	
      private final class ViewHolderPhotoOther {
  		public ImageView ivprofile;  
  		public TextView tvusername; 
  		public TextView tvtime;
  		//public TextView tvtext;
  		public ImageView ivphoto;  
  	} 
      private final class ViewHolderFileSelf {
   		public ImageView ivprofile;  
   		public TextView tvusername; 
   		public TextView tvtime;
   		
   		public TextView tvtext;
   		public ImageView ivvoice;  
   	}	
       private final class ViewHolderFileOther {
   		public ImageView ivprofile;  
   		public TextView tvusername; 
   		public TextView tvtime;
   		public TextView tvtext;
   		public ImageView ivvoice;  
   	} 
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//TIME,TYPE TEXT VOICE,FROMID,TOID,PROFILEPATH  
		viewHolderTextSelf = null;  
		viewHolderTextOther = null;  
		viewHolderVoiceSelf = null;  
		viewHolderVoiceOther = null;  
		viewHolderPhotoSelf = null;  
		viewHolderPhotoOther = null; 
		viewHolderFileSelf = null;  
		viewHolderFileOther = null;  
		
		int type = getItemViewType(position);	//得到No.i条数据布局类型
		
		//构建或者取出可复用布局
		if (convertView == null) { //若无可复用布局
			if(type== TYPE_TEXT_SELF){
				viewHolderTextSelf = new ViewHolderTextSelf();
				convertView = listContainer.inflate(R.layout.chat_item_text_right, null);	// 获取list_item布局文件的视图
				viewHolderTextSelf.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolderTextSelf.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
				viewHolderTextSelf.tvtime = (TextView) convertView .findViewById(R.id.tvtime);
				viewHolderTextSelf.tvtext = (TextView) convertView .findViewById(R.id.tvtext);
					
				convertView.setTag(viewHolderTextSelf);// 设置控件集到convertView
			}else if(type== TYPE_TEXT_OTHER){
				viewHolderTextOther = new ViewHolderTextOther();
				convertView = listContainer.inflate(R.layout.chat_item_text_left, null);	// 获取list_item布局文件的视图
				viewHolderTextOther.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolderTextOther.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
				viewHolderTextOther.tvtime = (TextView) convertView .findViewById(R.id.tvtime);
				viewHolderTextOther.tvtext = (TextView) convertView .findViewById(R.id.tvtext);
					
				convertView.setTag(viewHolderTextOther);// 设置控件集到convertView
			}else if(type== TYPE_VOICE_SELF){
				viewHolderVoiceSelf = new ViewHolderVoiceSelf();
				convertView = listContainer.inflate(R.layout.chat_item_voice_right, null);	// 获取list_item布局文件的视图
				viewHolderVoiceSelf.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolderVoiceSelf.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
				viewHolderVoiceSelf.tvtime = (TextView) convertView .findViewById(R.id.tvtime);
				viewHolderVoiceSelf.tvtext = (TextView) convertView .findViewById(R.id.tvtext);
				viewHolderVoiceSelf.ivvoice = (ImageView) convertView .findViewById(R.id.ivvoice);
			
				convertView.setTag(viewHolderVoiceSelf);// 设置控件集到convertView
			}else if(type== TYPE_VOICE_OTHER){
				viewHolderVoiceOther = new ViewHolderVoiceOther();
				convertView = listContainer.inflate(R.layout.chat_item_voice_left, null);	// 获取list_item布局文件的视图
				viewHolderVoiceOther.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolderVoiceOther.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
				viewHolderVoiceOther.tvtime = (TextView) convertView .findViewById(R.id.tvtime);
				viewHolderVoiceOther.tvtext = (TextView) convertView .findViewById(R.id.tvtext);
				viewHolderVoiceOther.ivvoice = (ImageView) convertView .findViewById(R.id.ivvoice);

				convertView.setTag(viewHolderVoiceOther);// 设置控件集到convertView
			}else if(type== TYPE_PHOTO_SELF){
				viewHolderPhotoSelf = new ViewHolderPhotoSelf();
				convertView = listContainer.inflate(R.layout.chat_item_photo_right, null);	// 获取list_item布局文件的视图
				viewHolderPhotoSelf.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolderPhotoSelf.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
				viewHolderPhotoSelf.tvtime = (TextView) convertView .findViewById(R.id.tvtime);
				//viewHolderPhotoSelf.tvtext = (TextView) convertView .findViewById(R.id.tvtext);
				viewHolderPhotoSelf.ivphoto = (ImageView) convertView .findViewById(R.id.ivphoto);
					
				convertView.setTag(viewHolderPhotoSelf);// 设置控件集到convertView
			}else if(type== TYPE_PHOTO_OTHER){
				viewHolderPhotoOther = new ViewHolderPhotoOther();
				convertView = listContainer.inflate(R.layout.chat_item_photo_left, null);	// 获取list_item布局文件的视图
				viewHolderPhotoOther.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolderPhotoOther.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
				viewHolderPhotoOther.tvtime = (TextView) convertView .findViewById(R.id.tvtime);
				//viewHolderPhotoOther.tvtext = (TextView) convertView .findViewById(R.id.tvtext);
				viewHolderPhotoOther.ivphoto = (ImageView) convertView .findViewById(R.id.ivphoto);

				convertView.setTag(viewHolderPhotoOther);// 设置控件集到convertView
			}else if(type== TYPE_FILE_SELF){
				viewHolderFileSelf = new ViewHolderFileSelf();
				convertView = listContainer.inflate(R.layout.chat_item_file_right, null);	// 获取list_item布局文件的视图
				viewHolderFileSelf.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolderFileSelf.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
				viewHolderFileSelf.tvtime = (TextView) convertView .findViewById(R.id.tvtime);
				viewHolderFileSelf.tvtext = (TextView) convertView .findViewById(R.id.tvtext);
				viewHolderFileSelf.ivvoice = (ImageView) convertView .findViewById(R.id.ivvoice);
					
				convertView.setTag(viewHolderFileSelf);// 设置控件集到convertView
			}else if(type== TYPE_FILE_OTHER){
				viewHolderFileOther = new ViewHolderFileOther();
				convertView = listContainer.inflate(R.layout.chat_item_file_left, null);	// 获取list_item布局文件的视图
				viewHolderFileOther.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolderFileOther.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
				viewHolderFileOther.tvtime = (TextView) convertView .findViewById(R.id.tvtime);
				viewHolderFileOther.tvtext = (TextView) convertView .findViewById(R.id.tvtext);
				viewHolderFileOther.ivvoice = (ImageView) convertView .findViewById(R.id.ivvoice);

				convertView.setTag(viewHolderFileOther);// 设置控件集到convertView
			}
		} else {//若有可复用布局
			if(type== TYPE_TEXT_SELF){
				viewHolderTextSelf = (ViewHolderTextSelf) convertView.getTag();
			}else if(type== TYPE_TEXT_OTHER){
				viewHolderTextOther = (ViewHolderTextOther) convertView.getTag();
			}else if(type== TYPE_VOICE_SELF){
				viewHolderVoiceSelf = (ViewHolderVoiceSelf) convertView.getTag();
			}else if(type== TYPE_VOICE_OTHER){
				viewHolderVoiceOther = (ViewHolderVoiceOther) convertView.getTag();
			}else 	if(type== TYPE_PHOTO_SELF){
				viewHolderPhotoSelf = (ViewHolderPhotoSelf) convertView.getTag();
			}else if(type== TYPE_PHOTO_OTHER){
				viewHolderPhotoOther = (ViewHolderPhotoOther) convertView.getTag();
			}else if(type== TYPE_FILE_SELF){
				viewHolderFileSelf = (ViewHolderFileSelf) convertView.getTag();
			}else if(type== TYPE_FILE_OTHER){
				viewHolderFileOther = (ViewHolderFileOther) convertView.getTag();
			}
		}
		// 设置文字和图片和监听
		//////////////////////////////////////////设置文本消息
		if(type == TYPE_TEXT_SELF ){ 
			viewHolderTextSelf.tvusername.setText( Tools.cutName(Constant.username)) ;
			viewHolderTextSelf.tvtime.setText(Tools.formatL(new Timestamp(Tools.parseLong(MapListUtil.getList(listItems, position, "TIME"))))) ;
	        //解析消息内容
	        SpannableString spannableString = EmotionUtils.getEmotionContent(context,viewHolderTextSelf.tvtext,MapListUtil.getList(listItems, position, "MSG").toString());
	        viewHolderTextSelf.tvtext.setText(spannableString);
	        viewHolderTextSelf.ivprofile.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(callInt != null)callInt.call(position);
				}
			});
			NetImage.loadProfile(context, MapListUtil.getList(listItems, position, "PROFILEPATH").toString(), viewHolderTextSelf.ivprofile);
		}else if(type == TYPE_TEXT_OTHER){ 
			viewHolderTextOther.tvusername.setText(Tools.cutName( MapListUtil.getList(listItems, position, "USERNAME").toString())) ;
			viewHolderTextOther.tvtime.setText(  Tools.formatL(new Timestamp(Tools.parseLong(MapListUtil.getList(listItems, position, "TIME"))))) ;
	        //解析消息内容
	        SpannableString spannableString = EmotionUtils.getEmotionContent(context,viewHolderTextOther.tvtext,MapListUtil.getList(listItems, position, "MSG").toString());
	        viewHolderTextOther.tvtext.setText(spannableString);
	        viewHolderTextOther.ivprofile.setOnClickListener(new OnClickListener() {
	    				@Override
	    				public void onClick(View arg0) {
	    					if(callInt != null)callInt.call(position);
	    				}
	    			});
			NetImage.loadProfile(context, MapListUtil.getList(listItems, position, "PROFILEPATH").toString(), viewHolderTextOther.ivprofile);
		}
		//////////////////////////////////////设置语音消息
		else if(type == TYPE_VOICE_SELF ){ 
			viewHolderVoiceSelf.tvusername.setText( Tools.cutName(Constant.username)) ;
			viewHolderVoiceSelf.tvtime.setText(Tools.formatL(new Timestamp(Tools.parseLong(MapListUtil.getList(listItems, position, "TIME"))))) ;
			NetImage.loadProfile(context, MapListUtil.getList(listItems, position, "PROFILEPATH").toString(), viewHolderVoiceSelf.ivprofile);
			//设置语音播放状态
			//viewHolderVoiceSelf.ivvoice.setImageResource(R.anim.anim_frame_voice_right);
			AnimationDrawable anim = (AnimationDrawable)viewHolderVoiceSelf.ivvoice.getDrawable(); 
//			anim.start();  

			if(MapListUtil.getMap(listItems.get( position), "isplay").equals("true")){
				 anim.start();  
			}else{
				 anim.stop();  
			}
			
			if(MapListUtil.getList(listItems, position, "isok").toString().equals("true")){//文件下载就绪
				viewHolderVoiceSelf.tvtext.setText(MapListUtil.getMap(listItems.get( position), "count"));
				viewHolderVoiceSelf.ivvoice.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if(callInt != null) callInt.call(position);
					}
				});
			}else if(MapListUtil.getList(listItems, position, "isok").toString().equals("error")){
				viewHolderVoiceSelf.tvtext.setText("下载失败");
				viewHolderVoiceSelf.ivvoice.setOnClickListener(null);
			}else {
				viewHolderVoiceSelf.tvtext.setText("下载中");
				viewHolderVoiceSelf.ivvoice.setOnClickListener(null);
			}
		
		}else if(type == TYPE_VOICE_OTHER){ 
			viewHolderVoiceOther.tvusername.setText(Tools.cutName( MapListUtil.getList(listItems, position, "USERNAME").toString())) ;
			viewHolderVoiceOther.tvtime.setText(  Tools.formatL(new Timestamp(Tools.parseLong(MapListUtil.getList(listItems, position, "TIME"))))) ;
			NetImage.loadProfile(context, MapListUtil.getList(listItems, position, "PROFILEPATH").toString(), viewHolderVoiceOther.ivprofile);
		
			//设置语音播放状态
			AnimationDrawable anim = (AnimationDrawable)viewHolderVoiceOther.ivvoice.getDrawable(); 
			if(MapListUtil.getMap(listItems.get( position), "isplay").equals("true")){
				if (!anim.isRunning()) {  
					anim.start();  
	            }
			}else{
				 if (anim.isRunning()) {  
						anim.stop();  
		          }
			}
			if(MapListUtil.getList(listItems, position, "isok").toString().equals("true")){//文件下载就绪
				viewHolderVoiceOther.tvtext.setText(MapListUtil.getMap(listItems.get( position), "count"));
				viewHolderVoiceOther.ivvoice.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if(callInt != null) callInt.call(position);
					}
				});
			}else if(MapListUtil.getList(listItems, position, "isok").toString().equals("error")){
				viewHolderVoiceOther.tvtext.setText("下载失败");
				viewHolderVoiceOther.ivvoice.setOnClickListener(null);
			}else {
				viewHolderVoiceOther.tvtext.setText("下载中");
				viewHolderVoiceOther.ivvoice.setOnClickListener(null);
			}
	
		}
		//////////////////////////////////////设置图片消息
		else if(type == TYPE_PHOTO_SELF ){ 
			viewHolderPhotoSelf.tvusername.setText( Tools.cutName(Constant.username)) ;
			viewHolderPhotoSelf.tvtime.setText(Tools.formatL(new Timestamp(Tools.parseLong(MapListUtil.getList(listItems, position, "TIME"))))) ;
			NetImage.loadProfile(context, MapListUtil.getList(listItems, position, "PROFILEPATH").toString(), viewHolderPhotoSelf.ivprofile);
		
			//NetImage.loadNetImageInto(context, MapListUtil.getList(listItems, position, "MSG").toString(), viewHolderPhotoSelf.ivphoto);
			if(MapListUtil.getList(listItems, position, "isok").toString().equals("true")){//文件下载就绪,使用picasso加载的本地文件
				NetImage.loadLocalImgResize(context, Constant.dirPhoto + MapListUtil.getList(listItems, position, "MSG"),Constant.photoMaxH, viewHolderPhotoSelf.ivphoto);
			//	viewHolderPhotoSelf.ivphoto.setImageBitmap(MyImage.getBitmapByDecodeFile(Constant.dirPhoto + MapListUtil.getList(listItems, position, "MSG").toString()));
			//	Tools.out("设置图片消息"+Constant.dirPhoto + MapListUtil.getList(listItems, position, "MSG").toString());
				viewHolderPhotoSelf.ivphoto.setClickable(true);
				viewHolderPhotoSelf.ivphoto.setOnClickListener(new OnClickListener() { // 点击放大
					public void onClick(View paramView) {
						if(callInt != null) callInt.call(position);
					} 
				});
			}else{
				viewHolderPhotoSelf.ivphoto.setClickable(false);
				//viewHolderPhotoSelf.tvtext.setText("下载中");
				viewHolderPhotoSelf.ivphoto.setImageResource(R.drawable.loading);
			}
			
		}else if(type == TYPE_PHOTO_OTHER){ 
			viewHolderPhotoOther.tvusername.setText(Tools.cutName( MapListUtil.getList(listItems, position, "USERNAME").toString())) ;
			viewHolderPhotoOther.tvtime.setText(  Tools.formatL(new Timestamp(Tools.parseLong(MapListUtil.getList(listItems, position, "TIME"))))) ;
			NetImage.loadProfile(context, MapListUtil.getList(listItems, position, "PROFILEPATH").toString(), viewHolderPhotoOther.ivprofile);
			
			//MSG存储图片名字id
			//NetImage.loadNetImageInto(context, MapListUtil.getList(listItems, position, "MSG").toString(), viewHolderPhotoSelf.ivphoto);
			if(MapListUtil.getList(listItems, position, "isok").toString().equals("true")){//文件下载就绪,使用picasso加载的本地文件
				NetImage.loadLocalImgResize(context, Constant.dirPhoto + MapListUtil.getList(listItems, position, "MSG"),Constant.photoMaxH, viewHolderPhotoOther.ivphoto);
				//viewHolderPhotoOther.ivphoto.setImageBitmap(MyImage.getBitmapByDecodeFile(Constant.dirPhoto + MapListUtil.getList(listItems, position, "MSG").toString()));
				//	Tools.out("设置图片消息"+Constant.dirPhoto + MapListUtil.getList(listItems, position, "MSG").toString());
				viewHolderPhotoOther.ivphoto.setOnClickListener(new OnClickListener() { // 点击放大
					public void onClick(View paramView) {
						if(callInt != null) callInt.call(position);
					} 
				});
			}else{
				//viewHolderPhotoSelf.tvtext.setText("下载中");
				viewHolderPhotoOther.ivphoto.setImageResource(R.drawable.loading);
			}
			//网络图片加载？缓存问题！ 还是说后台下载完毕后通知显示，状态位再更新,当是图片类型时MSG存储路径,自己发时存储本地路径，网络获取时存储网络路径
		}
		//////////////////////////////////////设置文件消息
		else if(type == TYPE_FILE_SELF ){ 
			viewHolderFileSelf.tvusername.setText( Tools.cutName(Constant.username)) ;
			viewHolderFileSelf.tvtime.setText(Tools.formatL(new Timestamp(Tools.parseLong(MapListUtil.getList(listItems, position, "TIME"))))) ;
			NetImage.loadProfile(context, MapListUtil.getList(listItems, position, "PROFILEPATH").toString(), viewHolderFileSelf.ivprofile);
			
			String id_filename = MapListUtil.getMap(listItems.get( position), "MSG");	//1011_xxx.doc
			String filename = id_filename.substring(id_filename.split(Constant.split)[0].length() + Constant.split.length());	//xxx.doc
			String fileext = Tools.getFileTypeByLocalPath(filename);	//doc
			int resid = Constant.getDrawableByTypeFile(fileext);
			
			NetImage.loadImage(context, resid, viewHolderFileSelf.ivvoice);

			if(MapListUtil.getList(listItems, position, "isok").toString().equals("true")){//文件下载就绪
				viewHolderFileSelf.tvtext.setText(filename);
				viewHolderFileSelf.ivvoice.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if(callInt != null) callInt.call(position);
					}
				});
			}else if(MapListUtil.getList(listItems, position, "isok").toString().equals("error")){
				viewHolderFileSelf.tvtext.setText(filename + "\n下载失败");
				viewHolderFileSelf.ivvoice.setOnClickListener(null);
			}else {
				viewHolderFileSelf.tvtext.setText(filename + "\n下载中");
				viewHolderFileSelf.ivvoice.setOnClickListener(null);
			}
			// viewHolderTextSelf.tvtext.setText(spannafbleString);
		}else if(type == TYPE_FILE_OTHER){ 
			viewHolderFileOther.tvusername.setText(Tools.cutName( MapListUtil.getList(listItems, position, "USERNAME").toString())) ;
			viewHolderFileOther.tvtime.setText(  Tools.formatL(new Timestamp(Tools.parseLong(MapListUtil.getList(listItems, position, "TIME"))))) ;
			NetImage.loadProfile(context, MapListUtil.getList(listItems, position, "PROFILEPATH").toString(), viewHolderFileOther.ivprofile);
		
			String id_filename = MapListUtil.getMap(listItems.get( position), "MSG");	//1011_xxx.doc
			String filename = id_filename.substring(id_filename.split(Constant.split)[0].length() + Constant.split.length());	//xxx.doc
			String fileext = Tools.getFileTypeByLocalPath(filename);	//doc
			int resid = Constant.getDrawableByTypeFile(fileext);
			
			NetImage.loadImage(context, resid, viewHolderFileOther.ivvoice);
			if(MapListUtil.getList(listItems, position, "isok").toString().equals("true")){//文件下载就绪
				viewHolderFileOther.tvtext.setText(filename);
				viewHolderFileOther.ivvoice.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if(callInt != null) callInt.call(position);
					}
				});
			}else if(MapListUtil.getList(listItems, position, "isok").toString().equals("error")){
				viewHolderFileOther.tvtext.setText(filename + "\n下载失败");
				viewHolderFileOther.ivvoice.setOnClickListener(null);
			}else {
				viewHolderFileOther.tvtext.setText(filename + "\n下载中");
				viewHolderFileOther.ivvoice.setOnClickListener(null);
			}
		}
		
		return convertView; 
	}
	
	//必须实现，通知adapter有几种布局类型
		@Override
	public int getViewTypeCount() {
		return 10;
	}
	//必须实现，让adapter可控布局类型
	@Override
	public int getItemViewType(int position) {
		if( MapListUtil.getList(listItems, position, "TYPE").toString().equals("text")){
			if( MapListUtil.getList(listItems, position,"FROMID").toString().equals(Constant.id)){//自己发送的
				return TYPE_TEXT_SELF;
			}else{
				return TYPE_TEXT_OTHER;
			}
		 }else if( MapListUtil.getList(listItems, position,"TYPE").toString().equals("voice")){
			if( MapListUtil.getList(listItems, position,"FROMID").toString().equals(Constant.id)){//自己发送的
				return TYPE_VOICE_SELF;
			}else{
				return TYPE_VOICE_OTHER;
			}
		 }else if( MapListUtil.getList(listItems, position,"TYPE").toString().equals("photo")){
			if( MapListUtil.getList(listItems, position,"FROMID").toString().equals(Constant.id)){//自己发送的
				return TYPE_PHOTO_SELF;
			}else{
				return TYPE_PHOTO_OTHER;
			}
		 }else if( MapListUtil.getList(listItems, position,"TYPE").toString().equals("file")){
			if( MapListUtil.getList(listItems, position,"FROMID").toString().equals(Constant.id)){//自己发送的
				return TYPE_FILE_SELF;
			}else{
				return TYPE_FILE_OTHER;
			}
		 }
		 return -1;
	}
		
	
	public AdapterLvChat(Context context, List<Map<String, Object>> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = listItems;
	 
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int i) {
		return listItems.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}


	
	//为何 listview点击item事件无效问题，！？暂且自定义接口回调
	public void setOnChose(CallInt callInt) {
		this.callInt = callInt;
	}
	CallInt callInt;
 

	 
	 
	

}
