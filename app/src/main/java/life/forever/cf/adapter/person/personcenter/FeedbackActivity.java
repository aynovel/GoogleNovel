package life.forever.cf.adapter.person.personcenter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.datautils.ISNav;
import life.forever.cf.entry.ISListConfig;
import life.forever.cf.datautils.DisplayUtils;
import life.forever.cf.weight.CustomViewPager;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;

import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FeedbackActivity extends BaseActivity {


    @BindView(R.id.et_content)
    EditText mEtContent;
    @BindView(R.id.tv_count)
    TextView mTvCount;
    @BindView(R.id.et_email)
    EditText mEtEmail;

    @BindView(R.id.rvImageList)
    RecyclerView rvImageList;
    @BindView(R.id.tv_hint_num)
    TextView tv_hint_num;

    private CustomViewPager viewPager;
    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.showRightImageView(FALSE);
        mTitleBar.setMiddleText(aiye_STRING_FEEDBACK);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_user_feedback);
        ButterKnife.bind(this);
        mEtContent.addTextChangedListener(textWatcher);

        rvImageList.setLayoutManager(new GridLayoutManager(rvImageList.getContext(), 3));
        rvImageList.addItemDecoration(new RecyclerView.ItemDecoration() {
            final int spacing = DisplayUtils.dip2px(rvImageList.getContext(), 6);
            final int halfSpacing = spacing >> 1;

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = halfSpacing;
                outRect.right = halfSpacing;
                outRect.top = halfSpacing;
                outRect.bottom = halfSpacing;
            }
        });

    }

    private final View.OnClickListener onBackClick = v -> onBackPressed();


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null != this.getCurrentFocus()) {
            InputMethodManager manager = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
            manager.hideSoftInputFromWindow(
                    FeedbackActivity.this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS
            );
        }
    }

    boolean isEnable = true;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String mString = s.toString().trim();
            int count = 0;//统计空格个数
            String mNewString = mString.replaceAll("\\s{1,}", " ");
            if (TextUtils.isEmpty(mNewString) || mNewString.equals("") || mNewString.equals(" ")) {
                count = 0;
            } else {
                count = 1;
            }
            for (int i = 0; i < mNewString.length(); i++) {
                char tem = mNewString.charAt(i);
                if (tem == ' ') // 空格
                {
                    count++;
                }
            }
            mTvCount.setText(String.format(Locale.getDefault(), "%d/2000", count));
            if (count >= THREE_THOUSAND) {
                if (isEnable) {
                    mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(s.length()) {
                    }});
                    isEnable = false;
                }
                PlotRead.toast(PlotRead.INFO, getString(R.string.maximum_word));
            } else {
                isEnable = true;
                mEtContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50000) {
                }});
            }
        }
    };

    //发送反馈
    @OnClick(R.id.feedback_send)
    void onFeedbackSendClick() {
        if (null != this.getCurrentFocus()) {
            InputMethodManager manager = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
            manager.hideSoftInputFromWindow(
                    FeedbackActivity.this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS
            );
        }
        if (!PlotRead.getAppUser().login()) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            return;
        }
        String email = mEtEmail.getText().toString().trim();
        String content = mEtContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            PlotRead.toast(PlotRead.INFO, getString(R.string.please_enter_feedback));
            return;
        }
        if (TextUtils.isEmpty(email)) {
            PlotRead.toast(PlotRead.INFO, getString(R.string.email_null));
            return;
        }
        if (!ComYou.isEmail(email)) {
            PlotRead.toast(PlotRead.INFO, getString(R.string.email_format_wrong));
            return;
        }
        showLoading(getString(R.string.is_submitted));

//        NetRequest.feedback(email, content, new OkHttpResult() {
//
//            @Override
//            public void onSuccess(JSONObject data) {
//                dismissLoading();
//                String serverNo = JSONUtil.getString(data, "ServerNo");
//                if (SN000.equals(serverNo)) {
//                    PlotRead.toast(PlotRead.SUCCESS, getString(R.string.feedback_success));
//                    onBackPressed();
//                } else {
//                    NetRequest.error(FeedbackActivity.this, serverNo);
//                }
//            }
//
//            @Override
//            public void onFailure(String error) {
//                dismissLoading();
//                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
//            }
//        });

        int uid = PlotRead.getAppUser().uid;
        NetRequest.uploadReportInfo(uid, email, content, null, null,
                1, 1, 0, 0,
                new OkHttpResult() {

                    @Override
                    public void onSuccess(JSONObject data) {
                        dismissLoading();
                        String serverNo = JSONUtil.getString(data, "ServerNo");
                        if (SN000.equals(serverNo)) {
                            PlotRead.toast(PlotRead.SUCCESS, getString(R.string.feedback_success));
                            onBackPressed();
                        } else {
                            NetRequest.error(FeedbackActivity.this, serverNo);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        dismissLoading();
                        PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                    }
                });

    }

    @Override
    protected void initializeData() {

    }

    private final int REQUEST_LIST_CODE = 00001;
    public void Multiselect() {
//        tvResult.setText("");
        ISListConfig config = new ISListConfig.Builder()
                .multiSelect(true)
                // 是否记住上次选中记录
                .rememberSelected(false)
                // 使用沉浸式状态栏
                .statusBarColor(Color.parseColor("#3F51B5")).build();

        ISNav.getInstance().toListActivity(this, config, REQUEST_LIST_CODE);
    }
}
