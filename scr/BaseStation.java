import jbotsim.Message;
import jbotsim.Node;

import java.util.ArrayList;

public class BaseStation extends Node {
    private ArrayList<Sensor> lstChild = new ArrayList<Sensor>();
    private int nbRobot = 0; // Nombre de robots en circulation
    private int nbTrueNeighbor = 0; // Ne compte pas les robots
    private boolean initNbChild = true; // Permet de ne faire qu'un passage dans "NBCHILD"
    private int cpt = 0; // Compteur

    @Override
    public void onStart() {
        setIcon("/images/server.png"); // to be adapted
        setSize(12);

        // Initiates tree construction with an empty message
        sendAll(new Message(null, "INIT"));
        for (Node neighbor : this.getNeighbors())
            if (neighbor instanceof Sensor)
                nbTrueNeighbor++;
    }

    @Override
    public void onMessage(Message message) {
        if (message.getFlag().equals("INIT")) {
            // retransmit up the tree
            lstChild.add((Sensor) message.getSender());
        } else if (message.getFlag().equals("RETURNCHILD")) {
            // Retour de la collecte des profondeurs des enfants
            cpt++;
        }
    }

    @Override
    public void onClock() {
        if (initNbChild) {
            if (lstChild.size() == nbTrueNeighbor) {
                sendAll(new Message(null, "NBCHILD"));
                initNbChild = false;
            }

        }
    }

    /**
     * Fonction qui permet de compter le nombre de robots en circulation
     *
     * @return : Le numéro/identifiant du robot
     */
    public int AddNumRobot() {
        return ++nbRobot;
    }

    /**
     * Fonction qui permet de donner le nombre de robots trouver
     *
     * @return : Renvoie le nombre de robots
     */
    public int getNbRobot() {
        return nbRobot;
    }

    /**
     * Fonction qui retourne la liste des enfants
     *
     * @return : Liste des Sensors
     */
    public ArrayList<Sensor> getLstChild() {
        return lstChild;
    }

    @Override
    public void onSensingIn(Node node) {
        if (node instanceof Robot)
            ((Robot) node).setLstNodeBaseStation(new LstTab(this));
    }

    @Override
    public String toString() {
        StringBuffer tmp = new StringBuffer();
        tmp.append("1" + this.getNeighbors().size());
        tmp.append(" 2 " + lstChild.size());
        return tmp.toString();
    }

}
