package com.wangxc.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wangxc.business.AppAutoItem;
import com.wangxc.business.SwitchButton;
import com.wangxc.portraitdisplay.R;
import com.wangxc.util.DensityUtil;

import java.util.ArrayList;


public class MoonsSetAutoStartAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<AppAutoItem> mApps;
	private Context context;
	private ArrayAdapter<String> mAdapter;
    private String[] res;
    private PackageManager pManager;
    
	public MoonsSetAutoStartAdapter(Context ms,ArrayList<AppAutoItem> listItem, String[] resString) {
		//context = ms.getApplicationContext();
	    context=ms;
		mInflater = LayoutInflater.from(ms);
		pManager = ms.getPackageManager();	
		mApps = listItem;
		res = resString;
	//	mAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_selectable_list_item, resString);
	}

	
	public int getCount() {
		return mApps.size();
	}

	
	public Object getItem(int position) {
		return mApps.get(position);
	}

	
	public long getItemId(int position) {
		return position;
	}
	
	//RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (60*Launcher.g_fscale), (int) (60*Launcher.g_fscale));
	public View getView(int position, View convertView, ViewGroup parent) {
		position = position;
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.autostart_item_type, null);

			holder = new ViewHolder();

		//	layoutParams.leftMargin = (int) (50*Launcher.g_fscale);
			holder.image = (ImageView) convertView.findViewById(R.id.ItemImage);
			setImageSize((int) (DensityUtil.dip2px(60)), (int) (DensityUtil.dip2px(60)),holder.image);
			//holder.image.setLayoutParams(layoutParams);
			holder.caption = (TextView) convertView.findViewById(R.id.ItemText);
			holder.caption.setTextSize(TypedValue.COMPLEX_UNIT_PX,DensityUtil.dip2px(30));		
			holder.switchButton = (SwitchButton)convertView.findViewById(R.id.ItemSwitchBtn);
					//holder.spinnerBtn .setTag(position+"");

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		AppAutoItem info = mApps.get(position);
		
		holder.image.setBackgroundDrawable(new BitmapDrawable(info._icon));
		
		holder.caption.setText(info._title);
		if(info._isOpen){
			//holder.itemtv.setText("已开启");
			holder.switchButton.setChecked(info._isOpen);
		}else{
			//holder.itemtv.setText("已禁止");
			holder.switchButton.setChecked(info._isOpen);
		}
		
	
		return convertView;
	}

	private void setImageSize(int x, int y, ImageView imageView) {
		LayoutParams lp = imageView.getLayoutParams();
		lp.width = x;
		lp.height = y;
		imageView.setLayoutParams(lp);
	}

	private class ViewHolder {
		public TextView caption;
		public ImageView image;
	//	public TextView itemtv;
		public Bitmap bitmap;
		public SwitchButton switchButton;
	}
}