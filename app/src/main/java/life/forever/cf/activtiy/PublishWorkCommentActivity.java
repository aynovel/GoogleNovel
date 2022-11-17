package life.forever.cf.activtiy;

import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Comment;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.TextCheckUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;


public class PublishWorkCommentActivity extends BaseActivity {

    private int wid;

    @BindView(R.id.ratingBar)
    RatingBar mRatingBar;
    @BindView(R.id.editText)
    EditText mEditText;
    @BindView(R.id.wordCount)
    TextView mWordCount;

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(COMMENT_STRING_PUBLISH_COMMENT);
        mTitleBar.setRightText(aiye_STRING_PUBLISH);
        mTitleBar.getRightTextView().setTextColor(THEME_COLOR);
        mTitleBar.showRightImageView(FALSE);
        mTitleBar.setRightTextViewOnClickListener(onPublishClick);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_publish_work_comment);
        ButterKnife.bind(this);
    }

    boolean isEnable = true;
    @OnTextChanged(value = R.id.editText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChanged(Editable s) {

        String mString = s.toString().trim();
        int count = 0;//统计空格个数
        String mNewString = mString.replaceAll("\\s{1,}", " ");
        if (TextUtils.isEmpty(mNewString)||mNewString == "" ||mNewString == " "){
            count = 0;
        }else{
            count = 1;
        }
        for (int i = 0; i < mNewString.length(); i++) {
            char tem = mNewString.charAt(i);
            if (tem == ' ') // 空格
            {
                count++;
            }
        }
        mWordCount.setText(String.format(Locale.getDefault(), "%d/3000", count));
        if (count >= THREE_THOUSAND) {
            if (isEnable){
                mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(s.length()){}});
                isEnable = false;
            }

            PlotRead.toast(PlotRead.INFO, getString(R.string.maximum_word));
        }else{
            isEnable = true;
            mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50000){}});
        }
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private final View.OnClickListener onPublishClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!PlotRead.getAppUser().login()) {
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                return;
            }
            int score = mRatingBar.getProgress();
            String trim = mEditText.getText().toString().trim();
            if (TextCheckUtil.isEmpty(trim)) {
                PlotRead.toast(PlotRead.INFO, getString(R.string.input_content));
                mEditText.setText(BLANK);
                return;
            }
            showLoading(getString(R.string.statement));
            if (score == ZERO) {
                addComment(trim);
            } else {
                addScoreComment(trim, score);
            }
        }
    };

    @Override
    protected void initializeData() {
        wid = getIntent().getIntExtra("wid", ZERO);
    }

    private void addScoreComment(String content, int score) {
        NetRequest.workAddScoreComment(wid, score, content, okHttpResult);
    }

    private void addComment(String content) {
        NetRequest.workAddComment(wid, ZERO, ONE, ZERO, ZERO,
                PlotRead.getAppUser().uid, BLANK, content, okHttpResult);
    }

    private final OkHttpResult okHttpResult = new OkHttpResult() {

        @Override
        public void onSuccess(JSONObject data) {
            dismissLoading();
            String serverNo = JSONUtil.getString(data, "ServerNo");
            if (SN000.equals(serverNo)) {
                JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                int status = JSONUtil.getInt(result, "status");
                if (status == ONE) {
                    JSONObject child = JSONUtil.getJSONObject(result, "comment");
                    Comment comment = BeanParser.getComment(child);
                    // 发送通知
                    Message message = Message.obtain();
                    message.what = BUS_WORK_COMMENT_ADD_SUCCESS;
                    message.obj = comment;
                    EventBus.getDefault().post(message);
                    mFirebaseAnalytics.setUserProperty("comment_user", "1");
                    PlotRead.toast(PlotRead.SUCCESS, getString(R.string.published_success));
                    onBackPressed();
                } else {
                    String msg = JSONUtil.getString(result, "msg");
                    PlotRead.toast(PlotRead.FAIL, msg);
                }
            } else {
                NetRequest.error(PublishWorkCommentActivity.this, serverNo);
            }
        }

        @Override
        public void onFailure(String error) {
            dismissLoading();
            PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
        }
    };
}
