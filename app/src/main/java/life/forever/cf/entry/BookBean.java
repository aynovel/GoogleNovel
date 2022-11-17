package life.forever.cf.entry;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Transient;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

@Entity
public class BookBean implements Comparable<BookBean> {

    public final static int BookBeanToReadFrom_Normal  = 0;

    public enum BookBeanType{
        BookBeanType_Normal,
        BookBeanType_Addmore,
        BookBeanType_AD,
    }

    @Index
    @Id
    public String wid;



//    @SerializedName("chapter_count")
    @SerializedName(value = "chapterCount",alternate={"chapter_count","counts"})
    public int chapterCount;
    public String title;
    public String author;

    public String rec_id;

    @SerializedName(value = "parent_sort",alternate={"sort","sort_title","parent_sort_title"})
    @Transient
    public String parent_sort;

    public String h_url;

    public String cp_name;

    @Transient
    public String subtitle;
    @Transient
    public String subtitle2;


    @Transient
    public float rank_s;
    @Transient
    public float rank_h;
    @Transient
    public float rank_p;

    public int is_finish;

//    public int counts;
    @SerializedName(value = "update_t",alternate={"updatetime","update_time"})
    public int update_t = 0;

    public int addtime = 0;
    public int readtime = 0;

    public int status;//书籍状态  2:书架下架；

    public int cp;//书籍来源；判断是否是阅文书籍

    //删除字段
    public boolean is_delete = false;
    public boolean is_update = false;
    public boolean is_recommend = false;

    @Transient
    public int word_total;

    @SerializedName("description")
    @Transient
    public String description;


    @Transient
    public String searchID;



    @Transient
    public BookBeanType beanType = BookBeanType.BookBeanType_Normal;
    @Transient
    public boolean editSelected = false;

    @Transient
    public int readFrom = BookBeanToReadFrom_Normal;

    @Transient
    public boolean hasExposure = false;

    @Transient
    public int click_from = 0;//1.书城,2.分类
    @Transient
    public String from_type;//来源类别，分类
    @Transient
    public String from_name;//来源名字


    @Transient
    private List<ChapterItemBean> bookChapterList;

    
    @Generated(hash = 269018259)
    public BookBean() {
    }
    @Generated(hash = 1463775802)
    public BookBean(String wid, int chapterCount, String title, String author, String rec_id, String h_url, String cp_name,
                    int is_finish, int update_t, int addtime, int readtime, int status, int cp, boolean is_delete,
                    boolean is_update, boolean is_recommend, int chapterindex, int lastchapterpos, String chapterid) {
        this.wid = wid;
        this.chapterCount = chapterCount;
        this.title = title;
        this.author = author;
        this.rec_id = rec_id;
        this.h_url = h_url;
        this.cp_name = cp_name;
        this.is_finish = is_finish;
        this.update_t = update_t;
        this.addtime = addtime;
        this.readtime = readtime;
        this.status = status;
        this.cp = cp;
        this.is_delete = is_delete;
        this.is_update = is_update;
        this.is_recommend = is_recommend;
        this.chapterindex = chapterindex;
        this.lastchapterpos = lastchapterpos;
        this.chapterid = chapterid;
    }
    public String getWid() {
        return this.wid;
    }
    public void setWid(String wid) {
        this.wid = wid;
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

    public int getUpdate_t() {
        return this.update_t;
    }
    public void setUpdate_t(int update_t) {
        this.update_t = update_t;
    }
    public boolean getIs_delete() {
        return this.is_delete;
    }
    public void setIs_delete(boolean is_delete) {
        this.is_delete = is_delete;
    }

    public boolean isYueWenBook(){
        boolean flag = false;
        if(cp == 132)
        {
            flag = true;
        }
        return flag;
    }


//    public  static void updateShelfGreenDao(BookBean bean){
//        LogUtils.d("cp ==== 111 = " + bean.cp);
//
//
//        List<BookBean> beans = new ArrayList<>();
//        beans.add(bean);
//        JuYueAppDBUitls.getInstance().updateBookShelfWithData(beans);
//    }
//
//    public  static void saveBookShelfGreenDao(BookBean bean){
//
//        LogUtils.d("cp ==== 222 = " + bean.cp);
//
//        BookRecordBean recordBean = JuYueAppDBUitls.getInstance().getBookRecord(bean.wid);
//
//        if(recordBean != null)
//        {
//            bean.readtime = recordBean.timeStamp;
//            bean.lastchapterpos = recordBean.chapterCharIndex;
//            bean.chapterindex = recordBean.chapterIndex;
//            bean.chapterid = recordBean.chapterID;
//        }
//
//
//        List<BookBean> beans = new ArrayList<>();
//        beans.add(bean);
//        JuYueAppDBUitls.getInstance().saveShelfBookBeansWithAsync(beans);
//    }
//
//
//    public static boolean insertBookShelfGreendao(BookBean bean){
//        BookRecordBean recordBean = JuYueAppDBUitls.getInstance().getBookRecord(bean.wid);
//
//        LogUtils.d("cp ==== 333 = " + bean.cp);
//
//        if(recordBean != null)
//        {
//            bean.readtime = recordBean.timeStamp;
//            bean.lastchapterpos = recordBean.chapterCharIndex;
//            bean.chapterindex = recordBean.chapterIndex;
//            bean.chapterid = recordBean.chapterID;
//        }else{
//            bean.addtime = TimeUtils.getCurrentTimestamp();
//            bean.readtime = TimeUtils.getCurrentTimestamp();
//        }
//        return JuYueAppDBUitls.getInstance().insertShelfBookBean(bean);
//    }
//
//    public static boolean hasBookShelfGreendao(BookBean bean){
//        return JuYueAppDBUitls.getInstance().hasShelfBookBean(bean);
//    }
//
//    public static boolean hasBookShelfGreendao(BookRecordBean bean){
//        BookBean readBook = new BookBean();
//        readBook.wid = bean.wid;
//        return JuYueAppDBUitls.getInstance().hasShelfBookBean(readBook);
//    }
//
//
//    public static BookBean getBookShelfGreendao(BookBean bean){
//        return JuYueAppDBUitls.getInstance().getShelfBookBean(bean);
//    }
//
//
//
//    public  static void saveBookShelfGreenDao(List<BookBean> beans){
//        JuYueAppDBUitls.getInstance().saveShelfBookBeansWithAsync(beans);
//    }
//
//
//
//    public  static void updateShelfGreenDao(List<BookBean> beans){
//        JuYueAppDBUitls.getInstance().updateBookShelfWithData(beans);
//    }
//
//    public  static void deleteDeleteShelfChaptersGreenDao(List<BookBean> beans){
//        for (BookBean item:
//             beans) {
//            JuYueAppDBUitls.getInstance().deleteChaptersWithBookID(item.wid);
//        }
//
//    }
//
//    public  static void cleanBookShelfGreenDao(){
//        JuYueAppDBUitls.getInstance().clearBookShelf();
//    }






    public int getReadtime() {
        return this.readtime;
    }
    public void setReadtime(int readtime) {
        this.readtime = readtime;
    }
    public boolean getIs_update() {
        return this.is_update;
    }
    public void setIs_update(boolean is_update) {
        this.is_update = is_update;
    }
    public boolean getIs_recommend() {
        return this.is_recommend;
    }
    public void setIs_recommend(boolean is_recommend) {
        this.is_recommend = is_recommend;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public int chapterindex = 0;//第几章
    public int lastchapterpos = 0;//章节某个字符，阅读进度
    public String chapterid = "";//章节ID


    public HashMap<String,String> getSnchronizeHashMap(){
        HashMap<String,String> bookMap = new HashMap<String,String>();
        bookMap.put("title",this.title);
        bookMap.put("wid",this.wid);
        bookMap.put("cover",this.h_url);
        bookMap.put("author",this.author);
        bookMap.put("readtime",""+this.readtime);
        bookMap.put("chapterCounts",""+this.chapterCount);
        bookMap.put("status",""+this.is_finish);
        if(this.is_delete)
        {
            bookMap.put("deleteflag","1");
        }else{
            bookMap.put("deleteflag","0");
        }

        bookMap.put("addtime",""+this.addtime);


        bookMap.put("sort",""+this.chapterindex);
        bookMap.put("lastchapterpos",""+this.lastchapterpos);
        bookMap.put("wtype",""+1);
        bookMap.put("lastchapter",this.chapterid);
        bookMap.put("cp",""+this.cp);





        return bookMap;

    }

    public HashMap<String,String> getNavitoHashMap(){
        HashMap<String,String> bookMap = new HashMap<String,String>();
        bookMap.put("title",this.title);
        bookMap.put("wid",this.wid);
        bookMap.put("h_url",this.h_url);
        bookMap.put("author",this.author);
        bookMap.put("readtime",""+this.readtime);
        bookMap.put("chapter_count",""+this.chapterCount);
        bookMap.put("is_finish",""+this.is_finish);
        bookMap.put("is_delete",""+this.is_delete);
        bookMap.put("addtime",""+this.addtime);


        bookMap.put("chapterindex",""+this.chapterindex);
        bookMap.put("lastchapterpos",""+this.lastchapterpos);
        bookMap.put("wtype",""+1);
        bookMap.put("chapterid",this.chapterid);
        bookMap.put("cp_name",this.cp_name);

        bookMap.put("readFrom",""+this.readFrom);

        bookMap.put("searchID",this.searchID);
        bookMap.put("rec_id",this.rec_id);

        bookMap.put("click_from",""+this.click_from);
        bookMap.put("from_type",this.from_type);
        bookMap.put("from_name",this.from_name);

        bookMap.put("subtitle",this.subtitle);
        bookMap.put("cp",""+this.cp);

        return bookMap;
    }

    public static BookBean getBookBeanFromBundle(HashMap<String,Object> map){

        BookBean bookBean = null;
        if(map != null)
        {
            JSONObject object = new JSONObject(map);

            Gson gson = new Gson();
            bookBean = gson.fromJson(object.toString(),BookBean.class);
        }


        return bookBean;
    }

    @Override
    public int compareTo(BookBean o) {//倒序排列
        float diff = this.rank_h - o.rank_h;
        if (diff > 0) {
            return -1;
        }else if (diff < 0) {
            return 1;
        }
        return 0; //相等为0
    }




    public int getAddtime() {
        return this.addtime;
    }
    public void setAddtime(int addtime) {
        this.addtime = addtime;
    }
    public int getChapterindex() {
        return this.chapterindex;
    }
    public void setChapterindex(int chapterindex) {
        this.chapterindex = chapterindex;
    }
    public int getLastchapterpos() {
        return this.lastchapterpos;
    }
    public void setLastchapterpos(int lastchapterpos) {
        this.lastchapterpos = lastchapterpos;
    }
    public String getChapterid() {
        return this.chapterid;
    }
    public void setChapterid(String chapterid) {
        this.chapterid = chapterid;
    }



    public void saveBigDataAddShelf(boolean searchFlag)
    {

    }
    public int getCp() {
        return this.cp;
    }
    public void setCp(int cp) {
        this.cp = cp;
    }


    public List<ChapterItemBean> getBookChapterList() {
        return bookChapterList;
    }

    public void setBookChapterList(List<ChapterItemBean> bookChapterList) {
        this.bookChapterList = bookChapterList;
    }

}
