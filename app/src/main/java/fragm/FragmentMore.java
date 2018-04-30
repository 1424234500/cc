package fragm;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.MapListUtil;
import util.fragm.BaseFragment;
import util.Tools;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.cc.R;
  
public class FragmentMore extends BaseFragment implements  OnItemClickListener  {  
   
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {  
    	 View view = inflater.inflate(R.layout.layout_more, container, false);  
           
    	   gview = (GridView) view.findViewById(R.id.gview);
           //新建List
           listItems = new ArrayList<Map<String, Object>>();
           //获取数据
           listItems = getData();
           //新建适配器
           String [] from ={"image","text"};
           int [] to = {R.id.iv,R.id.tv};
           sim_adapter = new SimpleAdapter(getContext(), listItems, R.layout.item_image_text, from, to);
           //配置适配器
           gview.setAdapter(sim_adapter);
           gview.setOnItemClickListener(this);
	     return view;
    }  
	static int ACTIVITY_RESULT_FILE = 1;

    private GridView gview;
    private List<Map<String, Object>> listItems;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = { R.drawable.icon_filetype_dir};
    private String[] iconName = { "文件" };

    public List<Map<String, Object>> getData(){        
    	List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            listItems.add(map);
        }
            
        return listItems;
    }
     
    Call call;
    public void setCall(Call call){
    	this.call = call;
    }
    public interface Call {
    	public void onChoseFile(String path);
    }
   

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int i, long l) {
		if(call != null){
			if(MapListUtil.getList(listItems, i, "text").equals("文件")){
				choseFile();
			}
		}
	}


	@Override
	public void resultChosePath(String path) {
		if(call != null){
			call.onChoseFile(path);
		}
	}  
  
}  