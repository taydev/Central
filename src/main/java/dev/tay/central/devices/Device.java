package dev.tay.central.devices;

import dev.tay.central.utils.NetworkUtils;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Device {

    private long ownerId;
    private String ipAddress;
    private String displayName;

    public Device(long ownerId) {
        this(ownerId, "", "");
    }

    public Device(long ownerId, String ipAddress, String displayName) {
        this.ownerId = ownerId;
        if (ipAddress.equals(""))
            this.ipAddress = NetworkUtils.generateIP();
        else
            this.ipAddress = ipAddress;

        if (displayName.equals(""))
            this.displayName = "DEFAULT-NODE-" + new DecimalFormat("000")
                .format(ThreadLocalRandom.current().nextInt(0, 1000));
        else
            this.displayName = displayName;
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

}
