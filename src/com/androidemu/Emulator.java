package com.androidemu;

import android.content.Context;

import android.graphics.Canvas;

import android.view.SurfaceHolder;

import java.io.IOException;

import java.nio.Buffer;

import com.androidemu.wrapper.Wrapper;

public class Emulator
{
	public interface OnFrameDrawnListener
	{
		void onFrameDrawn(Canvas canvas);
	}

	private static String engineLib;
	private static Emulator emulator;
	private Thread thread;
	private String romFileName;
	private boolean cheatsEnabled;
	private Cheats cheats;

	public static Emulator createInstance(Context context, String engine)
	{
		if (emulator == null) System.loadLibrary("emu");

		final String libDir = "/data/data/" + context.getPackageName() + "/lib";
		if (!engine.equals(engineLib))
		{
			engineLib = engine;
			loadEngine(libDir, engine);
		}

		if (emulator == null) emulator = new Emulator(libDir);
		return emulator;
	}

	public static Emulator getInstance()
	{
		return emulator;
	}

	private Emulator(String libDir)
	{
		initialize(libDir, Wrapper.SDK_INT);

		thread = new Thread()
		{
			public void run()
			{
				nativeRun();
			}
		};
		thread.start();
	}

	public final void enableCheats(boolean enable)
	{
		cheatsEnabled = enable;
		if (romFileName == null) return;

		if (enable && cheats == null)
			cheats = new Cheats(romFileName);
		else if (!enable && cheats != null)
		{
			cheats.destroy();
			cheats = null;
		}
	}

	public final Cheats getCheats()
	{
		return cheats;
	}

	public final boolean loadROM(String file)
	{
		if (!nativeLoadROM(file)) return false;

		romFileName = file;
		if (cheatsEnabled) cheats = new Cheats(file);
		return true;
	}

	public final void unloadROM()
	{
		nativeUnloadROM();

		cheats = null;
		romFileName = null;
	}

	public native void setFrameUpdateListener(FrameUpdateListener l);

	public native void setSurface(SurfaceHolder surface);

	public native void setSurfaceRegion(int x, int y, int w, int h);

	public native void setKeyStates(int states);

	public native void processTrackball(int key1, int duration1, int key2, int duration2);

	public native void fireLightGun(int x, int y);

	public native void setOption(String name, String value);

	public native int getOption(String name);

	public native int getVideoWidth();

	public native int getVideoHeight();

	private static native boolean loadEngine(String libDir, String lib);

	private native boolean initialize(String libDir, int sdk);

	private native void nativeRun();

	private native boolean nativeLoadROM(String file);

	private native void nativeUnloadROM();

	public native void reset();

	public native void power();

	public native void pause();

	public native void resume();

	public native void getScreenshot(Buffer buffer);

	public native boolean saveState(String file);

	public native boolean loadState(String file);

	public void setOption(String name, boolean value)
	{
		setOption(name, value ? "true" : "false");
	}

	public void setOption(String name, int value)
	{
		setOption(name, Integer.toString(value));
	}

	public interface FrameUpdateListener
	{
		int onFrameUpdate(int keys) throws IOException, InterruptedException;
	}
}
