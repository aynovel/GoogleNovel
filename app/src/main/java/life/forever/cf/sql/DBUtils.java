package life.forever.cf.sql;

import life.forever.cf.entry.AutoPayBookBean;
import life.forever.cf.entry.AutoPayBookBeanDao;
import life.forever.cf.entry.BookMultiDownConfigBean;
import life.forever.cf.entry.BookMultiDownConfigBeanDao;
import life.forever.cf.entry.BookRecordBean;
import life.forever.cf.entry.BookRecordBeanDao;
import life.forever.cf.entry.BookUpdateTimeInfoBean;
import life.forever.cf.entry.BookUpdateTimeInfoBeanDao;
import life.forever.cf.entry.ChapterItemBean;
import life.forever.cf.activtiy.LogUtils;

import org.greenrobot.greendao.async.AsyncOperationListener;
import org.greenrobot.greendao.async.AsyncSession;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import life.forever.cf.entry.ChapterItemBeanDao;
import life.forever.cf.entry.DaoSession;

public class DBUtils {
    private static volatile DBUtils sInstance;

    private DaoSession mSession;




    private DBUtils(){

        mSession = DaoDbHelper.getInstance()
                .getSession();
    }

    public static DBUtils getInstance(){
        if (sInstance == null){
            synchronized (DBUtils.class){
                if (sInstance == null){
                    sInstance = new DBUtils();
                }
            }
        }
        return sInstance;
    }



    /**
     * 异步存储BookChapter
     * @param beans
     */
    public void saveBookChaptersWithAsync(List<ChapterItemBean> beans, String bookID, AsyncOperationListener operationListener){
        AsyncSession asyncSession = mSession.startAsyncSession();

        if(operationListener != null)
        {
            asyncSession.setListener(operationListener);
        }
        asyncSession.runInTx(
                        () -> {
                            //存储BookChapterBean
                            for (int i = 0; i < beans.size() ; i++) {
                                ChapterItemBean bean = beans.get(i);
                                if(bean.bookID == null && bookID != null)
                                {
                                    bean.setBookID(bookID);
                                }
                            }

                            mSession.getChapterItemBeanDao()
                                    .insertOrReplaceInTx(beans);
                            LogUtils.d("saveBookChaptersWithAsync: "+"进行存储");
                        }
                );
    }

    /**
     * 获取单个章节
     */
    public ChapterItemBean getChapterItemBean(String bookID,String chapterID)
    {
        return mSession.getChapterItemBeanDao()
                .queryBuilder()
                .where(ChapterItemBeanDao.Properties.BookID.eq(bookID))
                .where(ChapterItemBeanDao.Properties.ChapterId.eq(chapterID))
                .unique();
    }



    /**
     * 更新单个章节目录
     */
    public void updateChapterItemBeanWithAsync(ChapterItemBean bean){

        if(bean == null)
        {
            return;
        }


        //存储阅读记录
        mSession.getChapterItemBeanDao()
                .update(bean);
        LogUtils.d("updateChapterItemBeanWithAsync: "+"更新目录信息 进行存储");
    }



    /**
     * 获取书籍章节列表
     */
    public Single<List<ChapterItemBean>> getBookChaptersInRx(String bookId){
        return Single.create(new SingleOnSubscribe<List<ChapterItemBean>>() {
            @Override
            public void subscribe(SingleEmitter<List<ChapterItemBean>> e) throws Exception {
                List<ChapterItemBean> beans = mSession
                        .getChapterItemBeanDao()
                        .queryBuilder()
                        .where(ChapterItemBeanDao.Properties.BookID.eq(bookId))
                        .orderAsc(ChapterItemBeanDao.Properties.Sort)
                        .list();
                e.onSuccess(beans);
            }
        });
    }

    public void deleteChaptersWithBookID(String bookId){
        mSession.getChapterItemBeanDao()
                .queryBuilder()
                .where(ChapterItemBeanDao.Properties.BookID.eq(bookId))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
    }





    /**
     * 书籍阅读记录
     */
    public void saveBookRecordWithAsync(BookRecordBean bean){

        if(bean == null)
        {
            return;
        }


        //存储阅读记录
        mSession.getBookRecordBeanDao()
                .insertOrReplace(bean);
        LogUtils.d("saveBookRecordWithAsync: "+"进行存储");
    }


    /**
     * 书籍是否存在阅读记录
     */
    public boolean hasBookRecord(String bookID){

        boolean flag = false;

        flag = mSession.getBookRecordBeanDao()
                .queryBuilder()
                .where(BookRecordBeanDao.Properties.Wid.eq(bookID))
                .unique() == null ? false : true;

        return flag;

    }


    /**
     * 书籍阅读记录
     */
    public BookRecordBean getBookRecord(String bookID){
        return mSession.getBookRecordBeanDao()
                .queryBuilder()
                .where(BookRecordBeanDao.Properties.Wid.eq(bookID))
                .unique();
    }

    /**
     * 书籍阅读记录列表
     */
    public Single<List<BookRecordBean>> getAllBookRecord(){
        return Single.create(new SingleOnSubscribe<List<BookRecordBean>>() {
            @Override
            public void subscribe(SingleEmitter<List<BookRecordBean>> e) throws Exception {
                List<BookRecordBean> beans = mSession
                        .getBookRecordBeanDao()
                        .queryBuilder()
                        .orderDesc(BookRecordBeanDao.Properties.Readtime)
                        .list();
                e.onSuccess(beans);
            }
        });
    }

    public void deleteBookRecord(List<BookRecordBean>recordBeans){
        mSession.getBookRecordBeanDao().deleteInTx(recordBeans);
    }

    /**
     * 清空历史阅读记录
     */
    public void clearBookRecord(){
        mSession.getBookRecordBeanDao().deleteAll();
    }

    // TODO: 2021/10/5 1.8.1自动购买书籍
    /**
     * 书籍是否存在自动购买记录
     */
    public boolean hasAutoBuyBookRecord(String bookID){

        boolean flag = false;

        flag = mSession.getAutoPayBookBeanDao()
                .queryBuilder()
                .where(AutoPayBookBeanDao.Properties.Wid.eq(bookID))
                .unique() == null ? false : true;

        return flag;
    }

    public AutoPayBookBean getAutoBuyBookRecord(String bookID){
        return mSession.getAutoPayBookBeanDao()
                .queryBuilder()
                .where(AutoPayBookBeanDao.Properties.Wid.eq(bookID))
                .unique();
    }

    public void saveBookAutoPayRecordWithAsync(AutoPayBookBean bean){

        if(bean == null)
        {
            return;
        }


        //存储阅读记录
        mSession.getAutoPayBookBeanDao()
                .insertOrReplace(bean);
        LogUtils.d("saveBookAutoPayRecordWithAsync: "+"进行存储");
    }

    public void deleteBookAutoPayRecord(AutoPayBookBean bean){
        if(bean == null)
        {
            return;
        }
        mSession.getAutoPayBookBeanDao().delete(bean);
    }

    public Single<List<AutoPayBookBean>> getAllAutoPayBookRecord(){
        return Single.create(new SingleOnSubscribe<List<AutoPayBookBean>>() {
            @Override
            public void subscribe(SingleEmitter<List<AutoPayBookBean>> e) throws Exception {
                List<AutoPayBookBean> beans = mSession
                        .getAutoPayBookBeanDao()
                        .queryBuilder()
                        .orderDesc(AutoPayBookBeanDao.Properties.AddTime)
                        .list();
                e.onSuccess(beans);
            }
        });
    }


    /**
     * 批量下载后缓存配置
     */
    public BookMultiDownConfigBean getBookMultiDownConfigBean(String bookID){
        return mSession.getBookMultiDownConfigBeanDao()
                .queryBuilder()
                .where(BookMultiDownConfigBeanDao.Properties.Wid.eq(bookID))
                .unique();
    }

    public void saveBookMultiDownConfigWithAsync(BookMultiDownConfigBean bean){

        if(bean == null)
        {
            return;
        }
        
        //批量缓存设置
        mSession.getBookMultiDownConfigBeanDao()
                .insertOrReplace(bean);
        LogUtils.d("saveBookMultiDownConfigWithAsync: "+"进行存储");
    }


    /**
     * 获取书籍更新缓存标识
     */
    public BookUpdateTimeInfoBean getBookUpdateTimeBean(String bookID){
        return mSession.getBookUpdateTimeInfoBeanDao()
                .queryBuilder()
                .where(BookUpdateTimeInfoBeanDao.Properties.Wid.eq(bookID))
                .unique();
    }

    public void saveBookUpdateTimeInfoBeanWithAsync(BookUpdateTimeInfoBean bean){

        if(bean == null)
        {
            return;
        }


        //存储阅读记录
        mSession.getBookUpdateTimeInfoBeanDao()
                .insertOrReplace(bean);
        LogUtils.d("saveBookUpdateTimeInfoBeanWithAsync: "+"进行存储");
    }



}
