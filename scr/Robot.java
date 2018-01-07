import jbotsim.Node;

import java.awt.geom.Point2D;
import java.util.*;

public class Robot extends WaypointNode {
    LstTab lstNodeBaseStation = null;
    LstTab arbreSensor = new LstTab();
    int numRobot = 0;
    int nbRobot = 0;

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
            if(numRobot == 0)
                numRobot = ((BaseStation) node).ajoutNumRobot();
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
        }
    }

    public void update0(){
        int moy = lstNodeBaseStation.moyenne();
        int cpt =0;

        Point2D tmp1 = (Point2D) this.getLocation().clone();
        System.out.println("Moyenne : " + moy);
        Set<Map.Entry<Integer, List<Sensor>>> setHm = lstNodeBaseStation.arbreDesProfondeur.entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Sensor>> e = it.next();
            if (test(e.getKey().intValue(), moy, cpt)) {
                for (int i = 0; i < e.getValue().size(); i++) {
                    //Point2D tmp2 = e.getValue().get(i).getLocation();
                    //tmp1 = ameliorerDestination(tmp1, tmp2);
                    arbreSensor.initLstEnf(e.getValue().get(i), arbreSensor.arbreDesProfondeur);
                }
            }
            cpt++;
        }
    }

    public boolean test(int key, double moyenne, int cpt){
        if(nbRobot == 1){
            if (key > moyenne)
                return true;
            else
                return false;
        }
        else {
            if (numRobot == 1)
                if (key > moyenne)
                    return true;
                else
                    return false;
            else
            if (key < moyenne)
                return true;
            else
                return false;
        }


    }

    public void updateDes(){
        int moy = lstNodeBaseStation.moyenne();
        Point2D tmp1 = (Point2D) this.getLocation().clone();
        System.out.println("Moyenne : " + moy);
        Set<Map.Entry<Integer, List<Sensor>>> setHm = arbreSensor.arbreDesProfondeur.entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Sensor>> e = it.next();

            //if (e.getKey().intValue() < moy) {

                for (int i = 0; i < e.getValue().size(); i++) {
                    Point2D tmp2 = e.getValue().get(i).getLocation();
                    tmp1 = ameliorerDestination(tmp1, tmp2);
                    addDestination(tmp1.getX(), tmp1.getY());
                }
            //}
        }
    }

    /*
    public void updateDes() {
        int moy = lstNodeBaseStation.moyenne();
        Point2D tmp1 = (Point2D) this.getLocation().clone();
        System.out.println("Moyenne : " + moy);
        Set<Map.Entry<Integer, List<Sensor>>> setHm = lstNodeBaseStation.arbreDesProfondeur.entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Sensor>> e = it.next();

            if (e.getKey().intValue() < moy) {

                System.out.println("Key : " + e.getKey() + " soze : " + e.getValue().size());
                for (int i = 0; i < e.getValue().size(); i++) {
                    Point2D tmp2 = e.getValue().get(i).getLocation();
                    System.out.print("Base 0 : " + tmp1.toString() + tmp2.toString());
                    System.out.println(" Distance : " + tmp1.distance(tmp2));

                    tmp1 = ameliorerDestination(tmp1, tmp2);
                    System.out.print("Base 1 : " + tmp1.toString() + tmp2.toString());
                    System.out.println(" Distance : " + tmp1.distance(tmp2));

                    addDestination(tmp1.getX(), tmp2.getY());
                }
            }
        }
    }
    */

    Point2D ameliorerDestination(Point2D pointBase, Point2D pointDestination) {
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
        System.out.println("Num : " + numRobot + " nb : " + nbRobot);
        System.out.println("CALCUL : " + 8/3);
        System.out.println("Taille pile : " + destinations.size());
        if (destinations.isEmpty() && lstNodeBaseStation != null) {
            update0();
            updateDes();
        }
        if (destinations.size() != 0)
            destinations.peek();
        else
            addDestination(100, 80);
    }
}
