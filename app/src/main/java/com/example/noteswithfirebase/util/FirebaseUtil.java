package com.example.noteswithfirebase.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static CollectionReference getCollectionReference() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return FirebaseFirestore.getInstance()
                .collection("notes")
                .document(currentUser.getUid())
                .collection("personal_note");

    }
}
