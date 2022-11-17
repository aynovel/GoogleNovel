package life.forever.cf.adapter.person.personcenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.ManagerDialog;
import life.forever.cf.publics.tool.NotificationsUtils;
import life.forever.cf.publics.weight.TaskCheckBox;
import com.google.firebase.messaging.FirebaseMessaging;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PushUpdateManagerActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    //蒙层
    @BindView(R.id.imgMask)
    ImageView mImgMask;
    @BindView(R.id.cbCardRemind)
    TaskCheckBox mCbCardRemind;
    @BindView(R.id.ll_notify)
    RelativeLayout mLlNotifyRootView;

    private final List<Work> works = new ArrayList<>();
    private PushManageAdapter mPushManageAdapter;
    private String mToken;
    private int push = ZERO;
    private ManagerDialog mManagerDialog;

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_auto_buy_manage);
        ButterKnife.bind(this);
        mTitleBar.setMiddleText(MINE_STRING_SETTING_SHELF_PUSH_MANAGE);
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mRootLayout.setBackgroundResource(R.color.color_FEFFFF);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        getToken();
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        mPushManageAdapter = new PushManageAdapter();
        mRecyclerView.setAdapter(mPushManageAdapter);
        works.addAll(ShelfUtil.queryShelfWorks());
        mPushManageAdapter.notifyDataSetChanged();
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
    }

    private final View.OnClickListener onBackClick = v -> onBackPressed();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_SHELF_CHANGE) {
            works.clear();
            works.addAll(ShelfUtil.queryShelfWorks());
            mPushManageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private class PushManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ZERO) {
                return new NoneViewHolder(PushUpdateManagerActivity.this, parent);
            }
            return new PushManageViewHolder(LayoutInflater.from(PushUpdateManagerActivity.this).inflate(R.layout.item_auto_buy_manage, parent, FALSE));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof NoneViewHolder) {
                NoneViewHolder viewHolder = (NoneViewHolder) holder;
                viewHolder.description.setText(getString(R.string.no_lookbook_record));
                return;
            }
            if (holder instanceof PushManageViewHolder) {
                Work work = works.get(position);
                boolean check;
                PushManageViewHolder viewHolder = (PushManageViewHolder) holder;
                GlideUtil.picCache(context, work.cover,work.wid + "small",R.drawable.default_work_cover,  viewHolder.cover);

//                String cover = PlotRead.getConfig().getString(work.wid + "small", "");
//                if (TextUtils.isEmpty(cover)) {
////                    SharedPreferencesUtil.putString(PlotRead.getConfig(), work.wid + "small", work.cover);
//                    GlideUtil.recommentLoad(context,work.wid + "small", work.cover, work.cover, R.drawable.default_work_cover, viewHolder.cover);
//                } else {
//                    GlideUtil.recommentLoad(context,"", cover, work.cover, R.drawable.default_work_cover, viewHolder.cover);
//                }
                viewHolder.title.setText(work.title);
                viewHolder.mTvAuthor.setText(work.author);
                check = "1".equals(work.push);
                viewHolder.checkBox.setChecked(check);

                if (work.isfinish == ONE) {
                    viewHolder.mTvTime.setText("Completed");
                } else {
                    viewHolder.mTvTime.setText(String.format("Updated %s", ComYou.formatTime(work.updatetime)));
                }

                holder.itemView.setOnClickListener(v -> setPush(works.get(position),position));
            }
        }

        @Override
        public int getItemCount() {
            if (works.size() == ZERO) {
                return ONE;
            }
            return works.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (works.size() == ZERO) {
                return ZERO;
            }
            return ONE;
        }
    }

    private static class PushManageViewHolder extends RecyclerView.ViewHolder {

        public ImageView cover;
        public TextView title, mTvAuthor, mTvTime;
        public CheckBox checkBox;

        PushManageViewHolder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
            mTvAuthor = itemView.findViewById(R.id.tv_author);
            mTvTime = itemView.findViewById(R.id.tv_time);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
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

    private void setPush(final Work work,int position) {
        showLoading(getString(R.string.seting));
        int uid = PlotRead.getAppUser().uid;
        int wid = work.wid;
        if ("0".equals(work.push)) {
            push = ONE;
        } else {
            push = ZERO;
        }
        NetRequest.setWorkPushs(Constant.TWO, uid, wid, push,1, mToken, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        if (push == ONE) {
                            work.push = "1";
                            works.get(position).push = "1";
                            ShelfUtil.updateWork(work);
                            PlotRead.toast(PlotRead.SUCCESS, "open success");
                        } else {
                            work.push = "0";
                            works.get(position).push = "0";
                            ShelfUtil.updateWork(work);
                            PlotRead.toast(PlotRead.SUCCESS, "close success");
                        }

//                        ShelfUtil.shelfDownload(PushUpdateManagerActivity.this, ZERO,true);
//                        works.clear();
//                        mPushManageAdapter.notifyDataSetChanged();

//                        works.clear();
//                        works.addAll(ShelfUtil.queryShelfWorks());
                        mPushManageAdapter.notifyItemChanged(position);

                    } else {
                        /*String msg = JSONUtil.getString(result, "msg");*/
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }


    @OnClick({R.id.imgMask,R.id.cbCardRemind})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgMask:
                toOpenNotification();
                break;
            case R.id.cbCardRemind:
                if (!mCbCardRemind.isChecked()) {
                    if (!NotificationsUtils.isNotifyEnabled(this)) {
                        toOpenNotification();
                    }

                } else {

                }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mManagerDialog != null) {
            mManagerDialog.dismiss();
        }
        initNotification();
    }


    /**
     * 初始化签到开关提醒
     */
    private void initNotification() {
        if (NotificationsUtils.isNotifyEnabled(this)) {
            mLlNotifyRootView.setVisibility(View.GONE);
            mImgMask.setVisibility(View.GONE);
            mCbCardRemind.setChecked(true);
        } else {
            mLlNotifyRootView.setVisibility(View.VISIBLE);
            mImgMask.setVisibility(View.VISIBLE);
            mCbCardRemind.setChecked(false);
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



}
