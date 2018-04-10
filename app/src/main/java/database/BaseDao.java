package database;

import java.util.List;
import java.util.Map;

/**
 * @author Walker
 * @date 2017-2-22 下午2:44:45
 * Description: 数据访问基础接口
 */
public interface BaseDao {
 
	//得到list数据
	List<Map<String, Object>> queryList(String sql, Object... objects );
	//得到指定数据
	Map<String, Object> queryOne(String sql, Object...objects );
	
	//执行非查询sql，-1失败，成功时可能返回条数数据
	int execSQL(String sql, Object... objects );
	
}
