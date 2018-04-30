package fragm;
 
import util.AndroidTools;
import util.fragm.BaseFragment;
import util.Tools;
import util.view.VoiceListener;
import util.view.VoiceListener.OnVoice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cc.R;
  
public class FragmentVoice extends BaseFragment   {  
	VoiceListener vl;
	
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidTools.life("FragmentVoice onCreate");
		
	}
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {  
    	View view = inflater.inflate(R.layout.layout_voice, container, false);  
		AndroidTools.life("FragmentVoice onCreateView");

    	vl = new VoiceListener(getContext(), view);
    	view.setOnTouchListener(vl);
    	vl.setOnVoice(onVoice);
    	vl.setFileBegin(begin);
    	
	    return view;
    }  
    OnVoice onVoice;
    public void setCall(OnVoice onVoice){
    	this.onVoice = onVoice;
    	AndroidTools.life("FragmentVoice setCall");

    }
    //设置存储文件名前缀
    public void setFileBegin(String toid) {
		begin = toid;
	} 
    String begin = "";
  
}  