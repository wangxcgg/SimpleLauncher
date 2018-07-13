package com.wangxc.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class DensityUtil {

	private static final String TAG = "DensityUtil";
	public static float scale_ =1;
	
	public static float  init(Context context)
	{
		
		 scale_ = context.getResources().getDisplayMetrics().density;
		 return scale_;
	}
	
	// int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
	// // 屏幕宽（像素，如：480px）
	// int screenHeight =
	// getWindowManager().getDefaultDisplay().getHeight(); // 屏幕高（像素，如：800p）
	// int xDip = DensityUtil.px2dip(SettingActivity.this, (float)
	// (screenWidth * 1.0));
	// int yDip = DensityUtil.px2dip(SettingActivity.this, (float)
	// (screenHeight * 1.0));

	public static int getScreenHeight(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}

	public static int getScreenWidth(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px( float dpValue) {
		if(scale_==0)
		{
			Log.e(TAG,"you forget to ini DensityUtil!!! ");
		}
		return (int) (dpValue * scale_ + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float pxValue) {
		if(scale_==0)
		{
			Log.e(TAG,"you forget to ini DensityUtil!!! ");
		}
		return (int) (pxValue / scale_ + 0.5f);//float0.5
	}
}