package life.forever.cf.activtiy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.entry.AppUser;
import life.forever.cf.entry.OverLimmitBook;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.BonusOverTimeDialog;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.JSONUtil;
import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yuanlong on 2021/02/21.
 * 钱包页面
 */
public class WalletActivity extends BaseActivity {

    @BindView(R.id.tv_coins)
    TextView mTvCoins;
    @BindView(R.id.tv_coupons)
    TextView mTvCoupons;
    //过期书卷提示
    @BindView(R.id.tvTip)
    TextView mTvTip;
    private final Intent intent = new Intent();

    private BonusOverTimeDialog mBonusDialog;

    //即将过期数据
    private OverLimmitBook mOverData;

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(application.getString(R.string.mine_wallet));
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initializeData() {
        AppUser user = PlotRead.getAppUser();
        mTvCoins.setText(user.money + "");
        mTvCoupons.setText(user.voucher + "");
        requestOverLimitBooks();
    }

    public void LayoutReceivedClick(View view) {
        if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
             intent.setClass(context, BillActivity.class);
            intent.putExtra("title", application.getString(R.string.bill_activity_received));
            intent.putExtra("type", ZERO);
        } else {
            intent.setClass(context, LoginActivity.class);
        }
        startActivity(intent);
    }

    public void LayoutConsumedClick(View view) {
        if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
             intent.setClass(context, BillActivity.class);
            intent.putExtra("title", application.getString(R.string.bill_activity_consumed));
            intent.putExtra("type", ONE);
        } else {
            intent.setClass(context, LoginActivity.class);
        }
        startActivity(intent);
    }


    public void LayoutExpiredClick(View view) {
        if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
            DeepLinkUtil.addPermanent(context, "event_wallet_expired", "钱包页", "书券过期页", "", "", "", "", "", "");
            intent.setClass(context, BillActivity.class);
            intent.putExtra("title", application.getString(R.string.bill_activity_expired));
            intent.putExtra("type", TWO);
        } else {
            intent.setClass(context, LoginActivity.class);
        }
        startActivity(intent);
    }

    //解锁章节记录
    public void LayoutChapterUnlockClick(View view) {
        if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
            DeepLinkUtil.addPermanent(context, "event_wallet_unlocked", "钱包页", "章节解锁页", "", "", "", "", "", "");
            intent.setClass(context, ChapterUnlockActivity.class);
        } else {
            intent.setClass(context, LoginActivity.class);
        }
        startActivity(intent);
    }

    //充值页
    public void LayoutTopUpClick(View view) {
        if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
            DeepLinkUtil.addPermanent(context, "event_wallet_topup", "钱包页", "充值按钮", "", "", "", "", "", "");
            intent.setClass(context, TopUpActivity.class);
            startActivityForResult(intent, PAY_SUCCESS);
        } else {
            intent.setClass(context, LoginActivity.class);
            startActivity(intent);
        }

    }

    private final View.OnClickListener onBackClick = v -> onBackPressed();


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 请求用户三日内即将过期书券接口，判断是存在->显示呼吸灯
     */
    private void requestOverLimitBooks(){
        NetRequest.getOverTimeBook(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {

                String ServerNo = JSONUtil.getString(data,"ServerNo");

                if (ServerNo.equals(SN000)){
                    JSONObject resultObject = JSONUtil.getJSONObject(data,"ResultData");
                    int status =  JSONUtil.getInt(resultObject,"status");

                    if (status == 1){

                        String dataString =  JSONUtil.getString(resultObject,"data");
                        OverLimmitBook overLimmitBook = new Gson().fromJson(dataString,OverLimmitBook.class);
                        mOverData = overLimmitBook;
                        if (overLimmitBook.list != null && overLimmitBook.list.size() >= 0){
                            //呼吸灯
                           String numString = overLimmitBook.bonus + " " + getString(R.string.wallet_bonus_tip);
                           mTvTip.setText(numString);

                        }else {


                        }

                    } else {


                    }
                }

            }

            @Override
            public void onFailure(String error) {



            }
        });

    }

    @OnClick({R.id.tvDetail})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tvDetail:
                if (mOverData != null){
                    showDialog(mOverData);
                }
                break;
        }
    }


    private void showDialog(OverLimmitBook data){
        mBonusDialog = new BonusOverTimeDialog(context,data);
        mBonusDialog.show();
    }

}
