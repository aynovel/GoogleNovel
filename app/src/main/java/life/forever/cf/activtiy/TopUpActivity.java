package life.forever.cf.activtiy;

import static life.forever.cf.internet.NetRequest.base64;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import life.forever.cf.R;
import life.forever.cf.entry.PayInfo;
import life.forever.cf.entry.TaskReword;
import life.forever.cf.entry.TopUpListBean;
import life.forever.cf.interfaces.InterFace;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.TopUpAdapter;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.adapter.person.pay.PaymentUtil;
import life.forever.cf.adapter.person.personcenter.FeedbackActivity;
import life.forever.cf.adapter.person.personcenter.UserHelpActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.OnItemClickListener;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.DisplayUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.TaskCompleteDialog;
import life.forever.cf.publics.weight.viewweb.Wei1;
import life.forever.cf.publics.weight.viewweb.JsAndroid1;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TopUpActivity extends BaseActivity implements PurchasesUpdatedListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.layout_info)
    LinearLayout mLayoutInfo;

    @BindView(R.id.ll_all_close)
    LinearLayout ll_all_close;

    @BindView(R.id.layout_pay_methed)
    RelativeLayout layout_pay_methed;
    @BindView(R.id.ll_pay_mothed)
    LinearLayout ll_pay_mothed;


    @BindView(R.id.webview)
    Wei1 webview;

    @BindView(R.id.webview1)
    Wei1 webview1;


    @BindView(R.id.iv_close)
    ImageView iv_close;

    TopUpAdapter mTopUpAdapter;
    private final Intent intent = new Intent();
    private boolean isClickCheckOrder = false;
    private List<PayInfo> payinfos = new ArrayList<>();
//    protected aiyeWebWidget1 mWebView;
    /**
     * 记录点的第几个支付选项
     */
    private int recordPosition = 0;
    /**
     * google pay
     */
    private BillingClient billingClient;
    List<String> skuList = new ArrayList<>();
    boolean isConnect = false;
    SkuDetails skuDetail = null;
    private boolean first = false;
    /**
     * 后台订单编号
     */
    private String mDeveloperPayload = "";
    /**
     * 支付信息
     */
    private PayInfo mPayInfo;
    /**
     * 充值金额
     */
    private String amount = "";
    /**
     * google商品id
     */
    private String goodsId = "";

    //    private int recordNum = 0;
    private int totalNum;
    private int successNum;
    private final int failNum = 0;

    private boolean isRestore = true;

    ProgressBar mProgressBar;

    //    private boolean lastone = false;
    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(getResources().getString(R.string.go_top_up));
        mTitleBar.showRightImageView(FALSE);
        mTitleBar.setRightText(getResources().getString(R.string.topup_restore_order_hint));
        mTitleBar.setRightTextViewOnClickListener(onRestoreClick);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_top_up);
        ButterKnife.bind(this);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ll_pay_mothed.getLayoutParams();
        params.height = DisplayUtil.sp2px(this, 390);
        ll_pay_mothed.setLayoutParams(params);

    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        isClickCheckOrder = false;
        payinfos = PaymentUtil.queryPayLists();
        first = true;
        initializeGooglePay();
    }

    private final View.OnClickListener onRestoreClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            restoreClick();
        }
    };

    private void loadUrl(TopUpListBean.ResultData.Info.Order_data mOrder) {
        JSONObject jsonObject = JSONUtil.newJSONObject();
        JSONUtil.put(jsonObject, "rule_id", mOrder.id);
        JSONUtil.put(jsonObject, "price_type", mOrder.note);

        if (mOrder.is_rec.equals("0") || mOrder.is_rec.equals("1")) {
            JSONUtil.put(jsonObject, "price", "$ " + mOrder.rmb);
        } else {
            JSONUtil.put(jsonObject, "price", mOrder.is_rec);
        }
        String param = base64(jsonObject.toString());
        String url = PlotRead.getINDEX() + NetRequest.path(InterFace.H5_PAY_PAL, param);
        webview.setJsAndroid(new JsAndroid1(this, webview));
        webview.setWebViewClient(webViewClient);
        webview.setWebChromeClient(webChromeClient);
        webview.setWebChromeClient(new WebChromeClient() {

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                return true;
            }
        });
        webview.loadUrl(url);

        webview1.setWebViewClient(webViewClient);
        webview1.setJsAndroid(new JsAndroid1(this, webview1));
        webview1.setWebChromeClient(webChromeClient);
        webview1.setWebChromeClient(new WebChromeClient() {

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                return true;
            }
        });
        mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.ress_drawable));
        mProgressBar.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dp2px(context, ONE)));
        mProgressBar.setMax(ONE_HUNDRED);
        webview1.addView(mProgressBar);

    }

    private final WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            webview.setProgress(newProgress);
        }

    };
    private final WebViewClient webViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            mLoadingLayout.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mWrongLayout.setVisibility(View.VISIBLE);
        }

    };

    /**
     * initializeGooglePay
     */
    private void initializeGooglePay() {
        DeepLinkUtil.addPermanent(TopUpActivity.this, "event_top_up", "进入充值页", "", "", "", "", "", "", "");
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The billing client is ready. You can query purchases here.
                    isConnect = true;
                    if (HomeActivity.contactList == null || HomeActivity.contactList.size() == 0) {
                        topuplist();
                    } else {
                        mTopUpAdapter = new TopUpAdapter(context, HomeActivity.contactList, onItemClick);
                        mRecyclerView.setAdapter(mTopUpAdapter);
                        mLayoutInfo.setVisibility(View.VISIBLE);
                        if (isConnect) {
                            for (int i = 0; i < HomeActivity.contactList.size(); i++) {
                                getSkuList(HomeActivity.contactList.get(i).goods, false, i);
                            }
                        }
                    }
                    onPurchasesUpdated();


                } else {
                    Log.e("PayDiamondActivity", billingResult.getResponseCode() + "");
                    isConnect = false;
                    if (HomeActivity.contactList == null || HomeActivity.contactList.size() == 0) {
                        topuplist();
                    } else {
                        mTopUpAdapter = new TopUpAdapter(context, HomeActivity.contactList, onItemClick);
                        mRecyclerView.setAdapter(mTopUpAdapter);
                        mLayoutInfo.setVisibility(View.VISIBLE);
                        if (isConnect) {
                            for (int i = 0; i < HomeActivity.contactList.size(); i++) {
                                getSkuList(HomeActivity.contactList.get(i).goods, false, i);
                            }
                        }
                    }
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


    private final OnItemClickListener onItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(RecyclerView.ViewHolder viewHolder) {
            recordPosition = viewHolder.getAdapterPosition();

            goodsId = HomeActivity.contactList.get(recordPosition).goods;
            amount = HomeActivity.contactList.get(recordPosition).rmb;

            isClickCheckOrder = false;

            if (HomeActivity.version_status == 1) {
                DeepLinkUtil.addPermanent(TopUpActivity.this, "event_pay_evoke", "唤起支付", "paypal", "", "", "", "", "", "");
                layout_pay_methed.setVisibility(View.VISIBLE);
                loadUrl(HomeActivity.contactList.get(recordPosition));
                ll_pay_mothed.getHeight();
            } else {
                DeepLinkUtil.addPermanent(TopUpActivity.this, "event_pay_evoke", "唤起支付", "google", "", "", "", "", "", "");
                layout_pay_methed.setVisibility(View.GONE);
                doPay(HomeActivity.contactList.get(recordPosition).rmb, HomeActivity.contactList.get(recordPosition).id, 1
                        , 1
                        , HomeActivity.contactList.get(recordPosition).note
                        , HomeActivity.contactList.get(recordPosition).rmb, null);
            }


        }
    };

    /**
     * 请求支付列表数据
     */
    private void topuplist() {
        NetRequest.topupList(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");

                        JSONObject jsonOders = new JSONObject(resultString);
                        String strOrders = jsonOders.getString("info");

                        JSONObject json = new JSONObject(strOrders);
                        String strResult = json.getString("order_data");
                        HomeActivity.version_status = json.getInt("version_status");

                        HomeActivity.contactList.clear();
                        Type listType = new TypeToken<List<TopUpListBean.ResultData.Info.Order_data>>() {
                        }.getType();
                        Gson gson = new Gson();
                        HomeActivity.contactList = gson.fromJson(strResult, listType);

                        mTopUpAdapter = new TopUpAdapter(context, HomeActivity.contactList, onItemClick);
                        mRecyclerView.setAdapter(mTopUpAdapter);
                        mLayoutInfo.setVisibility(View.VISIBLE);
                        if (isConnect) {
                            for (int i = 0; i < HomeActivity.contactList.size(); i++) {
                                getSkuList(HomeActivity.contactList.get(i).goods, false, i);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, "Request failed,Please try again later！");
            }
        });
    }

    //补单处理
    @OnClick({R.id.tv_restore})
    public void restoreClick() {
        if (isRestore) {
            reCheckOrder();
        }
    }

    //Q&A
    @OnClick({R.id.tv_top_help})
    public void TopHelpClick() {
        intent.setClass(context, UserHelpActivity.class);
        startActivity(intent);
    }

    // 反馈
    @OnClick({R.id.tv_top_feedback})
    public void TopFeedBackClick() {
        if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
            intent.setClass(context, FeedbackActivity.class);
        } else {
            intent.setClass(context, LoginActivity.class);
        }
        startActivity(intent);
    }

    // 反馈
    @OnClick({R.id.layout_pay_methed})
    public void layoutPayClick() {
    }

    // 检查掉单
    @OnClick({R.id.tv_restore})
    public void reCheckOrder() {

        if (isConnect) {
            onPurchasesUpdated();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };


    @Override
    public void onBackPressed() {

        if (webview1.getVisibility() == View.VISIBLE) {
            webview1.setVisibility(View.GONE);
        }
        if (layout_pay_methed.getVisibility() == View.VISIBLE) {
            layout_pay_methed.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        if (billingClient != null && billingClient.isReady()) {
            billingClient.endConnection();
            billingClient = null;
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    //-----------------------------google pay 查询未消耗商品 -----------------------------------------

    /**
     * google pay 查询掉单
     */
    public void onPurchasesUpdated() {
        if (isConnect) {
            isClickCheckOrder = true;
            successNum = 0;
            totalNum = 0;
            Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
            if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !purchasesResult.getPurchasesList().isEmpty()) {
                for (Purchase purchase : purchasesResult.getPurchasesList()) {
                    totalNum = purchasesResult.getPurchasesList().size();
                    if (payinfos.size() == 0) {
                        for (int k = 0; k < HomeActivity.contactList.size(); k++) {
                            if (HomeActivity.contactList.get(k).goods.equals(purchase.getSku())) {
                                isRestore = false;
                                doPay(HomeActivity.contactList.get(k).rmb, HomeActivity.contactList.get(k).id, 1
                                        , 1
                                        , HomeActivity.contactList.get(k).note
                                        , HomeActivity.contactList.get(k).rmb, purchase);
                                break;
                            }

                        }
                    } else {
                        for (int i = 0; i < payinfos.size(); i++) {
                            if (payinfos.get(i).getExpend().equals("0")) {
                                if (i == payinfos.size() - 1 && !purchase.getOriginalJson().equals(payinfos.get(i).pay_originalJson)) {
                                    for (int k = 0; k < HomeActivity.contactList.size(); k++) {
                                        if (HomeActivity.contactList.get(k).goods.equals(purchase.getSku())) {
                                            doPay(HomeActivity.contactList.get(k).rmb, HomeActivity.contactList.get(k).id, 1
                                                    , 1
                                                    , HomeActivity.contactList.get(k).note
                                                    , HomeActivity.contactList.get(k).rmb, purchase);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }

            if (payinfos.size() > 0) {
                totalNum += payinfos.size();
                for (int i = 0; i < payinfos.size(); i++) {
                    if (payinfos.get(i).getExpend().equals("0")) {

                        if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK && !purchasesResult.getPurchasesList().isEmpty()) {
                            for (Purchase purchase : purchasesResult.getPurchasesList()) {
                                if (purchase.getOriginalJson().equals(payinfos.get(i).pay_originalJson)) {
                                    handlePurchase(purchase, payinfos.get(i).getPay_id());
                                }
                            }
                        }
                    } else {
                        try {
                            if (payinfos.get(i).getExpend().equals("1")) {
                                postGooglePay(payinfos.get(i).getPay_originalJson(), payinfos.get(i).getSignature(), payinfos.get(i).getPay_id() + "", payinfos.get(i).getaMount());
                            }
                        } catch (Exception s) {
                        }
                    }
                }
            } else {
                if (!first && totalNum == 0) {
                    PlotRead.toast(PlotRead.NORMAL, getString(R.string.no_haved_order));

                    if(Cods.rechargeFlag == false)
                    {
                        Cods.rechargeFlag = true;
                        Message paypalMessage = Message.obtain();
                        paypalMessage.what = BUS_RECHARGE_SUCCESS;
                        EventBus.getDefault().post(paypalMessage);
                    }
                } else {
                    first = false;
                }

            }


        }
    }

    @OnClick(R.id.iv_close)
    void close() {
        layout_pay_methed.setVisibility(View.GONE);
    }

    /**
     * google pay
     */
    private void getSkuList(String goodsId, Boolean isPay, int position) {
        try {


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
                                if (skuDetailsList.size() > 0) {
                                    skuDetail = skuDetailsList.get(0);

                                    if (isPay) {
                                        googlePay();
                                    } else {
                                        HomeActivity.contactList.get(position).is_rec = skuDetail.getPrice();
                                        if (TextUtils.isEmpty(skuDetail.getPriceCurrencyCode())) {
                                            HomeActivity.contactList.get(position).note = skuDetail.getPrice();

                                        } else {
                                            HomeActivity.contactList.get(position).note = skuDetail.getPriceCurrencyCode();
                                        }

                                        mTopUpAdapter.notifyDataSetChanged();
                                    }
                                }

                            }
                        }
                    });
        } catch (Exception e) {
        }
    }

    /**
     * google pay
     */
    public void googlePay() {
        if (isConnect) {
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetail)
                    .build();
            if (billingClient != null && flowParams != null) {
                int responseCode = billingClient.launchBillingFlow(TopUpActivity.this, flowParams).getResponseCode();
                if (responseCode != 0) {
                    Toast.makeText(TopUpActivity.this, responseCode + ":Current region does not support Google payments", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(TopUpActivity.this, ":Current region does not support Google payments", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {

            for (Purchase purchase : purchases) {
                String OriginalJson = purchase.getOriginalJson();
                String Signature = purchase.getSignature();
                mPayInfo = new PayInfo();
                mPayInfo.setSignature(Signature);
                mPayInfo.setPay_id(mDeveloperPayload);
                mPayInfo.setPay_originalJson(OriginalJson);
                mPayInfo.setExpend("0");
                mPayInfo.setaMount(amount);
                mPayInfo.setOrder_name("1");
                PaymentUtil.insertRecord(mPayInfo);
                handlePurchase(purchase, mDeveloperPayload);

            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(TopUpActivity.this, "User cancel", Toast.LENGTH_SHORT);
        }
    }

    /**
     * google pay ,handlePurchase
     */
    void handlePurchase(Purchase purchase, String mDeveloperPayload) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                ConsumeParams acknowledgePurchaseParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                //注意这里通知方式分3种类型（消耗型、订阅型、奖励型），本文是消耗性产品的通知方式，其它方式请看官方文档
                billingClient.consumeAsync(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);

                String OriginalJson = purchase.getOriginalJson();
                String Signature = purchase.getSignature();
                if (mDeveloperPayload == "0") {
                    return;
                }
                mPayInfo = new PayInfo();
                mPayInfo.setSignature(Signature);
                mPayInfo.setPay_id(mDeveloperPayload);
                mPayInfo.setPay_originalJson(OriginalJson);
                mPayInfo.setaMount(amount);
                mPayInfo.setExpend("1");
                mPayInfo.setOrder_name(mDeveloperPayload);

                PaymentUtil.updatePay(mPayInfo);
                postGooglePay(OriginalJson, Signature, mDeveloperPayload, amount);
                isRestore = true;
            }
        }
    }


    private final ConsumeResponseListener acknowledgePurchaseResponseListener = new ConsumeResponseListener() {

        @Override
        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
            Log.e("ss", purchaseToken);

        }
    };


    /**
     * 充值前调用
     * rmb 充值金额
     * money 充值的书币
     * rule_id 规则id
     * custom 其他信息
     * channel 充值渠道 必填1google，2qpay
     * channel_child 充值子渠道 1zala ，2momo
     */
    private void doPay(String rmb, String rule_id
            , int pay_type
            , int pay_type_1
            , String price_type
            , String price
            , Purchase purchase) {
        showLoading(BLANK);
        amount = rmb;
        NetRequest.doPay(rmb, rule_id, pay_type, pay_type_1, price_type, price, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();

                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {

                        mDeveloperPayload = JSONUtil.getString(result, "order_id");

//                        mPayInfo = new PayInfo();
//                        mPayInfo.setSignature("");
//                        mPayInfo.setPay_id(mDeveloperPayload);
//                        mPayInfo.setPay_originalJson("");
//                        mPayInfo.setExpend("0");
//                        mPayInfo.setaMount(amount);
//                        mPayInfo.setOrder_name("0");
//                        PaymentUtil.insertRecord(mPayInfo);
                        if (purchase != null && isClickCheckOrder) {
                            handlePurchase(purchase, mDeveloperPayload);
                        } else {
                            getSkuList(goodsId, true, 0);
                        }
                        DeepLinkUtil.addPermanent(TopUpActivity.this, "event_pay_evokecoin_get_order", "支付订单", mDeveloperPayload, "", "", "", "", "", "");


                    } else {
                        PlotRead.toast(PlotRead.INFO, TopUpActivity.this.getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(TopUpActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, TopUpActivity.this.getString(R.string.no_internet));
            }
        });
    }

    /**
     * google play充值成功后调用ca:a6:20:02:a0:73:cb:a9:4b:66:19:04:a9:c4:c2:84:f5:d3:be:0d
     */
    private void postGooglePay(String orderinfo, String sign, String order_id, String amount) {
        mDeveloperPayload = order_id;
        showLoading(BLANK);
        DeepLinkUtil.addPermanent(TopUpActivity.this, "event_service_pay", "服务器回调", mDeveloperPayload, "", "", "", "", "", "");


        NetRequest.googlePay(orderinfo, sign, order_id, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        PaymentUtil.deletePayInfo(mDeveloperPayload);
                        payinfos = PaymentUtil.queryPayLists();
                        DeepLinkUtil.addPermanent(TopUpActivity.this, "event_service_pay_success", "服务器支付状态成功", mDeveloperPayload, "", "", "", "", "", "");
                        Intent mIntent = new Intent();
                        mIntent.putExtra(SUCCESS, true);
                        setResult(ONE, mIntent);
                        mFirebaseAnalytics.setUserProperty("paying_user", amount + "");

                        Message message = Message.obtain();
                        message.what = BUS_RECHARGE_SUCCESS;
                        EventBus.getDefault().post(message);

                        if (isClickCheckOrder) {
                            successNum++;
                            if (totalNum == 1) {
                                PlotRead.toast(PlotRead.SUCCESS, getString(R.string.have_order_success));
                                DeepLinkUtil.addPermanent(TopUpActivity.this, "event_pay_restore", "补单成功", "1", "", "", "", "", "", "");

                            } else {
                                toast();
                            }
                        }

                        //领取奖励
                        String taskString = null;
                        TaskReword taskBean = null;

                        try {

                            taskString = result.getString("task");
                            Object taskObject = new JSONTokener(taskString).nextValue();
                            if (taskObject instanceof JSONObject) {
                                if (taskString != null) {
                                    taskBean = new Gson().fromJson(taskString, TaskReword.class);
                                }
                                if (taskBean != null) {
                                    if (taskBean.auto.equals("1")) {
                                        getTaskReward(Integer.parseInt(taskBean.task_id));
                                    }
                                }
                            } else if (taskObject instanceof JSONArray) {
                                if (!isClickCheckOrder) {
                                    finish();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (!isClickCheckOrder) {
                                finish();
                            }
                        }

                    } else {
//                        String msg = JSONUtil.getString(result, "msg");
                        DeepLinkUtil.addPermanent(TopUpActivity.this, "event_service_pay_failed", "服务器支付状态失败", mDeveloperPayload, "", "", "", "", "", "");
                        if (isClickCheckOrder) {
                            PlotRead.toast(PlotRead.SUCCESS, getString(R.string.have_order_fail));
                        } else {
                            PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                        }

                    }
                } else {
                    NetRequest.error(TopUpActivity.this, serverNo);
                    DeepLinkUtil.addPermanent(TopUpActivity.this, "event_service_pay_failed", "服务器支付状态失败", mDeveloperPayload, "", "", "", "", "", "");

                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                DeepLinkUtil.addPermanent(TopUpActivity.this, "event_service_pay_failed", "服务器支付状态失败", mDeveloperPayload, "", "", "", "", "", "");
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    public void toast() {
        if (successNum == totalNum) {
            if (totalNum - successNum == 0) {
                PlotRead.toast(PlotRead.SUCCESS, successNum + " " + getString(R.string.have_order_success_more) + getString(R.string.have_order_more));

            } else {
                PlotRead.toast(PlotRead.SUCCESS, successNum + " " + getString(R.string.have_order_success_more) +
                        (totalNum - successNum) + " " + getString(R.string.have_order_fail_more) + getString(R.string.have_order_more));

            }
            DeepLinkUtil.addPermanent(TopUpActivity.this, "event_pay_restore", "补单成功", successNum + "", "", "", "", "", "", "");

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        switch (message.what) {
            case Constant.CLICK_PAYPAL:
                showLoading(BLANK);
                break;
            case Constant.PAYPAL_STARE:
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                String url = message.getData().getString("obj");
                webview1.setVisibility(View.VISIBLE);
                webview1.loadUrl(url);
                dismissLoading();

                mLoadingLayout.setVisibility(View.VISIBLE);
                break;
            case Constant.PAYPAL_RESULT:
                layout_pay_methed.setVisibility(View.GONE);
                webview1.setVisibility(View.GONE);
                Intent mIntent = new Intent();
                mIntent.putExtra(SUCCESS, true);
                setResult(ONE, mIntent);
                mFirebaseAnalytics.setUserProperty("paying_user", amount + "");
                // 发送通知
                Message paypalMessage = Message.obtain();
                paypalMessage.what = BUS_RECHARGE_SUCCESS;
                EventBus.getDefault().post(paypalMessage);
                break;
            case Constant.GOOGLE_PAY:
                layout_pay_methed.setVisibility(View.GONE);
                doPay(HomeActivity.contactList.get(recordPosition).rmb, HomeActivity.contactList.get(recordPosition).id, 1
                        , 1
                        , HomeActivity.contactList.get(recordPosition).note
                        , HomeActivity.contactList.get(recordPosition).rmb, null);
                break;
        }

    }

    private void getTaskReward(int taskId) {
        showLoading(getString(R.string.loading));

        NetRequest.getTaskReward(taskId, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String jsonString = jsonObject.getString("ResultData");
                        JSONObject resultObject = jsonObject.getJSONObject("ResultData");
                        if (resultObject != null && resultObject.getInt("status") == 1) {
                            Log.e("TAG", "onSuccess: 任务奖励领取成功 " + resultObject.getString("msg"));
                            String taskString = resultObject.getString("task");
                            TaskReword taskBean = new Gson().fromJson(taskString, TaskReword.class);
                            //后续操作  显示领取奖励dialog
                            TaskCompleteDialog mTaskCompleteDialog = new TaskCompleteDialog(context, taskBean,null);
                            mTaskCompleteDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    // 发送通知
                                    Message message = Message.obtain();
                                    message.what = BUS_RECHARGE_SUCCESS;
                                    EventBus.getDefault().post(message);
                                    if (!isClickCheckOrder) {
                                        finish();
                                    }
                                }
                            });
                            mTaskCompleteDialog.show();

                        } else {
                            if (!isClickCheckOrder) {
                                finish();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        dismissLoading();
                        if (!isClickCheckOrder) {
                            finish();
                        }
//                        if (!isClickCheckOrder){ finish();}
                    }

                } else {
                    dismissLoading();
                    if (!isClickCheckOrder) {
                        finish();
                    }
                }

            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                if (!isClickCheckOrder) {
                    finish();
                }
            }
        });
    }


}
