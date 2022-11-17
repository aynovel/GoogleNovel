package life.forever.cf.publics.tool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class CustomShadowView extends AppCompatImageView {
    private final Paint mPaint;

    public CustomShadowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
         mPaint.setShadowLayer(10F, 15F, 15F, Color.GRAY);
        RectF rect = new RectF(0 , 0, 200, 200);
        canvas.drawRoundRect(rect, (float)75, (float)75, mPaint);
    }

}
