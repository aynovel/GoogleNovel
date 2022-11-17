package life.forever.cf.publics.weight.imagespan;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;


public class AlignMiddleImageSpan extends ImageSpan {

    public static final int ALIGN_MIDDLE = -100;

    private float mFontWidthMultiple = -1f;


    private boolean mAvoidSuperChangeFontMetrics = true;

    @SuppressWarnings("FieldCanBeLocal")
    private int mWidth;

    public AlignMiddleImageSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }


    public AlignMiddleImageSpan(Drawable d, int verticalAlignment, float fontWidthMultiple) {
        this(d, verticalAlignment);
        if (fontWidthMultiple >= 0) {
            mFontWidthMultiple = fontWidthMultiple;
        }
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (mAvoidSuperChangeFontMetrics) {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            mWidth = rect.right;
        } else {
            mWidth = super.getSize(paint, text, start, end, fm);
        }
        if (mFontWidthMultiple > 0) {
            mWidth = (int) (paint.measureText("华") * mFontWidthMultiple);
        }
        return mWidth;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        if (mVerticalAlignment == ALIGN_MIDDLE) {
            Drawable d = getDrawable();
            canvas.save();
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int fontTop = y + fontMetricsInt.top;
            int fontMetricsHeight = fontMetricsInt.bottom - fontMetricsInt.top;
            int iconHeight = d.getBounds().bottom - d.getBounds().top;
            int iconTop = fontTop + (fontMetricsHeight - iconHeight) / 2;
            canvas.translate(x, iconTop);
            d.draw(canvas);
            canvas.restore();
        } else {
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }
    }

    /**
     * 是否避免父类修改FontMetrics，如果为 false 则会走父类的逻辑, 会导致FontMetrics被更改
     */
    public void setAvoidSuperChangeFontMetrics(boolean avoidSuperChangeFontMetrics) {
        mAvoidSuperChangeFontMetrics = avoidSuperChangeFontMetrics;
    }
}
