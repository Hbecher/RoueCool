package fr.polytech.ricm5.mm.rouecool.res.sounds;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.RawRes;
import android.util.SparseArray;
import android.util.SparseIntArray;

public class SoundManager
{
	private final SparseIntArray resToSample = new SparseIntArray();
	private final SparseArray<Sound> sounds = new SparseArray<>();
	private final BlockingQueue<Integer> requests = new LinkedBlockingQueue<>();
	private final Semaphore wait = new Semaphore(0);
	private final Context context;
	private final SoundPool pool;

	public SoundManager(Context context)
	{
		this.context = context;
		pool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
		pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
		{
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
			{
				if(status == 0)
				{
					sounds.put(sampleId, new Sound(SoundManager.this, sampleId));
				}

				wait.release();
			}
		});
	}

	public void start()
	{
		Player p = new Player();
		Thread soundThread = new Thread(p, "Sound manager thread");
		soundThread.setDaemon(true);
		soundThread.start();
	}

	public void stop()
	{
		requests.offer(-1);
	}

	public void playSound(@RawRes int resId)
	{
		requests.offer(resId);
	}

	Context getContext()
	{
		return context;
	}

	SoundPool getSoundPool()
	{
		return pool;
	}

	private void loadSound(int resId) throws InterruptedException
	{
		int sampleId = pool.load(context, resId, 1);

		wait.acquire();

		resToSample.put(resId, sampleId);
	}

	private boolean isLoaded(int resId)
	{
		return resToSample.indexOfKey(resId) >= 0;
	}

	private class Player implements Runnable
	{
		@Override
		public void run()
		{
			try
			{
				while(true)
				{
					Integer resId = requests.take();

					if(resId == null || resId < 0)
					{
						break;
					}

					if(!isLoaded(resId))
					{
						loadSound(resId);
					}

					Sound sound = sounds.get(resToSample.get(resId));

					if(sound != null)
					{
						sound.playSound();
					}
				}
			}
			catch(InterruptedException ignored)
			{
			}
		}
	}
}
