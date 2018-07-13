package com.wangxc.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;


public class FuncUtil {

	private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
	private static final String TAG = "FuncUtil";

	/**
	 * Generate a value suitable for use in {@link #setId(int)}.
	 * This value will not collide with ID values generated at build time by aapt for R.id.
	 *
	 * @return a generated ID value
	 */
	//生成视图控件ID，从1开始加
	public static int generateViewId() {
	    for (;;) {
	        final int result = sNextGeneratedId.get();
	        // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
	        int newValue = result + 1;
	        if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
	        if (sNextGeneratedId.compareAndSet(result, newValue)) {
	            return result;
	        }
	    }
	}
	
	
	
	public static  boolean isExitFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
		    return false;
		}
		else
		{
			return true;
		}
	}
	

	
	public static  boolean isExitDir(String path) {
		File file = new File(path);
		if (!file.exists()) {
		    file.mkdir();
		    return false;
		}
		else
		{
			return true;
		}
	}
	
	
	public static void ensureDir(String path)
	{
		File file = new File(path);
		if (!file.exists()) {
		    file.mkdir();
		}
	}
	
	
	public static Boolean CopyAssetsFile(Context nContext, String filename, String des) {
	    Boolean isSuccess = true;
	    //复制安卓apk的assets目录下任意路径的单个文件到des文件夹，注意是否对des有写权限
	    AssetManager assetManager = nContext.getAssets();

	   InputStream in = null;
	   OutputStream out = null;
	   try {
		 
	       in = assetManager.open(filename);
	       int start=filename.lastIndexOf("/");  
	       if(start!=-1)
	       {
	    	   filename=filename.substring(start);
	       }
	       String newFileName = des + "/" + filename;
	       out = new FileOutputStream(newFileName);

	       byte[] buffer = new byte[1024];
	       int read;
	       while ((read = in.read(buffer)) != -1) {
	           out.write(buffer, 0, read);
	       }
	       in.close();
	       in = null;
	       out.flush();
	       out.close();
	       out = null;
	    } catch (Exception e) {
	      e.printStackTrace();
	      isSuccess = false;
	    }

	    return isSuccess;

	}
	
		
	public static Boolean CopyAssetsDir(Context nContext, String src, String des) {
		  Boolean isSuccess = false;
		  String[] files;
		  try
		  {
		    files = nContext.getResources().getAssets().list(src);
//		    Log.e(TAG,"====files="+files.toString());
		  }
		  catch (IOException e1)
		  {
			  e1.printStackTrace();
		    return false;
		  }

		  if(files.length==0){
		    isSuccess = CopyAssetsFile(nContext,src,des);//对于文件直接复制
//		    Log.e(TAG,"====files copy isSuccess="+isSuccess);
		    if(!isSuccess)
		      return false;
		  }
		    for(int i=0;i<files.length;i++){
		   isSuccess = CopyAssetsDir(nContext,src + "/"+files[i], des);//递归调用
//		   Log.e(TAG,"====files copy isSuccess1="+isSuccess);
		  if(!isSuccess)
		    return false;
		    }
		    
		        return isSuccess;
		      
	}
		
	public static boolean downLoadFileToPath(Context nContext, String httpUrl,
                                             String filepath) {

		if (httpUrl == null) {
			Log.e(TAG, "downLoadFile() the httpUrl can`t be null!");
			return false;
		}

		HttpURLConnection conn = null;
		FileOutputStream fos = null;
		InputStream inputStream = null;
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		try {
			URL url = new URL(httpUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");// 这里是不能乱写的，详看API方法
			conn.setConnectTimeout(10 * 1000);
			if (conn.getResponseCode() >= 400) {
				Log.e(TAG,
						"downLoadFile() connect error:"
								+ conn.getResponseCode());

				return false;
			}

			int length = conn.getContentLength();

			if (length <= 0) {
				Log.e(TAG, "downLoadFile() file content length can not be 0!");
				return false;
			}

			inputStream = conn.getInputStream();

			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = inputStream.read(buffer)) != -1) {
				outstream.write(buffer, 0, len);
			}
			byte[] data = outstream.toByteArray();

			// 最后一次性写入本地
			fos = new FileOutputStream(filepath);
			fos.write(data);

			return true;

		} catch (Exception e) {

			e.printStackTrace();
			Log.e(TAG, "downLoadFile() download filed,error:" + e.getMessage());

			return false;
		} finally {
			try {
				if (conn != null)
					conn.disconnect();

				if (fos != null)
					fos.close();

				if (outstream != null)
					outstream.close();

				if (inputStream != null)
					inputStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	public static boolean downLoadFile(Context nContext, String httpUrl, String fileName) {
		
		
		if(httpUrl==null)
		{
			Log.e(TAG, "downLoadFile() the httpUrl can`t be null!");
			return false;
		}
		
		HttpURLConnection conn = null;
		
		FileOutputStream fos = null;
		InputStream inputStream =null;
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		try {
			URL url = new URL(httpUrl);

			 conn = (HttpURLConnection) url.openConnection();
			 conn.setRequestMethod("GET");// 这里是不能乱写的，详看API方法 
		     conn.setConnectTimeout(10 * 1000); 
		     if (conn.getResponseCode() >= 400) {
					Log.e(TAG, "downLoadFile() connect error:" + conn.getResponseCode());

					return false;
				}
		     
		     
//		     int length = conn.getContentLength();
//				
//				if (length <= 0) {
//					Log.e(TAG, "downLoadFile() file content length can not be 0!");
//					return false;
//				}
				
		     
				
		      inputStream = conn.getInputStream(); 
	          

		     byte[] buffer = new byte[1024];
		     int len = -1; 
		        while ((len = inputStream.read(buffer)) != -1) { 
		            outstream.write(buffer, 0, len); 
		        } 
		        byte[] data =outstream.toByteArray();
			
		     //最后一次性写入本地
			 fos = nContext.openFileOutput(fileName, Context.MODE_PRIVATE);
			 fos.write(data);
				
			
			return true;

		} catch (Exception e) {
			
			e.printStackTrace();
			Log.e(TAG, "downLoadFile() download filed,error:" + e.getMessage());
			
			return false;
		} 
		finally
		{
			try{
			if(conn!=null)
			conn.disconnect();
			
			if(fos!=null)
			fos.close();
			
			if(outstream!=null)
				outstream.close();
	
			if(inputStream!=null)
				inputStream.close();
			
			}
	    catch (IOException e) {
	     e.printStackTrace();
		}
			
		}
	}
	//获取应用程序的标题
	public static String getAppTitle(Context context, String packname) {
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packname, 0);
			if (info == null) {
				return "";
			}

			return info.loadLabel(context.getPackageManager()).toString();
		} catch (Exception e) {

			e.printStackTrace();
			return "";

		}
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth)
		{
			if (width > height)
			{
				inSampleSize = Math.round((float)height / (float)reqHeight);
				
				} 
			else
			{
					inSampleSize = Math.round((float)width / (float)reqWidth);
			}
		}
		
		return inSampleSize;
	}

	
	public static int computeSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength,
	            maxNumOfPixels);

	    int roundedSize;
	    if (initialSize <= 8) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }

	    return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;

	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
	            .sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
	            .floor(w / minSideLength), Math.floor(h / minSideLength));

	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }

	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}
	
	public static Bitmap getBitmapFromFile(String path, String name, int width, int height) {
        
		
		File dst = new File(path, name);
	    if (null != dst && dst.exists()) {
	        BitmapFactory.Options opts = null;
	        if (width > 0 && height > 0) {
	            opts = new BitmapFactory.Options();
	            opts.inJustDecodeBounds = true;
	            BitmapFactory.decodeFile(dst.getPath(), opts);
	            // 计算图片缩放比例
	            final int minSideLength = Math.min(width, height);
	            opts.inSampleSize = computeSampleSize(opts, minSideLength,
	                    width * height);
	            opts.inJustDecodeBounds = false;
	            opts.inInputShareable = true;
	            opts.inPurgeable = true;
	        }
	        try {
	            return BitmapFactory.decodeFile(dst.getPath(), opts);
	        } catch (OutOfMemoryError e) {
	            e.printStackTrace();
	        }
	    }
	    return null;
	}
	
	
	//加载本地图片
	public static Drawable loadLocalImage(String path, String name, int reqWidth, int reqHeight) {
		Bitmap bt = null;
		 Drawable drawable=null;
		String file_path=path+"/"+name;
		
		File file = new File(path, name);
		if (file.exists()) {
			//FileInputStream inStream = new FileInputStream(file);
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			 BitmapFactory.decodeFile(file_path, options);
			 options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
			 options.inJustDecodeBounds = false;
			//BitmapFactory.decodeStream(is, outPadding, opts)
			 bt = BitmapFactory.decodeFile(file_path, options);
			 if(bt!=null)
			 {
			 drawable = new BitmapDrawable(bt);
			// bt.recycle();
			 bt=null;
			 }
			//inStream.close();
		} else {
			file.delete();
			Log.e(TAG, "file not exitsts");
		}
		
		return drawable;
	}
	
	public static Bitmap loadLocalImage(String path, String name) {
		Bitmap bt = null;
		try {
		
			File file = new File(path, name);
			if (file.exists()) {
				FileInputStream inStream = new FileInputStream(file);
				bt = BitmapFactory.decodeFileDescriptor(inStream.getFD());
				inStream.close();
			} else {
				file.delete();
				Log.e(TAG, "###file not exitsts");
			}
			
		}catch (Exception e) {
			Log.e(TAG, "###Exception....e=" + e.toString());
			e.printStackTrace();
		}
		return bt;
	}
	
	public static boolean startActivityByIntent(Context context, Intent intent)
	{
		try{
		if (intent == null)
		{
			return false;
		}
		context.startActivity(intent);
		return true;
		}
		catch(Exception e)
		{
			Log.e(TAG,"startActivityByIntent error");
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean startActivityByName(Context context, String packageName) {
		
		try{
		PackageManager packageManager = context.getPackageManager();

		if (packageName==null||packageName.equals("")) 
		{
			return false;
		}

			Intent intent = packageManager.getLaunchIntentForPackage(packageName);
			if (intent == null)
			{
				return false;
			}
				context.startActivity(intent);
				return true;
		}
		catch(Exception e)
		{
			Log.e(TAG,"startActivityByName error");
//			e.printStackTrace();
			return false;
		}
		
	}
	
	public static  boolean isAppInstalled(Context context, String packageName) {

        if (packageName == null || "".equals(packageName))
            return false;
        
        
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (Exception e) {
            return false;
        }
        
        }
	
	public static int getVersionCode(Context context)//获取版本号(内部识别号)
	{  
	    try {  
	        PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return pi.versionCode;  
	    } catch (Exception e) {
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	        return 0;  
	    }  
	}  
	
	
	public static String getVersionName(Context context)//获取版本号
    {  
        try {  
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;  
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();  
            return "unknow";  
        }  
    }  
	
	//通过包名获取app的图标
	public static Drawable getAppIcon(Context context, String packname){
	      try {    
	             ApplicationInfo info = context.getPackageManager().getApplicationInfo(packname, 0);
	             if(info==null)
	             {
	            	 return null;
	             }
	             
	             return info.loadIcon(context.getPackageManager());    
	        }
	      catch (NameNotFoundException e)
	      {
	    	  e.printStackTrace();   
	            return null;
	      }
	      catch (Exception e) {
	          
	            e.printStackTrace();   
	            return null;
	              
	        }    
	    }    
	
	
	public static String getUrlContent(String url) {

		try {
			HttpClient httpClient = new DefaultHttpClient();
			// connect timeout
			httpClient.getParams().setIntParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
			// read data timeout
			httpClient.getParams().setIntParameter(
					CoreConnectionPNames.SO_TIMEOUT, 5000);

			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code != 200) {
				Log.e(TAG, String.format("fail to fetch json url, error http code:%d", code));
				return "";
			}

			String result = null;
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {
				builder.append(s);
			}
			result = builder.toString();
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	//pram   报名/类名
	public static boolean StartApp(Context ncontext, String pram)
	{
		 String[] packageName_result=	pram.split("/");
		 if(packageName_result.length==1)
		 {
			 if(!FuncUtil.startActivityByName(ncontext, packageName_result[0]))
		    	{
		    		Log.e(TAG,"start "+pram+" by pgname error!");
		    		return false;
		    	}
			 
		 }
		 else if(packageName_result.length==2)
		 {
			 try{
				  Intent intent = new Intent(Intent.ACTION_MAIN);
				  intent.addCategory(Intent.CATEGORY_LAUNCHER);
				  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				  ComponentName cn = new ComponentName(packageName_result[0], packageName_result[1]);
				  intent.setComponent(cn);  
				  ncontext.startActivity(intent);  
				  }
				  catch(Exception e)
				  {
					  Log.e(TAG,"start "+pram+"  by class error!"+e.getMessage());
					 return false;
				  }
		 }
		 return true;
	}

	//设置闹钟，不同的闹钟有不同的请求码
	public static void setAlarmTime(Context context, int requestCode, long triggerAtMillis) {
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent("android.moons.multiLauncherGZ.alarm.action");
		PendingIntent sender = PendingIntent.getBroadcast(
				context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		//第一个参数表明使用的是绝对时间，而第二个参数说明是自1970年1月1日0:00至Calendar实例对象设定的时间的毫秒数
		am.set(AlarmManager.RTC_WAKEUP, triggerAtMillis,sender);
//		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+5000, sender);
	}

   //时间格式为"18:50"
   public static long getAbsoluteTime(String time) {
	   String[] HM=time.trim().split(":");
	   int hour= Integer.parseInt(HM[0]);
	   int minute= Integer.parseInt(HM[1]);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	   //如果设定的闹钟时间比当前时间小(期望第二天的这个时间响)
	   if(System.currentTimeMillis()> cal.getTimeInMillis()){
		   cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
	   }
	   //24*3600*1000毫秒（24小时)
	   Log.i(TAG,"currentTime"+ System.currentTimeMillis()+"settingTime:"+cal.getTimeInMillis());
	   return cal.getTimeInMillis();
	}



}
