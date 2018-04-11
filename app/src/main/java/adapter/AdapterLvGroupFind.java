package adapter;

import java.util.List;
import java.util.Map;

import util.Tools;
import util.tools.picasso.NetImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cc.R;


 
/**
 * @author Walker
 * @date 2017-3-3 下午2:27:37
 * Description: 查找cc用户结果lv适配器
 */
public   class AdapterLvGroupFind extends BaseAdapter  {
	private Context context; // 运行上下文
	private List<Map<String, Object>>  listItems = null; //数据集合
	private LayoutInflater listContainer; // 视图容器
	private ListItemView listItemView ;		//视图
	private int layoutId = R.layout.listview_adapter_find_group_item;	//布局
	
	public final class ListItemView { //布局控件集合
		public ImageView ivprofile; 
		public TextView tvnum;
		public TextView tvusername; 
		public TextView tvid; 
		public TextView tvsign; 
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int posi = position;
		// 自定义视图
		listItemView = null; //根据item_layout构建的一条数据
		if (convertView == null) {
			listItemView = new ListItemView();
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listview_adapter_find_group_item, null);
			
			
			
			// 获取控件对象
			listItemView.tvnum = (TextView) convertView .findViewById(R.id.tvnum);
			listItemView.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
			listItemView.tvid = (TextView) convertView .findViewById(R.id.tvid);
			listItemView.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
			listItemView.tvsign = (TextView) convertView .findViewById(R.id.tvsign);
			// 设置文字和图片
			listItemView.tvid.setText(Tools.getList(listItems, position, "ID").toString()) ;
			listItemView.tvusername.setText(Tools.getList(listItems, position, "USERNAME").toString()) ;
			listItemView.tvsign.setText(Tools.getList(listItems, position, "SIGN").toString()) ;
			listItemView.tvnum.setText(Tools.getList(listItems, position, "NUM").toString()) ;
		 	NetImage.loadProfile(context, Tools.getList(listItems, position, "PROFILEPATH").toString(), listItemView.ivprofile);

		 	
		 	
		 	 
			// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
	  
		return convertView;
	}

	
	
	
	
	public AdapterLvGroupFind(Context context, List<Map<String, Object>> listItems) {
		this.context = context;
		listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = listItems;
		
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int i) {
		return listItems.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}
	
	
}
