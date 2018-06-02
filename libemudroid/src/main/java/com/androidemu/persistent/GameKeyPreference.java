package com.androidemu.persistent;

import com.androidemu.R;
import com.androidemu.R.string;
import com.androidemu.wrapper.Wrapper;

import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

public class GameKeyPreference extends DialogPreference implements DialogInterface.OnKeyListener
{
	private Resources resources;
	private int oldValue;
	private int newValue;

	public GameKeyPreference(Context context)
	{
		this(context, null);
	}

	public GameKeyPreference(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		resources = context.getResources();
		setPositiveButtonText(R.string.key_clear);
		setDefaultValue(0);
	}
	
	public final int getKeyValue()
	{
		return newValue;
	}

	public final void setKey(int key)
	{
		oldValue = newValue = key;
		updateSummary();
	}
	
	@Override
	protected void onPrepareDialogBuilder(Builder builder)
	{
		super.onPrepareDialogBuilder(builder);

		builder.setMessage(R.string.press_key_prompt).setOnKeyListener(this);
	}
	
	@Override
	protected void showDialog(Bundle state)
	{
		super.showDialog(state);

		final Dialog dialog = getDialog();
		if (dialog != null)
		{
			dialog.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		// clear key binding
		if (which == DialogInterface.BUTTON_POSITIVE) newValue = 0;

		super.onClick(dialog, which);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult)
	{
		super.onDialogClosed(positiveResult);

		if (!positiveResult)
			newValue = oldValue;
		else
		{
			oldValue = newValue;
			persistInt(newValue);
			updateSummary();
		}
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index)
	{
		return a.getInteger(index, 0);
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
	{
		oldValue = (restoreValue ? getPersistedInt(0) : ((Integer) defaultValue)
				.intValue());
		newValue = oldValue;
		updateSummary();
	}
	
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
	{
		if (!isKeyConfigurable(keyCode)) return false;

		newValue = keyCode;
		super.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
		dialog.dismiss();
		return true;
	}

	private void updateSummary()
	{
		setSummary(Wrapper.keyCodeToString(getContext(), newValue));
	}

	private boolean isKeyConfigurable(int keyCode)
	{
		switch (keyCode)
		{
			case KeyEvent.KEYCODE_MENU:
				return Wrapper.isHwMenuBtnAvailable(getContext());
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				return false;
		}
		return true;
	}
}
