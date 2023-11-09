package com.example.noteswithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import com.example.noteswithfirebase.databinding.ActivityLoginBinding;
import com.example.noteswithfirebase.util.NetworkUtil;
import com.example.noteswithfirebase.util.ToastUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding activityLoginBinding;

    private boolean progressBarVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(activityLoginBinding.getRoot());

        activityLoginBinding.loginBtn.setOnClickListener(this::loginAccount);
        activityLoginBinding.createAccountTv.setOnClickListener(this::goToCreateAccountAcvivity);
    }

    private void goToCreateAccountAcvivity(View v) {
        startActivity(new Intent(this, CreateAccountActivity.class));
        finish();
    }

    private void loginAccount(View v) {
        String email = Objects.requireNonNull(activityLoginBinding.emailEt.getText()).toString();
        String password = Objects.requireNonNull(activityLoginBinding.passwordEt.getText()).toString();

        if (TextUtils.isEmpty(email)) {
            activityLoginBinding.emailEtLayout.setError("Required");
            activityLoginBinding.emailEt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            activityLoginBinding.emailEtLayout.setError("Not a valid email");
            activityLoginBinding.emailEt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            activityLoginBinding.passwordEtLayout.setError("Required");
            activityLoginBinding.passwordEt.requestFocus();
            return;
        }

        if (NetworkUtil.checkNetwork(this)) {
            allowAccount(email, password, v);
        } else {
            ToastUtil.showToast(this, "Connect to the internet first");
        }
    }

    private void allowAccount(String email, String password, View v) {
        alternateProgressBar();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                alternateProgressBar();
                if (task.isSuccessful()) {
                    // login successful
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        ToastUtil.showToast(LoginActivity.this, "Login Successful");
                        startActivity(new Intent(LoginActivity.this, NoteListActivity.class));
                    } else {
                        ToastUtil.showToast(LoginActivity.this, "Login Failed. Please login again");
                    }
                } else {
                    ToastUtil.showToast(LoginActivity.this, task.getException().getLocalizedMessage());
                }
            }
        });
    }

    private void alternateProgressBar() {
        if (!progressBarVisible) {
            activityLoginBinding.progressBarView.setVisibility(View.VISIBLE);
            activityLoginBinding.loginBtn.setVisibility(View.INVISIBLE);
            activityLoginBinding.dontHaveAnAccountTv.setVisibility(View.INVISIBLE);
            activityLoginBinding.createAccountTv.setVisibility(View.INVISIBLE);
            progressBarVisible = true;
        } else {
            activityLoginBinding.progressBarView.setVisibility(View.INVISIBLE);
            activityLoginBinding.loginBtn.setVisibility(View.VISIBLE);
            activityLoginBinding.dontHaveAnAccountTv.setVisibility(View.VISIBLE);
            activityLoginBinding.createAccountTv.setVisibility(View.VISIBLE);
            progressBarVisible = false;
        }
    }
}