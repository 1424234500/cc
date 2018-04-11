package util.view;

import interfac.CallInt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cc.R;

public class TopPanelReturnTitleMenu extends RelativeLayout implements View.OnClickListener{
	CallInt callback;
	public void setCallback(CallInt callback){
		this.callback = callback;
	}
	Context context;
	TextView tvMenu, tvTitle, tvReturn;
	View view;
	
	public TopPanelReturnTitleMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View parentView = inflater.inflate(R.layout.top_panel_return_title_menu, this, true);
			
		//top panel组件总布局引用，控制背景
		view = (View)parentView.findViewById(R.id.rl_view);
		
		tvMenu = (TextView)parentView.findViewById(R.id.tvmenu);
		tvReturn = (TextView)parentView.findViewById(R.id.tvreturn);
		tvTitle = (TextView)parentView.findViewById(R.id.tvtitle);
		
		tvReturn.setOnClickListener(this);
		tvMenu.setOnClickListener(this);
		tvTitle.setOnClickListener(this);
		

	} 
	 
	//控件，处理，点击到某个id后返回给上级处理,并处理imageText的文字和图标替换，替换内容需要依靠上级传递下来
	@Override
	public void onClick(View view) {
		if(callback != null)
			callback.call( view.getId());
	}
	
	//设置无背景透明模式？！
	public void setAlphaMode(boolean bool){
		this.view.getBackground().setAlpha(bool?255:0);
	}
	 
	
	public void setTitleReturnMenu(String title, String retur, String menu){
		this.tvMenu.setText(menu);
		this.tvReturn.setText(retur);
		this.tvTitle.setText(title);

	}

	public void setReturnTitle(String str) {
		this.tvReturn.setText(str);
	}public void setTitle(String str) {
		this.tvTitle.setText(str);
	}public void setMenu(String str) {
		this.tvMenu.setText(str);
	}
	public String getMenuText(){
		return this.tvMenu.getText().toString();
	}
 
}
