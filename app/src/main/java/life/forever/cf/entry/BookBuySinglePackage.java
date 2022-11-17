package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class BookBuySinglePackage extends BasePackageBean{

    @SerializedName("ResultData")
    private BookBuySingleResult result;


    public BookBuySingleResult getResult() {
        return result;
    }

    public void setResult(BookBuySingleResult result) {
        this.result = result;
    }


}
