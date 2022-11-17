package life.forever.cf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;

import java.util.HashMap;

public class ReportAdapter extends AppendableAdapter<String> {

    private final HashMap<Integer, Boolean> mSelectMap = new HashMap<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feedback, parent, false);
        return new FeedBackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FeedBackViewHolder mHolder = (FeedBackViewHolder) holder;
        mHolder.mName.setText(mDataItems.get(position));

        if (mSelectMap.get(position) != null) {
            if (mSelectMap.get(position)) {
                mHolder.mImageSelect.setImageResource(R.drawable.icon_report_select);
            } else {
                mHolder.mImageSelect.setImageResource(R.drawable.icon_report_unselect);
            }
        } else {
            mHolder.mImageSelect.setImageResource(R.drawable.icon_report_unselect);
        }

        mHolder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetMap();
                mSelectMap.put(position, true);
                notifyDataSetChanged();
                mOnItemClickLitener.onItemClick(view, position);
            }
        });

    }

    public class FeedBackViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mImageSelect;
        private final TextView mName;
        private final ConstraintLayout mRootView;

        public FeedBackViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageSelect = itemView.findViewById(R.id.imgSelect);
            mName = itemView.findViewById(R.id.tvName);
            mRootView = itemView.findViewById(R.id.csRootView);

        }
    }

    /**
     * 设置选中选项
     */
    public void setSelectItem(int position) {
        mSelectMap.put(position, true);
        notifyDataSetChanged();
    }

    /**
     * 重置map值
     */
    public void resetMap() {

        for (int i = 0; i < mDataItems.size(); i++) {
            mSelectMap.put(i, false);
        }
    }

}
