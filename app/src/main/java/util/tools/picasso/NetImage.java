package util.tools.picasso; 

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import util.tools.MyImage;
import util.Tools;
import util.tools.picasso.transform.PicassoRoundTransform;

import com.cc.Constant;
import com.cc.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


public class NetImage {  
	
	public static void pause(Context context){
		Picasso.with(context).pauseTag(context);
	}
	public static void resume(Context context){
		Picasso.with(context).resumeTag(context);
	}
	public static void loadProfile(Context context, int id, ImageView imageView){
		Picasso .with(context) .load(id).placeholder(R.drawable.loading) 
		.error(R.drawable.loaderror)
		.transform( new PicassoRoundTransform()  )
		 .into(imageView);
	}
	public static void loadProfile(Context context, String url, ImageView imageView){
		if(url == null || url.equals("") || url.equals(" ")){
			loadImage(context, R.drawable.loading, imageView);
			return;
		}
		url = Constant.profileHttp() + url;
		//Tools.tip(" profile url=" + url);
		if(url.length() > 4 && url.substring(0, 4).toLowerCase().equals("http")){	//网络图片
			//Tools.out(url);
			Picasso .with(context) .load(url).placeholder(R.drawable.loading) 
			.error(R.drawable.loaderror)
			.transform( new PicassoRoundTransform()  )
			.into(imageView);
		}else{//本地图片
			//Tools.out("picasso访问本地图片："+url);
			Picasso .with(context) .load(new File(url)).placeholder(R.drawable.loading) 
			.error(R.drawable.loaderror)
			.transform( new PicassoRoundTransform()  )
			 .into(imageView);
		}
	}

	public static void loadProfileWallGroup(Context context, String url, ImageView imageView){
		if(url == null || url.equals("") || url.equals(" ")){
			loadImage(context, R.drawable.loading, imageView);
			return;
		}
		url = Constant.profileHttp() + url;
		//Tools.tip(" loadProfileWall url=" + url);

		if(url.length() > 4 && url.substring(0, 4).toLowerCase().equals("http")){	//网络图片
			//Tools.out(url);
			Picasso .with(context) .load(url).placeholder(R.drawable.loading) 
			.error(R.drawable.loaderror)
			.into(imageView);
		}else{//本地图片
			//Tools.out("picasso访问本地图片："+url);
			Picasso .with(context) .load(new File(url)).placeholder(R.drawable.loading) 
			.error(R.drawable.loaderror)
			.transform( new PicassoRoundTransform()  )
			 .into(imageView);
		}
	}
	public static void loadProfileWall(Context context, String url, ImageView imageView){
		if(url == null || url.equals("") || url.equals(" ")){
			loadImage(context, R.drawable.loading, imageView);
			return;
		}
		url = Constant.profileWallHttp() + url;
		//Tools.tip(" loadProfileWall url=" + url);

		if(url.length() > 4 && url.substring(0, 4).toLowerCase().equals("http")){	//网络图片
			//Tools.out(url);
			Picasso .with(context) .load(url).placeholder(R.drawable.loading) 
			.error(R.drawable.loaderror)
			.into(imageView);
		}else{//本地图片
			//Tools.out("picasso访问本地图片："+url);
			Picasso .with(context) .load(new File(url)).placeholder(R.drawable.loading) 
			.error(R.drawable.loaderror)
			.transform( new PicassoRoundTransform()  )
			 .into(imageView);
		}
	}
	public static void loadNetImageInto(Context context, String url, ImageView imageView){
		if(url == null || url.equals("") || url.equals(" ")){
			loadImage(context, R.drawable.loading, imageView);
			return;
		}
		
		if(url.length() > 4 && url.substring(0, 4).toLowerCase().equals("http")){	//网络图片
			//Tools.out(url);
			Picasso .with(context) .load(url).placeholder(R.drawable.loading) 
			.error(R.drawable.loaderror)
			.into(imageView);
		}else{//本地图片
			Tools.out("picasso访问本地图片："+url);
			Picasso .with(context) .load(new File(url)).placeholder(R.drawable.loading) .error(R.drawable.loaderror) .into(imageView);
		}
		
		
	}
	
	
	//加载本地图片并放缩
	public static void loadLocalImgResize(Context context, String url, ImageView imageView){
		loadLocalImgResize(context, url, Constant.ablumMaxH, imageView);
	}
	public static void loadLocalImgResize(Context context, String url, final int maxHeight, ImageView imageView){
	
//		//高速加载文件oom？
//		 BitmapFactory.Options opt = new BitmapFactory.Options();    
//    	 opt.inPreferredConfig = Bitmap.Config.RGB_565;     
//    	 opt.inPurgeable = true;    
//    	 opt.inInputShareable = true; 
//    	 opt.inJustDecodeBounds = true;  //设为true时，构造出的bitmap=null，单opt会被赋值长宽等配置信息，但比较快，设为false时，才有图片  
//
//         BitmapFactory.decodeFile(url, opt);
//         int w = opt.outWidth;
//         int h = opt.outHeight;
//        float ra = MyImage.calculateInSampleSizeFloat( opt, maxHeight, maxHeight);
//		w *= ra;
//		h *= ra;
//		
//		loadLocalImgResize(context, url, w, h, imageView);
		
 //		loadLocalImgResize(context, url,maxHeight, maxHeight, imageView);
		 
		Picasso.with(context) 
		.load(new File(url))
		.placeholder(R.drawable.loading)
	    .error(R.drawable.loaderror)
	    .transform(new Transformation() {
			@Override
			public Bitmap transform(Bitmap source) {
				int h = source.getHeight();
				int w = source.getWidth();
				float ra = MyImage.calculateInSampleSizeFloat(w, h, maxHeight, maxHeight);
				w /= ra;
				h /= ra;
			
	            Bitmap result = Bitmap.createScaledBitmap(source, w, h, false);
	            if (result != source) {
	                // Same bitmap is returned if sizes are the same
	                source.recycle();
	            }
	            return result;			
		    }
			@Override
			public String key() {
				return "key";
			}
		})
	    .into(imageView);
	}
	public static void loadLocalImgResize(Context context, String url, int width, int height, ImageView imageView){
		Picasso.with(context) 
		.load(new File(url))
		.placeholder(R.drawable.loading)
	    .error(R.drawable.loaderror)
	    .resize(width, height)
	    .centerCrop().into(imageView);
	}
	
	public static void loadImage(Context context,int resourceId, ImageView imageView){
		Picasso  .with(context)  .load(resourceId) .placeholder(R.drawable.loading)
	    .error(R.drawable.loaderror) .into(imageView);
	}
	public static void loadImage(Context context,int resourceId, int width, int height, ImageView imageView){
		Picasso  .with(context)  .load(resourceId) .placeholder(R.drawable.loading)
	    .error(R.drawable.loaderror) 
		  .resize(width, height)
		  .centerCrop().into(imageView);
	}
	
	 
	
	public static Bitmap getNetImage(Context context, String url)  {
		try {
			return Picasso .with(context) .load(url).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public static void clear(Context context, String url) {
		Picasso.with(context).invalidate(url);
	}
	 
	 
	 
}