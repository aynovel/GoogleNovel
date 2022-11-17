package life.forever.cf.weight;

import static life.forever.cf.interfaces.PageMode.SCROLL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import life.forever.cf.entry.BookBean;
import life.forever.cf.interfaces.PageMode;
import life.forever.cf.activtiy.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import life.forever.cf.interfaces.BusC;


public class PageView extends FrameLayout {

    private enum TURN_PAGE_TYPE{
        TURN_PAGE_TYPE_CUR, TURN_PAGE_TYPE_PREV, TURN_PAGE_TYPE_NEXT
    }

    private enum AD_TYPE{
        AD_TYPE_INTER, AD_TYPE_PAGE, AD_TYPE_NONE
    }

    private final static String TAG = "BookPageWidget";

    private int mViewWidth = 0; // 当前View的宽
    private int mViewHeight = 0; // 当前View的高

    private int mStartX = 0;
    private int mStartY = 0;
    private boolean isMove = false;
    // 初始化参数
    private int mBgColor = 0xFFCEC29C;
    private PageMode mPageMode = PageMode.SIMULATION;
    // 是否允许点击
    private boolean canTouch = true;
    // 唤醒菜单的区域
    private RectF mCenterRect = null;
    private boolean isPrepare;

    private Bitmap mBitmap;
    private Canvas mCanvas;

    private View mAdView = null; //当前页的广告View
    private YYFrame mFrame = null;    //当前页的广告Frame
    private AD_TYPE mAdType = AD_TYPE.AD_TYPE_NONE;   //当前页的广告类型

    private View mPrevAdView = null;   //前一页的广告View
    private YYFrame mPrevFrame = null;
    private AD_TYPE mPrevAdType = AD_TYPE.AD_TYPE_NONE;

    private View mNextAdView = null;   //后一页的广告View
    private YYFrame mNextFrame = null;
    private AD_TYPE mNextAdType = AD_TYPE.AD_TYPE_NONE;

    private TURN_PAGE_TYPE mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_CUR;

    private boolean shouldDraw = true;
    private int drawCount = 0;

    private boolean mTouchReloadBtn = false;

    private boolean mTouchPageStatusBtn = false;

    private boolean mTouchPageStatusAutoPayBtn = false;

    private boolean mTouchPageStatuFlag = false;
    private boolean mTouchPage = false;
    private boolean mTouchPageAutoPayStatuFlag = false;



    // 动画类
    private PageAnimation mPageAnim;
    // 动画监听类
    private PageAnimation.OnPageChangeListener mPageAnimListener = new PageAnimation.OnPageChangeListener() {
        @Override
        public boolean hasPrev() {
            mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_PREV;
            return PageView.this.hasPrevPage();
        }

        @Override
        public boolean hasNext() {
            mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_NEXT;
            return PageView.this.hasNextPage();
        }

        @Override
        public void pageCancel() {
            PageView.this.pageCancel();
        }

        @Override
        public void moveTurnPageBegin(){
            if (mTurnPageListener != null){
                mTurnPageListener.moveTurnPageBegin();
            }
        }

        @Override
        public void moveTurnPageFinished(boolean bFinished){
            if (mTurnPageListener != null){
                mTurnPageListener.moveTurnPageFinished(bFinished);
            }
            PageView.this.turnPageFinished();
        }

        @Override
        public void tapTurnPage(){
            if (mTurnPageListener != null){
                mTurnPageListener.tapTurnPage();
            }
//            PageView.this.turnPageFinished();
        }

        @Override
        public void autoReadPageFinish() {
            if (mAutoReadListener != null){
                mAutoReadListener.autoReadPageFinished();
            }else {
                ToastUtils.show("自动阅读已结束或出错请退出阅读重试");
            }
        }
    };

    public interface AutoReadListener{
        void autoReadStart();

        void autoReadPageFinished();

        void autoReadFinished();
    }

    //用于自动翻页回调
    private AutoReadListener mAutoReadListener = null;

    public PageMode getmPageMode() {
        return mPageMode;
    }

    public void setmAutoReadListener(@Nullable AutoReadListener autoReadListener) {
        this.mAutoReadListener = autoReadListener;
    }

    //信息流广告翻页监听
    private TurnPageListener mTurnPageListener = null;
    public void setmTurnPageListener(@Nullable TurnPageListener mTurnPageListener) {
        this.mTurnPageListener = mTurnPageListener;
    }

    //点击监听
    private TouchListener mTouchListener;
    //内容加载器
    private PageLoader mPageLoader;

    public PageView(Context context) {
        this(context, null);
    }

    public PageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        //千万不要关闭硬件加速，否则页面渲染会很卡
//        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

        isPrepare = true;

        if (mPageLoader != null) {
            mPageLoader.prepareDisplay(w, h);
        }
    }

    //设置翻页的模式
    void setPageMode(PageMode pageMode) {
        //视图未初始化的时候，禁止调用
        if (mViewWidth == 0 || mViewHeight == 0) return;

        Bitmap curBitmap = null;
        Bitmap nextBitmap = null;

        if (mPageMode == SCROLL){
            //scroll 切换到其他, 什么都不做
        }else if(pageMode == SCROLL){
            //其他切换到scroll，释放内存，特别是图片内存
            if (mPageAnim != null){
                mPageAnim.clear();
                mPageAnim = null;
            }
        }else {
            //非scroll的动画互相切换，共用bitmap,节省图片内存，防oom
            if (mPageAnim != null){
                curBitmap = mPageAnim.getmCurBitmap();
                nextBitmap = mPageAnim.getNextBitmap();
            }
        }
        mPageMode = pageMode;
        switch (mPageMode) {
            case SIMULATION:
                mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((SimulationPageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
                break;
            case COVER:
                mPageAnim = new CoverPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((CoverPageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
                break;
            case VERTICAL_COVER:
                mPageAnim = new CoverVerticalPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((CoverVerticalPageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
                break;
            case SLIDE:
                mPageAnim = new SlidePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((SlidePageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
                break;
            case NONE:
                mPageAnim = new NonePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((NonePageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
                break;
            case SCROLL:
                mPageAnim = new ScrollPageAnim(mViewWidth, mViewHeight, 0,
                        mPageLoader.getMarginHeight(), this, mPageAnimListener);
                break;
            case AUTO:
                mPageAnim = new AutoReadPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((AutoReadPageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
                break;
            default:
                mPageAnim = new SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener);
                ((SimulationPageAnim) mPageAnim).setBitmaps(curBitmap, nextBitmap);
        }
    }

    public Bitmap getNextBitmap() {
        if (mPageAnim == null) return null;
        return mPageAnim.getNextBitmap();
    }

    public Bitmap getBgBitmap() {
        if (mPageAnim == null) return null;
        return mPageAnim.getBgBitmap();
    }

    public void setBgBitmap(Bitmap bitmap) {
        if (mPageAnim == null) {
            return;
        }
        mPageAnim.setBgBitmap(bitmap);
    }

    public Bitmap getCurBitmap() {
        if (mPageAnim == null) {
            return null;
        }
        return mPageAnim.getmCurBitmap();
    }

    public boolean autoPrevPage() {
        //滚动暂时不支持自动翻页
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageAnimation.Direction.PRE);
            return true;
        }
    }

    public boolean autoNextPage() {
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageAnimation.Direction.NEXT);
            return true;
        }
    }

//    public boolean drawAdPage(Bitmap bitmap, YYPageAdView adView, YYFrame frame){
//        if (!isPrepare) return false;
//
//        if (adView == null){
//            return false;
//        }
//
//        mBitmap = bitmap;
//
//        shouldDraw = true;
//        adView.setmImageLoadListener(new YYPageAdView.YYPageAdImageLoadListener() {
//            @Override
//            public void imageLoadFinished() {
//                shouldDraw = true;
//            }
//        });
//
//
//        switch (mTurnPageType){
//            case TURN_PAGE_TYPE_CUR:
//                mAdView = adView;
//                mFrame = frame;
//                mAdType = AD_TYPE.AD_TYPE_PAGE;
//                break;
//            case TURN_PAGE_TYPE_PREV:
//                mPrevAdView = adView;
//                mPrevFrame = frame;
//                mPrevAdType = AD_TYPE.AD_TYPE_PAGE;
//                break;
//            case TURN_PAGE_TYPE_NEXT:
//                mNextAdView = adView;
//                mNextFrame = frame;
//                mNextAdType = AD_TYPE.AD_TYPE_PAGE;
//                break;
//            default:
//                break;
//        }
//
//        addAdLayout();
//        return true;
//    }

//    //新一页是广告页，需要渲染
//    public boolean drawInterAd(Bitmap bitmap, YYInterAdView adView, YYFrame frame){
//        if (!isPrepare) return false;
//
//        if (adView == null){
//            return false;
//        }
//
//        shouldDraw = true;
//        adView.setYYImageLoadListener(new YYInterAdView.YYInterAdImageLoadListener() {
//            @Override
//            public void imageLoadFinished() {
//                shouldDraw = true;
//            }
//        });
//
//        mBitmap = bitmap;
//
//        switch (mTurnPageType){
//            case TURN_PAGE_TYPE_CUR:
//                mAdView = adView;
//                mFrame = frame;
//                mAdType = AD_TYPE.AD_TYPE_INTER;
//                break;
//            case TURN_PAGE_TYPE_PREV:
//                mPrevAdView = adView;
//                mPrevFrame = frame;
//                mPrevAdType = AD_TYPE.AD_TYPE_INTER;
//                break;
//            case TURN_PAGE_TYPE_NEXT:
//                mNextAdView = adView;
//                mNextFrame = frame;
//                mNextAdType = AD_TYPE.AD_TYPE_INTER;
//                break;
//                default:
//                    break;
//        }
//
//        addAdLayout();
//        return true;
//    }

//    public boolean drawInterAdWithCloseMode(Bitmap bitmap){
//        if (!isPrepare) return false;
//        if (mAdView == null){
//            return false;
//        }
//        mBitmap = bitmap;
//
//        mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_CUR;
//
//        addAdLayout();
//        return true;
//    }

//
//    private void addVideoContainnerTemply(ViewGroup containner, View adView){
//        if (containner == null || adView == null){
//            return;
//        }
//
//        if (adView instanceof YYInterAdView){
//            if (((YYInterAdView)adView).isVideo()) {
//                addView(containner);
//            }else {
//                addView(containner);
//            }
//        }else if(adView instanceof YYPageAdView){
//            if (((YYPageAdView)adView).isGdtVideo()) {
//                addView(containner);
//            }else {
//                addView(containner);
//            }
//        }
//        return;
//    }

//    //根据TurnPageType来Add Ad
//    private void addAdLayout(){
//        View tmpAdView = null;
//        YYFrame tmpFrame = null;
//        AD_TYPE tmpAdType = AD_TYPE.AD_TYPE_NONE;
//        switch (mTurnPageType){
//            case TURN_PAGE_TYPE_CUR:
//                tmpAdView = mAdView;
//                tmpFrame = mFrame;
//                tmpAdType = mAdType;
//                break;
//            case TURN_PAGE_TYPE_PREV:
//                tmpAdView = mPrevAdView;
//                tmpFrame = mPrevFrame;
//                tmpAdType = mPrevAdType;
//                break;
//            case TURN_PAGE_TYPE_NEXT:
//                tmpAdView = mNextAdView;
//                tmpFrame = mNextFrame;
//                tmpAdType = mNextAdType;
//                break;
//            default:
//                break;
//        }
//
//        //无广告
//        if (tmpAdView == null || tmpFrame == null){
//            return;
//        }
//
//        if ((tmpAdType == AD_TYPE.AD_TYPE_INTER && ((YYInterAdView) tmpAdView).getmAdType() == YYNewAdType.YY_NEW_AD_TYPE_Native_GDT) ||
//                (tmpAdType == AD_TYPE.AD_TYPE_INTER && ((YYInterAdView) tmpAdView).getmAdType() == YYNewAdType.YY_NEW_AD_TYPE_Native_VIDEO_GDT)||
//                (tmpAdType == AD_TYPE.AD_TYPE_PAGE && ((YYPageAdView) tmpAdView).getmAdType() == YYNewAdType.YY_NEW_AD_TYPE_PAGE_GDT) ||
//                (tmpAdType == AD_TYPE.AD_TYPE_PAGE && ((YYPageAdView) tmpAdView).getmAdType() == YYNewAdType.YY_NEW_AD_TYPE_PAGE_VIDEO_GDT)) {
//
//            Boolean virtualClick = false;
////            virtualClick =  YYOLParmManage.getInstance().getSupportGDTVirtualClick();
//            if (virtualClick&&((tmpAdType == AD_TYPE.AD_TYPE_INTER
//                            && !((YYInterAdView) tmpAdView).isGdtVideo())
//                        ||(tmpAdType == AD_TYPE.AD_TYPE_PAGE
//                            && !((YYPageAdView) tmpAdView).isGdtVideo()))){
//                ViewGroup parent = (ViewGroup) tmpAdView.getParent();
//                if (parent != null){
//                    parent.removeView(tmpAdView);
//                }
//
//                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(tmpFrame.getWidth(),tmpFrame.getHeight());
//                lp.setMargins(tmpFrame.getX(),tmpFrame.getY(),0,0);
//                tmpAdView.setLayoutParams(lp);
//                addView(tmpAdView);
//            }else {
//                ViewGroup gdtContainner = (ViewGroup) tmpAdView.getParent();
//                if(gdtContainner != null)
//                {
//                    ViewGroup parent = (ViewGroup) gdtContainner.getParent();
//                    if (parent != null) {
//                        parent.removeView(gdtContainner);
//                    }
//
//
//                    try {
//                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)gdtContainner.getLayoutParams();
//                        params.setMargins(tmpFrame.getX(),tmpFrame.getY(),0,0);
//                        gdtContainner.setLayoutParams(params);
//                    } catch (Exception e) {
//
//                    }
//
//
//                    addVideoContainnerTemply(gdtContainner, tmpAdView);
//                }
//            }
//        }else if((tmpAdType == AD_TYPE.AD_TYPE_INTER && ((YYInterAdView) tmpAdView).getmAdType() == YYNewAdType.YY_NEW_AD_TYPE_Native_IC) ||
//                (tmpAdType == AD_TYPE.AD_TYPE_INTER && ((YYInterAdView) tmpAdView).getmAdType() == YYNewAdType.YY_NEW_AD_TYPE_Native_Video_IC) ||
//                (tmpAdType == AD_TYPE.AD_TYPE_INTER && ((YYInterAdView) tmpAdView).getmAdType() == YYNewAdType.YY_NEW_AD_TYPE_Native_FX) ||
//                (tmpAdType == AD_TYPE.AD_TYPE_PAGE && ((YYPageAdView) tmpAdView).getmAdType() == YYNewAdType.YY_NEW_AD_TYPE_PAGE_IC)||
//                (tmpAdType == AD_TYPE.AD_TYPE_PAGE && ((YYPageAdView) tmpAdView).getmAdType() == YYNewAdType.YY_NEW_AD_TYPE_PAGE_FX)){
//            ViewGroup iclickContainner = (ViewGroup)tmpAdView.getParent();
//            ViewGroup parent = null;
//            if(iclickContainner != null)
//            {
//                parent = (ViewGroup) iclickContainner.getParent();
//            }
//
//            if ((tmpAdType == AD_TYPE.AD_TYPE_PAGE &&((YYPageAdView) tmpAdView).getmAdType() == YYNewAdType.YY_NEW_AD_TYPE_PAGE_FX) ||
//                    (tmpAdType == AD_TYPE.AD_TYPE_INTER && ((YYInterAdView) tmpAdView).getmAdType() == YYNewAdType.YY_NEW_AD_TYPE_Native_FX)){
//                if (parent != null) {
//                    ViewGroup pp = (ViewGroup) parent.getParent();
//                    if (pp != null) {
//                        pp.removeView(parent);
//                    }
//                    iclickContainner = parent;
//                }
//            }else {
//                if (parent != null) {
//                    parent.removeView(iclickContainner);
//                }
//            }
//
//            if(iclickContainner != null)
//            {
//                if (iclickContainner.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)iclickContainner.getLayoutParams();
//                    params.setMargins(tmpFrame.getX(),tmpFrame.getY(),0,0);
//                    iclickContainner.setLayoutParams(params);
//                }else if(iclickContainner.getLayoutParams() instanceof ConstraintLayout.LayoutParams){
//                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)iclickContainner.getLayoutParams();
//                    params.setMargins(tmpFrame.getX(),tmpFrame.getY(),0,0);
//                    iclickContainner.setLayoutParams(params);
//                }else {
//                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iclickContainner.getLayoutParams();
//                    params.width = tmpFrame.getWidth();
//                    params.setMargins(tmpFrame.getX(), tmpFrame.getY(), tmpFrame.getX(), 0);
//                    params.gravity = Gravity.CENTER_HORIZONTAL;
//
//                    try {
//                        //风行sdk当使用广点通的广告时会添加左右margin10dp，导致interAdView的子view偏移，这里强制取消sdk添加的margin
//                        RelativeLayout.LayoutParams paramChild = (RelativeLayout.LayoutParams) iclickContainner.getChildAt(0).getLayoutParams();
//                        paramChild.setMargins(0, 0, 0, 0);
//                        iclickContainner.getChildAt(0).setLayoutParams(paramChild);
//                        iclickContainner.setLayoutParams(params);
//                    } catch (Exception e) {
//
//                    }
//
//
//                }
//
//                addVideoContainnerTemply(iclickContainner, tmpAdView);
//            }
//
//        }else {
//            ViewGroup parent = (ViewGroup) tmpAdView.getParent();
//            if (parent != null){
//                parent.removeView(tmpAdView);
//            }
//
//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(tmpFrame.getWidth(),tmpFrame.getHeight());
//            lp.setMargins(tmpFrame.getX(),tmpFrame.getY(),0,0);
//            tmpAdView.setLayoutParams(lp);
//            addView(tmpAdView);
//        }
//
//        if (tmpAdType == AD_TYPE.AD_TYPE_PAGE) {
//            addRewordBtn(tmpFrame);
//        }
//    }
//
//    public void addRewordBtn(YYFrame tmpFrame){
//        Button watchVideoBtn = new Button(getContext());
//
//        Integer btnWidth = ScreenUtils.dpToPx(200);
//        Integer btnX = tmpFrame.getX() + (tmpFrame.getWidth()-btnWidth)/2;
//
//        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(btnWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(btnX,tmpFrame.getY()+ tmpFrame.getHeight()+ScreenUtils.dpToPx(20),0,ScreenUtils.dpToPx(20)
//        );
//        watchVideoBtn.setLayoutParams(lp);
////        watchVideoBtn.setText("看视频去除" + YYOLParmManage.getInstance().getRemoveVideoAdTime() / 60 + "分钟广告>>");
//
//        int time = JuYueAdPostionReadRewardNoAdHelper.getInstance().getIncentiveTime();
//        watchVideoBtn.setText("看视频去除" + time + "分钟广告>>");
//
////        watchVideoBtn.setBackground(getContext().getResources().getDrawable(R.drawable.border_all_radius_3_noback));
//        JuYueReadSettingManager manager = JuYueReadSettingManager.getInstance();
//        if(manager.isNightMode())
//        {
//            watchVideoBtn.setBackgroundColor(getContext().getResources().getColor(R.color.jy_read_battery_night));
////            watchVideoBtn.setBackgroundColor(getContext().getResources().getColor(R.color.colorAppBlue));
//        }else{
//            watchVideoBtn.setBackgroundColor(getContext().getResources().getColor(R.color.app_gray_bg));
//        }
//        watchVideoBtn.setTextColor(getContext().getResources().getColor(R.color.colorAppBlue));
//        watchVideoBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
//        watchVideoBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mTouchListener != null)
//                {
//                    mTouchListener.onWatchVideoBtnClick();
//                }
//
//            }
//        });
//        addView(watchVideoBtn);
//    }

    /**
     * 清除添加的所有view
     */
    public void cleanAdView() {
        removeAllViews();
        if (mPageLoader != null && mPageLoader.mNativeAdListener != null) {
            mPageLoader.mNativeAdListener.pauseAd();
        }
        mCanvas = null;
        mBitmap = null;
    }

    private boolean isAutoRead = false;


    public void startAutoRead() {
        isAutoRead = true;
        startPageAnim(PageAnimation.Direction.NEXT);
    }

    public void endAutoRead() {
        isAutoRead = false;
    }

    public boolean isAutoRead() {
        return isAutoRead;
    }

    private void startPageAnim(PageAnimation.Direction direction) {
        if (mTouchListener == null) return;
        //是否正在执行动画
        abortAnimation();

        if (isAutoRead) {
            int x = mViewWidth;
            int y = mViewHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
//            //设置方向
//            Boolean hasNext = hasNextPage();
//
            mPageAnim.setDirection(direction);
//            if (!hasNext) {
//                return;
//            }
            mPageAnim.btnChangePage(true);
            return;
        }

        if (direction == PageAnimation.Direction.NEXT) {
            int x = mViewWidth;
            int y = mViewHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
//            //设置方向
//            Boolean hasNext = hasNextPage();
//
//            mPageAnim.setDirection(direction);
//            if (!hasNext) {
//                return;
//            }
            mPageAnim.btnChangePage(true);
        } else {
            int x = 0;
            int y = mViewHeight;
//            //初始化动画
            mPageAnim.setStartPoint(x, y);
//            //设置点击点
            mPageAnim.setTouchPoint(x, y);
//            mPageAnim.setDirection(direction);
//            //设置方向方向
//            Boolean hashPrev = hasPrevPage();
//            if (!hashPrev) {
//                return;
//            }
            mPageAnim.btnChangePage(false);
        }
//        mPageAnim.startAnim();
//        this.postInvalidate();
//        if (mTurnPageListener != null){
//            mTurnPageListener.moveTurnPageFinished(true);
//        }
    }

    public void setBgColor(int color) {
        mBgColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //绘制背景
//        canvas.drawColor(mBgColor);
//        canvas.drawBitmap(getBgBitmap(),0,0,null);
        //绘制动画
        if (mPageAnim == null || mPageAnim.getNextBitmap() == null){
            //当mPageAnim为null或者内存溢出时会导致bitmap为null，此时直接return
//            EventBus.getDefault().post(new ReadErrorEvent(true));

            Message message = Message.obtain();
            message.what = BusC.BUS_NOTIFY_USER_READ_ERROR;
            EventBus.getDefault().post(message);
            return;
        }
        mPageAnim.draw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            if (mBitmap != null){
                Log.d(TAG, "dispatchDraw: disPatchDraw start");
                if (mCanvas == null){
                    mCanvas = new Canvas(mBitmap);
                }

                if (mPageLoader == null){
                    return;
                }
                LineInfo.LineAdType adType = mPageLoader.mCurPage.getAdType();
                if (adType == LineInfo.LineAdType.LineAdTypeNone) {
                    return;
                }

//                if(mPageLoader.mCurPage.bHaveAd()){
//                    if (mAdView instanceof YYInterAdView) {
//                        if(((YYInterAdView) mAdView).isVideo()){
//                            if (mPageAnim.isRunning()) {
//                                if (shouldDraw) {
//                                    super.dispatchDraw(mCanvas);
//                                    drawCount++;
//                                    if (drawCount >= 1){
//                                        shouldDraw = false;
//                                        drawCount = 0;
//                                    }
//                                }
//                                return;
//                            }
//                            super.dispatchDraw(mCanvas);
//                            super.dispatchDraw(canvas);
//                            return;
//                        }
//                    }
//                    if (mAdView instanceof YYPageAdView) {
//                        if(((YYPageAdView) mAdView).isGdtVideo()){
//                            if (mPageAnim.isRunning()) {
//                                if (shouldDraw) {
//                                    super.dispatchDraw(mCanvas);
//                                    drawCount++;
//                                    if (drawCount >= 1){
//                                        shouldDraw = false;
//                                        drawCount = 0;
//                                    }
//                                }
//                                return;
//                            }
//                            super.dispatchDraw(mCanvas);
//                            super.dispatchDraw(canvas);
//                            return;
//                        }
//                    }
//                    if (shouldDraw) {
//                        super.dispatchDraw(mCanvas);
//                        drawCount++;
//                        if (drawCount >= 1){
//                            shouldDraw = false;
//                            drawCount = 0;
//                        }
//                    }
//                }
            }else {
                super.dispatchDraw(canvas);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean isPointInFrame(int x, int y, YYFrame frame){
        return x>=frame.getX() && (x<=frame.getX()+frame.getWidth()) && y>=frame.getY() && (y <= frame.getY()+frame.getHeight());
    }

    private boolean touchInReloadBtn(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        YYFrame btnFrame = YYFrame.YYFrameZero();
        btnFrame = mPageLoader.getReloadBtnFram();
        if (isPointInFrame(x,y,btnFrame)){
            return true;
        }
        return false;
    }

    private boolean touchInStatusBtn(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        YYFrame btnFrame = YYFrame.YYFrameZero();
        btnFrame = mPageLoader.getStatusBtnFram();


        if(mPageAnim instanceof ScrollPageAnim)
        {
            ArrayList<ScrollPageAnim.BitmapView> activeViews =  ((ScrollPageAnim) mPageAnim).getmActiveViews();

            for (ScrollPageAnim.BitmapView tempView:
                    activeViews) {
                Rect tempRect =  tempView.destRect;

                YYFrame tempBtnFrame = new YYFrame(btnFrame.getX(),btnFrame.getY(),btnFrame.getWidth(),btnFrame.getWidth());
                tempBtnFrame.setY(tempBtnFrame.getY()+tempRect.top);

                if (isPointInFrame(x,y,tempBtnFrame)){
                    return true;
                }
            }
        }else{
            if (isPointInFrame(x,y,btnFrame)){
                return true;
            }
        }

        return false;
    }

    private boolean touchInStatusAutoPayBtn(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        YYFrame btnFrame = YYFrame.YYFrameZero();
        btnFrame = mPageLoader.getStatusAutoPayBtnFram();

        if(mPageAnim instanceof ScrollPageAnim)
        {
            ArrayList<ScrollPageAnim.BitmapView> activeViews =  ((ScrollPageAnim) mPageAnim).getmActiveViews();

            for (ScrollPageAnim.BitmapView tempView:
                 activeViews) {
                Rect tempRect =  tempView.destRect;

                YYFrame tempBtnFrame = new YYFrame(btnFrame.getX(),btnFrame.getY(),btnFrame.getWidth(),btnFrame.getWidth());
                tempBtnFrame.setY(tempBtnFrame.getY()+tempRect.top);

                if (isPointInFrame(x,y,tempBtnFrame)){
                    return true;
                }
            }
        }else{
            if (isPointInFrame(x,y,btnFrame)){
                return true;
            }
        }

        return false;
    }


    private boolean touchInAdArea(MotionEvent event){
        int x = (int) event.getX();
        int y = (int) event.getY();
        YYFrame adFrame = YYFrame.YYFrameZero();
        if (mPageLoader.bCurPageHaveAd()){
            adFrame = mPageLoader.getCurPageAdFrame();
        }
        if (isPointInFrame(x,y,adFrame)){
            return true;
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (!canTouch && event.getAction() != MotionEvent.ACTION_DOWN) return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            isMove = false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                mMoveY = y;
                isMove = false;
                if(mTouchListener != null)
                {
                    canTouch = mTouchListener.onTouch();
                }

                if(mPageAnim != null)
                {
                    mPageAnim.onTouchEvent(event);
                }

                if (mPageLoader != null && mPageLoader.curPageShowReloadBtn()) {
                    mTouchReloadBtn = touchInReloadBtn(event);
                }

                boolean isScollFlag = false;
                Rect scollRect = new Rect(0,0,0,0);
                Rect scollPreRect = new Rect(0,0,0,0);
                if(mPageAnim instanceof ScrollPageAnim) {
                    isScollFlag = true;
                    ArrayList<ScrollPageAnim.BitmapView> activeViews =  ((ScrollPageAnim) mPageAnim).getmActiveViews();
                    if(activeViews.size() > 1)
                    {
                        scollRect = activeViews.get(activeViews.size() - 1).destRect;
                        scollPreRect = activeViews.get(activeViews.size() - 2).destRect;
                    }else{
                        if(activeViews.size() == 1)
                        {
                            scollRect = activeViews.get(activeViews.size() - 1).destRect;
                        }
                    }
                }

                if(mPageLoader != null && mPageLoader.curPageShowStatusBtn(event,isScollFlag,scollRect,scollPreRect))
                {
                    mTouchPageStatuFlag = true;
                }

                if(mPageLoader != null && mPageLoader.curPageAutoBuyShowStatusBtn(event,isScollFlag,scollRect,scollPreRect))
                {
                    mTouchPageAutoPayStatuFlag = true;
                }
                
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchReloadBtn) {
                    Log.e("speech_action", "mTouchReloadBtn");
                    return true;
                }

                if (mTouchPageStatusBtn) {
                    return true;
                }

                if (mTouchPageStatusAutoPayBtn) {
                    return true;
                }

                if(mTouchPageStatuFlag)
                {
                    return true;
                }

                if(mTouchPageAutoPayStatuFlag)
                {
                    return true;
                }

                // 判断是否大于最小滑动值。
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(mStartX - event.getX()) > slop || Math.abs(mStartY - event.getY()) > slop;
                }



                // 如果滑动了，则进行翻页。
                if (isMove) {
                    if (isBookSpeeching) {
                        if(mTouchListener != null)
                        {
                            mTouchListener.move(event.getX(),event.getY() - mMoveY);
                        }

                        mMoveY = event.getY();
                        break;
                    }

                    // TODO: 1/19/21 无动画不响应
                    if(getmPageMode()!=PageMode.NONE && mPageAnim != null) {
                        mPageAnim.onTouchEvent(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchReloadBtn) {
                    boolean lastTouchInReload = touchInReloadBtn(event);
                    if (lastTouchInReload){
                        //最后离开位置还在广告区域内，响应为点击
                        if(mTouchListener != null) {
                            mTouchListener.onReloadClick();
                        }
                    }
                    mTouchReloadBtn = false;
                    return true;
                }

                // TODO: 2021/10/4 1.8.1 修改阅读内容点击事件处理
                if(mTouchPageStatuFlag)
                {
                    isScollFlag = false;
                    scollRect = new Rect(0,0,0,0);
                    scollPreRect = new Rect(0,0,0,0);
                    if(mPageAnim instanceof ScrollPageAnim) {
                        isScollFlag = true;
                        ArrayList<ScrollPageAnim.BitmapView> activeViews =  ((ScrollPageAnim) mPageAnim).getmActiveViews();
                        if(activeViews.size() > 1)
                        {
                            scollRect = activeViews.get(activeViews.size() - 1).destRect;
                            scollPreRect = activeViews.get(activeViews.size() - 2).destRect;
                        }else{
                            if(activeViews.size() == 1)
                            {
                                scollRect = activeViews.get(activeViews.size() - 1).destRect;
                            }
                        }
                    }

                    TxtPage tempPage = mPageLoader.curScollPageShowStatusBtn(event,isScollFlag,scollRect,scollPreRect);

                    if(tempPage != null)
                    {
                        if(tempPage.getmPageStatus() != null)
                        {
                            if(mTouchListener != null) {
                                mTouchListener.onStatusBtnClick(tempPage.getmPageStatus().getChapterName(),tempPage.touchType,tempPage.getmPageStatus().getChapterOrder());
                            }
                        }
                    }



                    mTouchPageStatuFlag = false;
                    return true;
                }

                if(mTouchPageAutoPayStatuFlag)
                {
                    isScollFlag = false;
                    scollRect = new Rect(0,0,0,0);
                    scollPreRect = new Rect(0,0,0,0);
                    if(mPageAnim instanceof ScrollPageAnim) {
                        isScollFlag = true;
                        ArrayList<ScrollPageAnim.BitmapView> activeViews =  ((ScrollPageAnim) mPageAnim).getmActiveViews();
                        if(activeViews.size() > 1)
                        {
                            scollRect = activeViews.get(activeViews.size() - 1).destRect;
                            scollPreRect = activeViews.get(activeViews.size() - 2).destRect;
                        }else{
                            if(activeViews.size() == 1)
                            {
                                scollRect = activeViews.get(activeViews.size() - 1).destRect;
                            }
                        }
                    }

                    TxtPage tempPage = mPageLoader.curScollPageAutoBuyShowStatusBtn(event,isScollFlag,scollRect,scollPreRect);

                    if(tempPage != null)
                    {
                        if(tempPage.getmPageStatus() != null)
                        {
                            if(mTouchListener != null) {
                                mTouchListener.onAutoPayBtnClick(tempPage.getmPageStatus().getChapterName(),tempPage.getmPageStatus().getChapterOrder());
                            }
                        }
                    }

                    mTouchPageAutoPayStatuFlag = false;
                    return true;
                }


                if (mTouchPageStatusBtn) {
                    boolean lastTouchInStatus = touchInStatusBtn(event);
                    if (lastTouchInStatus){//触发页面不同状态点击
                        //最后离开位置还在广告区域内，响应为点击
//                        if(mTouchListener != null) {
//                            mTouchListener.onStatusBtnClick();
//                        }
                    }
                    mTouchReloadBtn = false;
                    return true;
                }

                if (mTouchPageStatusAutoPayBtn) {
                    boolean lastTouchInAutoPay = touchInStatusAutoPayBtn(event);
                    if (lastTouchInAutoPay){//触发页面自动购买点击
                        //最后离开位置还在广告区域内，响应为点击
//                        if(mTouchListener != null) {
//                            mTouchListener.onAutoPayBtnClick();
//                        }
                    }
                    mTouchReloadBtn = false;
                    return true;
                }

                if (!isMove) {
                    //设置中间区域范围
                    if (mCenterRect == null) {
                        mCenterRect = new RectF(mViewWidth / 5, mViewHeight / 3,
                                mViewWidth * 4 / 5, mViewHeight * 2 / 3);
                    }

                    //是否点击了中间
                    if (mCenterRect.contains(x, y)) {
                        if (mTouchListener != null) {
                            mTouchListener.center();
                        }
                        return true;
                    }
                }

                if (isBookSpeeching) {
                    if(mTouchListener != null)
                    {
                        mTouchListener.up();
                    }
                    return true;
                }

                if(mPageAnim != null)
                {
                    mPageAnim.onTouchEvent(event);
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 判断是否存在上一页
     *
     * @return
     */
    private boolean hasPrevPage() {
        if(mTouchListener != null)
        {
            mTouchListener.prePage();
        }

        shouldDraw=true;
        return mPageLoader.prev();
    }

    /**
     * 判断是否下一页存在
     *
     * @return
     */
    private boolean hasNextPage() {
        if(mTouchListener != null)
        {
            mTouchListener.nextPage();
        }

        shouldDraw=true;
        return mPageLoader.next();
    }

    private void pageCancel() {
        if(mTouchListener != null) {
            mTouchListener.cancel();
        }
        mPageLoader.pageCancel();

        cleanAdView();

        //重置TurnPageType
        mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_CUR;
//        addAdLayout();

//        if (mPageLoader.mCurPage != null && mPageLoader.mCurPage.bHaveAd()) {
//            //翻页取消的时候，如果当前页是广告页，那么就重新添加
//            addAdLayout(mIsInterAd, mFrame);
//        }else {
//            //不是广告页，则移除广告
//            cleanAdView();
//        }
    }

    private void turnPageFinished(){
        switch (mTurnPageType){
            case TURN_PAGE_TYPE_CUR:
                //理论上不会进
                break;
            case TURN_PAGE_TYPE_PREV:
                mNextAdView = mAdView;
                mNextFrame = mFrame;
                mNextAdType = mAdType;

                mAdView = mPrevAdView;
                mFrame = mPrevFrame;
                mAdType = mPrevAdType;

                mPrevAdView = null;
                mPrevFrame = null;
                mPrevAdType = AD_TYPE.AD_TYPE_NONE;
                break;
            case TURN_PAGE_TYPE_NEXT:
                mPrevAdView = mAdView;
                mPrevFrame = mFrame;
                mPrevAdType = mAdType;

                mAdView = mNextAdView;
                mFrame = mNextFrame;
                mAdType = mNextAdType;

                mNextAdView = null;
                mNextFrame = null;
                mNextAdType = AD_TYPE.AD_TYPE_NONE;
                break;
            default:
                break;
        }

        mTurnPageType = TURN_PAGE_TYPE.TURN_PAGE_TYPE_CUR;
    }

    @Override
    public void computeScroll() {
        //进行滑动
        if (mPageAnim != null) {
            mPageAnim.scrollAnim();
        }
        super.computeScroll();
    }

    public void continueAutoRead() {
        if (mPageAnim instanceof AutoReadPageAnim) {
            ((AutoReadPageAnim) mPageAnim).restartAnim();
        }
    }

    public void pauseAutoRead() {
        if (mPageAnim instanceof AutoReadPageAnim) {
            ((AutoReadPageAnim) mPageAnim).pauseAnim();
        }
    }

    //如果滑动状态没有停止就取消状态，重新设置Anim的触碰点
    public void abortAnimation() {
        mPageAnim.abortAnim();
    }

    public boolean isRunning() {
        if (mPageAnim == null) {
            return false;
        }
        return mPageAnim.isRunning();
    }

    public boolean isAutoReadRunning() {
        if (mPageAnim == null) {
            return false;
        }
        return mPageAnim.isAutoReadRunning();
    }

    public boolean isPrepare() {
        return isPrepare;
    }

    public void setTouchListener(TouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }

    public void drawNextPage() {
        if (!isPrepare) return;

        if (mPageAnim instanceof HorizonPageAnim) {
            ((HorizonPageAnim) mPageAnim).changePage();
        }
        if (mPageAnim instanceof VerticalPageAnim) {
            ((VerticalPageAnim) mPageAnim).changePage();
        }

        mPageLoader.drawPage(getNextBitmap(), false);
    }

    /**
     * 绘制当前页。
     *
     * @param isUpdate
     */
    public void drawCurPage(boolean isUpdate) {
        if (!isPrepare) return;

        if (!isUpdate) {
            if (mPageAnim instanceof ScrollPageAnim) {
                ((ScrollPageAnim) mPageAnim).resetBitmap();
            }

            if (mPageAnim instanceof SimulationPageAnim) {
                ((SimulationPageAnim) mPageAnim).resetAnim();
            }
        }

        if(mPageLoader != null)
        {
            mPageLoader.drawPage(getNextBitmap(), isUpdate);
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPageAnim!=null) {
            mPageAnim.abortAnim();
            mPageAnim.clear();
        }

        mPageLoader = null;
        mPageAnim = null;
    }

    /**
     * 获取 PageLoader
     *
     * @param collBook
     * @return
     */
    public PageLoader getPageLoader(BookBean collBook) {
        // 判是否已经存在
        if (mPageLoader != null) {
            return mPageLoader;
        }
        // 根据书籍类型，获取具体的加载器
        mPageLoader = new NetPageLoader(this, collBook);
        // 判断是否 PageView 已经初始化完成
        if (mViewWidth != 0 || mViewHeight != 0) {
            // 初始化 PageLoader 的屏幕大小
            mPageLoader.prepareDisplay(mViewWidth, mViewHeight);
        }

        return mPageLoader;
    }

    public interface TouchListener {
        boolean onTouch();

        void center();

        void prePage();

        void nextPage();

        void cancel();

        void move(float x, float y);

        void up();

        void onReloadClick();

        void onStatusBtnClick(String chapter, int type,int chapterOrder);//type 1：登录 2：购买 3：购买更多 4：重新加载

        void onAutoPayBtnClick(String chapter,int chapterOrder);


        void onWatchVideoBtnClick();
    }


    //用于信息流广告处理
    public interface TurnPageListener{
        void moveTurnPageBegin();   //开始翻页，包括开始翻页动画
        void moveTurnPageFinished(boolean bFinished);  //true:翻页成功  false:翻页回退
        void tapTurnPage();
    }

    public View getAdView() {
        return mAdView;
    }

    public void setAdCloseMode(){
        if (mAdView == null){
            return;
        }
        shouldDraw = true;
//        ((YYInterAdView)mAdView).setInterADClosed(true);
    }

    private float mMoveY = 0f;

    private boolean isBookSpeeching = false;

    public void setIsBookSpeeching(boolean isSpeeching) {
        isBookSpeeching = isSpeeching;
    }

    public boolean getIsBookSpeeching() {
        return isBookSpeeching;
    }
}
