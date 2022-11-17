package life.forever.cf.weight;


public class LineInfo {
    private String mLineText;
    private int mStartPos;
    private int mCharCount;
    private int mCharDistence;
    private float mAdjustOffset;
    private float mStartY;
    LineType mLineType;
    private YYAdView mAdView;
    private int mParaIndex;

    public enum LineType {
        LineTypeMainText, LineTypeTitle, LineTypeFirstLine, LineTypeAdView,LineTypeAdPage
    }

    public enum LineAdType{
        LineAdTypeNone,LineAdTypeInterNative,LineAdTypePage
    }

    public LineInfo(String text, int startPos, int length) {
        mLineText = text;
        if (text == null){
            mLineText = "";
        }
        mStartPos = startPos;
        mCharCount = length;
        mCharDistence = 0;
        mAdjustOffset = 0;
        mLineType = LineType.LineTypeMainText;
    }

    public LineInfo(YYAdView adView){
        mAdView = adView;
        mLineType = LineType.LineTypeAdView;
        mLineText = "";
    }

    public LineAdType getLineAdType(){
        LineAdType adType;
        switch (mLineType){
            case LineTypeAdView: {
                adType = LineAdType.LineAdTypeInterNative;
            }
                break;
            case LineTypeAdPage: {
                adType = LineAdType.LineAdTypePage;
            }
                break;
            default: {
                adType = LineAdType.LineAdTypeNone;
            }
                break;
        }
        return adType;
    }

    public YYAdView getmAdView() {
        return mAdView;
    }

    public void setmAdView(YYAdView mAdView) {
        this.mAdView = mAdView;
    }

    public LineType getmLineType() {
        return mLineType;
    }

    public void setmLineType(LineType mLineType) {
        this.mLineType = mLineType;
    }

    public String getmLineText() {
        return mLineText;
    }

    public void setmLineText(String mLineText) {
        this.mLineText = mLineText;
    }

    public int getmStartPos() {
        return mStartPos;
    }

    public void setmStartPos(int mStartPos) {
        this.mStartPos = mStartPos;
    }

    public int getmCharCount() {
        return mCharCount;
    }

    public void setmCharCount(int mCharCount) {
        this.mCharCount = mCharCount;
    }

    public int getmCharDistence() {
        return mCharDistence;
    }

    public void setmCharDistence(int mCharDistence) {
        this.mCharDistence = mCharDistence;
    }

    public float getmAdjustOffset() {
        return mAdjustOffset;
    }

    public void setmAdjustOffset(float mAdjustOffset) {
        this.mAdjustOffset = mAdjustOffset;
    }

    public float getmStartY() {
        return mStartY;
    }

    public void setmStartY(float mStartY) {
        this.mStartY = mStartY;
    }

    public int getmParaIndex() {
        return mParaIndex;
    }

    public void setmParaIndex(int mParaIndex) {
        this.mParaIndex = mParaIndex;
    }
}
