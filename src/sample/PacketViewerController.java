/**
 * This file is part of the CS 4398 Software Engineering Project, Spring 2017 class -- Group 2
 * Group Members
 * @author Gregory Pontejos
 * @author Donovan Wells
 * @author Jason Villegas
 * @author Kingsley Nyaosi
 */

package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import jpcap.packet.*;

import java.net.InetAddress;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by gregorypontejos on 4/2/17.
 */
public class PacketViewerController implements Initializable {

    private PacketManager pManager= new PacketManager();
    private Packet currentPacket;
    private int packetNum;

    @FXML
    public TextArea packetInfo = new TextArea();
    public TextArea payloadRaw = new TextArea();
    public TextArea payloadText = new TextArea();
    public TextArea followStreamText = new TextArea();
    public TextField inPacket = new TextField();
    public Button followStreamButton = new Button();
    public Button previousPacketButton = new Button();
    public Button nextPacketButton = new Button();

    // Protocols used
    private static int ICMP = 1;
    private static int IGMP = 2;
    private static int TCP = 6;
    private static int UDP = 17;
    private static int ICMP_IPV6 = 58;
    public PacketViewerController() throws IOException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        disableAllButtons();
    }

    /**
     * Handler to populate all packet information fields on button click
     */
    @FXML
    public void handleViewPacket() {
        if (!inPacket.getText().trim().isEmpty()) {
            packetNum = Integer.parseInt(inPacket.getText());
            System.out.printf("Packet Entered: %s\n", inPacket.getText());
            if (setCurrentPacket()) {
                //System.out.printf("%s", currentPacket.toString());
                populatePacketInfo();
                populatePayloadRaw();
                populatePayloadText();
                enableAllButtons();
            } else {
                inPacket.clear();
            }
        } else {
            inPacket.clear();
        }

        disableIfARP();
    }

    /**
     * Displays packet information in text field
     */
    private void populatePacketInfo() {
        packetInfo.setEditable(true);
        packetInfo.clear();
        packetInfo.appendText("Packet Number: ");
        packetInfo.appendText(Integer.toString(packetNum));
        packetInfo.appendText("\n");
        packetInfo.appendText(PacketManager.formatPacketInfo(currentPacket));
        packetInfo.appendText("\n");
        packetInfo.setEditable(false);
        disableIfARP();
    }

    /**
     *
     */
    private void populatePayloadRaw() {
        StringBuilder f = new StringBuilder(2048);
        int tmp;

        payloadRaw.setEditable(true);
        payloadRaw.clear();

        for (int i = 0; i < currentPacket.data.length; i = i + 16){
            tmp = 0;

            if (i == 272) {
                f.append(String.format("%04X\t\t", i));
            } else {
                f.append(String.format("%04X\t", i));
            }

            while(tmp < 16 && ((i + tmp) < currentPacket.data.length)) {
                f.append(String.format("%02X", currentPacket.data[i + tmp]));
                f.append("\t");
                tmp++;
            }
            f.append("\n");
        }

        payloadRaw.appendText(f.toString().toUpperCase());
        payloadRaw.setEditable(false);
    }

    /**
     *
     */
    private void populatePayloadText() {
        StringBuilder f = new StringBuilder(2048);
        int tmp;

        payloadText.setEditable(true);
        payloadText.clear();

        for (int i = 0; i < currentPacket.data.length; i = i + 16){
            tmp = 0;

            if (i == 272) {
                f.append(String.format("%04X\t\t", i));
            } else {
                f.append(String.format("%04X\t", i));
            }

            while(tmp < 16 && ((i + tmp) < currentPacket.data.length)) {
                if (currentPacket.data[i + tmp] < 33 ||
                        currentPacket.data[i + tmp] > 126) {
                    f.append(".");
                } else {
                    f.append((char)currentPacket.data[i + tmp]);
                }
                tmp++;
            }
            f.append("\n");
        }

        payloadText.appendText(f.toString());
        payloadText.setEditable(false);
    }

    /**
     *
     */
    @FXML
    public void handleNextPacket() {
        inPacket.clear();
        followStreamText.setEditable(true);
        followStreamText.clear();
        followStreamText.setEditable(false);

        if (PacketManager.isFilterApplied()) {
            if ((packetNum + 1) < PacketManager.getFilteredCaptureSize()) {
                packetNum++;
                System.out.printf("Next Packet displayed: %d\n", packetNum);
            }
            currentPacket = PacketManager.getCurrentFilteredPacket(packetNum);
        } else {
            if ((packetNum + 1) < PacketManager.getCurrentCaptureSize()) {
                packetNum++;
                System.out.printf("Next Packet displayed: %d\n", packetNum);
            }
            currentPacket = PacketManager.getCurrentCapturePacket(packetNum);
        }
        //System.out.printf("%s", currentPacket.toString());
        populatePacketInfo();
        populatePayloadRaw();
        populatePayloadText();
        disableIfARP();
    }

    /**
     *
     */
    @FXML
    public void handlePreviousPacket() {
        inPacket.clear();
        followStreamText.clear();

        if ((packetNum - 1) >= 0) {
            packetNum--;
            System.out.printf("Next Packet displayed: %d\n", packetNum);
            setCurrentPacket();
        }
        //System.out.printf("%s", currentPacket.toString());
        populatePacketInfo();
        populatePayloadRaw();
        populatePayloadText();
        disableIfARP();
    }

    /**
     *
     */
    @FXML
    public void handleFollowStream() {
        // Set protocol and IP to search
        InetAddress sIP = ((IPPacket) currentPacket).src_ip;
        InetAddress dIP = ((IPPacket) currentPacket).dst_ip;
        int protocol = ((IPPacket)currentPacket).protocol;
        int sPort = 0;
        int dPort = 0;

        StringBuilder f = new StringBuilder();
        followStreamText.setEditable(true);
        followStreamText.clear();

        // Set ports to search
        // ICMP and IGMP uses the same port, so ports are not needed.
        if (currentPacket instanceof TCPPacket) {
            sPort = ((TCPPacket) currentPacket).src_port;
            dPort = ((TCPPacket) currentPacket).dst_port;
        } else if (currentPacket instanceof UDPPacket) {
            sPort = ((UDPPacket) currentPacket).src_port;
            dPort = ((UDPPacket) currentPacket).dst_port;
        }

        System.out.printf("Ports to be matched for FollowStream\n");
        System.out.printf("Source IP: %s\n", sIP.toString());
        System.out.printf("Destination IP: %s\n", dIP.toString());
        System.out.printf("Source port: %d\n", sPort);
        System.out.printf("Destination port: %d\n", dPort);

        // Search through current capture for packets -- No filters applied
        if (!PacketManager.isFilterApplied()) {
            for (int i = 0; i < PacketManager.getCurrentCaptureSize(); i++) {
                Packet tPacket = PacketManager.getCurrentCapturePacket(i);

                System.out.printf("In loop -- Iteration %d\n", i);

                if (tPacket instanceof TCPPacket ||
                        tPacket instanceof UDPPacket) {
                    // Protocol check
                    if (((IPPacket) tPacket).protocol != protocol) {
                        System.out.printf("Protocol Fail\n");
                        continue;
                    }

                    // Port check
                    if (protocol == UDP) {
                        if (sPort != ((UDPPacket) tPacket).src_port &&
                                dPort != ((UDPPacket) tPacket).dst_port) {
                            System.out.printf("Port Fail\n");
                            continue;
                        }
                    }
                    if (protocol == TCP) {
                        if (sPort != ((TCPPacket) tPacket).src_port &&
                                dPort != ((TCPPacket) tPacket).dst_port) {
                            System.out.printf("Port Fail\n");
                            continue;
                        }
                    }

                    // IP check
                    if (!sIP.equals(((IPPacket) tPacket).src_ip) &&
                            !dIP.equals(((IPPacket) tPacket).dst_ip)) {
                        System.out.printf("IP Fail\n");
                        continue;
                    }

                    // Add payload to Follow Stream text area
                    for (int j = 0; j < tPacket.data.length - 1; j++) {
                        if (tPacket.data[j] <= 126 && tPacket.data[j] >= 32 ||
                                tPacket.data[j] == 10) {
                            f.append((char) tPacket.data[j]);
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < PacketManager.getFilteredCaptureSize(); i++) {
                Packet tPacket = PacketManager.getCurrentFilteredPacket(i);

                System.out.printf("In loop -- Iteration %d\n", i);

                if (tPacket instanceof TCPPacket ||
                        tPacket instanceof UDPPacket) {
                    // Protocol check
                    if (((IPPacket) tPacket).protocol != protocol) {
                        System.out.printf("Protocol Fail\n");
                        continue;
                    }

                    // Port check
                    if (protocol == UDP) {
                        if (sPort != ((UDPPacket) tPacket).src_port &&
                                dPort != ((UDPPacket) tPacket).dst_port) {
                            System.out.printf("Port Fail\n");
                            continue;
                        }
                    }
                    if (protocol == TCP) {
                        if (sPort != ((TCPPacket) tPacket).src_port &&
                                dPort != ((TCPPacket) tPacket).dst_port) {
                            System.out.printf("Port Fail\n");
                            continue;
                        }
                    }

                    // IP check
                    if (!sIP.equals(((IPPacket) tPacket).src_ip) &&
                            !dIP.equals(((IPPacket) tPacket).dst_ip)) {
                        System.out.printf("IP Fail\n");
                        continue;
                    }

                    // Add payload to Follow Stream text area
                    for (int j = 0; j < tPacket.data.length - 1; j++) {
                        if (tPacket.data[j] <= 126 && tPacket.data[j] >= 32 ||
                                tPacket.data[j] == 10) {
                            f.append((char) tPacket.data[j]);
                        }
                    }
                }
            }
        }
        followStreamText.appendText(f.toString());
    }

    /**
     * Helper function
     * Set current packet based on if filter is applied
     */
    private boolean setCurrentPacket() {
        if (PacketManager.isFilterApplied()) {
            if (packetNum < PacketManager.getFilteredCaptureSize() &&
                    packetNum >= 0 &&
                    PacketManager.getFilteredCaptureSize() > 0) {
                currentPacket = PacketManager.getCurrentFilteredPacket(packetNum);
                return true;
            }
        } else {
            if (packetNum < PacketManager.getCurrentCaptureSize() &&
                    packetNum >= 0 &&
                    PacketManager.getCurrentCaptureSize() > 0) {
                currentPacket = PacketManager.getCurrentCapturePacket(packetNum);
                return true;
            }
        }
        return false;
    }

    /**
     * Disables all buttons, except for 'View Packet'
     */
    private void disableAllButtons() {
        followStreamButton.setDisable(true);
        nextPacketButton.setDisable(true);
        previousPacketButton.setDisable(true);
    }

    /**
     * Enables all buttons, except for 'View Packet'
     */
    private void enableAllButtons() {
        followStreamButton.setDisable(false);
        nextPacketButton.setDisable(false);
        previousPacketButton.setDisable(false);
    }

    /**
     * Disable FollowStream button if ARP Packet
     */
    private void disableIfARP() {
        if (currentPacket instanceof TCPPacket || currentPacket instanceof UDPPacket) {
            followStreamButton.setDisable(false);
        } else followStreamButton.setDisable(true);
    }
}
