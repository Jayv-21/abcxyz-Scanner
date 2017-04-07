package sample;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.packet.Packet;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import java.io.File;
import javafx.stage.FileChooser;

public class Controller implements Initializable {

    private NetworkInterfaceManager nInterfaces= new NetworkInterfaceManager();
    private PacketManager pManager= new PacketManager();

    //private  Desktop desktop = Desktop.getDesktop();
    //FileChooser fileChooser = new FileChooser();

    @FXML
    public ComboBox deviceList = new ComboBox<>();
    public TextArea consoleOutput = new TextArea();


    private int captureStatus;
    private int lineCount = 0;
    private JpcapCaptor jpcap = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deviceList.setItems(NetworkInterfaceManager.activeInterfaces());
    }

    @FXML
    void closeApp() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void handleAbout() {
        GridPane root = new GridPane();
        Stage aboutPopup = new Stage();
        try {
            root = FXMLLoader.load(getClass().getResource("about.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        aboutPopup.setTitle("About");
        aboutPopup.setScene(new Scene(root, 382,144));
        aboutPopup.show();

        System.out.printf("Display 'About Scanner' Popup\n");
    }

    @FXML
    public void showInterfaces() throws IOException {
        Pane pane = FXMLLoader.load(getClass().getResource("interfaces.fxml"));
        Stage interfacePopUp = new Stage();

        interfacePopUp.setTitle("Network Interface Information");
        interfacePopUp.setScene(new Scene(pane, 600,400));
        interfacePopUp.show();

        System.out.print("Interface List PopUp Launched\n");
    }

    @FXML
    public void startCapture() throws IOException {
        PacketManager.newCapture();

        // Prevents capture from starting if an interface is not selected
        if (deviceList.getSelectionModel().getSelectedIndex() != -1) {
            Thread t;
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
        jpcap = captor;
        captor.close();
    }


    private void printPacket(Packet packet) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (packet != null) {
                    consoleOutput.appendText("Packet Number: ");
                    consoleOutput.appendText(Integer.toString(lineCount));
                    consoleOutput.appendText(PacketManager.formatPacketInfo(packet));
                    consoleOutput.appendText("\n");
                    System.out.printf("%s\n", String.valueOf(packet));
                    lineCount++;
                }
            }
        });

    }

    @FXML
    public void stopCapture() {
        captureStatus = 1;
        System.out.printf("Capture ended.\n");
    }


    @FXML
    public void clearCapture() {
        PacketManager.clearCapture();
        consoleOutput.clear();
        lineCount = 0;
    }

    @FXML
    public void handleClearStats() {
        PacketManager.clearStats();
    }

    @FXML
    public void handleSaveCapture(){
        FileChooser saveFileChooser = new FileChooser();
        configureFileChooser(saveFileChooser);
        //Opens up a file to save the packet datat to
        File file = saveFileChooser.showSaveDialog(null);
        JpcapCaptor captor = jpcap;
        /*try {
            captor = JpcapCaptor.openDevice(NetworkInterfaceManager.getSelectedInterface(deviceList), 65535, true, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        if(file != null)
        {
            String saveFileName = null;
            saveFileName = file.getName();
            System.out.println("A file was created and will be saved with the name of " + " " + saveFileName);
            String consoleText = consoleOutput.getText();
            PacketManager.saveCapture(captor, saveFileName, file, consoleText);
        }

    }

    @FXML
    public void handleOpenCapture(){
        FileChooser openFileChooser = new FileChooser();
        configureFileChooser(openFileChooser);
        File file = openFileChooser.showOpenDialog(null);
        if(file != null)
        {
            String openFileName = null;
            openFileName = file.getName();
            System.out.println("A file will be opened and the file being opened is  " + " " + openFileName);
            PacketManager.openCapture(file);
        }
    }

    public static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("tcpdump","*.pcap"),
                new FileChooser.ExtensionFilter("Text Document", "*.txt"));
    }
}



