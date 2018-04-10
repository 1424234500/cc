package util.view;

import interfac.CallInt;

import java.util.ArrayList;
import java.util.List;

import util.tools.Tools;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.cc.R;


public class BottomControlPanel extends LinearLayout implements View.OnClickListener {
 

	Context context;
	private CallInt callback = null;
	private List<ImageText> its = new ArrayList<ImageText>();
	
	String[] texts = {"消息", "联系人", "匿名"};
	int color1 =   R.color.qqloginwhite, color2 = R.color.qqtoppanelblue;
	int img1s[] = {R.drawable.msg, R.drawable.contact, R.drawable.doll, R.drawable.face3  };
	int img2s[] = {R.drawable.msgblue, R.drawable.contactblue, R.drawable.dollblack, R.drawable.dollars18hello  };
	int ids[] =   {R.id.itmsg, R.id.itcontact, R.id.itdollar};

	public BottomControlPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View parentView = inflater.inflate(R.layout.bottom_control_panel_layout, this, true);
		 for(int i = 0; i < ids.length; i++){
			 ImageText it = (ImageText)parentView.findViewById(ids[i]);
			 it.setText(texts[i], color1, color2);
			 it.setImages(img1s[i], img2s[i]);
			 it.setOnClickListener(this);
			 its.add(it);
		 }
		Tools.out("Bottom on create count :"     );

	}
	public void setCallback(CallInt callback){
		this.callback = callback;
	}
	 
	//控件，处理，点击到某个id后返回给上级处理,并处理imageText的文字和图标替换，替换内容需要依靠上级传递下来
	@Override
	public void onClick(View view) {
		int i = 0;
		 for(i = 0; i < its.size(); i++){
			if(its.get(i).getId() == view.getId()){
				callback.call( view.getId() );	//选中页传递
				its.get(i).setChecked(true);
				//Tools.toast(context, its.get(i).getText());
			}else{
				its.get(i).setChecked(false);
			}
		 }
		
		 
	}
   
 



}
