package com.wangxc.portraitdisplay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.wangxc.business.SwitchButton;
import com.wangxc.util.FuncUtil;


public class RebootTaskActivity extends Activity {
    protected static final String TAG = "RebootTaskActivity";
    private SwitchButton switchButton_A;
    private SwitchButton switchButton_B;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TimePicker timePicker_A;
    private TimePicker timePicker_B;
    private String plan_A;
    private String plan_B;
    private String time_A;
    private String time_B;
    private Button save_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reboot_task);
        switchButton_A = (SwitchButton) findViewById(R.id.switchButtonA);
        switchButton_B = (SwitchButton) findViewById(R.id.switchButtonB);
        timePicker_A = (TimePicker) findViewById(R.id.timePickerA);
        timePicker_B = (TimePicker) findViewById(R.id.timePickerB);
        save_button = (Button) findViewById(R.id.save_button);
        sharedPreferences = getSharedPreferences("reboot_task", Context.MODE_PRIVATE); //私有数据
        editor = sharedPreferences.edit();
        initSwitch();
        initTimePicker();
        setOnCheckChangeListener();
        setOnClick();
        setOnFocus();
    }

    private void setOnFocus() {
        switchButton_A.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // 重写焦点变化时的事件处理
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    (( SwitchButton) v)
                            .setBackgroundResource(R.drawable.letter_focus_true);
                } else {
                    ((SwitchButton) v)
                            .setBackgroundResource(R.drawable.transparent);

                }
            }
        });

        switchButton_B.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // 重写焦点变化时的事件处理
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    (( SwitchButton) v)
                            .setBackgroundResource(R.drawable.letter_focus_true);
                } else {
                    ((SwitchButton) v)
                            .setBackgroundResource(R.drawable.transparent);

                }
            }
        });
    }


    private void setOnClick() {
        save_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramView) {
                editor.putString("plan_A", plan_A);
                editor.putString("plan_B",plan_B);
                editor.putString("time_A", time_A);
                editor.putString("time_B", time_B);
                editor.commit();
                Toast toast = Toast.makeText(getApplicationContext(), "保存任务设置", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                updateAlarmTask();
            }
        });

    }


    private void updateAlarmTask() {
        if (plan_A.contains("true") && (!time_A.contains("none"))) {
            long time = FuncUtil.getAbsoluteTime(time_A);
            FuncUtil.setAlarmTime(this, 0, time);//设定第一个闹钟
        }

        if (plan_B.contains("true") && (!time_B.contains("none"))) {
            long time = FuncUtil.getAbsoluteTime(time_B);
            FuncUtil.setAlarmTime(this, 1, time);//设定第二个闹钟
        }

    }


    private void initSwitch() {
        plan_A = sharedPreferences.getString("plan_A", "false");
        plan_B = sharedPreferences.getString("plan_B", "false");
        if (plan_A.contains("false")) {
            switchButton_A.setChecked(false);
        } else {
            switchButton_A.setChecked(true);
        }
        if (plan_B.contains("false")) {
            switchButton_B.setChecked(false);
        } else {
            switchButton_B.setChecked(true);
        }
    }

    private void initTimePicker() {
        timePicker_A.setIs24HourView(true);
        timePicker_B.setIs24HourView(true);
        time_A = sharedPreferences.getString("time_A", "none");
        time_B = sharedPreferences.getString("time_B", "none");
        if (!time_A.contains("none")) {
            String[] HM = time_A.trim().split(":");
            Integer hour = Integer.parseInt(HM[0]);
            Integer minute = Integer.parseInt(HM[1]);
            timePicker_A.setCurrentHour(hour);
            timePicker_A.setCurrentMinute(minute);
        }
        if (!time_B.contains("none")) {
            String[] HM = time_B.trim().split(":");
            Integer hour = Integer.parseInt(HM[0]);
            Integer minute = Integer.parseInt(HM[1]);
            timePicker_B.setCurrentHour(hour);
            timePicker_B.setCurrentMinute(minute);
        }

        timePicker_A.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time_A = hourOfDay + ":" + minute;
            }
        });
        timePicker_B.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time_B = hourOfDay + ":" + minute;
            }
        });

    }

    private void setOnCheckChangeListener() {

        switchButton_A.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean flag) {
                // TODO Auto-generated method stub
                if (flag) {
                    Toast toast = Toast.makeText(getApplicationContext(), "打开PlanA", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    plan_A="true";
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "关闭PlanA", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    plan_A="false";
                }
            }
        });

        switchButton_B.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean flag) {
                // TODO Auto-generated method stub
                if (flag) {
                    Toast toast = Toast.makeText(getApplicationContext(), "打开PlanB", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    plan_B="true";
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "关闭PlanB", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    plan_B="false";
                }
            }
        });

    }
}
