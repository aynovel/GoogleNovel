package life.forever.cf.adapter.person;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.publics.BaseRecyclerViewActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.DisplayUtil;
import life.forever.cf.publics.weight.viewtext.MagnetTextView;

import java.util.ArrayList;
import java.util.List;

import life.forever.cf.adapter.ReadPreferenceAdapter;


public class ReadPreferenceSettingActivity extends BaseRecyclerViewActivity {

    List<ReadPreferenceAdapter.Preference> preferences = new ArrayList<>();

    @Override
    protected void initializeView() {
        super.initializeView();
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setMiddleText(Constant.aiye_STRING_READ_PREFERENCE_SETTING);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        mRefreshLayout.setHasFooter(FALSE);
        mRefreshLayout.setHasHeader(FALSE);

        LinearLayout linearLayout = new LinearLayout(getBaseContext());
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        mContentLayout.removeView(mRefreshLayout);
        setContentView(linearLayout);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ZERO);
        p.weight = ONE;
        mRefreshLayout.setLayoutParams(p);

        TextView textView = new MagnetTextView(getBaseContext());
        textView.setBackgroundColor(THEME_COLOR);
        textView.setText(aiye_STRING_SUBMIT);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, SIXTEEN);
        textView.setTextColor(Color.WHITE);
        int padding = DisplayUtil.dp2px(getBaseContext(), SIXTEEN);
        textView.setPadding(ZERO, padding, ZERO, padding);
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        linearLayout.addView(mRefreshLayout);
        linearLayout.addView(textView);
    }

    @Override
    protected void initializeData() {
        preferences.add(new ReadPreferenceAdapter.Preference());
        preferences.add(new ReadPreferenceAdapter.Preference());
        preferences.add(new ReadPreferenceAdapter.Preference());
        preferences.add(new ReadPreferenceAdapter.Preference());
        preferences.add(new ReadPreferenceAdapter.Preference());
        preferences.add(new ReadPreferenceAdapter.Preference());
        mRecyclerView.setAdapter(new ReadPreferenceAdapter(getBaseContext(), preferences));
    }
}
