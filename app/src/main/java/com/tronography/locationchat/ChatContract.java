package com.tronography.locationchat;

import com.google.firebase.database.DataSnapshot;


public class ChatContract {

  public interface View {

    void sendMessage();

    void fireBaseOnChildAdded(DataSnapshot dataSnapshot, String s);

    void fireBaseOnChildChanged();
  }

  public interface UserActionListener {

    void send();

    void childAdded(DataSnapshot dataSnapshot, String s);

    void childChanged(DataSnapshot dataSnapshot, String s);
  }
}
