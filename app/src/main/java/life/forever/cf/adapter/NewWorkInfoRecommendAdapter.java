package life.forever.cf.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import life.forever.cf.R;
import life.forever.cf.entry.BookEndRecommend;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.publics.tool.GlideUtil;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewWorkInfoRecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<BookEndRecommend> mBookEndRecommends;


    public NewWorkInfoRecommendAdapter(Context context, List<BookEndRecommend> mBookEndRecommends) {
        this.context = context;
        this.mBookEndRecommends = mBookEndRecommends;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecommendViewHolder(LayoutInflater.from(context).inflate(R.layout.you_like_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BookEndRecommend mBookEndRecommend = mBookEndRecommends.get(position);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < mBookEndRecommend.tag.size(); i++) {
            list.add(mBookEndRecommend.tag.get(i).tag);
        }
        String tag = Joiner.on("·").join(list);

        holder.itemView.setOnClickListener(new OnItemClick(mBookEndRecommend.wid, mBookEndRecommend.rec_id));
        RecommendViewHolder viewHolder = (RecommendViewHolder) holder;
        GlideUtil.loads(context, mBookEndRecommend.h_url, R.drawable.default_work_cover, viewHolder.cover);
        viewHolder.book_name.setText(mBookEndRecommend.title);
        viewHolder.book_author.setText(mBookEndRecommend.author);
        viewHolder.book_info.setText(mBookEndRecommend.description);

        if (TextUtils.isEmpty(mBookEndRecommend.sort_name)) {
            viewHolder.book_other.setText(tag);
        } else {
            viewHolder.book_other.setText(mBookEndRecommend.sort_name + "·" + tag);
        }

        if (mBookEndRecommend.isimg != null){

            if (mBookEndRecommend.isimg.equals("1")){
                Glide.with(context).load(mBookEndRecommend.isimgUrl).into(viewHolder.imgLogo);
            }else {
                viewHolder.imgLogo.setVisibility(View.GONE);
            }

        } else {
            viewHolder.imgLogo.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mBookEndRecommends.size();
    }

    private class OnItemClick implements View.OnClickListener {

        private final int wid;
        private final int recid;

        OnItemClick(int wid, int recid) {
            this.wid = wid;
            this.recid = recid;
        }

        @Override
        public void onClick(View v) {

             Intent intent = new Intent(context, WorkDetailActivity.class);
            intent.putExtra("wid", wid);
            intent.putExtra("recid", recid);
            context.startActivity(intent);
        }
    }

    class RecommendViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.book_name)
        TextView book_name;
        @BindView(R.id.book_author)
        TextView book_author;
        @BindView(R.id.book_info)
        TextView book_info;
        @BindView(R.id.book_other)
        TextView book_other;
        @BindView(R.id.imgLogo)
        ImageView imgLogo;

        RecommendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
