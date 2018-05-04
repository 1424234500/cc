package util.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cc.Constant;
import com.cc.R;

import java.util.ArrayList;
import java.util.List;

import interfac.CallInt;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import util.AndroidTools;
import util.MyVideo;
import util.Tools;


public class VideoRtmp extends RelativeLayout {


	Context context;
    TextView tvinfo, tvtitle;
    VideoView videoView;
    MyVideo video;
    public VideoRtmp(Context context) {
        this(context, null);
    }

    public VideoRtmp(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoRtmp(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        out("----------Constructor-------------");

        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View parentView = inflater.inflate(R.layout.video_rtmp, this, true);
        tvinfo = (TextView)parentView.findViewById(R.id.tvinfo);
        tvtitle = (TextView)parentView.findViewById(R.id.tvtitle);
        videoView = (VideoView)parentView.findViewById(R.id.vv);

    }








    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }





    public void setOnplayListener(Activity activity, final MyVideo.Callback callback){
        video = new MyVideo(videoView, new MyVideo.Callback() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                setInfoR("已缓冲：" + percent + "%");
            }

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        setInfo("开始缓冲,暂停播放");
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        setInfo("缓冲结束,开始播放");
                        break;
                    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                        setInfo("" + extra + "kb/s");
                        break;
                }
                return false;
            }

            @Override
            public boolean onRes(final MyVideo video, int status) {
                if(!callback.onRes(video, status)){
                    switch (status){
                        case MyVideo.INIT_START:
                            setInfo("初始化rtmp...");
                            break;
                        case MyVideo.INIT_TRUE:
                            setInfo("rtmp初始化成功");
                            break;
                        case MyVideo.INIT_FALSE:
                            setInfo("rtmp初始化失败");
                            break;
                    }
                }
                return false;
            }
        });
        video.init(activity);
    }
    public void play(String url){
        setTitle(url);
        video.setPath(url);
    }


    public void out(String str){
        AndroidTools.out(this.getClass().getName() + str);
    }
    public void setInfo(String str){
        tvinfo.setText( str );
    }
    public void setTitle(String str){
        tvtitle.setText(str);
    }

    public String getPath() {
        return video.getPath();
    }
}
