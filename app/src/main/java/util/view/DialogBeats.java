package util.view;


import com.cc.Constant;
import com.cc.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;

public class DialogBeats extends Dialog {  
 
	
    public DialogBeats(Context context) {  
        super(context, R.style.NoTitleDialog);
        setContentView(R.layout.dialog_beats);
//        setContentView(new android.widget.ProgressBar(context));
        
        android.view.WindowManager.LayoutParams p = this.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = Constant.screenW; //宽度设置为屏幕的0.5
        this.getWindow().setAttributes(p); //设置生效
        
    }

	 
} 