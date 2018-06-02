package com.androidemu.wrapper;

import java.io.File;

import android.R;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;

import android.os.Build;
import android.os.Environment;

import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

@SuppressWarnings("deprecation")
public class Wrapper
{
	public static final int SDK_INT = Integer.parseInt(Build.VERSION.SDK);

	public static boolean isBluetoothPresent()
	{
		if (SDK_INT >= 5) return Wrapper5.isBluetoothPresent();
		return false;
	}

	public static boolean isBluetoothEnabled()
	{
		if (SDK_INT >= 5) return Wrapper5.isBluetoothEnabled();
		return false;
	}

	public static boolean isBluetoothDiscoverable()
	{
		if (SDK_INT >= 5) return Wrapper5.isBluetoothDiscoverable();
		return false;
	}

	public static boolean supportsMultitouch(Context context)
	{
		if (SDK_INT >= 5) return Wrapper5.supportsMultitouch(context);
		return false;
	}

	public static final int MotionEvent_getPointerCount(MotionEvent event)
	{
		if (SDK_INT >= 5) return Wrapper5.MotionEvent_getPointerCount(event);
		return 1;
	}

	public static final int MotionEvent_getPointerId(MotionEvent event, int pointerIndex)
	{
		if (SDK_INT >= 5) return Wrapper5.MotionEvent_getPointerId(event, pointerIndex);
		return 0;
	}

	public static final int MotionEvent_findPointerIndex(MotionEvent event, int pointerId)
	{
		if (SDK_INT >= 5) return Wrapper5.MotionEvent_findPointerIndex(event, pointerId);
		if (pointerId == 0) return 0;
		return -1;
	}

	public static final float MotionEvent_getX(MotionEvent event, int pointerIndex)
	{
		if (SDK_INT >= 5) return Wrapper5.MotionEvent_getX(event, pointerIndex);
		return event.getX();
	}

	public static final float MotionEvent_getY(MotionEvent event, int pointerIndex)
	{
		if (SDK_INT >= 5) return Wrapper5.MotionEvent_getY(event, pointerIndex);
		return event.getY();
	}

	public static final float MotionEvent_getSize(MotionEvent event, int pointerIndex)
	{
		if (SDK_INT >= 5) return Wrapper5.MotionEvent_getSize(event, pointerIndex);
		return event.getSize();
	}
	
	public static final boolean KeyEvent_isLongPress(KeyEvent event)
	{
		if (SDK_INT >= 5) return Wrapper5.KeyEvent_isLongPress(event);
		return false;
	}

	public static final File getExternalScreenshotDirectory()
	{
		File picturesDir;

		if (SDK_INT >= 8)
		{
			picturesDir = Wrapper8.getExternalPicturesDirectory();
		}
		else
		{
			picturesDir = new File(Environment.getExternalStorageDirectory(), "pictures");
		}

		return new File(picturesDir, "screenshots");
	}

	public static final boolean isHwMenuBtnAvailable(Context ctx)
	{
		boolean result = true;

		// it is too dangerous to map menu key to smth on devices
		// without long-press detection support and proprietary tablets
		// usually do not have it
		if (SDK_INT <5 || SDK_INT >= 11 && SDK_INT <= 13)
		{
			result = false;
		}
		else if (SDK_INT >= 14)
		{
			result = Wrapper14.ViewConfiguration_hasPermanentMenuKey(ViewConfiguration.get(ctx));
		}

		return result;
	}

	public static final String keyCodeToString(Context ctx, int keyCode)
	{
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_A:
				return "A";
			case KeyEvent.KEYCODE_B:
				return "B";
			case KeyEvent.KEYCODE_C:
				return "C";
			case KeyEvent.KEYCODE_D:
				return "D";
			case KeyEvent.KEYCODE_E:
				return "E";
			case KeyEvent.KEYCODE_F:
				return "F";
			case KeyEvent.KEYCODE_G:
				return "G";
			case KeyEvent.KEYCODE_H:
				return "H";
			case KeyEvent.KEYCODE_I:
				return "I";
			case KeyEvent.KEYCODE_J:
				return "J";
			case KeyEvent.KEYCODE_K:
				return "K";
			case KeyEvent.KEYCODE_L:
				return "L";
			case KeyEvent.KEYCODE_M:
				return "M";
			case KeyEvent.KEYCODE_N:
				return "N";
			case KeyEvent.KEYCODE_O:
				return "O";
			case KeyEvent.KEYCODE_P:
				return "P";
			case KeyEvent.KEYCODE_Q:
				return "Q";
			case KeyEvent.KEYCODE_R:
				return "R";
			case KeyEvent.KEYCODE_S:
				return "S";
			case KeyEvent.KEYCODE_T:
				return "T";
			case KeyEvent.KEYCODE_U:
				return "U";
			case KeyEvent.KEYCODE_V:
				return "V";
			case KeyEvent.KEYCODE_W:
				return "W";
			case KeyEvent.KEYCODE_X:
				return "X";
			case KeyEvent.KEYCODE_Y:
				return "Y";
			case KeyEvent.KEYCODE_Z:
				return "Z";

			case KeyEvent.KEYCODE_0:
				return "0";
			case KeyEvent.KEYCODE_1:
				return "1";
			case KeyEvent.KEYCODE_2:
				return "2";
			case KeyEvent.KEYCODE_3:
				return "3";
			case KeyEvent.KEYCODE_4:
				return "4";
			case KeyEvent.KEYCODE_5:
				return "5";
			case KeyEvent.KEYCODE_6:
				return "6";
			case KeyEvent.KEYCODE_7:
				return "7";
			case KeyEvent.KEYCODE_8:
				return "8";
			case KeyEvent.KEYCODE_9:
				return "9";

			case KeyEvent.KEYCODE_ALT_LEFT:
				return "ALT (left)";
			case KeyEvent.KEYCODE_ALT_RIGHT:
				return "ALT (right)";
			case KeyEvent.KEYCODE_SHIFT_LEFT:
				return "SHIFT (left)";
			case KeyEvent.KEYCODE_SHIFT_RIGHT:
				return "SHIFT (right)";
			case KeyEvent.KEYCODE_SPACE:
				return "SPACE";
			case KeyEvent.KEYCODE_DEL:
				return "DEL";
			case KeyEvent.KEYCODE_ENTER:
				return "ENTER";
			case KeyEvent.KEYCODE_AT:
				return "@";
			case KeyEvent.KEYCODE_PERIOD:
				return ".";
			case KeyEvent.KEYCODE_COMMA:
				return ",";

			case KeyEvent.KEYCODE_DPAD_CENTER:
				return "DPAD Center";
			case KeyEvent.KEYCODE_DPAD_UP:
				return "DPAD Up";
			case KeyEvent.KEYCODE_DPAD_DOWN:
				return "DPAD Down";
			case KeyEvent.KEYCODE_DPAD_LEFT:
				return "DPAD Left";
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				return "DPAD Right";
			case KeyEvent.KEYCODE_BACK:
				return "BACK";
			case KeyEvent.KEYCODE_MENU:
				return "MENU";
			case KeyEvent.KEYCODE_HOME:
				return "HOME";
			case KeyEvent.KEYCODE_CALL:
				return "CALL";
			case KeyEvent.KEYCODE_CAMERA:
				return "CAMERA";
			case KeyEvent.KEYCODE_FOCUS:
				return "FOCUS";
			case KeyEvent.KEYCODE_SEARCH:
				return "SEARCH";
			case KeyEvent.KEYCODE_VOLUME_UP:
				return "Volume UP";
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				return "Volume DOWN";
				
			case KeyEvent.KEYCODE_BUTTON_L1:
				return "Left trigger";
			case KeyEvent.KEYCODE_BUTTON_L2:
				return "Right trigger";
			case KeyEvent.KEYCODE_BUTTON_START:
				return "Start";
			case KeyEvent.KEYCODE_BUTTON_SELECT:
				return "Select";

			case 0:
				return "-";
				
			default:
				if (SDK_INT >= 12)
				{
					return Wrapper12.keyCodeToString(keyCode)
							.replaceFirst("KEYCODE_", "").replace('_', ' ');
				}
				else
					return "??? (№" + String.valueOf(keyCode) + ')';
		}
	}
	
	public static void displayHomeAsUp(Activity a)
	{
		if (SDK_INT >= 11)
		{
			Wrapper11.displayHomeAsUp(a);
		}
	}
	
	public static void disableHomeButton(Activity a)
	{
		if (SDK_INT >= 14)
		{
			Wrapper14.disableHomeButton(a);
		}
	}
	
	public static final void Activity_invalidateOptionsMenu(Activity activity)
	{
		if (SDK_INT >= 11)
		{
			Wrapper11.Activity_invalidateOptionsMenu(activity);
		}
	}
	
	public static final void MenuItem_setShowAsAction(MenuItem item, boolean show)
	{
		if (SDK_INT >= 11)
		{
			Wrapper11.MenuItem_setShowAsAction(item, show);
		}
	}
	
	public static final void Activity_onBackPressed(Activity a)
	{
		Wrapper5.Activity_onBackPressed(a);
	}
}
