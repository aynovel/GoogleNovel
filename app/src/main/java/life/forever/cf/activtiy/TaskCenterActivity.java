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
 * @Description : 任务中心页面：签到，新手任务，每日任务
 * 任务有自动领取功能
 */
public class TaskCenterActivity extends BaseActivity {

    //签到
    @BindView(R.id.signRecyclerView)
    RecyclerView mSignRecyclerView;
    //新手任务
    @BindView(R.id.newRecyclerView)
    RecyclerView mNewRecyclerView;
    //每日任务
    @BindView(R.id.dailyRecyclerView)
    RecyclerView mDailyRecyclerView;
    //状态栏填充布局
    @BindView(R.id.imgStatusbar)
    ImageView mImgStatusbar;
    @BindView(R.id.imgHead)
    RadiusImageView mImgHead;
    //根nescrollview
    @BindView(R.id.nsRootView)
    TaskScrollView mNsRootView;
    //标题
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    //标题栏背景
    @BindView(R.id.imgTitleView)
    ImageView mImgTitleView;
    //返回按键
    @BindView(R.id.imgBack)
    ImageView mImgBack;
    //签到开关遮罩
    @BindView(R.id.imgMask)
    ImageView mImgMask;
    //签到提醒开关
    @BindView(R.id.cbCardRemind)
    TaskCheckBox mCbCardRemind;
    @BindView(R.id.tvRemainTip)
    TextView mTvRemainTip;
    //明日获得书卷
    @BindView(R.id.tvCardTitle)
    TextView mTvCardTitle;
    //书卷
    @BindView(R.id.tvBonusNum)
    TextView mTvBonusNum;
    @BindView(R.id.readTipView)
    ReadTipView mReadTipView;
    //新手任务总布局
    @BindView(R.id.cardNew)
    CardView mCardNewView;
    //每日任务标题
    @BindView(R.id.tvDailyTitle)
    TextView mTvDailyTitle;
    @BindView(R.id.tvNewTitle)
    TextView mTvNewTitle;
    //今日阅读时间
    @BindView(R.id.tvReadContent)
    TextView mTvReadContent;


    private ManagerDialog mManagerDialog;
    //签到弹窗
    private TaskSignDialog mTaskSignDialog;
    //领取任务奖励弹窗
    private TaskCompleteDialog mTaskCompleteDialog;
    private SignBean.ResultData mSignBean = new SignBean.ResultData();
    private SignRVAdapter mSignRVAdapter;
    //新手任务
    private TaskAdapter mNewTaskAdapter;
    //日常任务
    private TaskAdapter mDailyTaskAdapter;

    private final String TAG = "TaskCenterActivity";
    private String mToken;
    private int currentBonusNum;
    //0 未知 ，1 打开  ,2 未打开
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
                        //打开推送
//                        mCbCardRemind.setChecked(true);
                        Log.e(TAG, "onClick: 打开推送");
                        setSignSwitch(1);
                    }

                } else {
                    //关闭推送
//                    mCbCardRemind.setChecked(false);
                    Log.e(TAG, "onClick: 关闭推送");
                    setSignSwitch(0);
                }
                break;
            case R.id.tvDetail:
                //跳转收支记录页面
                LayoutReceivedClick();
                break;
        }
    }

    /**
     * 签到
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
     * 请求签到信息
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
                            String signInfo = "Tomorrow you’ll get " + mSignBean.info.next_price + " bonus";
                            mTvCardTitle.setText(signInfo);
                            //签到开关
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
     * 签到
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
                                // 刷新数据
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

                                // 发送通知
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
//                            // 刷新数据
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
     * 获取全部任务,阅读任务，新手任务，
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


                                //同步推送状态
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
     * 获取状态栏高度
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
     * 事件与监听
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
//                Log.e(TAG, "mReadTaskView: 点击了第 "+index+" 个图片" );
//            }
//        });
        //阅读控件点击事件
        mReadTipView.setOnClicksenter(new ReadTaskView.onClickLisenter() {
            @Override
            public void onReceive(int index, ReadTaskBean dataItem) {
//                Log.e(TAG, "mReadTipView: 点击了第 " + index + " 个图片");
                if (!dataItem.isReceive()) {
                    getReadTaskReward(Integer.parseInt(dataItem.getTaskId()), index);
                }
            }
        });
        //新手任务点击事件
        mNewTaskAdapter.setOnItemClickLitener(new AppendableAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                taskImplement(mNewTaskAdapter.getDataItems().get(position), 1, position);
            }
        });
        //每日任务点击事件
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
//                        //打开推送
////                        mCbCardRemind.setChecked(true);
//                        Log.e(TAG, "onClick: 打开推送" );
//                        setSignSwitch(1);
//                    }
//
//                }else {
//                    //关闭推送
////                    mCbCardRemind.setChecked(false);
//                    Log.e(TAG, "onClick: 关闭推送" );
//                    setSignSwitch(0);
//                }
//            }
//        });
    }


    /**
     * 初始化签到开关提醒
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
     * 去打开通知权限
     */
    private void toOpenNotification() {
        mManagerDialog = new ManagerDialog(this);
        mManagerDialog.findViewById(R.id.settings).setOnClickListener(v -> {
            Intent localIntent = new Intent();
            //直接跳转到应用通知设置的代码：
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0及以上
                localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getApplicationContext().getPackageName(), null));
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上到8.0以下
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", getApplicationContext().getPackageName());
                localIntent.putExtra("app_uid", getApplicationInfo().uid);
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {//4.4
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            } else {
                //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
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
            //push 开关关闭
        }else if (pushState == 1){
            //push 开关已打开
            initNotification();
        }

    }

    /**
     * 设置签到提醒开关
     */
    private void setSignSwitch(int status) {
        int uid = PlotRead.getAppUser().uid;

        //推送状态：1 打开推送  0 关闭推送
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
     * 通知权限关闭提醒
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
        //添加这一行指定区域的文字点击事件才能生效
        mTvRemainTip.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //收支记录
    public void LayoutReceivedClick() {
        Intent intent = new Intent();
//        if (PlotRead.getAppUser().login() && !PlotRead.getAppUser().isVisitor) {
//            DeepLinkUtil.addPermanent(context, "event_wallet_received", "钱包页", "收入页", "", "", "", "", "", "");
        intent.setClass(this, BillActivity.class);
        intent.putExtra("title", application.getString(R.string.bill_activity_received));
        intent.putExtra("type", ZERO);
//        } else {
//            intent.setClass(context, LoginActivity.class);
//        }
        startActivity(intent);
    }

    /**
     * 新手任务和每日任务点击事件
     * type = 1 新手任务，2每日任务
     */
    private void taskImplement(TaskItemBean itemData, int type, int position) {
        Intent intent = null;
        switch (Integer.parseInt(itemData.task_type)) {

            case TaskType.RECHARGE:
                //充值类型跳转充值页面

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
                //打开通知开关

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
                //跳转发现页

                if (itemData.status == TaskTypeState.INCOMPLETE) {
                    intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                } else if (itemData.status == TaskTypeState.UNCLAIMED) {
                    getTaskReward(itemData, type, position);
                } else if (itemData.status == TaskTypeState.RECEIVED) {

                }
                break;

            case TaskType.CONTINUE_SIGN:
                //签到任务不做操作

                if (itemData.status == TaskTypeState.INCOMPLETE) {

                } else if (itemData.status == TaskTypeState.UNCLAIMED) {
                    getTaskReward(itemData, type, position);
                } else if (itemData.status == TaskTypeState.RECEIVED) {

                }
                break;
        }
    }


    /**
     * 每日，新手领取任务奖励
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
                            Log.e(TAG, "onSuccess: 任务奖励领取成功 " + resultObject.getString("msg"));
                            String taskString = resultObject.getString("task");
                            TaskReword taskBean = new Gson().fromJson(taskString, TaskReword.class);
                            //更新本页bonus
                            currentBonusNum = Integer.parseInt(taskBean.giving) + currentBonusNum;
                            mTvBonusNum.setText(currentBonusNum + "");
                            //显示领取任务奖励弹窗
                            mTaskCompleteDialog = new TaskCompleteDialog(context, taskBean,null);
                            mTaskCompleteDialog.show();
                            //后续操作

                            Message message = Message.obtain();
                            message.what = ADD_BOUNS_SUCCESS;
                            EventBus.getDefault().post(message);

                            if (type == 1) {
                                //新手任务完成则取消 新手任务卡片布局
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
//                            Log.e(TAG, "failed: 任务奖励领取失败 " + resultObject.getString("msg"));
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
     * 同步消息通知 状态
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

        //推送状态：1 打开推送  0 关闭推送
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
                                //更新本通知任务状态
                                mNewTaskAdapter.mDataItems.get(index).status = 1;
                                mNewTaskAdapter.notifyItemChanged(index);
                            } else {
                                //加入 用户手速快操作了新手任务,从列表中寻找然后操作
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
     * 更新每日任务标题
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
     * 更新新手任务标题
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
                //充值成功任务
                Log.e(TAG, "onEventBus: 充值任务");
                //重新获取所有任务
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
     * 是否存在奖励需要领取
     */
    private void getExistReward() {
        NetRequest.getExistRewardStatus(new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    //0 没有待领取 1有待领取
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
     * 上报阅读时常
     */
    private void uploadReadTime() {
        int uid = PlotRead.getAppUser().uid;
        int time = Integer.parseInt(TimeUtil.currentYMDDate());
        long readMillion = SharedPreferencesUtil.getLong(PlotRead.getConfig(), TimeUtil.currentDate());
        int readTime = (int) readMillion / 60000;

    }

    /**
     * 领取任务奖励
     * index  领取的是第几阶段的礼物
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
//                            Log.e(TAG, "onSuccess: 任务奖励领取成功 " + resultObject.getString("msg"));
                            String taskString = resultObject.getString("task");
                            TaskReword taskBean = new Gson().fromJson(taskString, TaskReword.class);
                            //更新本页bonus数据
                            currentBonusNum = Integer.parseInt(taskBean.giving) + currentBonusNum;
                            mTvBonusNum.setText(currentBonusNum + "");
                            //显示领取任务奖励弹窗
                            mTaskCompleteDialog = new TaskCompleteDialog(context, taskBean,null);
                            mTaskCompleteDialog.show();
                            //后续操作
                            //更新UI页面
                            List<ReadTaskBean> readTaskBeanList = mReadTipView.getData();
                            //index比下标多1
                            readTaskBeanList.get(index - 1).setReceive(true);
                            mReadTipView.setData(readTaskBeanList);

                            Message message = Message.obtain();
                            message.what = ADD_BOUNS_SUCCESS;
                            EventBus.getDefault().post(message);
                        } else {
                            PlotRead.toast(PlotRead.INFO, resultObject.getString("msg"));
//                            Log.e(TAG, "failed: 任务奖励领取失败 " + resultObject.getString("msg"));
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
