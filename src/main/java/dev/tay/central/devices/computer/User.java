package dev.tay.central.devices.computer;

import dev.tay.central.utils.PasswordHashing;
import org.mindrot.bcrypt.BCrypt;

public class User {

    private long id;
    private String username;
    private String hashedPassword;
    // Not public, but... I can't think of another name.
    // Users are given two passwords; one they use to login with, and one public one that can be exposed.
    // Change the public one every time they change the real one.
    private String publicPassword;

    private User() {
    }

    public User(long id, String username, String hashedPassword) {
        this(id, username, hashedPassword, PasswordHashing.generatePass());
    }

    public User(long id, String username, String hashedPassword, String publicPassword) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.publicPassword = publicPassword;
    }

    public long getID() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean auth(String password) {
        return BCrypt.checkpw(password, hashedPassword) || password.equals(hashedPassword);
    }

}
