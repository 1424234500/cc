package util;

import java.util.HashMap;

import com.cc.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class MySoundPool extends SoundPool {

	private HashMap<Integer, Integer> map;
	Context context; 
	
	public MySoundPool(Context context) {
		super(4, AudioManager.STREAM_MUSIC, 0);
		this.context = context;

		map = new HashMap<Integer, Integer>();
		map.put(1, this.load(context, R.raw.brickhit, 1));
		map.put(2, this.load(context, R.raw.levelstarting, 1));
		map.put(3, this.load(context, R.raw.ice, 1));
		map.put(4, this.load(context, R.raw.gameover, 1));
	}
	
	public void playSounds(int sound, int number){
		 
        AudioManager am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = audioCurrentVolumn/audioMaxVolumn;
        
        this.play(map.get(sound), volumnRatio, volumnRatio, 1, number, 1);
    }
	public void playSounds(int sound){
		playSounds(sound, 0);
	}
	
	

}
