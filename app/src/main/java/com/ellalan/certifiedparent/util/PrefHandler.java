package com.ellalan.certifiedparent.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

@SuppressLint("NewApi")
public class PrefHandler {
	SharedPreferences pref;
	public static int USER_NOT_LOGGED_IN=0,USER_FACEBOOK=1,USER_GMAIL=2;
	Editor editor;

	Context _context;

	int PRIVATE_MODE = 0;

	private static final String PREF_NAME = "user_profile_pref";
	public static final String PREF_USER_LOGGED_IN= "user_logged_in";
	public static final String PREF_PERSONAL_NAME= "pref_personal_name";
	public static final String PREF_PERSONAL_PHOTO_URL = "pref_personal_photo_url";
	public static final String PREF_EMAIL= "pref_email";


	@SuppressLint("CommitPrefEdits")
	public PrefHandler(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setUserLoggedIn(int flag) {
		editor.putInt(PREF_USER_LOGGED_IN, flag);
		editor.commit();
	}

	public int getUserLoggedIn() {
		return pref.getInt(PREF_USER_LOGGED_IN, USER_NOT_LOGGED_IN);
	}

	public void setPersonalName(String flag) {
		editor.putString(PREF_PERSONAL_NAME, flag);
		editor.commit();
	}
	public String getPersonalName() {
		return pref.getString(PREF_PERSONAL_NAME, "");
	}

	public void setPhotoUrl(String flag) {
		editor.putString(PREF_PERSONAL_PHOTO_URL, flag);
		editor.commit();
	}
	public String getPhotoUrl() {
		return pref.getString(PREF_PERSONAL_PHOTO_URL, "");
	}

	public void setEmail(String flag) {
		editor.putString(PREF_EMAIL, flag);
		editor.commit();
	}
	public String getEmail() {
		return pref.getString(PREF_EMAIL, "");
	}


	public void clearData()
	{
		editor.clear();
		editor.commit();
	}
	
	
}
