package com.tronography.locationchat.lobby;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tronography.locationchat.ChatApplication;
import com.tronography.locationchat.LocationCheck;
import com.tronography.locationchat.R;
import com.tronography.locationchat.chatroom.ChatActivity;
import com.tronography.locationchat.firebase.eventlisteners.ChatroomEventListener;
import com.tronography.locationchat.login.LoginActivity;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tronography.locationchat.LocationCheck.LocationCallback;


public class LobbyActivity extends AppCompatActivity implements Lobby.View,
        LocationCallback {

    private static final String TAG = LobbyActivity.class.getSimpleName();

    @Bind(R.id.chatroom_card)
    CardView chatroomCard;
    @Bind(R.id.chatroom_tv)
    TextView chatroomTv;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Inject
    LobbyPresenter presenter;
    private LocationCheck mLocationCheck;

    public static Intent provideIntent(Context context) {
        return new Intent(context, LobbyActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.bind(this);
        ((ChatApplication) getApplicationContext()).getAppComponent().inject(this);

        presenter.setView(this);
        presenter.verifyUserAuth();
        attachFirebaseEventListener();
        mLocationCheck = new LocationCheck(this, this);
    }

    private void attachFirebaseEventListener() {
        ChatroomEventListener chatroomEventListener = new ChatroomEventListener();
        chatroomEventListener.addChatroomEventListener(presenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationCheck.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationCheck.disconnect();
    }

    @Override
    public void launchLoginActivity() {
        Intent intent = LoginActivity.provideIntent(this);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.chatroom_card)
    public void onChatroomClicked(){
        presenter.chatRoomClicked(chatroomTv.getText().toString());
    }

    @Override
    public void launchChatroomActivity(String roomName) {
        Intent intent = ChatActivity.provideIntent(this, roomName);
        startActivity(intent);
    }

    @Override
    public void showChatroom(String chatroomName) {
        progressBar.setVisibility(View.GONE);
        chatroomCard.setVisibility(View.VISIBLE);
        chatroomTv.setText(chatroomName);
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.e(TAG, "handleNewLocation: CALLED");
        Log.e(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        Geocoder geocoder = new Geocoder(this);

        try {
            List<Address> fromLocation = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
            Log.e(TAG, "handleNewLocation: " + fromLocation.get(0).toString());
            String postalCode = fromLocation.get(0).getPostalCode();
            presenter.retrieveChatroom(postalCode);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "handleNewLocation: " + e);
        }
    }
}
