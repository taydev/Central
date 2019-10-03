package dev.tay.central.devices.computer;

import dev.tay.central.devices.Device;
import dev.tay.central.filesystem.FileSystemElement;
import dev.tay.central.networking.Network;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Computer extends Device {

    private List<User> users;
    private FileSystemElement rootDirectory;

    private Network network;

    public Computer(long ownerId) {
        this(ownerId, null, null, null, null, null);
    }

    public Computer(long ownerId, String ipAddress, String displayName, List<User> users,
                    FileSystemElement rootDirectory) {
        this(ownerId, ipAddress, displayName, users, rootDirectory, null);
    }

    public Computer(long ownerId, String ipAddress, String displayName, List<User> users,
                    FileSystemElement rootDirectory, Network network) {
        super(ownerId, ipAddress, displayName);

        if (users == null)
            this.users = new CopyOnWriteArrayList<>();
        else
            this.users = users;

        if (rootDirectory == null)
            this.rootDirectory = FileSystemElement.generateRootDirectory(this);
        else
            this.rootDirectory = rootDirectory;

        // Technically speaking, it's okay if network is null. That just means it's offline... right?
        this.network = network;
    }

    public List<User> getUsers() {
        return this.users;
    }

    public boolean addUser(User u) {
        if (this.getUsers().stream().anyMatch(user -> user.getUsername().equals(u.getUsername())))
            return false;

        this.getUsers().add(u);
        return true;
    }

    public void removeUser(User u) {
        this.getUsers().remove(u);
    }

    public void removeUser(String username) {
        this.getUsers().removeIf(user -> user.getUsername().equals(username));
    }

    public FileSystemElement getRootDirectory() {
        return this.rootDirectory;
    }

    public Network getNetwork() {
        return this.network;
    }

    public boolean ping(String ipAddress) {
        // TODO: Implement the rest of the (possible) networks. Probably needs some sort of index system.
        // Only checks localhost at the moment.
        Device device = getNetwork().getDeviceByIP(ipAddress);
        return getNetwork().getDeviceByIP(ipAddress) != null;
    }

}
