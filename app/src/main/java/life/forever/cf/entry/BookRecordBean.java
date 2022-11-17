package life.forever.cf.entry;

import life.forever.cf.sql.DBUtils;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

@Entity
public class BookRecordBean {

    @Id
    public String wid;



    @SerializedName("chapter_count")
    public int chapterCount;
    public String title;
    public String author;

    public String rec_id;

    @SerializedName(value = "parent_sort",alternate={"sort","sort_title"})
    @Transient
    public String parent_sort;

    public String h_url;

    public String cp_name;

    @Transient
    public float rank_s;
    @Transient
    public float rank_h;
    @Transient
    public float rank_p;

    public int is_finish;

    public int counts;
    public int updatetime;

    public int addtime = 0;
    public int readtime = 0;

    public int status;//书籍状态

    @Transient
    public int word_total;

    @SerializedName("description")
    @Transient
    public String description;


    public int chapterIndex;
    public int chapterCharIndex;

    public String chapterID;
    public String chapterName;


    public int timeStamp;


    public BookBean getRecordBook(){
        BookBean bookBean = new BookBean();
        bookBean.wid = this.wid;
        bookBean.title = this.title;
        bookBean.is_finish = this.is_finish;
        bookBean.chapterCount = this.chapterCount;
        bookBean.readtime = this.readtime;
        bookBean.chapterid = this.chapterID;
        bookBean.chapterindex = this.chapterIndex;
        bookBean.lastchapterpos = this.chapterCharIndex;
        bookBean.h_url = this.h_url;
        bookBean.status = this.status;
        bookBean.update_t = this.updatetime;
        return bookBean;

    }



    @Generated(hash = 1335248343)
    public BookRecordBean(String wid, int chapterCount, String title, String author,
                          String rec_id, String h_url, String cp_name, int is_finish, int counts,
                          int updatetime, int addtime, int readtime, int status, int chapterIndex,
                          int chapterCharIndex, String chapterID, String chapterName,
                          int timeStamp) {
        this.wid = wid;
        this.chapterCount = chapterCount;
        this.title = title;
        this.author = author;
        this.rec_id = rec_id;
        this.h_url = h_url;
        this.cp_name = cp_name;
        this.is_finish = is_finish;
        this.counts = counts;
        this.updatetime = updatetime;
        this.addtime = addtime;
        this.readtime = readtime;
        this.status = status;
        this.chapterIndex = chapterIndex;
        this.chapterCharIndex = chapterCharIndex;
        this.chapterID = chapterID;
        this.chapterName = chapterName;
        this.timeStamp = timeStamp;
    }
    @Generated(hash = 398068002)
    public BookRecordBean() {
    }
    public String getWid() {
        return this.wid;
    }
    public void setWid(String wid) {
        this.wid = wid;
    }
    public int getChapterIndex() {
        return this.chapterIndex;
    }
    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }
    public int getChapterCharIndex() {
        return this.chapterCharIndex;
    }
    public void setChapterCharIndex(int chapterCharIndex) {
        this.chapterCharIndex = chapterCharIndex;
    }
    public String getChapterName() {
        return this.chapterName;
    }
    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }
    public int getTimeStamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getChapterID() {
        return this.chapterID;
    }
    public void setChapterID(String chapterID) {
        this.chapterID = chapterID;
    }


    public static boolean hasBookRecordGreendao(BookBean bean){
        return DBUtils.getInstance().hasBookRecord(bean.wid);
    }
    public int getChapterCount() {
        return this.chapterCount;
    }
    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAuthor() {
        return this.author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getRec_id() {
        return this.rec_id;
    }
    public void setRec_id(String rec_id) {
        this.rec_id = rec_id;
    }
    public String getH_url() {
        return this.h_url;
    }
    public void setH_url(String h_url) {
        this.h_url = h_url;
    }
    public String getCp_name() {
        return this.cp_name;
    }
    public void setCp_name(String cp_name) {
        this.cp_name = cp_name;
    }
    public int getIs_finish() {
        return this.is_finish;
    }
    public void setIs_finish(int is_finish) {
        this.is_finish = is_finish;
    }
    public int getCounts() {
        return this.counts;
    }
    public void setCounts(int counts) {
        this.counts = counts;
    }
    public int getUpdatetime() {
        return this.updatetime;
    }
    public void setUpdatetime(int updatetime) {
        this.updatetime = updatetime;
    }
    public int getAddtime() {
        return this.addtime;
    }
    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }
    public int getReadtime() {
        return this.readtime;
    }
    public void setReadtime(int readtime) {
        this.readtime = readtime;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

}
