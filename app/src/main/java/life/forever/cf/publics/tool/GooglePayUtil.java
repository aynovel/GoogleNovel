package life.forever.cf.publics.tool;

import static life.forever.cf.publics.tool.DeepLinkUtil.mFirebaseAnalytics;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.PayInfo;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.person.pay.PaymentUtil;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.weight.poputil.LoadingAlertDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import life.forever.cf.publics.Constant;


public class GooglePayUtil {
    /**google pay*/
    private  BillingClient billingClient;
    private  boolean isConnect;
    private final List<String> skuList = new ArrayList<>();
    private SkuDetails  skuDetail = null;
    protected AlertDialog loadingDialog;
    /**后台订单编号*/
    private String mDeveloperPayload = "";
    /**支付信息*/
    private PayInfo mPayInfo;
    private final Activity mActivity;
    private String mGoodsId= "";
    public GooglePayUtil(Activity activity,String goodsId,String aMount,String ruleId,String rmb){
        mActivity = activity;
        mGoodsId = goodsId;
        JSONObject object = JSONUtil.newJSONObject();
        /**
         * 请求支付列表数据
         */
        doPay(rmb,ruleId,1,1,"","");
    }
    /**
     * initializeGooglePay
     */
    public  void initializeGooglePay(String goodsId) {
        billingClient = BillingClient.newBuilder(mActivity).enablePendingPurchases().setListener(mPurchasesUpdatedListener).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The billing client is ready. You can query purchases here.
                    isConnect = true;
//                    Log.e("PayDiamondActivity", "谷歌支付链接成功");
                    getSkuList(goodsId);
                } else {
                    Log.e("PayDiamondActivity", billingResult.getResponseCode() + "");
                    isConnect = false;
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                isConnect = false;
            }
        });
    }

    PurchasesUpdatedListener mPurchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Toast.makeText(mActivity,"User cancel",Toast.LENGTH_SHORT);
            } else {
                // Handle any other error codes.
            }
        }
    };
    /**
     * google pay
     * */
    public  void getSkuList(String goodsId) {
        skuList.clear();
        skuList.add(goodsId);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                                && skuDetailsList != null) {
                            if (skuDetailsList.size()>0){
                                skuDetail = skuDetailsList.get(0);
                                googlePay();
                            }
                        }
                    }
                });
    }

    /**
     * google pay
     * */
    public void googlePay() {
        if (isConnect) {

            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetail)
                    .build();
            int responseCode = billingClient.launchBillingFlow(mActivity, flowParams).getResponseCode();
            if (responseCode != 0) {
                Toast.makeText(mActivity,responseCode + ":Current region does not support Google payments",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mActivity, ":Current region does not support Google payments",Toast.LENGTH_SHORT).show();
        }
    }



    /**
     * google pay ,handlePurchase
     * */
    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.
            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged()) {
                ConsumeParams acknowledgePurchaseParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                //注意这里通知方式分3种类型（消耗型、订阅型、奖励型），本文是消耗性产品的通知方式，其它方式请看官方文档
                billingClient.consumeAsync(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
            String DeveloperPayload = mDeveloperPayload;
//            String OrderId = purchase.getOrderId();
            String OriginalJson = purchase.getOriginalJson();
//            String PackageName = purchase.getPackageName();
//            String PurchaseState = purchase.getPurchaseState() + "";
//            String PurchaseTime = purchase.getPurchaseTime() + "";
//            String PurchaseToken = purchase.getPurchaseToken();
            String Signature = purchase.getSignature();
//            String Sku = purchase.getSku();
            mPayInfo = new PayInfo();
            mPayInfo.setSignature(Signature);
            mPayInfo.setPay_id(DeveloperPayload);
            mPayInfo.setPay_originalJson(OriginalJson);
            mPayInfo.setaMount(OriginalJson);
            mPayInfo.setExpend(OriginalJson);
            mPayInfo.setOrder_name(OriginalJson);
            PaymentUtil.insertRecord(mPayInfo);

            postGooglePay(OriginalJson,Signature, DeveloperPayload);
            //通知服务端
            String[] amountString = OriginalJson.split(",");
            String aMount = "0";
            for (int i = 0; i < amountString.length; i++) {
                if (!TextUtils.isEmpty(amountString[i]) && amountString[i].contains("com.novel.tok_")){
                    String[] amountStr = amountString[i].split("_");
                    if (!TextUtils.isEmpty(amountStr[1])){
                        aMount = amountStr[1];
                        aMount = aMount.substring(0,aMount.length()-1);
                        break;
                    }
                }

            }


            }
    }


    private final ConsumeResponseListener acknowledgePurchaseResponseListener = new ConsumeResponseListener() {

        @Override
        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
//            mDeveloperPayload = purchaseToken;

        }
    };


    /**
     * google play充值成功后调用
     */
    private void postGooglePay(String orderinfo, String sign, String order_id) {
//        showLoading(BLANK);
        NetRequest.googlePay(orderinfo, sign, order_id, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
               dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (Constant.SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == Constant.ONE) {
                        PaymentUtil.deletePayInfo(mDeveloperPayload);


                        mFirebaseAnalytics.setUserProperty("paying_user", "1");

                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, mActivity.getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(mActivity, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
//                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, mActivity.getString(R.string.no_internet));
            }
        });
    }


    /**
     * 充值前调用
     * rmb 充值金额
     * money 充值的书币
     * rule_id 规则id
     * custom 其他信息
     * channel 充值渠道 必填1google，2qpay
     * channel_child 充值子渠道 1zala ，2momo
     */
    private void doPay(String rmb,String rule_id
            ,int pay_type
            ,int pay_type_1
            ,String price_type
            ,String price) {
        NetRequest.doPay(rmb,rule_id,pay_type, pay_type_1,price_type,price,new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (Constant.SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
//                    merchantCode = JSONUtil.getString(result, "partner_code");
//                    env = JSONUtil.getBoolean(result, "env");

                    if (status == Constant.ONE) {

                        mDeveloperPayload = JSONUtil.getString(result, "order_id");
                        initializeGooglePay(mGoodsId);//google支付

                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, mActivity.getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(mActivity, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
//                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, mActivity.getString(R.string.no_internet));
            }
        });
    }



    /**
     * 隐藏loading弹窗
     */
    public void dismissLoading() {
        LoadingAlertDialog.dismiss(loadingDialog);
    }


}
