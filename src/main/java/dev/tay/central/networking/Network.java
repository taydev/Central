package dev.tay.central.networking;

import dev.tay.central.devices.Device;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Network {

    // Dunno what to do with this yet... do we even need this?
    private int id;

    // Might be an idea to make some sort of subnet mask formatting thingy thing
    // so we can do fancy stuff with IP generation for networks
    // although that might be useless
    private String mainframeIpAddress;
    private String displayName;

    private List<Device> devices;

    public Network(String mainframeIpAddress, String displayName, List<Device> devices) {
        this.mainframeIpAddress = mainframeIpAddress;
        if (displayName.equals(""))
            this.displayName = "MAINFRAME-NODE-001";
        else
            this.displayName = displayName;

        if (devices == null)
            this.devices = new CopyOnWriteArrayList<>();
        else
            this.devices = devices;
    }

    public int getID() {
        return this.id;
    }

    public String getMainframeIPAddress() {
        return this.mainframeIpAddress;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<Device> getDevices() {
        return this.devices;
    }

    public Device getDeviceByIP(String ipAddress) {
        return this.devices.stream().filter(device -> device.getIPAddress().equals(ipAddress)).findFirst().orElse(null);
    }

    public Device getDeviceByDisplayName(String displayName) {
        return this.devices.stream().filter(device -> device.getDisplayName().equals(displayName)).findFirst().orElse(null);
    }

    public void addDevice(Device device, boolean sync) {
        this.devices.add(device);
        if (sync)
            device.setNetwork(this, false);
    }

}
