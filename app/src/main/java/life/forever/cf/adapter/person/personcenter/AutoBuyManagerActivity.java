package life.forever.cf.adapter.person.personcenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.AutoBuy;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.GlideUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AutoBuyManagerActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.openAll)
    View mOpenAll;
    @BindView(R.id.closeAll)
    View mCloseAll;

    List<AutoBuy> infos;
    private AutoBuyManageAdapter autoBuyManageAdapter;

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_auto_buy_manage);
        ButterKnife.bind(this);
        mTitleBar.setMiddleText(MINE_STRING_SETTING_AUTO_BUY_MANAGE);
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
    }

    @Override
    protected void initializeData() {
        infos = AutoBuyUtil.query();
        updateBtns();
        autoBuyManageAdapter = new AutoBuyManageAdapter();
        mRecyclerView.setAdapter(autoBuyManageAdapter);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private void updateBtns() {
        if (infos.size() == ZERO) {
            mOpenAll.setEnabled(FALSE);
            mCloseAll.setEnabled(FALSE);
        } else {
            boolean allCheck = TRUE;
            boolean hasCheck = FALSE;
            for (AutoBuy info : infos) {
                if (allCheck) {
                    allCheck = allCheck && info.check == ONE;
                }
                if (!hasCheck) {
                    hasCheck = hasCheck || info.check == ONE;
                }
                if (!allCheck && hasCheck) {
                    break;
                }
            }
            if (allCheck) {
                mOpenAll.setEnabled(FALSE);
                mCloseAll.setEnabled(TRUE);
            } else {
                mOpenAll.setEnabled(TRUE);
                if (hasCheck) {
                    mCloseAll.setEnabled(TRUE);
                } else {
                    mCloseAll.setEnabled(FALSE);
                }
            }
        }
    }

    @OnClick(R.id.openAll)
    void OnOpenClick() {
        for (AutoBuy auto : infos) {
            auto.check = ZERO;
            auto.timestamp = ComYou.currentTimeSeconds();
        }
        mOpenAll.setEnabled(FALSE);
        mCloseAll.setEnabled(TRUE);
        autoBuyManageAdapter.notifyDataSetChanged();
        AutoBuyUtil.insert(infos);
        PlotRead.toast(PlotRead.SUCCESS, getString(R.string.set_success));
    }

    @OnClick(R.id.closeAll)
    void OnCloseClick() {
        for (AutoBuy auto : infos) {
            auto.check = ONE;
            auto.timestamp = ComYou.currentTimeSeconds();
        }
        mOpenAll.setEnabled(TRUE);
        mCloseAll.setEnabled(FALSE);
        autoBuyManageAdapter.notifyDataSetChanged();
        AutoBuyUtil.insert(infos);
        PlotRead.toast(PlotRead.SUCCESS, getString(R.string.set_success));
    }

    class AutoBuyManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ONE) {
                return new NoneViewHolder(getBaseContext(), parent);
            }
            return new AutoBuyManageViewHolder(LayoutInflater.from(getBaseContext()).inflate(R.layout.item_auto_buy_manage, parent, FALSE));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof AutoBuyManageViewHolder) {
                AutoBuy autoBuy = infos.get(position);
                AutoBuyManageViewHolder viewHolder = (AutoBuyManageViewHolder) holder;
                GlideUtil.load(context, autoBuy.cover, R.drawable.default_work_cover, viewHolder.cover);
                viewHolder.title.setText(autoBuy.title);
                viewHolder.check.setChecked(autoBuy.check == ONE);
                viewHolder.itemView.setOnClickListener(new OnItemClick(autoBuy));
            }
        }

        @Override
        public int getItemCount() {
            if (infos.size() == ZERO) {
                return ONE;
            }
            return infos.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (infos.size() == ZERO) {
                return ONE;
            }
            return ZERO;
        }
    }

    private class OnItemClick implements View.OnClickListener {

        private final AutoBuy autoBuy;

        OnItemClick(AutoBuy autoBuy) {
            this.autoBuy = autoBuy;
        }

        @Override
        public void onClick(View v) {
            autoBuy.check = autoBuy.check == ONE ? ZERO : ONE;
            autoBuyManageAdapter.notifyDataSetChanged();
            AutoBuyUtil.insert(autoBuy);
            updateBtns();
            PlotRead.toast(PlotRead.SUCCESS, getString(R.string.set_success));
        }
    }

    class AutoBuyManageViewHolder extends RecyclerView.ViewHolder {

        ImageView cover;
        TextView title;
        CheckBox check;

        AutoBuyManageViewHolder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
            check = itemView.findViewById(R.id.checkBox);
        }
    }

}
