package dev.tay.central.utils;

import java.util.concurrent.ThreadLocalRandom;

public class NetworkUtils {

    public static String generateIP() {
        StringBuilder sb = new StringBuilder("10.");
        sb.append(ThreadLocalRandom.current().nextInt(0, 256))
            .append(".").append(ThreadLocalRandom.current().nextInt(0, 256))
            .append(".").append(ThreadLocalRandom.current().nextInt(2, 255));
        // TODO: Implement a check for already existing IP addresses.
        return sb.toString();
    }

}
