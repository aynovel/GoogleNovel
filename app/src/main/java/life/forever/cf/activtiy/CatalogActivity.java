package life.forever.cf.activtiy;

import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.entry.Catalog;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.sql.CacheSQLiteHelper;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CatalogActivity extends BaseActivity {

    @BindView(R.id.status)
    TextView mStatus;
    @BindView(R.id.counts)
    TextView mCounts;
    @BindView(R.id.order)
    TextView mOrder;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private final List<Catalog> catalogs = new ArrayList<>();
    private CatalogAdapter catalogAdapter;
    private CacheSQLiteHelper cacheSQLiteHelper;

    private Work work;

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_work_catalog);
        ButterKnife.bind(this);
        mTitleBar.setMiddleText(aiye_STRING_CATALOG);
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        work = getIntent().getParcelableExtra("work");
        catalogAdapter = new CatalogAdapter();
        mRecyclerView.setAdapter(catalogAdapter);
        if (work != null){
            cacheSQLiteHelper = CacheSQLiteHelper.get(CatalogActivity.this, work.wid);
            cacheSQLiteHelper.query(catalogs);
        }
        fetch();
    }

    private void fetch() {
        int start = 0;
        int count = -1;

        // 本地无缓存
        if (catalogs.size() == 0) {
            fetchWorkCatalog(start, count);
            return;
        }
        // 有更新章节
        if (catalogs.size() < work.totalChapter) {
            start = catalogs.size();
            count = work.totalChapter - catalogs.size();
            fetchWorkCatalog(start, count);
            return;
        }

        fillUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        if (message.what == BUS_LOG_IN) {
            reload();
        }
    }

    @Override
    protected void reload() {
        mLoadingLayout.setVisibility(View.VISIBLE);
        mContentLayout.setVisibility(View.GONE);
        mWrongLayout.setVisibility(View.GONE);
        fetch();
    }

    /**
     * 请求作品目录
     *
     * @param start
     * @param count
     */
    private void fetchWorkCatalog(int start, int count) {
        NetRequest.workCatalog(work.wid, start, count, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    JSONArray array = JSONUtil.getJSONArray(result, "catalog");
                    for (int i = ZERO; array != null && i < array.length(); i++) {
                        JSONObject child = JSONUtil.getJSONObject(array, i);
                        Catalog catalog = BeanParser.getCatalog(child);
                        catalogs.add(catalog);
                    }
                    if (cacheSQLiteHelper != null){
                        cacheSQLiteHelper.insert(catalogs);
                    }
                    fillUI();
                } else {
                    NetRequest.error(CatalogActivity.this, serverNo);
                }

            }

            @Override
            public void onFailure(String error) {
                mLoadingLayout.setVisibility(View.GONE);
                mWrongLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 填充数据
     */
    private void fillUI() {
        mStatus.setText(work.isfinish == ZERO ? context.getString(R.string.serial) : context.getString(R.string.completed));
        mCounts.setText(String.format(Locale.getDefault(), getString(R.string.totil_chapter), catalogs.size()));
        catalogAdapter.notifyDataSetChanged();
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.order)
    public void onOrderClick() {
        if (catalogAdapter.reverse()) {
            catalogAdapter.update(FALSE);
            mOrder.setText(getString(R.string.reverse_order));
        } else {
            catalogAdapter.update(TRUE);
            mOrder.setText(getString(R.string.positive_sequence));
        }
    }

    /**
     * 目录适配器
     *
     * @author haojie
     */
    private class CatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private boolean reverse;

        public void update(boolean reverse) {
            this.reverse = reverse;
            notifyDataSetChanged();
        }

        boolean reverse() {
            return reverse;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CatalogViewHolder(LayoutInflater.from(context).inflate(R.layout.item_catalog, parent, FALSE));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Catalog catalog;
            if (reverse) {
                catalog = catalogs.get(catalogs.size() - position - 1);
            } else {
                catalog = catalogs.get(position);
            }
            CatalogViewHolder catalogViewHolder = (CatalogViewHolder) holder;
            catalogViewHolder.title.setText(catalog.title);
            catalogViewHolder.status.setVisibility(catalog.isvip == ZERO ? View.GONE : View.VISIBLE);
            holder.itemView.setOnClickListener(new OnItemClick(catalog));
        }

        @Override
        public int getItemCount() {
            return catalogs.size();
        }

    }

    /**
     * 目录布局装载器
     *
     * @author haojie
     */
    class CatalogViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.status)
        View status;

        CatalogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class OnItemClick implements View.OnClickListener {

        private final Catalog catalog;

        OnItemClick(Catalog catalog) {
            this.catalog = catalog;
        }

        @Override
        public void onClick(View v) {
            work.lastChapterPosition = ZERO;
            work.lastChapterOrder = catalogs.indexOf(catalog);
            work.lastChapterId = catalog.id;
            work.lasttime = ComYou.currentTimeSeconds();
            work.toReadType = 1;
            Intent intent = new Intent(context, ReadActivity.class);
            intent.putExtra("work", work);
            CollBookBean mCollBook  = new CollBookBean();
            mCollBook.setTitle(work.title);
            mCollBook.set_id(work.wid+"");
            intent.putExtra(Cods.EXTRA_COLL_BOOK, mCollBook);
            startActivity(intent);
        }
    }
}
