package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class TaskItemBean {

    public String id;
    public String type;
    public String task_type;
    public String title;
    public String reward;
    public String description;
    public String giving_type;
    public String giving;
    public String experience;
    public String duration;
    public String limit;
    public int status;
    public String addtime;
    public String updatetime;
    public String auto;
    public String sort;
    @SerializedName("continue")
    public int ncontinue;
}
