package life.forever.cf.weight;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;

import life.forever.cf.manage.ReadSettingManager;
import life.forever.cf.activtiy.LogUtils;


public class AutoReadPageAnim extends HorizonPageAnim {

    private Rect mSrcRect, mDestRect;
    private GradientDrawable mBackShadowDrawableLR;
    private int timePassed = 0;
    private int touchStartY = 0;

    public AutoReadPageAnim(int w, int h, View view, OnPageChangeListener listener) {
        super(w, h, view, listener);
        mSrcRect = new Rect(0, 0, mViewWidth, mViewHeight);
        mDestRect = new Rect(0, 0, mViewWidth, mViewHeight);
        int[] mBackShadowColors = new int[] { 0x66000000,0x00000000};
        mBackShadowDrawableLR = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, mBackShadowColors);
        mBackShadowDrawableLR.setGradientType(GradientDrawable.LINEAR_GRADIENT);
    }

    @Override
    public void drawStatic(Canvas canvas) {
        if (isCancel){
            mNextBitmap = mCurBitmap.copy(Bitmap.Config.RGB_565, true);
            canvas.drawBitmap(mCurBitmap, 0, 0, null);
        }else {
            canvas.drawBitmap(mNextBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取点击位置
        int x = (int)event.getX();
        int y = (int)event.getY();
        //设置触摸点
        setTouchPoint(x,y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                timePassed = mScroller.timePassed();
                pauseAnim();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mBeginMoveAnimation == false){
                    mListener.moveTurnPageBegin();
                    mBeginMoveAnimation = true;
                }

                mView.invalidate();

                touchStartY = y;
                break;
            case MotionEvent.ACTION_UP:
                touchStartY = y;
                restartAnim();
                break;
        }
        return true;
    }

    public void restartAnim() {
        mView.invalidate();
        startAnim();
        mListener.moveTurnPageFinished(true);
    }

    public void pauseAnim() {
        int hdis = (int) (mViewHeight - mStartY + mTouchY);

        if (!mScroller.isFinished()){
            mScroller.abortAnimation();
        }
        isAutoReadRunning = false;

        touchStartY = hdis;
    }

    @Override
    public void drawMove(Canvas canvas) {

        switch (mDirection){
            case NEXT:

                int hdis = (int) (mViewHeight - mStartY + mTouchY);
                if (hdis > mViewHeight) {
                    hdis = mViewHeight;
                }
                mSrcRect.top = hdis;
                mDestRect.top = hdis;
                canvas.drawBitmap(mNextBitmap,0,0,null);
                canvas.drawBitmap(mCurBitmap,mSrcRect,mDestRect,null);
                addShadow(hdis,canvas);

                break;
            default:

                mSrcRect.top = mViewHeight-(int) mTouchY;
                mDestRect.top = mViewHeight-(int) mTouchY;
                canvas.drawBitmap(mNextBitmap,0,0,null);
                canvas.drawBitmap(mCurBitmap,mSrcRect,mDestRect,null);
                addShadow((int) mTouchY,canvas);

                break;
        }
    }

    //添加阴影
    public void addShadow(int top,Canvas canvas) {
        mBackShadowDrawableLR.setBounds(0, top, mScreenWidth , top+30);
        mBackShadowDrawableLR.draw(canvas);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
//        canvas.drawBitmap(mBgBitmap, 0,0, null);
        int hdis = (int) ((mViewHeight - mStartY) + mTouchY);


        if (hdis >= mViewHeight) {
            mListener.autoReadPageFinish();
        }
    }

    @Override
    public void startAnim() {
        super.startAnim();
        int readSpeed = ReadSettingManager.getInstance().getAutoReadSpeed();
        readSpeed = 49-readSpeed;
        int duration = ((readSpeed*1000) * (mViewHeight-touchStartY)) / mViewHeight;
        LogUtils.e("autoread", "----" + duration);
        mScroller.startScroll(0, touchStartY, 0, mViewHeight-touchStartY, duration);
        touchStartY = 0;
        isAutoReadRunning = true;
    }

    public void pauseScroll() {
    }

    @Override
    public void resetAnim() {
    }
}
