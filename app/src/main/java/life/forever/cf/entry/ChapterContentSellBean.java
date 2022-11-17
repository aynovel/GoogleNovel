package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class ChapterContentSellBean {

    private String wid;

    @SerializedName("is_chapter")
    private Boolean isChapter;

    @SerializedName("price")
    private String price;

    @SerializedName("is_single")
    private Boolean isSingle;

    @SerializedName("single_price")
    private String singlePrice;

    @SerializedName("single_discount")
    private String singleDiscount;

    @SerializedName("is_month")
    private Boolean isMonth;

    @SerializedName("month_start")
    private String monthStart;

    @SerializedName("month_end")
    private String monthEnd;

    @SerializedName("is_discount")
    private Boolean isDiscount;

    @SerializedName("discount_start")
    private String discountStart;

    @SerializedName("discount_end")
    private String discountEnd;

    @SerializedName("is_activity")
    private Boolean isActivity;

    @SerializedName("activity_note")
    private String activityNote;

    @SerializedName("chapter_origin_price")
    private int chapterOriginPrice;

    @SerializedName("chapter_price")
    private int chapterPrice;


    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public Boolean getChapter() {
        return isChapter;
    }

    public void setChapter(Boolean chapter) {
        isChapter = chapter;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getSingle() {
        return isSingle;
    }

    public void setSingle(Boolean single) {
        isSingle = single;
    }

    public String getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(String singlePrice) {
        this.singlePrice = singlePrice;
    }

    public String getSingleDiscount() {
        return singleDiscount;
    }

    public void setSingleDiscount(String singleDiscount) {
        this.singleDiscount = singleDiscount;
    }

    public Boolean getMonth() {
        return isMonth;
    }

    public void setMonth(Boolean month) {
        isMonth = month;
    }

    public String getMonthStart() {
        return monthStart;
    }

    public void setMonthStart(String monthStart) {
        this.monthStart = monthStart;
    }

    public String getMonthEnd() {
        return monthEnd;
    }

    public void setMonthEnd(String monthEnd) {
        this.monthEnd = monthEnd;
    }

    public Boolean getDiscount() {
        return isDiscount;
    }

    public void setDiscount(Boolean discount) {
        isDiscount = discount;
    }

    public String getDiscountStart() {
        return discountStart;
    }

    public void setDiscountStart(String discountStart) {
        this.discountStart = discountStart;
    }

    public String getDiscountEnd() {
        return discountEnd;
    }

    public void setDiscountEnd(String discountEnd) {
        this.discountEnd = discountEnd;
    }

    public Boolean getActivity() {
        return isActivity;
    }

    public void setActivity(Boolean activity) {
        isActivity = activity;
    }

    public String getActivityNote() {
        return activityNote;
    }

    public void setActivityNote(String activityNote) {
        this.activityNote = activityNote;
    }

    public int getChapterOriginPrice() {
        return chapterOriginPrice;
    }

    public void setChapterOriginPrice(int chapterOriginPrice) {
        this.chapterOriginPrice = chapterOriginPrice;
    }

    public int getChapterPrice() {
        return chapterPrice;
    }

    public void setChapterPrice(int chapterPrice) {
        this.chapterPrice = chapterPrice;
    }






}
