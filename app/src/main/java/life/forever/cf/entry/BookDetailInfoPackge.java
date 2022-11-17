package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class BookDetailInfoPackge extends BasePackageBean{

    @SerializedName("ResultData")
    private BookDetailInfoResult result;

    public BookDetailInfoResult getResult() {
        return result;
    }

    public void setResult(BookDetailInfoResult result) {
        this.result = result;
    }

}
