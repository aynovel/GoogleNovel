package life.forever.cf.publics.fresh.android.resolver;


import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;

import life.forever.cf.publics.fresh.android.able.Pullable;

public interface IEventResolver extends NestedScrollingChild, NestedScrollingParent {

    Pullable getPullAble(View view);

    Pullable getPullAble(Pullable pullable);

    boolean isScrolling();

    void setViewTranslationP(View view, float value);

    boolean dispatchTouchEvent(MotionEvent ev);

    boolean interceptTouchEvent(MotionEvent ev);

    boolean touchEvent(MotionEvent ev);

    float getVelocity();

    void onDetachedFromWindow();
}
