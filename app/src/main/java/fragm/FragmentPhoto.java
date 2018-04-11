package fragm;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.fragm.BaseFragment;
import util.Tools;
import util.view.HorizontalListView;
import adapter.AdapterAblum;
import adapter.AdapterAblum.OnChose;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cc.R;
  
public class FragmentPhoto extends BaseFragment     {  
  
	List<Map<String, String>> listPhoto = new ArrayList<Map<String, String>>();
	AdapterAblum adapterAblum;
	TextView tvok;
	TextView tvablum;
	View viewPhoto;
	
 
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tools.life("FragmentPhoto onCreate");
 
	}


	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {  	
		Tools.life("FragmentPhoto onCreateView");

    	viewPhoto = inflater.inflate(R.layout.layout_photo, null);
	   	     tvablum = ((TextView)viewPhoto.findViewById(R.id.tvablum));
	   	   tvok = ((TextView)viewPhoto.findViewById(R.id.tvok));
	   	   tvok.setOnClickListener(new OnClickListener() {
		   @Override
			public void onClick(View arg0) {
			   List<String> files = new ArrayList<String>();
				for(int i = 0; i < listPhoto.size(); i++){
					//chose,path
					if(listPhoto.get(i).get("chose").equals("true")){
						files.add(listPhoto.get(i).get("path"));
						listPhoto.get(i).put("chose", "false");
					}
				}
//				Object[] obj = listPhoto.toArray();
//				listPhoto.clear();//????????????
 				adapterAblum.notifyDataSetChanged();
				tvok.setClickable(false);
				tvok.setBackgroundResource( R.drawable.selector_button_login_disable);
			 
				 if(call != null){
					 call.onCall(files);
				 }
//				 for(int i = 0; i < obj.length; i++){
//					 listPhoto.add((Map<String, String>) obj[i]);
//				 }
				 
			}
   	   });
   	   
   	   HorizontalListView lvablum = ((HorizontalListView)viewPhoto.findViewById(R.id.lvablum));
   	   tvablum.setOnClickListener(new OnClickListener() {
   		@Override
   		public void onClick(View arg0) {
   			chosePhoto();
   		}
   	   });
   	   adapterAblum = new AdapterAblum(getContext(), listPhoto);
   	   lvablum.setAdapter(adapterAblum);
   	   adapterAblum.setOnChose(new OnChose(){
    	 public void onChose(int position){
    		   if(! tvok.isClickable()){
    			   tvok.setClickable(true);
    			   tvok.setBackgroundResource( R.drawable.selector_button_login);
    		   }
    	 }  
		@Override
		public void onInChose(int position) {
			for(int i = 0; i < listPhoto.size(); i++){
				//chose,path
				if(listPhoto.get(i).get("chose").equals("true")){
					return;
				}
			}
			tvok.setClickable(false);
			tvok.setBackgroundResource( R.drawable.selector_button_login_disable);
		  }
    	});
   	   
   	   openChosePhoto();
   	   
	   return viewPhoto;
    }  
    
    
 	public static String TAKEPHOTO = "";

	@Override
	public void resultChosePath(String path) {
		List<String> list = new ArrayList<String>();
		list.add(path);
		if(call != null){
			call.onCall(list);
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public void resultTakePhoto() {
		List<String> list = new ArrayList<String>();
		list.add(this.TAKEPHOTO);
		if(call != null){
			call.onCall(list);
		}
	}

	public void openChosePhoto(){
		//重构选择数据
		listPhoto.clear();
		//tvSend.setClickable(true);
		//tvSend.setBackgroundResource( R.drawable.selector_button_login);
		tvok.setClickable(false);
		tvok.setBackgroundResource(R.drawable.selector_button_login_disable);
	   
		Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	    Cursor mCursor = getContext().getContentResolver().query(mImageUri, null,
	                  MediaStore.Images.Media.MIME_TYPE + "=? or "  + MediaStore.Images.Media.MIME_TYPE + "=?",
	                  new String[] { "image/jpeg", "image/png" }, MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
	    if(mCursor == null){  return;   }
	    int i = 0;
	    while(mCursor.moveToNext() && i++ < 10){//只显示最多30张图片
	    	   Map<String, String> map = new HashMap<String, String>();
	    	   map.put("chose", "false");
	    	   map.put("path", mCursor.getString(mCursor .getColumnIndex(MediaStore.Images.Media.DATA)));
	    	   listPhoto.add(map);
	    }
	   // Tools.log(Tools.list2strings(listPhoto));
	    adapterAblum.notifyDataSetChanged();
	}
    
    
    
    
    
    
    
    Call call;
    public interface Call {
    	public void onCall(List<String> files);
    }
    public void setCall(Call call){
    	this.call = call;
    }
    
     
  
}  