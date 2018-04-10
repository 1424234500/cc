package util.fragm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fragm.FragmentPhoto;

import util.ac.MyApplication;
import util.tools.AndroidTools;
import util.tools.Tools;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class BaseFragment extends Fragment{
	
	/**
	 * 头像选择,剪切,文件选择
	 */
	protected static final int ACTIVITY_RESULT_PROFILEWALL = 0;	
	protected static final int ACTIVITY_RESULT_PROFILE = 1;
	protected static final int ACTIVITY_RESULT_PROFILEWALL_CUT = 2;
	protected static final int ACTIVITY_RESULT_PROFILE_CUT = 3;
	protected static final int ACTIVITY_RESULT_PATH = 4;		//文件路径 图片/文件选取
	protected static final int ACTIVITY_RESULT_FILE = 5;		//文件选取
	protected static final int ACTIVITY_RESULT_TAKEPHOTO = 6;	//拍照

 
    public  void chosePhoto( ){
    	 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
         intent.addCategory(Intent.CATEGORY_OPENABLE);  
         intent.setType("image/*");  
         startActivityForResult(Intent.createChooser(intent, "选择图片"), ACTIVITY_RESULT_PATH );   
    }
    
    public  void takePhoto( String path ){
	  File temp = new File(path);
		Uri imageFileUri = Uri.fromFile(temp);//获取文件的Uri	 
		Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//跳转到相机Activity
		it.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);//告诉相机拍摄完毕输出图片到指定的Uri
		startActivityForResult(it, ACTIVITY_RESULT_TAKEPHOTO);
    }
    public  void choseFile(  ){
  	 Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
       intent.addCategory(Intent.CATEGORY_OPENABLE);  
       intent.setType("*/*");  
      startActivityForResult(Intent.createChooser(intent, "选择文件"), ACTIVITY_RESULT_PATH );   
    }
    //intent.setType(“image/*”);//选择图片  
    //intent.setType(“audio/*”); //选择音频  
    //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）  
    //intent.setType(“video/*;image/*”);//同时选择视频和图片  
      
  //相册、相机、文件 选择结果,子类重写相应操作结果处理函数即可
  	@Override
  	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
  		if(resultCode != Activity.RESULT_OK || data == null){
  			Tools.toast(getContext(), "操作取消");
  			return;
  		}
      	try{
          if (requestCode == ACTIVITY_RESULT_PATH ) { 
  	            Uri uri = data.getData();  
  				String path = AndroidTools.uri2FilePath(getContext(), uri);
  				resultChosePath(path);  				//调用子类重载函数
          } else if (requestCode == ACTIVITY_RESULT_TAKEPHOTO  ) {   
          	 	resultTakePhoto();
          } 
          
          
      	}  catch (Exception e) {
        	  e.printStackTrace();
        }
          
  	}  
	
	public void resultChosePath(String path){}
	public void resultTakePhoto( ){}
	
	
	
	
	
	
	
	
    private Activity activity;
    public Activity getAc(){
    	return activity;
    }
    public Context getContext(){
      //  Tools.tip("getContext activity ?? " + (activity == null?"null":"not null"));
        if(activity == null){
            return MyApplication.getInstance();
        }

        return activity;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
       // Tools.tip("onAttach?activity=" + (activity == null ? "null" : "not null"));

        this.activity = activity;//getActivity();
      //  Tools.tip("onAttach?getActivity()=" + (getActivity() == null ? "null" : "not null"));
    }
    
    
    
}
