package life.forever.cf.publics.weight;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import life.forever.cf.R;
import life.forever.cf.publics.tool.DisplayUtil;

import java.util.Locale;


public class LevelView extends AppCompatTextView {

    public LevelView(Context context) {
        this(context, null);
    }

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextColor(Color.WHITE);
        setGravity(Gravity.CENTER);
        setBackgroundResource(R.drawable.shape_user_tag_red);
        int left = DisplayUtil.dp2px(context, 3);
        setPadding(left, 0, left, 0);
    }

    public void setLevel(int level) {
        setText(String.format(Locale.getDefault(), "LV%d", level));
        setBackgroundResource(R.drawable.shape_user_level);
        setVisibility(level <= 0 ? View.GONE : View.VISIBLE);
    }

    public void setVipLevel(int level) {
        setText(String.format(Locale.getDefault(), "VIP%d", level));
        setBackgroundResource(R.drawable.shape_vip_level);
        setVisibility(View.VISIBLE);
    }

    public void setFansLevel(int level) {
        setVisibility(View.VISIBLE);
        if (level <= 0) {
            setVisibility(View.GONE);
        }
//        else if (level >= 1 && level <= 4) {
//            setBackgroundResource(R.drawable.shape_user_tag_green);
//        } else if (level >= 5 && level <= 8) {
//            setBackgroundResource(R.drawable.shape_user_tag_gold);
//        }
        else {
            setBackgroundResource(R.drawable.shape_user_tag_red);
        }
    }

}
