package util.view;


import com.cc.Constant;
import com.cc.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.view.View;

public class DialogImageShow extends Dialog {  
	ImageShowView isv;
	
    public DialogImageShow(Context context) {  
        super(context, R.style.BlackBackDialog);
		isv = new ImageShowView(context);
        setContentView(isv );  
        android.view.WindowManager.LayoutParams p = this.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = Constant.screenW; //宽度设置为屏幕的0.5
        p.height = Constant.screenH; //宽度设置为屏幕的0.5

        this.getWindow().setAttributes(p); //设置生效
    }
    public DialogImageShow(Context context, String path) {  
    	this(context);
    	
    	isv.setBitmap(BitmapFactory.decodeFile(path));
     
    }
    
      
} 