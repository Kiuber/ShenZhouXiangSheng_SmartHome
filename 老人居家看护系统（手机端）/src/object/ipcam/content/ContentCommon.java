
package object.ipcam.content;

public class ContentCommon{
	public static final String STR_CAMERA_INFO_RECEIVER = "object.ipcam.client.camerainforeceiver" ;
	public static final String STR_CAMERA_ADDR = "camera_addr" ;
	public static final String STR_CAMERA_PORT = "camera_port" ;
	public static final String STR_CAMERA_NAME = "camera_name" ;
	public static final String STR_CAMERA_MAC = "camera_mac" ;
	public static final String STR_CAMERA_USER = "camera_user" ;
	public static final String STR_CAMERA_PWD = "camera_pwd" ;
	public static final String STR_CAMERA_SNAPSHOT = "camera_snapshot" ;
	
	public static final String STR_CAMERA_OLD_ADDR = "camera_old_addr" ;
	public static final String STR_CAMERA_OLD_PORT = "camera_old_port" ;
	
	public static final String STR_CAMERA_TYPE = "camera_type";
	public static final String STR_STREAM_TYPE = "stream_type";
	public static final String STR_H264_MAIN_STREAM = "h264_main_stream";
	public static final String STR_H264_SUB_STREAM = "h264_sub_stream";
	public static final String STR_MJPEG_SUB_STREAM = "mjpeg_sub_stream";
	
	public static final int DEFAULT_PORT = 81;
	public static final String DEFAULT_USER_NAME = "admin" ;
	public static final String DEFAULT_USER_PWD = "" ;
	
	public static final String CAMERA_OPTION = "camera_option" ;
	public static final int ADD_CAMERA = 1;
	public static final int EDIT_CAMERA = 2;
	public static final int INVALID_OPTION = 0xffff;
	
	public static final int CAMERA_TYPE_MJPEG = 0;
	public static final int CAMERA_TYPE_H264 = 1;
	public static final int H264_MAIN_STREAM = 0;
	public static final int H264_SUB_STREAM = 1;
	public static final int MJPEG_SUB_STREAM = 3;
	
	//ptz control command ---------------------------------
	public static final int CMD_PTZ_UP = 0;
	public static final int CMD_PTZ_UP_STOP = 1;
	public static final int CMD_PTZ_DOWN = 2;
	public static final int CMD_PTZ_DOWN_STOP = 3;
	public static final int CMD_PTZ_LEFT = 4;
	public static final int CMD_PTZ_LEFT_STOP = 5;
	public static final int CMD_PTZ_RIGHT = 6;
	public static final int CMD_PTZ_RIGHT_STOP = 7;
	
}