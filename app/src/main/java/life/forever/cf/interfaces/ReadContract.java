package life.forever.cf.interfaces;

import android.app.Activity;

import life.forever.cf.entry.BookBean;
import life.forever.cf.entry.ChapterItemBean;
import life.forever.cf.entry.BookCommentListResult;
import life.forever.cf.entry.BookMoreBuyInfoPackage;
import life.forever.cf.entry.BookRecommendListResult;
import life.forever.cf.adapter.TxtChapter;

import java.util.List;

public interface ReadContract extends BaseContract {
    interface View extends BaseContract.BaseView{

        void getBookInfo(BookBean bookBean);

        void showCategory(List<ChapterItemBean> bookChapterList);

        void finishChapter(TxtChapter chapter);

        void errorChapter(TxtChapter chapter);

        void buySuccessChapter(TxtChapter chapter,int payType, boolean isMulti);

        void buyErrorChapter(TxtChapter chapter);

        void getSuccessMoreBuyInfo(BookMoreBuyInfoPackage infoPackage);

        boolean getIsCurrentChapter(TxtChapter chapter);

        void getBookCommentList(BookCommentListResult commentListResult);

        void freashHasBuyChapters(List<String> chapterIDs);

        void getBookRecommendList(BookRecommendListResult recommendListResult);

        Activity getActivityContext();
    }

    interface Presenter extends  BaseContract.BasePresenter<View>{
        void getBookInfo(String wid);
        void loadCategory(BookBean collBookBean);
        void loadChapter(String bookId,List<TxtChapter> bookChapterList);
        void buySingleChapter(String bookId,TxtChapter chapter);
        void getMoreBuyMultiInfo(String bookId,TxtChapter chapter);
        void buyMultiChapters(String bookId,TxtChapter chapter,int count);
        void getBookAutoTaskInfo(String bookId);
        void clearDownDisposable();

        void setAutoPaySelected(boolean autoPaySelected);


        void getBookCommentList(String bookId);

        void uploadUserReadTime(int readTime);


        boolean getAutoPaySelected();
    }

}
