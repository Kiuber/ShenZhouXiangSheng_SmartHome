package com.pcstar.people.sqlite;
/**
 * 描述:创建数据库
 * 作用：存储报警等信息数据
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Mysqlite extends SQLiteOpenHelper {

	public Mysqlite(Context context, String name,
			int version) {
		super(context, name, null, 1);
		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	

}
