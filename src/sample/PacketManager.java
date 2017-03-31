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


    /**
     * All protocols used
     */
    private static int ICMP = 1;
    private static int TCP = 6;
    private static int UDP = 17;
    private static int ICMP_IPV6 = 58;


    /**
     * Default Constructor
     */
    PacketManager() {
        newCapture();
        clearStats();
    }

    /**
     * Parse protocol type to correct formatting function
     * @param packet
     * @return
     */
    static String formatPacketInfo(Packet packet) {
        String t;

        if (packet instanceof IPPacket) {
            if (((IPPacket) packet).protocol == ICMP_IPV6) {
                t = formatPacketICMP((ICMPPacket) packet);
                return t;
            } else if (((IPPacket) packet).protocol == ICMP) {
                t = formatPacketICMP((ICMPPacket) packet);
                return t;
            } else if (((IPPacket) packet).protocol == TCP) {
                t = formatPacketTCP((TCPPacket) packet);
                return t;
            } else if (((IPPacket) packet).protocol == UDP) {
                t = formatPacketUDP((UDPPacket) packet);
                return t;
            }
        } else if (packet instanceof ARPPacket) {
            t = formatPacketARP((ARPPacket) packet);
            return t;
        }

        return String.valueOf(packet);
    }

    /**
     * Creates a new capture --> old capture is not saved
     */
    static void newCapture() {
        currentCapture = new ArrayList<>();
    }

    void saveCapture() {

    }

    /**
     *
     * @param packet
     */
    static void addPacket(Packet packet) {
        currentCapture.add(packet);
        if (packet != null) countPacket(packet);
    }

    /**
     * Deletes the current capture from memory
     */
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

    /**
     * Resets displayed statistics
     */
    static void clearStats() {
        totalCaptured = 0;
        totalTCP = 0;
        totalUDP = 0;
        totalICMP = 0;
        totalARP = 0;
        totalIP = 0;
    }

    /**
     *
     * @param packet
     */
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

    /**
     * Returns the total number of Packets captured
     * @return
     */
    static int getTotalCaptured() {
        return totalCaptured;
    }

    /**
     * Returns the total number of TCP Packets captured
     * @return
     */
    static int getTotalTCP() { return totalTCP; }

    /**
     * Returns the total number of UDP Packets captured
     * @return
     */
    static int getTotalUDP () {
        return totalUDP;
    }

    /**
     * Returns the current number of ICMP Packets captured
     * @return
     */
    static int getTotalICMP() {
        return totalICMP;
    }

    /**
     * Returns the current number of ARP Packets captured
     * @return
     */
    static int getTotalARP() {
        return totalARP;
    }

    /**
     * Returns the current number of IP Packets captured
     * @return
     */
    static int getTotalIP() {
        return totalIP;
    }

    /**
     * Format displayed output for ARP Packet
     * @param packet
     * @return
     */
    static String formatPacketARP(ARPPacket packet) {
        StringBuilder f = new StringBuilder(1024);

        // Line 1 - Packet
        f.append("\n____ARP Packet____");

        // Line 2 - IP


        return f.toString();
    }

    /**
     * Format displayed output for an ICMP Packet
     * IPv4 and IPv6 with be displayed
     * @param packet
     * @return
     */
    static String formatPacketICMP(ICMPPacket packet) {
        StringBuilder f = new StringBuilder(1024);

        // Line 1 - Packet
        f.append("\n____ICMP PACKET____");

        // Line 2 - IP
        f.append("\nSource IP: ");
        f.append(packet.src_ip.getHostAddress());
        f.append(" --> Destination IP: ");
        f.append(packet.dst_ip.getHostAddress());

        // Line 3 - Info
        f.append("\nType: ").append(codeMessageICMP(packet.code));
        f.append("\nSequence Number: ").append(packet.seq);

        return f.toString();
    }

    /**
     * Format displayed output for an IP Packet
     * @param packet
     * @return
     */
    static String formatPacketUDP(UDPPacket packet) {
        StringBuilder f = new StringBuilder(1024);

        f.append("\n____UDP Packet____");

        return f.toString();
    }

    /**
     * Format displayed output for a TCP Packet
     * @param packet
     * @return
     */
    static String formatPacketTCP(TCPPacket packet) {
        StringBuilder f = new StringBuilder(1024);
        int temp = getTotalCaptured();

        // Line 1 - Packet
        f.append("\n____TCP PACKET____");

        // Line 2 - IP
        f.append("\nSource IP: ");
        f.append(packet.src_ip.getHostAddress());
        f.append(" --> Destination IP: ");
        f.append(packet.dst_ip.getHostAddress());

        // Line 3 - Ports
        f.append("\nSource Port: ").append(packet.src_port);
        f.append(" --> Destination Port: ").append(packet.dst_port);

        // Line 4 - Flags
        f.append("\n-- Flags: ");
        if (packet.ack) { f.append("ACK "); }
        if (packet.fin) { f.append("FIN "); }
        if (packet.psh) { f.append("PSH "); }
        if (packet.rst) { f.append("RST "); }
        if (packet.syn) { f.append("SYN "); }
        if (packet.urg) { f.append("URG "); }

        // Line 5 - Payload
        //f.append("\n-- Payload\n").append(new String(packet.data));

        return f.toString();
    }

    /**
     * Helper function to show ICMP Type
     * @param m
     * @return
     */
    static String codeMessageICMP(byte m) {
        String r;

        switch (m){
            case 0:
                r = "Echo Reply";
                break;
            case 3:
                r = "Destination Unreachable";
                break;
            case 5:
                r = "Redirect";
                break;
            case 8:
                r = "Echo";
                break;
            case 9:
                r = "Router Advertisement";
                break;
            case 10:
                r = "Router Selection";
                break;
            case 11:
                r = "Time Exceeded";
                break;
            case 12:
                r = "Parameter Problem";
                break;
            case 13:
                r = "Timestamp";
                break;
            case 14:
                r = "Timestamp Reply";
                break;
            default:
                r = "Unknown Type";
        }

        return r;
    }
}