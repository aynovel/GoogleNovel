package life.forever.cf.entry;

import life.forever.cf.adapter.BoolTypeAdapter;
import com.google.gson.annotations.JsonAdapter;

public class BookAutoPayTaskResult{
    public int status;
    public String coupon;

    @JsonAdapter(BoolTypeAdapter.class)
    public boolean isReceive;

    public String msg;
    public String task_id;

    public BookAutoPayConfigBean config;
}
