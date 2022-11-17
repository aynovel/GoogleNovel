package life.forever.cf.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.TaskType;
import life.forever.cf.entry.TaskTypeState;
import life.forever.cf.entry.TaskItemBean;

public class TaskAdapter extends AppendableAdapter<TaskItemBean> {


    private final Context mContext;

    public TaskAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_task, parent, false);
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder,  int position) {
        TaskHolder mHolder = (TaskHolder) viewHolder;
        TaskItemBean itemData = mDataItems.get(position);

        mHolder.tvBonus.setText("+"+itemData.giving+" bonus");
        mHolder.tvTitle.setText(itemData.title);
        mHolder.tvContent.setText(itemData.description);

        int contentResource = 0;
        int bacgroundResourse = 0;
        int textColorResource = 0;

        switch (Integer.parseInt(itemData.task_type)) {
            //充值任务
            case TaskType.RECHARGE:
                if (itemData.status == TaskTypeState.INCOMPLETE) {
                    //未完成
                    contentResource = R.string.task_item_recharge;
                    bacgroundResourse = R.drawable.bg_task_item_nomal;
                    textColorResource = R.color.color_F9791C;

                } else if (itemData.status == TaskTypeState.RECEIVED) {
                    //完成已领取
                    contentResource = R.string.task_item_claimed;
                    bacgroundResourse = R.drawable.bg_task_item_claimed;
                    textColorResource = R.color.color_CECDCD;

                } else if (itemData.status == TaskTypeState.UNCLAIMED) {
                    //完成未领取
                    contentResource = R.string.task_item_claime;
                    bacgroundResourse = R.drawable.bg_task_item_claime;
                    textColorResource = R.color.colorWhite;

                }

                break;
            //订阅
            case TaskType.SUBSCRIBE:
                //阅读
            case TaskType.READING:
                //打赏
            case TaskType.REWARD:
                if (itemData.status == TaskTypeState.INCOMPLETE) {

                    contentResource = R.string.task_item_go;
                    bacgroundResourse = R.drawable.bg_task_item_nomal;
                    textColorResource = R.color.color_F9791C;

                } else if (itemData.status == TaskTypeState.RECEIVED) {

                    contentResource = R.string.task_item_claimed;
                    bacgroundResourse = R.drawable.bg_task_item_claimed;
                    textColorResource = R.color.color_CECDCD;

                } else if (itemData.status == TaskTypeState.UNCLAIMED) {

                    contentResource = R.string.task_item_claime;
                    bacgroundResourse = R.drawable.bg_task_item_claime;
                    textColorResource = R.color.colorWhite;
                }
                break;
            //其他
            case TaskType.OTHER:

                if (itemData.status == TaskTypeState.INCOMPLETE) {

                    bacgroundResourse = R.drawable.bg_task_item_nomal;
                    contentResource = R.string.task_item_open;
                    textColorResource = R.color.color_F9791C;

                } else if (itemData.status == TaskTypeState.RECEIVED) {

                    contentResource = R.string.task_item_claimed;
                    bacgroundResourse = R.drawable.bg_task_item_claimed;
                    textColorResource = R.color.color_CECDCD;

                } else if (itemData.status == TaskTypeState.UNCLAIMED) {

                    contentResource = R.string.task_item_claime;
                    bacgroundResourse = R.drawable.bg_task_item_claime;
                    textColorResource = R.color.colorWhite;

                }
                break;

//            case TaskType.CONTINUE_SIGN:
//                //连续签到
//
//                break;

            default:
                //连续签到任务十分的不确定  根据任务id 43  44 判断是否是连续签到

                if (itemData != null) {

                    if (itemData.id.equals("43") || itemData.id.equals("44")) {

                        if (itemData.status == TaskTypeState.INCOMPLETE) {

                            String a = itemData.ncontinue + "";
                            String b = "/" + itemData.limit;
                            String contentString = a + b;

                            SpannableString spannableString = new SpannableString(contentString);

                            int aStart = contentString.indexOf(a);
                            int bStart = contentString.indexOf(b);
                            int bEnd = contentString.length();

                            //设置字体颜色
                            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_F9791C)),
                                    aStart, bStart, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_AAAAAA)),
                                    bStart, bEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            mHolder.tvState.setBackgroundResource(R.drawable.bg_task_item_sign);
                            mHolder.tvState.setText(spannableString);

                        } else if (itemData.status == TaskTypeState.RECEIVED) {

                            contentResource = R.string.task_item_claimed;
                            bacgroundResourse = R.drawable.bg_task_item_claimed;
                            textColorResource = R.color.color_CECDCD;

                        } else if (itemData.status == TaskTypeState.UNCLAIMED) {
                            contentResource = R.string.task_item_claime;
                            bacgroundResourse = R.drawable.bg_task_item_claime;
                            textColorResource = R.color.colorWhite;
                        }
                    }else {

                        if (itemData.status == TaskTypeState.INCOMPLETE) {

                            contentResource = R.string.task_item_go;
                            bacgroundResourse = R.drawable.bg_task_item_nomal;
                            textColorResource = R.color.color_F9791C;

                        } else if (itemData.status == TaskTypeState.RECEIVED) {

                            contentResource = R.string.task_item_claimed;
                            bacgroundResourse = R.drawable.bg_task_item_claimed;
                            textColorResource = R.color.color_CECDCD;

                        } else if (itemData.status == TaskTypeState.UNCLAIMED) {

                            contentResource = R.string.task_item_claime;
                            bacgroundResourse = R.drawable.bg_task_item_claime;
                            textColorResource = R.color.colorWhite;
                        }

                    }
                }
                break;


        }

        if (contentResource != 0 && bacgroundResourse != 0) {
            mHolder.tvState.setBackgroundResource(bacgroundResourse);
            mHolder.tvState.setText(mContext.getResources().getString(contentResource));
            mHolder.tvState.setTextColor(mContext.getResources().getColor(textColorResource));
        }

        mHolder.tvState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickLitener.onItemClick(view, position);
            }
        });

    }


    public class TaskHolder extends RecyclerView.ViewHolder {

        private final TextView tvBonus;
        private final TextView tvTitle;
        private final TextView tvContent;
        private final TextView tvState;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            tvBonus = itemView.findViewById(R.id.tvBonus);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvState = itemView.findViewById(R.id.tvState);
        }

    }

}
