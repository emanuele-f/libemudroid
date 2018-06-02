package com.androidemu.persistent;

import com.androidemu.R;
import com.androidemu.R.xml;
import com.androidemu.wrapper.Wrapper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.MenuItem;

@SuppressWarnings("deprecation")
public class CommonPreferences extends PreferenceActivity implements Preference.OnPreferenceClickListener
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.common);
		
		findPreference("reset").setOnPreferenceClickListener(this);
		
		Wrapper.displayHomeAsUp(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				if (Wrapper.SDK_INT < 16)
				{
					// Game activity is system-global, so
					// FLAG_ACTIVITY_CLEAR_TOP is not needed
					PackageManager pm = getPackageManager();
				    Intent intent = pm.getLaunchIntentForPackage(getPackageName()).cloneFilter();
				    startActivity(intent);
					return true;
				}
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference)
	{
		String key = preference.getKey();
        
        if( key.equals("reset") )
        {
        	actionResetUserPrefs();
        	return true;
        }
        else
        {
        	return false;
        }
	}
	
	protected void actionResetUserPrefs()
    {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().clear().commit();
        
        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.common, true);
    }
}