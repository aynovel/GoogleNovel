package life.forever.cf.activtiy;

import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.entry.Comment;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.fresh.LoadFooterView;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.fresh.weight.PullRefreshLayout;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.DisplayUtil;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.ScreenUtil;
import life.forever.cf.publics.weight.LevelView;
import life.forever.cf.publics.weight.RadiusImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShowCommentPop extends PopupWindow implements Constant {

    @BindView(R.id.refreshLayout)
    PullRefreshLayout mRefreshLayout;
    @BindView(R.id.loadFooter)
    LoadFooterView mLoadFooter;
    @BindView(R.id.listView)
    ListView mListView;

    private final ReadActivity activity;
    private final List<Comment> comments;
    private final ChapterCommentAdapter commentAdapter;

    ShowCommentPop(ReadActivity activity, List<Comment> comments) {
        this.activity = activity;
        this.comments = comments;
        View root = LayoutInflater.from(activity).inflate(R.layout.layout_show_chapter_comment_pop, null);
        setContentView(root);
        ButterKnife.bind(this, root);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(TRUE);
        setFocusable(TRUE);
        setBackgroundDrawable(new ColorDrawable());

        mRefreshLayout.setHasFooter(FALSE);
        mLoadFooter.setOnLoadListener(onLoadListener);
        commentAdapter = new ChapterCommentAdapter();
        mListView.setAdapter(commentAdapter);
        setListViewHeight();
    }

    private final BaseFooterView.OnLoadListener onLoadListener = new BaseFooterView.OnLoadListener() {

        @Override
        public void onLoad(BaseFooterView baseFooterView) {
//            activity.getChapterComments();
        }
    };

//    @OnClick(R.id.write)
//    void onWriteClick() {
//        dismiss();
//        activity.writeChapterComment();
//    }

    public void show(View parent) {
        ComYou.setWindowAlpha(activity, DOT_FIVE);
        showAtLocation(parent, Gravity.BOTTOM, ZERO, DisplayUtil.dp2px(PlotRead.getApplication(), THIRTY));
    }

    @Override
    public void dismiss() {
        ComYou.setWindowAlpha(activity, ONE);
        super.dismiss();
    }

    @Override
    public void update() {
        commentAdapter.notifyDataSetChanged();
        setListViewHeight();
    }

    void setLoadable(boolean loadable) {
        mRefreshLayout.setHasFooter(loadable);
    }

    public PullRefreshLayout getRefreshLayout() {
        return mRefreshLayout;
    }

    private void setListViewHeight() {
        int height;
        if (comments.size() == ZERO) {
            height = ZERO;
        } else if (comments.size() == ONE) {
            height = getListViewHeight(mListView, ONE);
        } else {
            height = getListViewHeight(mListView, TWO);
        }
        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) mListView.getLayoutParams();
        p.height = height;
        mListView.setLayoutParams(p);
    }

    /**
     * 动态计算listView的高度
     *
     * @param listView
     * @param itemCount
     * @return
     */
    private int getListViewHeight(ListView listView, int itemCount) {
        int totalHeight = ZERO;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return totalHeight;
        }
        int height = ZERO;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(ScreenUtil.getScreenWidth(activity), View.MeasureSpec.AT_MOST);
        for (int i = ZERO; i < itemCount; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            height += listItem.getMeasuredHeight();
        }
        totalHeight = height + (listView.getDividerHeight() * (itemCount - 1));
        return totalHeight;
    }

    private class ChapterCommentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(activity).inflate(R.layout.item_chapter_comment, parent, FALSE);
                viewHolder.head = convertView.findViewById(R.id.head);
                viewHolder.name = convertView.findViewById(R.id.name);
                viewHolder.level = convertView.findViewById(R.id.level);
                viewHolder.fansLevel = convertView.findViewById(R.id.fansLevel);
                viewHolder.content = convertView.findViewById(R.id.content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Comment comment = comments.get(position);
            GlideUtil.load(activity, comment.head, R.drawable.default_user_logo, viewHolder.head);
            viewHolder.name.setText(comment.nickname);
            viewHolder.level.setLevel(comment.level);
            viewHolder.fansLevel.setFansLevel(comment.fansLevel);
            viewHolder.fansLevel.setText(comment.fansName);
            viewHolder.content.setText(comment.content);

            return convertView;
        }
    }

    private class ViewHolder {

        public RadiusImageView head;
        public TextView name;
        public LevelView level;
        public LevelView fansLevel;
        public TextView content;
    }

}
