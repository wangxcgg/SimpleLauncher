package com.wangxc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import com.wangxc.business.MyApplication;
import com.wangxc.util.FuncUtil;

import java.lang.reflect.Method;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private static final String ALARM_ACTION = "android.moons.portraitdisplay.alarm.action";
    private String plan_A;
    private String plan_B;
    private String time_A;
    private String time_B;

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "receieve alarm");
        if (ALARM_ACTION.equals(intent.getAction())) {
//            Toast.makeText(context, "hello alarm", Toast.LENGTH_LONG).show();
            runRebootTask();
            return;
        }
    }

    private void runRebootTask() {
        Calendar cal = Calendar.getInstance();
        String hour;
        String minute;
        if (cal.get(Calendar.AM_PM) == 0) {
            hour = String.valueOf(cal.get(Calendar.HOUR));
        } else {
            hour = String.valueOf(cal.get(Calendar.HOUR) + 12);
        }
        minute = String.valueOf(cal.get(Calendar.MINUTE));
        Log.i(TAG, "time is" + hour + ":" + minute);
        String currentTime = hour + ":" + minute;
        SharedPreferences sharedPreferences = MyApplication.getContext().getSharedPreferences("reboot_task", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        plan_A = sharedPreferences.getString("plan_A", "false");
        plan_B = sharedPreferences.getString("plan_B", "false");
        time_A = sharedPreferences.getString("time_A", "none");
        time_B = sharedPreferences.getString("time_B", "none");
        if (time_A.contains(currentTime) || time_B.contains(currentTime)) {
//            rebootSystem();
            turnoffSystem();
//            shutDown();
        } else {
            updateAlarmSetting();
        }
    }


    private void updateAlarmSetting() {
        Log.i(TAG, "update alarm");
        if (plan_A.contains("true") && (!time_A.contains("none"))) {
            long time = FuncUtil.getAbsoluteTime(time_A);
            FuncUtil.setAlarmTime(MyApplication.getContext(), 0, time);//设定第一个闹钟
        }
        if (plan_B.contains("true") && (!time_B.contains("none"))) {
            long time = FuncUtil.getAbsoluteTime(time_B);
            FuncUtil.setAlarmTime(MyApplication.getContext(), 1, time);//设定第二个闹钟
        }
    }


    private void rebootSystem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PowerManager pManager = (PowerManager) MyApplication.getContext()
                        .getSystemService(Context.POWER_SERVICE); // 重启到fastboot模式
                pManager.reboot("");
            }
        }).start();
    }


    private void turnoffSystem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 获得ServiceManager类
                    Class<?> ServiceManager = Class
                            .forName("android.os.ServiceManager");
                    // 获得ServiceManager的getService方法
                    Method getService = ServiceManager.getMethod(
                            "getService", String.class);
                    // 调用getService获取RemoteService
                    Object oRemoteService = getService.invoke(null,
                            Context.POWER_SERVICE);
                    // 获得IPowerManager.Stub类
                    Class<?> cStub = Class
                            .forName("android.os.IPowerManager$Stub");
                    // 获得asInterface方法
                    Method asInterface = cStub.getMethod("asInterface",
                            IBinder.class);
                    // 调用asInterface方法获取IPowerManager对象
                    Object oIPowerManager = asInterface.invoke(null,
                            oRemoteService);
                    // 获得shutdown()方法
                    Method shutdown = oIPowerManager.getClass()
                            .getMethod("shutdown", boolean.class,
                                    boolean.class);
                    // 调用shutdown()方法
                    shutdown.invoke(oIPowerManager, false, true);
                } catch (Exception e) {
                    Log.e(TAG, e.toString(), e);
                }
            }
        }).start();
    }


    public static void shutDown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class<?> clz = Class.forName("android.os.ServiceManager");
                    Method getService = clz.getMethod("getService", String.class);
                    Object powerService = getService.invoke(null, Context.POWER_SERVICE);
                    Class<?> cStub = Class.forName("android.os.IPowerManager$Stub");
                    Method asInterface = cStub.getMethod("asInterface", IBinder.class);
                    Object IPowerManager = asInterface.invoke(null, powerService);
                    Method shutDown = IPowerManager.getClass().getMethod("shutdown", boolean.class, boolean.class);
                    shutDown.invoke(IPowerManager, false, true);
                } catch (Exception e) {
                    Log.e("wmb", "--shutDown has an exception");
                    e.printStackTrace();
                }
            }
        }).start();


    }


}  