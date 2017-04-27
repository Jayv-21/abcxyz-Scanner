/**
 * This file is part of the CS 4398 Software Engineering Project, Spring 2017 class -- Group 2
 * Group Members
 * @author Gregory Pontejos
 * @author Donovan Wells
 * @author Jason Villegas
 * @author Kingsley Nyaosi
 */

package Scanner;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;

import java.net.URL;
import java.util.ResourceBundle;

public class InterfaceInfoController implements Initializable {

   private NetworkInterfaceManager nInterface = new NetworkInterfaceManager();
   private NetworkInterface[] devices = NetworkInterfaceManager.getInterfaceList();

   @FXML
   TextArea textArea = new TextArea();

    /**
     * Initialized the state of the Network Interface Manager
     * @param location Resource location
     * @param resources Name of resource
     */
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
