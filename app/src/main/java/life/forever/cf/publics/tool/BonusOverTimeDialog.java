package life.forever.cf.publics.tool;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.OverLimmitBook;
import life.forever.cf.adapter.BonusOverLImitAdapter;


public class BonusOverTimeDialog extends Dialog implements View.OnClickListener {

    private final Context mContext;
    private final OverLimmitBook mData;
    private final TextView mTvRequestTip;

    public BonusOverTimeDialog(final Context context, OverLimmitBook data) {
        super(context, R.style.Theme_Update_Dialog);
        mContext = context;
        mData = data;
        this.setContentView(R.layout.bonus_overtime_dialog);
        setCanceledOnTouchOutside(true);
        Window window = getWindow();
        WindowManager.LayoutParams params;
        if (window != null) {
            params = window.getAttributes();
            Resources resources = context.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            if (params != null) {
                params.width = (int) (dm.widthPixels * 0.98);
                params.height = dm.heightPixels;
            }
            window.setAttributes(params);
        }

        //收入
        RecyclerView bonusRecyclerView = findViewById(R.id.bonusRecyclerView);
        ImageView imgQuestion = findViewById(R.id.imgQuestion);
        mTvRequestTip = findViewById(R.id.tvRequestTip);
        ImageView imgCancle = findViewById(R.id.imgCancle);
        ImageView imgEmpty = findViewById(R.id.imgEmpty);
        TextView tvEmpty = findViewById(R.id.tvEmpty);
        ConstraintLayout csMiddleView = findViewById(R.id.csMiddleView);


        ViewGroup.LayoutParams layoutParams = csMiddleView.getLayoutParams();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        Drawable drawable = mContext.getResources().getDrawable(R.drawable.bg_item_decoration);
        bonusRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(drawable);
        bonusRecyclerView.addItemDecoration(dividerItemDecoration);

        BonusOverLImitAdapter bonusOverLImitAdapter = new BonusOverLImitAdapter();
        bonusRecyclerView.setAdapter(bonusOverLImitAdapter);
        imgQuestion.setOnClickListener(this);
        imgCancle.setOnClickListener(this);

        if (data != null && data.list != null) {

            if (data.list.size() >= 5) {
                layoutParams.height = DisplayUtil.dp2px(mContext, 75 * 5 + 90);
                csMiddleView.setLayoutParams(layoutParams);
                bonusOverLImitAdapter.setDataItems(data.list);
            } else if (data.list.size() > 0 && data.list.size() < 5) {

                layoutParams.height = DisplayUtil.dp2px(mContext, 75 * data.list.size() + 90);
                csMiddleView.setLayoutParams(layoutParams);
                bonusOverLImitAdapter.setDataItems(data.list);

            } else if (data.list.size() == 0) {
                imgEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.VISIBLE);
                bonusRecyclerView.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgQuestion:

                if (mTvRequestTip.getVisibility() == View.GONE) {
                    mTvRequestTip.setVisibility(View.VISIBLE);
                } else {
                    mTvRequestTip.setVisibility(View.GONE);
                }

                break;

            case R.id.imgCancle:
                dismiss();
                break;
        }
    }
}
