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
 * @date 2017-3-1 上午10:26:27
 * Description: 组装图片文字
 */
public class ImageText extends LinearLayout  {
	private Context context = null;
	private ImagePoint imagePoint = null;
	private TextView textView = null;
	
	
	private  static int DEFAULT_IMAGE_WIDTH = 90;
	private  static int DEFAULT_IMAGE_HEIGHT = 90;
	int imgs[] = new int[2];
	int colors[] = new int[2];
	String text = "tip";
	
	 

	public ImageText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View parentView = inflater.inflate(R.layout.image_text_item, this, true);
		imagePoint = (ImagePoint)parentView.findViewById(R.id.image_iamge_text);
		textView = (TextView)parentView.findViewById(R.id.text_iamge_text);
	
	}
	//设置图片
	public void setImages(int img1, int img2){
		imgs[0] = img1;
		imgs[1] = img2;
		if(imagePoint != null){
			imagePoint.setImageResource(img1);
			setImageSize(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
		}
	}
	//设置文本
	public void setText(String str, int color1, int color2 ){
		colors[0] = color1;
		colors[1] = color2;
		text = str;
		textView.setText(str);
		textView.setTextColor( color1);
	}
	
	public  void setImageSize(int w, int h){
		this.DEFAULT_IMAGE_HEIGHT = h;
		this.DEFAULT_IMAGE_WIDTH = w;
		if(imagePoint != null){
			ViewGroup.LayoutParams params = imagePoint.getLayoutParams();
			params.width = w;
			params.height = h;
			imagePoint.setLayoutParams(params);
		}
	}
	  
	public void setChecked(boolean flag){
		int i = flag?1:0;
		
		imagePoint.setImageResource(imgs[i]);
		setImageSize(DEFAULT_IMAGE_WIDTH, DEFAULT_IMAGE_HEIGHT);
		imagePoint.setShowPoint(false);
		textView.setTextColor( colors[i]);
		textView.setText(text);

	}

	public String getText() {
		return this.text;
	}

	public void setShowPoint(boolean flag){
		imagePoint.setShowPoint(flag);
	}


}
