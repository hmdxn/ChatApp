package com.tronography.locationchat.utils;

import java.util.Random;

/**
 * Created by jonathancolon on 8/4/17.
 */

public class UsernameGenerator {

    public String generateTempUsername() {
        Random random = new Random();
        int randomIdentifier = 1000 + random.nextInt(10000);
        return "user-" + randomIdentifier;
    }
}
