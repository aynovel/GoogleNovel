package life.forever.cf.publics.tool;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.adapter.ReportAdapter;
import life.forever.cf.adapter.AppendableAdapter;

import java.util.List;


public class ReportDialog extends Dialog {

    private final Context mContext;
    private final List<String> mTypeList;

    public ReportDialog(final Context context, List<String> typeList,int selectPosition) {
        super(context, R.style.Theme_Report_Dialog);
        mContext = context;
        mTypeList = typeList;
        this.setContentView(R.layout.report_dialog);
        setCanceledOnTouchOutside(true);
        Window window = getWindow();
        WindowManager.LayoutParams params;
        if (window != null) {
            params = window.getAttributes();
            Resources resources = context.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            if (params != null) {
                params.width = dm.widthPixels ;
                params.height = dm.heightPixels;
            }
            window.setAttributes(params);
        }

        //收入
        RelativeLayout cancle =  findViewById(R.id.rlCancle);
        RecyclerView mRecyclerView = findViewById(R.id.typeRecyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        ReportAdapter reportAdapter = new ReportAdapter();
        mRecyclerView.setAdapter(reportAdapter);
        reportAdapter.setDataItems(typeList);
        if (selectPosition != -1){
            reportAdapter.setSelectItem(selectPosition);
        }

        reportAdapter.setOnItemClickLitener(new AppendableAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (onSelectListener != null){
                    onSelectListener.onSelect(position, reportAdapter.getDataItems().get(position));
                }
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                },300);
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

   public interface OnSelectListener{
        void onSelect(int position,String name);
    }

    private OnSelectListener onSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener){
        this.onSelectListener = onSelectListener;
    }

}
