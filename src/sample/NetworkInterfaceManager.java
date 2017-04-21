/**
 * This file is part of the CS 4398 Software Engineering Project, Spring 2017 class -- Group 2
 * Group Members
 * @author Gregory Pontejos
 * @author Donovan Wells
 * @author Jason Villegas
 * @author Kingsley Nyaosi
 */

package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

import java.util.ArrayList;

/**
 * Created by gregorypontejos on 3/11/17.
 */
public class NetworkInterfaceManager {

    private static NetworkInterface [] devices;
    private static ArrayList<String> netInterface;

    NetworkInterfaceManager () {
        refreshInterfaces();
    }

    /**
     *
     */
    public static void refreshInterfaces() {
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
     *
     * @return
     */
    public static ObservableList<String> activeInterfaces() {
        return FXCollections.observableArrayList(netInterface);
    }

    /**
     *
     * @param deviceList
     * @return
     */
    public static NetworkInterface getSelectedInterface (ComboBox deviceList) {
        int r = deviceList.getSelectionModel().getSelectedIndex();

        System.out.printf("Device selected: %s\n", r);

        return devices[r];
    }

    /**
     *
     * @return
     */
    public static NetworkInterface [] getInterfaceList() {
        return devices;
    }

}
