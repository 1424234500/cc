package util;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cc.Constant;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import interfac.CallString;
import okhttp3.Call;
import okhttp3.Response;
import util.picasso.NetImage;

/**
 * 传感器工具
 */
public class MySensor {
    public interface OnCallback{
        public void make(Object...objects);
    }
    public OnCallback callback;
    public SensorManager sm;


    public List<String> show(Activity ac){
        // 实例化传感器管理者
        SensorManager mSensorManager = (SensorManager)ac.getSystemService(Context.SENSOR_SERVICE);
        // 得到设置支持的所有传感器的List
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        List<String> sensorNameList = new ArrayList<String>();
        out("传感器列表");
        List<String> res = new ArrayList<>();
        for (Sensor sensor : sensorList) {
            out(" :: "+sensor.getName());
            res.add(sensor.getName());
        }
        return res;
    }
    /**
     * 反注册传感器 并设置回调
     */
    public  void unsetSensor() {
        sm.unregisterListener(sel);

    }
    /**
     * 注册传感器 并设置回调
     */
    public  void setSensor(Activity ac, int type, OnCallback callback) {
        setSensor(ac, type, SensorManager.SENSOR_DELAY_NORMAL, callback);
    }
    /**
     * 注册传感器 并设置回调
     */
    public  void setSensor(Activity ac, int type, int hz, OnCallback callback) {
        this.callback = callback;
        //创建一个SensorManager来获取系统的传感器服务
        sm = (SensorManager) ac.getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(sel, sm.getDefaultSensor(type), hz);
//        sm.registerListener(sel, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        /*
         * 最常用的一个方法 注册事件
         * 参数1 ：SensorEventListener监听器
         * 参数2 ：Sensor 一个服务可能有多个Sensor实现，此处调用getDefaultSensor获取默认的Sensor
         * 参数3 ：模式 可选数据变化的刷新频率，多少微秒取一次。
         * */
//        //加速度传感器
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
//        // 为磁场传感器注册监听器
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
//        // 为方向传感器注册监听器
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
//        // 为陀螺仪传感器注册监听器
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
//        // 为重力传感器注册监听器
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
//        // 为线性加速度传感器注册监听器
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
//        // 为温度传感器注册监听器
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_TEMPERATURE), SensorManager.SENSOR_DELAY_NORMAL);
//        // 为光传感器注册监听器
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_LIGHT), SensorManager.SENSOR_DELAY_NORMAL);
//        // 为距离传感器注册监听器
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
//        // 为压力传感器注册监听器
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
//        // 计步统计
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER), SensorManager.SENSOR_DELAY_NORMAL);
//        // 单次计步
//        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR), SensorManager.SENSOR_DELAY_NORMAL);
    }
     SensorEventListener sel = new SensorEventListener(){
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float X_lateral = sensorEvent.values[0];
                float Y_longitudinal = sensorEvent.values[1];
                float Z_vertical = sensorEvent.values[2];
                out("xyz轴的加强度", X_lateral, Y_longitudinal, Z_vertical );
                callback.make(X_lateral, Y_longitudinal, Z_vertical);
            }
            else if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                float X_lateral = sensorEvent.values[0];
                float Y_longitudinal = sensorEvent.values[1];
                float Z_vertical = sensorEvent.values[2];
                out("xyz轴的磁场强度", X_lateral, Y_longitudinal, Z_vertical );
            }
            else if(sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION){
                float X_lateral = sensorEvent.values[0];
                float Y_longitudinal = sensorEvent.values[1];
                float Z_vertical = sensorEvent.values[2];
                out("绕xyz轴转过的角度", X_lateral, Y_longitudinal, Z_vertical );
                callback.make(X_lateral, Y_longitudinal, Z_vertical);
            }
            else if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE){
                //需要将弧度转为角度
                float X = (float)Math.toDegrees(sensorEvent.values[0]);
                float Y = (float)Math.toDegrees(sensorEvent.values[1]);
                float Z = (float)Math.toDegrees(sensorEvent.values[2]);
                out("绕xyz轴转过的角速度", X, Y, Z);
                callback.make(X, Y, Z);
            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY){
                float X = sensorEvent.values[0];
                float Y = sensorEvent.values[1];
                float Z = sensorEvent.values[2];
                out("xyz方向的重力加速度", X, Y, Z );
                callback.make(X, Y, Z);
            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
                float X = sensorEvent.values[0];
                float Y = sensorEvent.values[1];
                float Z = sensorEvent.values[2];
                out("x方向的线性加速度", X, Y, Z);
                callback.make(X, Y, Z);
            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_TEMPERATURE){
                float X = sensorEvent.values[0];
                out("温度为"+ X );
                callback.make(X);
            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT){
                float X = sensorEvent.values[0];
                out("光强度为为"+ X );
                callback.make(X);
            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY){
                float X = sensorEvent.values[0];
                out("距离为"+ X );
                callback.make(X);
            }else if(sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE){
                float X = sensorEvent.values[0];
                out("压强为"+ X );
                callback.make(X);
            }
            else if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
                float X = sensorEvent.values[0];
                out("COUNTER："+ X );
                callback.make(X);
            } else if(sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
                float X = sensorEvent.values[0];
                out("DECTOR："+ X );
                callback.make(X);
            }
        }

        //复写onAccuracyChanged方法
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            out("onAccuracyChanged");
        }
    };
    public  void out(Object...objects){
        AndroidTools.out(Tools.objects2string(objects));

    }








}
