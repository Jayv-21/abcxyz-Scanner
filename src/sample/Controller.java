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


public class Controller implements Initializable {

    private NetworkInterfaceManager nInterface = new NetworkInterfaceManager();

    @FXML
    public ComboBox deviceList = new ComboBox<>();

    @FXML
    public TextArea consoleOutput = new TextArea();

    public int captureStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deviceList.setItems(nInterface.activeInterfaces());
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

        if (deviceList.getSelectionModel().getSelectedIndex() != -1) {
            new Thread(() -> capturePackets()).start();
        } else {
            System.out.printf("Error: Interface not selected.\n");
        }
    }

    public void capturePackets() {
        captureStatus = 0;

        JpcapCaptor captor = null;

        try {
            captor = JpcapCaptor.openDevice(nInterface.getSelectedInterface(deviceList), 65535, false, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Network Interface opened\n");

        // Capture packets until stopCapture is called

        while (captureStatus == 0) {
            printPacket(captor.getPacket());
        }
        captor.close();
    }


    public void printPacket(Packet packet) {
        Platform.runLater(() -> {
            if (packet != null) {
                consoleOutput.appendText(String.valueOf(packet));
                consoleOutput.appendText("\n");
                System.out.printf("%s\n", String.valueOf(packet));
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
        consoleOutput.clear();
    }
}


