package adapter;

import interfac.InterfaceOkOrDel;

import java.util.List;
import java.util.Map;

import util.tools.Tools;
import util.tools.picasso.NetImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cc.R;
//登陆 输入框 的下拉lv适配器,暴露接口 回调函数
public abstract class AdapterLvIds extends BaseAdapter implements InterfaceOkOrDel{
	private Context context; // 运行上下文
	List<Map<String, Object>>  listItems = null; // listview的数据集合
	private LayoutInflater listContainer; // 视图容器

	// private boolean[] hasChecked; //记录商品选中状态

	public final class ListItemView { // 自定义控件集合
		public ImageView ivprofile; 
		public ImageView ivdel;
		public TextView tvid; 
		// public CheckBox check;
		// public Button detail;
	}

	public AdapterLvIds(Context context, List<Map<String, Object>> liststr) {
		this.context = context;
		listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = liststr;
	 
	}

	@Override
	public int getCount() {
		// TODO 自动生成的方法存根
		return listItems.size();
	}

	@Override
	public Object getItem(int i) {
		// TODO 自动生成的方法存根
		return listItems.get(i);
	}

	@Override
	public long getItemId(int i) {
		// TODO 自动生成的方法存根
		return 0;
	}
	ListItemView listItemView ;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final int posi = position;
		// 自定义视图
		listItemView = null; //根据item_layout构建的一条数据
		if (convertView == null) {
			listItemView = new ListItemView();
			
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listview_popup_item, null);
			// 获取控件对象
			listItemView.ivdel = (ImageView) convertView .findViewById(R.id.ivdel);
			listItemView.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
			listItemView.tvid = (TextView) convertView .findViewById(R.id.tvid);
			
			
		// 设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}
	 
		
		// 设置文字和图片
	 	listItemView.tvid.setText(Tools.getList(listItems, position, "id").toString()) ;
		
		//监听回调？？
		listItemView.ivdel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//??
				OnDel( listItems.get( posi ));
			}
		});
		listItemView.ivprofile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				OnOk(  listItems.get( posi ));
			}
		});
		listItemView.tvid.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				OnOk(  listItems.get( posi ));
			}
		});
	 	NetImage.loadProfile(context, Tools.getList(listItems, position, "profilepath").toString(), listItemView.ivprofile);
	
		 
		return convertView;
	}

}
