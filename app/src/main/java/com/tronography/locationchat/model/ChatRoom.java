package com.tronography.locationchat.model;

import java.util.UUID;

/**
 * Created by jonathancolon on 8/23/17.
 */

public class Chatroom {

    private String id;
    private String name;
    private int activeUsers;

    public Chatroom() {
    }

    public Chatroom(String name) {
        this.name = name;
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", activeUsers=" + activeUsers +
                '}';
    }
}
