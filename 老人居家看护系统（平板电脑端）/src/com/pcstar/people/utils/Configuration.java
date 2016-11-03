package com.pcstar.people.utils;

/**
 * 描述：配置信息 包括基本的静态常量信息和指令集 
 */
public class Configuration {		
	public static boolean isRun = true;//控制读串口的数据
	public static boolean isDefend = true;//是否布防
	/**
	 * 描述：socket通信配置信息
	 */
	public final static String PHONE_IP = "192.168.1.22";//手机端IP
	public final static int PHONE_PORT = 8080;//手机端口
	/**
	 * 描述：记录操作第几路继电器
	 */
	public static final String openPort1 = "050010FF00";
	public static final String closePort1 = "0500100000";
	public static final String openPort2 = "050011FF00";
	public static final String closePort2 = "0500110000";
	public static final String openPort3 = "050012FF00";
	public static final String closePort3 = "0500120000";
	public static final String openPort4 = "050013FF00";
	public static final String closePort4 = "0500130000";
	
	public static final String RS485 = "AT+AA_RS485TX=";
}
