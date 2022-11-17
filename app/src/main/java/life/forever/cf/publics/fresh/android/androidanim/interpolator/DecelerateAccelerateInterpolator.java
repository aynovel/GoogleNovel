package life.forever.cf.publics.fresh.android.androidanim.interpolator;

import android.view.animation.Interpolator;


public class DecelerateAccelerateInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        float result;
        if (input <= 0.5) {
            result = (float) (Math.sin(Math.PI * input)) / 2;
        } else {
            result = (float) (2 - Math.sin(Math.PI * input)) / 2;
        }
        return result;
    }
}
