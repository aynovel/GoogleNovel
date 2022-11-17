package life.forever.cf.publics.tool;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import life.forever.cf.activtiy.PlotRead;


public class SelectorFactory {

    public static StateListDrawable mkCheckedSelector(int checkedResId, int unCheckedResId) {
        StateListDrawable drawable = new StateListDrawable();
        Drawable checked = PlotRead.getApplication().getResources().getDrawable(checkedResId);
        Drawable unChecked = PlotRead.getApplication().getResources().getDrawable(unCheckedResId);
        drawable.addState(new int[]{android.R.attr.state_checked}, checked);
        drawable.addState(new int[]{-android.R.attr.state_checked}, unChecked);
        return drawable;
    }

    public static ColorStateList mkCheckedColorSelector(int normal, int checked) {
        int[] colors = new int[]{checked, normal};
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_checked};
        states[1] = new int[]{-android.R.attr.state_checked};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

}
