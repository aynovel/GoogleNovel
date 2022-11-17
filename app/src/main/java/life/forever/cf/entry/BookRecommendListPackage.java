package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class BookRecommendListPackage extends BasePackageBean{

    @SerializedName("ResultData")
    private BookRecommentListTypePakage result;

    public BookRecommentListTypePakage getResult() {
        return result;
    }

    public void setResult(BookRecommentListTypePakage result) {
        this.result = result;
    }


}
