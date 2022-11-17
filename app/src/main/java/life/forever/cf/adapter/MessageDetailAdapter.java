package life.forever.cf.adapter;

import static life.forever.cf.publics.Constant.DATE_FORMATTER_7;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import life.forever.cf.R;
import life.forever.cf.entry.MsgDetailBean;
import life.forever.cf.publics.tool.ComYou;

import java.util.List;


public class MessageDetailAdapter extends RecyclerView.Adapter<MessageDetailAdapter.RecomHolder> {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final List<MsgDetailBean.ResultData.list> mList;

    public MessageDetailAdapter(Context context, List<MsgDetailBean.ResultData.list> List) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mList = List;
    }

    @NonNull
    @Override
    public RecomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View convertView = mInflater.inflate(R.layout.message_item, viewGroup, false);
        RecomHolder holder = new RecomHolder(convertView);
        convertView.setTag(holder);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final RecomHolder holder, final int position) {
        MsgDetailBean.ResultData.list bean = mList.get(position);
        Glide.with(mContext).load("").placeholder(R.drawable.icon_msg_head).into(holder.mMsgCover);
        String mTime = ComYou.timeFormat(Integer.parseInt(bean.addtime), DATE_FORMATTER_7);
        holder.mTvInfoTime.setText(mTime);
        holder.mTvInfo.setText(bean.content);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class RecomHolder extends RecyclerView.ViewHolder {

        public ImageView mMsgCover;
        public TextView mTvInfoTime;
        public TextView mTvInfo;

        public RecomHolder(View itemView) {
            super(itemView);
            mMsgCover = itemView.findViewById(R.id.msg_cover);
            mTvInfoTime = itemView.findViewById(R.id.tv_info_time);
            mTvInfo = itemView.findViewById(R.id.tv_info);
        }
    }

}
