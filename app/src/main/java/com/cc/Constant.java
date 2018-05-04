package com.cc;

import android.os.Environment;

public class Constant {
	//public static String serverIp = "10.18.176.187";//校园网
	//public static String serverIp = "192.168.253.1";	//本机360wifi作废了


	public static int serverPort = 8092;
	public static String serverIpNet = "192.168.1.6";	//服务器
	public static String serverIpLocal = "192.168.191.1";	//本机

	public static String[] serverIps = {"39.107.26.100", "192.168.1.6", "192.168.191.1"};

	public static String serverIp = serverIpLocal;





	public static String systemKey = "";	//系统key
	public static String systemId = "";
	public static String systemPwd = "";




























	//public static String http = serverIp + ":8080/cc/";
	//public static String httpUpload = "http://"+serverIp + ":8080/cc/UploadFile"; //服务器http上传地址
	public static String httpUpload(){
		return "http://"+serverIp + ":8080/cc/UploadFile"; //服务器http上传地址
	}
	
	//http://10.18.176.187:8080/cc/Dispatch?type=photo&id=
	public static String profileHttp(){
		return "http://" + serverIp + ":8080/cc/Dispatch?type=profile&id=";
	}
	 
	public static String profileWallHttp(){
		return "http://" + serverIp + ":8080/cc/Dispatch?type=profilewall&id=";
	}
	 
//	public static final String profileHttp = "http://" + serverIp + ":8080/cc/Dispatch?type=profile&id=";
//	public static final String profileHttpDefault = "http://" + serverIp + ":8080/cc/Dispatch?type=profile&id=000";
//	public static final String profileWallHttp = "http://" + serverIp + ":8080/cc/Dispatch?type=profilewall&id=";
//	public static final String profileWallHttpDefault = "http://" + serverIp + ":8080/cc/Dispatch?type=profilewall&id=000";
	
	
	//上传type=uploadphoto、
	//图片访问type=profile、profilewall

	//http://10.18.176.187:8080/cc/profile/yls1.png服务器图片访问方式，个人信息访问权限问题？
	//头像可以随意访问，但是聊天图片呢？http访问图片限制验证密码or key？

 
		

      
    //Fragment的标识  
    public static final String FRAGMENT_FLAG_MESSAGE = "消息";   
    public static final String FRAGMENT_FLAG_CONTACTS = "联系人";   
    public static final String FRAGMENT_FLAG_NEWS = "新闻";   
    public static final String FRAGMENT_FLAG_SETTING = "设置";   
    public static final String FRAGMENT_FLAG_SIMPLE = "simple";
    
    //重连时间
    public static final long ReconnectTime = 3000;
    
    
    //本地文件存储路径
// /storage/emulated/0/mycc/record/100-1493005573881.amr 
	public static final String root = Environment.getExternalStorageDirectory() + "/mycc/";  
	public static final String dirVoice = root + "record/";  
	public static final String dirPhoto =  root + "photo/";  
	public static final String dirFile =  root + "file/";  
	public static final String dirCamera = root +  "camera/";  
	public static final String dirProfile = root +  "profile/";  
	public static final String dirProfileWall = root +  "profilewall/";

	public static  int tvSendSize = 0;  

	public static final String split = "OTOTO";

	protected static final int maxChatNum = 32;//最多聊天界面记录
	




	
	
	
	
	//暂存用户数据
	public static String username = "Walker";
	public static String id = "100";
	public static String pwd = "";
	public static String profilepath = "";
	public static String profilepathwall = "";
	public static String sign = "";
	public static String email = "";
	public static String sex = "";
	public static String loginstaus = "";
	public static boolean iflogin = false;
	//doll
	public static int ivdoll = 0;

	
	//聊天图片加载最大高度
	public static int photoMaxH = 400;
	//发送图片压缩高度
	public static int photoSend = 800;
	//相册加载最大高度
	public static int ablumMaxH = 500;
	//手机信息
	public static int screenH = 1980;
	public static int screenW = 1080;
	
	//emoji表情大小
	public static int emojiWH = 64;
	
	
	
	//数据库信息
	public static String LOGIN_USER = "login_user";//用户登陆信息表，成功登录记录
	//"create table if not exists login_user (id varchar(30) primary key, pwd varchar(50), profilepath varchar(200) ) 

	public static int offlineMode = 0;	//0正常模式，1离线模式
	protected static long sleepUpload = 2000;


	
	
	// 通过id得到映射的图片头像资源
	public static int getDrawableByIvProfile(int ivprofile) {

			int ress[] = { R.drawable.dollars0, R.drawable.dollars1,
					R.drawable.dollars2, R.drawable.dollars3, R.drawable.dollars4,
					R.drawable.dollars5, R.drawable.dollars6, R.drawable.dollars7,
					R.drawable.dollars8, R.drawable.dollars9, R.drawable.dollars10,
					R.drawable.dollars11, R.drawable.dollars12,
					R.drawable.dollars13, R.drawable.dollars14,
					R.drawable.dollars15, R.drawable.dollars16,
					R.drawable.dollars17, R.drawable.dollars18,
					R.drawable.dollars19, R.drawable.dollars20,
					R.drawable.dollars21, R.drawable.dollars22, 
					//System专属，不分配给普通用户
					R.drawable.dollar,R.drawable.dollar, };// 共24，system>23;普通用户<=23

			ivprofile %= ress.length;

			return ress[ivprofile];
		}
	// 通过id得到映射的图片 资源
	public static int getDrawableByTypeFile(String type) {
		type = type.toLowerCase();
		int id = R.drawable.icon_filetype_unkonwn;
		if(type.equals("apk")){
			id = R.drawable.icon_filetype_apk;
		}else if(type.equals("doc") || type.equals("docx") ){
			id = R.drawable.icon_filetype_doc ;
		} else if(type.equals("dir")){
			id = R.drawable.icon_filetype_dir ;
		} else if(type.equals("html") || type.equals("htm") || type.equals("jsp") || type.equals("asp")){
			id = R.drawable.icon_filetype_html ;
		} else if(type.equals("png") || type.equals("jpg") || type.equals("jpeg") || type.equals("gif") || type.equals("bmp")){
			id = R.drawable.icon_filetype_img ;
		} else if(type.equals("ipa")){
			id = R.drawable.icon_filetype_ipa ;
		} else if(type.equals("mp3") || type.equals("ape")){
			id = R.drawable.icon_filetype_music ;
		} else if(type.equals("pdf")){
			id = R.drawable.icon_filetype_pdf;
		} else if(type.equals("ppt") || type.equals("pptx")){
			id = R.drawable.icon_filetype_ppt;
		} else if(type.equals("bt")){
			id = R.drawable.icon_filetype_torrent;
		} else if(type.equals("txt")){
			id = R.drawable.icon_filetype_txt;
		} else if(type.equals("vcf")){
			id = R.drawable.icon_filetype_vcf;
		}  else if(type.equals("mp4") || type.equals("mkv") || type.equals("rmvb") || type.equals("avi")){
			id = R.drawable.icon_filetype_vedio;
		}  else if(type.equals("vsd")){
			id = R.drawable.icon_filetype_vsd;
		}  else if(type.equals("xls") || type.equals("xlsx")){
			id = R.drawable.icon_filetype_xls;
		}  else if(type.equals("zip")){
			id = R.drawable.icon_filetype_zip;
		}

		return id ;
	}
	
	
}
