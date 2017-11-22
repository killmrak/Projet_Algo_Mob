import jbotsim.Message;
import jbotsim.Node;

public class BaseStation extends Node {
    @Override
    public void onStart() {
        setIcon("/images/server.png"); // to be adapted
        setSize(12);

        // Initiates tree construction with an empty message
        sendAll(new Message(null, "INIT"));
    }
}
