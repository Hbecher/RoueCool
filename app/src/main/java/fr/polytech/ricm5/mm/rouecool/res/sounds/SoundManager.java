package fr.polytech.ricm5.mm.rouecool.res.sounds;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseArray;
import android.util.SparseIntArray;
import fr.polytech.ricm5.mm.rouecool.R;

public class SoundManager
{
	private final SparseIntArray idConverter = new SparseIntArray();
	private final SparseArray<Sound> sounds = new SparseArray<>();

	public SoundManager(final Context context)
	{
		SoundPool pool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		pool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
		{
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
			{
				if(status == 0)
				{
					sounds.put(sampleId, new Sound(soundPool, sampleId, context));
				}
			}
		});

		int[] soundIds = {R.raw.pap, R.raw.cla, R.raw.pop};

		for(int id : soundIds)
		{
			loadSound(pool, context, id);
		}
	}

	private void loadSound(SoundPool pool, Context context, int resId)
	{
		int sampleId = pool.load(context, resId, 1);

		idConverter.put(resId, sampleId);
	}

	public void playSound(int id)
	{
		if(idConverter.indexOfKey(id) >= 0)
		{
			Sound sound = sounds.get(idConverter.get(id));

			if(sound != null)
			{
				sound.playSound();
			}
		}
	}
}
