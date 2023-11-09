package com.example.noteswithfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.noteswithfirebase.util.NetworkUtil;
import com.example.noteswithfirebase.util.ToastUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(
                () -> {
                    if (NetworkUtil.checkNetwork(SplashActivity.this)) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            startActivity(new Intent(SplashActivity.this, NoteListActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        }
                    } else {
                        ToastUtil.showToast(SplashActivity.this, "Connect to the internet and log in");
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                    finish();
                },
                2000
        );
    }
}