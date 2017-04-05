package sample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import jpcap.packet.Packet;

import java.net.URL;
import java.util.ResourceBundle;

import static sample.PacketManager.getPacket;

/**
 * Created by gregorypontejos on 4/2/17.
 */
public class PacketViewerController implements Initializable {

    private PacketManager pManager= new PacketManager();
    private Packet currentPacket;
    private int packetNum;

    @FXML
    public TextArea packetInfo = new TextArea();
    public TextArea payloadRaw = new TextArea();
    public TextArea payloadText = new TextArea();
    public TextField inPacket = new TextField();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    /**
     * Handler to populate all packet information fields on button click
     */
    @FXML
    public void handleViewPacket() {
        if (inPacket.getText() != null) {
            packetNum = Integer.parseInt(inPacket.getText());
            System.out.printf("Packet Entered: %s\n", inPacket.getText());
            currentPacket = getPacket(packetNum);
            //System.out.printf("%s", currentPacket.toString());
            populatePacketInfo();
            populatePayloadRaw();
            populatePayloadText();
        } else {
            inPacket.clear();
        }
    }

    /**
     * Displays packet information in text field
     */
    private void populatePacketInfo() {
        packetInfo.setEditable(true);
        packetInfo.clear();
        packetInfo.appendText("Packet Number: ");
        packetInfo.appendText(Integer.toString(packetNum));
        packetInfo.appendText("\n");
        packetInfo.appendText(PacketManager.formatPacketInfo(currentPacket));
        packetInfo.appendText("\n");
        packetInfo.setEditable(false);
    }

    /**
     *
     */
    private void populatePayloadRaw() {
        StringBuilder f = new StringBuilder(2048);
        int tmp;

        payloadRaw.setEditable(true);
        payloadRaw.clear();

        for (int i = 0; i < currentPacket.data.length; i = i + 16){
            tmp = 0;
            f.append(String.format("%04X\t", i));
            while(tmp < 16 && ((i + tmp) < currentPacket.data.length)) {
                f.append(String.format("%02X", currentPacket.data[i + tmp]));
                f.append("\t");
                tmp++;
            }
            f.append("\n");
        }

        payloadRaw.appendText(f.toString().toUpperCase());
        payloadRaw.setEditable(false);
    }

    /**
     *
     */
    private void populatePayloadText() {
        StringBuilder f = new StringBuilder(2048);
        int tmp;

        payloadText.setEditable(true);
        payloadText.clear();

        for (int i = 0; i < currentPacket.data.length; i = i + 16){
            tmp = 0;
            f.append(String.format("%04X\t", i));
            while(tmp < 16 && ((i + tmp) < currentPacket.data.length)) {
                if (currentPacket.data[i + tmp] < 33 ||
                        currentPacket.data[i + tmp] > 126) {
                    f.append(".");
                } else {
                    f.append((char)currentPacket.data[i + tmp]);
                }
                tmp++;
            }
            f.append("\n");
        }

        payloadText.appendText(f.toString());
        payloadText.setEditable(false);
    }

    /**
     *
     */
    @FXML
    public void handleNextPacket() {
        inPacket.clear();
        if ((packetNum + 1) < PacketManager.getCaptureSize()) {
            packetNum++;
            System.out.printf("Next Packet displayed: %d\n", packetNum);
        }
        currentPacket = getPacket(packetNum);
        //System.out.printf("%s", currentPacket.toString());
        populatePacketInfo();
        populatePayloadRaw();
        populatePayloadText();
    }

    /**
     *
     */
    @FXML
    public void handlePreviousPacket() {
        inPacket.clear();
        if ((packetNum - 1) >= 0) {
            packetNum--;
            System.out.printf("Next Packet displayed: %d\n", packetNum);
        }
        currentPacket = getPacket(packetNum);
        //System.out.printf("%s", currentPacket.toString());
        populatePacketInfo();
        populatePayloadRaw();
        populatePayloadText();
    }
}
