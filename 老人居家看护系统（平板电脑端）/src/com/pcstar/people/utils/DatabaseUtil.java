package com.pcstar.people.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseUtil{

	private static final String TAG = "DatabaseUtil";

	/**
	 * Database Name
	 */
	private static final String DATABASE_NAME = "camera_database";

	/**
	 * Database Version
	 */
	private static final int DATABASE_VERSION = 1;

	/**
	 * Table Name
	 */
	private static final String DATABASE_TABLE = "cameralist";

	/**
	 * Table columns
	 */
	public static final String KEY_ID = "id" ;
	public static final String KEY_NAME = "name";
	public static final String KEY_ADDR = "addr";
	public static final String KEY_PORT = "port";
	public static final String KEY_USER = "user" ;
	public static final String KEY_PWD = "pwd" ;

	/**
	 * Database creation sql statement
	 */
	private static final String CREATE_STUDENT_TABLE =
		"create table " + DATABASE_TABLE 
		+ " (" + KEY_ID + " integer primary key autoincrement, "
		+ KEY_NAME +" text not null, " 
		+ KEY_ADDR + " text not null,"
		+ KEY_PORT + " text not null,"
		+ KEY_USER + " text not null,"
		+ KEY_PWD + " text);";

	/**
	 * Context
	 */
	private final Context mCtx;

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Inner private class. Database Helper class for creating and updating database.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		/**
		 * onCreate method is called for the 1st time when database doesn't exists.
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Creating DataBase: " + CREATE_STUDENT_TABLE);
			db.execSQL(CREATE_STUDENT_TABLE);
		}
		/**
		 * onUpgrade method is called when database version changes.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 *
	 * @param ctx the Context within which to work
	 */
	public DatabaseUtil(Context ctx) {
		this.mCtx = ctx;
	}
	/**
	 * This method is used for creating/opening connection
	 * @return instance of DatabaseUtil
	 * @throws SQLException
	 */
	public DatabaseUtil open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	/**
	 * This method is used for closing the connection.
	 */
	public void close() {
		mDbHelper.close();
	}


	/**
	 * This method is used to create/insert new record record.
	 * @param name
	 * @param addr
	 * @param port
	 * @param user
	 * @param pwd
	 * @return
	 */
	public long createCamera(String name, String addr, String port, String user, String pwd) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, name);
		initialValues.put(KEY_ADDR, addr);
		initialValues.put(KEY_PORT, port);
		initialValues.put(KEY_USER, user);
		initialValues.put(KEY_PWD, pwd);
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	/**
	 * This method will delete record.
	 * @param rowId
	 * @return boolean
	 */
	public boolean deleteCamera(long rowId) {
		return mDb.delete(DATABASE_TABLE, KEY_ID + "=" + rowId, null) > 0;
	}
	
	public boolean deleteCamera(String ipaddr, String port) {
		return mDb.delete(DATABASE_TABLE, KEY_ADDR + "='" + ipaddr + "' and " + KEY_PORT + "='" + port + "'", null) > 0;
	}

	/**
	 * This method will return Cursor holding all the records.
	 * @return Cursor
	 */
	public Cursor fetchAllCameras() {
		return mDb.query(DATABASE_TABLE, new String[] {KEY_ID, KEY_NAME,
				KEY_ADDR, KEY_PORT, KEY_USER, KEY_PWD}, null, null, null, null, null);
	}

	/**
	 * This method will return Cursor holding the specific record.
	 * @param id
	 * @return Cursor
	 * @throws SQLException
	 */
	public Cursor fetchCamera(long id) throws SQLException {
		Cursor mCursor =
			mDb.query(true, DATABASE_TABLE, new String[] {KEY_ID, 
					KEY_NAME, KEY_ADDR, KEY_PORT, KEY_USER, KEY_PWD}, KEY_ID + "=" + id, null,
					null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 * This method will update record.
	 * @param id
	 * @param name
	 * @param addr
	 * @param port
	 * @param user
	 * @param pwd
	 * @return
	 */
	public boolean updateCamera(int id, String name, String addr, String port, String user, String pwd) {
		ContentValues args = new ContentValues();	
		args.put(KEY_NAME, name);
		args.put(KEY_ADDR, addr);
		args.put(KEY_PORT, port);
		args.put(KEY_USER, user);
		args.put(KEY_PWD, pwd);
		return mDb.update(DATABASE_TABLE, args, KEY_ID + "=" + id, null) > 0;
	}
	
	/**
	 * 
	 * @param oldaddr
	 * @param oldport
	 * @param name
	 * @param addr
	 * @param port
	 * @param user
	 * @param pwd
	 * @return
	 */
	public boolean updateCamera(String oldaddr, String oldport, String name, String addr, String port, String user, String pwd) {
		ContentValues args = new ContentValues();
		args.put(KEY_NAME, name);
		args.put(KEY_ADDR, addr);
		args.put(KEY_PORT, port);
		args.put(KEY_USER, user);
		args.put(KEY_PWD, pwd);
		return mDb.update(DATABASE_TABLE, args, KEY_ADDR + "='" + oldaddr + "' and " + KEY_PORT + "='" + oldport + "'", null) > 0;
	}
}