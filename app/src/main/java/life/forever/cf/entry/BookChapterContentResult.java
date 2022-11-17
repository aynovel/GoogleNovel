package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class BookChapterContentResult extends BasePackageBean {
    @SerializedName("ResultData")
    private ChapterContentBean result;

    public ChapterContentBean getResult() {
        return result;
    }

    public void setResult(ChapterContentBean result) {
        this.result = result;
    }
}
