package sample;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.TextArea;
import jpcap.NetworkInterfaceAddress;
import jpcap.packet.Packet;


public class Controller implements Initializable {

    NetworkInterface[] devices = JpcapCaptor.getDeviceList();
    public ComboBox deviceList;

    @FXML
    public TextArea consoleOutput = new TextArea();

    public int captureStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.printf("Devices added: ");
        for(int i = 0; i < devices.length; i++){
            System.out.printf("%s ", devices[i].name);
            deviceList.getItems().add(devices[i].name);
        }
        System.out.printf("\n");
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

    public NetworkInterface getSelectedInterface () {
        int r = deviceList.getSelectionModel().getSelectedIndex();

        System.out.printf("Device selected: %s\n", r);

        return devices[r];
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
            captor = JpcapCaptor.openDevice(getSelectedInterface(), 65535, false, 1);
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


