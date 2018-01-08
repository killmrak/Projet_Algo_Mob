import java.util.*;

public class LstTab {
    private Map<Integer, List<Sensor>> treeOfDepth = new TreeMap<>(Collections.reverseOrder());

    LstTab(BaseStation bs) {
        addLstEnf(bs);
    }

    LstTab() {

    }

    /**
     * Getter de la Map
     *
     * @return : La Map
     */
    public Map<Integer, List<Sensor>> getTreeOfDepth() {
        return treeOfDepth;
    }

    /**
     * Méthode qui permet de lancer de manière recursive l'ajout de l'arbre
     * des profondeurs.
     *
     * @param bs : Objet de type BaseStation
     */
    private void addLstEnf(BaseStation bs) {
        for (Sensor s : bs.getLstChild())
            this.addLstEnf(s, treeOfDepth, true);
    }

    /**
     * Méthode qui permet d'ajouter un sensor dans un LstTab
     *
     * @param s        : Objet de type BaseStation
     * @param arbre    : Map d'entier Sensor et Liste de Sensor
     * @param recusive : Option qui permet d'appeler la méthoe de façon récursive
     */
    public void addLstEnf(Sensor s, Map<Integer, List<Sensor>> arbre, boolean recusive) {
        if (arbre.containsKey(s.getDepth())) {
            Set<Map.Entry<Integer, List<Sensor>>> setHm = treeOfDepth.entrySet();
            Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, List<Sensor>> e = it.next();
                if (e.getKey() == s.getDepth())
                    if (!e.getValue().contains((Sensor) s)) {
                        e.getValue().add(s);
                        break;
                    }
            }
        } else {
            List<Sensor> tmp = new ArrayList<>();
            tmp.add(s);
            arbre.put(s.getDepth(), tmp);
        }
        if (recusive)
            if (s.getLstChild().size() != 0)
                for (Sensor ss : s.getLstChild())
                    addLstEnf(ss, arbre, true);
    }

    /**
     * Fonction qui permet d'obtenir la moyenne d'au arbre
     *
     * @return : La moyenne de l'arbre
     */
    public int moyenne() {
        int result = 0;
        int nb = 0;
        Set<Map.Entry<Integer, List<Sensor>>> setHm = treeOfDepth.entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Sensor>> e = it.next();
            nb += e.getValue().size();
            result += (e.getKey().intValue() * e.getValue().size());
        }
        if (result == 0)
            return 0;
        return result / nb + 1;
    }

    /**
     * Fonction qui retourne le contenu de la Map
     *
     * @return : le contenu de la Map sous forme de string
     */
    @Override
    public String toString() {
        StringBuffer tmp = new StringBuffer();

        Set<Map.Entry<Integer, List<Sensor>>> setHm = treeOfDepth.entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Sensor>> e = it.next();
            tmp.append(e.getKey() + " : valeur : ");
            for (int i = 0; i < e.getValue().size(); i++)
                tmp.append(e.getValue().get(i).getID() + ", ");
            tmp.append("\n");
        }
        return tmp.toString();
    }
}