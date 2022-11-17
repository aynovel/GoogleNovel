package life.forever.cf.publics.weight;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import life.forever.cf.R;
import life.forever.cf.entry.ReadTaskBean;
import life.forever.cf.publics.tool.DisplayUtil;

import java.util.List;


public class ReadTipView extends RelativeLayout {

    private ReadTaskView mReadTaskView;
    private TextView mTvTipView;
    private final String TAG = "ReadTipView" ;

    private int mViewHeight;

    private int mViewWidth;

    private final int mLevelTime = 15;

    private List<ReadTaskBean> mDataList;

    private int mTipTopY ;

    private Context mContext;
    private Integer mTop;

    public ReadTipView(Context context) {
        super(context);
        init(context);
    }

    public ReadTipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReadTipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ReadTipView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_readtaskview, this);
        mReadTaskView = findViewById(R.id.readTaskView);
       mTvTipView = findViewById(R.id.tvTipView);

        mReadTaskView.setOnClicksenter(new ReadTaskView.onClickLisenter() {
            @Override
            public void onReceive(int index, ReadTaskBean dataItem) {
                if (mOnClickLisenter!=null){
                    mOnClickLisenter.onReceive(index,dataItem);
                }
            }
        });
    }

    public void setData(List<ReadTaskBean> dataList) {
        mDataList = dataList;
        mReadTaskView.setData(dataList);

        requestLayout();
        invalidate();
        setAnimation();
    }

    public List<ReadTaskBean> getData(){
        return mDataList;
    }


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
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if ( mDataList!=null && mDataList.size()>0){

            int readTime = mDataList.get(0).getAlreadyReadTime();

            for (int i = 1;i <= mDataList.size();i++){

                boolean showTip = false;
                if ( i == 1 ){
                    if (readTime < mLevelTime){
                        showTip = true;
                        mTvTipView.setVisibility(VISIBLE);
                    }

                }else if ( readTime > mLevelTime*Math.pow(2,i-2) && readTime < mLevelTime*Math.pow(2,i-1)){
                    showTip = true;
                    mTvTipView.setVisibility(VISIBLE);
                }else {
                    mTvTipView.setVisibility(GONE);
                }

                if (showTip){
                    int remindTime =  0;
                    if (i==1){
                        remindTime = mLevelTime - readTime;
                    }else {
                        remindTime = (int)(mLevelTime*Math.pow(2,i-1))-readTime;
                    }
                    String content = remindTime +" min more";
                    mTvTipView.setText(content);

                    int tipW = DisplayUtil.dp2px(mContext, 80);
                    int tipH = DisplayUtil.dp2px(mContext, 31);
                    int  mLeftMargin = DisplayUtil.dp2px(mContext, 20);
                    int  mRightMargin = DisplayUtil.dp2px(mContext, 40);
                    int level1Width = DisplayUtil.dp2px(mContext, 28);
                    int level2Width = DisplayUtil.dp2px(mContext, 38);
                    int level3Width = DisplayUtil.dp2px(mContext, 46);
                    int level3Height = DisplayUtil.dp2px(mContext, 58);
                    int distance = (mViewWidth - mLeftMargin - mRightMargin) / mDataList.size();
                    int mBootomHeight = DisplayUtil.dp2px(mContext, 10);

                    int level1Size = (mDataList.size() - 1) / 2;
                    int level2Size = mDataList.size() - 1;

                    //控件向下挪一些，因为图片边缘有很多空白
                    int bootomPadding = DisplayUtil.dp2px(mContext, 3);

                    int  y = mViewHeight * 2 / 3 + mBootomHeight;

                    int x = i*distance + mLeftMargin;

                    int tipLeft = 0 ;
                    int tipTop = 0 ;
                    int tipRight = 0;
                    int tipBottom = 0;

                    tipLeft = x - tipW/2;
                    tipRight = x + tipW/2;

                    if (i <= level1Size) {
                        tipTop = y - level1Width - tipH + bootomPadding;
                        tipBottom = y - level1Width  + bootomPadding;

                    } else if (i > level1Size && i <= level2Size) {
                        tipTop = y - level2Width  - tipH +bootomPadding;
                        tipBottom =  y - level2Width  +bootomPadding;

                    } else if (i == mDataList.size()) {
                        tipTop = y - level3Height  - tipH +bootomPadding;
                        tipBottom = y - level3Height  + bootomPadding;
                    }

                    mTipTopY = tipTop;
                    mTvTipView.layout(tipLeft,tipTop,tipRight,tipBottom);

                    break;
                }
            }
        }

    }


    private void setAnimation(){

//       ValueAnimator valueAnimator =  ValueAnimator.ofInt(0,100,0);
//        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//              Integer dx = (Integer) valueAnimator.getAnimatedValue();
//                getChildAt(1).setTop(200- dx);
//            }
//        });
//        valueAnimator.start();
          int moveDistance =  DisplayUtil.dp2px(mContext,5);
        ObjectAnimator objectAnimator =  ObjectAnimator.ofFloat(mTvTipView,"translationY",mTipTopY,mTipTopY-moveDistance,mTipTopY);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(2000);
        objectAnimator.start();

    }

    private ReadTaskView.onClickLisenter mOnClickLisenter;

    public interface onClickLisenter {
        void onReceive(int index, ReadTaskBean dataItem);
    }

    public void setOnClicksenter(ReadTaskView.onClickLisenter onClicksenter) {
        mOnClickLisenter = onClicksenter;
    }

}
