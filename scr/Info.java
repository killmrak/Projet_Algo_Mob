public class Info{
    Sensor id;
    int nbEnfant;
    int getNbEnfantTotal;

    Info(Sensor id, int nbE, int getNb){
        this.id = id;
        this.nbEnfant = nbE;
        this.getNbEnfantTotal = getNb;
    }

    @Override
    public String toString() {
        return "Id : " + id.getID() + " nb enfant : " + nbEnfant + " profondeur : " + getNbEnfantTotal + "\n";
    }
}