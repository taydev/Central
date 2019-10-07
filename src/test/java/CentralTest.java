import dev.tay.central.devices.Device;
import dev.tay.central.devices.computer.Computer;
import dev.tay.central.networking.Network;

public class CentralTest {

    private Network network;

    public static void main(String[] args) {
        CentralTest instance = new CentralTest();
        instance.testInitialisation();
        instance.testDeviceCreation();
    }

    private void log(String string, Object... args) {
        System.out.println(String.format(string, args));
    }

    private void testInitialisation() {
        network = new Network("10.0.0.1", "MAINFRAME", null);
        log("Created new Network \"%s\", IP %s, %d devices connected.",
            network.getDisplayName(),
            network.getMainframeIPAddress(),
            network.getDevices().size());
    }

    private void testDeviceCreation() {
        // oh hey it's my discord ID coolcool
        Computer computer = new Computer(277385484919898112L);
        computer.setNetwork(network, true);
        log("Created new Computer %s, with IP %s connected to network %s.",
            computer.getDisplayName(),
            computer.getIPAddress(),
            computer.getNetwork().getMainframeIPAddress());

        Device device = new Device(277385484919898113L);
        device.setNetwork(network, true);
        log("Created new Generic Device %s, with IP %s connected to network %s.",
            device.getDisplayName(),
            device.getIPAddress(),
            device.getNetwork().getMainframeIPAddress());

        testNetworking(computer.getIPAddress(), device.getIPAddress());
    }

    private void testNetworking(String ipA, String ipB) {
        Computer computer = (Computer) network.getDeviceByIP(ipA);
        Device device = network.getDeviceByIP(ipB);

        log("Can %s ping %s? %b",
            computer.getDisplayName(),
            device.getDisplayName(),
            computer.ping(device.getIPAddress()));
        log("Can %s ping %s? %b",
            device.getDisplayName(),
            computer.getDisplayName(),
            device.ping(computer.getIPAddress()));
        log("Can %s ping the mainframe? %b",
            computer.getDisplayName(),
            computer.ping(network.getMainframeIPAddress()));
        log("Can %s ping the mainframe? %b",
            device.getDisplayName(),
            device.ping(network.getMainframeIPAddress()));
    }
}
