package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class BookBuyMultiPackage {

    @SerializedName("ResultData")
    private BookBuyMultiResult result;

    public BookBuyMultiResult getResult() {
        return result;
    }

    public void setResult(BookBuyMultiResult result) {
        this.result = result;
    }


}
