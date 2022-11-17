package life.forever.cf.weight;

public class ChapterPageStatusInfo {


    public enum PageStatusMode {
        LOADING, ERROR, LOGIN, LACK_BALANCE, PAY, REWARD, RECOMMEND
    }
    private PageStatusMode mode = PageStatusMode.LOADING;

    private boolean isFirstPayFlag = false;

    private boolean isAutoPayFlag = false;

    private String firstPayNoticeStr = null;

    private String autoPayNoticeStr = null;



    private String multiPayNoticeStr = null;

    private String chapterContentStr;//章节内容

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    private String chapterName;

    public String getChapterCid() {
        return chapterCid;
    }

    public void setChapterCid(String chapterCid) {
        this.chapterCid = chapterCid;
    }

    private String chapterCid;

    private int chapterOrder;



    private int chapterPrice;


    public int getChapterOrder() {
        return chapterOrder;
    }

    public void setChapterOrder(int chapterOrder) {
        this.chapterOrder = chapterOrder;
    }




    public String getChapterContentStr() {
        return chapterContentStr;
    }

    public void setChapterContentStr(String chapterContentStr) {
        this.chapterContentStr = chapterContentStr;
    }

    public ChapterPageStatusInfo() {
        mode = PageStatusMode.LOADING;
    }

    public PageStatusMode getMode() {
        return mode;
    }

    public void setMode(PageStatusMode mode) {
        this.mode = mode;
    }

    public boolean isFirstPayFlag() {
        return isFirstPayFlag;
    }

    public void setFirstPayFlag(boolean firstPayFlag) {
        isFirstPayFlag = firstPayFlag;
    }

    public boolean isAutoPayFlag() {
        return isAutoPayFlag;
    }

    public void setAutoPayFlag(boolean autoPayFlag) {
        isAutoPayFlag = autoPayFlag;
    }

    public String getFirstPayNoticeStr() {
        return firstPayNoticeStr;
    }

    public void setFirstPayNoticeStr(String firstPayNoticeStr) {
        this.firstPayNoticeStr = firstPayNoticeStr;
    }

    public String getAutoPayNoticeStr() {
        return autoPayNoticeStr;
    }

    public void setAutoPayNoticeStr(String autoPayNoticeStr) {
        this.autoPayNoticeStr = autoPayNoticeStr;
    }

    public String getMultiPayNoticeStr() {
        return multiPayNoticeStr;
    }

    public void setMultiPayNoticeStr(String multiPayNoticeStr) {
        this.multiPayNoticeStr = multiPayNoticeStr;
    }

    public int getChapterPrice() {
        return chapterPrice;
    }

    public void setChapterPrice(int chapterPrice) {
        this.chapterPrice = chapterPrice;
    }

}
