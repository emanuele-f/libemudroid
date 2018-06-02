package com.androidemu;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.androidemu.wrapper.Wrapper;
import com.androidemu.wrapper.TabbedHelpViewFactory;

public class HelpActivity extends Activity
{
	TabbedHelpViewFactory tac;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Wrapper.displayHomeAsUp(this);

		setContentView(TabbedHelpViewFactory.newTabbedHelpView(
				this,
				new String[][]
				{
					new String[]
					{
						"file:///android_asset/faq.html",
						"FAQ"
					},
					new String[]
					{
						"file:///android_asset/gpl.html",
						"License"
					}
				}));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				if (Wrapper.SDK_INT < 16)
				{
					// Game activity is system-global, so FLAG_ACTIVITY_CLEAR_TOP is not needed
					PackageManager pm = getPackageManager();
					Intent intent = pm.getLaunchIntentForPackage(getPackageName())
							.cloneFilter();
					startActivity(intent);
					return true;
				}
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
