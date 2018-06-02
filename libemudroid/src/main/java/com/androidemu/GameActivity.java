package com.androidemu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;

import com.androidemu.persistent.UserPrefs;
import com.androidemu.wrapper.Wrapper;
import com.androidemu.wrapper.SystemUiHider;
import com.androidemu.wrapper.SystemUiHider.OnVisibilityChangeListener;

public class GameActivity extends Activity implements OnDismissListener
{
	private static final Logger logger;
	static
	{
		if (BuildConfig.DEBUG)
		{
			logger = Logger.getLogger("Emudroid");
			logger.setLevel(Level.INFO);
		} else
			logger = null;
	}
	
	protected static final int DIALOG_EXIT_PROMPT = 0;
	protected static final int DIALOG_FULLSCREEN_HINT = 100;

	private int fullScreenCfg;

	private Handler hideHandler;
	private Runnable hideRunnable;
	
	private UserPrefs prefs;
	
	// Some Android versions have fairly broken
	// handling of this
	private boolean menuShown;
	
	protected SystemUiHider uiHider;
	protected Resources res;
	
	protected boolean isGamePaused = false;
	protected boolean shunDangerousKeys = true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		debug("onCreate");

		super.onCreate(savedInstanceState);
		
		initResources();

		Wrapper.disableHomeButton(this);

		Intent serviceIntent = new Intent(this, EmulatorService.class).setAction(
				EmulatorService.ACTION_FOREGROUND).putExtra("target",
				getClass().getName());
		startService(serviceIntent);
		
		if (Wrapper.SDK_INT < 11)
		{
			debug("Pre-Honeycomb - hiding title separately");

			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		}
		else
			debug("API >= 11 => title = ActionBar");

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}
	
	@Override
	protected void onStart()
	{
		debug("onStart");

		super.onStart();
		
		prefs = new UserPrefs(getApplicationContext());
	}
	
	protected boolean isMenuAccessible()
	{
		return true;
	}
	
	@Override
	protected void onResume()
	{
		debug("onResume");
		
		super.onResume();
		
		if (!isMenuAccessible())
		{
			showDialog(DIALOG_FULLSCREEN_HINT);
		}
		
		/*
		 * On Honeycomb and newer:
		 * 1) No fullscreen -> fullScreenCfg = 0
		 * 2) Fullscreen with hidden controls -> fullScreenCfg has HIDE_NAVIGATION
		 * 3) Fullscreen with dimmed controls -> fullScreenCfg != 0
		 * Otherwise:
		 * 1) No fullscreen -> fullScreenCfg = 0
		 * 2) Fullscreen -> fullScreenCfg has FULLSCREEN (HIDE_NAVIGATION contains FULLSCREEN)
		 */
		if (prefs.fullScreen)
		{
			fullScreenCfg = prefs.hideNav ? SystemUiHider.FLAG_HIDE_NAVIGATION
					: Wrapper.SDK_INT < 11 ? SystemUiHider.FLAG_FULLSCREEN | SystemUiHider.FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES
							: SystemUiHider.FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES;
		}
		else
		{
			fullScreenCfg = 0;
		}

		if (fullScreenCfg != 0)
		{
			debug("Initializing UI hiding");

			hideHandler = new Handler();
			hideRunnable = new Runnable()
			{
				@Override
				public void run()
				{
					uiHider.hide();
				}
			};
			uiHider = SystemUiHider.getInstance(this, findViewById(android.R.id.content), fullScreenCfg);
			uiHider.setup();

			uiHider.setOnVisibilityChangeListener(new OnVisibilityChangeListener()
			{
				@Override
				public void onVisibilityChange(boolean visible)
				{
					if (visible)
					{
						hideUiDelayed();
					}
				}
			});
		}

		// show stuff after setting change
		if (uiHider != null && !uiHider.isVisible())
		{
			uiHider.show();
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		debug("onKeyDown, keyCode=" + keyCode);
		
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_CAMERA:
				if (shunDangerousKeys)
				{
					debug("Not wanted, shunning");
					
					return true;
				}
			case KeyEvent.KEYCODE_BACK:
				if (Wrapper.SDK_INT < 5 && event.getRepeatCount() == 0 && shunDangerousKeys)
				{
					debug("API < 5 => legacy BACK handling");
					
					Wrapper.Activity_onBackPressed(this);
					return true;
				}
				else if (Wrapper.SDK_INT >= 11 && prefs.fullScreen)
				{
					debug("API >= 11 & fullscreen is enabled, skipping other BACK handling");
					return true;
				}
			default:
				return super.onKeyDown(keyCode, event);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		debug("onCreateOptionsMenu");

		getMenuInflater().inflate(R.menu.base, menu);
		menu.findItem(R.id.menu_help).setIntent(
				new Intent(this, HelpActivity.class).setData(
						Uri.parse("file:///android_asset/faq.html")).putExtra("", "FAQ"));

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		debug("onPrepareOptionsMenu");
		
		if (Wrapper.SDK_INT < 11)
		{
			pauseGame();
		}
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu)
	{
		debug("onMenuOpened");
		
		// there is no way menu can be hidden at this stage
		menuShown = true;
		
		if (Wrapper.SDK_INT < 11)
		{
			pauseGame();
			
			if (fullScreenCfg != 0)
			{
				uiHider.show();
				hideHandler.removeCallbacks(hideRunnable);
			}
		}
		
		return super.onMenuOpened(featureId, menu);
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu)
	{
		debug("onOptionsMenuClosed");
		
		if (Wrapper.SDK_INT < 11 && fullScreenCfg != 0)
		{
			uiHider.hide();
		}
		
		resumeGame();

		super.onOptionsMenuClosed(menu);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id)
	{
		debug("onCreateDialog");
		
		switch (id)
		{
			case DIALOG_FULLSCREEN_HINT:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				final View dialogView = getLayoutInflater().inflate(R.layout.dialog_hint,
						null);

				return builder.setView(dialogView).setCancelable(false)
						.setPositiveButton(android.R.string.ok, new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								if (((CheckBox) dialogView.findViewById(R.id.shown))
										.isChecked())
								{
									prefs.setHintShown();
								}
							}
						}).create();
		}

		return super.onCreateDialog(id);
	}
	
	@Override
	@Deprecated
	protected void onPrepareDialog(int id, Dialog dialog)
	{
		debug("onPrepareDialog");
		
		switch (id)
		{
			case DIALOG_EXIT_PROMPT:
				dialog.setCancelable(!isGamePaused);
			default:
				dialog.setOnDismissListener(this);
				pauseGame();
				break;
		}
			
		super.onPrepareDialog(id, dialog);
	}
	
	@Override
	public void onDismiss(DialogInterface dialog)
	{
		resumeGame();
	}
	
	@Override
	public boolean onSearchRequested()
	{
		return false;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		debug("onWindowFocusChanged: hasFocus=" + hasFocus);
		
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus)
		{
			if (isGamePaused && !menuShown)
			{
				showDialog(DIALOG_EXIT_PROMPT);
			}
			else
			{
				resumeGame();
			}
			
			if (fullScreenCfg != 0)
			{
				hideUiDelayed();
			}
			else if (uiHider != null)
			{
				uiHider.show();
			}
		}
		else
		{
			pauseGame();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		resumeGame();
	}

	@Override
	protected void onPause()
	{
		debug("onPause");

		// at this stage nobody cares if it is shown or not -
		// when activity resumes game will be paused anyway
		menuShown = false;
		
		if (uiHider != null)
		{
			uiHider.setOnVisibilityChangeListener(null);
		}

		if (hideHandler != null)
		{
			hideHandler.removeCallbacks(hideRunnable);
		}
		
		pauseGame();

		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		debug("onDestroy");

		stopService(new Intent(this, EmulatorService.class));

		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		debug("onConfigurationChanged");
		
		super.onConfigurationChanged(newConfig);
		
		initResources();
	}
	
	protected void initResources()
	{
		res = getResources();
		prefs = new UserPrefs(getApplicationContext());
	}

	protected void hideUiDelayed()
	{
		hideHandler.removeCallbacks(hideRunnable);

		if (fullScreenCfg != 0)
		{
			debug("Sheduling UI hiding");

			hideHandler.postDelayed(hideRunnable, 3000);
		}
	}
	
	protected void pauseGame()
	{
		debug("Pause requested");
		
		isGamePaused = true;
		
		Wrapper.Activity_invalidateOptionsMenu(this);
	}
	
	protected void resumeGame()
	{
		debug("Resume requested");
		
		// there is no way menu can be shown at this stage
		isGamePaused = menuShown = false;
		
		Wrapper.Activity_invalidateOptionsMenu(this);
	}

	protected boolean extractAsset(File file)
	{
		if (!file.exists())
		{
			debug(file.getPath() + " not found - extracting");

			InputStream in = null;
			OutputStream out = null;

			try
			{
				in = getAssets().open(file.getName());
				out = new FileOutputStream(file);

				byte[] buf = new byte[8192];
				int len;
				while ((len = in.read(buf)) > 0)
					out.write(buf, 0, len);

			}
			catch (Exception e)
			{
				log(e, "Failed to extract " + file.getPath());

				return false;
			}
			finally
			{
				try
				{
					if (out != null) out.close();
					if (in != null) in.close();
				}
				catch (IOException e)
				{
					log(e, "Failed to close stream after extraction");
				}
			}
		}
		else
			debug(file.getPath() + " already exist - skipped");

		return true;
	}

	protected static void debug(String logMessage)
	{
		if (BuildConfig.DEBUG)
		{
			logger.info(logMessage);
		}
	}

	protected static void log(Exception e, String logMessage)
	{
		logger.severe(logMessage);
		e.printStackTrace();
	}
}
