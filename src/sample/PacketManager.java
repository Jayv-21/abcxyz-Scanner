package sample;

import jpcap.packet.*;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by gregorypontejos on 3/12/17.
 */
public class PacketManager implements Serializable {

    private static ArrayList<Packet> currentCapture = new ArrayList<>();
    private static ArrayList<Packet> filteredCapture;

    // Stats variables
    private static int totalCaptured;
    private static int totalTCP;
    private static int totalUDP;
    private static int totalICMP;
    private static int totalARP;
    private static int totalIP;

    // Protocol filter variables
    private static boolean filterTCP = false;
    private static boolean filterUDP = false;
    private static boolean filterICMP = false;
    private static boolean filterARP = false;
    private static boolean filterIGMP = false;
    private static InetAddress filterIP;
    private static int filterPort;
    private static boolean filterIsSourceIP;
    private static boolean filterApplied;

    // Protocols used
    private static int ICMP = 1;
    private static int IGMP = 2;
    private static int TCP = 6;
    private static int UDP = 17;
    private static int ICMP_IPV6 = 58;


    /**
     * Default Constructor
     */
    PacketManager() {
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
        if (packet != null) {
            currentCapture.add(packet);
            countPacket(packet);
        }
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

        newCapture();
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
            } else if (((IPPacket) packet).protocol == IGMP) {
                t = formatPacketIGMP((IPPacket)packet);
                return t;
            }
        } else if (packet instanceof ARPPacket) {
            t = formatPacketARP((ARPPacket) packet);
            return t;
        }

        return String.valueOf(packet);
    }

    /**
     * Format displayed output for ARP Packet
     * @param packet
     * @return
     */
    static String formatPacketARP(ARPPacket packet) {
        StringBuilder f = new StringBuilder(1024);

        // Line 1 - Packet
        f.append("Packet Type: ARP");

        // Line 2 - Hardware
        f.append("\nSender Hardware Address: ").append(packet.getSenderHardwareAddress());
        f.append("\nSender Protocol Address: ").append(packet.getSenderProtocolAddress());
        f.append("\nTarget Hardware Address: ").append(packet.getTargetHardwareAddress());
        f.append("\nTarget Protocol Address: ").append(packet.getTargetProtocolAddress());

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
        f.append("Packet Type: ICMP");

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
     * Format displayed output for an UDP Packet
     * @param packet
     * @return
     */
    static String formatPacketUDP(UDPPacket packet) {
        StringBuilder f = new StringBuilder(1024);

        // Line 1 - Protocol
        f.append("Packet Type: UDP");

        // Line 2 - IP
        f.append("\nSource IP: ");
        f.append(packet.src_ip.getHostAddress());
        f.append(" --> Destination IP: ");
        f.append(packet.dst_ip.getHostAddress());

        // Line 3 - Ports
        f.append("\nSource Port: ").append(packet.src_port);
        f.append(" --> Destination Port: ").append(packet.dst_port);

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
        f.append("Packet Type: TCP");

        // Line 2 - IP
        f.append("\n\tSource IP: ");
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
     * Format displayed output for IGMP Packet
     * @param packet
     * @return
     */
    static String formatPacketIGMP(IPPacket packet){
        StringBuilder f = new StringBuilder();

        // Line 1 - Packet
        f.append("Packet Type: IGMP");

        // Line 2 - IP
        f.append("\nSource IP: ");
        f.append(packet.src_ip.getHostAddress());
        f.append(" --> Destination IP: ");
        f.append(packet.dst_ip.getHostAddress());

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

    /**
     * Get the specified packet number from the original capture ArrayList
     * @param n
     * @return
     */
    public static Packet getCurrentCapturePacket(int n) {
        return currentCapture.get(n);
    }

    /**
     * Get the specified packet number from the filtered ArrayList
     * @param n
     * @return
     */
    public static Packet getCurrentFilteredPacket(int n) { return filteredCapture.get(n); }

    /**
     * Get total capture size
     * @return
     */
    public static int getCurrentCaptureSize(){
        return currentCapture.size();
    }

    /**
     * Get total size of filtered capture
     * @return
     */
    public static int getFilteredCaptureSize() { return filteredCapture.size(); }

    /**
     * Populate an ArrayList containing Filtered Packets
     * @return
     */
    public static ArrayList<Packet> populateFilteredPackets() {
        filteredCapture = new ArrayList<>();
        Packet packet;

        for (int i = 0; i < currentCapture.size(); i++) {
            packet = currentCapture.get(i);

            if (packet instanceof IPPacket) {
                if (filterICMP) {
                    if (((IPPacket) packet).protocol != ICMP_IPV6) {
                        continue;
                    } else if (!(((IPPacket) packet).protocol != ICMP)) {
                        continue;
                    }
                }
                if (filterTCP) {
                    if (((IPPacket) packet).protocol != TCP) {
                        continue;
                    }
                }
                if (filterUDP) {
                    if (((IPPacket) packet).protocol != UDP) {
                       continue;
                    }
                }
                if (filterIGMP)
                if (((IPPacket) packet).protocol != IGMP) {
                    continue;
                }
            }
            if (filterARP) {
                if (!(packet instanceof ARPPacket)) {
                    continue;
                }
            }
            filteredCapture.add(packet);
        }

        return filteredCapture;
    }

    /**
     * Assign protocol filters to PacketManager
     * @param tcp
     * @param udp
     * @param icmp
     * @param arp
     */
    public static void setProtocolFilters(boolean tcp, boolean udp, boolean icmp, boolean arp) {
        filterApplied = true;
        filterTCP = tcp;
        filterUDP = udp;
        filterICMP = icmp;
        filterARP = arp;
    }

    /**
     * Assign ip filters to PacketManager
     * @param ip
     */
    public static void setIPAddressFilter(InetAddress ip) {
        filterApplied = true;
        filterIP = ip;

    }

    /**
     *
     * @param port
     * @param isSource
     */
    public static void setSourcePort(int port, boolean isSource){
        filterApplied = true;
        filterPort = port;
        filterIsSourceIP = isSource;
    }

    /**
     * Check if a filter is applied
     * @return
     */
    public static boolean isFilterApplied() {
        return filterApplied;
    }

    /**
     * Reset status to reflect that there are no filters applied
     */
    public static void clearFilters() {
        filterApplied = false;
    }
}