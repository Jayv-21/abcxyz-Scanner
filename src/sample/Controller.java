package sample;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.packet.Packet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    private NetworkInterfaceManager nInterfaces= new NetworkInterfaceManager();
    private PacketManager pManager= new PacketManager();

    // Output and Network objects
    @FXML
    public ComboBox deviceList = new ComboBox<>();
    public TextArea consoleOutput = new TextArea();

    //Filter objects
    @FXML
    public CheckBox filterTCP = new CheckBox();
    public CheckBox filterUDP = new CheckBox();
    public CheckBox filterICMP = new CheckBox();
    public CheckBox filterARP = new CheckBox();
    public TextField filterIP = new TextField();
    public TextField filterPort = new TextField();
    public CheckBox filterSourceIP = new CheckBox();
    public CheckBox filterDestinationIP = new CheckBox();

    // Stats objects
    @FXML
    public TextField totalStats = new TextField();
    public TextField TCPStat = new TextField();
    public TextField UDPStat = new TextField();
    public TextField ICMPStat = new TextField();
    public TextField ARPStat = new TextField();


    private int captureStatus;
    private int lineCount = 0;

    // 0 if filters are not applied, 1 if filters are applied
    private int filterApplied = 0;

    /**
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deviceList.setItems(NetworkInterfaceManager.activeInterfaces());
    }

    /**
     *
     */
    @FXML
    void closeApp() {
        Platform.exit();
        System.exit(0);
    }

    /**
     *
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
        aboutPopup.setScene(new Scene(root, 382,144));
        aboutPopup.show();

        System.out.printf("Display 'About Scanner' Popup\n");
    }

    /**
     *
     * @throws IOException
     */
    @FXML
    public void showInterfaces() throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("interfaces.fxml"));
        Stage interfacePopUp = new Stage();
        interfacePopUp.setResizable(false);
        interfacePopUp.setTitle("Network Interface Information");
        interfacePopUp.setScene(new Scene(pane, 600,400));
        interfacePopUp.show();
        System.out.print("Interface List PopUp Launched\n");
    }

    /**
     *
     * @throws IOException
     */
    @FXML
    public void startCapture() throws IOException {
        PacketManager.newCapture();

        // Prevents capture from starting if an interface is not selected
        if (deviceList.getSelectionModel().getSelectedIndex() != -1) {
            Thread t;
            consoleOutput.setEditable(true);
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Controller.this.capturePackets();
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
    private void capturePackets() {
        Packet tempPacket;
        captureStatus = 0;

        JpcapCaptor captor = null;

        // Open the selected network interface to begin a capture
        try {
            captor = JpcapCaptor.openDevice(NetworkInterfaceManager.getSelectedInterface(deviceList), 65535, false, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("Network Interface opened\n");

        // Capture packets until stopCapture is called
        while (captureStatus == 0) {
            assert captor != null;
            tempPacket = captor.getPacket();
            PacketManager.addPacket(tempPacket);
            printPacket(tempPacket);

        }
        captor.close();
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
        captureStatus = 1;
        consoleOutput.setEditable(false);
        System.out.printf("Capture ended.\n");
    }

    /**
     *
     */
    @FXML
    public void clearCapture() {
        PacketManager.clearCapture();
        consoleOutput.clear();
        lineCount = 0;
    }

    /**
     *
     */
    @FXML
    public void handleClearStats() {
        PacketManager.clearStats();
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
        payloadPopUp.setScene(new Scene(root, 700,500));
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
        if (!filterIP.getText().isEmpty()) {
            PacketManager.setIPAddressFilter(InetAddress.getByName(filterIP.getText()));
        }
        if (!filterPort.getText().isEmpty()) {
            PacketManager.setSourcePort(Integer.parseInt(filterPort.getText()), filterSourceIP.isSelected());
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

    /**
     *
     */
    @FXML
    public void handleClearProtocolFilters() {
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
        if (filterSourceIP.isSelected()) {
            filterDestinationIP.setSelected(false);
        }
    }

    /**
     * Verifies source IP box is not checked
     */
    @FXML
    public void handleDestinationIPCheckBox() {
        if (filterDestinationIP.isSelected()) {
            filterSourceIP.setSelected(false);
        }
    }

    private void clearFilterText() {
        filterTCP.setSelected(false);
        filterUDP.setSelected(false);
        filterICMP.setSelected(false);
        filterIP.clear();
        filterPort.clear();
        filterSourceIP.setSelected(false);
        filterDestinationIP.setSelected(false);
    }
}
