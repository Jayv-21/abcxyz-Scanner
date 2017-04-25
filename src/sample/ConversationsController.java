/**
 * This file is part of the CS 4398 Software Engineering Project, Spring 2017 class -- Group 2
 * Group Members
 * @author Gregory Pontejos
 * @author Donovan Wells
 * @author Jason Villegas
 * @author Kingsley Nyaosi
 */

package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ConversationsController implements Initializable{

    //private PacketManager pManager= new PacketManager();

    @FXML
    public TextArea consoleOutput = new TextArea();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        PacketManager.populateConversations();
        populateConversationOutput();
    }

    /**
     *
     */
    private void populateConversationOutput() {
        StringBuilder f = new StringBuilder();
        consoleOutput.setEditable(true);
        ArrayList tempList;
        String sIP;
        String dIP;

        for (int i = 0; i < PacketManager.getConversationsSize(); i++) {
            tempList = PacketManager.getConversation(i);
            sIP = tempList.get(0).toString().substring(1);
            for (int j = 1; j < tempList.size(); j++) {
                dIP = tempList.get(j).toString().substring(1);
                f.append(sIP).append("\t-->\t").append(dIP).append("\n");
            }
        }

        consoleOutput.appendText(f.toString());
        consoleOutput.setEditable(false);
    }
}
