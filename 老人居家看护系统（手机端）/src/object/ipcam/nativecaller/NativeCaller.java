package object.ipcam.nativecaller;

import android.content.Context;
import android.view.Surface;


public class NativeCaller {
    /** Called when the activity is first created. */
	
	static {		
		System.loadLibrary("ffmpeg");	
		System.loadLibrary("object_jni");		
	}
		
	private static final String LOG_TAG = "NativeCaller" ;	
	
	public native static void StartSearch(Context object) ;	
	public native static void StopSearch();
	public native static void Init();
	public native static void Free();
	public native static int StartPlay(int cameratype, int streamtype, String ipaddr, int port, String user, String pwd, Surface surface, Context object);
	public native static int StopPlay();
	public native static int StartAudio();
	public native static int StopAudio();
	public native static void AudioData(byte[] data, int len);
	public native static int StartTalk();
	public native static int StopTalk();
	
    
}