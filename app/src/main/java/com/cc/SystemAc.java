package com.cc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import net.MSGSender;
import net.MSGTYPE;

import java.util.Map;

import service.NetService;
import util.AndroidTools;
import util.JsonUtil;
import util.MapListUtil;
import util.MyImage;
import util.MySensor;
import util.MyVideo;
import util.Tools;
import util.view.CanvasView;
import util.view.VideoRtmp;


public class SystemAc extends BaseAc implements View.OnTouchListener {
    Button bgohead, bgoback, bturnleft, bturnright;
//    VideoRtmp video;
    ImageView ivphoto;
    SeekBar sbcarmera;
    SeekBar sbspeed;
    ImageView ivdetail;
    Button bphoto;
    Button btake;


    @Override
	public void OnCreate(Bundle savedInstanceState) {
		Tools.out("SystemAc.oncreate");

		setContentView(R.layout.ac_system);



        ivdetail = (ImageView) findViewById(R.id.ivdetail);
        bphoto = (Button)findViewById(R.id.bphoto);
        btake = (Button)findViewById(R.id.btake);
        btake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSGSender.systemCtrl(getContext(), "file", MapListUtil.map().put("id", "_temp_now.png").build());
            }
        });
        bphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSGSender.systemCtrl(getContext(), "file", MapListUtil.map().put("id", photoId).build());
            }
        });
        ivphoto = (ImageView)findViewById(R.id.ivphoto);
//		ivphoto.setOnCanvas(new CanvasView.OnCanvas() {
//            @Override
//            public void onDraw(CanvasView view, Canvas canvas, Paint paint, Object... objects) {
//                draw(view, canvas, paint, objects);
//            }
//        });

        sbcarmera = (SeekBar)findViewById(R.id.sbcarmera);
        sbcarmera.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                turnCaramera(seekBar.getProgress());
            }
        });
        sbspeed = (SeekBar)findViewById(R.id.sbspeed);
        sbspeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                move("movefasterto" + "-" + seekBar.getProgress());
            }
        });
		bgohead = (Button)findViewById(R.id.bgohead);
		bgoback = (Button)findViewById(R.id.bgoback);
		bturnleft = (Button)findViewById(R.id.bturnleft);
		bturnright = (Button)findViewById(R.id.bturnright);

        btns = new Button[]{ bturnleft, bturnright};
        btnsOn = new int[btns.length];
        btnsOnOld = new int[btns.length];
		for(int i = 0; i < btns.length; i++){
            btnsOn[i] = 0;
            btnsOnOld[i] = 0;
        }
        final View viewsystem = findViewById(R.id.viewsystem);
		viewsystem.setOnTouchListener(this);
        bgohead.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:   //0
                        move("head");
                        break;
                    case MotionEvent.ACTION_UP:     //2
                        move("space");
                        break;
                }
                return false;
            }
       });
        bgoback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:   //0
                        move("back");
                        break;
                    case MotionEvent.ACTION_UP:     //2
                        move("space");
                        break;
                }
                return false;
            }
        });
		bturnleft.setOnTouchListener(this);
		bturnright.setOnTouchListener(this);

//        final String path = "rtmp://192.168.191.1:1935/myapp/test1";
//        final String path = "rtmp://39.107.26.100:1935:1935/myapp/test1";
//
//
//        video = (VideoRtmp)findViewById(R.id.video);
//        video.setOnClickListener(null);
//        video.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                final EditText inputServer = new EditText(getContext());
//                inputServer.setText(video.getPath());
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("rtmp地址").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)  .setNegativeButton("Cancel", null);
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        String text = inputServer.getText().toString();
//                        video.play(text);
//                    }
//                });
//                builder.show();
//                return true;
//            }
//        });
//        video.setOnplayListener(this, new MyVideo.Callback() {
//            @Override
//            public boolean onRes(MyVideo video, int status) {
//                return false;
//            }
//
//            @Override
//            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//            }
//
//            @Override
//            public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                return false;
//            }
//        });
//        video.play(path);

        sensor = new MySensor();
        sensor.setSensor(this, Sensor.TYPE_ORIENTATION, SensorManager.SENSOR_DELAY_NORMAL, new MySensor.OnCallback() {
            @Override
            public void make(Object... objects) {
                onTurnOri(objects);
            }
        });


	}
	@Override
	public void callback(String jsonstr) {
        Map map = JsonUtil.getMap(jsonstr);
        int cmd = MapListUtil.getMap(map, "cmd", 0);
        String value = MapListUtil.getMap(map, "value0", "false");
        String str;
        int w, h;
		switch (cmd) {
            case MSGTYPE.OK:
                break;
            case MSGTYPE.LOGIN_BY_ID_PWD:
                str = MapListUtil.getMap(map, "res").equals("0")?"认证失败":"认证成功";
                toast(str);
                break;
            case MSGTYPE.SYS_DECT_ON:
			case MSGTYPE.SYS_PHOTO:
				onRecvPhoto(map);
				break;
            case MSGTYPE.SYS_PHOTO_DETAIL:
                onRecvPhotoDetail(map);
                break;
		}
		
		
	}
	String photoId = "";
	public void onRecvPhoto(Map map){
		String photo = MapListUtil.getMap(map, "res");
		String time = MapListUtil.getMap(map, "time");
		String id = MapListUtil.getMap(map, "id");
		this.photoId = id;
		toast("检测到人出没",id);
		Bitmap arr = MyImage.toBitmap(photo);
        ivphoto.setImageBitmap(arr);

    }
    public void onRecvPhotoDetail(Map map){
        String photo = MapListUtil.getMap(map, "res");
        String id = MapListUtil.getMap(map, "id");
        Bitmap arr = MyImage.toBitmap(photo);
        ivdetail.setImageBitmap(arr);
    }
    public void draw(CanvasView view, Canvas canvas, Paint paint, Object...objects){
	    Bitmap bitmap = (Bitmap)objects[0];
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
	    int sw = view.getWidth();
	    int sh = view.getHeight();
	    //800,600 -> 600,300
        //              8   3     6   6         2:3
        float b1 = 1f * w / h;      //120：50 2.4  1200 500
        float b2 = 1f * sw / sh;    //2 : 1  = 2  1000 500
        int tw = 0;
        int th = 0;
        int sx = 0;
        int sy = 0;
        if(b1 < b2){
            th = sh;
            tw = (int)(1f * w * th / h);
            sx = (sw - tw) / 2;
        }else{
            tw = sw;
            th = (int)(1f * h * tw / w);
            sy = (sh - th) / 2;
        }

        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, new Rect(0, 0, w, h), new Rect(sx, sy, tw, th),  paint);

    }

	@Override
	public boolean OnBackPressed() {
		return false;
	}













    MySensor sensor;
    Button btns[];
    int    btnsOn[];
    int    btnsOnOld[];
    int    btnsLastOn = -1;
    @Override
	public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
		float x = event.getRawX();
		float y = event.getRawY();
		int i = 0;
//        AndroidTools.log("" + id + ", " + x +" " + y);

        for(i = 0; i < btns.length; i++) {
            if(AndroidTools.isOnClick(btns[i], x, y)){
                id = btns[i].getId();
                break;
            }
        }

        btnsOnOld = btnsOn.clone();

        if(i >= btns.length){
            //注意滑动事件却没有命中的情况 是从其他按钮区域滑出
            if(event.getAction() == MotionEvent.ACTION_MOVE && btnsLastOn < btns.length){ //上一点命中 且这一点移出 未命中
                //获取上次命中的点 取消状态
                btnsOn[btnsLastOn] = 0;
                ifMove(btnsLastOn);
            }
        }else {
            switch (event.getAction()) {
//按下或者移动进入 则 开启
                case MotionEvent.ACTION_DOWN:   //0
//				AndroidTools.log("开启" + id);
                    btnsOn[i] = 1;
                    break;
//抬起或者移动离开 则关闭
                case MotionEvent.ACTION_UP:     //2
//				AndroidTools.log("关闭" + id);
                    btnsOn[i] = 0;
                    break;
                case MotionEvent.ACTION_MOVE:    //0
                    if(btnsLastOn != i && btnsLastOn < btns.length){
                        btnsOn[btnsLastOn] = 0;
                        ifMove(btnsLastOn);
                    }
                    if (btnsOnOld[i] == 0) { //本来是0 滑入 那肯定之前是 其他可点区域移动过来 经历过移出
                        btnsOn[i] = 1;
                    } else {//按钮中滑动

                    }
//                AndroidTools.log("移动" + id);
                    break;
            }
            ifMove(i);
        }

        btnsLastOn = i;
        return false;
	}

	public void ifMove(int i){
        if(i > btns.length || i < 0) return;
        int id = btns[i].getId();
        //比对第i号位变换 btns[] btnsOn[] btnsOnOld[] 若i有变化则处理
        if (btnsOn[i] == btnsOnOld[i]) {

        } else if (btnsOn[i] == 1 && btnsOnOld[i] == 0) {  //从0-》1 开启
            out("按钮" + i + " 开启");
            if (id == R.id.bgohead) {
                move("head");
            } else if (id == R.id.bgoback) {
                move("back");
            } else if (id == R.id.bturnleft) {
                move("left");
            } else if (id == R.id.bturnright) {
                move("right");
            }
        } else if (btnsOn[i] == 0 && btnsOnOld[i] == 1) {  //从1-》0 关闭
            out("按钮" + i + " 关闭");

            if (id == R.id.bgohead) {
                move("space");
            } else if (id == R.id.bgoback) {
                move("space");
            } else if (id == R.id.bturnleft) {
                move("turnrevert");
            } else if (id == R.id.bturnright) {
                move("turnrevert");
            }
        }
    }

	public void move(String go){
		MSGSender.systemCtrl(getContext(), "move", go);
	}

    public void turnCaramera(int i){
//        0-100 -> 0-180
        int to = 180 - i;//(int) (1.0 * i / 100 * 180);
        MSGSender.systemCtrl(getContext(), "cameraTurn", to+"");
    }

    float[] arr = new float[3];
    int now = 0;
    int last = 2;
    public void onTurnOri(Object...objects){
        float x = (float) objects[0];
        float y = (float) objects[1];
        float z = (float) objects[2];
        //x 水平 旋转  0 - 360
        //y 横瓶 左偏 90 - 0 - -90
        //z 竖屏 左偏 90 - 0 - -90
        y = z;

        y = y > 90 ? 90 : y;
        y = y < -90 ? -90 : y;
        y += 90;

        arr[now] = y;
        if(Math.abs(arr[now] - arr[last]) > 8){
            arr[last] = y;
//            out("横屏反转", y);
        }

    }




}
