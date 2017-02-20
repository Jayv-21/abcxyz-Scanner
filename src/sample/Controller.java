package sample;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import jpcap.*;

import javafx.fxml.Initializable;


public class Controller implements Initializable {

    NetworkInterface[] devices = JpcapCaptor.getDeviceList();
    public ComboBox deviceList;

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
    public void closeApp () {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void handleAbout() {
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

}
