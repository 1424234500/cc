package util;


import android.app.Activity;
import android.net.Uri;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class MyVideo {
    VideoView videoView;   //rtmp播放器
    String path;

    public final static int INIT_START = 0;
    public final static int INIT_TRUE = 1;
    public final static int INIT_FALSE = 2;

    Callback callback;
    public interface Callback {
        public boolean onRes(MyVideo video, int status);
        public void onBufferingUpdate(MediaPlayer mp, int percent);
        public boolean onInfo(MediaPlayer mp, int what, int extra);

    }
    public MyVideo(VideoView videoView, final Callback callback) {
        this.videoView = videoView;
        this.callback = callback;
    }

    public void init(Activity activity) {
        callback.onRes(MyVideo.this, INIT_START);
        if (LibsChecker.checkVitamioLibs(activity)) {
            callback.onRes(MyVideo.this, INIT_TRUE);
        } else {
            callback.onRes(MyVideo.this, INIT_FALSE);
            return;
        }
        videoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
        MediaController controller = new MediaController(activity);
        videoView.setMediaController(controller);
        videoView.setBufferSize(1024); //设置视频缓冲大小。默认1024KB，单位byte
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
                //mediaPlayer.setLooping(true);
            }
        });

        videoView.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                AndroidTools.out("已缓冲：" + percent + "%");
                callback.onBufferingUpdate(mp, percent);
            }
        });
        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if(! callback.onInfo(mp, what, extra)) {    //若子级未处理 则由此处处理
                    switch (what) {
                        case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                            AndroidTools.out("开始缓冲,暂停播放");
                            mp.pause();
                            break;
                        case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                            AndroidTools.out("缓冲结束,开始播放");
                            mp.start(); //缓冲结束再播放
                            break;
                        case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                            AndroidTools.out("缓存中,当前网速:" + extra + "kb/s");
                            break;
                    }
                }
                return true;
            }
        });
    }

    public String getPath(){
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
        videoView.setVideoURI(Uri.parse(path));
//        videoView.setVideoPath(path);
    }


}