package fr.polytech.ricm5.mm.rouecool.res.sounds;

import static android.content.Context.AUDIO_SERVICE;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

class Sound
{
	private final SoundPool pool;
	private int id;
	private Context context;

	Sound(SoundPool pool, int id, Context context)
	{
		this.pool = pool;
		this.id = id;
		this.context = context;
	}

	void playSound()
	{
		AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
		float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;

		pool.play(id, volume, volume, 1, 0, 1.0F);
	}
}
