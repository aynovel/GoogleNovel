package life.forever.cf.publics.weight;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;


public class TaskCheckBox extends AppCompatCheckBox {
    public TaskCheckBox(Context context) {
        super(context);
    }

    public TaskCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TaskCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        return callOnClick();
    }

}
