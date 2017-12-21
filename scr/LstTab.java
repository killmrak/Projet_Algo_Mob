import java.util.*;

public class LstTab {
    BaseStation bs;
    Map<Integer, List<Sensor>> arbreDesProfondeur = new HashMap<>();
    LstTab(BaseStation bs){
        this.bs = bs;
        init();
    }
    void init(){

        int tmp1 = 1;
        for (Sensor s : bs.lstEnfant)
            tmp1 += s.profondeur;


        initLstEnf();
        System.out.println(toString());
    }

    void initLstEnf(){
        for (Sensor s : bs.lstEnfant)
            initLstEnf(s, arbreDesProfondeur);
    }

    void initLstEnf(Sensor s, Map<Integer, List<Sensor>> arbre){
        if(arbre.containsKey(s.profondeur)){
            Set<Map.Entry<Integer, List<Sensor>>> setHm = arbreDesProfondeur.entrySet();
            Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
            while(it.hasNext()){
                Map.Entry<Integer, List<Sensor>> e = it.next();
                if(e.getKey() == s.profondeur){
                    e.getValue().add(s);
                    break;
                }
            }
        }
        else{
            List<Sensor> tmp = new ArrayList<>();
            tmp.add(s);
            arbre.put(s.profondeur, tmp);
        }
        if(s.lstEnfant.size() != 0)
            for (Sensor ss : s.lstEnfant)
                initLstEnf(ss, arbre);
    }

    @Override
    public String toString() {
        StringBuffer tmp = new StringBuffer();

        Set<Map.Entry<Integer, List<Sensor>>> setHm = arbreDesProfondeur.entrySet();
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