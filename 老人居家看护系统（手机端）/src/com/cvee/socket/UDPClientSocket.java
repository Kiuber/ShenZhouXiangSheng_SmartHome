package com.cvee.socket;
/**
 * 描述：此类用于向终端通过UDP协议发送信息
 */
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClientSocket {
	/**
	 * 描述：通过UDP协议向服务端发送信息
	 */
	public static void send(String str,String ip,int port){
		DatagramSocket socket = null;
		//首先创建一个DatagramSocket对象		
		try {
			if (socket == null) {
				socket = new DatagramSocket();
			}					 
			 //创建一个InetAddree
			 InetAddress serverAddress = InetAddress.getByName(ip);
			 byte data [] = str.getBytes("utf-8");  //把传输内容分解成字节
			 //创建一个DatagramPacket对象，并指定要讲这个数据包发送到网络当中的哪个、地址，以及端口号
			 DatagramPacket packet = new
			 DatagramPacket(data,data.length,serverAddress,port);
			 //调用socket对象的send方法，发送数据
			 socket.send(packet);
			 socket.close(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
