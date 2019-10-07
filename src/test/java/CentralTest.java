import dev.tay.central.devices.Device;
import dev.tay.central.devices.computer.Computer;
import dev.tay.central.filesystem.FileSystemElement;
import dev.tay.central.networking.Network;

import java.util.Collections;

public class CentralTest {

    private Network network;

    public static void main(String[] args) {
        CentralTest instance = new CentralTest();
        instance.testInitialisation();
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

        testDeviceCreation();
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
        // I just wanted to make sure this works.
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

        testFilesystems(computer);
    }

    private void testFilesystems(Computer computer) {
        log("Testing filesystem...");
        exploreFilesystem(computer.getRootDirectory(), 0);
        testFileCreation(computer);
        log("Exploring updated filesystem...");
        exploreFilesystem(computer.getRootDirectory(), 0);
        testPathfinding(computer);
        testFileDeletion(computer);
    }

    private void testFileCreation(Computer computer) {
        log("Creating directory \"/test/\"...");
        computer.getRootDirectory().createBlankDirectory("test");
        log("Creating file \"/test/test.txt\"...");
        computer.getRootDirectory().getDirectory("test").createFile("test.txt", "");
        log("\"/test/test.txt\" contents: %s",
            computer.getRootDirectory().getDirectory("test").getFile("test.txt").getFileContents());
        log("Setting contents of \"/test/test.txt\" to \"test\"...");
        computer.getRootDirectory().getDirectory("test").getFile("test.txt").setContents("test");
        log("New contents of \"/test/test.txt\": %s",
            computer.getRootDirectory().getDirectory("test").getFile("test.txt").getFileContents());
    }

    private void testPathfinding(Computer computer) {
        log("File contents at path \"test/test.txt\": %s",
            computer.getRootDirectory().getElementByRelativePath("test/test.txt").getFileContents());
        log("File contents at path \"../sys/os.bin\" from \"test/\": %s",
            computer.getRootDirectory().getDirectory("test").getElementByRelativePath("../sys/os.bin").getFileContents());
        try {
            log("File contents at invalid path \"../asdf/asdf.bin\": %s",
                computer.getRootDirectory().getElementByRelativePath("../asdf/asdf.bin").getFileContents());
        } catch (NullPointerException ignored) {
            log("Caught invalid path at \"../asdf/asdf.bin\". Of course, this will never happen in actual code. We " +
                "check our nulls like good boys.");
        }
    }

    private void testFileDeletion(Computer computer) {
        log("Deleting file \"/test/test.txt\"...");
        computer.getRootDirectory().getDirectory("test").getFile("test.txt").delete();
        log("New dir listing: ");
        exploreFilesystem(computer.getRootDirectory(), 0);
        log("Deleting folder \"/test/\"...");
        computer.getRootDirectory().getDirectory("test").delete();
        log("New dir listing: ");
        exploreFilesystem(computer.getRootDirectory(), 0);
    }

    private void exploreFilesystem(FileSystemElement currentElement, int indent) {
        log("%s- %s", String.join("", Collections.nCopies(indent, " ")), currentElement.getName());
        if (currentElement.isDirectory()) {
            for (FileSystemElement element : currentElement.getDirContents()) {
                exploreFilesystem(element, indent + 2);
            }
        }
    }
}
