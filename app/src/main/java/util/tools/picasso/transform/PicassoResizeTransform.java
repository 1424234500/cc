package util.tools.picasso.transform;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;

import com.squareup.picasso.Transformation;


	//图片处理
	public class PicassoResizeTransform implements Transformation {  
		 
	    @Override  
	    public Bitmap transform(Bitmap source) {  
	        int widthLight = source.getWidth();  
	        int heightLight = source.getHeight();  
	  
	        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);  
	  
	        Canvas canvas = new Canvas(output);  
	        Paint paintColor = new Paint();  
	        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);  
	  
	        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));  
	  
	        canvas.drawRoundRect(rectF, widthLight / 5, heightLight / 5, paintColor);  
	  
	        Paint paintImage = new Paint();  
	        paintImage.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));  
	        canvas.drawBitmap(source, 0, 0, paintImage);  
	        source.recycle();  
	        return output;  
	    }  
	  
	    @Override  
	    public String key() {  
	        return "roundcorner";  
	    }  
	  
	  
	  
	}  