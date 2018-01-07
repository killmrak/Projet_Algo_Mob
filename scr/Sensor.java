import jbotsim.Message;
import jbotsim.Node;
import org.jcp.xml.dsig.internal.SignerOutputStream;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Sensor extends Node {
    Node parent = null;
    int battery = 255;

    ArrayList<Sensor> lstChild = new ArrayList<Sensor>();
    //  ArrayList<Integer> nbChild = new ArrayList<Integer>();
    int nbReturnChild = 0; // compteur du nombre de ReturnChild
    int depth = 1; // Nombre de descendant totale
    boolean sensorDead = false;

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
                // Envoie du message "INIT" à tous ceux qui n'ont pas de parent, diminuer les messages en trop
                for (Node neighbors : this.getNeighbors()) {
                    if (neighbors instanceof Sensor)
                        if (((Sensor) neighbors).parent == null)
                            send(neighbors, message);
                }
                // Le comptage des enfants pour les sensors se faire dans "NBCHILD"
                if (parent instanceof BaseStation)
                    send(parent, new Message(null, "INIT"));
            }
        } else if (message.getFlag().equals("SENSING")) {
            // retransmit up the tree
            send(parent, message);
        }
        // I/a -> exploration de l'arbre
        else if (message.getFlag().equals("NBCHILD")) {
            if (lstChild.size() == 0) {
                // Compte le nombre d'enfant d'un noeud
                for (Node n : this.getNeighbors())
                    if (n instanceof Sensor)
                        if (((Sensor) n).parent == this)
                            lstChild.add((Sensor) n);
                for (Sensor s : lstChild)
                    send(s, message);
                // Cas d'une feuille, commence le rappatriement des informations de l'eploration
                if (lstChild.size() == 0)
                    send(parent, new Message(null, "ReturnChild"));
            }

        } else if (message.getFlag().equals("ReturnChild")) {
            // Vérifie que tous les enfants existant ont terminer l'exploration de leurs enfants
            if (nbReturnChild != lstChild.size())
                nbReturnChild++;
            if (nbReturnChild == lstChild.size()) {
                // Calcul de la profondeur du noeurd (nombre toral d'enfant et sous enfant)
                for (Sensor s : lstChild)
                    depth += s.depth;
                send(parent, new Message(null, "ReturnChild"));
            }
        }
    }

    @Override
    public void send(Node destination, Message message) {
        if (battery > 0) {
            super.send(destination, message);
            battery--;
            updateColor();
            toString();
        } else {
            if (!sensorDead) {
                sensorDead = true;
                /*
                System.out.print("end " + getID() + " ");
                System.out.print(nbChild + " ");
                System.out.print(depth+ "\n");
                */
            }
        }
    }

    @Override
    public void onClock() {
        if (parent != null) { // if already in the tree
            if (Math.random() < 0.02) { // from time to time...
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
        result.append(depth);
        return result.toString();
    }
/*
    // Première méthode d'exploration des enfants
    int initChild(){
        int tmp = 0;
        if(this.lstChild.size() == 0){
            return 1;
        }
        else{
            for (Sensor s : lstChild){
                tmp += s.initChild();
            }
        }
        return tmp +1;
    }
    */
}
