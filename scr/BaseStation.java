import jbotsim.Message;
import jbotsim.Node;

import java.util.ArrayList;

public class BaseStation extends Node {
    ArrayList<Sensor> lstEnfant = new ArrayList<Sensor>();
    @Override
    public void onStart() {
        setIcon("/images/server.png"); // to be adapted
        setSize(12);

        // Initiates tree construction with an empty message
        sendAll(new Message(null, "INIT"));

    }

    @Override
    public void onMessage(Message message) {
        if (message.getFlag().equals("CHILD")) {
            // retransmit up the tree
            lstEnfant.add((Sensor) message.getSender());
        }
    }

    @Override
    public void onClock() {
        if (lstEnfant.size() == this.getNeighbors().size()){
            sendAll(new Message(null,"NBCHILD"));
        }
    }
}
