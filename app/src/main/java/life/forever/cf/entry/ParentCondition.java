package life.forever.cf.entry;

import java.util.ArrayList;
import java.util.List;


public class ParentCondition {

    public String type;
    public String title;
    public List<ChildCondition> conditions = new ArrayList<>();
    public int checkId;

    @Override
    public String toString() {
        return "[ type = " + type + " title " + title + "]";
    }
}
