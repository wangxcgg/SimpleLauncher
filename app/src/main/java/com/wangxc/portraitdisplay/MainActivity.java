package com.wangxc.portraitdisplay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.wangxc.util.FuncUtil;

public class MainActivity extends Activity {
    private final String TAG = this.getClass().toString();
    private Button AutoStart;
    private Button PowerOffTask;
    private Button SystemSetting;
    private Button AllApplication;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setEventListener();
//        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

        sharedPreferences = getSharedPreferences("reboot_task", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        do_boot_app();
        AddAlarmTask();
    }



    private void do_boot_app() {
        Handler threadHandler = new Handler();
        threadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 判断启动时间
                long boottime = SystemClock.elapsedRealtime();
                Log.d(TAG, "boottime =" + boottime);
                if (boottime >= 300000) {
                    Log.d(TAG, "boottime last than 5 min,return! ");
                    return;
                }
                SharedPreferences sp = getSharedPreferences("boot_app_setting",
                        MODE_PRIVATE);
                String boot_app = sp.getString("boot_app", "");
                Log.d(TAG, "boot_app form SharedPreferences =" + boot_app);

                if (boot_app.equals("")) {
                    return;
                }
                try {
                    Intent i = getPackageManager().getLaunchIntentForPackage(
                            boot_app);
                    startActivity(i);

                } catch (Exception e) {
                    Log.e(TAG, "boot app failed!");
                    e.printStackTrace();
                }

            }

        }, 10000);

    }



    private void AddAlarmTask() {
        String plan_A = sharedPreferences.getString("plan_A", "none");
        String plan_B = sharedPreferences.getString("plan_A", "none");
        String time_A = sharedPreferences.getString("time_A", "none");
        String time_B = sharedPreferences.getString("time_B", "none");
        if (plan_A.contains("true") && (!time_A.contains("none"))) {
            long time = FuncUtil.getAbsoluteTime(time_A);
            FuncUtil.setAlarmTime(this, 0, time);//设定第一个闹钟
        }

        if (plan_B.contains("true") && (!time_B.contains("none"))) {
            long time = FuncUtil.getAbsoluteTime(time_B);
            FuncUtil.setAlarmTime(this, 1, time);//设定第二个闹钟
        }

    }
    private void initView() {
        AutoStart = (Button) findViewById(R.id.button1);
        PowerOffTask = (Button) findViewById(R.id.button2);
        SystemSetting = (Button) findViewById(R.id.button3);
        AllApplication = (Button) findViewById(R.id.button4);
    }


    private void setEventListener() {
        AutoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setClassName(MainActivity.this, "com.wangxc.portraitdisplay.AutoStartActivity");//另外一個activity的包名，Activity類名
                startActivity(it);
            }
        });
        PowerOffTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setClassName(MainActivity.this, "com.wangxc.portraitdisplay.RebootTaskActivity");
                startActivity(it);
            }
        });
        SystemSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settings = new Intent(
                        android.provider.Settings.ACTION_SETTINGS);
                settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(settings);
            }
        });
        AllApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent();
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setClassName(MainActivity.this, "com.wangxc.portraitdisplay.AllAppActivity");
                startActivity(it);
            }
        });

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int nKeyCode = event.getKeyCode();
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }
        Log.d(TAG, "nKeyCode=" + nKeyCode);
        if (nKeyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }


}
