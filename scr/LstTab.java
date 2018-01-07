import java.util.*;

public class LstTab {
    Map<Integer, List<Sensor>> TreeOfDepth = new TreeMap<>(Collections.reverseOrder());

    LstTab(BaseStation bs){

        init(bs);
    }

    LstTab(){

    }

    void init(BaseStation bs){
        int tmp1 = 1;
        for (Sensor s : bs.lstChild)
            tmp1 += s.depth;
        initLstEnf(bs);
        System.out.println(toString());
    }

    void initLstEnf(BaseStation bs){
        for (Sensor s : bs.lstChild)
            this.initLstEnf(s, TreeOfDepth);
    }

    void  initLstEnf(Sensor s, Map<Integer, List<Sensor>> arbre){
        if(arbre.containsKey(s.depth)){
            Set<Map.Entry<Integer, List<Sensor>>> setHm = TreeOfDepth.entrySet();
            Iterator<Map.Entry<Integer, List<Sensor>>> it = setHm.iterator();
            while(it.hasNext()){
                Map.Entry<Integer, List<Sensor>> e = it.next();
                if(e.getKey() == s.depth){
                    e.getValue().add(s);
                    break;
                }
            }
        }
        else{
            List<Sensor> tmp = new ArrayList<>();
            tmp.add(s);
            arbre.put(s.depth, tmp);
        }
        if(s.lstChild.size() != 0)
            for (Sensor ss : s.lstChild)
                initLstEnf(ss, arbre);
    }

    public int moyenne() {
        int result = 0;
        int nb = 0;
        Set<Map.Entry<Integer, List<Sensor>>> setHm = TreeOfDepth.entrySet();
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

        Set<Map.Entry<Integer, List<Sensor>>> setHm = TreeOfDepth.entrySet();
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