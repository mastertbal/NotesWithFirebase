package com.example.noteswithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.noteswithfirebase.databinding.ActivityCreateAccountBinding;
import com.example.noteswithfirebase.util.ToastUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {

    private boolean progressBarVisible = false;

    private ActivityCreateAccountBinding activityCreateAccountBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCreateAccountBinding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(activityCreateAccountBinding.getRoot());

        activityCreateAccountBinding.createAccountBtn.setOnClickListener(this::signUp);
        activityCreateAccountBinding.loginTv.setOnClickListener(this::goToLoginActivity);
    }

    private void signUp(View v) {
        String email = Objects.requireNonNull(activityCreateAccountBinding.emailEt.getText()).toString();
        String password = Objects.requireNonNull(activityCreateAccountBinding.passwordEt.getText()).toString();
        String confirmPassword = Objects.requireNonNull(activityCreateAccountBinding.confirmPasswordEt.getText()).toString();

        if (TextUtils.isEmpty(email)) {
            activityCreateAccountBinding.emailEtLayout.setError("Required");
            activityCreateAccountBinding.emailEt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            activityCreateAccountBinding.emailEtLayout.setError("Not a valid email");
            activityCreateAccountBinding.emailEt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            activityCreateAccountBinding.passwordEtLayout.setError("Required");
            activityCreateAccountBinding.passwordEt.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            activityCreateAccountBinding.confirmPasswordEtLayout.setError("Required");
            activityCreateAccountBinding.passwordEt.requestFocus();
            return;
        }

        if (!TextUtils.equals(password, confirmPassword)) {
            activityCreateAccountBinding.confirmPasswordEtLayout.setError("Password do not match");
            activityCreateAccountBinding.passwordEt.requestFocus();
            return;
        }

        newAccount(email, password, v);

    }

    private void newAccount(String email, String password, View v) {
        alternateProgressBar();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // account created
                            alternateProgressBar();
                            ToastUtil.showToast(CreateAccountActivity.this, "Account created. Check email to verify");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            goToLoginActivity(v);
                        } else {
                            String errorMsg = task.getException().getLocalizedMessage();
                            ToastUtil.showToast(CreateAccountActivity.this, errorMsg);
                        }
                    }
                });

    }

    private void alternateProgressBar() {
        if (!progressBarVisible) {
            activityCreateAccountBinding.progressBarView.setVisibility(View.VISIBLE);
            activityCreateAccountBinding.createAccountBtn.setVisibility(View.INVISIBLE);
            activityCreateAccountBinding.alreadyHaveAnAccountTv.setVisibility(View.INVISIBLE);
            activityCreateAccountBinding.loginTv.setVisibility(View.INVISIBLE);
            progressBarVisible = true;
        } else {
            activityCreateAccountBinding.progressBarView.setVisibility(View.INVISIBLE);
            activityCreateAccountBinding.createAccountBtn.setVisibility(View.VISIBLE);
            activityCreateAccountBinding.alreadyHaveAnAccountTv.setVisibility(View.VISIBLE);
            activityCreateAccountBinding.loginTv.setVisibility(View.VISIBLE);
            progressBarVisible = false;
        }
    }

    private void goToLoginActivity(View v) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}