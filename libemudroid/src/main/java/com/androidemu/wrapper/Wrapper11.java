package com.androidemu.wrapper;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;

import android.os.Build;

import android.view.MenuItem;
import android.view.View;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
class Wrapper11
{
	static void View_setSystemUiVisibility(View view, int visibility)
	{
		view.setSystemUiVisibility(visibility);
	}

	public static void displayHomeAsUp(Activity a)
	{
		a.getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	public static void Activity_invalidateOptionsMenu(Activity activity)
	{
		activity.invalidateOptionsMenu();
	}

	public static void MenuItem_setShowAsAction(MenuItem item, boolean show)
	{
		item.setShowAsAction(show ? MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT : MenuItem.SHOW_AS_ACTION_NEVER);
	}
}
