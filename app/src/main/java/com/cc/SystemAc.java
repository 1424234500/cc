package com.cc;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import net.MSGSender;
import net.MSGTYPE;
import net.Msg;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import service.NetService;
import util.AndroidTools;
import util.JsonMsg;
import util.JsonUtil;
import util.MapListUtil;
import util.MySP;
import util.MySensor;
import util.Tools;


public class SystemAc extends BaseAc implements View.OnTouchListener {

	Button bgohead, bgoback, bturnleft, bturnright;
    SeekBar sbcarmera, sbspeed;
	@Override
	public void OnCreate(Bundle savedInstanceState) {
		Tools.out("SystemAc.oncreate");

		setContentView(R.layout.ac_system);
        sbcarmera = (SeekBar)findViewById(R.id.sbcarmera);
        sbcarmera.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                turnCaramera(i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbspeed = (SeekBar)findViewById(R.id.sbspeed);
        sbspeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                move("movefasterto" + "-" + i);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
        findViewById(R.id.viewsystem).setOnTouchListener(this);
        bgohead.setOnTouchListener(this);
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


        sensor = new MySensor();
        sensor.setSensor(this, Sensor.TYPE_ORIENTATION, SensorManager.SENSOR_DELAY_GAME, new MySensor.OnCallback() {
            @Override
            public void make(Object... objects) {
                onTurnOri(objects);
            }
        });

		MSGSender.systemLogin(getContext());	//认证系统 让系统能够收到消息
		MSGSender.systemAuth(getContext());		//权限控制


	}
	@Override
	public void callback(String jsonstr) { 
//		out("StartAc.callback."+jsonstr);
		Map map = JsonUtil.getMap(jsonstr);

		switch (Tools.parseInt(MapListUtil.getMap(map, "cmd"))) {
		case MSGTYPE.OK:


			break;
		
		}
		
		
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
        int to = i;//(int) (1.0 * i / 100 * 180);
        MSGSender.systemCtrl(getContext(), "turn", to+"");
    }
    public void onTurnOri(Object...objects){
        int x = (int) objects[0];
        int y = (int) objects[0];
        int z = (int) objects[0];

        //y 横向 的左偏 90 - 0 - -90



    }




}
