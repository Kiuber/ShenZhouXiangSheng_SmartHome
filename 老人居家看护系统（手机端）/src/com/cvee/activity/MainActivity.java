package com.cvee.activity;
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.app.AlertDialog.Builder;
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
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.Toast;

import com.cvee.R;
import com.cvee.socket.UDPClientSocket;
import com.cvee.sqlite.Mysqlite;
import com.cvee.utils.Configuration;
import com.cvee.utils.Utils;
public class MainActivity extends TabActivity implements OnCheckedChangeListener{
	
	public static TabHost mHost = null;	
	private Mysqlite mySqlite;
	private AlertDialog alertDialog;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_maintabs);
		
		//建报警信息表
		setTable();
		
		mHost = getTabHost();		
		initRadios();
		
		// 开启监听UDP通信信息
		UDPServer Server=new UDPServer();
		Server.start();
	}
	/**
	 * 描述：初始化底部按钮
	 */
	private void initRadios(){
			mHost.addTab(buildTabSpec("a0", "a", new Intent(this,CamerActivity.class)));
			mHost.addTab(buildTabSpec("b0", "b", new Intent(this,NewsFindActivity.class)));
		((RadioButton) findViewById(R.id.radio_button0))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button1))
				.setOnCheckedChangeListener(this);
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
    /**
	 * 描述：建表的方法
	 */
    public  void setTable(){
    	mySqlite=new Mysqlite(MainActivity.this, "users.db", 1);
    	String sql ="create table if not exists alerting_table(time varchar(10) ,news varchar(10))";
		SQLiteDatabase db=mySqlite.getWritableDatabase();
		db.execSQL(sql);
		db.close();
    }
    
    /**
     * 描述：向数据库写报警信息
     */
    public void writeNewsToSql(String strNews){
    	mySqlite=new Mysqlite(MainActivity.this, "users.db", 1);
    	SQLiteDatabase db=mySqlite.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("time", Utils.getTime());
		values.put("news", strNews);
		db.insert("alerting_table", null, values);
		db.close();
    }
    /**
     * 描述：退出程序
     */
	@Override
	public void finish(){
		Utils.showExitDialog(MainActivity.this);
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
        		// 关闭XX报警
        		else if (msgstr.equals("SHUIJIN")) {
        			// 信息储存数据库
        			writeNewsToSql("水浸报警");
					setDialog("水浸报警","CLOSESHUIJIN");
					}
        		//-----------------------------注意的代码----------------------------------------
			}
        }      
    };
    
	   /**
  * 描述：报警提示对话框
  */
 public void setDialog(String coutent,final String delectOrder){
		final Builder builder = new AlertDialog.Builder(MainActivity.this);		
		builder.setTitle("报警提示");
		builder.setMessage(coutent+"正在报警……");
		builder.setPositiveButton("关闭报警器", new AlertDialog.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				//向pad端发送关闭报警设备
				UDPClientSocket.send(delectOrder, com.cvee.utils.Configuration.PAD_IP, com.cvee.utils.Configuration.PAD_PORT);
			}
		});
		alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.show();
	}

}
