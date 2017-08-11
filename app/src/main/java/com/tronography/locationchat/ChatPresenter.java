

package com.tronography.locationchat;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;


public class ChatPresenter implements ChatContract.UserActionListener {

  private ChatContract.View view;

  public ChatPresenter(@NonNull ChatContract.View view) {
    this.view = view;
  }

  @Override
  public void send() {
    view.sendMessage();
  }

  @Override
  public void childAdded(DataSnapshot dataSnapshot, String s) {
    view.fireBaseOnChildAdded(dataSnapshot, s);
  }

  @Override
  public void childChanged(DataSnapshot dataSnapshot, String s){
    view.fireBaseOnChildChanged();
  }
}
