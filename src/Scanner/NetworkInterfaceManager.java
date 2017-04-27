/**
 * This file is part of the CS 4398 Software Engineering Project, Spring 2017 class -- Group 2
 * Group Members
 * @author Gregory Pontejos
 * @author Donovan Wells
 * @author Jason Villegas
 * @author Kingsley Nyaosi
 */

package Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

import java.util.ArrayList;

/**
 * All network interfaces are
 */
class NetworkInterfaceManager {

    private static NetworkInterface [] devices;
    private static ArrayList<String> netInterface;

    NetworkInterfaceManager () {
        refreshInterfaces();
    }

    /**
     * This function rescans for and populates all currently available network interfaces
     */
    static void refreshInterfaces() {
        netInterface = new ArrayList<>();
        devices = JpcapCaptor.getDeviceList();
        System.out.printf("Devices added: ");
        for (int i = 0; i < devices.length; i++) {
            System.out.printf("%s ", devices[i].name);
            netInterface.add(devices[i].name);
        }
        System.out.printf("\n");
    }

    /**
     * @return collection of currently available network interfaces
     */
    static ObservableList<String> activeInterfaces() {
        return FXCollections.observableArrayList(netInterface);
    }

    /**
     * @param deviceList the interface Combobox in the Main Controller
     * @return the network interface that will be used for the current capture
     */
    static NetworkInterface getSelectedInterface (ComboBox deviceList) {
        int r = deviceList.getSelectionModel().getSelectedIndex();

        System.out.printf("Device selected: %s\n", r);

        return devices[r];
    }

    /**
     * @return the network inferfaces that were captured
     */
    static NetworkInterface [] getInterfaceList() {
        return devices;
    }

}
