package util.ac.demo;

import android.app.Activity;
import android.os.Bundle;

import com.cc.R;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;


public class RtmpAc extends Activity {
	private VideoView mVideoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		setContentView(R.layout.ac_rtmp);

		mVideoView = (VideoView) findViewById(R.id.vitamio_videoView);
        String path = "rtmp://192.168.191.1:1935/myapp/test1";

		mVideoView.setVideoPath(path);
	}
}