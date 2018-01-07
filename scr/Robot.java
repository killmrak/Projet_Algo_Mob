import jbotsim.Node;

import java.awt.geom.Point2D;
import java.util.*;

public class Robot extends WaypointNode {
    // Liste de tous les capteurs du réseaux classé par nombre de fils (profondeur)
    LstTab lstNodeBaseStation = null;
    // Liste des capteurs dont le robots aura la charge
    LstTab TreeSensor = new LstTab();
    int numRobot = 0; // Numéro du robot
    int nbRobot = 0; // Nombre de robots totales en services

    BaseStation base = null;
    @Override
    public void onStart() {
        setIcon("/images/robot.png"); // to be adapted
        setSensingRange(30);
        onArrival();
    }

    @Override
    public void onSensingIn(Node node) {
        if (node instanceof Sensor)
            ((Sensor) node).battery = 255;
        else if (node instanceof BaseStation) {
            this.lstNodeBaseStation = new LstTab((BaseStation) node);
            if(numRobot == 0) {
                // 1er contact avec la base, rècupère certaines informations
                numRobot = ((BaseStation) node).AddNumRobot();
                base = ((BaseStation) node);
            }
            // Actualise le nombre totale de robots en service
            nbRobot = ((BaseStation) node).getNbRobot();
        }
        else if (node instanceof Robot) {
            if(((Robot) node).nbRobot > this.nbRobot)
                this.nbRobot = ((Robot) node).nbRobot;
        }
    }

    @Override
    public void onSensingOut(Node node) {
        if (node instanceof BaseStation) {
            this.lstNodeBaseStation = new LstTab((BaseStation) node);
            nbRobot = ((BaseStation) node).getNbRobot();
        }
    }

    /**
     * Méthode qui permet d'obtenir une liste de capteur apdater pour chaque robot
     */
    public void update0(){
        int cpt =0;

        Point2D tmp1 = (Point2D) this.getLocation().clone();
        Set<Map.Entry<Integer, List<Sensor>>> setHm = lstNodeBaseStation.treeOfDepth.entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Sensor>> e = it.next();
            if (checkIntervalSenssor(e.getKey().intValue(), lstNodeBaseStation.moyenne(),
                    cpt, lstNodeBaseStation.treeOfDepth.size()))
                for (int i = 0; i < e.getValue().size(); i++)
                    TreeSensor.addLstEnf(e.getValue().get(i), TreeSensor.treeOfDepth, false);
            cpt++;
        }
        updateDes();
    }

    /**
     * Fonction qui permet de choisir l'intervalle d'action du robot par rapport à la profondeure
     * des capteurs (Possède une limite sur le nombre de robots)
     * Voici les différents cas
     *      - Robot numéro 1 :
     *          - Si il est seule, il parcoure tout
     *          - Si il y a 2 robots, il parcoure les capteurs ont la profondeur est supérieure
     *              à la moyenne de l'arbre
     *          - Si il y a plus de 2 robots, il parcoure les 3 plus grosses profondeur (arbitaire)
     *      - Robot numéro 2 :
     *          - Si il y a 2 robots, il parcoure les capteurs ont la profondeur est inférieure
     *      - Autre cas :
     *          - Chaque robot parcours une zone donnée par cette formule dans le cas ou cpt > 2
     *              (cpt >= (numRobot * 2) - 1 && cpt <= (numRobot *  ((double) nbIndice / nbRobot)) + 1
     * @param key : Profondeur du capteur (nombre de fils/petits fils)
     * @param average : Moyenne de l'arbre de capteur
     * @param cpt : Numéro d'indice de la clé dans la Map
     * @param nbIndice : Nombre de clé dans la Map
     * @return
     */
    public boolean checkIntervalSenssor(int key, double average, int cpt, int nbIndice){
        //int roundNbIndiceNbRobot = Math.round(nbIndice / nbRobot);
        //System.out.println("Count indice : " +  ((double) nbIndice / nbRobot));
        if(numRobot == 1){
            if (nbRobot == 1)
                return true;
            else if (nbRobot == 2) {
                if (key > average) return true;
                else return false;
            }
            else{
                if(cpt < 3) return true;
                else return false;
            }
        }
        else {
            if(nbRobot == 2){
                if (key <= average) return true;
                else return false;
            }
            else {
                if(cpt > 2)
                    if (cpt >= (numRobot * 2) - 1 &&
                            cpt <= (numRobot *  ((double) nbIndice / nbRobot)) + 1) return true;
                return false;
            }
        }
    }

    /**
     * Méthode qui permet redonner une liste de cpateur à visiter
     */
    public void updateDes(){
        int moy = lstNodeBaseStation.moyenne();
        Point2D tmp1 = (Point2D) this.getLocation().clone();
        System.out.println("Average : " + moy);
        Set<Map.Entry<Integer, List<Sensor>>> setHm = TreeSensor.treeOfDepth.entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Sensor>> e = it.next();
                for (int i = 0; i < e.getValue().size(); i++) {
                    Point2D tmp2 = e.getValue().get(i).getLocation();
                    tmp1 = ImproveDestination(tmp1, tmp2);
                    addDestination(tmp1.getX(), tmp1.getY());
                }
        }
    }

    /**
     * Fonction qui permet d'obtenir un "meilleur" point de destination par rapport
     * à la portée du robot
     * Algorithme : approche par le milieu succesive
     * @param pointBase : Point à partir duquelle le robot part pour rejoidre le point suivant
     * @param pointDestination : Nouvelle destination du robot
     * @return : Nouveau points calculer
     */
    Point2D ImproveDestination(Point2D pointBase, Point2D pointDestination) {
        Point2D tmp1 = (Point2D) pointBase.clone();
        Point2D tmp2 = (Point2D) pointDestination.clone();
        while (tmp1.distance(tmp2) > getSensingRange()) {
            tmp1 = new Point2D.Double((tmp1.getX() + tmp2.getX()) / 2,
                    (tmp1.getY() + tmp2.getY()) / 2);

        }
        return tmp1;
    }

    @Override
    public void onArrival() {
        if (destinations.isEmpty() && lstNodeBaseStation != null)
            update0();
        if (destinations.size() != 0)
            destinations.peek();
        else {
            addDestination(100, 80);
            addDestination(Math.random() * 300, Math.random() * 200);
            addDestination(100, 80);
        }

    }
}
