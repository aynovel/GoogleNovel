package life.forever.cf.activtiy;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
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
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.R;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.bookcase.bookweight.EditPopup;
import life.forever.cf.publics.BaseRecyclerViewActivity;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.CustomDialog;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReadHistoryActivity extends BaseRecyclerViewActivity {

    private final List<Work> works = new ArrayList<>();
    private final List<Work> selects = new ArrayList<>();
    private ReadHistoryAdapter historyAdapter;

    private int pageIndex = ONE;
    private int totalPage = ZERO;
    private boolean isEditStatus;
    private EditPopup editPopup;
    private CustomDialog mCustomDialog;

    @Override
    protected void initializeView() {
        super.initializeView();
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(SHELF_STRING_READ_HISTORY);
        mTitleBar.setRightImageResource(R.drawable.icon_edit);
        mTitleBar.setRightImageViewOnClickListener(onClearClick);
        mTitleBar.showLeftImageView(TRUE);
        mTitleBar.showRightImageView(TRUE);
        mRefreshLayout.setHasHeader(FALSE);
        mLoadFooter.setOnLoadListener(onLoadListener);
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
            fetch();
        }
    };

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        historyAdapter = new ReadHistoryAdapter();
        historyAdapter.setOnItemClickListener(onItemClickListener);
        mRecyclerView.setAdapter(historyAdapter);
        fetch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_SHELF_CHANGE) {
            historyAdapter.notifyDataSetChanged();
            return;
        }
        if (message.what == BUS_LOG_IN) {
            works.clear();
            historyAdapter.notifyDataSetChanged();
            reload();
        }
    }

    private final View.OnClickListener onClearClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!isEditStatus && works.size() != 0) {
                startEdit();
            }
        }
    };

    private final View.OnClickListener onEndEditClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            endEdit();
        }
    };

    /**
     * 启动编辑模式
     */
    private void startEdit() {
        isEditStatus = true;
        mTitleBar.showRightImageView(FALSE);
        mTitleBar.setRightText("Done");
        mTitleBar.setRightTextViewOnClickListener(onEndEditClick);
        historyAdapter.update(isEditStatus, selects);
        if (editPopup == null) {
            editPopup = new EditPopup(context, onEditItemClick);
        }
        editPopup.show(mTitleBar, works.size());
    }

    /**
     * 退出编辑模式
     */
    public void endEdit() {
        isEditStatus = false;
        selects.clear();
        mTitleBar.getRightTextView().setVisibility(View.GONE);
        mTitleBar.setRightImageResource(R.drawable.icon_edit);
        mTitleBar.setRightImageViewOnClickListener(onClearClick);
        mTitleBar.showRightImageView(TRUE);
        historyAdapter.update(isEditStatus, selects);
        if (editPopup != null && editPopup.isShowing()) {
            editPopup.dismiss();
        }
    }

    private final EditPopup.OnItemClickListener onEditItemClick = new EditPopup.OnItemClickListener() {

        @Override
        public void onItemClick(TextView textView, int position) {
            if (position == ZERO) {
                if (selects.size() == works.size()) {
                    selects.clear();
                } else {
                    selects.clear();
                    selects.addAll(works);
                }
                historyAdapter.notifyDataSetChanged();
                editPopup.update(works.size(), selects.size());
                return;
            }
            if (position == ONE) {
                String info;
                if (selects.size() == works.size()) {
                    info = getString(R.string.delete_history_checkall);
                } else {
                    info = getString(R.string.delete_history_radio);
                }
                mCustomDialog = new CustomDialog(ReadHistoryActivity.this, info, new CustomDialog.OnDialogClickListener() {

                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.cancel:
                                mCustomDialog.dismiss();
                                break;
                            case R.id.delete:
                                String bookId = "";
                                for (Work work : selects) {
                                    if (TextUtils.isEmpty(bookId)) {
                                        bookId += work.wid;
                                    } else {
                                        bookId += "," + work.wid;
                                    }
                                }
                                deleteReadRecord(bookId);
                                mCustomDialog.dismiss();
                                break;
                        }
                    }
                });
                mCustomDialog.show();
                Window dialogWindow = mCustomDialog.getWindow();
                dialogWindow.setGravity(Gravity.CENTER);
                WindowManager.LayoutParams params = mCustomDialog.getWindow().getAttributes();
                //设置dialog的背景颜色为透明色,就可以显示圆角了!!
                mCustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mCustomDialog.getWindow().setAttributes(params);
            }
        }
    };

    private final OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(int position) {
            if (works.size()>0) {
                Work work = works.get(position);
                if (isEditStatus) {
                    if (selects.contains(work)) {
                        selects.remove(work);
                    } else {
                        selects.add(work);
                    }
                    historyAdapter.notifyItemChanged(position);
                    editPopup.update(works.size(), selects.size());
                } else {
                    Intent intent = new Intent(context, WorkDetailActivity.class);
                    intent.putExtra("wid", work.wid);
                    startActivity(intent);
                }
            }
        }
    };

    private class ReadHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Work> selects;
        private boolean isEditStatus;
        private OnItemClickListener onItemClickListener;

        public void update(boolean isEditStatus, List<Work> selects) {
            this.isEditStatus = isEditStatus;
            this.selects = selects;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ZERO) {
                return new NoneViewHolder(context, parent);
            }
            return new ReadHistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_read_history, parent, FALSE));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            holder.itemView.setOnClickListener(new OnItemViewClick(position));
            if (holder instanceof NoneViewHolder) {
                NoneViewHolder viewHolder = (NoneViewHolder) holder;
                viewHolder.description.setText(getString(R.string.no_lookbook_record));
                return;
            }
            ReadHistoryViewHolder viewHolder = (ReadHistoryViewHolder) holder;
            final Work work = works.get(position);
            if (isEditStatus) {
                viewHolder.select.setVisibility(View.VISIBLE);
                if (selects.contains(work)) {
                    viewHolder.select.setImageResource(R.drawable.book_shelf_item_selected);
                } else {
                    viewHolder.select.setImageResource(R.drawable.book_shelf_item_unselected);
                }
            } else {
                viewHolder.select.setVisibility(View.GONE);
            }

            GlideUtil.picCache(context,work.cover,work.wid+"small",R.drawable.default_work_cover, viewHolder.cover);

//            String cover = PlotRead.getConfig().getString(work.wid + "small", "");
//            if (TextUtils.isEmpty(cover)) {
////                SharedPreferencesUtil.putString(PlotRead.getConfig(), work.wid + "small", work.cover);
//                GlideUtil.recommentLoad(context,work.wid+"small",work.cover, work.cover, R.drawable.default_work_cover, viewHolder.cover);
//            } else {
//                GlideUtil.recommentLoad(context,"",cover, work.cover, R.drawable.default_work_cover, viewHolder.cover);
//            }
            viewHolder.title.setText(work.title);
            viewHolder.author.setText(work.author);
            viewHolder.date.setText(ComYou.timeFormat(work.lasttime, DATE_FORMATTER_10));
            if (ShelfUtil.exist(work.wid)) {
                viewHolder.collect.setVisibility(View.GONE);
            } else {
                viewHolder.collect.setVisibility(View.VISIBLE);
                viewHolder.collect.setEnabled(TRUE);
                viewHolder.collect.setText(context.getString(R.string.add_bookcase));
            }
            viewHolder.collect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShelfUtil.insert(ReadHistoryActivity.this, work,false);
                    PlotRead.toast(PlotRead.SUCCESS, context.getString(R.string.bookshelf_added_successfully));
                }
            });
//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, WorkDetailActivity.class);
//                    intent.putExtra("wid", work.wid);
//                    startActivity(intent);
//                }
//            });
        }

        @Override
        public int getItemCount() {
            if (works.size() == ZERO) {
                return ONE;
            }
            return works.size();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        class OnItemViewClick implements View.OnClickListener {

            private final int position;

            OnItemViewClick(int position) {
                this.position = position;
            }

            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (works.size() == ZERO) {
                return ZERO;
            }
            return ONE;
        }
    }

    static class ReadHistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cover)
        ImageView cover;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.collect)
        TextView collect;
        @BindView(R.id.select)
        ImageView select;

        ReadHistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private void fetch() {
        NetRequest.userReadRecord(pageIndex, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mContentLayout.setVisibility(View.VISIBLE);
                }
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int nums = JSONUtil.getInt(result, "nums");
                    if (pageIndex == ONE && totalPage == ZERO) {
                        totalPage = nums % TWENTY == ZERO ? nums / TWENTY : nums / TWENTY + ONE;
                        mRefreshLayout.setHasFooter(totalPage > ONE);
                    }
                    JSONArray booklist = JSONUtil.getJSONArray(result, "booklist");
                    for (int i = ZERO; booklist != null && i < booklist.length(); i++) {
                        JSONObject child = JSONUtil.getJSONObject(booklist, i);
                        Work work = BeanParser.getWork(child);
                        work.lasttime = JSONUtil.getInt(child, "readtime");
                        works.add(work);
                    }
                    historyAdapter.notifyDataSetChanged();
                    pageIndex++;
                    mRefreshLayout.setHasFooter(pageIndex <= totalPage);
                } else {
                    NetRequest.error(ReadHistoryActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
                if (mRefreshLayout.isLoading()) {
                    mRefreshLayout.stopLoad();
                } else {
                    mLoadingLayout.setVisibility(View.GONE);
                    mWrongLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 删除阅读历史
     */
    private void deleteReadRecord(String bookId) {
        if (works.isEmpty()) {
            endEdit();
            return;
        }
        showLoading(getString(R.string.cleaning_empty));
        NetRequest.deleteReadRecord(bookId, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        works.clear();
                        selects.clear();
                        historyAdapter.notifyDataSetChanged();
                        ShelfUtil.clearRecord();
                        endEdit();
                        PlotRead.toast(PlotRead.SUCCESS, getString(R.string.clean_all));
                        pageIndex = ONE;
                        fetch();
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    @Override
    protected void reload() {
        pageIndex = ONE;
        totalPage = ZERO;
        mLoadingLayout.setVisibility(View.VISIBLE);
        mWrongLayout.setVisibility(View.GONE);
        fetch();
    }
}
