import jbotsim.Message;
import jbotsim.Node;
import org.jcp.xml.dsig.internal.SignerOutputStream;

import java.awt.Color;
import java.util.ArrayList;

public class Sensor extends Node {
    public static double TEMPS_ENVOI = 0.04;
    Node parent = null;
    int battery = 255;
    ArrayList<Sensor> lstEnfant = new ArrayList<Sensor>();

    boolean bool = false;
    @Override
    public void onMessage(Message message) {
        // "INIT" flag : construction of the spanning tree
        // "SENSING" flag : transmission of the sensed values
        // You can use other flags for your algorithms
        if (message.getFlag().equals("INIT")) {
            // if not yet in the tree
            if (parent == null) {
                // enter the tree
                parent = message.getSender();
                getCommonLinkWith(parent).setWidth(4);
                // propagate further
                sendAll(message);
                send(parent, new Message(null,"CHILD"));

            }
        } else if (message.getFlag().equals("SENSING")) {
            // retransmit up the tree
            send(parent, message);
        }
        // I/a -> exploration de l'arbre
         else if (message.getFlag().equals("CHILD")) {
            // retransmit up the tree
            lstEnfant.add((Sensor) message.getSender());
        }
    }

    @Override
    public void send(Node destination, Message message) {
        if (battery > 0) {
            super.send(destination, message);
            battery--;
            updateColor();
            toString();
        }
        else{
            if (!bool){
                bool = true;
                System.out.println(toString());
            }
        }
    }

    @Override
    public void onClock() {
        if (parent != null) { // if already in the tree
            if (Math.random() < TEMPS_ENVOI) { // from time to time...
                double sensedValue = Math.random(); // sense a value
                send(parent, new Message(sensedValue, "SENSING")); // send it to parent
            }
        }
    }

    protected void updateColor() {
        setColor(battery == 0 ? Color.red : new Color(255 - battery, 255 - battery, 255));
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("Début : " + this.getID() + ", ");
        for(Sensor tmp : lstEnfant){
            result.append(tmp.getID() + ", ");
        }
        result.append("\n");
        return result.toString();
    }
}
