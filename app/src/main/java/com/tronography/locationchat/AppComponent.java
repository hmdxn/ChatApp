package com.tronography.locationchat;

import com.tronography.locationchat.chatroom.ChatroomActivity;
import com.tronography.locationchat.lobby.LobbyActivity;
import com.tronography.locationchat.userprofile.UserProfileActivity;
import com.tronography.locationchat.utils.LocationTracker;
import com.tronography.locationchat.utils.UtilsModule;

import javax.inject.Singleton;

import dagger.Component;


@Singleton
@Component(modules = {AppModule.class, SharedPrefModule.class, UtilsModule.class})
public interface AppComponent {

    void inject(ChatroomActivity target);

    void inject(UserProfileActivity target);

    void inject(LobbyActivity target);

    void inject(LocationTracker target);
}
