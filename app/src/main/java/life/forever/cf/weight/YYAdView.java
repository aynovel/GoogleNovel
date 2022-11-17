package life.forever.cf.weight;

import android.view.View;


public class YYAdView {

    private int mLineWidth;
    private int mLineHeight;
    private int mStartX;
    private int mStartY;
    private View mAdView;



    private boolean mEndPageFlag;

    public YYAdView(View adView, int x, int y, int width, int height, boolean endPageFlag){
        mAdView = adView;
        mStartX = x;
        mStartY = y;
        mLineWidth = width;
        mLineHeight = height;
        mEndPageFlag = endPageFlag;
    }

    public YYAdView(YYFrame frame){
        mStartX = frame.getX();
        mStartY = frame.getY();
        mLineWidth = frame.getWidth();
        mLineHeight = frame.getHeight();
        mAdView = null;
        mEndPageFlag = false;
    }

    public YYFrame getAdFrame(){
        YYFrame frame = new YYFrame();
        frame.setX(mStartX);
        frame.setY(mStartY);
        frame.setWidth(mLineWidth);
        frame.setHeight(mLineHeight);
        return frame;
    }

    public int getmLineWidth() {
        return mLineWidth;
    }

    public void setmLineWidth(int mLineWidth) {
        this.mLineWidth = mLineWidth;
    }

    public int getmLineHeight() {
        return mLineHeight;
    }

    public void setmLineHeight(int mLineHeight) {
        this.mLineHeight = mLineHeight;
    }

    public int getmStartX() {
        return mStartX;
    }

    public void setmStartX(int mStartX) {
        this.mStartX = mStartX;
    }

    public int getmStartY() {
        return mStartY;
    }

    public void setmStartY(int mStartY) {
        this.mStartY = mStartY;
    }

    public View getmAdView() {
        return mAdView;
    }

    public void setmAdView(View mAdView) {
        this.mAdView = mAdView;
    }

    public boolean ismEndPageFlag() {
        return mEndPageFlag;
    }

    public void setmEndPageFlag(boolean mEndPageFlag) {
        this.mEndPageFlag = mEndPageFlag;
    }

}
