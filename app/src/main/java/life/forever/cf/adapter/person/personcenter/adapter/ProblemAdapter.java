package life.forever.cf.adapter.person.personcenter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.adapter.person.personcenter.bean.ProblemBean;

import java.util.ArrayList;
import java.util.List;

public class ProblemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int TYPE_ONE = 0;
    public final static int TYPE_TWO = 1;
    private final Context mContext;
    private final List<String> beans;
    private List<ProblemBean.ResultData.Data> mProblemBeanList;

    public ProblemAdapter(Context context, List<ProblemBean.ResultData.Data> ProblemBeanList) {
        this.mContext = context;
        this.mProblemBeanList = ProblemBeanList;
        this.beans = new ArrayList<>();
    }

    public void add(String bean) {
        this.beans.add(bean);
        notifyDataSetChanged();
    }

    public void data(List<ProblemBean.ResultData.Data> ProblemBeanList) {
        this.mProblemBeanList = ProblemBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (beans.get(position).contains("0")) {
            return TYPE_ONE;
        } else {
            return TYPE_TWO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.problem_item, parent, false);
        switch (viewType) {
            case TYPE_ONE:
                holder = new ViewHolderOne(view);
                break;
            case TYPE_TWO:
                holder = new ViewHolderTwo(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int positions) {
        LinearLayoutManager LayoutManager = new LinearLayoutManager(mContext);
        LayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (holder instanceof ViewHolderOne) {
            ((ViewHolderOne) holder).mTitle.setText(mProblemBeanList.get(positions).title);
            ((ViewHolderOne) holder).mOneRecyclerView.setLayoutManager(LayoutManager);
            ((ViewHolderOne) holder).mOneRecyclerView.setHasFixedSize(true);
            ((ViewHolderOne) holder).mOneRecyclerView.setItemAnimator(new DefaultItemAnimator());
            ProblemItemAdapter mOneRVAdapter = new ProblemItemAdapter(mContext, TYPE_ONE, mProblemBeanList.get(positions).list);
            ((ViewHolderOne) holder).mOneRecyclerView.setAdapter(mOneRVAdapter);
        } else if (holder instanceof ViewHolderTwo) {
            ((ViewHolderTwo) holder).mTitle.setText(mProblemBeanList.get(positions).title);
            ((ViewHolderTwo) holder).mTwoRecyclerView.setLayoutManager(LayoutManager);
            ((ViewHolderTwo) holder).mTwoRecyclerView.setHasFixedSize(true);
            ((ViewHolderTwo) holder).mTwoRecyclerView.setItemAnimator(new DefaultItemAnimator());
            ProblemItemAdapter mTwoRVAdapter = new ProblemItemAdapter(mContext, TYPE_TWO, mProblemBeanList.get(positions).list);
            ((ViewHolderTwo) holder).mTwoRecyclerView.setAdapter(mTwoRVAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    static class ViewHolderOne extends RecyclerView.ViewHolder {
        TextView mTitle;
        RecyclerView mOneRecyclerView;

        public ViewHolderOne(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tv_name);
            mOneRecyclerView = itemView.findViewById(R.id.rcv_content);
        }
    }

    static class ViewHolderTwo extends RecyclerView.ViewHolder {
        TextView mTitle;
        RecyclerView mTwoRecyclerView;

        public ViewHolderTwo(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.tv_name);
            mTwoRecyclerView = itemView.findViewById(R.id.rcv_content);
        }
    }
}
