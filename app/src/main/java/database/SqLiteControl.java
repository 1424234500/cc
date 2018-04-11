package database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqLiteControl {
  
	final Context context;
	DatabaseHelper dbHelper;
	SQLiteDatabase sqlDatabase;

	String dbName;	
	
	public SqLiteControl(Context ctx, String dbName) {
		this.context = ctx;
		this.dbName = dbName;
	}
	
	//执行非查询sql，-1失败，成功时可能返回条数数据
	public int execSQL(String sql, Object...objects){
		try{
			open();
			sqlDatabase.execSQL(sql, objects);
			close();
			
			return 0;
		}catch(Exception e){
			out("execSQL:" + sql + " " + Tools.objects2string( objects));
			return -1;
		}
		
	}
	//得到list数据
	List<Map<String, Object>> queryList(String sql, Object... objects ){
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		
		try{
			open();
		 
			String[] objs = Tools.objects2strings(objects);
			Cursor cursor = sqlDatabase.rawQuery(sql, objs);
			String columnNames[] = cursor.getColumnNames();
			while (cursor.moveToNext()) {  
				  Map<String, Object> map = new HashMap<String, Object>();
				  for(String temp : columnNames){
					  map.put(temp, cursor.getString(cursor.getColumnIndex(temp)));
				  }
				  res.add(map);
			}  
			
			cursor.close();  
			
			close();
			
			return res.size() >= 1? res: null;
			
		}catch(Exception e){
			out("execSQL:" + sql + " " + Tools.objects2string( objects ));
			return null;
		}
	}
	//得到指定数据
	Map<String, Object> queryOne(String sql, Object...objects ){
		List<Map<String, Object>> res = queryList(sql, objects);
		if(res != null){
			if(res.size() >= 1){
				return res.get(0);
			}
		}
		return null;
	}
	
	 
	public void open() {
		if (dbHelper == null)
			dbHelper = new DatabaseHelper(context);
		if (sqlDatabase == null)
			sqlDatabase = dbHelper.getWritableDatabase();
		if (sqlDatabase == null || dbHelper == null) {
			out("open sqlDatabase error");
		} else {
		    //out("openSql success");
		}
	}

	public void close() {
		if (sqlDatabase != null) {
			sqlDatabase.close();
			sqlDatabase = null;
		}
		if (dbHelper != null) {
			dbHelper.close();
			dbHelper = null;
		}
	}
      
 
	private class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, dbName, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			out("not exist database so ## Creating DataBase: " + dbName);
			// 创建表单

			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			out("Upgrading database from version " + oldVersion + " to " + newVersion);
		}
	}

	public void out(String s) {
			s = Tools.tooLongCut(s);
			Log.e("sql","SQL."+   s); 
	}

}