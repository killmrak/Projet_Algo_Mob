import java.util.*;

public class LstTab {
    Map<Integer, List<Info>> lstEnfants = new HashMap<Integer, List<Info>>();
    BaseStation bs;

    LstTab(BaseStation bs){
        this.bs = bs;
        init();
    }
    void init(){

        int tmp1 = 1;
        for (Sensor s : bs.lstEnfant)
            tmp1 += s.profondeur;


        initLstEnf();
    }

    void initLstEnf(){
        for (Sensor s : bs.lstEnfant)
            initLstEnf(s, 0);
    }

    void initLstEnf(Sensor s, int profobdeurArbre){
        List<Info> lst= new ArrayList<Info>();
        lstEnfants.put(profobdeurArbre, lst);
        //lstEnfants.put(profobdeurArbre, new Info(s.getID(), s.nbChild, s.profondeur));
        for (Sensor tmp : s.lstEnfant){
            initLstEnf(tmp, profobdeurArbre++);
        }
    }

    @Override
    public String toString() {
        StringBuffer tmp = new StringBuffer();
/*
        Set<Map.Entry<Integer, Info>> setHm = lstEnfants.entrySet();
        Iterator<Map.Entry<Integer, Info>> it = setHm.iterator();
        while(it.hasNext()){
            Map.Entry<Integer, Info> e = it.next();
            tmp.append(e.getKey() + " : " + e.getValue().toString());

        }*/
        return tmp.toString();
    }
}