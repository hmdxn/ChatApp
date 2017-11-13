package com.tronography.locationchat.lobby;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tronography.locationchat.ChatApplication;
import com.tronography.locationchat.R;
import com.tronography.locationchat.chatroom.ChatActivity;
import com.tronography.locationchat.firebase.eventlisteners.ChatroomEventListener;
import com.tronography.locationchat.login.LoginActivity;
import com.tronography.locationchat.utils.AppUtils;
import com.tronography.locationchat.utils.LocationHelper;
import com.tronography.locationchat.utils.LocationTracker;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;


public class LobbyActivity extends AppCompatActivity implements Lobby.View, LocationTracker.onLocationListener {

    private static final String TAG = LobbyActivity.class.getSimpleName();

    @BindView(R.id.chatroom_card)
    CardView chatroomCard;
    @BindView(R.id.chatroom_tv)
    TextView chatroomTv;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @Inject
    LobbyPresenter presenter;
    @Inject
    AppUtils mAppUtils;
    @Inject
    LocationTracker locationTracker;
    @Inject
    LocationHelper locationHelper;

    public static Intent provideIntent(Context context) {
        return new Intent(context, LobbyActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ButterKnife.bind(this);
        ((ChatApplication) getApplicationContext()).getAppComponent().inject(this);

        locationTracker.setLocationListener(this);
        presenter.setView(this);
        presenter.verifyUserAuth();
        attachFirebaseEventListener();
    }

    private void attachFirebaseEventListener() {
        ChatroomEventListener chatroomEventListener = new ChatroomEventListener();
        chatroomEventListener.addChatroomEventListener(presenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationTracker.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationTracker.disconnect();
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
    public void onConnected(Location location) {
        String postalCode = locationHelper.getPostalCode(location.getLatitude(),
                location.getLongitude());

        presenter.retrieveChatroom(postalCode);
    }
}
