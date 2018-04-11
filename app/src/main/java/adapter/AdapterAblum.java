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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.cc.Constant;
import com.cc.R;

 
 
/**
 * @author Walker
 * @date 2017-4-20 下午10:00:47
 * Description: 图片选择相册
 */
public   class AdapterAblum extends BaseAdapter      {
	private Context context; // 运行上下文
	private List<Map<String, String>>  listItems = null; // listview的数据集合
	private LayoutInflater listContainer; // 视图容器
	//控件集合实例
	private ViewHolder viewHolder ;
	//布局类型
    final int TYPE_USER = 0;
    final int TYPE_GROUP = 1;
    // // 自定义控件集合  布局类型
	public final class ViewHolder {
		public ImageView ivphoto; 
		public CheckBox cbchose;
	} 
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		viewHolder = null;  
		
		//构建或者取出可复用布局
		if (convertView == null) { //若无可复用布局
			viewHolder = new ViewHolder();
			convertView = listContainer.inflate(R.layout.listview_adapter_ablum_item, null);	// 获取list_item布局文件的视图
			viewHolder.ivphoto = (ImageView) convertView .findViewById(R.id.ivphoto);
			viewHolder.cbchose = (CheckBox) convertView .findViewById(R.id.cbchose);
			
			convertView.setTag(viewHolder);// 设置控件集到convertView
		} else {//若有可复用布局
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 设置文字和图片和监听
		viewHolder.cbchose.setChecked(Tools.getListS(listItems, position,"chose").equals("true")) ;
		viewHolder.cbchose.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean bool) {
				listItems.get(position).put("chose", bool?"true":"false");
				if(onChose != null){
					if(bool)onChose.onChose(position);
					else onChose.onInChose(position);
				}
			}
		});
	
		//viewHolder.ivphoto.setImageBitmap(MyImage.getBitmapByDecodeFile(Tools.getListS(listItems, position,"path")));
		NetImage.loadLocalImgResize(context, Tools.getListS(listItems, position,"path"),Constant.ablumMaxH,Constant.ablumMaxH, viewHolder.ivphoto);
	 	
	 	
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
			 return -1;
		}
		
	
	public AdapterAblum(Context context, List<Map<String, String>> listItems) {
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

	
	public interface OnChose{
		public void onChose(int position);
		public void onInChose(int position);
	}
	public void setOnChose(OnChose onChose) {
		this.onChose = onChose;
	}
	OnChose onChose;
 

	 
	

}
