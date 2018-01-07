import java.util.*;

public class LstTab {
    Map<Integer, List<Sensor>> treeOfDepth = new TreeMap<>(Collections.reverseOrder());

    LstTab(BaseStation bs){
        addLstEnf(bs);
    }

    LstTab(){

    }

    /**
     * Méthode qui permet de lancer de manière recursive l'ajout de l'arbre
     * des profondeurs.
     * @param bs : Objet de type BaseStation
     */
    void addLstEnf(BaseStation bs){
        for (Sensor s : bs.lstChild)
            this.addLstEnf(s, treeOfDepth, true);
        //System.out.println(toString());
    }

    /**
     * Méthode qui permet d'ajouter un sensor dans un LstTab
     * @param s : Objet de type BaseStation
     * @param arbre : Map d'entier Sensor et Liste de Sensor
     * @param recusive : Option qui permet d'appeler la méthoe de façon récursive
     */
    void addLstEnf(Sensor s, Map<Integer, List<Sensor>> arbre, boolean recusive){
        if(arbre.containsKey(s.depth)){
            Set<Map.Entry<Integer, List<Sensor>>> setHm = treeOfDepth.entrySet();
            Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
            while(it.hasNext()){
                Map.Entry<Integer, List<Sensor>> e = it.next();
                if(e.getKey() == s.depth){
                    if(!e.getValue().contains((Sensor) s)) {
                        e.getValue().add(s);
                        break;
                    }
                }
            }
        }
        else{
            List<Sensor> tmp = new ArrayList<>();
            tmp.add(s);
            arbre.put(s.depth, tmp);
        }
        if (recusive) {
            if (s.lstChild.size() != 0)
                for (Sensor ss : s.lstChild)
                    addLstEnf(ss, arbre, true);
        }
    }

    /**
     * Fonction qui permet d'obtenir la moyenne d'au arbre
     * @return : La moyenne de l'arbre
     */
    public int moyenne() {
        int result = 0;
        int nb = 0;
        Set<Map.Entry<Integer, List<Sensor>>> setHm = treeOfDepth.entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while(it.hasNext()){
            Map.Entry<Integer, List<Sensor>> e = it.next();
            nb += e.getValue().size();
            result += (e.getKey().intValue() * e.getValue().size());
        }
        if(result == 0)
            return 0;
        return result / nb + 1;
    }

    @Override
    public String toString() {
        StringBuffer tmp = new StringBuffer();

        Set<Map.Entry<Integer, List<Sensor>>> setHm = treeOfDepth.entrySet();
        Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
        while(it.hasNext()){
            Map.Entry<Integer, List<Sensor>> e = it.next();
            tmp.append(e.getKey() + " : valeur : " );
            for (int i = 0; i < e.getValue().size(); i++){
                tmp.append(e.getValue().get(i).getID() + ", ");
            }
            tmp.append("\n");
        }
        return tmp.toString();
    }
}