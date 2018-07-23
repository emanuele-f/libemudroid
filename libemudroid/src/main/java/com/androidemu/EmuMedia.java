package com.androidemu;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.SurfaceHolder;

public class EmuMedia
{

	private static SurfaceHolder holder;
	private static Rect region = new Rect();
	private static AudioTrack track;
	private static Emulator.OnFrameDrawnListener onFrameDrawnListener;
	private static float volume = AudioTrack.getMaxVolume();
    private static boolean must_resume = false;
    private static long play_millis = 0;
    private static final long MIN_MILLIS_BEFORE_PLAY = 300;

    static void destroy()
	{
		if (track != null)
		{
			track.stop();
			track = null;
		}
	}

	public static void setOnFrameDrawnListener(Emulator.OnFrameDrawnListener l)
	{
		onFrameDrawnListener = l;
	}

	static void setSurface(SurfaceHolder h)
	{
		holder = h;
	}

	static void setSurfaceRegion(int x, int y, int w, int h)
	{
		region.set(x, y, x + w, y + h);
	}

	static void bitBlt(int[] image, boolean flip)
	{
		// Fill background
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.BLACK);
		if (flip) canvas.rotate(180, canvas.getWidth() / 2, canvas.getHeight() / 2);

		Bitmap bitmap = Bitmap.createBitmap(image, 0, region.width(), region.width(), region.height(),
				Bitmap.Config.RGB_565);
		canvas.drawBitmap(bitmap, region.left, region.top, null);
		if (onFrameDrawnListener != null) onFrameDrawnListener.onFrameDrawn(canvas);

		holder.unlockCanvasAndPost(canvas);
	}

	static boolean audioCreate(int rate, int bits, int channels)
	{
		int format = (bits == 16 ? AudioFormat.ENCODING_PCM_16BIT
				: AudioFormat.ENCODING_PCM_8BIT);
		int channelConfig = (channels == 2 ? AudioFormat.CHANNEL_CONFIGURATION_STEREO
				: AudioFormat.CHANNEL_CONFIGURATION_MONO);

		// avoid recreation if no parameters change
		if (track != null && track.getSampleRate() == rate
				&& track.getAudioFormat() == format
				&& track.getChannelCount() == channels) return true;

		int bufferSize = AudioTrack.getMinBufferSize(rate, channelConfig, format) * 2;
		if (bufferSize < 1500) bufferSize = 1500;

		try
		{
			track = new AudioTrack(AudioManager.STREAM_MUSIC, rate, channelConfig,
					format, bufferSize, AudioTrack.MODE_STREAM);

			if (track.getState() == AudioTrack.STATE_UNINITIALIZED) track = null;

		}
		catch (IllegalArgumentException e)
		{
			track = null;
		}
		if (track == null) return false;

		track.setStereoVolume(volume, volume);
		return true;
	}

	static void audioSetVolume(int vol)
	{
		final float min = AudioTrack.getMinVolume();
		final float max = AudioTrack.getMaxVolume();
		volume = min + (max - min) * vol / 100;

		if (track != null)
            track.setStereoVolume(volume, volume);
	}

	static void audioDestroy()
	{
		if (track != null)
		{
			track.stop();
			track = null;
            must_resume = false;
		}
	}

	static void audioStart() {
	    if (track != null) {
            play_millis = System.currentTimeMillis();
            must_resume = true;
        }
	}

	static void audioStop()
	{
		if (track != null)
		{
			track.stop();
			track.flush();
            must_resume = false;
		}
	}

	static void audioPause()
	{
		if (track != null) {
            track.pause();
            must_resume = false;
        }
	}

	static void audioPlay(byte[] data, int size)
	{
		if (track != null)
            track.write(data, 0, size);

        /* Delay actual resume to prevent crackling sounds */
        if (must_resume && ((System.currentTimeMillis() - play_millis) >= MIN_MILLIS_BEFORE_PLAY)) {
            track.play();
            must_resume = false;
        }
	}
}
