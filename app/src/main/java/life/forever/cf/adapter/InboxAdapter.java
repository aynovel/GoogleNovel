package life.forever.cf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.InboxBean;
import life.forever.cf.publics.tool.GlideUtil;

import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.RecomHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    List<InboxBean.ResultData.Lists.Rec_list> mList;
    private OnItemClickListener onItemClickListener;

    public InboxAdapter(Context context, List<InboxBean.ResultData.Lists.Rec_list> List) {
        super();
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mList = List;
    }

    public void data(List<InboxBean.ResultData.Lists.Rec_list> List) {
        this.mList = List;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View mConvertView = mInflater.inflate(R.layout.item_inbox, viewGroup, false);
        RecomHolder holder = new RecomHolder(mConvertView);
        mConvertView.setTag(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecomHolder holder, final int position) {
        InboxBean.ResultData.Lists.Rec_list bean =  mList.get(position);

        GlideUtil.picCache(mContext,bean.recimg,bean.id+"info",R.drawable.default_info_cover, holder.mInboxImg);

//        String cover = PlotRead.getConfig().getString(bean.id+"info","");
//        if (TextUtils.isEmpty(cover)){
////            SharedPreferencesUtil.putString(PlotRead.getConfig(), bean.id+"info",bean.recimg);
//            GlideUtil.recommentLoad(mContext,bean.id+"info",bean.recimg,bean.recimg,R.drawable.default_info_cover, holder.mInboxImg);
//        }else{
//            GlideUtil.recommentLoad(mContext,"",cover,bean.recimg,R.drawable.default_info_cover, holder.mInboxImg);
//        }

        holder.mName.setText(bean.title);
        holder.mReadNum.setText(bean.config_num);

        if (mList != null) {
            holder.itemView.setOnClickListener(new OnItemViewClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class OnItemViewClick implements View.OnClickListener {

        private final int position;

        OnItemViewClick(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        }
    }


    public static class RecomHolder extends RecyclerView.ViewHolder {
        public ImageView mInboxImg;
        public TextView mName;
        public TextView mReadNum;

        public RecomHolder(View itemView) {
            super(itemView);
            mInboxImg = itemView.findViewById(R.id.inbox_img);
            mName = itemView.findViewById(R.id.inbox_name);
            mReadNum = itemView.findViewById(R.id.tv_read_num);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }
}
