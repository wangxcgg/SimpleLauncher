package com.wangxc.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import com.wangxc.business.MyApplication;
import java.util.Timer;
import java.util.TimerTask;

//长期运行后台用startService,需要和正在运行的Service数据交换，用bindService
public class RebootService extends Service {
    private static final String TAG="RebootService";
    private long RESTART_DELAY = 330 * 1000; // 多少时间后重启
    private MyBinder mBinder;
    // 此对象用于绑定的service与调用者之间的通信
    public class MyBinder extends Binder {
        public RebootService getService() {
            return RebootService.this;
        }
        public void setRebootTime(long time){
            RESTART_DELAY=time;
        }
        public void startRebootTask(final Context context) {
            Toast.makeText(context, "start", Toast.LENGTH_SHORT).show();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    PowerManager pManager = (PowerManager) MyApplication.getContext()
                            .getSystemService(Context.POWER_SERVICE); // 重启
                    pManager.reboot("");
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, RESTART_DELAY);
        }
    }

    //在Service第一次创建立即回调该方法
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"---onCreate()---");
    }

    //Service被关闭之前回回调该函数
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"---onDestroy()---");
    }

    //必须实现的方法，返回一个IBinder对象，应用程序可以通过该对象与Service组件通信
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"---onBind---");
        // Create MyBinder object
        if (mBinder == null) {
            mBinder = new MyBinder();
        }
        return mBinder;
    }

    //Service被断开连接时回调该方法
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG,"---onUnbind---");
        return true;
    }





}