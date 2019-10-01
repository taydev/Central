package dev.tay.central.computer;

import dev.tay.central.filesystem.FileSystemElement;
import dev.tay.central.utils.NetworkUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Computer {

    private long id;
    private String ipAddress;
    private String displayName;
    private List<User> users;

    private FileSystemElement rootDirectory;

    public Computer(long id) {
        this(id, null, null, null, null);
    }

    public Computer(long id, String ipAddress, String displayName, List<User> users, FileSystemElement rootDirectory) {
        this.id = id;
        if (ipAddress == null)
            this.ipAddress = NetworkUtils.generateIP();
        else
            this.ipAddress = ipAddress;

        if (displayName == null)
            this.displayName = "DEFAULT-NODE-" + new DecimalFormat("000")
                .format(ThreadLocalRandom.current().nextInt(0, 1000));
        else
            this.displayName = displayName;

        if (users == null)
            this.users = new CopyOnWriteArrayList<>();
        else
            this.users = users;

        if (rootDirectory == null)
            this.rootDirectory = FileSystemElement.generateRootDirectory(this);
        else
            this.rootDirectory = rootDirectory;
    }

    public long getID() {
        return this.id;
    }

    public String getIPAddress() {
        return this.ipAddress;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public FileSystemElement getRootDirectory() {
        return this.rootDirectory;
    }

}
