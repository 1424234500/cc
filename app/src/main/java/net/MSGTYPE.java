package net;


//消息结构类型名称定义
public   class MSGTYPE {

	public final static String broadcastUrl = "cc.broadcast.client";	//广播接收注册id

	public static final int BEATS = -5;		//心跳
	public static final int LOG = -4;
	public static final int CLOSE = -3;
	public static final int TOAST = -2;
	public final static int ERROR = -1;	//错误
	public final static int OK = 0;	//成功
//////////////////////////////////////////////////////////////////////
	public final static int PROFILE_PATH_BY_ID = 1;	//获取头像，
	public final static int LOGIN_BY_ID_PWD = 2;	//登陆，id， pwd
	public static final int REGISTE_BY_USERNAME_EMAIL_SEX_PWD = 3;	//注册

	public static final int FIND_USERS_GROUPS_BY_ID = 4;		//查询 cc群 cc用户 列表
	public static final int GET_USER_GROUP_DETAIL_BY_TYPE_ID = 5;			//查询群/用户详情

	public static final int ADD_USER_GROP_BY_TYPE_ID_YANZHEN_NICKNAME = 6;			//添加好友/群操作

	public static final int CONTACT_USER_GROUP_MAP = 7;			//服务器发送过来的 好友列表map

	public static final int GET_USER_GROUP_CHAT_BY_TYPE_ID_START = 8;	//聊天消息获取，并添加会话列表记录
	public static final int ADD_CHAT_SESSION_BY_TYPE_ID = 9;	//聊天会话 添加
	public static final int GET_CHAT_SESSIONS = 10;	//获取会话列表
	public static final int UPDATE_NICKNAME_BY_ID_NICKNAME_GROUPID = 11;	//更新昵称
	public static final int SEND_CHATMSG_BY_GTYPE_TOID_TYPE_TIME_MSG = 12;	//发送消息,收到消息
	public static final int SEND_CHATMSG_RESULT = 13;	//发送消息结果 true false 发送成功失败,发送时间作为标志

	public static final int UPDATE_BY_USERNAME_SIGN_SEX_OLDPWD_NEWPWD = 14;	//更新用户信息,密码可选

	public static final int RESULT_USER_GROP_BY_TYPE_ID_RESULT_NICKNAME = 15;			//同意/拒绝添加好友/群组

	public static final int UPDATE_PROFILE_BY_ID_TYPE = 16;	//更新头像等图片数据

	public static final int LINE_STATUS_ID_TYPE = 17;	//上线通知,下线通知

	
	public static final int CREATE_GROUP_BY_NAME_NUM_CHECK = 18;	//创建/更新群？讨论组？
	public static final int UPDATE_GROUP_BY_ID_NAME_SIGN_NUM_CHECK = 19;	// 更新群？讨论组？

	public static final int FIND_USERS_BY_GROUPID = 20;		//查询 cc群 cc用户 列表
	public static final int GET_USER_GROUP_CHAT_BY_TYPE_ID_START_HISTORY = 21;	//聊天消息获取，并添加会话列表记录
	public static final int REMOVE_CHAT_SESSION_BY_ID = 22;	//删除会话

	
	

	public static final int TURN_DELETE_RELEATIONSHIP_BY_GROUPNAME = 97;//被踢出群
	public static final int TURN_DELETE_RELEATIONSHIP_BY_FRIENDNAME = 98;//被删除好友
	public static final int DELETE_RELEATIONSHIP_BY_GROUPID_USERID = 99;//踢出群
	public static final int DELETE_RELEATIONSHIP_BY_TYPE_ID = 100;//删除关系
	

	////////////////////////////////////////////////////////////////////////////
	//匿名模块消息,用户只能加入一个聊天室，返回即退出，文本消息不存储，可是语音呢，图片呢？暂缓到聊天室关闭删除, 私聊功能？
	public static final int DOLL_CREATE_BY_NAME_NUM = 200;//创建聊天室,name,num
	public static final int DOLL_INTO_BY_NAME = 201;//加入聊天室 
	public static final int DOLL_CHAT_BY_TONAME_TYPE_MSG = 202;//聊天
	
	public static final int DOLL_EXIT = 203;//退出聊天室 
	public static final int DOLL_ROOM_LIST = 204;//聊天室 列表

	public static final int DOLL_ROOM_UPDATE_BY_NAME = 205;//聊天室 刷新
	public static final int DOLL_IN_OR_OUT_BY_NAME_TYPE = 206;//某房间进入退出房间信息


	///////////////////////////////////////////////////////////////////
    //图片传输监控模块
    public static final int SYS_DECT_ON = 300;//检测提醒



	
	
}
