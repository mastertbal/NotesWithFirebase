package com.example.noteswithfirebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteswithfirebase.util.FirebaseUtil;
import com.example.noteswithfirebase.util.IntentUtil;
import com.example.noteswithfirebase.util.ToastUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

public class NoteListAdapter extends FirestoreRecyclerAdapter<Note, NoteListAdapter.NoteViewHolder> {

    Context context;
    FirestoreRecyclerOptions<Note> firestoreRecyclerOptions;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public NoteListAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
        this.firestoreRecyclerOptions = options;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.note_single_item, parent, false)
        );
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.title.setText(note.getTitle());
        holder.timeStamp.setText(note.fromTimeStampToString(note.getTimestamp()));
        holder.text.setText(note.getText());
        holder.popup_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.inflate(R.menu.recycler_view_popup);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Delete")) {
                            deleteNote(getSnapshots().getSnapshot(position).getId());
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateOrUpdateNoteActivity.class);
                intent.putExtra(IntentUtil.NOTE_KEY, IntentUtil.OLD_NOTE)
                        .putExtra(IntentUtil.TITLE_KEY, note.getTitle())
                        .putExtra(IntentUtil.TEXT_KEY, note.getText());
                String documentId = getSnapshots().getSnapshot(position).getId();
                intent.putExtra(IntentUtil.DOC_ID_KEY, documentId);
                context.startActivity(intent);
            }
        });

    }

    private void deleteNote(String noteId) {
        DocumentReference documentReference = FirebaseUtil.getCollectionReference().document(noteId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ToastUtil.showToast(context, "Note deleted");
                } else {
                    ToastUtil.showToast(context, "Falied to delete note");
                }
            }
        });
        this.notifyDataSetChanged();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {

        TextView title, timeStamp, text;
        ImageButton popup_icon;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_note_title);
            timeStamp = itemView.findViewById(R.id.item_note_time_stamp);
            text = itemView.findViewById(R.id.item_note_text);
            popup_icon = itemView.findViewById(R.id.popup_icon);
        }
    }
}
