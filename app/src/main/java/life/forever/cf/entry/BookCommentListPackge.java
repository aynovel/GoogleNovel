package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class BookCommentListPackge extends BasePackageBean{

    @SerializedName("ResultData")
    private BookCommentListResult result;

    public BookCommentListResult getResult() {
        return result;
    }

    public void setResult(BookCommentListResult result) {
        this.result = result;
    }
}
