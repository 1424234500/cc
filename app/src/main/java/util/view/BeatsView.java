package util.view;

import com.cc.Constant;
import com.cc.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class BeatsView extends ImageView implements OnClickListener {

	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			count ++;
			invalidate();
			//invalidate()必须要在UI主线程当中，如果不在UI主线程中，就要去调用postInValidate()】
		}
		
	};
	
    private Paint paint;
   int count;
	int maxCount;
    private Bitmap bitmap;
    private Bitmap bitmapBack;
    float dw;
    int dy, dx, sw, sh;
    int dd = 15;	//上下间隔空白
    int detaHz = 120;	//帧/s
    double detaCount = 0.012;	//每次多显示多少百分比，共此分之一帧
    double lowSpeed = 4;	//动画变换最慢的时候的快慢
    int waitSleep = 300;
    int mode = 1;	//1画出，0画退
    int showWidth ;
    float detaT ,detaY;
public BeatsView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setOnClickListener(this);
        
        
        Drawable drawable = getDrawable();  
        if (null != drawable) {  
        	bitmap = ((BitmapDrawable) drawable).getBitmap();  
        	bitmapBack = BitmapFactory.decodeResource(getResources(), R.color.black);
        	
        	sw = Constant.screenW;
            dw = (float)bitmap.getWidth() /  sw; //x满宽，放大dw倍
        	dy = (int) ((float)bitmap.getHeight() / dw);	   //图像的目标高度dh
        	sh = dy;
        	maxCount = (int) (1.0 / detaCount);
        	showWidth = (int) (sw * detaCount * funCount(maxCount) )+15 ;
        	detaT = (float) (showWidth * detaCount);
        	detaY = (float) (bitmap.getWidth() * detaCount );
        }
        
        startAnim();
    }

	public void startAnim(){
		count = 0;
		
        new Thread(){
        	public void run(){
        		boolean isShow = true;
        		while(isShow){
        			handler.sendEmptyMessage(0);
        			try {
						sleep(1000/detaHz);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        			if(count >= maxCount){
        				try {
							sleep(waitSleep);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
        				change();
        				isShow = false;
        			}
        			
        		}
        		
        	}
        }.start();
	}
 
    @Override
    protected void onDraw(Canvas canvas) {
    	
    	//背景
        paint.setColor(Color.BLACK);
    	canvas.drawRect(new Rect(0, 0, sw, sh + dd+dd), paint);

    	
    	Rect rectSrc;
    	Rect rectDest;
    	if(mode == 1){
    	     rectSrc = new Rect(0, 0,  (int) (detaY* funCount(count))  , bitmap.getHeight());  
             rectDest = new Rect(0, dd, (int) (detaT * funCount(count) ) , dd + sh);
    	}else{
	   	     rectSrc = new Rect(  (int) (detaY * funCount(count)), 0, bitmap.getWidth()  , bitmap.getHeight());  
	         rectDest = new Rect((int) (detaT * funCount(count) ), dd, showWidth , dd + sh);
    	}
       canvas.drawBitmap(bitmap, rectSrc, rectDest, paint);  
  
    }
    public double funCount(int count){
    	//return count;
    	return detaCount*detaCount*(4-lowSpeed)  *count*count*count + (-5.6 + 1.5*lowSpeed)*detaCount * count*count + (2.6-0.5*lowSpeed)* count;
    }
    
    public void change(){
    	mode = 1 - mode;
    	//lowSpeed = 10 - lowSpeed;
 
//    	if(mode == 0)
//    		bitmap  =BitmapFactory.decodeResource(getResources(), R.drawable.oneblack);
//    	else
//          bitmap  =BitmapFactory.decodeResource(getResources(), R.drawable.one);

        startAnim();
        
    }
    @Override
    public void onClick(View v) {
    	change();
    }
     
    
    //自定义view的宽高  
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
        
        setMeasuredDimension(sw , sh + dd * 2);  
        //自定义view的宽高时，不实用下面函数  ?
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);  
    }

     
}