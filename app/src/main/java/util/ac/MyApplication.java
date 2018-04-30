package util.ac;

import util.AndroidTools;
import util.Tools;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static MyApplication mInstance;

    public static MyApplication getInstance(){
        if(mInstance == null){
            mInstance = new MyApplication();
        }
        return mInstance;
    }

	@Override
	public void onCreate() {
		super.onCreate();
		
		// 初始化全局变量
		
		showSystemInfo();
	}
    
    public void showSystemInfo(){
    	ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
		int memorySize = activityManager.getMemoryClass();
    	
    	AndroidTools.out("设备内存限制:" + memorySize);
    }
    
    
    
}