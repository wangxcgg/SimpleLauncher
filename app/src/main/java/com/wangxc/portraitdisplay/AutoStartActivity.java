package com.wangxc.portraitdisplay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dk.animation.SwitchAnimationUtil;
import com.wangxc.adapter.MoonsSetAutoStartAdapter;
import com.wangxc.business.AppAutoItem;
import com.wangxc.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AutoStartActivity extends BaseActivity {

	private static final String TAG = "AutoStartActivity";
	private TextView title;
	private ListView mAllAppListView;
	ArrayList<AppAutoItem> allAutoItem;
	private MoonsSetAutoStartAdapter mItemAutoAdapter;
	private String[] items;
	private SwitchAnimationUtil mSwitchAnimationUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_start);

		iniView();

		allAutoItem = getAllAutoApps2();

		initAutoSettings2();

	}

	protected void iniView() {
		Locale locale = this.getResources().getConfiguration().locale; // 获取locale;
		String strCountry = locale.getCountry();// 获取country
		Log.i(TAG, "country is " + strCountry);
		title = (TextView) findViewById(R.id.title);
		mAllAppListView = (ListView) findViewById(R.id.allapp_lv);
		title.setTextSize(TypedValue.COMPLEX_UNIT_PX, DensityUtil.dip2px(30));
		title.setText("开机启动设置");
		if (strCountry.contains("TW")) {
			title.setText("開機啓動設置");
		}else if(strCountry.contains("US")) {
			title.setText("Boot up settings");
		}
		
	}

	public ArrayList<AppAutoItem> getAllAutoApps2() {

		SharedPreferences sp = getSharedPreferences("boot_app_setting",
				MODE_PRIVATE);
		String boot_app = sp.getString("boot_app", "");
		Log.d(TAG, "boot_app =" + boot_app);

		ArrayList<AppAutoItem> alllist = new ArrayList<AppAutoItem>();

		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> resolveList = getPackageManager()
				.queryIntentActivities(intent, 0);

		for (ResolveInfo app : resolveList) {

			AppAutoItem appAutoItem = new AppAutoItem();
			appAutoItem._title = app.loadLabel(getPackageManager()).toString();
			appAutoItem._packageName = app.activityInfo.packageName;
			if (appAutoItem._classNameList == null) {
				appAutoItem._classNameList = new ArrayList<String>();
			}
			appAutoItem._classNameList.add(app.activityInfo.name);

			Drawable appIcon = getPackageManager().getApplicationIcon(
					app.activityInfo.applicationInfo);
			BitmapDrawable bd = (BitmapDrawable) appIcon;
			Bitmap bmIcon = bd.getBitmap();
			appAutoItem._icon = bmIcon;
			if (boot_app.equals(appAutoItem._packageName)) {
				appAutoItem._isOpen = true;
			} else {
				appAutoItem._isOpen = false;
			}

			alllist.add(appAutoItem);
		}

		return alllist;

	}

	private void initAutoSettings2() {

		if (mAllAppListView != null) {
			mAllAppListView.setAdapter(null);
		}
		if (allAutoItem == null || allAutoItem.size() <= 0) {
			return;
		}
		mItemAutoAdapter = new MoonsSetAutoStartAdapter(AutoStartActivity.this,
				allAutoItem, items);

		mAllAppListView.setAdapter(mItemAutoAdapter);
		mAllAppListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {

						try {
							AppAutoItem appAutoItem = allAutoItem.get(arg2);
							if (appAutoItem._isOpen) {

								SharedPreferences sp = getSharedPreferences(
										"boot_app_setting", MODE_PRIVATE);
								SharedPreferences.Editor editor = sp.edit();
								editor.putString("boot_app", "");
								editor.commit();

								allAutoItem.get(arg2)._isOpen = false;

							} else {
								SharedPreferences sp = getSharedPreferences(
										"boot_app_setting", MODE_PRIVATE);
								SharedPreferences.Editor editor = sp.edit();
								editor.putString("boot_app",
										appAutoItem._packageName);
								editor.commit();

								for (AppAutoItem app : allAutoItem) {
									if (app._isOpen) {
										app._isOpen = false;
									}
								}
								allAutoItem.get(arg2)._isOpen = true;
							}
							mItemAutoAdapter.notifyDataSetChanged();

						} catch (Exception e) {

							e.printStackTrace();
						}

					}
				});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (mSwitchAnimationUtil == null) {
			mSwitchAnimationUtil = new SwitchAnimationUtil();
			mSwitchAnimationUtil.startAnimation(mAllAppListView,
					SwitchAnimationUtil.AnimationType.HORIZION_RIGHT);
		}
	}

}
