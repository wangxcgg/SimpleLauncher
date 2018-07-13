package com.wangxc.portraitdisplay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dk.animation.SwitchAnimationUtil;
import com.dk.animation.SwitchAnimationUtil.AnimationType;
import com.wangxc.business.ApplicationInfo;
import com.wangxc.util.DensityUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllAppActivity extends BaseActivity {
    /**
     * Tag used for logging errors.
     */
     private SwitchAnimationUtil mSwitchAnimationUtil;
    private static final String TAG = "AllAppActivity";


    private ArrayList<ApplicationInfo> mApplications;


    private GridView mGrid;

    private   boolean mIsFirtStart = true;
    private ApplicationsAdapter mApplicationsAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG,"AllAppActivity onCreate()");
        super.onCreate(savedInstanceState);
        // 设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.all_app_activity);
        GridView gridview = (GridView) findViewById(R.id.all_apps);
        gridview.setColumnWidth(DensityUtil.dip2px(200));

        mIsFirtStart = true;

        Handler threadHandler = new Handler();
        threadHandler.postDelayed(runnable, 200);
        loadApplications(true);

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // loadApplications(true);

            bindApplications();
            // bindFavorites(true);
            // bindRecents();
            bindButtons();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.show_all_app, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Close the menu
        if (Intent.ACTION_MAIN.equals(intent.getAction())) {
            getWindow().closeAllPanels();
        }
    }

    @Override
    protected void onRestart() {
        Log.w(TAG,"AllAppActivity onRestart()");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        Log.w(TAG,"AllAppActivity onPause()");
        super.onPause();
    }
    @Override
    protected void onStop() {
        Log.w(TAG,"AllAppActivity onStop()");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.w(TAG,"AllAppActivity onDestroy()");
        super.onDestroy();

        // Remove the callback for the cached drawables or we leak
        // the previous Home screen on orientation change
        if (mApplications != null) {
            int count = mApplications.size();
            for (int i = 0; i < count; i++) {
                mApplications.get(i).icon.setCallback(null);


                mApplications.get(i).icon = null;
            }
            mApplications.clear();
            mApplications = null;
        }

        // mHideAppList = null;

        if (mApplicationsAdapter != null) {
            mApplicationsAdapter.clear();
            mApplicationsAdapter = null;
        }

        mGrid = null;

        runnable = null;
        System.gc();

        // unregisterReceiver(mWallpaperReceiver);
        // unregisterReceiver(mApplicationsReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.w(TAG,"AllAppActivity onResume()");
        // bindRecents();
        if (mIsFirtStart) {
            mIsFirtStart = false;
        } else {
            loadApplications(false);
            bindApplications();
            bindButtons();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        // final boolean opened = state.getBoolean(KEY_SAVE_GRID_OPENED, false);
        // if (opened) {
        // // showApplications(false);
        // }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // outState.putBoolean(KEY_SAVE_GRID_OPENED, mGrid.getVisibility() ==
        // View.VISIBLE);
    }



    /**
     * Creates a new appplications adapter for the grid view and registers it.
     */
    private void bindApplications() {
        if (mGrid == null) {
            mGrid = (GridView) findViewById(R.id.all_apps);
        }
        mApplicationsAdapter = new ApplicationsAdapter(this, mApplications);
        mGrid.setAdapter(mApplicationsAdapter);
        mGrid.setSelection(0);

        // if (mApplicationsStack == null) {
        // mApplicationsStack = (ApplicationsStackLayout)
        // findViewById(R.id.faves_and_recents);
        // }
    }

    /**
     * Binds actions to the various buttons.
     */
    private void bindButtons() {
        // mShowApplications = findViewById(R.id.show_all_apps);
        // mShowApplications.setOnClickListener(new ShowApplications());
        // mShowApplicationsCheck = (CheckBox)
        // findViewById(R.id.show_all_apps_check);
        if (mGrid != null)
            mGrid.setOnItemClickListener(new ApplicationLauncher());
    }


    private ApplicationInfo getApplicationInfo(PackageManager manager,
            Intent intent) {
        ResolveInfo resolveInfo = manager.resolveActivity(intent, 0);

        if (resolveInfo == null) {
            return null;
        }

        ApplicationInfo info = new ApplicationInfo();
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        // info.icon = activityInfo.loadIcon(manager);
        if (info.title == null || info.title.length() == 0) {
            info.title = activityInfo.loadLabel(manager);
        }
        if (info.title == null) {
            info.title = "";
        }
        resolveInfo = null;
        activityInfo = null;
        return info;
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                // mBackDown = true;
                AllAppActivity.this.finish();
                return true;
            case KeyEvent.KEYCODE_HOME:
                // mHomeDown = true;
                AllAppActivity.this.finish();
                return true;
            case KeyEvent.KEYCODE_MENU:
                return true;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (!event.isCanceled()) {
                    // Do BACK behavior.
                }
                // mBackDown = true;
                return true;
            case KeyEvent.KEYCODE_HOME:
                if (!event.isCanceled()) {
                    // Do HOME behavior.
                }
                // mHomeDown = true;
                return true;
            }
        }

        return super.dispatchKeyEvent(event);
    }



    /**
     * Loads the list of installed applications in mApplications.
     */
    private void loadApplications(boolean isLaunching) {
        if (isLaunching && mApplications != null) {
            return;
        }

        Log.d(TAG,"loadApplications()");
        PackageManager manager = getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);

         Collections.sort(apps, new
         ResolveInfo.DisplayNameComparator(manager));

        if (apps != null) {
            int count = apps.size();
            Log.d(TAG,"count="+count);
            if (mApplications == null) {
                mApplications = new ArrayList<ApplicationInfo>(count);
            } else {
                if (mApplications != null) {
                    final int ncount = mApplications.size();
                    for (int i = 0; i < ncount; i++) {
                        mApplications.get(i).icon.setCallback(null);
                        mApplications.get(i).icon = null;

                    }
                }
            }
            mApplications.clear();
            // PacketUtils packageUtils = new PacketUtils(
            // this.getApplicationContext());
            String tag_str="75";
            int index =1;
            for (int i = 0; i < count; i++) {
                ApplicationInfo application = new ApplicationInfo();

                ResolveInfo info = apps.get(i);

                try {
                      if(true){
                        // if(!mHideAppList.IsHidePackageName(info.activityInfo.packageName)){
                        application.title = info.loadLabel(manager);


                        if (application.title.equals("")
                                || application.title == null) {
                            application.title = getApplicationName(info.activityInfo.applicationInfo.packageName);
                        }
                        ComponentName cn = new ComponentName(
                                info.activityInfo.applicationInfo.packageName,
                                info.activityInfo.name);
                        application
                                .setActivity(
                                        cn,
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        application.icon = info.activityInfo
                                .loadIcon(manager);
                        cn = null;
                        // ActivityInfo activityinfo =
                        // verifyPackage(manager,info);
                        // if(activityinfo!=null){

                        application.tag_id= tag_str+index;
                        index++;
                        mApplications.add(application);
                        // }
                        // }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            // packageUtils = null;
        }
        apps.clear();
        apps = null;
        manager = null;
        mainIntent = null;

    }



    private String getApplicationName(String strPackageName) {
        PackageManager packageManager = null;
        android.content.pm.ApplicationInfo applicationInfo = null;
        try {
            packageManager = this.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(strPackageName,
                    0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = (String) packageManager
                .getApplicationLabel(applicationInfo);
        packageManager = null;
        applicationInfo = null;
        return applicationName;
    }

    public ActivityInfo verifyPackage(PackageManager manager, ResolveInfo info) {
        String packageName = info.activityInfo.packageName;
        String className = info.activityInfo.name;
        // final PackageManager manager = _context.getPackageManager();
        ComponentName cn;
        ActivityInfo activityinfo;
        try {
            try {
                cn = new ComponentName(packageName, className);
                activityinfo = manager.getActivityInfo(cn, 0);
                cn = null;
                return activityinfo;
            } catch (PackageManager.NameNotFoundException nnfe) {
                String[] packages = manager
                        .currentToCanonicalPackageNames(new String[] { packageName });
                cn = new ComponentName(packages[0], className);
                activityinfo = manager.getActivityInfo(cn, 0);
                cn = null;
                packages = null;
                return activityinfo;
            }
        } catch (PackageManager.NameNotFoundException nnfe) {
            // Log.e(TAG, "Unable to add category: " + item._packageName + "/"
            // + item._className, nnfe);
            cn = null;
            return null;
        }

    }



    public final class ViewHolder {
        public ImageView imageView;
        public TextView textView;

    }

    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            DensityUtil.dip2px(100), DensityUtil.dip2px(100));

    /**
     * GridView adapter to show the list of all installed applications.
     */
    private class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {
        private Rect mOldBounds = new Rect();

        public ApplicationsAdapter(Context context,
                ArrayList<ApplicationInfo> apps) {
            super(context, 0, apps);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.topMargin = DensityUtil.dip2px(20);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ApplicationInfo info = mApplications.get(position);
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.application_item,
                        parent, false);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.label);
                holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        DensityUtil.dip2px(30));
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.app_icon);
                holder.imageView.setLayoutParams(layoutParams);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            // textView.setCompoundDrawablesWithIntrinsicBounds(null, icon,
            // null, null);
            holder.imageView.setBackgroundDrawable(info.icon);
            holder.textView.setText(info.title);

            return convertView;
        }
    }


    /**
     * Starts the selected activity/application in the grid view.
     */
    private class ApplicationLauncher implements
            AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View v, int position,
                                long id) {
            ApplicationInfo app = (ApplicationInfo) parent
                    .getItemAtPosition(position);



            startActivity(app.intent);
            app = null;
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d(TAG,"onWindowFocusChanged hasFocus:"+hasFocus);
        super.onWindowFocusChanged(hasFocus);
        if (mSwitchAnimationUtil == null) {
            mSwitchAnimationUtil = new SwitchAnimationUtil();
            mSwitchAnimationUtil.startAnimation(getWindow().getDecorView(),AnimationType.SCALE);
        }
    }

}

