package life.forever.cf.adapter;


import life.forever.cf.weight.ChapterPageStatusInfo;

public class TxtChapter{

    public String bookId;
    public String siteId;



    public String chapterId;


    public String link;


    public String title;

    public long start;
    public long end;


    public int textCount;

    public int chapterOrder;

    public  int errorTimes = 0;

    public void setChapterOrder(int chapterOrder) {
        this.chapterOrder = chapterOrder;
    }

    public int getChapterOrder() {
        return chapterOrder;
    }



    public String getBookId() {
        return bookId;
    }

    public void setBookId(String id) {
        this.bookId = id;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setTextCount(int count) {
        textCount = count;
    }

    public int getTextCount() {
        return textCount;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    private ChapterPageStatusInfo mChatperStatusInfo;


    public ChapterPageStatusInfo getmChatperStatusInfo() {
        return mChatperStatusInfo;
    }

    public void setmChatperStatusInfo(ChapterPageStatusInfo mChatperStatusInfo) {
        if(mChatperStatusInfo != null)
        {
            mChatperStatusInfo.setChapterOrder(this.chapterOrder);
        }
        this.mChatperStatusInfo = mChatperStatusInfo;
    }





    @Override
    public String toString() {
        return "TxtChapter{" +
                "title='" + title + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
