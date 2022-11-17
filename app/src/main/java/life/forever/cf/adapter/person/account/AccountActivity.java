package life.forever.cf.adapter.person.account;

import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.activtiy.TopUpActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.tool.DeepLinkUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AccountActivity extends BaseActivity {

    @BindView(R.id.money)
    TextView mMoney;
    @BindView(R.id.voucher)
    TextView mVoucher;

    @Override
    protected void initializeView() {
        mTitleBar.showDivider(FALSE);
        mTitleBar.setMiddleText(ACCOUNT_STRING_MY_ACCOUNT);
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        setMoneyNum();
    }

    /**
     * 设置用户余额及书券
     */
    private void setMoneyNum() {
        mMoney.setText(String.valueOf(PlotRead.getAppUser().money));
        mVoucher.setText(String.valueOf(PlotRead.getAppUser().voucher));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_MONEY_CHANGE || message.what == BUS_USER_INFO_SUCCESS) {
            setMoneyNum();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @OnClick(R.id.recharge)
    void onRechargeClick() {

        DeepLinkUtil.addPermanent(AccountActivity.this,"event_account_pay","帐户中心","点击充值","","","","","","");
        Intent intent = new Intent(context, TopUpActivity.class);
        startActivityForResult(intent,PAY_SUCCESS);
    }

    @OnClick(R.id.voucherItem)
    void onVoucherItemClick() {
        Intent intent = new Intent(context, VoucherActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rechargeItem)
    void onRechargeItemClick() {
        Intent intent = new Intent(context, RechargeRecordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.consumeItem)
    void onConsumeItemClick() {
        Intent intent = new Intent(context, ConsumeRecordActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_SUCCESS) {
            if (null != data && data.getBooleanExtra(SUCCESS,false)) {
                PlotRead.getAppUser().fetchUserInfo(AccountActivity.this);
            }
        }
    }
}
