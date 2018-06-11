package adapter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import util.EmotionUtils;
import util.MapListUtil;
import util.Tools;
import util.picasso.NetImage;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cc.R;
 
/**
 * @author Walker
 * @date 2017-3-21 下午2:30:44
 * Description: 聊天会话列表
 */
public   class AdapterLvSession extends BaseAdapter    {
	private Context context; // 运行上下文
	private List<Map<String, Object>>  listItems = null; // listview的数据集合
	private LayoutInflater listContainer; // 视图容器
	//控件集合实例
	private ViewHolderUser viewHolderUser ;
	//布局类型
    // // 自定义控件集合  布局类型
	public final class ViewHolderUser {
		public ImageView ivprofile; 
		public TextView tvusername; 
		public TextView tvmsg;
		public TextView tvtime;
		
		public TextView tvnum;
		
	}
	 
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		viewHolderUser = null;  

		//构建或者取出可复用布局
		if (convertView == null) { //若无可复用布局
				viewHolderUser = new ViewHolderUser();
				convertView = listContainer.inflate(R.layout.listview_adapter_session_item, null);	// 获取list_item布局文件的视图
				viewHolderUser.ivprofile = (ImageView) convertView .findViewById(R.id.ivprofile);
				viewHolderUser.tvtime = (TextView) convertView .findViewById(R.id.tvtime);
				viewHolderUser.tvusername = (TextView) convertView .findViewById(R.id.tvusername);
				viewHolderUser.tvmsg = (TextView) convertView .findViewById(R.id.tvmsg);
				viewHolderUser.tvnum = (TextView) convertView .findViewById(R.id.tvnum);
				
				convertView.setTag(viewHolderUser);// 设置控件集到convertView
		} else {//若有可复用布局
				viewHolderUser = (ViewHolderUser) convertView.getTag();
		}
		// 设置文字和图片和监听
		//	viewHolderUser.tvusername.setText("aaaaaaaaaaaaaaaaaa") ;

		viewHolderUser.tvusername.setText(MapListUtil.getList(listItems, position, "NAME")) ;
		//viewHolderUser.tvmsg.setText(MapListUtil.getList(listItems, position, "MSG")) ;
		if(MapListUtil.getList(listItems, position,"MSG").equals("text")){
			SpannableString spannableString = EmotionUtils.getEmotionContent(context,viewHolderUser.tvmsg,MapListUtil.getList(listItems, position, "MSG"));
			viewHolderUser.tvmsg.setText(spannableString);
		}else if(MapListUtil.getList(listItems, position,"MSG").equals("voice")){
			viewHolderUser.tvmsg.setText("[语音]");
		}else if(MapListUtil.getList(listItems, position,"MSG").equals("file")){
			viewHolderUser.tvmsg.setText("[文件]");
		}else if(MapListUtil.getList(listItems, position, "MSG").equals("photo")){
			viewHolderUser.tvmsg.setText("[图片]");
		}else {
			SpannableString spannableString = EmotionUtils.getEmotionContent(context,viewHolderUser.tvmsg,MapListUtil.getList(listItems, position, "MSG"));
			viewHolderUser.tvmsg.setText(spannableString);
		}
		
			
		viewHolderUser.tvtime.setText(Tools.formatL(new Timestamp(Tools.parseLong(MapListUtil.getList(listItems, position, "TIME")))));
		int t = Tools.parseInt( MapListUtil.getList(listItems, position, "NUM") );
		if(t <= 0){
			viewHolderUser.tvnum.setText( "") ;
			viewHolderUser.tvnum.setVisibility(View.INVISIBLE);
		}else if(t > 99){
			viewHolderUser.tvnum.setText( "99+") ;
			viewHolderUser.tvnum.setVisibility(View.VISIBLE);
		}else{
			viewHolderUser.tvnum.setText( ""+t) ;
			viewHolderUser.tvnum.setVisibility(View.VISIBLE);
		}
	 	NetImage.loadProfile(context, MapListUtil.getList(listItems, position, "PROFILEPATH"), viewHolderUser.ivprofile);
		 
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
		
	
	public AdapterLvSession(Context context, List<Map<String, Object>> listItems) {
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
