package life.forever.cf.popup;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.RewardInfo;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.activtiy.TopUpActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RewardPopup extends PopupWindow implements Constant {

    @BindView(R.id.my_coins)
    TextView my_coins;
    @BindView(R.id.money_1)
    TextView money_1;
    @BindView(R.id.money_2)
    TextView money_2;
    @BindView(R.id.money_3)
    TextView money_3;
    @BindView(R.id.money_4)
    TextView money_4;
    @BindView(R.id.money_5)
    TextView money_5;
    @BindView(R.id.money_6)
    TextView money_6;

    @BindView(R.id.ll_bg_1)
    LinearLayout ll_bg_1;
    @BindView(R.id.ll_bg_2)
    LinearLayout ll_bg_2;
    @BindView(R.id.ll_bg_3)
    LinearLayout ll_bg_3;
    @BindView(R.id.ll_bg_4)
    LinearLayout ll_bg_4;
    @BindView(R.id.ll_bg_5)
    LinearLayout ll_bg_5;
    @BindView(R.id.ll_bg_6)
    LinearLayout ll_bg_6;

    @BindView(R.id.confirm)
    TextView mConfirm;

    private final BaseActivity activity;
//    private TextView mCustomMoney;
    private final List<RewardInfo> rewardInfos = new ArrayList<>();
//    private RewardAdapter rewardAdapter;
    private int current = ZERO;

    private final Work work;
    private int cid;

    public RewardPopup(BaseActivity activity, Work work) {
        this.activity = activity;
        this.work = work;
        View root = LayoutInflater.from(activity).inflate(R.layout.layout_reward_popup, null, FALSE);
        setContentView(root);
        ButterKnife.bind(this, root);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_slide_alpha_bottom_style);
        setFocusable(TRUE);
        setOutsideTouchable(FALSE);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        fetchList();
    }
    private void rewardlist(){
        RewardInfo rewardInfo = new RewardInfo();
        for (int i = 0; i < rewardInfos.size(); i++) {
            switch (i){
                case 0:
                    rewardInfo = rewardInfos.get(0);
                    money_1.setText(String.format(Locale.getDefault(), rewardInfo.coinType == ONE ? "%d "+activity.getString(R.string.coins) : "%d "+activity.getString(R.string.coins), rewardInfo.money));
                    break;
                case 1:
                    rewardInfo = rewardInfos.get(1);
                    money_2.setText(String.format(Locale.getDefault(), rewardInfo.coinType == ONE ? "%d "+activity.getString(R.string.coins) : "%d "+activity.getString(R.string.coins), rewardInfo.money));

                    break;
                case 2:
                    rewardInfo = rewardInfos.get(2);
                    money_3.setText(String.format(Locale.getDefault(), rewardInfo.coinType == ONE ? "%d "+activity.getString(R.string.coins) : "%d "+activity.getString(R.string.coins), rewardInfo.money));

                    break;
                case 3:
                    rewardInfo = rewardInfos.get(3);
                    money_4.setText(String.format(Locale.getDefault(), rewardInfo.coinType == ONE ? "%d "+activity.getString(R.string.coins) : "%d "+activity.getString(R.string.coins), rewardInfo.money));

                    break;
                case 4:
                    rewardInfo = rewardInfos.get(4);
                    money_5.setText(String.format(Locale.getDefault(), rewardInfo.coinType == ONE ? "%d "+activity.getString(R.string.coins) : "%d "+activity.getString(R.string.coins), rewardInfo.money));

                    break;
                case 5:
                    rewardInfo = rewardInfos.get(5);
                    money_6.setText(String.format(Locale.getDefault(), rewardInfo.coinType == ONE ? "%d "+activity.getString(R.string.coins) : "%d "+activity.getString(R.string.coins), rewardInfo.money));

                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + rewardInfos.size());

            }
        }
    }
    private void fetchList() {
        NetRequest.rewardList(new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                        for (int i = ZERO; lists != null && i < lists.length(); i++) {
                            JSONObject child = JSONUtil.getJSONObject(lists, i);
                            RewardInfo rewardInfo = BeanParser.getReward(child);
                            rewardInfos.add(rewardInfo);
                        }
                        rewardlist();
                        initSubmitBtnText();
                    }
                } else {
                    NetRequest.error(activity, serverNo);
                    dismiss();
                }
            }

            @Override
            public void onFailure(String error) {

            }
        });
    }


    @OnClick(R.id.ll_bg_1)
    public  void ll_bg_1(){
        bg(ll_bg_1,0);
    }
    @OnClick(R.id.ll_bg_2)
    public  void ll_bg_2(){
        bg(ll_bg_2,1);
    }
    @OnClick(R.id.ll_bg_3)
    public  void ll_bg_3(){
        bg(ll_bg_3,2);
    }
    @OnClick(R.id.ll_bg_4)
    public  void ll_bg_4(){
        bg(ll_bg_4,3);
    }
    @OnClick(R.id.ll_bg_5)
    public  void ll_bg_5(){
        bg(ll_bg_5,4);
    }
    @OnClick(R.id.ll_bg_6)
    public  void ll_bg_6(){
        bg(ll_bg_6,5);
    }


    public void bg(LinearLayout linearLayout,int o){
        ll_bg_1.setBackgroundResource(R.drawable.shape_d4d5d6_corner_20dp);
        ll_bg_2.setBackgroundResource(R.drawable.shape_d4d5d6_corner_20dp);
        ll_bg_3.setBackgroundResource(R.drawable.shape_d4d5d6_corner_20dp);
        ll_bg_4.setBackgroundResource(R.drawable.shape_d4d5d6_corner_20dp);
        ll_bg_5.setBackgroundResource(R.drawable.shape_d4d5d6_corner_20dp);
        ll_bg_6.setBackgroundResource(R.drawable.shape_d4d5d6_corner_20dp);
        linearLayout.setBackgroundResource(R.drawable.shape_theme_corner_20dp);
        current = o;
        if (rewardInfos == null || rewardInfos.size()== 0) {
            return;
        }
        RewardInfo rewardInfo = rewardInfos.get(current);
        if (rewardInfo.coinType == ONE) { // 书币打赏
            if (rewardInfo.money > PlotRead.getAppUser().money) {
                mConfirm.setText(aiye_STRING_RECHARGE);
            } else {
                mConfirm.setText(aiye_STRING_SEND_OUT);
            }
        } else { // 书豆打赏
            if (rewardInfo.money > PlotRead.getAppUser().voucher) {
                mConfirm.setText(aiye_STRING_RECHARGE);
            } else {
                mConfirm.setText(aiye_STRING_SEND_OUT);
            }
        }
    }

    private void initSubmitBtnText() {
        if (rewardInfos.size() <= ZERO) {
            return;
        }
        RewardInfo rewardInfo = rewardInfos.get(current);
        if (rewardInfo.coinType == ONE) { // 书币打赏
            if (rewardInfo.money > PlotRead.getAppUser().money) {
                mConfirm.setText(aiye_STRING_RECHARGE);
            } else {
                mConfirm.setText(aiye_STRING_SEND_OUT);
            }
        } else { // 书豆打赏
            if (rewardInfo.money > PlotRead.getAppUser().voucher) {
                mConfirm.setText(aiye_STRING_RECHARGE);
            } else {
                mConfirm.setText(aiye_STRING_SEND_OUT);
            }
        }
    }

    public void show(View parent, int cid) {
        this.cid = cid;
        my_coins.setText(String.valueOf(PlotRead.getAppUser().money));
//        mVoucher.setText(String.valueOf(PlotRead.getAppUser().voucher));
        ComYou.setWindowAlpha(activity, DOT_FIVE);
        showAtLocation(parent, Gravity.BOTTOM, ZERO, ZERO);
    }

    @Override
    public void dismiss() {
        ComYou.setWindowAlpha(activity, ONE);
        super.dismiss();
    }

    @OnClick(R.id.confirm)
    void onConfirmClick() {
        if (mConfirm.getText().toString().equals(aiye_STRING_SEND_OUT)) {
            reward();
        } else {
            dismiss();
            Intent intent = new Intent(activity, TopUpActivity.class);
            activity.startActivityForResult(intent,PAY_SUCCESS);
        }
    }

    private void reward() {
        final boolean byMoney;
        final int money;
        int ruleId = ZERO;
        if (rewardInfos.size() <= ZERO) {
            return;
        }
        if (rewardInfos == null || rewardInfos.size() == 0 || current >= rewardInfos.size()) {
           return;
        }
            RewardInfo rewardInfo = rewardInfos.get(current);
            ruleId = rewardInfo.id;
            money = rewardInfo.money;
            byMoney = rewardInfo.coinType == ONE;
//        } else {
//            String trim = mCustomMoney.getText().toString().trim();
//            if (TextUtils.isEmpty(trim) || Integer.parseInt(trim) == ZERO) {
//                RewardInputAlertDialog.show(activity, inputResultListener);
//                return;
//            }
//            money = Integer.parseInt(trim);
//            byMoney = TRUE;
//        }
        activity.showLoading(activity.getString(R.string.detail_rewarding));
        NetRequest.workReward(work.wid, ruleId, money, cid, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                activity.dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        dismiss();
                        // 刷新余额
                        PlotRead.getAppUser().fetchUserMoney();
                        // 发出打赏成功通知
                        Message message = Message.obtain();
                        message.what = BUS_REWARD_SUCCESS;
                        message.obj = work.wid;
                        EventBus.getDefault().post(message);
                        activity.mFirebaseAnalytics.setUserProperty("reward_user", "1");
                        // 分享弹窗
                        RewardShareDialog.show(activity, work, cid,
                                String.format(Locale.getDefault(), activity.getString(R.string.detail_reward)+" " +"%d%s", money, byMoney ? activity.getString(R.string.topup_coins) :
                                        activity.getString(R.string.topup_bouns)));
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.FAIL, activity.getResources().getString(R.string.no_internet));
                    }
                } else {
                    dismiss();
                    NetRequest.error(activity, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                activity.dismissLoading();
                PlotRead.toast(PlotRead.FAIL, activity.getString(R.string.no_internet));
            }
        });
    }



//    private class RewardAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return rewardInfos.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return rewardInfos.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = LayoutInflater.from(activity).inflate(R.layout.item_reward_normal,  null);
//            RewardInfo rewardInfo = rewardInfos.get(position);
//            TextView textView = (TextView) view.findViewById(R.id.tv_money);
//            textView.setText(String.format(Locale.getDefault(), rewardInfo.coinType == ONE ? "%d"+activity.getString(R.string.coins) : "%d"+activity.getString(R.string.coins), rewardInfo.money));
//            return textView;
//        }
//
//
//    }
}
