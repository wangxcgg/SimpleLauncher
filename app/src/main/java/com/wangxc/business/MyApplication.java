package com.wangxc.business;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.DisplayMetrics;

import com.wangxc.service.RebootService;
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static Context mContext = null;
    private static final String REBOOT_SERVICE_ACTION = "com.moons.multiLauncherGZ.rebootservice";
    public static float convert=1.0f;

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;


    //ServiceConnection对象，用于监听访问者与Service之间的连接情况
    private ServiceConnection conn = new ServiceConnection() {
        //访问者与Service之间连接成功时回调该方法,方法中的IBinder对象实现与被绑定Service之间的通信
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RebootService.MyBinder mBinder = (RebootService.MyBinder) service;
            mBinder.startRebootTask(MyApplication.this);
        }

        //Service所在的宿主进程由于异常中止或其他原因，导致Service与访问者之间连接断开时回调该方法，主动unBindService()时不回调此方法。
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
       // 启动Service,访问者与Service绑定在一起，访问者退出，Service也终止
//        Intent intent = new Intent(this, RebootService.class);
//        intent.setAction(REBOOT_SERVICE_ACTION);
//        bindService(intent, conn, Context.BIND_AUTO_CREATE);//绑定时自动创建Service

        sharedPreferences = getSharedPreferences("reboot_task", Context.MODE_PRIVATE); //私有数据
        editor = sharedPreferences.edit();
        intiConvert();
    }

    private void intiConvert() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float w=(float)displayMetrics.widthPixels;
        convert=w/1280/(displayMetrics.density);
    }






    //解绑Service
    public void disConnService() {
        if (conn != null) {
            unbindService(conn);
        }
    }


    public static Context getContext() {
        return mContext;
    }

    public static float getConvert(){
        return convert;
    }

}
