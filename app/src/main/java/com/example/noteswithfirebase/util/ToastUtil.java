package com.example.noteswithfirebase.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.noteswithfirebase.R;
import com.google.android.material.textview.MaterialTextView;

public class ToastUtil {
    public static void showToast(Context c, String info) {
        View toastView = LayoutInflater.from(c).inflate(R.layout.toast_custom_view, null);
        MaterialTextView text = toastView.findViewById(R.id.toast_info);
        text.setText(info);
        Toast toast = new Toast(c);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastView);
        toast.show();
    }
}
