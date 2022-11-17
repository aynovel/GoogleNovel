package life.forever.cf.adapter.person.readingtask;

import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.Task;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constant {

    private final BaseActivity context;
    private final List<Task> tasks;

    TaskAdapter(BaseActivity context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ZERO) {
            return new NoneViewHolder(context, parent);
        }
        return new TaskViewHolder(LayoutInflater.from(context).inflate(R.layout.item_task, parent, FALSE));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoneViewHolder) {
            NoneViewHolder viewHolder = (NoneViewHolder) holder;
            viewHolder.description.setText(context.getString(R.string.series_completed));
            return;
        }
        TaskViewHolder viewHolder = (TaskViewHolder) holder;
        Task task = tasks.get(position);
        viewHolder.mTaskName.setText(task.title);
        viewHolder.mTaskAward.setText(task.reward);
        if (task.status == ZERO) {
            viewHolder.mTaskStatus.setText(TASK_STRING_WAIT_COMPLETED);
            viewHolder.mTaskStatus.setTextColor(ORANGE);
            viewHolder.mTaskStatus.setEnabled(FALSE);
        } else if (task.status == ONE) {
            viewHolder.mTaskStatus.setText(TASK_STRING_RECEIVE);
            viewHolder.mTaskStatus.setTextColor(THEME_COLOR);
            viewHolder.mTaskStatus.setEnabled(TRUE);
        } else {
            viewHolder.mTaskStatus.setText(TASK_STRING_COMPLETED);
            viewHolder.mTaskStatus.setTextColor(GRAY_1);
            viewHolder.mTaskStatus.setEnabled(FALSE);
        }
        viewHolder.mTaskStatus.setOnClickListener(new OnReceiveClick(task));
        viewHolder.itemView.setOnClickListener(new OnItemClick(task));
    }

    @Override
    public int getItemCount() {
        if (tasks.size() == ZERO) {
            return ONE;
        }
        return tasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (tasks.size() == ZERO) {
            return ZERO;
        }
        return ONE;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.taskName)
        TextView mTaskName;
        @BindView(R.id.taskAward)
        TextView mTaskAward;
        @BindView(R.id.taskStatus)
        TextView mTaskStatus;

        TaskViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class OnItemClick implements View.OnClickListener {

        private final Task task;

        OnItemClick(Task task) {
            this.task = task;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
//            if (task.id == 19 && task.status == ZERO) { // 绑定手机号任务
//                intent.setClass(context, BindPhoneNumActivity.class);
//            } else {
            intent.setClass(context, TaskDetailActivity.class);
            intent.putExtra("task", task);
//            }
            context.startActivity(intent);
        }
    }

    private class OnReceiveClick implements View.OnClickListener {

        private final Task task;

        OnReceiveClick(Task task) {
            this.task = task;
        }

        @Override
        public void onClick(View v) {
            context.showLoading(context.getString(R.string.collecting));
            NetRequest.receiveTaskAward(task.id, new OkHttpResult() {

                @Override
                public void onSuccess(JSONObject data) {
                    if(ComYou.isDestroy(context)){
                        return;
                    }
                    context.dismissLoading();
                    String serverNo = JSONUtil.getString(data, "ServerNo");
                    if (SN000.equals(serverNo)) {
                        JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                        int status = JSONUtil.getInt(result, "status");
                        if (status == ONE) {
                            task.status = TWO;
                            notifyDataSetChanged();
                            // 刷新用户余额
                            PlotRead.getAppUser().fetchUserMoney();
                            // 弹窗
                            ReceiveAwardSuccessDialog.show(context, context.getString(R.string.congratulations_success), task, null);
                            // 如果是新手礼包，则通知书城取消领取按钮
                            if (task.id == 17) {
                                Message message = Message.obtain();
                                message.what = BUS_FRESH_GIFT_RECEIVED;
                                EventBus.getDefault().post(message);
                            }
                        } else {
                            String msg = JSONUtil.getString(result, "msg");
                            PlotRead.toast(PlotRead.INFO, context.getString(R.string.no_internet));
                        }
                    }
                }

                @Override
                public void onFailure(String error) {
                    context.dismissLoading();
                    PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                }
            });
        }
    }
}
