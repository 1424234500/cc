package net;

import service.NetService;
import util.tools.MyJson;
import util.Tools;
import android.content.Context;
import android.content.Intent;

//传递信息 封装json 调用net发送
public   class MSGSender {
 
	
	//向service发送传递消息
	private static void send(Context context, String jsonstr) {
		Tools.out("MSG.send." + jsonstr);
		context.startService(new Intent(context, NetService.class).putExtra("msg", jsonstr));
	}
	
	public static void close(Context context){	//只下线
		send(context, MyJson.makeJson(MSG.CLOSE, ""));
	}
	public static void exitApp(Context context){	//exitapp
		send(context, MyJson.makeJson(MSG.CLOSE, "exitapp"));
	}
	
	public static void getProfileByPath(Context context, String id) {
		send(context, MyJson.makeJson(MSG.PROFILE_PATH_BY_ID, id));
	}
	public static void loginByIdPwd(Context context, String id, String pwd) {
		pwd = util.tools.MD5.make(id, pwd);

		send(context, MyJson.makeJson(MSG.LOGIN_BY_ID_PWD, id, pwd));
	}

	public static void registe(Context context, String username, String email, String sex, String pwd) {
		send(context, MyJson.makeJson(MSG.REGISTE_BY_USERNAME_EMAIL_SEX_PWD, username, email, sex, pwd));
	}
	public static void update(Context context, String...objects) {
		send(context, MyJson.makeJson(MSG.UPDATE_BY_USERNAME_SIGN_SEX_OLDPWD_NEWPWD, objects));
	}
	
	public static void findById(Context context, String id){
		send(context, MyJson.makeJson(MSG.FIND_USERS_GROUPS_BY_ID, id));
	}
	public static void findByGroupId(Context context, String groupid){
		send(context, MyJson.makeJson(MSG.FIND_USERS_BY_GROUPID, groupid));
	}
	public static void getUserGroupDetailByTypeId(Context context, String type, String id) {
		send(context, MyJson.makeJson(MSG.GET_USER_GROUP_DETAIL_BY_TYPE_ID, type, id));
	}

	public static void addUserGroupByTypeIdYanZhengBeizhu(Context context, String type, String id, String yanzhen, String nickname) {
		send(context, MyJson.makeJson(MSG.ADD_USER_GROP_BY_TYPE_ID_YANZHEN_NICKNAME, type, id, yanzhen, nickname));
	}	
	public static void resultUserGroupByTypeIdResultBeizhu(Context context, String type, String id, String result, String nickname) {
		send(context, MyJson.makeJson(MSG.RESULT_USER_GROP_BY_TYPE_ID_RESULT_NICKNAME, type, id, result, nickname));
	}
	public static void resultUserGroupByTypeIdResultBeizhu(Context context, String type, String id, String result, String nickname, String groupid) {
		send(context, MyJson.makeJson(MSG.RESULT_USER_GROP_BY_TYPE_ID_RESULT_NICKNAME, type, id, result, nickname, groupid));
	}

	public static void getContact(Context context) {
		send(context, MyJson.makeJson(MSG.CONTACT_USER_GROUP_MAP ));
	}

	public static void getChatMsgByTypeIdStarttime(Context context,String type,  String id, String starttime) {
		send(context, MyJson.makeJson(MSG.GET_USER_GROUP_CHAT_BY_TYPE_ID_START, type,id, starttime ));
	}
	public static void getChatMsgByTypeIdStarttimeHistory(Context context,String type,  String id, String starttime) {
		send(context, MyJson.makeJson(MSG.GET_USER_GROUP_CHAT_BY_TYPE_ID_START_HISTORY, type,id, starttime ));
	}
	public static void sendChatMsgByGtypeToidTypeTimeMsg(Context context,String gtype, String toid, String type, String time, String msg) {
		send(context, MyJson.makeJson(MSG.SEND_CHATMSG_BY_GTYPE_TOID_TYPE_TIME_MSG, gtype, toid, type, time, msg));
	}
	
	public static void removeChatSessionById(Context context, String id){
		send(context, MyJson.makeJson(MSG.REMOVE_CHAT_SESSION_BY_ID,id));

	}
	
	
	
	//添加聊天会话表
	public static void addChatSession(Context context,String type,  String id) {
		send(context, MyJson.makeJson(MSG.ADD_CHAT_SESSION_BY_TYPE_ID, type,id));
	}
	//获取会话列表
	public static void getSessions(Context context) {
		send(context, MyJson.makeJson(MSG.GET_CHAT_SESSIONS ));
	}
	//心跳？
	public static void beats(Context context) {
		send(context, MyJson.makeJson(MSG.BEATS ));
	}
	
	
	//更新好友/群友的昵称
	public static void updateNickname(Context context, String id, String nickname, String groupid) {
		send(context, MyJson.makeJson(MSG.UPDATE_NICKNAME_BY_ID_NICKNAME_GROUPID, id, nickname, groupid));
	}
	//更新头像和背景墙
	public static void updateProfile(Context context, String id, String type) {//profile/profilewall
		send(context, MyJson.makeJson(MSG.UPDATE_PROFILE_BY_ID_TYPE, id, type));
	}
	
	//删除好友 or 退出群组
	public static void deleteReleationship(Context context,String type,  String id) {
		send(context, MyJson.makeJson(MSG.DELETE_RELEATIONSHIP_BY_TYPE_ID, type,id));
	}
	//踢出群组
	public static void deleteUserFromGroup(Context context,String userid,  String groupid) {
		send(context, MyJson.makeJson(MSG.DELETE_RELEATIONSHIP_BY_GROUPID_USERID, groupid,userid));
	}
	public static void createGroupByNameNumCheck(Context context, String name, String num, String check) {
		send(context, MyJson.makeJson(MSG.CREATE_GROUP_BY_NAME_NUM_CHECK, name, num, check));
	}
	public static void updateGroupByNameSignNumCheck(Context context,String id,  String name,String sign, String num, String check) {
		send(context, MyJson.makeJson(MSG.UPDATE_GROUP_BY_ID_NAME_SIGN_NUM_CHECK, id, name,sign, num, check));
		
	}
	
	
	
	
	
	////////////////////////////////////////////////////
	//匿名模块
	public static void getDollRooms(Context context ) {
		send(context, MyJson.makeJson(MSG.DOLL_ROOM_LIST));
	}
	public static void createDollByNameNum(Context context, String name, String num ) {
		send(context, MyJson.makeJson(MSG.DOLL_CREATE_BY_NAME_NUM, name, num));
	}
	public static void intoDollByName(Context context, String name ) {
		send(context, MyJson.makeJson(MSG.DOLL_INTO_BY_NAME, name));
	}
	public static void updateDollByName(Context context, String name ) {
		send(context, MyJson.makeJson(MSG.DOLL_ROOM_UPDATE_BY_NAME, name));
	}
	public static void exitDoll(Context context ) {
		send(context, MyJson.makeJson(MSG.DOLL_EXIT));
	}
	public static void chatDollByTypeMsg(Context context, String toname, String type, String msg) {
		send(context, MyJson.makeJson(MSG.DOLL_CHAT_BY_TONAME_TYPE_MSG, toname, type, msg));
	}
	
	
}
