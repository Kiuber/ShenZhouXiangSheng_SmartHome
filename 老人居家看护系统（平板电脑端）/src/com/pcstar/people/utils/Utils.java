package com.pcstar.people.utils;
/**
 * 描述：此类为工具类包含一些项目中经常用的静态方法（函数）
 * 
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
public class Utils{
	public static Context context;	
	/**
	 * 检查网络可用
	 * @param activity 当前activity
	 * @return 返回是否可用
	 */
	public static boolean checkNet(Activity activity)
	{
		ConnectivityManager manager = (ConnectivityManager)activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if(manager == null)
		{
			return false;
		}
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if(networkInfo == null || !networkInfo.isAvailable())
		{
			return false;
		}
		return true;
	}
	/**
	 * 描述：弹出连接网络错误提示框
	 */
	public static void AlertNetError(final Context context)
	{
		AlertDialog.Builder alertError = new AlertDialog.Builder(context);
		alertError.setTitle("网络错误");
		alertError.setMessage("无法连接网络, 请检查网络配置");
		alertError.setNegativeButton("退出", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				android.os.Process.killProcess(android.os.Process.myPid()); 
				System.exit(0);
			}
		});
		alertError.setPositiveButton("设置", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
			}
		});
		alertError.create().show();
	}	
	/**
	 * 描述：杀进程退出程序
	 */
	public static void exit(){
		android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
	}
	/**
	 * 描述：判断手机号是否合法
	 */
	public static boolean isMobile(String mobiles){
		String str="^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
		Pattern p = Pattern.compile(str);	  
		Matcher m = p.matcher(mobiles);  		  
		return m.matches();
	} 
	/**
	 * 检查SD卡是否可用
	 * @return true表示可用
	 */
	public static boolean checkSDCard()
	{
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			return true;
		}
		return false;
	}
	/**
	 * 描述：  显示退出对话框
	 * @param context 上下文
	 */
	public static void showExitDialog(final Context context)
	{
		new AlertDialog.Builder(context).setTitle("系统提示！").setMessage("确定退出程序?").
		setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
		}).setNegativeButton("取消", null).create().show();
	}
	/**
	 * 描述：获得系统当前时间
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getTime(){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.d  HH:mm:ss");
    	Date curDate = new Date(System.currentTimeMillis());//获取当前时间        
    	String str = formatter.format(curDate);  
		return str;		
	}	
	
	
	/*
	 * 得到CRC值
	 */
	public static String getCRC(String str) {
		long lon = GetModBusCRC(str.replace(" ", ""));
		int h1, l0;
		l0 = (int) lon / 256;
		h1 = (int) lon % 256;
		String s = "";
		if (Integer.toHexString(h1).length() < 2) {
			s = "0" + Integer.toHexString(h1)+"";
		} else {
			s = Integer.toHexString(h1)+"";
		}
		if (Integer.toHexString(l0).length() < 2) {
			s = s + "0" + Integer.toHexString(l0)+"";
		} else {
			s = s + Integer.toHexString(l0)+"";
		}
		// System.out.println(s);
		return str +s;

	}
	
	/**
	 * 描述：获取CRC校验值
	 * 
	 * @param DATA
	 * @return
	 */
	public static long GetModBusCRC(String DATA) {
		long functionReturnValue = 0;
		long i = 0;

		long J = 0;
		int[] v = null;
		byte[] d = null;
		v = strToToHexByte(DATA);

		long CRC = 0;
		CRC = 0xffffL;
		for (i = 0; i <= (v).length - 1; i++) { // 1.把第一个8位二进制数据（既通讯信息帧的第一个字节）与16位的CRC寄存器的低8位相异或，把结果放于CRC寄存器；
			CRC = (CRC / 256) * 256L + (CRC % 256L) ^ v[(int) i];
			for (J = 0; J <= 7; J++) {
				// 2.把CRC寄存器的内容右移一位（朝低位）用0填补最高位，并检查最低位；
				// 3.如果最低位为0：重复第3步（再次右移一位）；
				// 如果最低位为1：CRC寄存器与多项式A001（1010 0000 0000
				// 0001）进行异或；
				// 4.重复步骤3和4，直到右移8次，这样整个8位数据全部进行了处理；
				long d0 = 0;
				d0 = CRC & 1L;
				CRC = CRC / 2;
				if (d0 == 1)
					CRC = CRC ^ 0xa001L;
			} // 5.重复步骤2到步骤5，进行通讯信息帧下一字节的处理；
		} // 6.最后得到的CRC寄存器内容即为：CRC码。
		CRC = CRC % 65536;
		functionReturnValue = CRC;
		return functionReturnValue;
	}
	
	private static int[] strToToHexByte(String hexString) {
		hexString = hexString.replace(" ", "");
		// 如果长度不是偶数，那么后面添加空格。

		if ((hexString.length() % 2) != 0) {
			hexString += " ";
		}

		// 定义数组，长度为待转换字符串长度的一半。
		int[] returnBytes = new int[hexString.length() / 2];

		for (int i = 0; i < returnBytes.length; i++)
			returnBytes[i] = (0xff & Integer.parseInt(
					hexString.substring(i * 2, i * 2 + 2), 16));
		return returnBytes;
	}
}
