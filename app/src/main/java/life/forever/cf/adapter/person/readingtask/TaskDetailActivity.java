package life.forever.cf.adapter.person.readingtask;

import android.os.Message;
import android.view.View;
import android.widget.TextView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.Task;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.viewtext.MagnetTextView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TaskDetailActivity extends BaseActivity {

    Task task;
    @BindView(R.id.description)
    TextView mDescription;
    @BindView(R.id.money)
    TextView mMoney;
    @BindView(R.id.experience)
    TextView mExperience;
    @BindView(R.id.complete)
    MagnetTextView mComplete;

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);
        initializeData();
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    protected void initializeData() {
        task = getIntent().getParcelableExtra("task");
        mTitleBar.setMiddleText(task.title);
        mDescription.setText(task.description);
        mMoney.setText(String.format(Locale.getDefault(), TASK_STRING_AWARD_VOUCHER, task.giving, task.givingType == ONE ? getString(R.string.bean): getString(R.string.book_money)));
        mMoney.setVisibility(task.giving == ZERO ? View.GONE : View.VISIBLE);
//        mExperience.setText(String.format(Locale.getDefault(), TASK_STRING_AWARD_EXPERIENCE, task.experience));
        mExperience.setVisibility(task.experience == ZERO ? View.GONE : View.VISIBLE);

        if (task.status == ZERO) {
            mComplete.setText(TASK_STRING_WAIT_COMPLETED);
            mComplete.setEnabled(FALSE);
        } else if (task.status == ONE) {
            mComplete.setText(TASK_STRING_RECEIVE);
            mComplete.setEnabled(TRUE);
        } else {
            mComplete.setText(TASK_STRING_COMPLETED);
            mComplete.setEnabled(FALSE);
        }
    }

    @OnClick(R.id.complete)
    void onCompleteClick() {
        showLoading(context.getString(R.string.collecting));
        NetRequest.receiveTaskAward(task.id, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                if(ComYou.isDestroy(TaskDetailActivity.this)){
                    return;
                }
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        task.status = TWO;
                        mComplete.setText(TASK_STRING_COMPLETED);
                        mComplete.setEnabled(FALSE);
                        // 刷新用户余额
                        PlotRead.getAppUser().fetchUserMoney();
                        // 弹窗
                        ReceiveAwardSuccessDialog.show(context, context.getString(R.string.congratulations_success), task, null);
                        // 发通知
                        Message message = Message.obtain();
                        message.what = BUS_TASK_STATUS_CHANGE;
                        message.obj = task.type;
                        EventBus.getDefault().post(message);
                        // 如果是新手礼包，则通知书城取消领取按钮
                        if (task.id == 17) {
                            Message receive = Message.obtain();
                            receive.what = BUS_FRESH_GIFT_RECEIVED;
                            EventBus.getDefault().post(receive);
                        }
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(TaskDetailActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

}
