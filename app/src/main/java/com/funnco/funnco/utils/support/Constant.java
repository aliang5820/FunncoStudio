package com.funnco.funnco.utils.support;

/**
 * Fragment标识
 */
public class Constant {

		//Btn的标识 1 2 4 8
	  public static final int BTN_FLAG_SCHEDULE = 0x01;
	  public static final int BTN_FLAG_SERVICE = 0x01 << 1;
	  public static final int BTN_FLAG_UPLOAD = 0x01 << 2;
	  public static final int BTN_FLAG_MY = 0x01 << 3;
	  
	  //Fragment标识
	  public static final String FRAGMENT_FLAG_SCHEDULE = "日程";
	  public static final String FRAGMENT_FLAG_SERVICE = "服务";
	  public static final String FRAGMENT_FLAG_UPLOAD = "作品";
	  public static final String FRAGMENT_FLAG_MY = "个人中心";
	  public static final String FRAGMENT_FLAG_SIMPLE = "simple"; 

	public static final String COOKIE_PHPSESSID = "PHPSESSID";
	public static final String COOKIE_FUNNCO_UID = "funnco_uid";
	public static final String COOKIE_FUNNCO_CLIENT_PWD = "funnco_client_pwd";
	public static final String COOKIE_FUNNCO_ONLINE_PWD = "funnco_online_pwd";

}
