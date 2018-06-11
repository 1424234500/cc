package util.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cc.Constant;

import util.AndroidTools;

public class CanvasView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	// SurfaceHolder实例
	private SurfaceHolder mSurfaceHolder;
	// Canvas对象
	private Canvas canvas;
	// 控制子线程是否运行
	private boolean startDraw;
	// Path实例
	private Path mPath = new Path();
	// Paint实例
	private Paint paint = new Paint();
//	绘图数据
	private int[][] arr;
	private int w = 0;
	private int h = 0;

	public CanvasView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(); // 初始化
	}

	private void initView() {
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);

		// 设置可获得焦点
		setFocusable(true);
		setFocusableInTouchMode(true);
		// 设置常亮
		this.setKeepScreenOn(true);

	}

	@Override
	public void run() {
		// 如果不停止就一直绘制
		while (startDraw) {
			// 绘制
			draw();
			AndroidTools.sleep(100);
		}
	}

	/*
	 * 创建
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		startDraw = true;
		new Thread(this).start();
	}

	/*
	 * 改变
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	/*
	 * 销毁
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		startDraw = false;
	}
	public void setData(int w, int h, int[][] arr){
		this.w = w;
		this.h = h;
		this.arr = arr;
		AndroidTools.out(w + " " + h + " " + arr.toString());
		draw();
	}
	private synchronized void draw() {
		try {
			canvas = mSurfaceHolder.lockCanvas();
			canvas.drawColor(Color.WHITE);
			float dw = 1f * Constant.screenW / w;
			float dh = 1f * Constant.screenH / h;

			if(arr != null && arr.length > 0 && arr[0].length > 0){
				for(int i = 0 ; i < h; i++){
					for(int j = 0; j < w; j++){
						int cc = arr[i][j];
						int c = Color.rgb(cc, cc, cc);
						paint.setColor(c);
						canvas.drawRect(i*dw, i*dh, i*dw+dw, i*dh+dh, paint);
					}
				}

			}


		} catch (Exception e) {
		} finally {
			// 对画布内容进行提交
			if (canvas != null) {
				mSurfaceHolder.unlockCanvasAndPost(canvas);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		/*int x = (int) event.getX();    //获取手指移动的x坐标
		int y = (int) event.getY();    //获取手指移动的y坐标
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				mPath.moveTo(x, y);
				break;

			case MotionEvent.ACTION_MOVE:

				mPath.lineTo(x, y);
				break;

			case MotionEvent.ACTION_UP:
				break;
		}*/
		return true;
	}

	// 重置画布
	public void reset() {
		mPath.reset();
	}

}