package life.forever.cf.activtiy;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.adapter.AppendableAdapter;
import life.forever.cf.adapter.TaskAdapter;
import life.forever.cf.entry.AppUser;
import life.forever.cf.entry.ReadTaskBean;
import life.forever.cf.entry.SignBean;
import life.forever.cf.entry.TaskBean;
import life.forever.cf.entry.TaskReword;
import life.forever.cf.entry.TaskType;
import life.forever.cf.entry.TaskTypeState;
import life.forever.cf.entry.TaskItemBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.SignRVAdapter;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.DataString;
import life.forever.cf.publics.tool.DisplayUtil;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.ManagerDialog;
import life.forever.cf.publics.tool.NotificationsUtils;
import life.forever.cf.publics.tool.ScreenUtil;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import life.forever.cf.publics.tool.TaskCompleteDialog;
import life.forever.cf.publics.tool.TaskSignDialog;
import life.forever.cf.publics.tool.TimeUtil;
import life.forever.cf.publics.weight.RadiusImageView;
import life.forever.cf.publics.weight.ReadTaskView;
import life.forever.cf.publics.weight.ReadTipView;
import life.forever.cf.publics.weight.TaskCheckBox;
import life.forever.cf.publics.weight.TaskScrollView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @Author : xupanfei
 * @Time : On 2021/8/18 10:44
 * @Description : ?????????????????????????????????????????????????????????
 * ???????????????????????????
 */
public class TaskCenterActivity extends BaseActivity {

    //??????
    @BindView(R.id.signRecyclerView)
    RecyclerView mSignRecyclerView;
    //????????????
    @BindView(R.id.newRecyclerView)
    RecyclerView mNewRecyclerView;
    //????????????
    @BindView(R.id.dailyRecyclerView)
    RecyclerView mDailyRecyclerView;
    //?????????????????????
    @BindView(R.id.imgStatusbar)
    ImageView mImgStatusbar;
    @BindView(R.id.imgHead)
    RadiusImageView mImgHead;
    //???nescrollview
    @BindView(R.id.nsRootView)
    TaskScrollView mNsRootView;
    //??????
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    //???????????????
    @BindView(R.id.imgTitleView)
    ImageView mImgTitleView;
    //????????????
    @BindView(R.id.imgBack)
    ImageView mImgBack;
    //??????????????????
    @BindView(R.id.imgMask)
    ImageView mImgMask;
    //??????????????????
    @BindView(R.id.cbCardRemind)
    TaskCheckBox mCbCardRemind;
    @BindView(R.id.tvRemainTip)
    TextView mTvRemainTip;
    //??????????????????
    @BindView(R.id.tvCardTitle)
    TextView mTvCardTitle;
    //??????
    @BindView(R.id.tvBonusNum)
    TextView mTvBonusNum;
    @BindView(R.id.readTipView)
    ReadTipView mReadTipView;
    //?????????????????????
    @BindView(R.id.cardNew)
    CardView mCardNewView;
    //??????????????????
    @BindView(R.id.tvDailyTitle)
    TextView mTvDailyTitle;
    @BindView(R.id.tvNewTitle)
    TextView mTvNewTitle;
    //??????????????????
    @BindView(R.id.tvReadContent)
    TextView mTvReadContent;


    private ManagerDialog mManagerDialog;
    //????????????
    private TaskSignDialog mTaskSignDialog;
    //????????????????????????
    private TaskCompleteDialog mTaskCompleteDialog;
    private SignBean.ResultData mSignBean = new SignBean.ResultData();
    private SignRVAdapter mSignRVAdapter;
    //????????????
    private TaskAdapter mNewTaskAdapter;
    //????????????
    private TaskAdapter mDailyTaskAdapter;

    private final String TAG = "TaskCenterActivity";
    private String mToken;
    private int currentBonusNum;
    //0 ?????? ???1 ??????  ,2 ?????????
    private int pushState = 0;

    @Override
    protected void initializeView() {
        mRootLayout.setFitsSystemWindows(false);
        ScreenUtil.setStatusBarDark(this, true);
        mTitleBar.setMiddleText("taskcenter");
        mTitleBar.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_taskcenter);
        ButterKnife.bind(this);
        initStatusBarHeight();

        AppUser user = PlotRead.getAppUser();
        GlideUtil.load(context, user.head, R.drawable.logo_default_user, mImgHead);
        mTvBonusNum.setText(user.voucher + "");
        currentBonusNum = user.voucher;
        initRemindTip();
        mDailyTaskAdapter = new TaskAdapter(context);
        mNewTaskAdapter = new TaskAdapter(context);
        initEvent();
    }

    @Override
    protected void initializeData() {


        mNewRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mDailyRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mNewRecyclerView.setAdapter(mNewTaskAdapter);
        mDailyRecyclerView.setAdapter(mDailyTaskAdapter);

        showLoading(getString(R.string.loading));
        getToken();

//        getExistReward();


        long readMillion = SharedPreferencesUtil.getLong(PlotRead.getConfig(), TimeUtil.currentDate());
        int readTime = (int) readMillion / 60000;
        String readContent = getString(R.string.task_read_time) + " " + readTime + " min";
        mTvReadContent.setText(readContent);

        if (readTime < 15) {
            fetchSignInfo();
        } else {
            uploadReadTime();
        }
//        getAllTask();
//        fetchSignInfo();
    }

    @OnClick({R.id.imgBack, R.id.imgMask, R.id.cbCardRemind, R.id.tvDetail})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                finish();
                break;
            case R.id.imgMask:
//                toOpenNotification();
                mTvRemainTip.setVisibility(View.VISIBLE);
                mTvRemainTip.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mTvRemainTip.setVisibility(View.GONE);
                    }
                }, 5000);
                break;
            case R.id.cbCardRemind:
                if (!mCbCardRemind.isChecked()) {
                    if (!NotificationsUtils.isNotifyEnabled(TaskCenterActivity.this)) {
                        toOpenNotification();
                    } else {
                        //????????????
//                        mCbCardRemind.setChecked(true);
                        Log.e(TAG, "onClick: ????????????");
                        setSignSwitch(1);
                    }

                } else {
                    //????????????
//                    mCbCardRemind.setChecked(false);
                    Log.e(TAG, "onClick: ????????????");
                    setSignSwitch(0);
                }
                break;
            case R.id.tvDetail:
                //????????????????????????
                LayoutReceivedClick();
                break;
        }
    }

    /**
     * ??????
     */
    private void initSign() {
        mSignRVAdapter = new SignRVAdapter(this, mSignBean);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == mSignRVAdapter.getItemCount() - 1) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mSignRecyclerView.setLayoutManager(mGridLayoutManager);
        mSignRecyclerView.setHasFixedSize(true);
        mSignRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mSignRecyclerView.setAdapter(mSignRVAdapter);
    }


    /**
     * ??????????????????
     */
    private void fetchSignInfo() {
        NetRequest.signInfo(new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String resultString = jsonObject.getString("ResultData");
                        Gson gson = new Gson();
                        mSignBean = gson.fromJson(resultString, SignBean.ResultData.class);
                        if (mSignBean != null && mSignBean.info != null) {
                            if (1 == mSignBean.info.today_is_sign) {
                                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_SIGN, TRUE);
                                getAllTask();
                            } else {
                                SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_SIGN, FALSE);
                                sign(DataString.StringData());
                            }
                            initSign();
                            String signInfo = "Tomorrow you???ll get " + mSignBean.info.next_price + " bonus";
                            mTvCardTitle.setText(signInfo);
                            //????????????
                            if (mSignBean.info.is_push) {
                                mCbCardRemind.setChecked(true);
                                pushState = 1;
                                initNotification();
                            } else {
                                mCbCardRemind.setChecked(false);
                                pushState = 2;
                            }
//                            mSignRVAdapter = new SignRVAdapter(TaskCenterActivity.this, mSignBean);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    NetRequest.error(TaskCenterActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    /**
     * ??????
     */
    private void sign(int week) {
        showLoading(context.getString(R.string.content_loading));
        NetRequest.sign(week, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                if (ComYou.isDestroy(TaskCenterActivity.this)) {
                    return;
                }
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {

                        SharedPreferencesUtil.putBoolean(PlotRead.getConfig(), FIRST_SIGN, TRUE);
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(data));
                            String resultString = jsonObject.getString("ResultData");

                            JSONObject jsonOders = new JSONObject(resultString);
                            String strOrders = jsonOders.getString("info");

                            JSONObject json = new JSONObject(strOrders);
                            String strResult = json.getString("sign");

                            Type listType = new TypeToken<List<SignBean.ResultData.Info.Sign>>() {
                            }.getType();
                            Gson gson = new Gson();
                            if (mSignBean != null && mSignBean.info != null) {
                                mSignBean.info.sign.clear();
                                mSignBean.info.sign.addAll(gson.fromJson(strResult, listType));
                                // ????????????
                                mSignRVAdapter = new SignRVAdapter(TaskCenterActivity.this, mSignBean);
                                mSignRecyclerView.setAdapter(mSignRVAdapter);
                                mTaskSignDialog = new TaskSignDialog(TaskCenterActivity.this, mSignBean);
                                mTaskSignDialog.show();

                                String todaytime = ComYou.timeFormat(ComYou.currentTimeSeconds(), DATE_FORMATTER_8);

                                for (int i = 0;i< mSignBean.info.sign.size();i++){
                                    if (mSignBean.info.sign.get(i).date.equals(todaytime)){
                                        String todayNumber = mSignBean.info.sign_price.get(i);
                                        currentBonusNum = Integer.parseInt(todayNumber) + currentBonusNum;
                                        mTvBonusNum.setText(currentBonusNum + "");
                                    }
                                }

                                // ????????????
                                Message message = Message.obtain();
                                message.what = BUS_USER_SIGN_STATE_CHANGE;
                                EventBus.getDefault().post(message);
//                                PlotRead.toast(PlotRead.SUCCESS, "check-in success");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        getAllTask();

                    } else {
                        /*String msg = JSONUtil.getString(result, "msg");*/
//                        if (mSignBean != null && mSignBean.info != null) {
//                            // ????????????
//                            mSignRVAdapter = new SignRVAdapter(TaskCenterActivity.this, mSignBean);
//                        }
                    }
                } else {
                    NetRequest.error(TaskCenterActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }


    /**
     * ??????????????????,??????????????????????????????
     */
    private void getAllTask() {
        showLoading(context.getString(R.string.content_loading));
        NetRequest.getTask(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");

                    if (status == ONE) {

                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(data));
                            String resultString = jsonObject.getString("ResultData");
                            Gson gson = new Gson();
                            TaskBean.ResultData resultData = gson.fromJson(resultString, TaskBean.ResultData.class);
                            if (resultData != null && resultData.lists != null) {

                                if (resultData.lists.first_list.size() == 0) {
                                    mCardNewView.setVisibility(View.GONE);
                                } else {
                                    mNewTaskAdapter.setDataItems(resultData.lists.first_list);
                                    notifyNewTitle(resultData.lists.first_list);
                                }

                                mDailyTaskAdapter.setDataItems(resultData.lists.daily_list);
                                notifyDailyTitle(resultData.lists.daily_list);


                                //??????????????????
                                syncNoticy(resultData.lists.first_list);

                                long readMillionTime = SharedPreferencesUtil.getLong(PlotRead.getConfig(), TimeUtil.currentDate());
                                int readtime = (int) readMillionTime / 60000;

                                List<ReadTaskBean> readTaskBeanList = new ArrayList<>();
                                for (TaskItemBean item : resultData.lists.read_list) {
                                    ReadTaskBean readTaskBean = new ReadTaskBean();
                                    readTaskBean.setReceive(item.status == 2);
                                    readTaskBean.setTaskId(item.id);
                                    readTaskBean.setAlreadyReadTime(readtime);
                                    readTaskBeanList.add(readTaskBean);
                                }
                                mReadTipView.setData(readTaskBeanList);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    NetRequest.error(TaskCenterActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));

            }
        });
    }

    /**
     * ?????????????????????
     */
    private void initStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        ViewGroup.LayoutParams imgLayParams = mImgStatusbar.getLayoutParams();
        imgLayParams.height = statusBarHeight;
        mImgStatusbar.setLayoutParams(imgLayParams);

        ConstraintLayout.LayoutParams headParams = (ConstraintLayout.LayoutParams) mImgHead.getLayoutParams();
        headParams.setMargins(DisplayUtil.dp2px(context, 20), DisplayUtil.dp2px(context, 62) + statusBarHeight, 0, 0);
        mImgHead.setLayoutParams(headParams);

    }

    /**
     * ???????????????
     */
    private void initEvent() {
        EventBus.getDefault().register(this);
        mNsRootView.setOnScrollChanged(new TaskScrollView.onScrollChanged() {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {

                if (t < DisplayUtil.dp2px(context, 30)) {

                    mTvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
                    mImgTitleView.setBackgroundResource(R.color.color_00000000);
                    mImgStatusbar.setBackgroundResource(R.color.color_00000000);
                    mImgBack.setImageResource(R.drawable.back_white);
                    ScreenUtil.setStatusBarDark(TaskCenterActivity.this, true);

                } else if (t > DisplayUtil.dp2px(context, 30)) {

                    mTvTitle.setTextColor(getResources().getColor(R.color.colorBlack));
                    mImgTitleView.setBackgroundResource(R.color.colorWhite);
                    mImgStatusbar.setBackgroundResource(R.color.colorWhite);
                    mImgBack.setImageResource(R.drawable.back_icon);
                    ScreenUtil.setStatusBarDark(TaskCenterActivity.this, false);

                }
            }
        });

//        mReadTaskView.setOnClicksenter(new ReadTaskView.onClickLisenter() {
//            @Override
//            public void onReceive(int index, ReadTaskBean dataItem) {
//                Log.e(TAG, "mReadTaskView: ???????????? "+index+" ?????????" );
//            }
//        });
        //????????????????????????
        mReadTipView.setOnClicksenter(new ReadTaskView.onClickLisenter() {
            @Override
            public void onReceive(int index, ReadTaskBean dataItem) {
//                Log.e(TAG, "mReadTipView: ???????????? " + index + " ?????????");
                if (!dataItem.isReceive()) {
                    getReadTaskReward(Integer.parseInt(dataItem.getTaskId()), index);
                }
            }
        });
        //????????????????????????
        mNewTaskAdapter.setOnItemClickLitener(new AppendableAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                taskImplement(mNewTaskAdapter.getDataItems().get(position), 1, position);
            }
        });
        //????????????????????????
        mDailyTaskAdapter.setOnItemClickLitener(new AppendableAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                taskImplement(mDailyTaskAdapter.mDataItems.get(position), 2, position);
            }
        });

//        mCbCardRemind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b){
//                    if (!NotificationsUtils.isNotifyEnabled(TaskCenterActivity.this)){
//                        toOpenNotification();
//                    }else {
//                        //????????????
////                        mCbCardRemind.setChecked(true);
//                        Log.e(TAG, "onClick: ????????????" );
//                        setSignSwitch(1);
//                    }
//
//                }else {
//                    //????????????
////                    mCbCardRemind.setChecked(false);
//                    Log.e(TAG, "onClick: ????????????" );
//                    setSignSwitch(0);
//                }
//            }
//        });
    }


    /**
     * ???????????????????????????
     */
    private void initNotification() {
        if (NotificationsUtils.isNotifyEnabled(this)) {
            mImgMask.setVisibility(View.GONE);
//            mCbCardRemind.setChecked(true);
        } else {
            mImgMask.setVisibility(View.VISIBLE);
//            mCbCardRemind.setChecked(false);
            mTvRemainTip.setVisibility(View.VISIBLE);
            mTvRemainTip.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTvRemainTip.setVisibility(View.GONE);
                }
            }, 5000);
        }
    }

    /**
     * ?????????????????????
     */
    private void toOpenNotification() {
        mManagerDialog = new ManagerDialog(this);
        mManagerDialog.findViewById(R.id.settings).setOnClickListener(v -> {
            Intent localIntent = new Intent();
            //?????????????????????????????????????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0?????????
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getApplicationContext().getPackageName(), null));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0?????????8.0??????
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", getApplicationContext().getPackageName());
                localIntent.putExtra("app_uid", getApplicationInfo().uid);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {//4.4
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            } else {
                //4.4???????????????app????????????????????????????????????Action???????????????????????????????????????,
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (Build.VERSION.SDK_INT >= 9) {
                    localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    localIntent.setData(Uri.fromParts("package", getApplicationContext().getPackageName(), null));
                } else {
                    localIntent.setAction(Intent.ACTION_VIEW);
                    localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                    localIntent.putExtra("com.android.settings.ApplicationPkgName", getApplicationContext().getPackageName());
                }
            }
            startActivity(localIntent);
        });
        mManagerDialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mManagerDialog != null) {
            mManagerDialog.dismiss();
        }

        if (pushState == 2){
            //push ????????????
        }else if (pushState == 1){
            //push ???????????????
            initNotification();
        }

    }

    /**
     * ????????????????????????
     */
    private void setSignSwitch(int status) {
        int uid = PlotRead.getAppUser().uid;

        //???????????????1 ????????????  0 ????????????
        NetRequest.setSignSwitch(uid, status, mToken, 2, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        JSONObject resultString = jsonObject.getJSONObject("ResultData");
                        int mstatus = JSONUtil.getInt(resultString, "status");
                        if (mstatus == ONE) {
                            if (status == ONE) {
                                PlotRead.toast(PlotRead.SUCCESS, "open success");
                                mCbCardRemind.setChecked(true);
                            } else {
                                PlotRead.toast(PlotRead.SUCCESS, "close success");
                                mCbCardRemind.setChecked(false);
                            }

                        } else {
                            /*String msg = JSONUtil.getString(result, "msg");*/
                            PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(String error) {

            }
        });

    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    mToken = task.getResult();
                });
    }

    /**
     * ????????????????????????
     */
    private void initRemindTip() {
        String remindTip = getResources().getString(R.string.task_remind_tip) + getResources().getString(R.string.task_remind_open);
        String notificationTip = getResources().getString(R.string.task_remind_open);
        SpannableString spannableTip = new SpannableString(remindTip);
        int start = remindTip.indexOf(notificationTip);
        int length = remindTip.length();
        spannableTip.setSpan(new UnderlineSpan(), start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableTip.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                toOpenNotification();
            }
        }, start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableTip.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_FF7000)), start, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        remindTip = remindTip + spannableTip;
        mTvRemainTip.setText(spannableTip);
        //????????????????????????????????????????????????????????????
        mTvRemainTip.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //????????????
    public void LayoutReceivedClick() {
        Intent intent = new Intent();
//        if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
//            DeepLinkUtil.addPermanent(context, "event_wallet_received", "?????????", "?????????", "", "", "", "", "", "");
        intent.setClass(this, BillActivity.class);
        intent.putExtra("title", application.getString(R.string.bill_activity_received));
        intent.putExtra("type", ZERO);
//        } else {
//            intent.setClass(context, LoginActivity.class);
//        }
        startActivity(intent);
    }

    /**
     * ???????????????????????????????????????
     * type = 1 ???????????????2????????????
     */
    private void taskImplement(TaskItemBean itemData, int type, int position) {
        Intent intent = null;
        switch (Integer.parseInt(itemData.task_type)) {

            case TaskType.RECHARGE:
                //??????????????????????????????

                if (itemData.status == TaskTypeState.INCOMPLETE) {
                    intent = new Intent(this, TopUpActivity.class);
                    startActivity(intent);
                } else if (itemData.status == TaskTypeState.UNCLAIMED) {
                    getTaskReward(itemData, type, position);
                } else if (itemData.status == TaskTypeState.RECEIVED) {
//                    intent = new Intent(this, TopUpActivity.class);
//                    startActivity(intent);
                }

                break;

            case TaskType.OTHER:
                //??????????????????

                if (itemData.status == TaskTypeState.INCOMPLETE) {
                    toOpenNotification();
                } else if (itemData.status == TaskTypeState.UNCLAIMED) {
                    getTaskReward(itemData, type, position);
                } else if (itemData.status == TaskTypeState.RECEIVED) {

                }
                break;

            case TaskType.SUBSCRIBE:
            case TaskType.READING:
            case TaskType.REWARD:
                //???????????????

                if (itemData.status == TaskTypeState.INCOMPLETE) {
                    intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                } else if (itemData.status == TaskTypeState.UNCLAIMED) {
                    getTaskReward(itemData, type, position);
                } else if (itemData.status == TaskTypeState.RECEIVED) {

                }
                break;

            case TaskType.CONTINUE_SIGN:
                //????????????????????????

                if (itemData.status == TaskTypeState.INCOMPLETE) {

                } else if (itemData.status == TaskTypeState.UNCLAIMED) {
                    getTaskReward(itemData, type, position);
                } else if (itemData.status == TaskTypeState.RECEIVED) {

                }
                break;
        }
    }


    /**
     * ?????????????????????????????????
     */
    private void getTaskReward(TaskItemBean itemData, int type, int position) {
        if (itemData == null) {
            return;
        }
        NetRequest.getTaskReward(Integer.parseInt(itemData.id), new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String jsonString = jsonObject.getString("ResultData");
                        JSONObject resultObject = jsonObject.getJSONObject("ResultData");
                        if (resultObject != null && resultObject.getInt("status") == 1) {
                            Log.e(TAG, "onSuccess: ???????????????????????? " + resultObject.getString("msg"));
                            String taskString = resultObject.getString("task");
                            TaskReword taskBean = new Gson().fromJson(taskString, TaskReword.class);
                            //????????????bonus
                            currentBonusNum = Integer.parseInt(taskBean.giving) + currentBonusNum;
                            mTvBonusNum.setText(currentBonusNum + "");
                            //??????????????????????????????
                            mTaskCompleteDialog = new TaskCompleteDialog(context, taskBean,null);
                            mTaskCompleteDialog.show();
                            //????????????

                            Message message = Message.obtain();
                            message.what = ADD_BOUNS_SUCCESS;
                            EventBus.getDefault().post(message);

                            if (type == 1) {
                                //??????????????????????????? ????????????????????????
                                if (mNewTaskAdapter.getDataItems().size() > 1) {
                                    mNewTaskAdapter.getDataItems().remove(position);
                                    mNewTaskAdapter.notifyItemRemoved(position);
                                    mNewRecyclerView.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mNewTaskAdapter.notifyDataSetChanged();
                                        }
                                    }, 300);
                                    notifyNewTitle(mNewTaskAdapter.mDataItems);

                                } else {
                                    mCardNewView.setVisibility(View.GONE);
                                }

                            } else if (type == 2) {
                                mDailyTaskAdapter.mDataItems.get(position).status = 2;
                                mDailyTaskAdapter.notifyItemChanged(position);
                                notifyDailyTitle(mDailyTaskAdapter.mDataItems);
                            }

                        } else {
                            PlotRead.toast(PlotRead.INFO, resultObject.getString("msg"));
//                            Log.e(TAG, "failed: ???????????????????????? " + resultObject.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    NetRequest.error(TaskCenterActivity.this, serverNo);
                }

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }

    /**
     * ?????????????????? ??????
     *
     * @param dataList
     */
    private void syncNoticy(List<TaskItemBean> dataList) {

        if (dataList == null || dataList.size() == 0) {
            return;
        }

        for (int index = 0; index < dataList.size(); index++) {
            if (Integer.parseInt(dataList.get(index).task_type) == TaskType.OTHER) {
                if (dataList.get(index).status == 0) {
                    if (NotificationsUtils.isNotifyEnabled(this)) {
                        syncNotification(1, index);
                    }
                }
            }
        }
    }


    private void syncNotification(int status, int index) {
        int uid = PlotRead.getAppUser().uid;

        //???????????????1 ????????????  0 ????????????
        NetRequest.setSignSwitch(uid, status, mToken, 3, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        JSONObject resultString = jsonObject.getJSONObject("ResultData");
                        int mstatus = JSONUtil.getInt(resultString, "status");
                        if (mstatus == ONE) {

                            if (Integer.parseInt(mNewTaskAdapter.mDataItems.get(index).task_type) == TaskType.OTHER) {
                                //???????????????????????????
                                mNewTaskAdapter.mDataItems.get(index).status = 1;
                                mNewTaskAdapter.notifyItemChanged(index);
                            } else {
                                //?????? ????????????????????????????????????,??????????????????????????????
                                for (int i = 0; i < mNewTaskAdapter.mDataItems.size(); i++) {
                                    if (Integer.parseInt(mNewTaskAdapter.mDataItems.get(i).task_type) == TaskType.OTHER) {
                                        mNewTaskAdapter.mDataItems.get(i).status = 1;
                                        mNewTaskAdapter.notifyItemChanged(index);
                                    }
                                }
                            }

                        } else {
                            /*String msg = JSONUtil.getString(result, "msg");*/
                            PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(String error) {

            }
        });


    }

    /**
     * ????????????????????????
     *
     * @param dataList
     */
    private void notifyDailyTitle(List<TaskItemBean> dataList) {
        int num = 0;
        int allNum = dataList.size();

        for (TaskItemBean itemBean : dataList) {

            if (itemBean.status != 0) {
                num++;
            }
        }
        String title = getString(R.string.task_daily_title) + " (" + num + "/" + allNum + ")";
        mTvDailyTitle.setText(title);
    }

    /**
     * ????????????????????????
     *
     * @param dataList
     */
    private void notifyNewTitle(List<TaskItemBean> dataList) {
        int num = 0;
        int allNum = dataList.size();

        for (TaskItemBean itemBean : dataList) {

            if (itemBean.status != 0) {
                num++;
            }
        }
        String title = getString(R.string.task_new_title) + " (" + num + "/" + allNum + ")";
        mTvNewTitle.setText(title);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        switch (message.what) {
            case BUS_RECHARGE_SUCCESS:
                //??????????????????
                Log.e(TAG, "onEventBus: ????????????");
                //????????????????????????
                getAllTask();
                break;

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    /**
     * ??????????????????????????????
     */
    private void getExistReward() {
        NetRequest.getExistRewardStatus(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    //0 ??????????????? 1????????????
                    int claimedStatus = JSONUtil.getInt(result, "unclaimed");
                    if (claimedStatus == 1) {


                    }

                }

            }

            @Override
            public void onFailure(String error) {

            }
        });

    }


    /**
     * ??????????????????
     */
    private void uploadReadTime() {
        int uid = PlotRead.getAppUser().uid;
        int time = Integer.parseInt(TimeUtil.currentYMDDate());
        long readMillion = SharedPreferencesUtil.getLong(PlotRead.getConfig(), TimeUtil.currentDate());
        int readTime = (int) readMillion / 60000;

    }

    /**
     * ??????????????????
     * index  ?????????????????????????????????
     */
    private void getReadTaskReward(int taskId, int index) {
        if (taskId == 0) {
            return;
        }
        NetRequest.getTaskReward(taskId, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));
                        String jsonString = jsonObject.getString("ResultData");
                        JSONObject resultObject = jsonObject.getJSONObject("ResultData");
                        if (resultObject != null && resultObject.getInt("status") == 1) {
//                            Log.e(TAG, "onSuccess: ???????????????????????? " + resultObject.getString("msg"));
                            String taskString = resultObject.getString("task");
                            TaskReword taskBean = new Gson().fromJson(taskString, TaskReword.class);
                            //????????????bonus??????
                            currentBonusNum = Integer.parseInt(taskBean.giving) + currentBonusNum;
                            mTvBonusNum.setText(currentBonusNum + "");
                            //??????????????????????????????
                            mTaskCompleteDialog = new TaskCompleteDialog(context, taskBean,null);
                            mTaskCompleteDialog.show();
                            //????????????
                            //??????UI??????
                            List<ReadTaskBean> readTaskBeanList = mReadTipView.getData();
                            //index????????????1
                            readTaskBeanList.get(index - 1).setReceive(true);
                            mReadTipView.setData(readTaskBeanList);

                            Message message = Message.obtain();
                            message.what = ADD_BOUNS_SUCCESS;
                            EventBus.getDefault().post(message);
                        } else {
                            PlotRead.toast(PlotRead.INFO, resultObject.getString("msg"));
//                            Log.e(TAG, "failed: ???????????????????????? " + resultObject.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    NetRequest.error(TaskCenterActivity.this, serverNo);
                }

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }


}
