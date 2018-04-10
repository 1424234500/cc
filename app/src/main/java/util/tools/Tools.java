package util.tools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class Tools {
	
	public static int getCount(String[] array, String str){
		if(array == null) return -1;
		if(str == null) return -1;
		for(int i = 0; i < array.length; i++){
			if(array[i].equals(str)){
				return i;
			}
		}
		return -1;
	}
	
	public static int parseInt(String num) {
		int res = 0;
		 if(num == null){
			 res = 0;
		 }else{
			 try{
				 res = Integer.parseInt(num);
			 }catch(Exception e){
				 Tools.out("解析:" + num + "数字失败");
				 res = 0;
			 }
		 }
		return res;
	}
	public static String getList(List<Map<String,Object>> list, int i, String name){
		if(list == null) return "list is null";
		if(i < 0)return "i < 0";
		if(i >= list.size())return "i > list size";
		if(list.get(i).get(name) == null){
			return "";
		}else{
			return list.get(i).get(name).toString();
		}
	}
	public static String getMap(Map<String,Object> map, String name){
		if(map == null)return "map is null";
		if(map.get(name) == null){
			return "";
		}else{
			return map.get(name).toString();
		}
	}

	public static String getListS(List<Map<String,String>> list, int i, String name){
		if(list == null) return "list is null";
		if(i < 0)return "i < 0";
		if(i >= list.size())return "i > list size";
		if(list.get(i).get(name) == null){
			return "";
		}else{
			return list.get(i).get(name).toString();
		}
	}
	public static String getMapS(Map<String,String> map, String name){
		if(map == null)return "map is null";
		if(map.get(name) == null){
			return "";
		}else{
			return map.get(name).toString();
		}
	}
	 // /sdcard/mycc/record/100-101020120120120.amr return amr
	public static String getFileTypeByLocalPath(String localpath){
		String res = "null";

		if(localpath == null){
		}else{
			String[] ss=localpath.split("/");
			if(ss.length > 0){
				localpath = ss[ss.length - 1];	//0120.amr
				ss=localpath.split("\\.");
				if(ss.length > 0){
					res = ss[ss.length - 1];	//0120.amr
				}
			}
		}
		return res;
	}
	public static String getFileNameOnlyByLocalPath(String localpath){
		String res = "null";

		if(localpath == null){
		}else{
			String[] ss=localpath.split("/");
			if(ss.length > 0){
				localpath = ss[ss.length - 1];	//0120.amr
				res=localpath.substring(0,localpath.lastIndexOf("\\."));
				 
			}
		}
		return res;
	}
// /sdcard/mycc/record/100-101020120120120.amr return asdfa.amr
	public static String getFileNameByLocalPath(String localpath){
		String res = "null";

		if(localpath == null){
		}else{
			String[] ss=localpath.split("/");
			if(ss.length > 0){
				res = ss[ss.length - 1];	//0120.amr
			}
		}
		return res;
	}
	
	//通过字符串长度，计算大概的 流量大小 MB KB B char=B
	public static String calcSize(int length) {
		int m = length/(1024*1024);
		int k = length%(1024*1024)/1024;
		int b = length%(1024*1024)%1024;
		return m>0?  m+"."+k/100+"MB" : k>0? k+"."+b/100+"KB" : b+"B";
	}
	//语音时间计算
	public static String calcTimeSize(int length) {
		length = length / 1000;	//s
		int m = length/60;	//min
		int k = length%60;	// min s
		return m>0?  m+"'"+k+"''" : length + "''";
	}
	
	public static void toast(Context c, String str){
		Toast.makeText(c, str, Toast.LENGTH_SHORT).show();
		log("toast." + str);
	}
	public static void out(String str) {
		Log.e("tools", ""+ str.length() + "." + str);
	}

	public static void log(String str) {
		Log.e("mylog", str);
	}
	public static void life(String str) {
		Log.e("life", str);
	}
	public static void tip(String str) {
		Log.e("tip", str);
	}
	public static void list(String str) {
		Log.e("list", str);
	}

	public static String getNowTimeS( ) {
		return dateFormatS(new Date());
	}
	public static String getNowTime( ) {
		return dateFormat(new Date());
	}
	public static String getNowTimeL( ) {
		return dateFormatL(new Date());
	} 
	public static String dateFormatS(java.util.Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String res = sdf.format(d);
		return res;
	}
	public static String dateFormat(java.util.Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String res = sdf.format(d);
		return res;
	}
	public static String dateFormatL(java.util.Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String res = sdf.format(d);
		return res;
	}
	public static long getCurrentTime(){
		return System.currentTimeMillis();
	}
	public static String long2string(long time){
		return time/1000/60+":"+time%(1000*60)/1000;
	}
	
	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt2（）配套使用
	 */

	public static byte[] int2bytes(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (value >> (24 - i * 8));
		}
		return b;
	}

	public static int bytes2int(byte[] b) {
		return (((int) b[0] << 24) + (((int) b[1]) << 16) + (((int) b[2]) << 8) + b[3]);
	}

	private static int toolong = 120;

	public static String tooLongCut(String str) {
		if (str.length() > toolong)
			return str.substring(0, toolong) + " || length=" + str.length();
		return str;
	}
	
	public static String getServerIp(String localIp) {
		String ip = localIp;

		if (ip != null) {
			if (ip.length() >= 7) {// 231.132.131.n?,wifi主机往往那个是尾数为1
				String ss[] = ip.split("\\."); // 特殊字符分割转义##########################
				if (ss.length >= 4) {
					ip = ss[0] + "." + ss[1] + "." + ss[2] + ".1"; // 因为主机往往是 尾号
																	// 1，
																	// 寻找当前ip的主机
					// out("Make a server ip=" + ip);
				}
			}
		}
		return ip;
	}

	public static String getServerIp(Context c) {
		String ip = getLocalIp(c);

		if (ip != null) {
			if (ip.length() >= 7) {// 231.132.131.n?,wifi主机往往那个是尾数为1
				String ss[] = ip.split("\\."); // 特殊字符分割转义##########################
				if (ss.length >= 4) {
					ip = ss[0] + "." + ss[1] + "." + ss[2] + ".1"; // 因为主机往往是 尾号
																	// 1，
																	// 寻找当前ip的主机
					// out("Make a server ip=" + ip);
				}
			}
		}
		return ip;
	}

	public static String getLocalIp(Context c) {
		WifiManager wifiManager = (WifiManager) c
				.getSystemService(c.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		// 获取32位整型IP地址
		int ipAddress = wifiInfo.getIpAddress();
		// 返回整型地址转换成“*.*.*.*”地址
		return String.format("%d.%d.%d.%d", (ipAddress & 0xff),
				(ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));
	}

	public static String getStringBySize(int filesize) {
		return filesize > 1024 * 1024 * 1024 ? (float) (10 * filesize / (1024 * 1024 * 1024))
				/ 10 + "G"
				: (filesize > 1024 * 1024 ? filesize / 1024 / 1024 + "M"
						: filesize / 1024 + "K");
	}

	public static String getVelocityByDetaTime(long l) {
		// TODO 自动生成的方法存根

		return null;
	}

	static int r = 0, g = 0, b = 0;

	public static int getLineColor() {
		int d = 8;
		double pi = 2 * Math.PI;
		r = (int) (128 * (1 + Math.sin(r % (pi))));
		g = (int) (128 * (1 + Math.cos(r % (pi))));
		b = (int) (128 * (1 + Math.sin(r % (pi) + pi / 2)));

		return Color.rgb(r, g, b);
	}

	public static int getRadomColor() {
		int R = 0, G = 0, B = 0;
		R = (int) (Math.random() * 255);
		G = (int) (Math.random() * 255);
		B = (int) (Math.random() * 255);

		return Color.rgb(R, B, G);
	}

	public static int Int(String time) {
		// TODO 自动生成的方法存根
		try {
			int res = Integer.parseInt(time);
			return res;
		} catch (Exception e) {
			// TODO: handle exception
			return -1;
		}
	}

 //空 true
	public static boolean testNull(Object...strs) {
		for(Object str: strs){
			if (str == null)
				return true;
			if (str.toString().equals(""))
				return true;
		}
		return false;
	}
	public static String getMapString(Map<String,Object> map, String name){
		String res = "";
		
		if(map.get(name) != null){
			res = map.get(name).toString();
		}
		
		return res;
	}
 
	public static String[] objects2strings(Object...objects) {
		if(objects == null)return null;
		String[] objs = new  String[objects.length] ;
		for(int i = 0; i < objects.length; i++){
			objs[i] = objects[i].toString();
		}
		return objs;
	}
	
	public static String s2s(String[] strs){
		String res = "[ ";
		for(String str: strs){
			res += str + ", ";
		}
		res += " ]";
		return res;
	}
	public static String objects2string(Object...objects) {
		String[] res = objects2strings(objects);
		return s2s(res);
	}

 
	public static String list2string(List<Map<String,Object>> list){
		String res = "[ \n";
		for(Map<String,Object> map: list){
			res += map.toString();
			res += "\n";
		}
		res += " ]";
		return res;
	}
	public static String list2strings(List<Map<String,String>> list){
		String res = "[ \n";
		for(Map<String,String> map: list){
			res += map.toString();
			res += "\n";
		}
		res += " ]";
		return res;
	}

	public static int getCountListByName(List<Map<String,Object>> list, String name, String value){
		if(list == null)return -1;
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).get(name).toString().equals(value)){
				return i;
			}
		}
		return -1;
		
	}
	
	
	public static String cutName(String str){
		return str.length()>4 ? str.substring(0,4)+".." : str;
	}

	public static boolean fileExist(String str) {
		File f = new File(str);
		 return f.exists() && f.isFile() && f.length() > 10;
	}
	
	
}