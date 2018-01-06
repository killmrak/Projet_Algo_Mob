import jbotsim.Message;
import jbotsim.Node;
import org.jcp.xml.dsig.internal.SignerOutputStream;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Sensor extends Node {
    public static double TEMPS_ENVOI = 2;
    Node parent = null;
    int battery = 255;

    ArrayList<Sensor> lstEnfant = new ArrayList<Sensor>();
  //  ArrayList<Integer> nbChild = new ArrayList<Integer>();
    int nbRetourEnfant = 0; // compteur du nombre de RETOURENFANT
    int profondeur = 1; // Nombre de descendant totale

    boolean sensorMort = false;
    //boolean tousRetournementRecu = false;
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
                //sendAll(message);
                //send(parent, new Message(null,"INIT"));
                for (Node voisins : this.getNeighbors()) {
                    if (voisins instanceof Sensor)
                        if (((Sensor) voisins).parent == null) {
                            send(voisins, message);
                        }
                }
                if(parent instanceof BaseStation)
                    send(parent, new Message(null,"INIT"));
            }

            //lstEnfant.
        } else if (message.getFlag().equals("SENSING")) {
            // retransmit up the tree
            send(parent, message);
        }
        // I/a -> exploration de l'arbre
         else if (message.getFlag().equals("NBCHILD")) {
            if(lstEnfant.size() == 0){
                //System.out.println(countNbChild+ "  COUNT_START ");
                System.out.println("bib " + getID() + " " + getNeighbors().size() + " " + lstEnfant.size());
                for (Node n : this.getNeighbors()){
                    if(n instanceof Sensor) {
                        if (((Sensor) n).parent == this) {
                            lstEnfant.add((Sensor) n);
                            //System.out.println(getID() + " " + ((Sensor) n).getID() + " " +lstEnfant.size());
                            //System.out.println("SIZE, nbchild" + ((Sensor) n).lstEnfant.size());
                        }
                    }
                }

                for(Sensor s : lstEnfant){
                    send(s, message);
                }

                if(lstEnfant.size() == 0){
                    send(parent, new Message(null, "RETOURENFANT"));
                }
            }

        }
        else if (message.getFlag().equals("RETOURENFANT")) {
            //System.out.println("dedans/n");

            if(nbRetourEnfant != lstEnfant.size()){
                nbRetourEnfant++;
            }
            System.out.println( this.getID() + " " +  message.getSender().getID() + " " + this.lstEnfant.size() + " " + nbRetourEnfant);
             if (nbRetourEnfant == lstEnfant.size()){// && !tousRetournementRecu){
                //System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh ");
                for (Sensor s : lstEnfant)
                    profondeur += s.profondeur;
                send(parent, new Message(null, "RETOURENFANT"));
                //tousRetournementRecu = true;
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
        }
        else{
            if (!sensorMort){
                sensorMort = true;
                /*
                System.out.print("end " + getID() + " ");
                System.out.print(nbChild + " ");
                System.out.print(profondeur+ "\n");
                */
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
    /*
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
    */

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(profondeur);
        return result.toString();
    }

    int initChild(){
        int tmp = 0;
        if(this.lstEnfant.size() == 0){
            return 1;
        }
        else{
            for (Sensor s : lstEnfant){
                tmp += s.initChild();
            }
        }
        return tmp +1;
    }
}
