package com.pcstar.people.activity;

/**
 * 描述：此类为程序运行第一个加载的Activity，
 * 其主要作用是：
 * 1.控制其余Activity的切换
 * 2.建立存储数据的数据库和表
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.pcstar.people.daomain.BrowserInfo;
import com.pcstar.people.socket.BluetoothChatService;
import com.pcstar.people.socket.UDPClientSocket;
import com.pcstar.people.sqlite.Mysqlite;
import com.pcstar.people.utils.Configuration;
import com.pcstar.people.utils.Utils;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends TabActivity implements OnCheckedChangeListener {
	public static TabHost mHost = null;
	private Button backButton;
	private TextView mTitle_bt;
	private TextView mTitle_el;
	private Mysqlite mySqlite;
	private AlertDialog alertDialog;
	private boolean WEBSOCKET_ISCONNECTION = false;
	private boolean IS_RECONNECTION = true;

	private final static WebSocketConnection mConnection = new WebSocketConnection();
	byte[] buf = new byte[1];

	// 调试
	private static final String TAG = "MainActivity";
	private static final boolean D = true;

	// 从BluetoothChatService发送处理程序的消息类型
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// 连接设备的名称
	private String mConnectedDeviceName = null;
	// 本地蓝牙适配器
	private BluetoothAdapter mBluetoothAdapter = null;
	// 成员对象聊天服务
	private static BluetoothChatService mChatService = null;
	
	private View view;
	private EditText ipEdit;//IP地址输入框
	private String PCIP;
	private SharedPreferences sharedPreferences;//记录摄像机IP

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_maintabs);
		// 建报警信息表
		setTable();
		mHost = getTabHost();
		
		view = getLayoutInflater().inflate(R.layout.alertdialog_style, null);
		ipEdit = (EditText)view.findViewById(R.id.alertdialog_edit); 
		ipEdit.setFocusableInTouchMode(false);
		ipEdit.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				ipEdit.setFocusableInTouchMode(true);
			}
		});
		// 获取存储的pc端ip并显示到控件中
		sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
		PCIP = sharedPreferences.getString("PCIP", "");
		ipEdit.setText(PCIP);
		setPCIPDialog();
		
		backButton = (Button) findViewById(R.id.maintabs_backButton);
		mTitle_bt = (TextView) findViewById(R.id.main_textView1);
		mTitle_el = (TextView) findViewById(R.id.main_textView2);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Utils.showExitDialog(MainActivity.this);
			}
		});

		// 获取本地蓝牙适配器
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// 判断蓝牙是否可用
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "蓝牙是不可用的", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		initRadios();
		
		// 开启监听UDP通信信息
		UDPServer Server=new UDPServer();
        Server.start();
        

	}

	/**
     * 描述：仿真平台IP输入框
	 * @param pCIP 
     */
    public void setPCIPDialog(){    	
    	final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this) 
    	.setTitle("请输入仿真平台IP") 
    	.setIcon(android.R.drawable.ic_dialog_info) 
    	.setView(view) 
    	.setPositiveButton("确定", new AlertDialog.OnClickListener() {			
    		@Override
    		public void onClick(DialogInterface dialog, int which) {
    			PCIP = ipEdit.getText().toString().trim();
    			//保存IP
    			Editor editor = sharedPreferences.edit();
    			editor.putString("PCIP", PCIP);
    			editor.commit();
    			// 连接web服务器并接收服务器消息
    			startWebSocket(PCIP);
    		}
    	})
    	.setNegativeButton("取消", null);
    	alertDialog = builder.create();
    	alertDialog.setCanceledOnTouchOutside(false);
    	alertDialog.show();
    }

	/**
	 * 描述：初始化底部按钮
	 */
	private void initRadios() {
		mHost.addTab(buildTabSpec("a0", "a", new Intent(this,CamerActivity.class)));
		mHost.addTab(buildTabSpec("b0", "b", new Intent(this,NewsFindActivity.class)));
		((RadioButton) findViewById(R.id.radio_button0)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button1)).setOnCheckedChangeListener(this);
	}

	/**
	 * 描述：切换模块
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.radio_button0:
				mHost.setCurrentTabByTag("a0");
				break;
			case R.id.radio_button1:
				mHost.setCurrentTabByTag("b0");
				break;
			}
		}
	}

	private TabHost.TabSpec buildTabSpec(String tag, String resLabel,
			Intent content) {
		return mHost
				.newTabSpec(tag)
				.setIndicator(resLabel,getResources().getDrawable(R.drawable.ic_launcher))
				.setContent(content);
	}

	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// 判断蓝牙是否打开.
		// setupChat在onActivityResult()将被调用
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			if (mChatService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");
		if (mChatService != null) {
			if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
				mChatService.start();
			}
		}
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");
		// 初始化BluetoothChatService进行蓝牙连接
		mChatService = new BluetoothChatService(this, mHandler);
	}

	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 停止蓝牙
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	// 此Handler处理BluetoothChatService传来的消息
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case BluetoothChatService.STATE_CONNECTED:
					mTitle_bt.setText(R.string.title_connected_to);
					mTitle_bt.append(mConnectedDeviceName);
					break;
				case BluetoothChatService.STATE_CONNECTING:
					mTitle_bt.setText(R.string.title_connecting);
					break;
				case BluetoothChatService.STATE_LISTEN:
				case BluetoothChatService.STATE_NONE:
					mTitle_bt.setText(R.string.title_bt_connected);
					break;
				}
				break;
			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				String writeMessage = new String(writeBuf);
				break;
			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// 读取到的数据
				String readMessage = new String(readBuf, 0, msg.arg1);
				Log.d(TAG, "收到的数据："+readMessage);
				// ------------------------注意的代码---------------------------
				// ------*处理zigbee信息*-------
				if (readMessage.startsWith("AT+AA_Z_RX_DT=Z6055,5,0")) {
					// 弹出报警事件提示框，并发送控制继电器关闭报警设备指令
					setDialog("水浸报警","AT+AA_Z_TX_DT=Z6057,6,"+ Utils.getCRC("01"+ Configuration.closePort2)+ "\r");
					// 发送控制继电器控制执行器响应
					MainActivity.sendMessage("AT+AA_Z_TX_DT=Z6057,6,"+ Utils.getCRC("01"+ Configuration.openPort2)+ "\r");
					// 将报警事件写入数据库
					writeNewsToSql("水浸报警");
					// 向手机端发送报警信息 只需修改报警事件名称字段即可
					UDPClientSocket.send("SHUIJIN",Configuration.PHONE_IP,Configuration.PHONE_PORT);
					// 向pc端发送报警信息，只需修改报警事件名称即可
					sendData("TaskingR", "", "老太太报警");
				}
				// ------------*处理315信息*--------------
				if (readMessage.startsWith("AT+AA_C_315=")) {
					if (readMessage.contains("AT+AA_C_315=60")) {
						// 弹出报警事件提示框，并发送控制继电器关闭报警设备指令
						setDialog("手持报警按钮","AT+AA_Z_TX_DT=Z6057,6,"+ Utils.getCRC("01"+ Configuration.closePort2)+ "\r");
						// 发送控制继电器控制执行器响应
						MainActivity.sendMessage("AT+AA_Z_TX_DT=Z6057,6,"+ Utils.getCRC("01"+ Configuration.openPort2)+ "\r");
						// 将报警事件写入数据库
						writeNewsToSql("手持报警按钮");
						// 向手机端发送报警信息 只需修改报警事件名称字段即可
						UDPClientSocket.send("SHOUCHI",Configuration.PHONE_IP,Configuration.PHONE_PORT);
						// 向pc端发送报警信息，只需修改报警事件名称即可
						sendData("TaskingR", "", "手持报警按钮");
					}

					// ----------*处理RS485信息*-----------
				} else if (readMessage.startsWith("AT+AA_Z_RX_DT=Z6057,6,")) {
					String alrm = readMessage.split(",6,")[1];
					if (alrm.length() == 14) {
						String string = hexString2binaryString( alrm.subSequence(6, 8) + "").substring(4, 8);
						// 匹配继电器的地址和功能码
						if (alrm.substring(0, 4).equals("0102")) {
							// if (Configuration.isDefend == true) {} 此代码是布防/撤防控制代码
							// 根据实际情况修改0/1的状态完成报警指令响应
							// 第四路继电器
							if (string.substring(0, 1).equals("0")) {
								
							} else if (string.substring(1, 2).equals("0")) {

								// 第二路继电器
							} else if (string.substring(2, 3).equals("0")) {
								// 第一路继电器
							} else if (string.substring(3, 4).equals("0")) {
								// 弹出报警事件提示框，并发送控制继电器关闭报警设备指令
								setDialog("固定报警按钮报警","AT+AA_Z_TX_DT=Z6057,6,"+ Utils.getCRC("01"+ Configuration.closePort2)+ "\r");
								// 发送控制继电器控制执行器响应
								MainActivity.sendMessage("AT+AA_Z_TX_DT=Z6057,6,"+ Utils.getCRC("01"+ Configuration.openPort2)+ "\r");
								// 将报警事件写入数据库
								writeNewsToSql("固定报警按钮报警");
								// 向手机端发送报警信息 只需修改报警事件名称字段即可
								UDPClientSocket.send("GUDING",Configuration.PHONE_IP,Configuration.PHONE_PORT);
								// 向pc端发送报警信息，只需修改报警事件名称即可
								sendData("TaskingR", "", "固定报警按钮报警");
							}
						}
					}
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// 保存连接设备的名字
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),"连接到" + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// 当DeviceListActivity返回与设备连接的消息
			if (resultCode == Activity.RESULT_OK) {
				// 得到链接设备的MAC
				String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// 得到BLuetoothDevice对象
				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
				// 试图连接到设备
				mChatService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// 判断蓝牙是否启用
			if (resultCode == Activity.RESULT_OK) {
				// 建立连接
				setupChat();
			} else {
				Log.d(TAG, "蓝牙未启用");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			// 连接设备
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			// 允许被发现设备
			ensureDiscoverable();
			return true;
		}
		return false;
	}

	// 描述：建表的方法

	public void setTable() {
		mySqlite = new Mysqlite(MainActivity.this, "users.db", 1);
		String sql = "create table if not exists alerting_table(time varchar(10) ,news varchar(10))";
		SQLiteDatabase db = mySqlite.getWritableDatabase();
		db.execSQL(sql);
		db.close();
	}

	// 描述：报警提示对话框

	public void setDialog(String news, final String order1) {
		final Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("报警提示");
		builder.setMessage(news + "正在报警……");
		builder.setPositiveButton("关闭报警器", new AlertDialog.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				sendMessage(order1);
			}
		});
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
	}

	/**
	 * 描述：向数据库写报警信息
	 */
	public void writeNewsToSql(String strNews) {
		SQLiteDatabase db = mySqlite.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("time", Utils.getTime());
		values.put("news", strNews);
		db.insert("alerting_table", null, values);
		db.close();
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 *            发送的内容
	 */
	public static void sendMessage(String message) {
		if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
			return;
		}

		if (message.length() > 0) {
			byte[] send = message.getBytes();
			mChatService.write(send);
		}
	}

	/**
	 * @param
	 */

	/**
	 * 描述：退出程序
	 */
	@Override
	public void finish() {
		Utils.showExitDialog(MainActivity.this);
	}

	/**
	 * 描述：连接web服务器并接收服务器消息
	 * 
	 * @param pcIP
	 *            pc端IP地址
	 */
	private void startWebSocket(String pcIP) {
		final String url = "ws://" + pcIP + ":2012";
		try {
			mConnection.connect(url, new WebSocketHandler() {

				@Override
				public void onClose(int code, String reason) {
					// TODO Auto-generated method stub
					super.onClose(code, reason);
					WEBSOCKET_ISCONNECTION = false;
					mConnection.disconnect();
					// 给用户提示是否重新连接服务器
					reConnection();
				}

				@Override
				public void onOpen() {
					// TODO Auto-generated method stub
					super.onOpen();
					WEBSOCKET_ISCONNECTION = true;
					mTitle_el.setText("仿真平台已连接");
				}

				@Override
				public void onTextMessage(String payload) {
					// TODO Auto-generated method stub
					super.onTextMessage(payload);
//					Toast.makeText(MainActivity.this, "收到来自服务器消息：" + payload,Toast.LENGTH_SHORT).show();
//					Gson gson = new Gson();
//					BrowserInfo browserInfo = gson.fromJson(payload,BrowserInfo.class);
				}
			});
		} catch (WebSocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 描述：是否重新连接web服务器提示框
	 */
	public void reConnection() {
		if (IS_RECONNECTION) {
			// Utils.delay(6000);
			final Builder builder = new AlertDialog.Builder(MainActivity.this);
			// isDialog = true;

			builder.setTitle("提示");
			builder.setMessage("是否重新连接服务器");
			builder.setPositiveButton("是", new AlertDialog.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					startWebSocket(PCIP);
				}
			});
			builder.setNegativeButton("否", new AlertDialog.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.dismiss();
				}
			});
			alertDialog = builder.create();
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.show();

		}
	}

	/**
	 * 向web发数据
	 * 
	 * @param type
	 * @param id
	 * @param order
	 */
	public void sendData(String type, String id, String order) {
		if (WEBSOCKET_ISCONNECTION == false) {
			Toast.makeText(MainActivity.this, "未连接服务器", Toast.LENGTH_SHORT).show();
		} else {
			mConnection.sendTextMessage("{\"type\":\"" + type+ "\",\"name\": \"" + id + "\",\"order\": \"" + order+ "\"}");
		}
	}

	/**
	 * 十六进制转二进制
	 * 
	 * @param hexString
	 *            十六进制字符串
	 * @return
	 */
	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000"+ Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}
	
	 /**
		 * 与终端或应用中心平台（仿真机）进行通信
		 */
	    /**
	     * 描述：UDP通信服务类
	     * 主要作用：
	     * 1.接收平板或应用中心平台（仿真机）传过来的数据并进行相应处理
	     * @author Administrator
	     *
	     */
	    class UDPServer extends Thread {
	    	  
	        private static final int PORT = 8080;
	      
	        private byte[] msg = new byte[100]; 
	      
	        private boolean life = true;      
	        public UDPServer() {
	        	
	        }
	      
	        /** 
	         * @return the life 
	         */
	        public boolean isLife() {
	            return life; 
	        } 
	      
	        /** 
	         * @param life 
	         *            the life to set 
	         */
	        public void setLife(boolean life) { 
	            this.life = life; 
	        } 
	      
	        @Override
	        public void run() {
	            DatagramSocket dSocket = null; 
	            
	            try { 
	                dSocket = new DatagramSocket(PORT); 
	                while (life) { 
	                    try {
	                    	DatagramPacket dPacket = new DatagramPacket(msg, msg.length);
	                    	dSocket.setSoTimeout(1000);
	                        dSocket.receive(dPacket); 
	                        String str = new String(msg,0,dPacket.getLength());
	                        Message msg = new Message();
	    					msg.what = 0x123;
	    					msg.obj = str;
	    					UDPhandler.sendMessage(msg);
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                    } 
	                } 
	            } catch (SocketException e) {
	                e.printStackTrace();
	            } 
	        }

	    }
	    Handler UDPhandler = new Handler() {

	        public void handleMessage(Message msg) {
	        	if (msg.what == 0x123) {
	        		String msgstr = msg.obj.toString();
	        		if (msgstr.equals("isDefend=false")) {
	        			// 撤防
	        			CamerActivity.disarm();
	        			Toast.makeText(MainActivity.this, "进入撤防状态", Toast.LENGTH_SHORT).show();
					}
	        		else if (msgstr.equals("isDefend=true")) {
	        			// 布防
	        			CamerActivity.defence();
	        			Toast.makeText(MainActivity.this, "进入布防状态", Toast.LENGTH_SHORT).show();
					}
	        		//-----------------------------注意的代码----------------------------------------
	        		// 接收手机端反馈信息 并 处理
	        		else if (msgstr.equals("CLOSESHUIJIN")) {
	        			MainActivity.sendMessage("AT+AA_Z_TX_DT=Z6057,6,"+ Utils.getCRC("01"+ Configuration.closePort2)+ "\r");
	        		}
	        		//-----------------------------注意的代码----------------------------------------
				}
	        }      
	    };

}
