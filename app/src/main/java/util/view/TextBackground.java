package util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cc.R;


 
/**
 * @author Walker
 * @date 2017-3-29 下午5:55:47
 * Description: 背景图片的文字气泡
 */
public class TextBackground extends LinearLayout  {
	private Context context = null;
	
	private TextView textView = null;
	 
	int num = 55; 

	public TextBackground(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View parentView = inflater.inflate(R.layout.item_redpoint_image_text, this, true);
		textView = (TextView)parentView.findViewById(R.id.tvnum);
	
	}
 
	//设置文本
	public void setNum(int num ){
		if(num <= 0){
			textView.setText(""); 
		}else{
			textView.setText(num+""); 
		}
	}
	 
	  

	public int getNum() {
		return this.num;
	}
 


}
