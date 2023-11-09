package com.example.noteswithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.noteswithfirebase.databinding.ActivityNoteListBinding;
import com.example.noteswithfirebase.util.FirebaseUtil;
import com.example.noteswithfirebase.util.IntentUtil;
import com.example.noteswithfirebase.util.NetworkUtil;
import com.example.noteswithfirebase.util.ToastUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class NoteListActivity extends AppCompatActivity {

    private ActivityNoteListBinding activityNoteListBinding;
    private NoteListAdapter noteListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNoteListBinding = ActivityNoteListBinding.inflate(getLayoutInflater());
        setContentView(activityNoteListBinding.getRoot());
        setRv();
        activityNoteListBinding.addNoteBtn.setOnClickListener(this::createNewNote);
        activityNoteListBinding.menuImg.setOnClickListener(this::showPopup);

        activityNoteListBinding.noteListRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    activityNoteListBinding.addNoteBtn.shrink();
                } else {
                    activityNoteListBinding.addNoteBtn.extend();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteListAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteListAdapter.notifyDataSetChanged();
    }

    private void setRv() {
        Query query = FirebaseUtil.getCollectionReference().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();
        activityNoteListBinding.noteListRv.setHasFixedSize(true);
        activityNoteListBinding.noteListRv.setLayoutManager(new LinearLayoutManager(this));
        noteListAdapter = new NoteListAdapter(firestoreRecyclerOptions, this);
        activityNoteListBinding.noteListRv.setAdapter(noteListAdapter);
    }

    private void createNewNote(View v) {
        startActivity(
                new Intent(this, CreateOrUpdateNoteActivity.class)
        );
    }

    private void showPopup(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (Objects.equals(item.getTitle(), "Logout")) {
                    if (NetworkUtil.checkNetwork(NoteListActivity.this)) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(NoteListActivity.this, LoginActivity.class));
                        finish();
                        return true;
                    } else {
                        ToastUtil.showToast(NoteListActivity.this, "Internet connection missing");
                        return true;
                    }
                }
                return false;
            }
        });
    }
}