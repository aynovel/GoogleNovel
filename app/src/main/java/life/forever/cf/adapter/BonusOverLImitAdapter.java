package life.forever.cf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.BookDetail;


public class BonusOverLImitAdapter extends AppendableAdapter<BookDetail> {


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view  =  LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bonus_overtime,parent,false);
        return new OverLImitHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OverLImitHolder mHolder = (OverLImitHolder) holder;
        BookDetail detailData = mDataItems.get(position);
        mHolder.mTvTitle.setText(detailData.name);
        mHolder.mTvNumber.setText(detailData.left + " " + detailData.unit);
        mHolder.mTvStart.setText(detailData.format_time);
        mHolder.mTvEnd.setText(detailData.format_end_time);
    }

    private class OverLImitHolder extends RecyclerView.ViewHolder {
        private final TextView mTvNumber;
        private final TextView mTvTitle;
        private final TextView mTvStart;
        private final TextView mTvEnd;

        public OverLImitHolder(@NonNull View itemView) {
            super(itemView);
            mTvTitle = itemView.findViewById(R.id.tvTitle);
            mTvNumber = itemView.findViewById(R.id.tvNumber);
            mTvStart = itemView.findViewById(R.id.tvStart);
            mTvEnd = itemView.findViewById(R.id.tvEnd);
        }
    }

}
