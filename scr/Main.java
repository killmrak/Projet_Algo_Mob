import jbotsim.LinkResolver;
import jbotsim.Node;
import jbotsim.Topology;
import jbotsim.ui.JViewer;

public class Main {
    public static void main(String[] args) {
        // Create topology with clock not started
        Topology tp = new Topology(false);

        // Forbid communication between robots and sensors
        tp.setLinkResolver(new LinkResolver(){
            @Override
            public boolean isHeardBy(Node n1, Node n2) {
                if ((n1 instanceof Robot && n2 instanceof Sensor) ||
                        (n1 instanceof Sensor && n2 instanceof Robot))
                    return false;
                else
                    return super.isHeardBy(n1, n2);
            }
        });

        // Add sensors
        tp.setDefaultNodeModel(Sensor.class);
        tp.fromFile("/net/cremi/atruong/sensors.tp"); // to be adapted

        // Add base station
        tp.addNode(100, 80, new BaseStation());

        // Add two robots
        tp.addNode(90, 40, new Robot());
        tp.addNode(60, 80, new Robot());

        new JViewer(tp);
        tp.start(); // starts the clock
    }
}
