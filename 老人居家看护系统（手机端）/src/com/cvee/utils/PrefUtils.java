package com.cvee.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference封装
 * 
 * @author Kevin
 * 
 */
public class PrefUtils {

	public static final String PREF_NAME = "config";

	private static SharedPreferences mPrefs;

	public static void putBoolean(Context ctx, String key, boolean value) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}

		mPrefs.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(Context ctx, String key, boolean defValue) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		return mPrefs.getBoolean(key, defValue);
	}

	public static void putString(Context ctx, String key, String value) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		mPrefs.edit().putString(key, value).commit();
	}

	public static String getString(Context ctx, String key, String defValue) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		return mPrefs.getString(key, defValue);
	}
	
	public static void putInt(Context ctx, String key, int value) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		mPrefs.edit().putInt(key, value).commit();
	}

	public static int getInt(Context ctx, String key, int defValue) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}
		return mPrefs.getInt(key, defValue);
	}

	public static void remove(Context ctx, String key) {
		if (mPrefs == null) {
			mPrefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
		}

		mPrefs.edit().remove(key).commit();
	}
}
