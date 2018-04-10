package util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cc.R;


 public class ChatView extends LinearLayout implements OnClickListener,interfac.CallInt{
    private ImageView ivvoice;
    private ImageView ivphoto;
    private ImageView ivgraph;
    private ImageView ivemoji;
    private ImageView ivmore;
    
    
    ImageView ivs[];
    int img1s[] = {R.drawable.voice, R.drawable.photo, R.drawable.graph, R.drawable.emoji, R.drawable.more  };
    int img2s[] = {R.drawable.voiceblue, R.drawable.photoblue, R.drawable.graphblue, R.drawable.emojiblue, R.drawable.moreblue  };
	
    int nowChoseId = 0;
    
    
    public ChatView(Context context) {
        super(context);
        initView(context);
    }

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context){
       View view = LayoutInflater.from(context).inflate(R.layout.item_chat, this, true);
        ivphoto= (ImageView) view.findViewById(R.id.ivphoto);
        ivvoice= (ImageView)view. findViewById(R.id.ivvoice);
        ivgraph= (ImageView) view.findViewById(R.id.ivgraph);
        ivemoji= (ImageView) view.findViewById(R.id.ivemoji);
        ivmore= (ImageView)view. findViewById(R.id.ivmore);
        
        ivs = new ImageView[5];
        ivs[0] = ivvoice;
        ivs[1] = ivphoto;
        ivs[2] = ivgraph;
        ivs[3] = ivemoji;
        ivs[4] = ivmore;
        for(int i = 0; i < ivs.length; i++){
        	ivs[i].setOnClickListener(this);
        }
        		
    }
    public void close() {
    	nowChoseId = 0;
		call(0);
	}

	@Override
	public void onClick(View v) {
		call(v.getId());
	}
	@Override
	public void call(int id) {
		for(int i = 0; i < ivs.length; i++){
			if(id == ivs[i].getId()){
				if(nowChoseId == id){//重复点，关闭
					if(this.onControl != null){
						onControl.onClose(); 
					}
					nowChoseId = 0;
					//ivs[i].setImageResource(img1s[i]);
				}else{//从其它切换到，从关闭到打开
				//	ivs[i].setImageResource(img2s[i]);
					nowChoseId = id;
					if(this.onControl != null){
						onControl.onOpen(id); 
					}
				} 
				
			}else{
				//ivs[i].setImageResource(img1s[i]);
			}
		} 
	}
    OnControl onControl;

    public void clearId(){
		nowChoseId = 0;

    	for(int i = 0; i < ivs.length; i++){
			ivs[i].setImageResource(img1s[i]);
		} 
    }
	public int setNowId(int id){
		nowChoseId = id;
		return nowChoseId;
	} 
	public int getNowId(){
		return nowChoseId;
	} 
	public void setOnControl(OnControl onControl){
		this.onControl = onControl;
	}
	public interface OnControl{
	  public void onClose();
	  public void onOpen(int id);
    }
	
}