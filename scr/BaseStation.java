import jbotsim.Message;
import jbotsim.Node;

import java.util.ArrayList;

public class BaseStation extends Node {
    ArrayList<Sensor> lstChild = new ArrayList<Sensor>();
    int nbRobot = 0;
    int nbTrueNeighbor = 0;
    LstTab lstTab;
    boolean initNbChild = true;
    int cpt = 0;
    @Override
    public void onStart() {
        setIcon("/images/server.png"); // to be adapted
        setSize(12);

        // Initiates tree construction with an empty message
        sendAll(new Message(null, "INIT"));
        for(Node neighbor : this.getNeighbors())
            if(neighbor instanceof Sensor)
                nbTrueNeighbor++;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getFlag().equals("INIT")) {
            // retransmit up the tree
            lstChild.add((Sensor) message.getSender());
            System.out.println(((Sensor) message.getSender()).getID() + " MESSAGE");
        }
        else if (message.getFlag().equals("RETURNCHILD")) {
            System.out.println("Base " + this.getID() + " " +  message.getSender().getID());
            cpt++;
            if(cpt == lstChild.size());
                //lstTab = new LstTab(this);
        }
    }

    @Override
    public void onClock() {
        if(initNbChild) {
            if (lstChild.size() == nbTrueNeighbor) {
                System.out.println(" MESSAGE2");
                sendAll(new Message(null, "NBCHILD"));

                //lstTab = new LstTab(this);
                //lstTab.init(this);
                // System.out.println(toString());
                initNbChild = false;
            }

        }
    }

    public int AddNumRobot(){
        System.out.println("ADD BASE ");
        return ++nbRobot;
    }

    public int getNbRobot(){
        return nbRobot;
    }

    public void onSensingIn(Node node) {
        if (node instanceof Robot) {
            ((Robot)node).lstNodeBaseStation = new LstTab(this);
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
        tmp.append(" 2 " + lstChild.size());
        return tmp.toString();
    }

}
