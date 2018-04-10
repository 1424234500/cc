package util.tools;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MyMediaPlayer { 
	    private static MyMediaPlayer mMyMediaPlayer;
	    private MediaPlayer mMediaPlayer;

	    public void setPlayOnCompleteListener(MediaPlayer.OnCompletionListener playOnCompleteListener){
	        if(mMediaPlayer != null){
	            mMediaPlayer.setOnCompletionListener(playOnCompleteListener);
	        }
	    }


	    public static MyMediaPlayer getInstance(){
	        if(mMyMediaPlayer == null){
	            mMyMediaPlayer = new MyMediaPlayer();
	        }
	        return  mMyMediaPlayer;
	    }


	    private MyMediaPlayer(){
	        mMediaPlayer = new MediaPlayer();
	    }

	    public int getDutation(String soundFilePath){
	        try {
	            mMediaPlayer.reset();
	            mMediaPlayer.setDataSource(soundFilePath);
	            mMediaPlayer.prepare();
            	int res = -1;
            	res = mMediaPlayer.getDuration();
            	stop();
	            return res;
	        }catch (Exception e){
	            e.printStackTrace();
	        }
            return -1;
	    }

	    public void play(final String soundFilePath, final OnPlay onPlay){
	        if(mMediaPlayer == null){
	            return;
	        }
	        try {
	        	Tools.log("播放文件:" + soundFilePath + " 开始 ");
	        	
	            mMediaPlayer.reset();
	            mMediaPlayer.setDataSource(soundFilePath);
	            mMediaPlayer.prepare();
	            
	            
	            mMediaPlayer.start();
	            
	            //部分手机无效？
	            mMediaPlayer.setOnCompletionListener(new OnCompletionListener(){
	                 @Override
	                 public void onCompletion(MediaPlayer mp) {
	                	 Tools.log("播放文件:" + soundFilePath + " 结束");
	                    if(onPlay != null){
	                    	Tools.log("回调");
	                    	onPlay.onPlayEnd(mMediaPlayer);
	                    }
	                    stop();
	                 }
	            });
	            
	        }catch (Exception e){
	            e.printStackTrace();
	        }
	    }


	    public void pause(){
	        if(mMediaPlayer != null){
	            mMediaPlayer.pause();
	        }
	    }


	    public void stop(){
	        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
	            mMediaPlayer.stop();
	        }
	    }


	    public int getCurrentPosition(){
	        if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
	            return mMediaPlayer.getCurrentPosition();
	        }else{
	            return 0;
	        }
	    }
	
	    public int getDutation(){
	        if(mMediaPlayer!= null && mMediaPlayer.isPlaying()){
	            return mMediaPlayer.getDuration();
	        }else{
	            return 0;
	        }
	    }


	    public boolean isPlaying(){
	        if(mMediaPlayer != null){
	            return mMediaPlayer.isPlaying();
	        }else{
	            return false;
	        }
	    } 
	 
		
	    
	    public interface OnPlay{
			void onPlayEnd(MediaPlayer mediaPlayer);
		}
	
	  
					  
 
}