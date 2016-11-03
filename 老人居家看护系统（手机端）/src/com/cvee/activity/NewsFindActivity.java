package com.cvee.activity;
/**
 * 描述：信息查询类
 * 作用：主要查询和删除报警信息
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cvee.R;
import com.cvee.sqlite.Mysqlite;
public class NewsFindActivity extends Activity {
	private com.cvee.utils.DragListView listView1;
	private Button deleteButton;//删除
	private List<String> timeList = new ArrayList<String>();//报警时间
	private List<String> newsList = new ArrayList<String>();//报警信息
	private List<String> checkedTimeList = new ArrayList<String>();//选中列表的时间
	private Map<Integer, Boolean> checkBoxVisibleMap;//记录多选框的选中状态
	private MyBaseAdapter myBaseAdapter;
	private CheckBox allCheckBox;
	private Mysqlite mysqlite;
	private String zhuangtai = "";
	private boolean noMore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_newsfind);			
		initView();
		listView1.onRefreshLoadingMoreListener = new com.cvee.utils.DragListView.OnRefreshLoadingMoreListener()
		{

			@Override
			public void onRefresh()
			{
				zhuangtai = "shuaxin";
				
				getTimeAndNews();
				checkBoxVisibleMap.clear();
				for (int i = 0; i < timeList.size(); i++) {
					
					checkBoxVisibleMap.put(i, false);
				}
				noMore = true;
				listView1.onLoadMoreComplated(noMore);
				myBaseAdapter.notifyDataSetChanged();
			}

			@Override
			public void onLoadMore() {
				
			}
		};
	}
	/**
	 * 描述：初始化组件
	 */
	public void initView(){
		getTimeAndNews();
		listView1 = (com.cvee.utils.DragListView)findViewById(R.id.newsfind_listView);
		allCheckBox = (CheckBox)findViewById(R.id.newsfind_checkBox);
		deleteButton = (Button)findViewById(R.id.newsfind_deleteButton);
//		refreshButton = (Button)findViewById(R.id.newsfind_refreshButton);
		
		allCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					for (int i = 0; i < timeList.size(); i++) {
						checkBoxVisibleMap.put(i, true);
					}
				}else{
					for (int i = 0; i < timeList.size(); i++) {
						checkBoxVisibleMap.put(i, false);
					}
				}
				myBaseAdapter.notifyDataSetChanged();
			}
		});
		deleteButton.setOnClickListener(new MyOnClickListener());
//		refreshButton.setOnClickListener(new MyOnClickListener());
		myBaseAdapter = new MyBaseAdapter();
		noMore = true;
		listView1.onLoadMoreComplated(noMore);
		listView1.setAdapter(myBaseAdapter);
	}
	/**
	 * 描述：自定义适配器
	 */
	public class MyBaseAdapter extends BaseAdapter{
		@SuppressLint("UseSparseArrays")
		public MyBaseAdapter(){
			checkBoxVisibleMap = new HashMap<Integer, Boolean>();
			initCheckBoxList();
		}
		public void initCheckBoxList(){
			for (int i = 0; i < timeList.size(); i++) {
				checkBoxVisibleMap.put(i, false);
			}
		}
		public int getCount() {
			return timeList.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			 View view = convertView;
			 final ReferenceHolder holder;
	          try{
	 	        	  view = getLayoutInflater().inflate(R.layout.newsfind_listitem, null);
	                  holder = new ReferenceHolder();
	                 
	                  holder.alertingClassText=(TextView)view.findViewById(R.id.listitem_classText);
	                  holder.timeText=(TextView)view.findViewById(R.id.listitem_timeText);
	                  holder.checkbox = (CheckBox)view.findViewById(R.id.listitem_checkBox);
	                 
	                  view.setTag(holder);
		 	         holder.alertingClassText.setText(newsList.get(position));
		 	         holder.timeText.setText(timeList.get(position));
		 	         holder.checkbox.setChecked(checkBoxVisibleMap.get(position));
		 	         
	 	        final int index = position;
				holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked )
						{
							checkBoxVisibleMap.put(index, true);
						}
						else
						{
							checkBoxVisibleMap.put(index, false);
						}
					}
				});  
	          }catch (Exception e) {
	        	  e.printStackTrace();
			}
			return view;
		}
	}
	/**
	 * 描述：声明适配器MyBaseAdapter中的组件
	 */
	 class ReferenceHolder {
			public TextView alertingClassText;
			public TextView timeText;
			public CheckBox checkbox;
	 }
	 /**
	  *描述：按钮接听类
	  */
	 class MyOnClickListener implements OnClickListener
	 {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.newsfind_deleteButton:
				checkedTimeList = getTimes();
				if (checkedTimeList.isEmpty()) {
					Toast.makeText(NewsFindActivity.this, "至少选择一项", Toast.LENGTH_SHORT).show();
				}else{
					deleteNewsToSql();
					getTimeAndNews();
					checkBoxVisibleMap.clear();
					for (int i = 0; i < timeList.size(); i++) {
						
						checkBoxVisibleMap.put(i, false);
					}
					myBaseAdapter.notifyDataSetChanged();
					allCheckBox.setChecked(false);
				}
				break;
//			case R.id.newsfind_refreshButton:
//				getTimeAndNews();
//				checkBoxVisibleMap.clear();
//				for (int i = 0; i < timeList.size(); i++) {
//					
//					checkBoxVisibleMap.put(i, false);
//				}
//				myBaseAdapter.notifyDataSetChanged();
//				break;
			default:
				break;
			}
			
		}
	 }
	 /**
	  * 描述：得到选中列表的报警时间
	  */
	private ArrayList<String> getTimes(){
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < timeList.size(); i++) {
			if(checkBoxVisibleMap.get(i) == true){
				list.add(0,timeList.get(i));
			}
		}
		return list;
	}
	/**
	 * 描述：从数据库中的到报警时间和信息
	 */
	private void getTimeAndNews(){
		timeList.clear();
		newsList.clear();
		mysqlite = new Mysqlite(NewsFindActivity.this, "users.db", 1);

		SQLiteDatabase db=mysqlite.getWritableDatabase();
		Cursor cursor=db.query("alerting_table", new String[]{"time","news"}, null, null,null,null,null);
		while(cursor.moveToNext()){
			String time=cursor.getString(cursor.getColumnIndex("time"));
			String news=cursor.getString(cursor.getColumnIndex("news"));
			timeList.add(time);
			newsList.add(news);
		}	
		cursor.close();
		db.close();
		if (timeList.size() == 0&&newsList.size() == 0) {
			Toast.makeText(NewsFindActivity.this, "暂时没有报警信息", Toast.LENGTH_SHORT).show();
		} 
		if (zhuangtai.equals("shuaxin")) {
			listView1.onRefreshComplete();
		}
	}
	/**
	 * 描述：删除数据库信息
	 */
	private void deleteNewsToSql(){
		SQLiteDatabase db=mysqlite.getWritableDatabase();
		for (int i = 0; i < checkedTimeList.size(); i++) {
			db.delete("alerting_table", "time=?", new String[]{checkedTimeList.get(i)});
		}
		
		db.close();
	}
}
