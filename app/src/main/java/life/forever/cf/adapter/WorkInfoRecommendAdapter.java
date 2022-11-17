package life.forever.cf.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import life.forever.cf.R;
import life.forever.cf.entry.Work;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.GlideUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkInfoRecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Work> works;
    private final int type;
    private String ishot;
    private String mIsimg;
    private String mLogoUrl;

    public WorkInfoRecommendAdapter(Context context, List<Work> works, int type) {
        this.context = context;
        this.works = works;
        this.type = type;
    }

    public void setNotify(String ishot){
        this.ishot = ishot;
        notifyDataSetChanged();
    }

    public void setLable(String isImg,String logoUrl){
        mIsimg = isImg;
        mLogoUrl = logoUrl;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecommendViewHolder(LayoutInflater.from(context).inflate(R.layout.discover_read_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Work work = works.get(position);
        holder.itemView.setOnClickListener(new OnItemClick(work.wid, work.recId));
        RecommendViewHolder viewHolder = (RecommendViewHolder) holder;
        GlideUtil.loads(context, work.cover, R.drawable.default_work_cover, viewHolder.cover);
        viewHolder.book_name.setText(work.title);
        viewHolder.ll_hot.setVisibility(View.GONE);

        if (mIsimg != null && mLogoUrl != null){
            if (mIsimg.equals("1")){

                Glide.with(context).load(mLogoUrl).into(viewHolder.mImgLogo);
                viewHolder.mImgLogo.setVisibility(View.VISIBLE);
            }else {
                viewHolder.mImgLogo.setVisibility(View.GONE);
            }
        }else {
            viewHolder.mImgLogo.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return works.size();
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
//            if (type == Constant.ZERO) {
//                MobclickAgent.onEvent(context, Constant.XQ_8);
//            } else if (type == Constant.ONE) {
//                MobclickAgent.onEvent(context, Constant.XQ_10);
//            } else if (type == Constant.TWO) {
//                MobclickAgent.onEvent(context, Constant.YDWY_5);
//            } else if (type == Constant.THREE) {
//                MobclickAgent.onEvent(context, Constant.YDWY_6);
//            }
            DeepLinkUtil.addPermanent(context,"event_details_recommend","详情页","详情底部推荐位","",wid+"","","","","");
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
        @BindView(R.id.ll_hot)
        LinearLayout ll_hot;
        @BindView(R.id.read_num)
        TextView read_num;
        @BindView(R.id.imgLogo)
        ImageView mImgLogo;

        RecommendViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
