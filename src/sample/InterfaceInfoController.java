package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class InterfaceInfoController implements Initializable {

   NetworkInterface[] devices = JpcapCaptor.getDeviceList();

   @FXML
   TextArea textArea = new TextArea();

   @Override
   public void initialize(URL location, ResourceBundle resources) {
       for (int i = 0; i < devices.length; i++) {
           //print out its name and description
           textArea.appendText(i + ": "+ devices[i].name + "(" + devices[i].description + ")\n");

           //print out its datalink name and description
           textArea.appendText(" datalink: " + devices[i].datalink_name + "(" + devices[i].datalink_description +
                   ")\n");

           //print out its MAC address
           textArea.appendText(" MAC address:");
           for (byte b : devices[i].mac_address)
               textArea.appendText(Integer.toHexString(b&0xff) + ":");
           textArea.appendText("\n");

           //print out its IP address, subnet mask and broadcast address
           for (NetworkInterfaceAddress a : devices[i].addresses)
               textArea.appendText(" address:" + a.address + " " + a.subnet + " " + a.broadcast + "\n");

           textArea.appendText("\n");
        }

        textArea.setEditable(false);
   }
}
