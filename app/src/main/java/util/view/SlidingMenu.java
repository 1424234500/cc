package util.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class SlidingMenu extends ViewGroup {

	private static final String TAG = SlidingMenu.class.getName();

	private enum Scroll_State {
		Scroll_to_Open, Scroll_to_Close;
	}

	// ///////////////////////////////////////////////////////////////
	public int menuwidth = 5; // /   菜单栏宽度 
	public int menuslide = 4; // /超过 menuslide/10  进入/退出菜单栏需要滑动距离
	public int menuanimationtime = 200; // ms 菜单移动的动画持续时间

	// /////////////////////////////////////
	private Scroll_State state;
	private int mMostRecentX;
	private int downX;
	private boolean isOpen = false;

	public View menu;
	public View mainView;

	private Scroller mScroller;

	private OnSlidingMenuListener onSlidingMenuListener;

	
	//一个上转为context，一个上转为interface
	public SlidingMenu(Context context, View main, View menu) {
		super(context);
		// TODO Auto-generated constructor stub
		setMainView(main);
		setMenu(menu);
		mScroller = new Scroller(context);
	}

	@Override
	protected void onLayout(boolean arg0, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		mainView.layout(l, t, r, b);
		menu.layout(-menu.getMeasuredWidth(), t, 0, b);
	}

	public void setMainView(View view) {
		mainView = view;
		addView(mainView);
	}

	public void setMenu(View view) {
		menu = view;
		addView(menu);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mainView.measure(widthMeasureSpec, heightMeasureSpec);
		menu.measure(widthMeasureSpec * menuwidth / 10, heightMeasureSpec); // 设置菜单栏暂用宽度
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mMostRecentX = (int) event.getX();
			downX = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) event.getX();
			int deltaX = mMostRecentX - moveX;
			// 如果在菜单打开时向右滑动及菜单关闭时向左滑动不会触发Scroll事件
			if ((!isOpen && (downX - moveX) < 0)
					|| (isOpen && (downX - moveX) > 0)) {
				scrollBy(deltaX, 0);
			}
			mMostRecentX = moveX;
			break;
		case MotionEvent.ACTION_UP:
			int upX = (int) event.getX();
			int dx = upX - downX;
			if (!isOpen) {// 菜单关闭时
				// 向右滑动超过menu一半宽度才会打开菜单
				if (dx > menu.getMeasuredWidth() * menuslide / 10) {
					state = Scroll_State.Scroll_to_Open;
				} else {
					state = Scroll_State.Scroll_to_Close;
				}
			} else {// 菜单打开时
				// 当按下时的触摸点在menu区域时，只有向左滑动超过menu的一半，才会关闭
				// 当按下时的触摸点在main区域时，会立即关闭
				if (downX < menu.getMeasuredWidth()) {
					if (dx < -menu.getMeasuredWidth() * menuslide / 10) {
						state = Scroll_State.Scroll_to_Close;
					} else {
						state = Scroll_State.Scroll_to_Open;
					}
				} else {
					state = Scroll_State.Scroll_to_Close;
				}
			}
			smoothScrollto();
			break;
		default:
			break;
		}
		return true;
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {  
       Boolean intercept=false;  
    	 //获取坐标点：  
      int x= (int) ev.getX();  
      int y= (int) ev.getY();  
        switch (ev.getAction()){  
            case MotionEvent.ACTION_DOWN:  
                intercept=false;  
                break;  
            case MotionEvent.ACTION_MOVE:  
                int deletx=x-mx;  
                int delety=y-my;  
                if(Math.abs(deletx)>Math.abs(delety))   {  
                    intercept=true;  
                  //  Tools.log("拦截了");  
                }  
                else {  
                    intercept=false;  
                   // Tools.log("没拦截");  
                }  
                break;  
            case MotionEvent.ACTION_UP:  
                intercept=false;  
                break;  
            default:  
                break;  
        }  
        //这里尤其重要，解决了拦截MOVE事件却没有拦截DOWN事件没有坐标的问题  
        mx=x;  
        my=y;  
        return intercept;  
    }  
	int mx,my;
	
	
	
	
	
	private void smoothScrollto() {
		int scrollx = getScrollX();
		switch (state) {
		case Scroll_to_Close:
			
			mScroller.startScroll(scrollx, 0, -scrollx, 0, menuanimationtime);
			 
			if (onSlidingMenuListener != null && isOpen) {
				onSlidingMenuListener.close();
			}
			isOpen = false;

			// //////////////////////////////////////////////////
			// ///////////////////////////////////////////////////

			break;
		case Scroll_to_Open:
			mScroller.startScroll(scrollx, 0, -scrollx - menu.getMeasuredWidth(), 0, menuanimationtime);
			if (onSlidingMenuListener != null && !isOpen) {
				onSlidingMenuListener.close();
			}

			// //////////////////////////////////////////////////
			// ///////////////////////////////////////////////////

			isOpen = true;
			break;
		default:
			break;
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), 0);
		}
		invalidate();
	}

	public void open() {
		state = Scroll_State.Scroll_to_Open;
		smoothScrollto();
		 
	}

	public void close() {
		state = Scroll_State.Scroll_to_Close;
		smoothScrollto();
	 

	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOnSlidingMenuListener(
			OnSlidingMenuListener onSlidingMenuListener) {
		this.onSlidingMenuListener = onSlidingMenuListener;
	}

	public interface OnSlidingMenuListener {
		public void open();

		public void close();
	}

}