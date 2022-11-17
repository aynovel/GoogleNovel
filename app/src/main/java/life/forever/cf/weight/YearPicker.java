package life.forever.cf.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import life.forever.cf.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class YearPicker extends WheelPicker<Integer> {

    private int mStartYear, mEndYear;
    private int mSelectedYear;
    private OnYearSelectedListener mOnYearSelectedListener;

    public YearPicker(Context context) {
        this(context, null);
    }

    public YearPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YearPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        setItemMaximumWidthText("0000");
        updateYear();
        setSelectedYear(mSelectedYear, false);
        setOnWheelChangeListener(new OnWheelChangeListener<Integer>() {
            @Override
            public void onWheelSelected(Integer item, int position) {
                mSelectedYear = item;
                if (mOnYearSelectedListener != null) {
                    mOnYearSelectedListener.onYearSelected(item);
                }
            }
        });
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        mSelectedYear = Calendar.getInstance().get(Calendar.YEAR);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.YearPicker);
        mStartYear = a.getInteger(R.styleable.YearPicker_startYear, 1970);
        mEndYear = a.getInteger(R.styleable.YearPicker_endYear, Calendar.getInstance().get(Calendar.YEAR));
        a.recycle();

    }

    private void updateYear() {
        List<Integer> list = new ArrayList<>();
        for (int i = mStartYear; i <= mEndYear; i++) {
            list.add(i);
        }
        setDataList(list);
    }

    public void setYear(int startYear, int endYear) {
        mStartYear = startYear;
        mEndYear = endYear;
        updateYear();
    }

    public void setSelectedYear(int selectedYear) {
        setSelectedYear(selectedYear, true);
    }

    public void setSelectedYear(int selectedYear, boolean smoothScroll) {
        mSelectedYear = selectedYear;

        setCurrentPosition(mSelectedYear - mStartYear, smoothScroll);
    }

    public int getSelectedYear() {
        return mSelectedYear;
    }

    public void setOnYearSelectedListener(OnYearSelectedListener onYearSelectedListener) {
        mOnYearSelectedListener = onYearSelectedListener;
    }

    public interface OnYearSelectedListener {
        void onYearSelected(int year);
    }

}
