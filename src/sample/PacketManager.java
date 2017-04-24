/**
 * This file is part of the CS 4398 Software Engineering Project, Spring 2017 class -- Group 2
 * Group Members
 * @author Gregory Pontejos
 * @author Donovan Wells
 * @author Jason Villegas
 * @author Kingsley Nyaosi
 */

package sample;

import jpcap.JpcapCaptor;
import jpcap.packet.*;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 *
 */
class PacketManager implements Serializable {

    private static ArrayList<Packet> currentCapture = new ArrayList<>();
    private static ArrayList<Packet> filteredCapture;
    private static ArrayList<ArrayList> conversations = new ArrayList<>();

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
    private static InetAddress filterIP = null;
    private static int filterPort = -1;
    private static boolean filterIsSourceSelected = false;
    private static boolean filterIsDestinationSelected = false;
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
    PacketManager() throws IOException {
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
     * Deletes the current capture from memory and automatically
     * creates a new capture.
     */
    static void clearCapture() {
        currentCapture = null;

        System.out.printf("Total captured: %s\n", totalCaptured);
        System.out.printf("Total TCP: %s\n", totalTCP);
        System.out.printf("Total UDP: %s\n", totalUDP);
        System.out.printf("Total ICMP: %s\n", totalICMP);
        System.out.printf("Total ARP: %s\n", totalARP);
        System.out.printf("Total IP: %s\n", totalIP);
        System.out.printf("Ready to start new capture\n");

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
     * Counts packets being captured based on protocol
     * Note: IGMP is currently associated with the ICMP statistics
     * @param packet Packet to be counted
     */
    static void countPacket(Packet packet) {
        if (packet instanceof ARPPacket) {
            totalARP++;
        } else if (packet instanceof UDPPacket) {
            totalUDP++;
        } else if (packet instanceof ICMPPacket) {
            totalICMP++;
        } else if (packet instanceof TCPPacket) {
            totalTCP++;
        } else if (packet instanceof IPPacket) {
            totalICMP++;
        }

        totalCaptured++;
    }

    /**
     * Returns the total number of Packets captured
     * @return Total number of packets captures
     */
    static int getTotalCaptured() {
        return totalCaptured;
    }

    /**
     * Returns the total number of TCP Packets captured
     * @return Total TCP packets captured
     */
    static int getTotalTCP() { return totalTCP; }

    /**
     * Returns the total number of UDP Packets captured
     * @return Total UDP packets captured
     */
    static int getTotalUDP () {
        return totalUDP;
    }

    /**
     * Returns the current number of ICMP Packets captured
     * @return Total ICMP packets captured
     */
    static int getTotalICMP() {
        return totalICMP;
    }

    /**
     * Returns the current number of ARP Packets captured
     * @return Total ARP packets captured
     */
    static int getTotalARP() {
        return totalARP;
    }

    /**
     * Parse protocol type to correct formatting function
     * Only protocols currently supported are TCP,ICMP, UDP, and ARP
     * @param packet Packet to be formatted
     * @return Formatted packet
     */
    static String formatPacketInfo(Packet packet) {
        String t;

        if (packet instanceof IPPacket) {
            if (((IPPacket) packet).protocol == ICMP_IPV6) {
                t = formatPacketICMPV6((IPPacket) packet);
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
     * @param packet Packet to be formatted
     * @return ARP formatted packet
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
     * @param packet Packet to be formatted
     * @return ICMP with IPv4 formatting
     */
    static String formatPacketICMP(ICMPPacket packet) {
        StringBuilder f = new StringBuilder(1024);

        // Line 1 - Packet
        f.append("Packet Type: ICMPv4");

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
     * Format displayed output for ICMPv6 Packet
     * @param packet Packet to be formatted
     * @return ICMP packet with IP version 6 formatted
     */
    static String formatPacketICMPV6(IPPacket packet){
        StringBuilder f = new StringBuilder(1024);

        // Line 1 - Packet
        f.append("Packet Type: ICMPv6");

        // Line 2 - IP
        f.append("\nSource IP: ");
        f.append(packet.src_ip.getHostAddress());
        f.append(" --> Destination IP: ");
        f.append(packet.dst_ip.getHostAddress());

        return f.toString();
    }

    /**
     * Format displayed output for an UDP Packet
     * @param packet Packet to be formatted
     * @return UDP formatted packet
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
     * @param packet Packet to be formatted
     * @return TCP formatted packet
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
     * @param packet Packet to be formatted
     * @return IGMP formatted packet
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
     * @param m ICMP code
     * @return ICMP message associated with ICMP code
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
     * @param n Packet to be retrieved from current capture
     * @return Packet from current capture
     */
    public static Packet getCurrentCapturePacket(int n) {
        return currentCapture.get(n);
    }

    /**
     * Get the specified packet number from the filtered ArrayList
     * @param n Packet to be retrieved from current filter capture
     * @return Packet from current filter capture
     */
    public static Packet getCurrentFilteredPacket(int n) { return filteredCapture.get(n); }

    /**
     * Get total capture size
     * @return Total size of the current capture
     */
    public static int getCurrentCaptureSize(){
        return currentCapture.size();
    }

    /**
     * Get total size of filtered capture
     * @return Total size of the filtered capture
     */
    public static int getFilteredCaptureSize() { return filteredCapture.size(); }

    /**
     * Populate an ArrayList containing Filtered Packets
     * @return Filtered packet ArrayList to be displayed
     */
    public static ArrayList<Packet> populateFilteredPackets() {
        filteredCapture = new ArrayList<>();
        Packet packet;

        for (int i = 0; i < currentCapture.size(); i++) {
            packet = currentCapture.get(i);

            // Protocol packet filters
            if (filterTCP || filterUDP || filterICMP || filterARP) {
                if (!filterProtocolCheck(packet)) {
                    continue;
                }
            }

            // IP and Port filters
            // If source or destination are not selected
            if (filterPort > 0 || filterIP != null) {
                if (!(packet instanceof ARPPacket)) {
                    if (!filterIPCheck(packet)) {
                        continue;
                    }
                }
            }
            filteredCapture.add(packet);
        }

        return filteredCapture;
    }

    /**
     * Assign protocol filters to PacketManager
     * @param tcp True if filtering TCP Packets
     * @param udp True if filtering UDP Packets
     * @param icmp True if filtering ICMP Packets
     * @param arp True if filtering ARP Packets
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
     * @param ip IP to be filtered
     * @param isSource True if IP is the source
     * @param isDestination True if IP is the destination
     */
    public static void setIPAddressFilter(InetAddress ip, boolean isSource, boolean isDestination) {
        filterApplied = true;
        filterIP = ip;
        filterIsSourceSelected = isSource;
        filterIsDestinationSelected = isDestination;
    }

    /**
     * Set Port filters to be used
     * @param port Port to be filtered
     * @param isSource True if Port is the source
     * @param isDestination True if Port is the destination
     */
    public static void setSourcePort(int port, boolean isSource, boolean isDestination) {
        filterApplied = true;
        filterPort = port;
        filterIsSourceSelected = isSource;
        filterIsDestinationSelected = isDestination;
    }

    /**
     * Check if a filter is applied
     * @return True if filter is currently applied
     */
    public static boolean isFilterApplied() {
        return filterApplied;
    }

    /**
     * Reset status to reflect that there are no filters applied
     */
    public static void clearFilters() throws UnknownHostException {
        filterApplied = false;
        filterIP = null;
        filterTCP = false;
        filterUDP = false;
        filterICMP = false;
        filterARP = false;
        filterPort = -1;
        filterIsSourceSelected = false;
        filterIsDestinationSelected = false;
    }

    /**
     * Verifies if the Protocol filter matches the current packet
     * @param packet Packet to check
     * @return True if current packet is a match
     */
    private static boolean filterProtocolCheck(Packet packet) {
        if (!(packet instanceof ARPPacket)) {
            if (filterICMP) {
                if (((IPPacket) packet).protocol == ICMP_IPV6 ||
                        (((IPPacket) packet).protocol == ICMP) ||
                        ((IPPacket) packet).protocol == IGMP) {
                    return true;
                }
            }
            if (filterTCP) {
                if (((IPPacket) packet).protocol == TCP) {
                    return true;
                }
            }
            if (filterUDP) {
                if (((IPPacket) packet).protocol == UDP) {
                    return true;
                }
            }
        }
        if (filterARP) {
            if (packet instanceof ARPPacket) { return true; }
        }

        return false;
    }

    /**
     * Verifies if IP and/or port are a match
     * @param packet Packet to check
     * @return True if the current packet is a match
     */
    private static boolean filterIPCheck(Packet packet) {
        if (!(packet instanceof TCPPacket) && !(packet instanceof UDPPacket)) { return false; }


        if (!filterIsDestinationSelected &&
                !filterIsSourceSelected) {
            // ARP packets do not reference ports
            if (filterPort > 0) {
                if (packet instanceof TCPPacket) {
                    if (((TCPPacket) packet).src_port != filterPort &&
                            ((TCPPacket) packet).dst_port != filterPort) {
                        return false;
                    }
                }
                if (packet instanceof UDPPacket) {
                    if (((UDPPacket) packet).src_port != filterPort &&
                            ((UDPPacket) packet).dst_port != filterPort) {
                        return false;
                    }
                }
            }


            // IP filter
            if (filterIP != null) {
                if (packet instanceof TCPPacket || packet instanceof UDPPacket) {
                    if (((IPPacket)packet).src_ip.toString().compareTo(filterIP.toString()) != 0 &&
                            ((IPPacket)packet).dst_ip.toString().compareTo(filterIP.toString()) != 0) {
                        return false;
                    }
                }
            }
        }

        // If source is selected
        if (filterIsSourceSelected) {
            // ARP packets do not reference ports
            if (filterPort > 0) {
                if (packet instanceof TCPPacket) {
                    if (((TCPPacket) packet).src_port != filterPort) {
                        return false;
                    }
                }
                if (packet instanceof UDPPacket) {
                    if (((UDPPacket) packet).src_port != filterPort) {
                        return false;
                    }
                }
            }
            // IP filter
            if (filterIP != null) {
                if (packet instanceof TCPPacket || packet instanceof UDPPacket) {
                    if (((IPPacket)packet).src_ip.toString().compareTo(filterIP.toString()) != 0) {
                        return false;
                    }
                }
            }
        }

        // If destination is selected
        if (filterIsDestinationSelected) {
            // ARP packets do not reference ports
            if (filterPort > 0) {
                if (packet instanceof TCPPacket) {
                    if (((TCPPacket) packet).dst_port != filterPort) {
                        return false;
                    }
                }
                if (packet instanceof UDPPacket) {
                    if (((UDPPacket) packet).dst_port != filterPort) {
                        return false;
                    }
                }
            }

            // IP filter
            if (filterIP != null) {
                if (packet instanceof TCPPacket || packet instanceof UDPPacket) {
                    if (((IPPacket)packet).dst_ip.toString().compareTo(filterIP.toString()) != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * The function searches for all of the existing conversations in
     * the current capture. The conversations are saved in a nested ArrayList,
     * where the first element is always the source, and the remaining elements
     * are destinations from that source.
     */
    public static void populateConverations() {
        Packet packet;
        InetAddress sAddr;
        InetAddress dAddr;
        int sPosition;
        int dPosition;
        ArrayList<InetAddress> tempList;
        conversations = new ArrayList<>();

        for (int i = 0; i < getCurrentCaptureSize(); i++) {
            packet = getCurrentCapturePacket(i);
            if (packet instanceof TCPPacket || packet instanceof UDPPacket) {
                sAddr = ((IPPacket)packet).src_ip;
                dAddr = ((IPPacket)packet).dst_ip;
            } else {
                continue;
            }

            sPosition = checkIfConvoSourceExists(sAddr);

            // Case if Source does not exist in conversations
            if (sPosition == -1) {
                tempList = new ArrayList<>();
                tempList.add(sAddr);
                tempList.add(dAddr);
                conversations.add(tempList);
            } else {
                // Case if Source does exist
                dPosition = checkIfConvoDestExists(sPosition, dAddr);
                if (dPosition != -1) {
                    ((ArrayList<InetAddress>)conversations.get(sPosition)).add(dAddr);
                }
            }
        }
        System.out.printf("End populate conversation function\n");
    }

    /**
     * This function checks to see if the address is an existing source for
     * a conversation
     * @param addr Input address to be searched
     * @return True position of source address if found in conversations
     */
    private static int checkIfConvoSourceExists(InetAddress addr) {
        int count = 0;

        if (conversations.isEmpty()) {
            return -1;
        } else {
            while (count < conversations.size()) {
                InetAddress tempAddr = (InetAddress)conversations.get(count).get(0);
                if (tempAddr.toString().compareTo(addr.toString()) == 0) {
                    return count;
                }
                count++;
            }
        }
        return -1;
    }

    /**
     * This function check to see if the given destination address is associated
     * with the existing source. If not, the position of the destination IP from the
     * nested ArrayList will be returned.
     * @param sPosition Sournce IP position within the conversation
     * @param dAddr The address to be tested
     * @return Position of the destination IP address associated with the source IP
     */
    private static int checkIfConvoDestExists(int sPosition, InetAddress dAddr) {
        int count = 1;
        ArrayList tempList = conversations.get(sPosition);

        while (count < tempList.size()) {
            InetAddress tempAddr = (InetAddress)tempList.get(count);
            if (tempAddr.toString().compareTo(dAddr.toString()) == 0) {
                return -1;
            }
            count++;
        }
        return count;
    }

    /**
     * Gets the total number of IP sources
     * @return Size of conversation array list
     */
    public static int getConversationsSize() {
        return conversations.size();
    }

    /**
     * Gets the IP source and all destinations
     * @param n The conversation to be retrieved
     * @return An ArrayList containing a source and all of its destinations
     */
    public static ArrayList getConversation(int n) {
        return conversations.get(n);
    }

    public static void saveCapture(File file) {
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            for(int i = 0; i < currentCapture.size(); i++)
                out.writeObject(currentCapture.get(i));
            out.close();
            fileOut.close();
            System.out.printf("Done.");
        }catch(IOException i) {
            i.printStackTrace();
        }
    }

    public static void openCapture(File file){
        try {
            FileInputStream fileIn = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Packet inPacket = (Packet) in.readObject();
            System.out.println("Start open read");
            while(inPacket != null) {
                addPacket(inPacket);
                System.out.println(inPacket);
                inPacket = (Packet) in.readObject();
            }
            in.close();
            fileIn.close();
        } catch(EOFException i){
            return;
        } catch(IOException i) {
            i.printStackTrace();
            return;
        } catch(ClassNotFoundException c) {
            c.printStackTrace();
            return;
        }
    }
}