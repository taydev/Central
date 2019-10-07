package dev.tay.central.devices;

import dev.tay.central.networking.Network;
import dev.tay.central.utils.NetworkUtils;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class Device {

    private long ownerId;
    private String ipAddress;
    private String displayName;

    private Network network;

    public Device(long ownerId) {
        this(ownerId, null, null, null);
    }

    public Device(long ownerId, String ipAddress, String displayName) {
        this(ownerId, ipAddress, displayName, null);
    }

    public Device(long ownerId, String ipAddress, String displayName, Network network) {
        this.ownerId = ownerId;
        if (ipAddress == null)
            this.ipAddress = NetworkUtils.generateIP();
        else
            this.ipAddress = ipAddress;

        if (displayName == null)
            this.displayName = "DEFAULT-NODE-" + new DecimalFormat("000")
                .format(ThreadLocalRandom.current().nextInt(0, 1000));
        else
            this.displayName = displayName;

        this.network = network;
    }

    public long getOwnerID() {
        return this.ownerId;
    }

    public String getIPAddress() {
        return this.ipAddress;
    }

    public void setIPAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Network getNetwork() {
        return this.network;
    }

    public void setNetwork(Network network, boolean sync) {
        this.network = network;

        if (sync)
            this.network.addDevice(this, false);
    }

    public boolean ping(String ipAddress) {
        // TODO: Implement the rest of the (possible) networks. Probably needs some sort of index system.
        // Only checks localhost at the moment.
        return getNetwork() != null && (getNetwork().getDeviceByIP(ipAddress) != null || getNetwork().getMainframeIPAddress().equals(ipAddress));
    }

}
