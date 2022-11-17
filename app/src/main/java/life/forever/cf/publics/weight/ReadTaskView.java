package life.forever.cf.publics.weight;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import life.forever.cf.R;
import life.forever.cf.entry.ReadTaskBean;
import life.forever.cf.publics.tool.DisplayUtil;

import java.util.List;


public class ReadTaskView extends View {

    private Paint mDotPaint;

    private Paint mShadowPaint;

    private Paint mLinePaint;
    //
    private Paint mTipPaint;
    //
    private Paint mTimePaint;

    private int mViewHeight;

    private int mViewWidth;

    private int mAllLevel;

    private int mLeftMargin;

    private int mRightMargin;

    private int mBootomHeight;
    private final int mLevelTime = 15;
    private int mReadTime;
    private boolean isShowTip = false;

    private Context mContext;
    private List<ReadTaskBean> mDataList;
    private onClickLisenter mOnClickLisenter;
    private final String TAG = "ReadTaskView";
    public ReadTaskView(Context context) {
        super(context);
        init(context);
    }

    public ReadTaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReadTaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

//    @Override
//    protected void onLayout(boolean ab, int l, int t, int r, int b) {
//        super.layout(l,t,r+mViewWidth,b+mViewHeight);
//    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    //初始化
    private void init(Context context) {
        mContext = context;
//        LayoutInflater.from(context).inflate(R.layout.layout_readtaskview,this);
//        inflate(context,R.layout.layout_readtaskview,this);
//        mTipTextView = new TextView(context);
        mShadowPaint = new Paint();
        mShadowPaint.setColor(getResources().getColor(R.color.color_30F9791C));
        mShadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStrokeWidth(DisplayUtil.dp2px(mContext, 4));
        mLinePaint = new Paint();
        mLinePaint.setColor(getResources().getColor(R.color.color_F9791C));
        mLinePaint.setStrokeWidth(DisplayUtil.dp2px(mContext, 4));
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDotPaint = new Paint();
        mDotPaint.setColor(getResources().getColor(R.color.color_30F9791C));
        mLinePaint.setStrokeWidth(DisplayUtil.dp2px(mContext, 4));
        mDotPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mDotPaint.setAntiAlias(true);
        mTipPaint = new Paint();
        mTipPaint.setColor(getResources().getColor(R.color.colorWhite));
        mTipPaint.setTextSize(DisplayUtil.sp2px(mContext, 11));
        mTipPaint.setAntiAlias(true);
        mTipPaint.setTextAlign(Paint.Align.CENTER);
        mTimePaint = new Paint();
        mTimePaint.setColor(getResources().getColor(R.color.color_656667));
        mTimePaint.setTextSize(DisplayUtil.sp2px(mContext, 12));
        mTimePaint.setAntiAlias(true);
        mTimePaint.setTextAlign(Paint.Align.CENTER);
        mAllLevel = 3;
        mLeftMargin = DisplayUtil.dp2px(mContext, 20);
        mRightMargin = DisplayUtil.dp2px(mContext, 40);
        mBootomHeight = DisplayUtil.dp2px(mContext, 10);
    }


    /**
     * 根据是否完成阅读动态设置控件高度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();

        if (mDataList != null && mDataList.size() > 0) {
            if (mDataList.get(0).getAlreadyReadTime() < mLevelTime * Math.pow(2, mDataList.size()-1)) {
                //未完成阅读任务 高度留出提示栏的高度
                mViewHeight = DisplayUtil.dp2px(mContext, 120);
            } else {
                mViewHeight = DisplayUtil.dp2px(mContext, 100);
            }
        } else {
            //未完成阅读任务 高度留出提示栏的高度
            mViewHeight = DisplayUtil.dp2px(mContext, 120);
        }

        setMeasuredDimension(mViewWidth, mViewHeight);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawDot(canvas);
        drawLine(canvas);
        drawProgress(canvas);
        drawImagViewAndTime(canvas);

    }

    // 原点向下移动 起始点 阴影
    private void drawDot(Canvas canvas) {
        Canvas canvas1 = new Canvas();
        canvas1.drawCircle(0,0,50,mDotPaint);
        float x = (float) mLeftMargin;
        float y = 0;

        if (mDataList != null && mDataList.size() > 0) {
            if (mDataList.get(0).getAlreadyReadTime() < mLevelTime * Math.pow(2, mDataList.size()-1)) {
                //未阅读完成所有任务
                y = (float) mViewHeight * 2 / 3;
                isShowTip = true;
            } else {
                //已完成所有阅读任务
                y = (float) mViewHeight * 1 / 2;
                isShowTip = false;
            }
        } else {
            //未阅读完成所有任务
            y = (float) mViewHeight * 2 / 3;
            isShowTip = true;
        }

        canvas.translate(x, y);
        //原点阴影
        int shadowRadius = DisplayUtil.dp2px(mContext, 15) / 2;
        canvas.drawCircle(0, 0, shadowRadius, mDotPaint);
        //原点
        int dotRadius = DisplayUtil.dp2px(mContext, 5) / 2;
        canvas.drawCircle(0, 0, dotRadius, mLinePaint);


    }

    /**
     * 阴影进度条
     */
    private void drawLine(Canvas canvas) {
//        float x = (float) mLeftMargin;
//        float y = (float) mViewHeight / 2;
        float endx = (float) (mViewWidth - mRightMargin - mLeftMargin);
        canvas.drawLine(0, 0, endx, 0, mShadowPaint);
    }

    //阶段图片
    private void drawImagViewAndTime(Canvas canvas) {
        float x = (float) mLeftMargin;
        float y = (float) mViewHeight / 2;
        int level1Width = DisplayUtil.dp2px(mContext, 28);
        int level2Width = DisplayUtil.dp2px(mContext, 38);
        int level3Width = DisplayUtil.dp2px(mContext, 46);
        int level3Height = DisplayUtil.dp2px(mContext, 58);
        int level1Size = (mAllLevel - 1) / 2;
        int level2Size = mAllLevel - 1;
        int imgX = (mViewWidth - mLeftMargin - mRightMargin) / mAllLevel;

        for (int i = 1; i <= mAllLevel; i++) {
            if (i <= level1Size) {
                //绘制一级图标
                drawLevelBitmap(canvas, 1, level1Width, level2Width, level3Width, 0, i, imgX);


            } else if (i > level1Size && i <= level2Size) {
                //绘制2级图标
                drawLevelBitmap(canvas, 2, level1Width, level2Width, level3Width, 0, i, imgX);
            } else if (i == mAllLevel) {
                //绘制终极图标
                drawLevelBitmap(canvas, 3, level1Width, level2Width, level3Width, level3Height, i, imgX);
            }
        }
    }

    //提示view
    private void drawTipView(Canvas canvas) {


    }

    /**
     * 绘制各个坐标图片
     *
     * @param canvas
     * @param level       要绘制图片的等级
     * @param level1W     1级图片的宽度（正方形图片）
     * @param level2W     2级图片的宽度
     * @param level3W     3级图片的宽度
     * @param index       绘制第几个图片
     * @param levelLength 每一级的x轴的长度
     * @param level3H     3级图片的高度
     */
    private void drawLevelBitmap(Canvas canvas, int level, int level1W, int level2W, int level3W, int level3H, int index, int levelLength) {

        int x = index * levelLength;
        int left, top, right, bootom;
        int levelW = 0;
        int levelH = 0;
        int bitmapResorce = 0;

        switch (level) {
            case 1:
                levelW = level1W;
                levelH = level1W;
                if (isReaded(index)){
                    bitmapResorce = R.drawable.icon_read_level1;
                }else {
                    bitmapResorce = R.drawable.icon_reading_level1;
                }
                break;

            case 2:
                levelW = level2W;
                levelH = level2W;
                if (isReaded(index)){
                    bitmapResorce = R.drawable.icon_read_level2;
                }else {
                    bitmapResorce = R.drawable.icon_reading_level2;
                }
                break;
            case 3:
                levelW = level3W;
                levelH = level3H;
                if (isReaded(index)){
                    bitmapResorce = R.drawable.icon_read_level3;
                }else {
                    bitmapResorce = R.drawable.icon_reading_level3;
                }
                break;
        }


        left = x - levelW / 2;
        top = 0 - (levelH - mBootomHeight);
        right = x + levelW / 2;
        bootom = mBootomHeight;

        Bitmap srcBitmap = BitmapFactory.decodeResource(getResources(), bitmapResorce);
        Bitmap bitmap = Bitmap.createScaledBitmap(srcBitmap, levelW, levelH, false);
        Rect src1Rect = new Rect(0, 0, levelW, levelH);
        Rect dst1Rect = new Rect(left, top, right, bootom);
        canvas.drawBitmap(bitmap, src1Rect, dst1Rect, null);

        boolean showTip = false;

        if (index == 1) {

            if (mLevelTime > mReadTime) {
                showTip = true;
            }

        } else {
            if (mReadTime > mLevelTime * Math.pow(2, index - 2) && mReadTime < mLevelTime * Math.pow(2, index - 1)) {
                showTip = true;
            }
        }


        if (false) {
            Bitmap srcTipBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_readview_tip);
            int tipW = DisplayUtil.dp2px(mContext, 80);
            int tipH = DisplayUtil.dp2px(mContext, 31);

            Bitmap tipBitmap = Bitmap.createScaledBitmap(srcTipBitmap, tipW, tipH, false);

            int tipLeft = x - tipW / 2;
            int tipTop = top - tipH;
            int tipRight = x + tipW / 2;
            int tipBottom = top + DisplayUtil.dp2px(mContext, 5);
            Rect srcTipRect = new Rect(0, 0, tipW, tipH);
            Rect dstTipRect = new Rect(tipLeft, tipTop, tipRight, tipBottom);
            canvas.drawBitmap(tipBitmap, srcTipRect, dstTipRect, null);

            int tipTextY = top - tipH / 2;
            Paint.FontMetrics fontMetrics = mTipPaint.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            float baseline = tipTextY + distance / 2;
            int tipTime = mLevelTime * (int) Math.pow(2, index - 1) - mReadTime;
            String tipString = tipTime + " min more";
            canvas.drawText(tipString, x, baseline, mTipPaint);
        }


        int timeY = DisplayUtil.dp2px(mContext, 30);
        String context = (int) (mLevelTime * Math.pow(2, index - 1)) + "min";
        canvas.drawText(context, x, timeY, mTimePaint);

    }

    /**
     * 绘制阅读时间进度
     */
    private void drawProgress(Canvas canvas) {
        if (mDataList != null && mDataList.size() > 0) {
            int maxTime = (int)(mLevelTime * Math.pow(2,mDataList.size()-1));
            int progressLength = mViewWidth - mLeftMargin - mRightMargin;

            int oneDistance = progressLength/mDataList.size();

            if (mReadTime>=maxTime){
                mReadTime = maxTime;
                canvas.drawLine(0, 0, progressLength, 0, mLinePaint);
            }else {


                //确定当前是哪个时间段的，因为时间间隔不同所以
                int currentLevel = 0;

                int currentX = 0;
                for (int i = 1; i <= mDataList.size();i++){

                    if (i == 1){

                        if (mReadTime<mLevelTime){
                            currentLevel = i;

                             currentX = mReadTime * oneDistance / mLevelTime;

                             break;

                        }else if (mReadTime == mLevelTime){
                             currentX = oneDistance;
                             break;
                        }

                    } else if (mReadTime> mLevelTime* Math.pow(2,i-2)&&mReadTime<=mLevelTime* Math.pow(2,i-1)){
                        currentLevel = i;
                         currentX = oneDistance*(i-1) + (int)(oneDistance*((mReadTime -mLevelTime* Math.pow(2,i-2))/(mLevelTime* (Math.pow(2,i-1)- Math.pow(2,i-2)) ))) ;
                         int length =(int) ( oneDistance*(mReadTime -mLevelTime* Math.pow(2,i-2)/(mLevelTime* (Math.pow(2,i-1)- Math.pow(2,i-2)) )));
//                        Log.e(TAG, "drawProgress: " + length);
                         break;
                    }
                }




//                int currentX = mReadTime * progressLength / maxTime;

                canvas.drawLine(0, 0, currentX, 0, mLinePaint);

            }





        } else {
            canvas.drawLine(0, 0, 0, 0, mLinePaint);
        }
    }


    /**
     * 添加数据并刷新
     *
     * @param dataList
     */
    public void setData(List<ReadTaskBean> dataList) {
        mDataList = dataList;
        if (mDataList != null && mDataList.size() > 0) {
            mAllLevel = mDataList.size();
//            mAllLevel = 6;
            mReadTime = mDataList.get(0).getAlreadyReadTime();
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int distance = (mViewWidth - mRightMargin - mLeftMargin) / mAllLevel;
        int level1Width = DisplayUtil.dp2px(mContext, 28);
        int level2Width = DisplayUtil.dp2px(mContext, 38);
        int level3Width = DisplayUtil.dp2px(mContext, 46);
        int level3Height = DisplayUtil.dp2px(mContext, 58);
        int level1Size = (mAllLevel - 1) / 2;
        int level2Size = mAllLevel - 1;
        int left = 0, top = 0, right = 0, bootom = 0;
        int x, y;
        x = mLeftMargin;
        if (isShowTip) {
            y = mViewHeight * 2 / 3;
        } else {
            y = mViewHeight * 3 / 5;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                Log.e("mReadTaskView", "onTouchEvent: 点 " + event.getX() + "  " + event.getY());

                Rect rect = null;
                for (int i = 1; i <= mAllLevel; i++) {
                    //x坐标累加
                    x = distance + x;
                    if (i <= level1Size) {
                        left = x - level1Width / 2;
                        right = x + level1Width / 2;
                        top = y - level1Width / 2;
                        bootom = y + level1Width / 2;

                    } else if (i > level1Size && i <= level2Size) {
                        left = x - level2Width / 2;
                        right = x + level2Width / 2;
                        top = y - level2Width / 2;
                        bootom = y + level2Width / 2;

                    } else if (i == mAllLevel) {

                        left = x - level3Width / 2;
                        right = x + level3Width / 2;
                        top = y - level3Height / 2;
                        bootom = y + level3Height / 2;
                    }
//                    Log.e("mReadTaskView", "onTouchEvent: 点击区域 " + left + " " + top + " " + right + " " + bootom);

                    rect = new Rect(left, top, right, bootom);
                    if (rect.contains((int) event.getX(), (int) event.getY())) {

                        if (isReaded(i)){
                            if (mOnClickLisenter != null) {
//                            monClickLisenter.onReceive(i, mDataList.get(0));
                                mOnClickLisenter.onReceive(i, mDataList.get(i-1));
                            }
                        }
                        break;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_MOVE:

                break;
        }

        return true;
    }

    public interface onClickLisenter {
        void onReceive(int index, ReadTaskBean dataItem);
    }

    public void setOnClicksenter(onClickLisenter onClicksenter) {
        mOnClickLisenter = onClicksenter;
    }


    /**
     * 判断当前阶段是否被阅读了
     */
    private boolean isReaded(int index) {
        return !(mReadTime < mLevelTime * Math.pow(2, index - 1));
    }

    /**
     *
     */
    private void startAnmication(){
//        ObjectAnimator.ofInt()

        ValueAnimator animator =  ValueAnimator.ofInt(0,100);
        animator.setDuration(2000);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int carValue =  (Integer)valueAnimator.getAnimatedValue();


            }
        });
        animator.start();
    }



}
