package database;

import java.util.List;
import java.util.Map;

import android.content.Context;

public class BaseDaoImpl implements BaseDao{
	String dbName = "mydb";
	
	SqLiteControl sqLite;
	 
	public BaseDaoImpl(Context ctx){
		sqLite = new SqLiteControl(ctx, dbName);
	}
 

	@Override
	public List<Map<String, Object>> queryList(String sql, Object... objects) {
		return sqLite.queryList(sql, objects);
	}

	@Override
	public Map<String, Object> queryOne(String sql, Object... objects) {
		return sqLite.queryOne(sql, objects);
	}

	@Override
	public int execSQL(String sql, Object... objects) {
		return sqLite.execSQL(sql, objects);
	}

}
