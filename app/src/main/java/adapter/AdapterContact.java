package adapter;

import java.util.List;
import java.util.Map;

import util.tools.Tools;
import util.tools.picasso.NetImage;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cc.R;


 
/**
 * @author Walker
 * @date 2017-3-3 下午2:27:37
 * Description: 查找cc用户结果lv适配器
 */
public   class AdapterContact extends  BaseExpandableListAdapter      {
	private Context context; // 运行上下文
	private List<Map<String, Object>>  listItems = null; //子list的数据集合，各种类型，有序，阶段性分布
	private List<Map<String, Object>>  listType = null; // 父list数据集合，map包含 NAME名字,NUM子条数,ONNUM在线数量,START子list起始位置
	
	
	private LayoutInflater listContainer; // 视图容器
	
	//控件集合实例
	private ViewHolder viewHolder ;
	private ViewHolderUser viewHolderUser ;
	private ViewHolderGroup viewHolderGroup ;
	//布局类型
	  final int TYPE_USER = 0;
	  final int TYPE_GROUP = 1;
	  final int TYPE_3 = 2;
	//控件集合
	public final class ViewHolder {  	//分组信息
		public ImageView ivicon;
		public TextView tvusername; 
		public TextView tvonnum;  
		public TextView tvnum; 
	}
	public final class ViewHolderUser {  //用户
 		public ImageView ivprofile; 
		public TextView tvusername; 
		public TextView tvstatus;  
		public TextView tvsign; 
	}
	public final class ViewHolderGroup { //群组
		public ImageView ivprofile; 
		public TextView tvusername;  
	}
	public AdapterContact(Context context,List<Map<String, Object>> listType, List<Map<String, Object>> listItems) {
		this.context = context;
		this.listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.listItems = listItems;
		this.listType = listType;
	}
	  
	

    //  获得某个父项的某个子项  
    @Override  
    public Object getChild(int groupPosition, int childPos) {  
        return  listItems.get((Tools.parseInt( (Tools.getList(listType, groupPosition, "START").toString())) + childPos));  
    }  
  
    //  获得父项的数量  
    @Override  
    public int getGroupCount() {  
        return listType.size();  
    }  
  
    //  获得某个父项的子项数目  
    @Override  
    public int getChildrenCount(int groupPosition) {  
        return Tools.parseInt( (Tools.getList(listType, groupPosition, "NUM").toString()));  
    }  
  
    //  获得某个父项  
    @Override  
    public Object getGroup(int groupPosition) {  
        return   listType.get(groupPosition) ;  
    }  
  
    
    
    //  获得某个父项的id  
    @Override  
    public long getGroupId(int groupPosition) {  
        return groupPosition;  
    }  
    //	获得某个父项的某个子项类型
    @Override
	public int getChildType(int groupPositionition, int childPosition) {
    	String type = listItems.get( 
    			childPosition + Tools.parseInt(  listType.get(groupPositionition).get("START").toString() ) 
    	).get("TYPE").toString();
    	
	   	 if(type .equals("user")){
			 return TYPE_USER;	
		 }else if( type.equals("group")){
			 return TYPE_GROUP;	
		 } 
		 return -1;
    }
    //	获得子类型种类数量
	@Override
	public int getChildTypeCount() {
		return 2;
	}
	//获得分组类型
	@Override
	public int getGroupType(int groupPosition) { 
		return super.getGroupType(groupPosition);
	}

	@Override
	public int getGroupTypeCount() {
		// TODO 自动生成的方法存根
		return 1;
	}

	//  获得某个父项的某个子项的id  
    @Override  
    public long getChildId(int groupPosition, int childPos) {  
        return childPos;  
    }  
  
    //  按函数的名字来理解应该是是否具有稳定的id，这个方法目前一直都是返回false，没有去改动过  
    @Override  
    public boolean hasStableIds() {  
        return false;  
    }  
  
    
    
    
    //  获得父项显示的view  
    @Override  
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup viewGroup) {  
    	viewHolder = null; //根据item_layout构建的一条数据
		if (convertView == null) {
			viewHolder = new ViewHolder();
			// 获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listview_adapter_contact_item, null);// 获取控件对象
			viewHolder.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
			viewHolder.tvonnum = (TextView) convertView .findViewById(R.id.tvonnum);
			viewHolder.tvnum = (TextView) convertView .findViewById(R.id.tvnum);
			viewHolder.ivicon = (ImageView) convertView .findViewById(R.id.ivicon);
			
			convertView.setTag(viewHolder);// 设置控件集到convertView
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		} 
		 
		viewHolder.tvusername.setText(Tools.getList(listType, groupPosition, "USERNAME").toString());
		viewHolder.tvonnum.setText(Tools.getList(listType, groupPosition, "ONNUM").toString());
		viewHolder.tvnum.setText(Tools.getList(listType, groupPosition, "NUM").toString());
		viewHolder.ivicon .setBackgroundResource(isExpanded? R.drawable.sanjiaod : R.drawable.sanjiaor);
		
		convertView.setPadding(0,20,0,0);
    
		 
		return convertView;
    }  
  
    //  获得子项显示的view  
    @Override  
    public View getChildView(int groupPosition, int childPos, boolean b, View convertView, ViewGroup viewGroup) {  
    	
    	viewHolderUser = null;  
		viewHolderGroup = null;  
		int type = getChildType(groupPosition, childPos);	//得到No.i条数据布局类型
		int position = childPos + Tools.parseInt( Tools.getList(listType, groupPosition, "START").toString()) ;
		//构建或者取出可复用布局
		if (convertView == null) { //若无可复用布局
			if(type== TYPE_USER){
				viewHolderUser = new ViewHolderUser();
				convertView = listContainer.inflate(R.layout.listview_adapter_contact_user_item, null);	// 获取list_item布局文件的视图
				//viewHolderUser.ivsex = (ImageView) convertView .findViewById(R.id.ivsex);
				viewHolderUser.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolderUser.tvsign = (TextView) convertView .findViewById(R.id.tvsign);
				viewHolderUser.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
				viewHolderUser.tvstatus = (TextView) convertView .findViewById(R.id.tvstatus);
				convertView.setTag(viewHolderUser);// 设置控件集到convertView
			}else if(type== TYPE_GROUP){
				viewHolderGroup = new ViewHolderGroup();
				convertView = listContainer.inflate(R.layout.listview_adapter_contact_group_item, null);// 获取list_item布局文件的视图
				viewHolderGroup.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolderGroup.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
			
				convertView.setTag(viewHolderGroup);	// 设置控件集到convertView
			}
		} else {//若有可复用布局
			if(type== TYPE_USER){
				viewHolderUser = (ViewHolderUser) convertView.getTag();
			}else if(type== TYPE_GROUP){
				viewHolderGroup = (ViewHolderGroup) convertView.getTag();
			}
		}
		// 设置文字和图片和监听
		if(type== TYPE_USER){ 
			viewHolderUser.tvusername.setText(Tools.getList(listItems, position, "NAME").toString()) ;
			viewHolderUser.tvsign.setText(Tools.getList(listItems, position, "SIGN").toString()) ;
			viewHolderUser.tvstatus.setText(Tools.getList(listItems, position, "STATUS").toString()) ;
		 	NetImage.loadProfile(context, Tools.getList(listItems, position, "PROFILEPATH").toString(), viewHolderUser.ivprofile);
		 	//NetImage.loadImage( context,Tools.getList(listItems, position, "SEX").toString().equals("男") ? R.drawable.boy:R.drawable.girl, viewHolderUser.ivsex);
		}else if(type== TYPE_GROUP){  
			viewHolderGroup.tvusername.setText(Tools.getList(listItems, position, "USERNAME").toString()) ;
		 	NetImage.loadProfile(context, Tools.getList(listItems, position, "PROFILEPATH").toString(), viewHolderGroup.ivprofile);
		}
		 
		return convertView; 
    }  
  
    //  子项是否可选中，如果需要设置子项的点击事件，需要返回true  
    @Override  
    public boolean isChildSelectable(int i, int i1) {  
        return true;  
    }




    
    
     
	


	 
	

}
