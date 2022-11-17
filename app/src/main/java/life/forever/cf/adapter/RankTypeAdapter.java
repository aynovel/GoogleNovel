package life.forever.cf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import life.forever.cf.R;
import life.forever.cf.entry.RankType;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.OnItemClickListener;

import java.util.List;


public class RankTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constant {

    private final Context context;
    private final List<RankType> types;
    private final OnItemClickListener onItemClickListener;
    private int current = ZERO;

    public RankTypeAdapter(Context context, List<RankType> types, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.types = types;
        this.onItemClickListener = onItemClickListener;
    }

    public void update(int position) {
        this.current = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RankTypeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_left_indicator, parent, FALSE));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder);
            }
        });

        RankType HRRankType = types.get(position);
        RankTypeViewHolder viewHolder = (RankTypeViewHolder) holder;
        viewHolder.checkBox.setChecked(position == current);
//        viewHolder.checkBox.setText(rankType.title);
        if (current == position) {
            Glide.with(context).load(HRRankType.icon_image).placeholder(R.drawable.icon_type_select).into(viewHolder.iv_type);
                /*String cover = PlotRead.getConfig().getString(HRRankType.id+"ranksort","");
                if (TextUtils.isEmpty(cover)){
                    SharedPreferencesUtil.putString(PlotRead.getConfig(), HRRankType.id+"ranksort",HRRankType.icon_image);
                    GlideUtil.rankLoad(context,HRRankType.icon_image,HRRankType.icon_image,R.drawable.icon_type_select, viewHolder.iv_type);
                }else{
                    GlideUtil.rankLoad(context,cover,HRRankType.icon_image,R.drawable.icon_type_select, viewHolder.iv_type);
                }*/
        } else {
            Glide.with(context).load(HRRankType.icon_gray_image).placeholder(R.drawable.icon_type_unselect).into(viewHolder.iv_type);
            /*String cover = PlotRead.getConfig().getString(HRRankType.id + "unranksort", "");
            if (TextUtils.isEmpty(cover)) {
                SharedPreferencesUtil.putString(PlotRead.getConfig(), HRRankType.id + "unranksort", HRRankType.icon_gray_image);
                GlideUtil.rankLoad(context, HRRankType.icon_gray_image, HRRankType.icon_gray_image, R.drawable.icon_type_unselect, viewHolder.iv_type);
            } else {
                GlideUtil.rankLoad(context, cover, HRRankType.icon_gray_image, R.drawable.icon_type_unselect, viewHolder.iv_type);
            }*/
        }

    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    private static class RankTypeViewHolder extends RecyclerView.ViewHolder {

        private final CheckBox checkBox;
        private final ImageView iv_type;

        public RankTypeViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            iv_type = itemView.findViewById(R.id.iv_type);
        }
    }
}
