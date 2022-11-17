package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
public class ChapterContentBean {

    private ChapterItemBean chapter;

    @SerializedName("content")
    private String chapterContent;

    private ChapterContentSellBean sell;



    private UserFinanceBean finance;



    public String getChapterContent() {
        return chapterContent;
    }

    public void setChapterContent(String chapterContent) {
        this.chapterContent = chapterContent;
    }

    public ChapterItemBean getChapter() {
        return chapter;
    }

    public void setChapter(ChapterItemBean chapter) {
        this.chapter = chapter;
    }

    public ChapterContentSellBean getmSellBean() {
        return sell;
    }

    public void setmSellBean(ChapterContentSellBean mSellBean) {
        this.sell = mSellBean;
    }

    public UserFinanceBean getFinance() {
        return finance;
    }

    public void setFinance(UserFinanceBean finance) {
        this.finance = finance;
    }
}
