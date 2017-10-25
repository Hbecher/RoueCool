package fr.polytech.ricm5.mm.rouecool.res.sounds;

import static android.content.Context.AUDIO_SERVICE;

import android.media.AudioManager;

class Sound
{
	private final SoundManager manager;
	private final int sampleId;

	Sound(SoundManager manager, int sampleId)
	{
		this.manager = manager;
		this.sampleId = sampleId;
	}

	void playSound()
	{
		AudioManager audioManager = (AudioManager) manager.getContext().getSystemService(AUDIO_SERVICE);
		float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;

		manager.getSoundPool().play(sampleId, volume, volume, 1, 0, 1.0F);
	}
}
