package life.forever.cf.activtiy;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.WrapListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;


public class CommentReportActivity extends BaseActivity {

    @BindView(R.id.listView)
    WrapListView mListView;
    @BindView(R.id.editText)
    EditText mEditText;
    @BindView(R.id.wordCount)
    TextView mWordCount;

    List<ReportInfo> infos = new ArrayList<>();
    private int wid;
    private int cid;
    private ReportInfoAdapter reportInfoAdapter;

    @Override
    protected void initializeView() {
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        mTitleBar.setMiddleText(aiye_STRING_REPORT);
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        setContentView(R.layout.activity_comment_report);
        ButterKnife.bind(this);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    boolean isEnable = true;
    @OnTextChanged(value = R.id.editText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterTextChange(Editable s) {
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

    @Override
    protected void initializeData() {
        wid = getIntent().getIntExtra("wid", ZERO);
        cid = getIntent().getIntExtra("cid", ZERO);
        infos.add(new ReportInfo(ONE, getString(R.string.infringement), FALSE));
        infos.add(new ReportInfo(TWO, getString(R.string.personal_attack), FALSE));
        infos.add(new ReportInfo(THREE, getString(R.string.advertising), FALSE));
        infos.add(new ReportInfo(FOUR, getString(R.string.pornographic), FALSE));
        infos.add(new ReportInfo(FIVE, getString(R.string.sensitive), FALSE));
        reportInfoAdapter = new ReportInfoAdapter();
        mListView.setAdapter(reportInfoAdapter);
    }

    @OnItemClick(R.id.listView)
    void onItemClick(int position) {
        ReportInfo info = infos.get(position);
        info.check = !info.check;
        reportInfoAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.submit)
    void onSubmitClick() {
        String reason = "";
        String trim = mEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(trim)) {
            reason += String.format(Locale.getDefault(), "%d,", ZERO);
        }
        for (ReportInfo info : infos) {
            if (info.check) {
                reason += String.format(Locale.getDefault(), "%d,", info.id);
            }
        }
        if (!TextUtils.isEmpty(reason)) {
            reason = reason.substring(ZERO, reason.lastIndexOf(","));
        }
        if (TextUtils.isEmpty(reason) && TextUtils.isEmpty(trim)) {
            PlotRead.toast(PlotRead.INFO, getString(R.string.report_empty));
            return;
        }
        showLoading(getString(R.string.is_submitted));
        NetRequest.workCommentReport(wid, cid, reason, trim, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        PlotRead.toast(PlotRead.SUCCESS, getString(R.string.report_success));
                        onBackPressed();
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(CommentReportActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    class ReportInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_comment_report, parent, FALSE);
            ViewHolder holder = new ViewHolder(view);
            ReportInfo info = infos.get(position);
            holder.textView.setText(info.name);
            holder.checkBox.setChecked(info.check);
            return view;
        }
    }

    class ViewHolder {
        @BindView(R.id.textView)
        TextView textView;
        @BindView(R.id.checkBox)
        CheckBox checkBox;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    class ReportInfo {
        int id;
        String name;
        boolean check;

        public ReportInfo(int id, String name, boolean check) {
            this.id = id;
            this.name = name;
            this.check = check;
        }
    }
}
