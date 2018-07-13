package com.wangxc.business;

import android.graphics.Bitmap;

import java.util.ArrayList;


public class AppAutoItem {
	public String _title;
	public String _packageName;
	public ArrayList<String> _classNameList;
	public Bitmap _icon;
	public Boolean _isOpen;

	public AppAutoItem() {
		_title = null;
		_packageName = null;
		_classNameList = new ArrayList<String>();
		_icon = null;
		_isOpen = false;
	}
}