package life.forever.cf.publics.weight.viewtext;

import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.method.Touch;
import android.view.MotionEvent;
import android.widget.TextView;


public class FixedLinkMovementMethod extends LinkMovementMethod {

    private static FixedLinkMovementMethod sInstance;
    private static final LinkTouchDecorHelper sHelper = new LinkTouchDecorHelper();

    public static MovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new FixedLinkMovementMethod();
        }
        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        return sHelper.onTouchEvent(widget, buffer, event) || Touch.onTouchEvent(widget, buffer, event);
    }

}
