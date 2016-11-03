package com.cvee.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.cvee.R;
import com.cvee.socket.UDPClientSocket;
import com.cvee.utils.Configuration;
import com.cvee.utils.MyCamera;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.CameraListener;
import com.tutk.IOTC.IMonitor;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.MediaCodecListener;
import com.tutk.IOTC.MonitorClickListener;
import com.tutk.IOTC.st_LanSearchInfo;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq;
import com.tutk.Logger.Glog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

public class CamerActivity extends Activity implements ViewSwitcher.ViewFactory,
		IRegisterIOTCListener, MonitorClickListener,
		android.view.View.OnTouchListener, CameraListener, MediaCodecListener,
		OnClickListener {

	private static final String LOG_TAG = "CamerActivity" ;	
	
	private final int THUMBNAIL_LIMIT_HEIGHT = 720;
	private final int THUMBNAIL_LIMIT_WIDTH = 1280;
	private String PATH = "";
	private IMonitor mHardMonitor;
    private MyCamera camera;
    
	//public static MyCamera camera;
	
	private SearchResult r;
	/**
	 * 布防/撤防
	 */
	private static ToggleButton btn_bf_cf;
	/**
	 * 布防/撤防状态
	 */
	private static TextView tv_bf_cf;

	private List<SearchResult> list = new ArrayList<SearchResult>();
	
	private Handler handler = new Handler() {};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cameractivity_main);
		MyCamera.init();

		mHardMonitor = (IMonitor) findViewById(R.id.hardMonitor);
		// -------用户名、密码如果没改动填写默认的"admin","admin" 如果改了填写改后的用户名、密码---------------
		camera = new MyCamera("Camera", PATH, "admin","admin");
		camera.registerIOTCListener(this);
		camera.SetCameraListener(this);
//		camera.connect("PK6MRNNBA61L9KF6111A");
		// -------------------------------------注意用户名、密码------------------
		camera.start(Camera.DEFAULT_AV_CHANNEL, "admin", "admin");
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
				SMsgAVIoctrlGetSupportStreamReq.parseContent());
		
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
		
		btn_bf_cf = (ToggleButton) findViewById(R.id.btn_BFCF);
		tv_bf_cf = (TextView) findViewById(R.id.tv_BFCF);
		
		btn_bf_cf.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					btn_bf_cf.setBackgroundResource(R.drawable.bufang_selector);
					Configuration.isDefend = false;
					tv_bf_cf.setText("已撤防");
					UDPClientSocket.send("isDefend=false",Configuration.PAD_IP, Configuration.PAD_PORT);
					Toast.makeText(getApplicationContext(), "进入撤防状态",Toast.LENGTH_SHORT).show();
					
				}else {
					btn_bf_cf.setBackgroundResource(R.drawable.chefang_selector);
					Configuration.isDefend = true;
					tv_bf_cf.setText("已布防");
					UDPClientSocket.send("isDefend=true",Configuration.PAD_IP, Configuration.PAD_PORT);
					Toast.makeText(getApplicationContext(), "进入布防状态",Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		
		btn_bf_cf.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});



		mSearchBtn = (Button) findViewById(R.id.SearchBtn);
		mSearchBtn.setOnClickListener(this);

		left = (Button) findViewById(R.id.left);
		right = (Button) findViewById(R.id.right);
		left.setOnClickListener(this);
		right.setOnClickListener(this);
		up = (Button) findViewById(R.id.up);
		down = (Button) findViewById(R.id.down);
		up.setOnClickListener(this);
		down.setOnClickListener(this);
		
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);
		start.setOnClickListener(this);
		stop.setOnClickListener(this);
	}
	
	/**
	 * 布防
	 */
	public static void defence(){
		btn_bf_cf.setBackgroundResource(R.drawable.chefang_selector);
		Configuration.isDefend = true;
		tv_bf_cf.setText("已布防");
	}
	/**
	 * 撤防
	 */
	public static void disarm(){
		btn_bf_cf.setBackgroundResource(R.drawable.bufang_selector);
		Configuration.isDefend = false;
		tv_bf_cf.setText("已撤防");
	}


	private Button mSearchBtn;
	private Button left;
	private Button right;
	private Button up;
	private Button down;
	private Button start;
	private Button stop;

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				mHardMonitor.enableDither(true);
				mHardMonitor.attachCamera(camera, 0);
/*
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						camera.startShow(0, true, true,true);
						
					}
				}, 1500);*/
				// mHardMonitor.setMaxZoom(3.0f);
				
				
			}
		});

    if (camera != null) {
			
  
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					camera.startShow(0, true, true,true);
					
				}
			}, 1500);
			boolean sessionConnected = camera.isSessionConnected();
			Log.d(LOG_TAG, "摄像机状态：sessionConnected：" + sessionConnected);

		}

		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		/*
		 * if (camera != null) { camera.unregisterIOTCListener(this);
		 * camera.stopSpeaking(0); camera.stopListening(0); camera.stopShow(0);
		 * }
		 */
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		quitCamera();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:

			// quit();

			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void quit() {

		byte[] snapshot = null;
		Bitmap bmp = camera.Snapshot(0);
		if (bmp != null) {
			try {
				bmp = compressImage(camera.Snapshot(0));
				// if (bmp.getWidth() * bmp.getHeight() > THUMBNAIL_LIMIT_WIDTH
				// * THUMBNAIL_LIMIT_HEIGHT) {
				// if(!bmp.isRecycled())
				// bmp = Bitmap.createScaledBitmap(bmp, THUMBNAIL_LIMIT_WIDTH,
				// THUMBNAIL_LIMIT_HEIGHT, false);
				// }
				// snapshot = DatabaseManager.getByteArrayFromBitmap(bmp);
				bmp.recycle();

			} catch (OutOfMemoryError E) {
				Glog.D("LiveView", "compressImage OutOfMemoryError" + E);
				System.gc();
				// continue;
			}

		}

		/* return values to main page */

	}

	private Bitmap compressImage(Bitmap image) {

		Bitmap tempBitmap = image;

		try {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			image.compress(Bitmap.CompressFormat.JPEG, 5, baos);
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());
			if (image.getWidth() * image.getHeight() > THUMBNAIL_LIMIT_WIDTH
					* THUMBNAIL_LIMIT_HEIGHT) {
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize = 4;
				Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, opts);
				return bitmap;
			} else {
				Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
				return bitmap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tempBitmap;
	}

	@Override
	public void Unavailable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void zoomSurface(float arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void OnSnapshotComplete() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		/*
		 * switch (event.getAction()) { case MotionEvent.ACTION_DOWN: if (camera
		 * != null) { camera.startSpeaking(0); //camera.stopListening(0); }
		 * break; case MotionEvent.ACTION_UP: if (camera != null) {
		 * camera.stopSpeaking(0); //camera.startListening(0, false); } break; }
		 */
		return false;
	}

	@Override
	public void receiveChannelInfo(Camera arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveFrameData(Camera arg0, int arg1, Bitmap arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveFrameDataForMediaCodec(Camera arg0, int arg1,
			byte[] arg2, int arg3, int arg4, byte[] arg5, boolean arg6, int arg7) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveFrameInfo(Camera arg0, int arg1, long arg2, int arg3,
			int arg4, int arg5, int arg6) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveIOCtrlData(Camera arg0, int arg1, int arg2, byte[] arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveSessionInfo(Camera arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			
		case R.id.SearchBtn:
			doLanSearch();
			break;
		case R.id.up:
			SetPTZ((byte) 1);
			break;
		case R.id.down:
			SetPTZ((byte) 2);
			break;
		case R.id.left:
			SetPTZ((byte) 3);
			break;
		case R.id.right:
			SetPTZ((byte) 6);
			break;
		case R.id.start:
		camera.startShow(0, true, true, true);
			break;
		case R.id.stop:
			camera.stopShow(0);
			break;
		default:
			break;
		}

	}

	@Override
	public void OnClick() {
		// TODO Auto-generated method stub

	}
	
	private void SetPTZ(byte cmd) {
		camera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND, setZoomParam(cmd));
	}
	
	private static byte[] setZoomParam(byte control) {
		byte[] result = new byte[8];

		result[0] = control;

		// byte[] rb = Packet.intToByteArray_Little(10);
		if (control == 23) {
			result[1] = (byte) 10;
			result[7] = 10;
		}
		if (control == 24) {
			result[1] = (byte) 10;
			result[7] = 10;
		}

		/*
		 * byte[] size = Packet.intToByteArray_Little(???);
		 * System.arraycopy(size, 0, result, 0, 4);
		 * 
		 * byte[] isSupportTimeZone =
		 * Packet.intToByteArray_Little(nIsSupportTimeZone);
		 * System.arraycopy(isSupportTimeZone, 0, result, 4, 4);
		 * 
		 * byte[] GMTDiff = Packet.intToByteArray_Little(nGMTDiff);
		 * System.arraycopy(GMTDiff, 0, result, 8, 4);
		 * 
		 * System.arraycopy(szTimeZoneString, 0, result, 12,
		 * szTimeZoneString.length);
		 */

		return result;
	}

	
	

	private static String getFileNameWithTime() {

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH) + 1;
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		int mSec = c.get(Calendar.SECOND);

		StringBuffer sb = new StringBuffer();
		sb.append("IMG_");
		sb.append(mYear);
		if (mMonth < 10)
			sb.append('0');
		sb.append(mMonth);
		if (mDay < 10)
			sb.append('0');
		sb.append(mDay);
		sb.append('_');
		if (mHour < 10)
			sb.append('0');
		sb.append(mHour);
		if (mMinute < 10)
			sb.append('0');
		sb.append(mMinute);
		if (mSec < 10)
			sb.append('0');
		sb.append(mSec);
		sb.append(".jpg");

		return sb.toString();
	}

	private static String getFileNameWithTime2() {

		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH) + 1;
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		int mHour = c.get(Calendar.HOUR_OF_DAY);
		int mMinute = c.get(Calendar.MINUTE);
		int mSec = c.get(Calendar.SECOND);

		StringBuffer sb = new StringBuffer();
		// sb.append("MP4_");
		sb.append(mYear);
		if (mMonth < 10)
			sb.append('0');
		sb.append(mMonth);
		if (mDay < 10)
			sb.append('0');
		sb.append(mDay);
		sb.append('_');
		if (mHour < 10)
			sb.append('0');
		sb.append(mHour);
		if (mMinute < 10)
			sb.append('0');
		sb.append(mMinute);
		if (mSec < 10)
			sb.append('0');
		sb.append(mSec);
		sb.append(".mp4");

		sb.toString();

		return sb.toString();
	}
	
	private void quitCamera() {

		if(camera!=null){
			camera.disconnect();
			camera.unregisterIOTCListener(this);
		}

		MyCamera.uninit();

		int pid = android.os.Process.myPid();
		android.os.Process.killProcess(pid);

	}
	
	private static boolean isSDCardValid() {

		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}
	
	
	private void doLanSearch() {

		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(CamerActivity.this, R.style.HoloAlertDialog));
		final AlertDialog dlg = builder.create();
		dlg.setTitle(getText(R.string.dialog_LanSearch));
		dlg.setIcon(android.R.drawable.ic_menu_more);

		LayoutInflater inflater = dlg.getLayoutInflater();
		View view = inflater.inflate(R.layout.search_device, null);
		dlg.setView(view);

		ListView lstSearchResult = (ListView) view.findViewById(R.id.lstSearchResult);
		Button btnRefresh = (Button) view.findViewById(R.id.btnRefresh);

		list.clear();
		st_LanSearchInfo[] arrResp = Camera.SearchLAN();

		if (arrResp != null && arrResp.length > 0) {
			for (st_LanSearchInfo resp : arrResp) {

				list.add(new SearchResult(new String(resp.UID).trim(), new String(resp.IP).trim(), (int) resp.port));
			}
		}else {
			Toast.makeText(this, "无可用设备", 1).show();
		}

		final SearchResultListAdapter adapter = new SearchResultListAdapter(dlg.getLayoutInflater());
		lstSearchResult.setAdapter(adapter);

		lstSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				r = list.get(position);
				PATH = r.UID;
				camera.connect(r.UID);
				dlg.dismiss();
			}
		});

		btnRefresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				list.clear();
				st_LanSearchInfo[] arrResp = Camera.SearchLAN();

				if (arrResp != null && arrResp.length > 0) {
					for (st_LanSearchInfo resp : arrResp) {
						list.add(new SearchResult(new String(resp.UID).trim(), new String(resp.IP).trim(), (int) resp.port));
					}
				}else {
					Toast.makeText(CamerActivity.this, "无可用设备", 1).show();
				}
				adapter.notifyDataSetChanged();
			}
		});

		dlg.show();
	}
	
	private class SearchResult {

		public String UID;
		public String IP;

		// public int Port;

		public SearchResult(String uid, String ip, int port) {
			UID = uid;
			IP = ip;
			// Port = port;
		}
	}
	
	
	
	private class SearchResultListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public SearchResultListAdapter(LayoutInflater inflater) {

			this.mInflater = inflater;
		}

		public int getCount() {

			return list.size();
		}

		public Object getItem(int position) {

			return list.get(position);
		}

		public long getItemId(int position) {

			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			final SearchResult result = (SearchResult) getItem(position);
			ViewHolder holder = null;

			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.search_device_result, null);

				holder = new ViewHolder();
				holder.uid = (TextView) convertView.findViewById(R.id.uid);
				holder.ip = (TextView) convertView.findViewById(R.id.ip);

				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			holder.uid.setText(result.UID);
			holder.ip.setText(result.IP);
			// holder.port.setText(result.Port);

			return convertView;
		}// getView()

		public final class ViewHolder {
			public TextView uid;
			public TextView ip;
		}
	}
}
