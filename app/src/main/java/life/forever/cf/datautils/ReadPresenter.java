package life.forever.cf.datautils;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.entry.AppUser;
import life.forever.cf.entry.TaskType;
import life.forever.cf.entry.BookBean;
import life.forever.cf.entry.BookUpdateTimeInfoBean;
import life.forever.cf.entry.BuyFinaceBean;
import life.forever.cf.entry.ChapterContentSellBean;
import life.forever.cf.entry.ChapterItemBean;
import life.forever.cf.entry.UserFinanceBean;
import life.forever.cf.entry.BookAutoPayTaskPackage;
import life.forever.cf.entry.BookAutoPayTaskResult;
import life.forever.cf.entry.BookBuyMultiPackage;
import life.forever.cf.entry.BookBuyMultiResult;
import life.forever.cf.entry.BookBuySinglePackage;
import life.forever.cf.entry.BookBuySingleResult;
import life.forever.cf.entry.BookCatalogResult;
import life.forever.cf.entry.BookChapterContentResult;
import life.forever.cf.entry.BookChapterPackage;
import life.forever.cf.entry.BookCommentListPackge;
import life.forever.cf.entry.BookDetailInfoPackge;
import life.forever.cf.entry.BookModifyInfoPackage;
import life.forever.cf.entry.BookModifyInfoResult;
import life.forever.cf.entry.BookMoreBuyInfoPackage;
import life.forever.cf.entry.BookRecommendListPackage;
import life.forever.cf.adapter.RxPresenter;
import life.forever.cf.entry.DataPointBean;
import life.forever.cf.entry.DataPointType;
import life.forever.cf.interfaces.ReadContract;
import life.forever.cf.sql.DBUtils;
import life.forever.cf.manage.NewMultiDownloadManager;
import life.forever.cf.internet.ReaderRemoteRepository;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import life.forever.cf.weight.ChapterPageStatusInfo;
import life.forever.cf.adapter.TxtChapter;
import life.forever.cf.activtiy.DF;
import life.forever.cf.activtiy.BookManager;
import life.forever.cf.activtiy.LogUtils;
import life.forever.cf.activtiy.ToastUtils;
import life.forever.cf.activtiy.UserTaskReceiveManager;
import life.forever.cf.interfaces.ReceviedRewardCallBack;

import org.greenrobot.greendao.async.AsyncOperation;
import org.greenrobot.greendao.async.AsyncOperationListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import life.forever.cf.publics.Constant;

public class ReadPresenter extends RxPresenter<ReadContract.View> implements ReadContract.Presenter {

    protected CompositeDisposable mDownChapterDisposable = new CompositeDisposable();


//    private BookBean mBookBean;

    private BookAutoPayTaskResult mAutoPayConfigBean;

    private boolean autoBuyFlag = false;

    private List<String> hasBuyChapters = new ArrayList<>();
    private List<String> sendContentChapters = new ArrayList<>();

    public boolean isAutoBuyFlag() {
        return autoBuyFlag;
    }

    public void setAutoBuyFlag(boolean autoBuyFlag) {
        this.autoBuyFlag = autoBuyFlag;
    }


    public ReadPresenter() {

    }

    @Override
    public void setAutoPaySelected(boolean autoPaySelected) {
        setAutoBuyFlag(autoPaySelected);
    }

    @Override
    public void clearDownDisposable() {
        if (mDownChapterDisposable != null) {
            mDownChapterDisposable.clear();
        }
    }


    private void getLocalChapterLists(BookBean collBookBean) {

        List<ChapterItemBean> tempLastChapterArray = new ArrayList<>();

        if(collBookBean == null)
        {
            mView.showCategory(tempLastChapterArray);
            mView.complete();
            return;
        }

        Disposable getLocalChaptersDispo = DBUtils.getInstance().
                getBookChaptersInRx(collBookBean.wid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        chapterItemBeans -> {
                            if (chapterItemBeans != null) {
                                tempLastChapterArray.clear();
                                tempLastChapterArray.addAll(chapterItemBeans);
                            }

                            if(tempLastChapterArray.size() <=0)
                            {
                                getNetOnlineChapterLists(collBookBean,tempLastChapterArray);
                            }else{


                                mView.showCategory(tempLastChapterArray);
                                mView.complete();

                                if(tempLastChapterArray.size() < collBookBean.chapterCount)
                                {
                                    if(tempLastChapterArray.get(0).sort>1)
                                    {
                                        tempLastChapterArray.clear();
                                    }

                                    getNetOnlineChapterLists(collBookBean,tempLastChapterArray);
                                }else{
                                    getBookModifyInfo(collBookBean);
                                }


                            }
                        },
                        (e) -> {
                            getNetOnlineChapterLists(collBookBean,tempLastChapterArray);
                        }
                );

        addDisposable(getLocalChaptersDispo);
    }

    private void getOnlyLocalChapterLists(BookBean collBookBean) {

        List<ChapterItemBean> tempLastChapterArray = new ArrayList<>();

        if(collBookBean == null)
        {
            mView.showCategory(tempLastChapterArray);
            mView.complete();
            return;
        }

        Disposable getLocalChaptersDispo = DBUtils.getInstance().
                getBookChaptersInRx(collBookBean.wid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        chapterItemBeans -> {
                            if (chapterItemBeans != null) {
                                tempLastChapterArray.clear();
                                tempLastChapterArray.addAll(chapterItemBeans);
                            }

                            mView.showCategory(tempLastChapterArray);
                            mView.complete();

//                            getBookModifyInfo(collBookBean);

                        },
                        (e) -> {
                            mView.showCategory(tempLastChapterArray);
                            mView.complete();
                        }
                );

        addDisposable(getLocalChaptersDispo);
    }


    private void getNetOnlineChapterLists(BookBean collBookBean,List<ChapterItemBean> tempLastChapterArray) {

        String wid = collBookBean.wid;
        int index = tempLastChapterArray.size();
        int count = -1;
        List<ChapterItemBean> finalTempLastChapterArray  = new ArrayList<>();
        if(collBookBean.chapterCount > 0 && tempLastChapterArray.size() > 0)
        {
            count = collBookBean.chapterCount - tempLastChapterArray.size();
        }
        if(tempLastChapterArray != null)
        {
            index = tempLastChapterArray.size();

            finalTempLastChapterArray.addAll(tempLastChapterArray);
        }

        boolean isAllOnline = false;
        if(count == -1)
        {
            isAllOnline = true;
        }

        final boolean lastAllOnline = isAllOnline;


        Disposable chapterDis = ReaderRemoteRepository.getInstance()
                .ReaderCatalog(wid, index, count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookChapterPackage chapterInfo) -> {
                            if (chapterInfo.getResult() != null) {
                                BookCatalogResult result = chapterInfo.getResult();

                                if (result.count == -1) {
                                    ToastUtils.show(result.msg);
                                }

                                life.forever.cf.activtiy.LogUtils.d("阅读页目录 ====== " + result.getCatalog().size());

                                List<ChapterItemBean> tagBeans = result.getCatalog();

                                if(tagBeans != null)
                                {
                                    finalTempLastChapterArray.addAll(tagBeans);
                                }

//                                mView.showCategory(finalTempLastChapterArray);


                                if (finalTempLastChapterArray.size() > 0) {


                                    BookUpdateTimeInfoBean tempTimeInfoBean = DBUtils.getInstance().getBookUpdateTimeBean(collBookBean.wid);

                                    if(tempTimeInfoBean != null && !lastAllOnline) {
                                        if (tempTimeInfoBean.update_time > result.update_time) {
                                            deleteBookChapterAndContentCache(collBookBean);
                                            List<ChapterItemBean> tempChapterArray  = new ArrayList<>();
                                            getNetOnlineChapterLists(collBookBean, tempChapterArray);

                                            BookUpdateTimeInfoBean timeInfoBean = new BookUpdateTimeInfoBean(collBookBean.wid,
                                                    collBookBean.chapterCount,
                                                    result.update_time);
                                            DBUtils.getInstance().saveBookUpdateTimeInfoBeanWithAsync(timeInfoBean);

                                            return;
                                        }
                                    }

                                    if(lastAllOnline && tempTimeInfoBean != null)
                                    {
                                        if(result.update_time < tempTimeInfoBean.update_time)
                                        {
                                            result.update_time = tempTimeInfoBean.update_time;
                                        }
                                    }


                                    BookUpdateTimeInfoBean timeInfoBean = new BookUpdateTimeInfoBean(collBookBean.wid,
                                            collBookBean.chapterCount,
                                            result.update_time);
                                    DBUtils.getInstance().saveBookUpdateTimeInfoBeanWithAsync(timeInfoBean);
                                    DBUtils.getInstance().saveBookChaptersWithAsync(finalTempLastChapterArray, collBookBean.wid, new AsyncOperationListener() {
                                        @Override
                                        public void onAsyncOperationCompleted(AsyncOperation operation) {
                                            getOnlyLocalChapterLists(collBookBean);
                                        }
                                    });
                                }else {
                                    mView.showCategory(finalTempLastChapterArray);
                                    mView.complete();
                                }

                            } else {
                                life.forever.cf.activtiy.LogUtils.d("阅读页目录 失败 ====== " + chapterInfo.reason);
                                mView.showCategory(finalTempLastChapterArray);
                            }

                            mView.complete();

                        },
                        (e) -> {
                            life.forever.cf.activtiy.LogUtils.e("目录加载失败=========" + e);
                            mView.showCategory(finalTempLastChapterArray);
                            mView.complete();
                        }

                );
        addDisposable(chapterDis);
    }

    @Override
    public void loadCategory(BookBean collBookBean) {
        //加载目录
        mView.showLoading();

        String wid = collBookBean.wid;

        getBookInfo(wid);

        getBookAutoTaskInfo(wid);

        getBookCommentList(wid);

        getBookRecommendList();

        getLocalChapterLists(collBookBean);
    }

    @Override
    public void loadChapter(String bookId, List<TxtChapter> bookChapterList) {
//        //加载章节内容
//        mView.showLoading();

//        if(mDownChapterDisposable != null) {
//            mDownChapterDisposable.clear();
//        }


        for (TxtChapter chapterItem :
                bookChapterList) {
//            downChapterContents(chapterItem);
            waitDownBuyChapter(chapterItem);
        }

    }


    /**
     * 获取章节内容
     *
     * @param chapter
     */
    private void downChapterContents(TxtChapter chapter) {
        String wid = chapter.getBookId();
        String cid = chapter.getChapterId();

        if (sendContentChapters.contains(cid)) {
            return;
        } else {
            sendContentChapters.add(cid);
        }

        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderChapterContent(wid, cid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookChapterContentResult chapterContentResult) -> {

                            if (chapterContentResult.getResult() != null) {
//                                String contentStr = chapterContentResult.getResult().getChapterContent();
//                                contentStr = AESUtils.decrypt(contentStr);
//
//                                BookManager.getInstance().saveChapterInfo(mReadBookBean.wid, "0", chapter.getTitle(),
//                                        contentStr);
//                            } else {
//
                                String contentStr = chapterContentResult.getResult().getChapterContent();


                                life.forever.cf.activtiy.LogUtils.e("oss 请求 =====" + chapter.getTitle());
                                life.forever.cf.activtiy.LogUtils.e("oss 请求 =====" + contentStr);


                                if (chapterContentResult.ServerNo.equals("SN000")) {
                                    chapter.setmChatperStatusInfo(null);
                                    hasBuyChapters.remove(chapter.getChapterId());
                                    downOssChapterContent(contentStr, chapter, true);
                                } else {
                                    ChapterPageStatusInfo statusInfo = new ChapterPageStatusInfo();
                                    statusInfo.setChapterName(chapter.getTitle());
                                    statusInfo.setChapterCid(chapter.getChapterId());
                                    if (chapterContentResult.getResult().getmSellBean() != null) {
                                        statusInfo.setChapterPrice(chapterContentResult.getResult().getmSellBean().getChapterPrice());
                                    }
                                    switch (chapterContentResult.ServerNo) {
                                        case "SN006"://SN006未登录,用户问题，重新登录SN004.equals(serverNo) || SN009
//                                        {
//                                            // TODO: 2021/10/5 测试购买状态
//                                            statusInfo.setMode(ChapterPageStatusInfo.PageStatusMode.PAY);
//                                        }
//                                        break;
                                        case "SN004":
                                        case "SN009": {
                                            statusInfo.setMode(ChapterPageStatusInfo.PageStatusMode.LOGIN);


                                        }
                                        break;
                                        case "SN031"://未购买
                                        {

                                            statusInfo.setMode(ChapterPageStatusInfo.PageStatusMode.PAY);


                                            ChapterContentSellBean sellBean = chapterContentResult.getResult().getmSellBean();
                                            UserFinanceBean userFinanceBean = chapterContentResult.getResult().getFinance();
                                            if (sellBean != null) {
                                                if (userFinanceBean != null) {
                                                    AppUser user = PlotRead.getAppUser();
                                                    user.money = userFinanceBean.money;
                                                    user.voucher = userFinanceBean.voucher;
                                                    if (user != null) {
                                                        SharedPreferencesUtil.putInt(user.config, Constant.KEY_MONEY, user.money);
                                                        SharedPreferencesUtil.putInt(user.config, Constant.KEY_VOUCHER, user.voucher);

                                                    }
                                                }
                                                if (PlotRead.getAppUser().voucher < sellBean.getChapterPrice()
                                                        && PlotRead.getAppUser().money < sellBean.getChapterPrice()) {
                                                    statusInfo.setMode(ChapterPageStatusInfo.PageStatusMode.LACK_BALANCE);
                                                }

                                            }


//                                            statusInfo.setMode(ChapterPageStatusInfo.PageStatusMode.ERROR);


                                        }
                                        break;
                                    }
                                    chapter.setmChatperStatusInfo(statusInfo);

                                    if (mAutoPayConfigBean != null) {
                                        statusInfo.setFirstPayNoticeStr(mAutoPayConfigBean.config.order_discount);
                                        statusInfo.setMultiPayNoticeStr(mAutoPayConfigBean.config.batch_discount);
                                        statusInfo.setAutoPayNoticeStr(mAutoPayConfigBean.coupon);
                                    }


//                                    if(chapter.getTitle().equals("Chapter 20 Your Reward"))
//                                    {
//                                        contentStr = contentStr +"11111";
//
//                                        downOssChapterContent(contentStr,chapter,false);
//                                    }else{


                                    boolean autoBuyFlag = isAutoBuyFlag();
                                    if (autoBuyFlag && statusInfo.getMode() == ChapterPageStatusInfo.PageStatusMode.PAY) {

//                                        if(chapter.getmChatperStatusInfo().getMode() == ChapterPageStatusInfo.PageStatusMode.PAY)
//                                        {
//                                            downOssChapterContent(contentStr, chapter, false);
//                                        }

                                        if (hasBuyChapters.contains(chapter.getChapterId())) {

                                            chapter.errorTimes++;
                                            if (chapter.errorTimes > 3) {
                                                statusInfo.setMode(ChapterPageStatusInfo.PageStatusMode.ERROR);
                                                statusInfo.setChapterName(chapter.getTitle());
                                                statusInfo.setChapterCid(chapter.getChapterId());
                                                statusInfo.setChapterContentStr(null);
                                                chapter.setmChatperStatusInfo(statusInfo);
                                                mView.errorChapter(chapter);
                                                sendContentChapters.remove(cid);

                                            } else {
                                                sendContentChapters.remove(cid);
                                                waitDownBuyChapter(chapter);
                                            }

//                                            downOssChapterContent(contentStr, chapter, false);
                                        } else {

                                            buySingleChapter(chapter.getBookId(), chapter);
                                        }
                                    } else {
                                        downOssChapterContent(contentStr, chapter, false);
                                    }


//                                    }

                                }

                            } else {
                                ChapterPageStatusInfo statusInfo = new ChapterPageStatusInfo();
                                statusInfo.setMode(ChapterPageStatusInfo.PageStatusMode.ERROR);
                                statusInfo.setChapterName(chapter.getTitle());
                                statusInfo.setChapterCid(chapter.getChapterId());
                                chapter.setmChatperStatusInfo(statusInfo);
                                mView.errorChapter(chapter);

                                sendContentChapters.remove(cid);
                            }
//
//                            finishChapter();

                            life.forever.cf.activtiy.LogUtils.e("内容请求 ===== " + chapter.getTitle());
                        },
                        (e) -> {
                            life.forever.cf.activtiy.LogUtils.e("内容错误=====" + e);
                            ChapterPageStatusInfo statusInfo = new ChapterPageStatusInfo();
                            statusInfo.setMode(ChapterPageStatusInfo.PageStatusMode.ERROR);
                            statusInfo.setChapterName(chapter.getTitle());
                            statusInfo.setChapterCid(chapter.getChapterId());
                            chapter.setmChatperStatusInfo(statusInfo);
                            mView.errorChapter(chapter);

                            sendContentChapters.remove(cid);
                        }
                );
        addDisposable(disposable);
    }

    @Override
    public void buySingleChapter(String bookId, TxtChapter chapter) {
        String wid = chapter.getBookId();
        String cid = chapter.getChapterId();


        life.forever.cf.activtiy.LogUtils.e("章节购买 ====== " + chapter.getTitle());

        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderBuySingleContent(wid, cid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookBuySinglePackage buySinglePackageResult) -> {

                            if (buySinglePackageResult != null && buySinglePackageResult.getResult() != null) {
                                sendContentChapters.remove(chapter.getChapterId());

                                BookBuySingleResult result = buySinglePackageResult.getResult();
                                if (result.status.equals("1")) {
                                    if (chapter != null && chapter.getChapterId() != null) {
                                        hasBuyChapters.add(chapter.getChapterId());
                                    }

                                    mView.buySuccessChapter(chapter, result.account, false);


//                                    downChapterContents(chapter);


                                    if (mView.getIsCurrentChapter(chapter)) {

                                    } else {
                                        waitDownBuyChapter(chapter);
                                    }

                                    DataPointBean settingPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_buy_chapter");
                                    settingPagePoint.setReadDataPoint(chapter.getBookId(),chapter.getChapterId()
                                            ,0,0,0,null);


                                    recevieDiscountReward();//领取任务奖励

                                    UserTaskReceiveManager.getInstance().freashAllAutoReceiveTasks(mView.getActivityContext(), TaskType.SUBSCRIBE);
                                } else {
                                    life.forever.cf.activtiy.LogUtils.e("单张购买 失败错误=====" + result.msg);

                                    mView.buyErrorChapter(chapter);
                                }


                            } else {
                                life.forever.cf.activtiy.LogUtils.e("单张购买 失败错误===== 222");

                                mView.buyErrorChapter(chapter);
                                sendContentChapters.remove(chapter.getChapterId());
                            }
                        },
                        (e) -> {
                            life.forever.cf.activtiy.LogUtils.e("单张购买 失败错误=====" + e);

                            mView.buyErrorChapter(chapter);
                            sendContentChapters.remove(chapter.getChapterId());

                        }
                );

        addDisposable(disposable);

    }

    private void waitDownBuyChapter(TxtChapter chapter) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                downChapterContents(chapter);
            }
        }).start();
    }


    @Override
    public void buyMultiChapters(String bookId, TxtChapter chapter, int count) {
        String wid = bookId;
        String cid = chapter.getChapterId();
        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderBuyMultiContent(wid, cid, count)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookBuyMultiPackage buyMultiPackageResult) -> {

                            if (buyMultiPackageResult != null && buyMultiPackageResult.getResult() != null) {
                                BookBuyMultiResult result = buyMultiPackageResult.getResult();
                                if (result.status.equals("1")) {
                                    mView.buySuccessChapter(chapter, 0, true);
                                    mView.freashHasBuyChapters(result.lists);


                                    for (String chapterID:
                                         result.lists) {

                                        ChapterItemBean itemBean = DBUtils.getInstance().getChapterItemBean(chapter.getBookId(),chapterID);

                                        if(itemBean != null && itemBean.getIsvip())
                                        {
                                            itemBean.setIsvip(false);
                                            DBUtils.getInstance().updateChapterItemBeanWithAsync(itemBean);
                                        }
                                    }



                                    // TODO: 2021/10/10 批量购买逻辑
                                    boolean isstate = PlotRead.getConfig().getBoolean(Constant.IS_AUTO_BUY, false);
                                    if(!isstate)//反正逻辑
                                    {
                                        NewMultiDownloadManager manager = new NewMultiDownloadManager(null);
                                        manager.startIdsDownload(chapter.getBookId(), result.lists);

                                        DataPointBean settingPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_download");
                                        settingPagePoint.setReadDataPoint(chapter.getBookId(),chapter.getChapterId()
                                                ,count,0,0,null);

                                    }

                                    DataPointBean settingPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_buy_chapter");
                                    settingPagePoint.setReadDataPoint(chapter.getBookId(),chapter.getChapterId()
                                            ,0,0,0,null);


                                    UserTaskReceiveManager.getInstance().freashAllAutoReceiveTasks(mView.getActivityContext(), TaskType.SUBSCRIBE);
                                } else {
                                    life.forever.cf.activtiy.LogUtils.e("批量购买 失败错误=====" + result.msg);

                                    mView.buyErrorChapter(chapter);
                                }
                            } else {
                                life.forever.cf.activtiy.LogUtils.e("批量购买 失败错误===== 222");
                                mView.buyErrorChapter(chapter);
                            }
                        },
                        (e) -> {
                            life.forever.cf.activtiy.LogUtils.e("批量购买 失败错误=====" + e);
                            mView.buyErrorChapter(chapter);
                        }
                );
        addDisposable(disposable);
    }

    private void downOssChapterContent(String ossUrlStr, TxtChapter chapter, Boolean cacheFlag) {
        Disposable chapterDispo = ReaderRemoteRepository.getInstance()
                .ReaderOssContent(ossUrlStr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> {
                            String contentStr = responseBody.string();
                            if (cacheFlag) {
                                String temp = DF.decrypt(contentStr);
                                if (temp == null || (temp != null && temp.length() <= 0)) {

                                } else {
                                    contentStr = temp;
                                }
                            }


                            life.forever.cf.activtiy.LogUtils.d("oss 阅读页内容 ====== " + chapter.getTitle() + " ======== " + contentStr);


                            if (cacheFlag) {
                                BookManager.getInstance().saveChapterInfo(chapter.getBookId(), "0", chapter.getChapterId(),
                                        contentStr);
                            } else {
                                if (chapter.getmChatperStatusInfo() != null) {
                                    chapter.getmChatperStatusInfo().setChapterContentStr(contentStr);
                                }
                            }

                            mView.finishChapter(chapter);

                            sendContentChapters.remove(chapter.getChapterId());
                        },
                        (e) -> {
                            life.forever.cf.activtiy.LogUtils.e(" oss 阅读页内容  加载失败====== " + e + " ==== " + chapter.getTitle());

                            ChapterPageStatusInfo statusInfo = new ChapterPageStatusInfo();
                            statusInfo.setMode(ChapterPageStatusInfo.PageStatusMode.ERROR);
                            statusInfo.setChapterName(chapter.getTitle());
                            statusInfo.setChapterCid(chapter.getChapterId());
                            chapter.setmChatperStatusInfo(statusInfo);
                            mView.errorChapter(chapter);

                            sendContentChapters.remove(chapter.getChapterId());

                        }
                );
        addDisposable(chapterDispo);
    }

    @Override
    public void getMoreBuyMultiInfo(String bookId, TxtChapter chapter) {
        String wid = chapter.getBookId();
        String cid = chapter.getChapterId();

        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderMoreBuyInfo(wid, cid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookMoreBuyInfoPackage moreBuyPackageResult) -> {

                            if (moreBuyPackageResult != null && moreBuyPackageResult.result != null) {
                                BookMoreBuyInfoPackage.BookMoreAllInfoResult result = moreBuyPackageResult.result;

                                if (result != null && moreBuyPackageResult.result.info != null) {
                                    BuyFinaceBean finaceBean = moreBuyPackageResult.result.info.finance;
                                    if (finaceBean != null) {
                                        AppUser user = PlotRead.getAppUser();
                                        user.money = finaceBean.money;
                                        user.voucher = finaceBean.voucher;
                                        if (user != null) {
                                            SharedPreferencesUtil.putInt(user.config, Constant.KEY_MONEY, user.money);
                                            SharedPreferencesUtil.putInt(user.config, Constant.KEY_VOUCHER, user.voucher);
                                        }
                                    }
                                }


                                if (mView != null) {
                                    mView.getSuccessMoreBuyInfo(moreBuyPackageResult);
                                }

                            }
                        },
                        (e) -> {
                            life.forever.cf.activtiy.LogUtils.e("批量购买内容  加载失败====== " + e + " ==== " + chapter.getTitle());
                        }
                );

        addDisposable(disposable);
    }


    @Override
    public void getBookAutoTaskInfo(String bookId) {
        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderBookAutoTaskInfo(bookId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookAutoPayTaskPackage autoPayTaskPackage) -> {

                            if (autoPayTaskPackage != null && autoPayTaskPackage.getResult() != null) {
                                if (autoPayTaskPackage.getResult().config != null) {
                                    mAutoPayConfigBean = autoPayTaskPackage.getResult();
                                }
                            }
                        },
                        (e) -> {
                            life.forever.cf.activtiy.LogUtils.e("自动购买人物信息  加载失败====== " + e + " ==== " + bookId);
                        }
                );

        addDisposable(disposable);
    }


    /**
     * 获取书籍详情
     */
    @Override
    public void getBookInfo(String wid) {
        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderBookInfo(wid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookDetailInfoPackge detailInfoPackge) -> {

                            if (detailInfoPackge != null && detailInfoPackge.getResult() != null) {
                                if (detailInfoPackge.getResult().info != null) {
                                    if (mView != null) {
                                        mView.getBookInfo(detailInfoPackge.getResult().info);
                                    }
                                }
                            }
                        },
                        (e) -> {
                            life.forever.cf.activtiy.LogUtils.e("获取书籍详情  失败====== " + e);
                        }
                );

        addDisposable(disposable);

    }

    /**
     * 获取书籍评论
     */

    @Override
    public void getBookCommentList(String bookId) {

        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderBookCommentList(bookId, 1, Constant.ONE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookCommentListPackge commentListPackge) -> {

                            if (commentListPackge != null && commentListPackge.getResult() != null) {
//                                if (detailInfoPackge.getResult().info != null) {
//                                    if (mView != null) {
//                                        mView.getBookInfo(detailInfoPackge.getResult().info);
//                                    }
//                                }
                                if (mView != null) {
                                    mView.getBookCommentList(commentListPackge.getResult());
                                }


                            }
                        },
                        (e) -> {
                            life.forever.cf.activtiy.LogUtils.e("获取书籍评论  失败====== " + e);
                        }
                );

        addDisposable(disposable);

    }

    private void getBookRecommendList() {
        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderBookRecommendList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookRecommendListPackage recommendListPackage) -> {

                            if (recommendListPackage != null && recommendListPackage.getResult() != null) {
                                if (mView != null && recommendListPackage.getResult().exit_rec != null) {
                                    mView.getBookRecommendList(recommendListPackage.getResult().exit_rec);
                                }
                            }
                        },
                        (e) -> {
                            life.forever.cf.activtiy.LogUtils.e("获取推荐书籍  失败====== " + e);
                        }
                );

        addDisposable(disposable);
    }

    private void getBookModifyInfo(BookBean mBookBean)
    {
        if(mBookBean == null)
        {
            return;
        }
        Disposable disposable = ReaderRemoteRepository.getInstance()
                .ReaderBookModifyInfo(mBookBean.wid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BookModifyInfoPackage modifyInfoPackage) -> {

                            if (modifyInfoPackage != null && modifyInfoPackage.getResult() != null) {

                                BookModifyInfoResult modifyInfoResult = modifyInfoPackage.getResult();

                                BookUpdateTimeInfoBean timeInfoBean = DBUtils.getInstance().getBookUpdateTimeBean(mBookBean.wid);

                                if(timeInfoBean != null)
                                {
                                    if(timeInfoBean.update_time < modifyInfoResult.update_time) {
                                        deleteBookChapterAndContentCache(mBookBean);

                                        List<ChapterItemBean> tempLastChapterArray = new ArrayList<>();
                                        getNetOnlineChapterLists(mBookBean,tempLastChapterArray);
                                        if(mView != null)
                                        {
                                            mView.showLoading();
                                        }
                                    }

                                    timeInfoBean.counts = modifyInfoResult.counts;
                                    timeInfoBean.update_time = modifyInfoResult.update_time;
                                }else{
                                    mBookBean.chapterCount = modifyInfoResult.counts;
                                    timeInfoBean = new BookUpdateTimeInfoBean(mBookBean.wid,
                                            modifyInfoResult.counts,
                                            modifyInfoResult.update_time);
                                }



                                DBUtils.getInstance().saveBookUpdateTimeInfoBeanWithAsync(timeInfoBean);
                            }
                        },
                        (e) -> {
                            life.forever.cf.activtiy.LogUtils.e("获取书籍 缓存更新  ====== " + e);
                        }
                );

        addDisposable(disposable);


    }

    private void deleteBookChapterAndContentCache(BookBean bookBean){
        if(bookBean != null)
        {
            BookManager.getInstance().deleteBookChapterAndContentCache(bookBean.wid);
        }
    }

    /**
     * 设置三次单章订阅后显示多章订阅
     */
//    private void setShowMore() {
//        int singleChapter = PlotRead.getConfig().getInt(m, 0);
//        {
//            switch (singleChapter) {
//                case 0:
//                    SharedPreferencesUtil.putInt(PlotRead.getConfig(), HRWork.wid + "", 1);
//                    break;
//                case 1:
//                    SharedPreferencesUtil.putInt(PlotRead.getConfig(), HRWork.wid + "", 2);
//                    break;
//                case 2:
//                    SharedPreferencesUtil.putInt(PlotRead.getConfig(), HRWork.wid + "", 3);
//                    isShowMore = true;
//                    mPageLoader.isShowMore(isShowMore);
//                    break;
//
//            }
//        }
//    }


    /**
     * 订阅领取奖励
     */

    private void  recevieDiscountReward()
    {
        if(mAutoPayConfigBean != null)
        {
            if(mAutoPayConfigBean.status == 1 && !mAutoPayConfigBean.isReceive) {
                UserTaskReceiveManager.getInstance().getAutoBuyTaskRecevice(mAutoPayConfigBean.task_id, new ReceviedRewardCallBack() {
                    @Override
                    public void getReceviedRewardResult(boolean isReceived) {
                        mAutoPayConfigBean.isReceive = isReceived;
                    }
                });
            }
        }
    }

    @Override
    public void uploadUserReadTime(int readTime) {
//        UserTaskReceiveManager.getInstance().updateUserReadTimeAndGetTaskRecevice(readTime);
    }


    private void testAppSign() {

        String wid = "3405";
        String cid = "12627";


        Disposable chapterDispo = ReaderRemoteRepository.getInstance()
                .ReaderTestSign(wid, cid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        responseBody -> {
                            String contentStr = responseBody.string();

                            life.forever.cf.activtiy.LogUtils.d("签名信息 ====== " + contentStr);
                        },
                        (e) -> {
                            LogUtils.e("签名信息  加载失败====== " + e);
                        }
                );
        addDisposable(chapterDispo);
    }

    public boolean getAutoPaySelected() {
        return isAutoBuyFlag();
    }

}
