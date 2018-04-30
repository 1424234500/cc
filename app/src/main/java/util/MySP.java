package util;

import android.content.Context;
import android.content.SharedPreferences;

public class MySP {
	public static void  put(Context context, String name , String value){
		 SharedPreferences sp = context.getSharedPreferences("mysharedpreferences",  0);
		 sp.edit().putString(name, value).commit();
	} 
	public static String get(Context context, String name, String defaultValue  ){
		 SharedPreferences sp = context.getSharedPreferences("mysharedpreferences",  0);
		return  sp.getString(name, defaultValue  );
	}
	
}
