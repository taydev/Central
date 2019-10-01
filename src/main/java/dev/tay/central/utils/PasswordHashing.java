package dev.tay.central.utils;

import org.mindrot.bcrypt.BCrypt;

import java.util.concurrent.ThreadLocalRandom;

public class PasswordHashing {
    // Yes, this is being changed when prod happens. I'm not that dumb.
    private final static String SALT = "$2a$08$iFUeIJCiTjB9AgLmn8XA6.";

    public static String encrypt(String raw) {
        return BCrypt.hashpw(raw, SALT);
    }

    public static String generatePass() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            res.append(chars[ThreadLocalRandom.current().nextInt(0, chars.length)]);
        }
        return res.toString();
    }

}
