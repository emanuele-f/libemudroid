package com.androidemu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;

import android.os.IBinder;

import android.util.Log;

public class EmulatorService extends Service
{
	public static final String ACTION_FOREGROUND = "com.androidemu.actions.FOREGROUND";
	public static final String ACTION_BACKGROUND = "com.androidemu.actions.BACKGROUND";

	private static final String LOG_TAG = "EmulatorService";

	private static final Class[] mStartForegroundSignature = new Class[] { int.class,
			Notification.class };
	private static final Class[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mStartForeground;
	private Method mStopForeground;
	private Method mSetForeground = null;
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	@Override
	public void onCreate()
	{
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		try
		{
			mStartForeground = getClass().getMethod("startForeground",
					mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground",
					mStopForegroundSignature);
		}
		catch (NoSuchMethodException e)
		{
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
			try
			{
				mSetForeground = getClass().getMethod("setForeground",
						mStopForegroundSignature);
			}
			catch (NoSuchMethodException crap)
			{
				Log.w(LOG_TAG,
						"OS doesn't have Service.startForeground OR Service.setForeground!",
						crap);
			}
		}
	}

	// This is the old onStart method that will be called on the pre-2.0
	// platform. On 2.0 or later we override onStartCommand() so this
	// method will not be called.
	@Override
	public void onStart(Intent intent, int startId)
	{
		handleCommand(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		handleCommand(intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY_COMPATIBILITY;
	}

	void handleCommand(Intent intent)
	{
		if (ACTION_FOREGROUND.equals(intent.getAction()))
		{
			// In this sample, we'll use the same text for the ticker and the
			// expanded notification
			CharSequence text = getText(R.string.emulator_service_running);

			// Set the icon, scrolling text and timestamp
			Notification notification = new Notification(R.drawable.app_icon, text,
					System.currentTimeMillis());

			// The PendingIntent to launch our activity if the user selects this
			// notification
			try
			{
				Class<Activity> clazz = (Class<Activity>) Class
						.forName(intent.getStringExtra("target"));

				PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
						new Intent(this, clazz), 0);

				// Set the info for the views that show in the notification
				// panel.
                // TODO: migrate to new API
				/*notification.setLatestEventInfo(this, getText(R.string.app_label), text,
						contentIntent);*/
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}

			startForegroundCompat(R.string.emulator_service_running, notification);

		}
		else if (ACTION_BACKGROUND.equals(intent.getAction()))
		{
			stopForegroundCompat(R.string.emulator_service_running);
		}
	}

	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification)
	{
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null)
		{
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			try
			{
				mStartForeground.invoke(this, mStartForegroundArgs);
			}
			catch (InvocationTargetException e)
			{
				// Should not happen.
				Log.w(LOG_TAG, "Unable to invoke startForeground", e);
			}
			catch (IllegalAccessException e)
			{
				// Should not happen.
				Log.w(LOG_TAG, "Unable to invoke startForeground", e);
			}
			return;
		}

		// Fall back on the old API.
		if (mSetForeground != null)
		{
			mStopForegroundArgs[0] = Boolean.TRUE;
			try
			{
				mSetForeground.invoke(this, mStopForegroundArgs);
			}
			catch (InvocationTargetException e)
			{
				// Should not happen.
				Log.w(LOG_TAG, "Unable to invoke setForeground", e);
			}
			catch (IllegalAccessException e)
			{
				// Should not happen.
				Log.w(LOG_TAG, "Unable to invoke setForeground", e);
			}
			return;
		}
		mNM.notify(id, notification);
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id)
	{
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null)
		{
			mStopForegroundArgs[0] = Boolean.TRUE;
			try
			{
				mStopForeground.invoke(this, mStopForegroundArgs);
			}
			catch (InvocationTargetException e)
			{
				// Should not happen.
				Log.w(LOG_TAG, "Unable to invoke stopForeground", e);
			}
			catch (IllegalAccessException e)
			{
				// Should not happen.
				Log.w(LOG_TAG, "Unable to invoke stopForeground", e);
			}
			return;
		}

		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(id);
		if (mSetForeground != null)
		{
			mStopForegroundArgs[0] = Boolean.FALSE;
			try
			{
				mSetForeground.invoke(this, mStopForegroundArgs);
			}
			catch (InvocationTargetException e)
			{
				// Should not happen.
				Log.w(LOG_TAG, "Unable to invoke setForeground", e);
			}
			catch (IllegalAccessException e)
			{
				// Should not happen.
				Log.w(LOG_TAG, "Unable to invoke setForeground", e);
			}
			return;
		}
	}

	@Override
	public void onDestroy()
	{
		// Make sure our notification is gone.
		stopForegroundCompat(R.string.emulator_service_running);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
}
