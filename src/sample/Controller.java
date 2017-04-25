/**
 * This file is part of the CS 4398 Software Engineering Project, Spring 2017 class -- Group 2
 * Group Members
 * @author Gregory Pontejos
 * @author Donovan Wells
 * @author Jason Villegas
 * @author Kingsley Nyaosi
 */

package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.packet.Packet;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    // Static classes to manage interfaces and packets
    private NetworkInterfaceManager nInterfaces= new NetworkInterfaceManager();
    private PacketManager pManager= new PacketManager();

    // Output and Network objects
    @FXML
    public ComboBox deviceList = new ComboBox<>();
    public TextArea consoleOutput = new TextArea();
    public CheckBox promiscuousModeButton = new CheckBox();
    public Button clearCaptureButton = new Button();
    public Button startCaptureButton = new Button();

    //Filter objects
    @FXML
    public CheckBox filterTCP = new CheckBox();
    public CheckBox filterUDP = new CheckBox();
    public CheckBox filterICMP = new CheckBox();
    public CheckBox filterARP = new CheckBox();
    public TextField filterIP = new TextField();
    public TextField filterPort = new TextField();
    public CheckBox filterSource = new CheckBox();
    public CheckBox filterDestination = new CheckBox();
    public Button filterApplyButton = new Button();
    public Button filterClearButton = new Button();

    // Stats objects
    @FXML
    public TextField totalStats = new TextField();
    public TextField TCPStat = new TextField();
    public TextField UDPStat = new TextField();
    public TextField ICMPStat = new TextField();
    public TextField ARPStat = new TextField();

    //private int captureStatus;
    private int lineCount = 0;
    private Thread t;

    // 0 if filters are not applied, 1 if filters are applied
    private int filterApplied = 0;

    public Controller() throws IOException {
    }

    /**
     * Initialize GUI and get current network interfaces
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deviceList.setItems(NetworkInterfaceManager.activeInterfaces());
        disableAllFilterFields();
        consoleOutput.setEditable(false);
    }

    /**
     * Force shutdown application
     */
    @FXML
    void closeApp() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Displays About Us modal
     */
    @FXML
    void handleAbout() {
        GridPane root = new GridPane();
        Stage aboutPopup = new Stage();
        try {
            root = FXMLLoader.load(getClass().getResource("about.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        aboutPopup.setResizable(false);
        aboutPopup.setTitle("About");
        Scene nScene = new Scene(root, 382,144);
        nScene.getStylesheets().clear();
        nScene.getStylesheets().add("theme.css");
        aboutPopup.setScene(nScene);
        aboutPopup.show();

        System.out.printf("Display 'About Scanner' Popup\n");
    }

    /**
     * Opens new modal to display attributes of all attached network interfaces
     * @throws IOException
     */
    @FXML
    public void showInterfaces() throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("interfaces.fxml"));
        Stage interfacePopUp = new Stage();
        interfacePopUp.setResizable(false);
        interfacePopUp.setTitle("Network Interface Information");
        Scene nScene = new Scene(pane, 600,400);
        nScene.getStylesheets().clear();
        nScene.getStylesheets().add("theme.css");
        interfacePopUp.setScene(nScene);
        interfacePopUp.show();
        System.out.print("Interface List PopUp Launched\n");
    }

    /**
     * Starts a new capture if an active interface is selected
     * @throws IOException
     */
    @FXML
    public void startCapture() throws IOException {
        PacketManager.newCapture();
        disableAllFilterFields();
        startCaptureButton.setDisable(true);
        clearCaptureButton.setDisable(true);
        promiscuousModeButton.setDisable(true);

        // Prevents capture from starting if an interface is not selected
        if (deviceList.getSelectionModel().getSelectedIndex() != -1) {
            consoleOutput.setEditable(true);
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Controller.this.capturePackets();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        } else {
            System.out.printf("Error: Interface not selected.\n");
        }
    }

    /**
     *
     */

  private void capturePackets() throws IOException {
        Packet tempPacket;
        JpcapCaptor captor = null;

        // Open the selected network interface to begin a capture
        try {
            captor = JpcapCaptor.openDevice(NetworkInterfaceManager.getSelectedInterface(deviceList),
                    65535, promiscuousModeButton.isSelected(), 20);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("Network Interface opened\n");
        while(!Thread.currentThread().isInterrupted()) {
            try {
                assert captor != null;
                tempPacket = captor.getPacket();
                if (tempPacket != null && tempPacket.toString().length() > 20) {
                    PacketManager.addPacket(tempPacket);
                    Controller.this.printPacket(tempPacket);
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\n\n" + PacketManager.getCurrentCapturePacket(2));
        captor.close();
        System.out.println("\n\n" + PacketManager.getCurrentCapturePacket(2));
    }

    /**
     *
     * @param packet
     */
    private void printPacket(Packet packet) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (packet != null) {
                    consoleOutput.appendText("Packet Number: ");
                    consoleOutput.appendText(Integer.toString(lineCount));
                    consoleOutput.appendText("\n");
                    consoleOutput.appendText(PacketManager.formatPacketInfo(packet));
                    consoleOutput.appendText("\n\n");
                    updateStats();
                    System.out.printf("%s\n", String.valueOf(packet));
                    lineCount++;
                }
            }
        });

    }

    /**
     *
     */
    private void updateStats(){
        totalStats.setText(String.valueOf(PacketManager.getTotalCaptured()));
        TCPStat.setText(String.valueOf(PacketManager.getTotalTCP()));
        UDPStat.setText(String.valueOf(PacketManager.getTotalUDP()));
        ICMPStat.setText(String.valueOf(PacketManager.getTotalICMP()));
        ARPStat.setText(String.valueOf(PacketManager.getTotalARP()));
    }

    /**
     *
     */
    @FXML
    public void resetStats(){
        totalStats.setText("");
        TCPStat.setText("");
        UDPStat.setText("");
        ICMPStat.setText("");
        ARPStat.setText("");
    }

    /**
     *
     */
    @FXML
    public void stopCapture() {
        // captureStatus = 1;
        t.interrupt();

        consoleOutput.setEditable(false);
        System.out.printf("Capture ended.\n");
        if (PacketManager.getCurrentCaptureSize() > 0) {
            enableAllFilterFields();
            checkEmptyStats();
        }
        clearCaptureButton.setDisable(false);
        startCaptureButton.setDisable(false);
        promiscuousModeButton.setDisable(false);
    }

    /**
     *
     */
    @FXML
    public void clearCapture() throws UnknownHostException {
        PacketManager.clearCapture();
        consoleOutput.clear();
        lineCount = 0;

        handleClearProtocolFilters();
        disableAllFilterFields();
    }

    @FXML
    public void handleViewConversations() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("conversations.fxml"));
        Stage conversationPopUp = new Stage();
        conversationPopUp.setResizable(false);
        conversationPopUp.setTitle("Conversations");
        Scene nScene = new Scene(root, 596,336);
        nScene.getStylesheets().clear();
        nScene.getStylesheets().add("theme.css");
        conversationPopUp.setScene(nScene);
        conversationPopUp.show();
        System.out.print("View Conversations PopUp Launched\n");
    }

    /**
     *
     * @throws IOException
     */
    @FXML
    public void handleViewPayload() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("PacketViewer.fxml"));
        Stage payloadPopUp = new Stage();
        payloadPopUp.setResizable(false);
        payloadPopUp.setTitle("Packet/Payload Information");
        Scene nScene = new Scene(root, 987,500);
        nScene.getStylesheets().clear();
        nScene.getStylesheets().add("theme.css");
        payloadPopUp.setScene(nScene);
        payloadPopUp.show();
        System.out.print("View Payload PopUp Launched\n");
    }

    /**
     *
     */
    @FXML
    public void handleApplyProtocolFilters() throws UnknownHostException {
        filterApplied = 1;

        // Set filters
        if (!checkEmptyFilters()) {
            if (!filterIP.getText().trim().isEmpty() &&
                    isIPValid(filterIP.getText())) {
                PacketManager.setIPAddressFilter(InetAddress.getByName(filterIP.getText().trim()),
                        filterSource.isSelected(),
                        filterDestination.isSelected());
            }
            if (!filterPort.getText().isEmpty()) {
                PacketManager.setSourcePort(Integer.parseInt(filterPort.getText().trim()),
                        filterSource.isSelected(),
                        filterDestination.isSelected());
            }
            PacketManager.setProtocolFilters(filterTCP.isSelected(),
                    filterUDP.isSelected(),
                    filterICMP.isSelected(),
                    filterARP.isSelected());
            PacketManager.populateFilteredPackets();

            // Print packets to console
            consoleOutput.setEditable(true);
            consoleOutput.clear();

            for (int i = 0; i < PacketManager.getFilteredCaptureSize(); i++) {
                consoleOutput.appendText("Packet Number: ");
                consoleOutput.appendText(Integer.toString(i));
                consoleOutput.appendText("\n");
                consoleOutput.appendText(PacketManager.formatPacketInfo(PacketManager.getCurrentFilteredPacket(i)));
                consoleOutput.appendText("\n\n");
                lineCount++;
            }
            consoleOutput.setEditable(false);
        }
    }

    /**
     *
     */
    @FXML
    public void handleClearProtocolFilters() throws UnknownHostException {
        PacketManager.clearFilters();
        clearFilterText();
        consoleOutput.setEditable(true);
        consoleOutput.clear();

        for (int i = 0; i < PacketManager.getCurrentCaptureSize(); i++) {
            consoleOutput.appendText("Packet Number: ");
            consoleOutput.appendText(Integer.toString(i));
            consoleOutput.appendText("\n");
            consoleOutput.appendText(PacketManager.formatPacketInfo(PacketManager.getCurrentCapturePacket(i)));
            consoleOutput.appendText("\n\n");
        }

        consoleOutput.setEditable(false);
    }
    /**
     * Verifies destination IP box is not checked
     */
    @FXML
    public void handleSourceIPCheckBox() {
        if (filterSource.isSelected()) {
            filterDestination.setSelected(false);
        }
    }

    /**
     * Verifies source IP box is not checked
     */
    @FXML
    public void handleDestinationIPCheckBox() {
        if (filterDestination.isSelected()) {
            filterSource.setSelected(false);
        }
    }

    /**
     *
     */
    @FXML
    public void handleTCPCheckBoxSelect() {
        if (filterUDP.isSelected()) filterUDP.setSelected(false);
        if (filterICMP.isSelected()) filterICMP.setSelected(false);
        if (filterARP.isSelected()) filterARP.setSelected(false);

        enablePortField();
    }

    /**
     *
     */
    @FXML
    public void handleUDPCheckBoxSelect() {
        if (filterTCP.isSelected()) filterTCP.setSelected(false);
        if (filterICMP.isSelected()) filterICMP.setSelected(false);
        if (filterARP.isSelected()) filterARP.setSelected(false);

        enablePortField();
    }

    /**
     *
     */
    @FXML
    public void handleICMPCheckBoxSelect() {
        if (filterTCP.isSelected()) filterTCP.setSelected(false);
        if (filterUDP.isSelected()) filterUDP.setSelected(false);
        if (filterARP.isSelected()) filterARP.setSelected(false);

        enablePortField();
    }

    /**
     * Disables ports when only ARP protocol is selected
     */
    @FXML
    public void handleARPCheckBoxSelect() {
        if (filterTCP.isSelected()) filterTCP.setSelected(false);
        if (filterUDP.isSelected()) filterUDP.setSelected(false);
        if (filterICMP.isSelected()) filterICMP.setSelected(false);

        enablePortField();
    }

    /**
     * Helpter function -- clears all filter fields
     */
    private void clearFilterText() {
        filterTCP.setSelected(false);
        filterUDP.setSelected(false);
        filterICMP.setSelected(false);
        filterARP.setSelected(false);
        filterIP.clear();
        filterPort.clear();
        filterSource.setSelected(false);
        filterDestination.setSelected(false);
    }

    /**
     * Validates IP string input
     * @param ip IP user input string to be validated
     * @return True if the IP is of the correct format
     */
    private static boolean isIPValid(String ip) {
        if(ip == null || ip.length() < 7 || ip.length() > 15) return false;

        try {
            int x = 0;
            int y = ip.indexOf('.');

            if (y == -1 || ip.charAt(x) == '-' || Integer.parseInt(ip.substring(x, y)) > 255) return false;

            x = ip.indexOf('.', ++y);
            if (x == -1 || ip.charAt(y) == '-' || Integer.parseInt(ip.substring(y, x)) > 255) return false;

            y = ip.indexOf('.', ++x);
            return  !(y == -1 ||
                    ip.charAt(x) == '-' ||
                    Integer.parseInt(ip.substring(x, y)) > 255 ||
                    ip.charAt(++y) == '-' ||
                    Integer.parseInt(ip.substring(y, ip.length())) > 255 ||
                    ip.charAt(ip.length()-1) == '.');

        } catch (NumberFormatException e) {
            return false;
        }
    }




    /**
     * Helper function to enable port field if ARP is selected
     */
    private void enablePortField() {
        if (filterARP.isSelected() &&
                !filterTCP.isSelected() &&
                !filterUDP.isSelected() &&
                !filterICMP.isSelected()) {
            filterPort.setDisable(true);
            filterIP.setDisable(true);
            filterSource.setDisable(true);
            filterDestination.setDisable(true);
        } else {
            filterPort.setDisable(false);
            filterIP.setDisable(false);
            filterSource.setDisable(false);
            filterDestination.setDisable(false);
        }
    }

    /**
     * Helper function -- disable all filter fields
     */
    private void disableAllFilterFields() {
        filterTCP.setDisable(true);
        filterUDP.setDisable(true);
        filterICMP.setDisable(true);
        filterARP.setDisable(true);
        filterIP.setDisable(true);
        filterPort.setDisable(true);
        filterSource.setDisable(true);
        filterDestination.setDisable(true);
        filterApplyButton.setDisable(true);
        filterClearButton.setDisable(true);
    }

    /**
     * Helper function -- enable all filter fields
     */
    private void enableAllFilterFields() {
        filterTCP.setDisable(false);
        filterUDP.setDisable(false);
        filterICMP.setDisable(false);
        filterARP.setDisable(false);
        filterIP.setDisable(false);
        filterPort.setDisable(false);
        filterSource.setDisable(false);
        filterDestination.setDisable(false);
        filterApplyButton.setDisable(false);
        filterClearButton.setDisable(false);
    }


    /**
     * Helper function -- disable filter field if no packets of that protocol were captured
     */
    private void checkEmptyStats() {
        if (PacketManager.getTotalARP() == 0) { filterARP.setDisable(true); }
        if (PacketManager.getTotalICMP() == 0) { filterICMP.setDisable(true); }
        if (PacketManager.getTotalTCP() == 0) { filterTCP.setDisable(true); }
        if (PacketManager.getTotalUDP() == 0) { filterUDP.setDisable(true); }
    }

    /**
     * Helper function - check if there are any filters are populated
     */
    private boolean checkEmptyFilters() {
        return !(filterTCP.isSelected() ||
                filterUDP.isSelected() ||
                filterICMP.isSelected() ||
                filterARP.isSelected() ||
                filterIP.getText().isEmpty() ||
                filterPort.getText().isEmpty());
    }

    @FXML
    public void handleSaveCapture(){
        FileChooser saveFileChooser = new FileChooser();
        configureFileChooser(saveFileChooser);
        //Opens up a file to save the packet datat to
        File file = saveFileChooser.showSaveDialog(null);

            String saveFileName = file.getName();
            //saveFileName = file.getName();
            System.out.println("A file was created and will be saved with the name of " + " " + saveFileName);
            //String consoleText = consoleOutput.getText();
            PacketManager.saveCapture(file);
    }

    @FXML
    public void handleOpenCapture(){
        // make sure currentCapture is clear before opening a capture from file.
        try {
            clearCapture();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        resetStats();


        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);

        File file = fileChooser.showOpenDialog(null);
        String FileName = file.getName();
        System.out.println("A file will be opened with the name of " + " " + FileName);
        PacketManager.openCapture(file);

        for(int i =0; i < pManager.getCurrentCaptureSize(); i++) {
            printPacket(pManager.getCurrentCapturePacket(i));
        }
        stopCapture();
    }

    public static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("tcpdump","*.pcap"),
                new FileChooser.ExtensionFilter("Text Document", "*.txt"));

    }
}

