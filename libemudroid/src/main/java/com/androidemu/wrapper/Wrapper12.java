package com.androidemu.wrapper;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.KeyEvent;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
class Wrapper12
{
	static String keyCodeToString(int keyCode)
	{
		return KeyEvent.keyCodeToString(keyCode);
	}
}
