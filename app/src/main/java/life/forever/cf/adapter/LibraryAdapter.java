package life.forever.cf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.LibraryBean;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.GlideUtil;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;


public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.RecomHolder> {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<LibraryBean.ResultData.Records> mList;
    private OnItemClickListener onItemClickListener;

    public LibraryAdapter(Context context, List<LibraryBean.ResultData.Records> List) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mList = List;
    }


    public void data(List<LibraryBean.ResultData.Records> contentlist) {
        this.mList = contentlist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecomHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View convertView = mInflater.inflate(R.layout.discover_more_item, viewGroup, false);
        RecomHolder holder = new RecomHolder(convertView);
        convertView.setTag(holder);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final RecomHolder holder, final int position) {
        LibraryBean.ResultData.Records bean = mList.get(position);
        holder.mRanking.setVisibility(View.GONE);
        List<String> list = new ArrayList<>();

        if(bean.tag != null)
        {
            for (int i = 0; i < bean.tag.size(); i++) {
                list.add(bean.tag.get(i).tag);
            }
        }

        String tag = Joiner.on("·").join(list);

        GlideUtil.picCache(mContext,bean.h_url,bean.wid+"small",R.drawable.default_work_cover, holder.mCover);


        holder.mBookName.setText(bean.title);
        holder.mBookAuthor.setText(bean.author);
        holder.mSortName.setText(tag);
        String mIsVip;
        String mIsFinish;
        if ("1".equals(bean.is_vip)) {
            mIsVip = "Charges";
        } else {
            mIsVip = "Free";
        }

        if ("1".equals(bean.is_finish)) {
            mIsFinish = "Completed";
        } else {
            mIsFinish = "Uncompleted";
        }
        holder.mReadNum.setText(ComYou.formatNum((int)bean.pv) + " View" + "·" + mIsVip + "·" + mIsFinish);
        holder.mBookInfo.setText(bean.description);

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

        public ImageView mCover;
        public TextView mRanking;
        public TextView mBookName;
        public TextView mBookAuthor;
        public TextView mSortName;
        public TextView mReadNum;
        public TextView mBookInfo;

        public RecomHolder(View itemView) {
            super(itemView);
            mCover = itemView.findViewById(R.id.cover);
            mRanking = itemView.findViewById(R.id.ranking);
            mBookName = itemView.findViewById(R.id.book_name);
            mBookAuthor = itemView.findViewById(R.id.book_author);
            mSortName = itemView.findViewById(R.id.sort_name);
            mReadNum = itemView.findViewById(R.id.read_num);
            mBookInfo = itemView.findViewById(R.id.book_info);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

}
