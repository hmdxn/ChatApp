package com.tronography.locationchat.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by jonathancolon on 8/26/17.
 */

public class FirebaseDatabaseReference {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference root = database.getReference();
    private static DatabaseReference chatRoomReference = database.getReference("chatrooms");
    private static DatabaseReference messageReference = database.getReference("messages");
    private static DatabaseReference userReference = database.getReference("members");

    public static FirebaseDatabase getDatabase() {
        return database;
    }

    public static DatabaseReference getRoot() {
        return root;
    }

    public static DatabaseReference getChatRoomReference() {
        return chatRoomReference;
    }

    public static DatabaseReference getMessageReference() {
        return messageReference;
    }

    public static DatabaseReference getUserReference() {
        return userReference;
    }
}
