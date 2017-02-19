package sample;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.ComboBox;
import jpcap.*;

import javafx.fxml.Initializable;


public class Controller implements Initializable {

    NetworkInterface[] devices = JpcapCaptor.getDeviceList();
    public ComboBox deviceList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for(int i = 0; i < devices.length; i++){
            System.out.println(devices[i].name);
            deviceList.getItems().add(devices[i].name);
        }
        System.out.println("starting");
    }

    public void fillDeviceList(){
        System.out.println("ajhsdkf");
    }
}
