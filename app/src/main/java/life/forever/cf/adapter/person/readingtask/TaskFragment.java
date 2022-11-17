package life.forever.cf.adapter.person.readingtask;

import android.os.Bundle;
import android.os.Message;
import android.view.View;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Task;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.BaseRecyclerViewFragment;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.scrollweight.ScrollHelper;
import life.forever.cf.publics.tool.JSONUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TaskFragment extends BaseRecyclerViewFragment implements ScrollHelper.ScrollParent {

    public static int TASK_TYPE_DAY = TWO;
    public static int TASK_TYPE_GUIDE = ONE;
    public static int TASK_TYPE_SUM = THREE;

    private int type;
    private final List<Task> tasks = new ArrayList<>();
    private TaskAdapter taskAdapter;

    public static TaskFragment get(int type) {
        TaskFragment taskFragment = new TaskFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        taskFragment.setArguments(bundle);
        return taskFragment;
    }

    @Override
    protected void bindView() {
        super.bindView();
        mTitleBar.setVisibility(View.GONE);
        mRefreshLayout.setHasHeader(FALSE);
        mRefreshLayout.setHasFooter(FALSE);
    }

    @Override
    protected void fetchData() {
        EventBus.getDefault().register(this);
        type = getArguments().getInt("type");
        taskAdapter = new TaskAdapter((BaseActivity) getActivity(), tasks);
        mRecyclerView.setAdapter(taskAdapter);
        fetchTask();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_TASK_STATUS_CHANGE && (int) message.obj == type) {
            mLoadingLayout.setVisibility(View.VISIBLE);
            mContentLayout.setVisibility(View.GONE);
            fetchTask();
            return;
        }
        if (message.what == BUS_LOG_IN) {
            reload();
        }
    }

    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
        mWrongLayout.setVisibility(View.GONE);
        fetchTask();
    }

    private void fetchTask() {
        NetRequest.userTask(type, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                mLoadingLayout.setVisibility(View.GONE);
                mContentLayout.setVisibility(View.VISIBLE);
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        tasks.clear();
                        JSONArray lists = JSONUtil.getJSONArray(result, "lists");
                        for (int i = ZERO; lists != null && i < lists.length(); i++) {
                            JSONObject child = JSONUtil.getJSONObject(lists, i);
                            Task task = BeanParser.getTask(child);
                            if (type == TASK_TYPE_GUIDE && task.status == TWO) {
                                continue;
                            }
                            tasks.add(task);
                        }
                        taskAdapter.notifyDataSetChanged();
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.FAIL, msg);
                    }
                } else {
                    NetRequest.error(getActivity(), serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                mLoadingLayout.setVisibility(View.GONE);
                mWrongLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public View getScrollView() {
        return mRecyclerView;
    }
}
