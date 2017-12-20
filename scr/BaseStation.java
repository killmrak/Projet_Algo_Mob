import jbotsim.Message;
import jbotsim.Node;

import java.util.ArrayList;

public class BaseStation extends Node {
    ArrayList<Sensor> lstEnfant = new ArrayList<Sensor>();
    LstTab lstTab;
    boolean bool = true;
    @Override
    public void onStart() {
        setIcon("/images/server.png"); // to be adapted
        setSize(12);

        // Initiates tree construction with an empty message
        sendAll(new Message(null, "INIT"));

    }

    @Override
    public void onMessage(Message message) {
        if (message.getFlag().equals("INIT")) {
            // retransmit up the tree
            lstEnfant.add((Sensor) message.getSender());
            System.out.println(((Sensor) message.getSender()).getID() + " MESSAGE");
        }
    }

    @Override
    public void onClock() {
        if(bool) {
            if (lstEnfant.size() /2 == this.getNeighbors().size()) {
                System.out.println(" MESSAGE2");
                sendAll(new Message(null, "NBCHILD"));

                lstTab = new LstTab(this);
                //lstTab.init(this);
                // System.out.println(toString());
                bool = false;
            }

        }
    }

    @Override
    public String toString() {
        StringBuffer tmp = new StringBuffer();
/*
        for(Sensor s : lstEnfant){
            tmp.append(s.toString() + "\n");
        }
*/
        tmp.append("1" + this.getNeighbors().size());
        tmp.append(" 2 " + lstEnfant.size());
        return tmp.toString();
    }
}
