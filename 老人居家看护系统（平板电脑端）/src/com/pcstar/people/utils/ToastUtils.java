package com.pcstar.people.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast mToast;
    public static void showToast(Context context, String text) {
        if (context != null && text != null) {
            try {
                if (mToast == null) {
                    mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(text);
                    mToast.setDuration(Toast.LENGTH_SHORT);
                }
                mToast.show();
            } catch (NullPointerException e) {
            }
        }
    }
}
