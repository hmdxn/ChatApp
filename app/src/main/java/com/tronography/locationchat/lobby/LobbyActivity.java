package com.tronography.locationchat.lobby;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tronography.locationchat.ChatApplication;
import com.tronography.locationchat.R;
import com.tronography.locationchat.chatroom.ChatroomActivity;
import com.tronography.locationchat.common.BaseActivity;
import com.tronography.locationchat.firebase.eventlisteners.ChatroomEventListener;
import com.tronography.locationchat.login.LoginActivity;
import com.tronography.locationchat.utils.AppUtils;
import com.tronography.locationchat.utils.LocationUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;
import pl.charmas.android.reactivelocation2.ReactiveLocationProviderConfiguration;


public class LobbyActivity extends BaseActivity implements Lobby.View {

    private static final String TAG = LobbyActivity.class.getSimpleName();

    @BindView(R.id.chatroom_card)
    CardView chatroomCard;
    @BindView(R.id.chatroom_tv)
    TextView chatroomTv;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.current_location_tv)
    TextView currentLocationTv;
    @BindView(R.id.public_chat_title_tv)
    TextView publicChatTitleTv;
    @Inject
    LobbyPresenter presenter;

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
    }

    private void attachFirebaseEventListener() {
        ChatroomEventListener chatroomEventListener = new ChatroomEventListener();
        chatroomEventListener.addChatroomEventListener(presenter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void launchLoginActivity() {
        Intent intent = LoginActivity.provideIntent(this);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.chatroom_card)
    public void onChatroomClicked() {
        Log.e(TAG, "onChatroomClicked: "  );
        presenter.enterChatroom(chatroomTv.getText().toString());
    }

    @Override
    public void launchChatroomActivity(String roomName) {
        Intent intent = ChatroomActivity.provideIntent(this, roomName);
        startActivity(intent);
    }

    @Override
    public void showChatroomDetails(String chatroomName) {
        progressBar.setVisibility(View.GONE);
        chatroomCard.setVisibility(View.VISIBLE);
        chatroomTv.setText(chatroomName);
        currentLocationTv.setVisibility(View.VISIBLE);
        publicChatTitleTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void showCurrentLocationDetails(String location) {
        currentLocationTv.setText(location);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onPermissionsGranted() {

        Log.e(TAG, "isGpsEnabled: " + AppUtils.isGpsEnabled(this));

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(
                getApplicationContext(),
                ReactiveLocationProviderConfiguration
                        .builder()
                        .setRetryOnConnectionSuspended(true)
                        .build()
        );

        locationProvider.getLastKnownLocation()
                .subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(Location location) {
                        Log.e(TAG, "getLastKnownLocation: " + location);
                        String postalCode = LocationUtils.getPostalCode(
                                LobbyActivity.this,
                                location.getLatitude(),
                                location.getLongitude());

                        Log.e(TAG, "getLastKnownLocation: " + postalCode );

                        presenter.retrieveChatroom(postalCode);
                        Toast.makeText(
                                LobbyActivity.this,
                                postalCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
