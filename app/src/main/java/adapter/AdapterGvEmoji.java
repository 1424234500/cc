package adapter;

import java.util.List;
import java.util.Map;

import util.MapListUtil;
import util.Tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cc.R;

public class AdapterGvEmoji extends BaseAdapter{
	//private Context context; // 运行上下文
	private List<Map<String, Object>>  listItems = null; // listview的数据集合
	private LayoutInflater listContainer; // 视图容器
	//控件集合实例
	private ViewHolderUser viewHolderEmoji ;
	//布局类型
    // // 自定义控件集合  布局类型
	public final class ViewHolderUser {
		public ImageView ivEmoji;  
	}
	 
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		viewHolderEmoji = null;  

		//构建或者取出可复用布局
		if (convertView == null) { //若无可复用布局
			viewHolderEmoji = new ViewHolderUser();
			convertView = listContainer.inflate(R.layout.gridview_emoji_item, null);	// 获取list_item布局文件的视图
			viewHolderEmoji.ivEmoji = (ImageView) convertView .findViewById(R.id.ivemoji);
			
			convertView.setTag(viewHolderEmoji);// 设置控件集到convertView
		} else {//若有可复用布局
			viewHolderEmoji = (ViewHolderUser) convertView.getTag();
		}
		// 设置文字和图片和监听
		//Tools.log(listItems.get(position).get("id").toString() + " | " + R.drawable.at);
		viewHolderEmoji.ivEmoji.setImageResource(Tools.parseInt( MapListUtil.getList(listItems, position, "id").toString() ));
		//viewHolderEmoji.ivEmoji.setImageResource(R.drawable.at);
		
		return convertView; 
	}
	
	//必须实现，通知adapter有几种布局类型
	@Override
	public int getViewTypeCount() {
		return 1;
	}
	//必须实现，让adapter可控布局类型
	@Override
	public int getItemViewType(int position) {
		 return 1;
	}
		
	
	public AdapterGvEmoji(Context context, List<Map<String, Object>> listItems) {
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