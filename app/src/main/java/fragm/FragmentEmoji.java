package fragm;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cc.Constant;

import util.fragm.BaseFragment;
import util.tools.EmotionUtils;
import util.Tools;
import adapter.AdapterGvEmoji;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
  
public class FragmentEmoji extends BaseFragment implements  OnItemClickListener  
{  
  
       
    
	//name,id
	List<Map<String, Object>> listEmoji;
	AdapterGvEmoji adapterGridEmoji;
	
	
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tools.life("FragmentEmoji onCreate");
		listEmoji = new ArrayList<Map<String, Object>>();
	   	 Map<String, Object> map;
		 Map<String, Integer> emojiMap =  EmotionUtils.getEmojiMap();
		 Object[] keys = emojiMap.keySet().toArray();
		 for(int i = 0; i < keys.length; i++){
			 map = new HashMap<String, Object>();
			 map.put("name", "" + keys[i] );
			 map.put("id", "" + emojiMap.get(keys[i]));
			 listEmoji.add(map);  
		 }  
		
		
		
		
	}


	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {  
//    	   View view = inflater.inflate(R.layout.fragment_two, container, false);  
//           mBtn = (Button) view.findViewById(R.id.id_fragment_two_btn);  
		Tools.life("FragmentEmoji onCreateView");

		 AdapterGvEmoji adapterGridEmoji = new AdapterGvEmoji(getContext(), listEmoji);
//		 Tools.tip("emoji. getContext=" + (getContext()==null?"null":"not null"));
//		 Tools.tip("emoji. getActivity=" + (getActivity()==null?"null":"not null"));
		 
		 GridView viewEmoji = new GridView( getContext() );
		 viewEmoji.setNumColumns(7);
	     viewEmoji.setAdapter(adapterGridEmoji);
	     viewEmoji.setOnItemClickListener(this);
	     
	     return viewEmoji;
    }  
    
    Call call;
    public interface Call {
    	public void onCall(SpannableString spannableString);
    }
    public void setCall(Call call){
    	this.call = call;
    }
    
    
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int i, long l) {
		if(call != null){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Tools.parseInt( Tools.getList(listEmoji, i, "id").toString()) );
            bitmap = Bitmap.createScaledBitmap(bitmap, Constant.emojiWH, Constant.emojiWH, true);
            ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);
            SpannableString spannableString = new SpannableString(Tools.getList(listEmoji, i, "name").toString());
            spannableString.setSpan(imageSpan, 0, Tools.getList(listEmoji, i, "name").toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
          
			call.onCall(spannableString );
		}
	}  
  
}  