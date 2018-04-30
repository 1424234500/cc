package util.view;

import util.picasso.NetImage;
import interfac.CallInt;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cc.R;

public class TopPanelImageTitleMenu extends RelativeLayout implements View.OnClickListener{


	Context context;
	private CallInt callback = null;
	
	int color1 =   R.color.white;
	int color2 = R.color.qqtoppanelblue;
	int img1 = R.drawable.profile;
	int img2 = R.drawable.profile;
	
	ImagePoint ipProfile;
	TextView tvMenu, tvTitle;
	 public TopPanelImageTitleMenu(Context context) {
        this(context, null);
    }
    public TopPanelImageTitleMenu(Context context, AttributeSet attrs,  int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
	public TopPanelImageTitleMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View parentView = inflater.inflate(R.layout.top_panel_image_title_menu, this, true);
			
		
		ipProfile = (ImagePoint)parentView.findViewById(R.id.ipprofile);
		ipProfile.setImageResource(R.drawable.profile);
	//	ipProfile.setShowPoint(true);
		ipProfile.setShowPoint(false);
		
		tvMenu = (TextView)parentView.findViewById(R.id.tvmenu);
		tvTitle = (TextView)parentView.findViewById(R.id.tvtitle);
		ipProfile.setOnClickListener(this);
	//	tvMenu.setOnClickListener(this);
	//	tvTitle.setOnClickListener(this);

	}
	public void setCallback(CallInt callback){
		this.callback = callback;
	}
	 
	//控件，处理，点击到某个id后返回给上级处理,并处理imageText的文字和图标替换，替换内容需要依靠上级传递下来
	@Override
	public void onClick(View view) {
		callback.call(view.getId());
	}
	
	public void setProfile(String path){
		NetImage.loadProfile(context, path, ipProfile.ivImage);
	}
	public void setProfile(int reseourceId){
		NetImage.loadProfile(context, reseourceId, ipProfile.ivImage);
	}
	
	
	public void setTitleAndMenu(String title, String menu, OnClickListener ocl){
		this.tvMenu.setText(menu);
		this.tvMenu.setOnClickListener(ocl);
		this.tvTitle.setText(title);

	}
	
 
}
