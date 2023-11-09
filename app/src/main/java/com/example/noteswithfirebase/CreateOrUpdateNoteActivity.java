package com.example.noteswithfirebase;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.noteswithfirebase.databinding.ActivityCreateOrUpdateNoteBinding;
import com.example.noteswithfirebase.util.FirebaseUtil;
import com.example.noteswithfirebase.util.IntentUtil;
import com.example.noteswithfirebase.util.NetworkUtil;
import com.example.noteswithfirebase.util.ToastUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Objects;

public class CreateOrUpdateNoteActivity extends AppCompatActivity {

    private ActivityCreateOrUpdateNoteBinding activityCreateOrUpdateNoteBinding;
    private String incomingTitle = "", incomingText = "", incomingDocumentId = "", oldNote = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCreateOrUpdateNoteBinding = ActivityCreateOrUpdateNoteBinding.inflate(getLayoutInflater());
        setContentView(activityCreateOrUpdateNoteBinding.getRoot());

        oldNote = getIntent().getStringExtra(IntentUtil.NOTE_KEY);
        if (!TextUtils.isEmpty(oldNote)) {
            activityCreateOrUpdateNoteBinding.pageTitle.setText(R.string.update_note_text);
            incomingTitle = getIntent().getStringExtra(IntentUtil.TITLE_KEY);
            incomingText = getIntent().getStringExtra(IntentUtil.TEXT_KEY);
            incomingDocumentId = getIntent().getStringExtra(IntentUtil.DOC_ID_KEY);
            populateViewsWithIncomingData();
        } else {
            activityCreateOrUpdateNoteBinding.pageTitle.setText(R.string.add_new_note_text);
        }


        activityCreateOrUpdateNoteBinding.saveNote.setOnClickListener(this::saveNote);
        activityCreateOrUpdateNoteBinding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        ToastUtil.showToast(CreateOrUpdateNoteActivity.this, "No note saved or edited");
        super.onBackPressed();
    }

    private void populateViewsWithIncomingData() {
        activityCreateOrUpdateNoteBinding.noteTitle.setText(incomingTitle);
        activityCreateOrUpdateNoteBinding.noteText.setText(incomingText);
    }

    private void saveNote(View v) {
        if (NetworkUtil.checkNetwork(this)) {
            String title = Objects.requireNonNull(activityCreateOrUpdateNoteBinding.noteTitle.getText()).toString();
            String text = Objects.requireNonNull(activityCreateOrUpdateNoteBinding.noteText.getText()).toString();

            if (TextUtils.isEmpty(title)) {
                activityCreateOrUpdateNoteBinding.noteTitle.setError("Note title is required");
                activityCreateOrUpdateNoteBinding.noteTitle.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(text)) {
                activityCreateOrUpdateNoteBinding.noteText.setError("Note text is required");
                activityCreateOrUpdateNoteBinding.noteText.requestFocus();
                return;
            }

            saveToCloud(title, text);
        }else {
            ToastUtil.showToast(this, "Internet connection missing");
        }
    }

    private void saveToCloud(String title, String text) {
        Note note = new Note(title, text, Timestamp.now());
        DocumentReference documentReference;
        if (TextUtils.isEmpty(incomingDocumentId)) {
            documentReference = FirebaseUtil.getCollectionReference().document();
        } else {
            documentReference = FirebaseUtil.getCollectionReference().document(incomingDocumentId);
        }
        documentReference.set(note).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ToastUtil.showToast(CreateOrUpdateNoteActivity.this, "Note Added Successfully");
                    finish();
                } else {
                    String errorMsg = task.getException().getLocalizedMessage();
                    ToastUtil.showToast(CreateOrUpdateNoteActivity.this, "Failed to add note\n" + errorMsg);
                    Log.d("NOTE", errorMsg);
                }
            }
        });
    }
}