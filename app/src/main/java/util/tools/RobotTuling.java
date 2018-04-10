package util.tools;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.cc.Constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;




//聊天机器人
public class RobotTuling {
	public static String getHttp() {
		return robotHttp + "?key="+APIKEY+"&info=";
	}
	public final static String robotHttp = "http://www.tuling123.com/openapi/api";
	public final static String APIKEY="bfbf6432b655493b9e861b470bca9921";
//http://www.tuling123.com/openapi/api?key=bfbf6432b655493b9e861b470bca9921&info=[可爱]
//	{
//		“key”: “APIKEY”,
//		“info”: “小狗的图片”
//	 }
	static private String robot(String info){
		String res = "";
//			JSONObject jo = new JSONObject();
//			jo.put("key", APIKEY);
//			jo.put("info", info);
//			String json = jo.toString();
			//param1=value&param2=value2&param3=value3
		//	res = HttpUtils.postDownloadJson(robotHttp , "key=" + APIKEY + "&info=" + info);
		//	Tools.log("dopost="+res);
		//	res = HttpUtils.getJsonByInternet(robotHttp + "?key=" + APIKEY + "&info=" + info);
		//	Tools.log("doGet="+res);
		 
		return res;
	}
	
	
	public static String makeMsg(String jsonstr){
	 
			if(Tools.testNull( jsonstr))return "nothing";
			String res = "";
			List<Map<String,Object>> list;
			int code = MyJson.getInt(jsonstr, "code");
			switch(code){
			case 100000:
				res = MyJson.getString(jsonstr, "text");
				break;
			case 200000:
				res = MyJson.getString(jsonstr, "text") + "\n" + MyJson.getString(jsonstr, "url");
				break;
			case 302000:
				res = MyJson.getString(jsonstr, "text") + "\n" ;
				list = MyJson.getList(jsonstr, "list");
				for(Map<String,Object> map: list){
					res += "" + map.get("source").toString() + "\n";
					res += "" + map.get("article").toString() + "\n";
					res += "" + map.get("detailurl").toString() + "\n";
//					"article": "工信部:今年将大幅提网速降手机流量费",
//					"source": "网易新闻",
//					"icon": "",
//					"detailurl": "http://news.163.com/15/0416/03/AN9SORGH0001124J.html"
				}
				break;
			default:
				res = MyJson.getString(jsonstr, "text")  ;
				break;
			}
			Tools.out(  "聊天机器人返回." + jsonstr + "解析结果." + res );
			return res;
		}
//100000	文本类
//	"code":100000,
//	"text":"你也好 嘻嘻"
	
//200000	链接类
//	"code": 200000,
//	"text": "亲，已帮你找到图片",
//	"url": "http://m.image.so.com/i?q=%E5%B0%8F%E7%8B%97"
	
//302000	新闻类
//	"code": 302000,
//	"text": "亲，已帮您找到相关新闻",
//	"list": [
//	{
//	"article": "工信部:今年将大幅提网速降手机流量费",
//	"source": "网易新闻",
//	"icon": "",
//	"detailurl": "http://news.163.com/15/0416/03/AN9SORGH0001124J.html"
//	},
//	{
//	"article": "北京最强沙尘暴午后袭沪 当地叫停广场舞",
//	"source": "网易新闻",
//	"icon": "",
//	"detailurl": "http://news.163.com/15/0416/14/ANB2VKVC00011229.html"
//	},
//	{
//	"article": "公安部:小客车驾照年内试点自学直考",
//	"source": "网易新闻",
//	"icon": "",
//	"detailurl": "http://news.163.com/15/0416/01/AN9MM7CK00014AED.html"
//	} ]
	            
//308000	菜谱类
	//"text": "亲，已帮您找到菜谱信息",
	//"list": [{
	//"name": "鱼香肉丝",
	//"icon": "http://i4.xiachufang.com/image/280/cb1cb7c49ee011e38844b8ca3aeed2d7.jpg",
	//"info": "猪肉、鱼香肉丝调料 | 香菇、木耳、红萝卜、黄酒、玉米淀粉、盐",
	//"detailurl": "http://m.xiachufang.com/recipe/264781/"
	//}]
//313000（儿童版）	儿歌类
//314000（儿童版）	诗词类
//40001	参数key错误
//	40002	请求内容info为空
//	40004	当天请求次数已使用完
//	40007	数据格式异常


		            
	
}
