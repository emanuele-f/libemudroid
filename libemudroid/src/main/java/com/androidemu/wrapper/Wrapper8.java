package com.androidemu.wrapper;

import java.io.File;

import android.annotation.TargetApi;

import android.os.Build;
import android.os.Environment;

@TargetApi(Build.VERSION_CODES.FROYO)
class Wrapper8
{
	static File getExternalPicturesDirectory()
	{
		return Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	}
}
