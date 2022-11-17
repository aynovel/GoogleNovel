package life.forever.cf.publics.weight.imagespan;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;


public class MarginImageSpan extends AlignMiddleImageSpan {

    private int mSpanMarginLeft = 0;
    private int mSpanMarginRight = 0;
    private int mOffsetY = 0;

    public MarginImageSpan(Drawable d, int verticalAlignment, int marginLeft, int marginRight) {
        super(d, verticalAlignment);
        mSpanMarginLeft = marginLeft;
        mSpanMarginRight = marginRight;
    }

    public MarginImageSpan(Drawable d, int verticalAlignment, int marginLeft, int marginRight, int offsetY) {
        this(d, verticalAlignment, marginLeft, marginRight);
        mOffsetY = offsetY;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (mSpanMarginLeft != 0 || mSpanMarginRight != 0) {
            super.getSize(paint, text, start, end, fm);
            Drawable d = getDrawable();
            return d.getIntrinsicWidth() + mSpanMarginLeft + mSpanMarginRight;
        } else {
            return super.getSize(paint, text, start, end, fm);
        }
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        canvas.save();
        canvas.translate(0, mOffsetY);
        // marginRight不用专门处理，只靠getSize()中改变即可
        super.draw(canvas, text, start, end, x + mSpanMarginLeft, top, y, bottom, paint);
        canvas.restore();
    }
}
