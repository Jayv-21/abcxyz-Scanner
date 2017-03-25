package sample;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.*;

import java.io.IOException;
import java.util.ArrayList;
/**
 * Created by gregorypontejos on 3/12/17.
 */
public class PacketManager {

    private static ArrayList<Packet> currentCapture;

    private static int totalCaptured;
    private static int totalTCP;
    private static int totalUDP;
    private static int totalICMP;
    private static int totalARP;
    private static int totalIP;


    PacketManager() {
        newCapture();
        clearStats();
    }

    static String formatPacketInfo(Packet packet) {
        return String.valueOf(packet);
    }


    static void newCapture() {
        currentCapture = new ArrayList<>();
    }

    void saveCapture() {

    }

    static void addPacket(Packet packet) {
        currentCapture.add(packet);
        if (packet != null) countPacket(packet);
    }

    static void clearCapture() {
        currentCapture = null;

        System.out.printf("Total captured: %s\n", totalCaptured);
        System.out.printf("Total TCP: %s\n", totalTCP);
        System.out.printf("Total UDP: %s\n", totalUDP);
        System.out.printf("Total ICMP: %s\n", totalICMP);
        System.out.printf("Total ARP: %s\n", totalARP);
        System.out.printf("Total IP: %s\n", totalIP);

        clearStats();
    }

    static void clearStats() {
        totalCaptured = 0;
        totalTCP = 0;
        totalUDP = 0;
        totalICMP = 0;
        totalARP = 0;
        totalIP = 0;
    }

    static void countPacket(Packet packet) {
        if (packet instanceof TCPPacket) {
            totalTCP++;
        } else if (packet instanceof UDPPacket) {
            totalUDP++;
        } else if (packet instanceof ICMPPacket) {
            totalICMP++;
        } else if (packet instanceof ARPPacket) {
            totalARP++;
        } else if (packet instanceof IPPacket) {
            totalIP++;
        }

        totalCaptured++;
    }

    static int getTotalCaptured() {
        return totalCaptured;
    }

    static int getTotalTCP() {
        return totalTCP;
    }

    static int getTotalUDP () {
        return totalUDP;
    }

    static int getTotalICMP() {
        return totalICMP;
    }

    static int getTotalARP() {
        return totalARP;
    }

    static int getTotalIP() {
        return totalIP;
    }
}
