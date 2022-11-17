package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class BookAutoPayTaskPackage extends BasePackageBean{

    @SerializedName("ResultData")
    private BookAutoPayTaskResult result;

    public BookAutoPayTaskResult getResult() {
        return result;
    }

    public void setResult(BookAutoPayTaskResult result) {
        this.result = result;
    }
}
