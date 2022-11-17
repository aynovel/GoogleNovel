package life.forever.cf.adapter.person.personcenter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHelpDetailsActivity extends BaseActivity {

    @BindView(R.id.layout_answer)
    View mLayoutAnswer;
    @BindView(R.id.layout_none)
    LinearLayout mLayoutNone;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_answer)
    TextView mTvAnswer;
    @BindView(R.id.feed_back)
    TextView mFeedBack;

    @BindView(R.id.web_answer)
    WebView mWebAnswer;


    private int id;
    private String mAnswer = null;
    private Intent intent = new Intent();

    @Override
    protected void initializeView() {
        id = getIntent().getIntExtra("id", ZERO);
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.showRightImageView(FALSE);
        /*mTitleBar.setMiddleText(getString(R.string.mine_help));*/
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_user_help_details);
        ButterKnife.bind(this);

        mWebAnswer.setHorizontalScrollBarEnabled(false);//水平不显示
        mWebAnswer.setVerticalScrollBarEnabled(false); //垂直不显示
        mFeedBack.setOnClickListener(onFeedbackClick);
    }

    @Override
    protected void initializeData() {
        problemanswer();
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private final View.OnClickListener onFeedbackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
                intent = new Intent();
                intent.setClass(context, FeedbackActivity.class);
            } else {
                intent.setClass(context, LoginActivity.class);
            }
            startActivity(intent);
        }
    };


    private void problemanswer() {
        NetRequest.problemanswer(id, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String mResultData = jsonObject.getString("ResultData");
                        JSONObject mData = new JSONObject(mResultData);
                        String mDataString = mData.getString("data");
                        JSONObject json = new JSONObject(mDataString);
                        String mTitle = json.getString("title");
                        mAnswer = json.getString("content");
                        switchPageBySize();
                        /*DisplayMetrics outMetrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
                        int widthPixels = outMetrics.widthPixels;
                        int heightPixels = outMetrics.heightPixels;*/
                        mTvTitle.setText(mTitle);
                        StringBuilder mStringBuilder = new StringBuilder();
                        mStringBuilder.append(getHtmlData(mAnswer));
                        mWebAnswer.loadDataWithBaseURL(null,mStringBuilder.toString(),"text/html", "utf-8", null);
                        /*RichTextUtils.setHtmlToTextView(UserHelpDetailsActivity.this, mTvAnswer, mAnswer, widthPixels);*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    switchPageBySize();
                }
            }

            @Override
            public void onFailure(String error) {
                switchPageBySize();
                PlotRead.toast(PlotRead.FAIL, "Request failed,Please try again later！");
            }
        });
    }

    /**
     * 富文本适配
     */
    private String getHtmlData(String bodyHTML) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:auto; height:auto;}</style>"
                + "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    /**
     * 根据作品数量是否为0来切换页面状态
     */
    private void switchPageBySize() {
        if (TextUtils.isEmpty(mAnswer)) {
            mLayoutAnswer.setVisibility(View.GONE);
            mLayoutNone.setVisibility(View.VISIBLE);
        } else {
            mLayoutAnswer.setVisibility(View.VISIBLE);
            mLayoutNone.setVisibility(View.GONE);
        }

    }

}
