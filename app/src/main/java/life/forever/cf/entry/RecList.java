package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class RecList {

    public String id;
    public String rec_id;
    public int wid;
    public String sort;
    public String status;

    public String recimg;
    public String rec_tag;
    public String intro;

    public String addtime;

    public int advertise_type;

    @SerializedName("advertise_data")
    public AdvertiseData advertise_data  = new AdvertiseData();

    public String title;

    public String channel;

    public String pack;
    public String config_num;
    public String score;
    public String author;
    public String sortname;
    public String parent_sort;
    public String update_time;
    public String description;
    public String h_url;
    public String wtype;
    public String js_dispatch;

}
