package com.wangxc.portraitdisplay;

import android.app.Activity;
import android.view.KeyEvent;

public class BaseActivity extends Activity {
    private static String TAG = "BaseActivity";

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int nKeyCode = event.getKeyCode();


        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event);
        }

        return super.dispatchKeyEvent(event);


    }
}
