package life.forever.cf.bookcase.bookweight;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Work;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.ScreenUtil;
import life.forever.cf.publics.tool.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeekRecommendDialog implements Constant {

    public static void show(final Activity mActivity) {
        String json = SharedPreferencesUtil.getString(PlotRead.getConfig(), SHELF_WEEK_RECOMMEND_JSON);
        if (TextUtils.isEmpty(json)) {
            return;
        }
        JSONObject recommend = JSONUtil.newJSONObject(json);
        JSONArray rec_list = JSONUtil.getJSONArray(recommend, "rec_list");
        if (rec_list == null || rec_list.length() == ZERO) {
            return;
        }
        final List<Work> works = new ArrayList<>();
        JSONObject rec_info = JSONUtil.getJSONObject(recommend, "rec_info");
        int recId = JSONUtil.getInt(rec_info, "rec_id");
        for (int i = ZERO; i < rec_list.length(); i++) {
            JSONObject child = JSONUtil.getJSONObject(rec_list, i);
            Work work = BeanParser.getWork(child);
            work.recId = recId;
            works.add(work);
        }

        View root = LayoutInflater.from(mActivity).inflate(R.layout.layout_week_recommend_dialog, null, FALSE);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setCancelable(FALSE);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = ScreenUtil.getScreenWidth(mActivity);
        window.setAttributes(attributes);

        RecyclerView mRecyclerView = window.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, FALSE));

        window.findViewById(R.id.receive).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                MobclickAgent.onEvent(context, SJ_4);
                ShelfUtil.insert(mActivity, works,false);
                SharedPreferencesUtil.remove(PlotRead.getConfig(), SHELF_WEEK_RECOMMEND_JSON);
                dialog.dismiss();
            }
        });
        window.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.remove(PlotRead.getConfig(), SHELF_WEEK_RECOMMEND_JSON);
                dialog.dismiss();
            }
        });


        mRecyclerView.setAdapter(new WeekAdapter(mActivity, works));
    }

    private static class WeekAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;
        private final List<Work> works;

        WeekAdapter(Context context, List<Work> works) {
            this.context = context;
            this.works = works;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WeekViewHolder(LayoutInflater.from(context).inflate(R.layout.item_week_recommend, parent, FALSE));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Work work = works.get(position);
            WeekViewHolder viewHolder = (WeekViewHolder) holder;
            GlideUtil.load(context, work.cover, R.drawable.default_work_cover, viewHolder.cover);
            viewHolder.title.setText(work.title);
            holder.itemView.setOnClickListener(new OnItemClick(context, work));
        }

        @Override
        public int getItemCount() {
            return works.size();
        }
    }

    private static class OnItemClick implements View.OnClickListener {

        private final Context context;
        private final Work work;

        OnItemClick(Context context, Work work) {
            this.context = context;
            this.work = work;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, WorkDetailActivity.class);
            intent.putExtra("wid", work.wid);
            intent.putExtra("recid", work.recId);
            context.startActivity(intent);
        }
    }

    private static class WeekViewHolder extends RecyclerView.ViewHolder {

        public ImageView cover;
        public TextView title;

        WeekViewHolder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.cover);
            title = itemView.findViewById(R.id.title);
        }
    }
}
