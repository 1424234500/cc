package util;
import util.view.ChatView;
import util.view.ChatView.OnControl;

import com.cc.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * @author Walker
 * @date 2017-3-28 下午4:17:10
 * Description: 改写网上demo，适配效果
 */
public class EmotionKeyboardCopy {

        private static final String SHARE_PREFERENCE_NAME = "EmotionKeyboard";
        private static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "soft_input_height";
        private Activity mActivity;
        private InputMethodManager mInputManager;//软键盘管理类
        private SharedPreferences sp;
        
        private ViewGroup viewFill;//总容器布局
        private View viewVoice,viewPhoto,viewGraph,viewEmoji,viewMore;	//栏目布局
        private ChatView cvChat;
        
        private EditText mEditText;//输入框，点击切换到输入法
        private View mContentView;//内容布局view,即除了表情布局或者软键盘布局以外的布局，用于固定bar的高度，防止跳闪
		
        
     

        /**
         * 外部静态调用
         * @param activity
         * @return
         */
        public static EmotionKeyboardCopy with(Activity activity) {
            EmotionKeyboardCopy emotionInputDetector = new EmotionKeyboardCopy();
            emotionInputDetector.mActivity = activity;
            emotionInputDetector.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            emotionInputDetector.sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
            return emotionInputDetector;
        }

        /**
         * 绑定内容view，此view用于固定bar的高度，防止跳闪
         * @param contentView
         * @return
         */
        public EmotionKeyboardCopy bindToContent(View contentView) {
            mContentView = contentView;
            return this;
        }
        
        /**
         * 绑定编辑框
         * @param editText
         * @return
         */
        public EmotionKeyboardCopy bindToEditText(EditText editText) {
            mEditText = editText;
            mEditText.requestFocus();
            mEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP && viewFill.isShown()) {
                        lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                        hideFillLayout(true);//隐藏表情布局，显示软件盘
                        cvChat.close();
                        //软件盘显示后，释放内容高度
                         mEditText.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                unlockContentHeightDelayed();
                            }
                        }, 100L);
                    }
                    return false;
                }
            });
            return this;
        }

        
        
        OnControl onControl;
        public void setOnControl(OnControl onControl){
        	this.onControl  = onControl;
        }
        /**
         * 绑定菜单栏
         */
        public EmotionKeyboardCopy bindToChatView(final ChatView cvchat) {
        	this.cvChat = cvchat;
        	
            cvchat.setOnControl( new OnControl() {
				@Override
				public void onClose() { 
					hideFillLayout(false);
					if(onControl!=null)onControl.onClose();
				} 
				@Override
				public void onOpen(int id) {
					if(onControl!=null){
						onControl.onOpen(id);
					}
					if(id == R.id.ivgraph)return;
					
					viewFill.removeAllViews();
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
					//params.addRule(LinearLayout.);
					viewFill.addView(getViewById(id),params);

                    if (isSoftInputShown()) { //若已经打开软键盘
	                    lockContentHeight();
                    	hideSoftInput();
                    	showFillLayout();
	                    unlockContentHeightDelayed();
                    } else {
                    	showFillLayout();//没显示，直接显示表情布局
                    }
				}
            });
            return this;
        }
        public View getViewById(int id){
        	switch(id){
        	case R.id.ivvoice:
        		return viewVoice;
        	case R.id.ivphoto:
        		return viewPhoto;
        	case R.id.ivgraph:
        		return viewGraph;
        	case R.id.ivemoji:
        		return viewEmoji;
        	case R.id.ivmore:
        		return viewMore;
        	}
        	return null;
        }
        
        /**
         * 设置总容器

         * @return
         */
        public EmotionKeyboardCopy setFillView(ViewGroup viewfill) {
            this.viewFill = viewfill;
            return this;
        }
        /**
         * 设置语音内容布局

         * @return
         */
        public EmotionKeyboardCopy setVoiceView(View viewVoice) {
            this.viewVoice = viewVoice;
            return this;
        }
        /**
         * 设置图片内容布局

         * @return
         */
        public EmotionKeyboardCopy setPhotoView(View viewPhoto) {
            this.viewPhoto = viewPhoto;
            return this;
        }
        /**
         * 设置照相机内容布局

         * @return
         */
        public EmotionKeyboardCopy setGraphView(View viewGraph) {
            this.viewGraph = viewGraph;
            return this;
        }
        /**
         * 设置表情内容布局

         * @return
         */
        public EmotionKeyboardCopy setEmotionView(View viewEmoji) {
            this.viewEmoji = viewEmoji;
            return this;
        }
        /**
         * 设置表情内容布局

         * @return
         */
        public EmotionKeyboardCopy setMoreView(View viewMore) {
            this.viewMore = viewMore;
            return this;
        }
        public EmotionKeyboardCopy build(){
        		//设置软件盘的模式：SOFT_INPUT_ADJUST_RESIZE  这个属性表示Activity的主窗口总是会被调整大小，从而保证软键盘显示空间。
        	//从而方便我们计算软件盘的高度
        	mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //隐藏软件盘
            hideSoftInput();
            return this;
        }
        /**
         * 点击返回键时先隐藏表情布局
         * @return
         */
        public boolean interceptBackPress() {
            if (viewFill.isShown()) {
                hideFillLayout(false);
                return true;
            }
            return false;
        }
        private void showFillLayout() {
            int softInputHeight = getSupportSoftInputHeight();
            if (softInputHeight == 0) {
                softInputHeight = sp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 400);
            }
            hideSoftInput();
            viewFill.getLayoutParams().height = softInputHeight;
            viewFill.setVisibility(View.VISIBLE);
            
        }
        /**
         * 隐藏表情布局
         * @param showSoftInput 是否显示软件盘
         */
        public void hideFillLayout(boolean showSoftInput) {
            if (viewFill.isShown()) {
            	viewFill.setVisibility(View.GONE);
                if (showSoftInput) {
                    showSoftInput();
                }
            }
        }
        /**
         * 锁定内容高度，防止跳闪
         */
        private void lockContentHeight() {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
            params.height = mContentView.getHeight();
            params.weight = 0.0F;
        }
        /**
         * 释放被锁定的内容高度
         */
        private void unlockContentHeightDelayed() {
            mEditText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
                }
            }, 200L);
        }
       
        /**
         * 编辑框获取焦点，并显示软件盘
         */
        private void showSoftInput() {
            mEditText.requestFocus();
            mEditText.post(new Runnable() {
                @Override
                public void run() {
                    mInputManager.showSoftInput(mEditText, 0);
                }
            });
        }
        /**
         * 隐藏软件盘
         */
        private void hideSoftInput() {
            mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
        }
        /**
         * 是否显示软件盘
         * @return
         */
        private boolean isSoftInputShown() {
            return getSupportSoftInputHeight() != 0;
        }
        /**
         * 获取软件盘的高度
         * @return
         */
        private int getSupportSoftInputHeight() {
            Rect r = new Rect();
            /**
             * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
             * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
             */
            mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            //获取屏幕的高度
            int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
            //计算软件盘的高度
            int softInputHeight = screenHeight - r.bottom;
            /**
             * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
             * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
             * 我们需要减去底部虚拟按键栏的高度（如果有的话）
             */
            if (Build.VERSION.SDK_INT >= 20) {
                // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
                softInputHeight = softInputHeight - getSoftButtonsBarHeight();
            }
            if (softInputHeight < 0) {
               // LogUtils.w("EmotionKeyboard--Warning: value of softInputHeight is below zero!");
            }
            //存一份到本地
            if (softInputHeight > 0) {
                sp.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
            }
            return softInputHeight;
        }
        /**
         * 底部虚拟按键栏的高度
         * @return
         */
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        private int getSoftButtonsBarHeight() {
            DisplayMetrics metrics = new DisplayMetrics();
            //这个方法获取可能不是真实屏幕的高度
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            //获取当前屏幕的真实高度
            mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight) {
                return realHeight - usableHeight;
            } else {
                return 0;
            }
        }
	    /**
	     * 获取软键盘高度
	     * @return
	     */
        public int getKeyBoardHeight(){
        return sp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 400);
    }
}