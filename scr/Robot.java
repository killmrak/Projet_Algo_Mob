import jbotsim.Node;

import java.awt.geom.Point2D;
import java.util.*;

public class Robot extends WaypointNode {
    // Liste de tous les capteurs du réseau classé par nombre de fils (profondeur)
    private LstTab lstNodeBaseStation = null;
    // Liste des capteurs dont le robot aura la charge
    private LstTab treeSensor = new LstTab();
    private int numRobot = 0; // Numéro du robot
    private int nbRobot = 0; // Nombre de robots total en service

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
            if (numRobot == 0) {
                // 1er contact avec la base, récupère certaines informations
                numRobot = ((BaseStation) node).AddNumRobot();
            }
            // Actualise le nombre total de robots en service
            nbRobot = ((BaseStation) node).getNbRobot();
        } else if (node instanceof Robot)
            if (((Robot) node).nbRobot > this.nbRobot)
                this.nbRobot = ((Robot) node).nbRobot;
    }

    @Override
    public void onSensingOut(Node node) {
        if (node instanceof BaseStation) {
            this.lstNodeBaseStation = new LstTab((BaseStation) node);
            nbRobot = ((BaseStation) node).getNbRobot();
        }
    }

    /**
     * Méthode qui permet d'obtenir un intervalle d'action pour le robot
     * cela correspond à une liste de capteurs
     */
    private void update0() {
        int cpt = 0;
        Set<Map.Entry<Integer, List<Sensor>>> setHm = lstNodeBaseStation.getTreeOfDepth().entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Sensor>> e = it.next();
            if (checkIntervalSenssor(e.getKey().intValue(), lstNodeBaseStation.moyenne(),
                    cpt, lstNodeBaseStation.getTreeOfDepth().size()))
                for (int i = 0; i < e.getValue().size(); i++)
                    treeSensor.addLstEnf(e.getValue().get(i), treeSensor.getTreeOfDepth(), false);
            cpt++;
        }
        updateDes();
    }

    /**
     * Fonction qui permet de choisir l'intervalle d'action du robot par rapport à la profondeur
     * des capteurs (Possède une limite sur le nombre de robots)
     * Voici les différents cas
     * - Robot numéro 1 :
     * - S'il est seule, il parcoure tout
     * - S'il y a 2 robots, il parcourt les capteurs ont la profondeur est supérieur
     * à la moyenne de l'arbre
     * - S'il y a plus de 2 robots, il parcourt les 3 plus grosses profondeurs (arbitraire)
     * - Robot numéro 2 :
     * - S'il y a 2 robots, il parcourt les capteurs dont la profondeur est inférieur à la moyenne
     * - Autre cas :
     * - Chaque robot parcourt une zone donnée par cette formule dans le cas ou cpt > 2
     * (cpt >= (numRobot * 2) - 1 && cpt <= (numRobot *  ((double) nbIndice / nbRobot)) + 1
     *
     * @param key      : Profondeur du capteur (nombre de fils/petits-fils)
     * @param average  : Moyenne de l'arbre de capteur
     * @param cpt      : Numéro d'indice de la clé dans la Map
     * @param nbIndice : Nombre de clés dans la Map
     * @return : true si la key appartient à l'intervalle d'action du robot
     */
    private boolean checkIntervalSenssor(int key, double average, int cpt, int nbIndice) {
        if (numRobot == 1) {
            if (nbRobot == 1)
                return true;
            else if (nbRobot == 2)
                return key > average;
            return (cpt < 3);
        } else {
            if (nbRobot == 2)
                return key <= average;
            else
                return (cpt > 1 && cpt >= (numRobot * 2) - 1 &&
                        cpt < (numRobot * ((double) nbIndice / nbRobot)) + 1);
        }
    }

    /**
     * Méthode qui permet de donner une liste de capteurs à visiter
     */
    private void updateDes() {
        int cpt = 0;
        Point2D pointBase = (Point2D) this.getLocation().clone();
        Set<Map.Entry<Integer, List<Sensor>>> setHm = treeSensor.getTreeOfDepth().entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Sensor>> e = it.next();
            for (int i = 0; i < e.getValue().size(); i++) {
                pointBase = ImproveDestination(pointBase, e.getValue().get(i).getLocation());
                addDestination(pointBase.getX(), pointBase.getY());
            }
            if (nbRobot == 2) {
                if (numRobot == 1 && cpt == (treeSensor.getTreeOfDepth().size() / 2) + 1)
                    strategy0();
                else if (numRobot == 2 && cpt == (treeSensor.getTreeOfDepth().size() / 2))
                    strategy0();
            }
            cpt++;
        }
    }

    /**
     * Procédure qui ajoute une petite stratégie
     * Elle permet d'ajouter les capteurs les plus sensibles du robot
     * dans la liste des destinations du robot
     * <p>
     * Condition : N'avoir que 2 robots en circulation
     * Appeler la procédure une fois avoir atteint le milieu de l'insertion
     * des points de destination
     */
    private void strategy0() {
        int moitier = treeSensor.getTreeOfDepth().size() / 2;
        Point2D tmp1 = getLocation();
        int cpt = 0;
        Set<Map.Entry<Integer, List<Sensor>>> setHm = treeSensor.getTreeOfDepth().entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Sensor>> e = it.next();
            if (cpt < moitier) {
                for (int i = 0; i < e.getValue().size(); i++) {
                    Point2D tmp2 = e.getValue().get(i).getLocation();
                    tmp1 = ImproveDestination(tmp1, tmp2);
                    addDestination(tmp1.getX(), tmp1.getY());
                }
                cpt++;
            }
        }
    }

    /**
     * Fonction qui permet d'obtenir un "meilleur" point de destination par rapport
     * à la portée du robot
     * Algorithme : approche par le milieu succesif
     *
     * @param pointBase        : Point à partir du quelle le robot part pour rejoindre le point suivant
     * @param pointDestination : Nouvelle destination du robot
     * @return : Nouveau point calculer
     */
    private Point2D ImproveDestination(Point2D pointBase, Point2D pointDestination) {
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
            // Permet l'actualisation des données.
            addDestination(100, 80);
            addDestination(Math.random() * 300, Math.random() * 200);
            addDestination(100, 80);
        }

    }

    /**
     * Setter de lstNodeBaseStation
     *
     * @param lstNodeBaseStation
     */
    public void setLstNodeBaseStation(LstTab lstNodeBaseStation) {
        this.lstNodeBaseStation = lstNodeBaseStation;
    }
}
