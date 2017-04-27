package sample;

/**
 * This file is part of the CS 4398 Software Engineering Project, Spring 2017 class -- Group 2
 * Group Members
 * @author Gregory Pontejos
 * @author Donovan Wells
 * @author Jason Villegas
 * @author Kingsley Nyaosi
 */

import jpcap.packet.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static sample.PacketManager.*;

class PacketManagerTest {

    // Protocols used
    private static int ICMP = 1;
    private static int IGMP = 2;
    private static int TCP = 6;
    private static int UDP = 17;
    private static int ICMP_IPV6 = 58;

    private TCPPacket testPacketTCP;
    private UDPPacket testPacketUDP;
    private ARPPacket testPacketARP;
    private ICMPPacket testPacketICMP;
    private IPPacket testPacketICMPv6;
    private IPPacket testPacketIGMP;

    private InetAddress testIPsrc1;
    private InetAddress testIPdst1;
    private InetAddress testIPsrc2;
    private InetAddress testIPdst2;

    @BeforeEach
    void setUp() throws UnknownHostException {
        clearCapture();
        testIPsrc1 = InetAddress.getByName("127.0.0.1");
        testIPdst1 = InetAddress.getByName("127.0.0.2");
        testIPsrc2 = InetAddress.getByName("128.0.0.1");
        testIPdst2 = InetAddress.getByName("128.0.0.2");


        testPacketTCP = new TCPPacket(100, 101, 102, 1, false, false, false,
                false, false, false, false, false, 1, 0);
        testPacketTCP.setIPv4Parameter(0,false, false, false, 0, false, false,
                false, 0, 0, 0, TCP, testIPsrc1, testIPdst1);

        testPacketUDP = new UDPPacket(1000, 2000);
        testPacketUDP.setIPv4Parameter(0,false, false, false, 0, false, false,
                false, 0, 0, 0, UDP, testIPsrc2, testIPdst2);

        testPacketICMP = new ICMPPacket();
        testPacketICMP.setIPv4Parameter(0,false, false, false, 0, false, false,
                false, 0, 0, 0, ICMP, testIPsrc1, testIPdst1);

        testPacketICMPv6 = new IPPacket();
        testPacketICMPv6.setIPv4Parameter(0,false, false, false, 0, false, false,
                false, 0, 0, 0, ICMP_IPV6, testIPsrc1, testIPdst1);

        testPacketIGMP = new IPPacket();
        testPacketIGMP.setIPv4Parameter(0,false, false, false, 0, false, false,
                false, 0, 0, 0, IGMP, testIPsrc2, testIPdst2);

        testPacketARP = new ARPPacket();
    }

    /*
    The following tests are for the addPacket function
     */

    @Test
    void addTCPPacketTest() {
        clearCapture();
        addPacket(testPacketTCP);
        assertEquals(1, getCurrentCaptureSize());
    }

    @Test
    void addUDPPacketTest() {
        clearCapture();
        addPacket(testPacketUDP);
        assertEquals(1, getCurrentCaptureSize());
    }

    @Test
    void addARPPacketTest() {
        clearCapture();
        addPacket(testPacketARP);
        assertEquals(1, getCurrentCaptureSize());
    }

    @Test
    void addICMPPacketTest() {
        clearCapture();
        addPacket(testPacketICMP);
        assertEquals(1, getCurrentCaptureSize());
    }

    @Test
    void addICMPv6PacketTest() {
        clearCapture();
        addPacket(testPacketICMPv6);
        assertEquals(1, getCurrentCaptureSize());
    }

    @Test
    void addIGMPPacketTest() {
        clearCapture();
        addPacket(testPacketIGMP);
        assertEquals(1, getCurrentCaptureSize());
    }

    @Test
    void addNullPacketTest() {
        ARPPacket tempPacket = null;
        clearCapture();
        addPacket(tempPacket);
        assertEquals(0, getCurrentCaptureSize());
    }

    /*
    The following tests verify if all packets are correctly counted
     */

    @Test
    void countTCPPacketTest() {
        clearCapture();
        addPacket(testPacketTCP);
        assertEquals(1, getTotalTCP());
    }

    @Test
    void countUDPPacketTest() {
        clearCapture();
        addPacket(testPacketUDP);
        assertEquals(1, getTotalUDP());
    }

    @Test
    void countARPPacketTest() {
        clearCapture();
        addPacket(testPacketARP);
        assertEquals(1, getTotalARP());
    }

    @Test
    void countICMPPacketTest() {
        clearCapture();
        addPacket(testPacketICMP);
        assertEquals(1, getTotalICMP());
    }

    @Test
    void countICMPv6PacketTest() {
        clearCapture();
        addPacket(testPacketICMPv6);
        assertEquals(1, getTotalICMP());
    }

    @Test
    void countIGMPPacketTest() {
        clearCapture();
        addPacket(testPacketIGMP);
        assertEquals(1, getTotalICMP());
    }

    @Test
    void countTotalPacketsTest() {
        clearCapture();
        addPacket(testPacketIGMP);
        addPacket(testPacketICMPv6);
        addPacket(testPacketICMP);
        addPacket(testPacketARP);
        addPacket(testPacketUDP);
        addPacket(testPacketTCP);
        assertEquals(6, getTotalCaptured());
    }

    /*
    The following tests verify the IP, Port, and Protocol filters
     */

    @Test
    void filterProtocolTCPTest() throws UnknownHostException {
        clearFilters();
        setProtocolFilters(true, false, false, false);
        assertTrue(filterProtocolCheck(testPacketTCP));
    }

    @Test
    void filterProtocolUDPTest() throws UnknownHostException {
        clearFilters();
        setProtocolFilters(false, true, false, false);
        assertTrue(filterProtocolCheck(testPacketUDP));
    }

    @Test
    void filterProtocolARPTest() throws UnknownHostException {
        clearFilters();
        setProtocolFilters(false, false, false, true);
        assertTrue(filterProtocolCheck(testPacketARP));
    }

    @Test
    void filterProtocolICMPTest() throws UnknownHostException {
        clearFilters();
        setProtocolFilters(false, false, true, false);
        assertTrue(filterProtocolCheck(testPacketICMP));
    }

    @Test
    void filterProtocolICMPv6Test() throws UnknownHostException {
        clearFilters();
        setProtocolFilters(false, false, true, false);
        assertTrue(filterProtocolCheck(testPacketICMPv6));
    }

    @Test
    void filterPortSourcePassTest() throws UnknownHostException {
        clearFilters();
        setSourcePort(1000, true, false);
        assertTrue(filterIPCheck(testPacketUDP));
    }

    @Test
    void filterPortSourceFailTest() throws UnknownHostException {
        clearFilters();
        setSourcePort(2000, true, false);
        assertFalse(filterIPCheck(testPacketUDP));
    }

    @Test
    void filterPortDestinationPassTest() throws UnknownHostException {
        clearFilters();
        setSourcePort(2000, false, true);
        assertTrue(filterIPCheck(testPacketUDP));
    }

    @Test
    void filterPortDestinationFailTest() throws UnknownHostException {
        clearFilters();
        setSourcePort(1000, false, true);
        assertFalse(filterIPCheck(testPacketUDP));
    }

    @Test
    void filterPortBothPassTest1() throws UnknownHostException {
        clearFilters();
        setSourcePort(1000, false, false);
        assertTrue(filterIPCheck(testPacketUDP));
    }

    @Test
    void filterPortBothPassTest2() throws UnknownHostException {
        clearFilters();
        setSourcePort(2000, false, false);
        assertTrue(filterIPCheck(testPacketUDP));
    }

    @Test
    void filterIPSourceTest() throws UnknownHostException {
        clearFilters();
        setIPAddressFilter(testIPsrc1, true, false);
        assertTrue(filterIPCheck(testPacketTCP));
    }

    @Test
    void filterIPDestinationTest() throws UnknownHostException {
        clearFilters();
        setIPAddressFilter(testIPdst1, false, true);
        assertTrue(filterIPCheck(testPacketTCP));
    }

    @Test
    void filterIPBothTest1() throws UnknownHostException {
        clearFilters();
        setIPAddressFilter(testIPsrc2, false, false);
        assertTrue(filterIPCheck(testPacketUDP));
    }

    @Test
    void filterIPBothTest2() throws UnknownHostException {
        clearFilters();
        setIPAddressFilter(testIPdst2, false, false);
        assertTrue(filterIPCheck(testPacketUDP));
    }

    /*
    Verifies conversation functionality
     */

    @Test
    void conversationTest() {
        clearCapture();
        addPacket(testPacketIGMP);
        addPacket(testPacketICMPv6);
        addPacket(testPacketICMP);
        addPacket(testPacketARP);
        addPacket(testPacketUDP);
        addPacket(testPacketTCP);

        populateConversations();
        assertEquals(2, getConversationsSize());
    }
}

