package util;



import android.app.Activity;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.widget.VideoView;

public class MyVideo {
	VideoView videoView;   //rtmp播放器
	Activity activity;
	String path;

	public interface Callback{
	    public void onRes(Boolean res);
    }

	public MyVideo(Activity activity, int rid, final Callback callback){
		this.activity = activity;
		this.videoView = (VideoView) activity.findViewById(rid);

		new Thread(){
		    public void run(){
                callback.onRes(initLib());
            }
        }.start();

	}
	public boolean initLib(){
	    //阻塞加载库
		if (!LibsChecker.checkVitamioLibs(activity))
		    return false;
		return true;
	}
	public void setPath(String path){
	    this.path = path;
        videoView.setVideoPath(path);
    }

	public void initRtmpVideo(Activity activity,  int rid, String path){
//        String path = "rtmp://rrbalancer.broadcast.tneg.de:1935/pw/ruk/ruk";
            /*options = new HashMap<>();
            options.put("rtmp_playpath", "");
            options.put("rtmp_swfurl", "");
            options.put("rtmp_live", "1");
            options.put("rtmp_pageurl", "");*/
		//mVideoView.setVideoURI(Uri.parse(path), options);
//        mVideoView.setMediaController(new MediaController(this));
//        mVideoView.requestFocus();
//        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mediaPlayer.setPlaybackSpeed(1.0f);
//            }
//        });
	}
					  
 
}