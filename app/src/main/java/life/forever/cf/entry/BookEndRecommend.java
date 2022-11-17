package life.forever.cf.entry;

import java.io.Serializable;
import java.util.List;


public class BookEndRecommend {

    public int id;
    public int rec_id;
    public int wid;
    public String sort;
    public String status;

    public String recimg;
    public String title;
    public String score;
    public String author;
    public String parent_sort;
    public String description;
    public String h_url;
    public String sortname;
    public List<Tag> tag;
    public String sort_name;
    public String isimg;
    public String isimgUrl;

    public static class Tag implements Serializable {
        public String id;
        public String tag;
    }
}
