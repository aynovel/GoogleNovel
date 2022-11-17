package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookRecommendListResult {



    @SerializedName("rec_list")
    public List<RecList> rec_list;
}
