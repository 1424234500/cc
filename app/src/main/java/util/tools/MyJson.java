package util.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * org.json.tools
 */


public class MyJson {



	// msg json {"cmd":"conn","value":"connok" }
		public static String makeJson(int cmd, String...values) {
			String res = "";
			try {
				JSONObject jo = new JSONObject();
				jo.put("cmd", cmd);
				int i = 0;
				for(String value : values){
					jo.put("value"+i++, value);
				}
				res = jo.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return res;
		}

		public static List<Map<String, Object>> getList(String jsonstr, String name){
			try {
				JSONObject jo = new JSONObject(jsonstr);
				return MyJson.jsonArrayToListMap(jo.getJSONArray(name));
			} catch (Exception e) {
				out("json get list error from " + jsonstr + " exception：" + e.toString());
			}
			return null;
		}
	 
		public static String getValue0(String jsonstr) {
			return getString(jsonstr, "value0");
		}

		public static String getValue1(String jsonstr) {
			return getString(jsonstr, "value1");
		}

		public static String getValue2(String jsonstr) {
			return getString(jsonstr, "value2");
		}
		public static String getValue3(String jsonstr) {
			return getString(jsonstr, "value3");
		}
		public static String getValueI(String jsonstr, int i) {
			return getString(jsonstr, "value" + i);
		}
		
	public static int getCmd(String jsonstr) {
		return getInt(jsonstr, "cmd");
	}

	public static String getString(String jsonstr, String name) {
		String res = "";
		try {
			JSONObject jo = new JSONObject(jsonstr);
			res = jo.getString(name);
		} catch (Exception e) {
			 out("json. "  + "." + name + " is not exist");
		}
		return res;
	}

	public static int getInt(String jsonstr, String name) {
		int res = -1;
		try {
			JSONObject jo = new JSONObject(jsonstr);
			res = jo.getInt(name);
		} catch (Exception e) {
			out("json get error from " + jsonstr + " exception：" + e.toString());
		}
		return res;
	}

	public static void out(String str) {
		Log.d("MyJson", str);
	}
	 
	public static JSONArray listMapToJSONArray(List<Map<String, Object>> list){
		if(list == null)return new JSONArray();
		return new JSONArray(list);
	}
	public static List<Map<String, Object>>   jsonArrayToListMap(JSONArray jsonArray){
		List<Map<String, Object>> list = new  ArrayList<Map<String, Object>>();
		String name;

		try {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);

				Iterator<String> nameItr = jo.keys();
				Map<String, Object> map = new HashMap<String, Object>();
				while (nameItr.hasNext()) {
					name = nameItr.next();
					map.put(name, jo.getString(name));
				}
				list.add(map);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}	
	

	public static String makeJson(int cmd, List<Map<String, Object>> list){
		String res = "";
		try {
			JSONObject jo = new JSONObject();
			jo.put("cmd", cmd);
			jo.put("result", MyJson.listMapToJSONArray(list));
			res = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
	public static List<Map<String, Object>> getListType(String jsonstr){
		try {
			JSONObject jo = new JSONObject(jsonstr);
			return MyJson.jsonArrayToListMap(jo.getJSONArray("listtype"));
		} catch (Exception e) {
			out("json get list error from " + jsonstr + " exception：" + e.toString());
		}
		return null;
	}
	public static List<Map<String, Object>> getList(String jsonstr){
		try {
			JSONObject jo = new JSONObject(jsonstr);
			return MyJson.jsonArrayToListMap(jo.getJSONArray("list"));
		} catch (Exception e) {
			out("json get list error from " + jsonstr + " exception：" + e.toString());
		}
		return null;
	}
	
	public static String makeJson(int cmd, Map<String, Object> map){
		String res = "";
		try {
			JSONObject jomap = new JSONObject(map);

			JSONObject jo = new JSONObject();
			jo.put("cmd", cmd);
			jo.put("result", jomap);
			res = jo.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}
	public static Map<String, Object>  getMap(String jsonstr){
		try {
			JSONObject jo = new JSONObject(jsonstr).getJSONObject("result");
			String name;
			Iterator<String> nameItr = jo.keys();
			Map<String, Object> map = new HashMap<String, Object>();
			while (nameItr.hasNext()) {
				name = nameItr.next();
				map.put(name, jo.getString(name));
			}
			return map; 
		} catch (Exception e) {
			out("json. " + jsonstr + ".result" + " is not exist");
		}
		return null;
	}


	
	public static List<Map<?, ?>> orderListMap( List<Map<?, ?>> list) {
		if(list == null ) return null;
		
		ArrayList<Map<?, ?>>  list1 = new ArrayList<Map<?,?>>();
		
		for(Map<?,?> map : list){
		//	if(map.get("TYPE").equals("user")) 
				list1.add(map);
		}
//		for(Map<String,Object> map : list){
//			if(map.get("TYPE").equals("group")) 
//				list1.add(list1.size(), map);
//		}
		
		return list;
	}
	

	
	
}
