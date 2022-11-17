package life.forever.cf.publics.weight.imagespan;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

import androidx.annotation.Nullable;


public class CustomTypefaceSpan extends TypefaceSpan {

    public static final Creator<CustomTypefaceSpan> CREATOR = new Creator<CustomTypefaceSpan>() {
        @Override
        public CustomTypefaceSpan createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public CustomTypefaceSpan[] newArray(int size) {
            return new CustomTypefaceSpan[size];
        }
    };
    private final @Nullable
    Typeface newType;


    public CustomTypefaceSpan(String family, @Nullable Typeface type) {
        super(family);
        newType = type;
    }

    private static void applyCustomTypeFace(Paint paint, @Nullable Typeface tf) {
        if (tf == null) {
            return;
        }

        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) {
            oldStyle = 0;
        } else {
            oldStyle = old.getStyle();
        }

        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }

        if ((fake & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }

        paint.setTypeface(tf);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        applyCustomTypeFace(ds, newType);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }
}