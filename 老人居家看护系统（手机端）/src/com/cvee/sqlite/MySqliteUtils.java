package com.cvee.sqlite;
/**
 * 数据库的操作类
 * @author Administrator
 *
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MySqliteUtils {
	public static Mysqlite mysqite;
	public static Context context;	
	/**
	 * 描述：建表的方法
	 */
    public static void setTable(){
    	mysqite=new Mysqlite(context, "users.db", 1);
    	String sql ="create table if not exists alerting_table(time varchar(10) ," +
				"news varchar(10),";
		SQLiteDatabase db=mysqite.getWritableDatabase();
		db.execSQL(sql);
		db.close();
    }
    /**
     * 描述：查询数据库中是否存在此表
     */
	 public static boolean isTableExist(String tableName){
		 mysqite=new Mysqlite(context, "users.db", 1);
		SQLiteDatabase db=mysqite.getWritableDatabase();
         boolean result = false; 
         
         if(tableName == null){  
                 return false;  
         }  
          
         try {  
             Cursor cursor = null;  
                 String sql = "select count(1) as c from sqlite_master where type =" +
                 		"'table' and name ='alerting_table'";  
                 cursor = db.rawQuery(sql, null);  
                 if(cursor.moveToNext()){  
                         int count = cursor.getInt(0);  
                         if(count>0){  
                                 result = true;  
                         }  
                 }  
                   
                   
                 cursor.close();  
         } catch (Exception e) {  
                   
         }
         finally{
        	 db.close();
         }
         return result;  
	 }  
}
