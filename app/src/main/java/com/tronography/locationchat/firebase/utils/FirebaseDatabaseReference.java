package com.tronography.locationchat.firebase.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.tronography.locationchat.utils.Constants.Firebase.CHATROOMS;
import static com.tronography.locationchat.utils.Constants.Firebase.MEMBERS;
import static com.tronography.locationchat.utils.Constants.Firebase.MESSAGES;


/**
 * Created by jonathancolon on 8/26/17.
 */

public class FirebaseDatabaseReference {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference root = database.getReference();
    private static DatabaseReference chatRoomReference = database.getReference(CHATROOMS);
    private static DatabaseReference messageReference = database.getReference(MESSAGES);
    private static DatabaseReference userReference = database.getReference(MEMBERS);


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

    public static DatabaseReference getChatRoomMessageRef(String roomID) {
        return getMessageReference().child(roomID);
    }
}
