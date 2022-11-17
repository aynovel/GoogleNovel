package life.forever.cf.entry;

import life.forever.cf.adapter.BoolTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Garrett on 2018/11/17.
 * contact me krouky@outlook.com
 */
@Entity
public class ChapterItemBean {

    @Index
    public String bookID;

    @SerializedName("id")
    @Id
    private String chapterId;

    @SerializedName("title")
    private String chapterName;


    @JsonAdapter(BoolTypeAdapter.class)
    public Boolean isvip;

    @SerializedName(value = "sort",alternate={"order","pos"})
    public int sort;

    @Transient
    public String link;

    @SerializedName("content")
    @Transient
    public String content;

    @SerializedName("booktitle")
    @Transient
    public String booktitle;


    @Transient
    public int  errorCount = 0;


    @Transient
    public ChapterContentSellBean mSellBean;


    @Generated(hash = 1351436529)
    public ChapterItemBean(String bookID, String chapterId, String chapterName, Boolean isvip,
            int sort) {
        this.bookID = bookID;
        this.chapterId = chapterId;
        this.chapterName = chapterName;
        this.isvip = isvip;
        this.sort = sort;
    }

    @Generated(hash = 1042254985)
    public ChapterItemBean() {
    }

    public String getBookID() {
        return this.bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }

    public String getChapterId() {
        return this.chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return this.chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public Boolean getIsvip() {
        return this.isvip;
    }

    public void setIsvip(Boolean isvip) {
        this.isvip = isvip;
    }

    public int getOrder() {
        return this.sort;
    }

    public void setOrder(int order) {
        this.sort = order;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public HashMap<String,String> getNavitoHashMap(){
        HashMap<String,String> bookMap = new HashMap<String,String>();
        bookMap.put("title",this.chapterName);
        bookMap.put("bookID",this.bookID);
        bookMap.put("id",this.chapterId);
        bookMap.put("booktitle",this.booktitle);

        return bookMap;
    }


    public static ChapterItemBean getChapterItemFromBundle(HashMap<String,Object> map){

        JSONObject object = new JSONObject(map);

        Gson gson = new Gson();
        ChapterItemBean chapterItemBean = gson.fromJson(object.toString(),ChapterItemBean.class);

        return chapterItemBean;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getSort() {
        return this.sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

}
