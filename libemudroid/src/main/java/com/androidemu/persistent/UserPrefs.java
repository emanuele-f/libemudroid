package com.androidemu.persistent;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.androidemu.R;

public class UserPrefs
{
	protected SharedPreferences prefsData;
	protected Resources res;
	
	public final boolean fullScreen;
	public final boolean hideNav;
	public final boolean hintShown_fullScreen;
	
	public UserPrefs(Context context)
	{
		prefsData = PreferenceManager.getDefaultSharedPreferences(context);
		
		res = context.getResources();
		
		fullScreen = prefsData.getBoolean("fullScreen", res.getBoolean(R.bool.def_fullScreen));
		hideNav = prefsData.getBoolean("hideNav", res.getBoolean(R.bool.def_hideNav));

		hintShown_fullScreen = prefsData.getBoolean("fullscreen_hint_shown", false);
	}

	public void setHintShown()
	{
		prefsData.edit().putBoolean("fullscreen_hint_shown", true).commit();
	}
}
