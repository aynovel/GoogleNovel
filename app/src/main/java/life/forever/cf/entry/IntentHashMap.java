package life.forever.cf.entry;

import java.io.Serializable;
import java.util.HashMap;

public class IntentHashMap implements Serializable {
    private HashMap map;
    public HashMap getMap() {
        return map;
    }
    public void setMap(HashMap map) {
        this.map= map;
    }
}
