package life.forever.cf.adapter;

import static life.forever.cf.activtiy.Cods.EXTRA_COLL_BOOK;

import android.content.Intent;
import android.graphics.Color;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.sql.NoneViewHolder;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.Comment;
import life.forever.cf.entry.CommentClassify;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.activtiy.WorkCommentDetailActivity;
import life.forever.cf.sql.NiceRatingBar;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.OnItemClickListener;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.WrapListView;
import life.forever.cf.publics.weight.poputil.ManagePopup;
import life.forever.cf.publics.weight.viewtext.FixedClickableSpan;
import life.forever.cf.publics.weight.viewtext.FixedTextView;
import life.forever.cf.publics.weight.viewtext.TextViewUtil;
import life.forever.cf.activtiy.ReadActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CommentGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Constant {

    private final int CLASSIFY = ZERO;
    private final int COMMENT = ONE;
    private final int NONE = TWO;
    private final OnItemClickListener replyClick;
    private final BaseActivity context;
    private final List<CommentClassify> classifies;
    private final List<Integer> positions = new ArrayList<>();

    private ManagePopup managePopup;
    private final View.OnClickListener mHottestClick;
    private final View.OnClickListener mLatestClick;

    int showIndex = 1;
    public CommentGroupAdapter(BaseActivity context, List<CommentClassify> classifies, OnItemClickListener replyClick, View.OnClickListener hottestClick , View.OnClickListener latestClick ) {
        this.context = context;
        this.classifies = classifies;
        getClassifyTypePositions();
        this.replyClick = replyClick;
        mHottestClick = hottestClick;
        mLatestClick = latestClick;
    }

    /**
     * 刷新界面数据，不能使用 {@link RecyclerView.Adapter#notifyDataSetChanged()}
     */
    public void update() {
        getClassifyTypePositions();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == NONE) {
            return new NoneViewHolder(context, parent);
        }
        if (viewType == CLASSIFY) {
            return new CommentClassifyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_work_comment_classify, parent, FALSE));
        }
        return new WorkCommentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_work_comment, parent, FALSE));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoneViewHolder) {
            NoneViewHolder viewHolder = (NoneViewHolder) holder;
            viewHolder.description.setText(context.getString(R.string.looking_wonderful_comments));
            return;
        }
        if (holder instanceof WorkCommentViewHolder) {
            final WorkCommentViewHolder viewHolder = (WorkCommentViewHolder) holder;
            final Comment comment;
            comment = classifies.get(ZERO).comments.get(position - ONE);
            GlideUtil.load(context, comment.head, R.drawable.default_user_logo, viewHolder.head);
            viewHolder.name.setText(comment.nickname);
            viewHolder.replyCount.setVisibility(View.GONE);
            if(comment.is_author_comment == 1){
                viewHolder.iv_start.setVisibility(View.VISIBLE);
            }else{
                viewHolder.iv_start.setVisibility(View.GONE);
            }
            viewHolder.likeCount.setText( String.valueOf(comment.likeCount));

//            viewHolder.like.setText(comment.likeCount > ZERO ? String.valueOf(comment.likeCount) : context.getString(R.string.like));
            if (comment.isLike == ONE) {
                viewHolder.iv_likeCount.setImageResource(R.drawable.praised);
                viewHolder.likeCount.setTextColor(context.getResources().getColor(R.color.theme_color));
            }else{
                viewHolder.iv_likeCount.setImageResource(R.drawable.praise);
                viewHolder.likeCount.setTextColor(context.getResources().getColor(R.color.color_999999));
            }
            viewHolder.date.setText(ComYou.formatTime(comment.addtime));
            viewHolder.score.setRating(comment.score);
            if (comment.contentType == TWO) {
                viewHolder.reward.setVisibility(View.VISIBLE);
                viewHolder.score.setVisibility(View.GONE);
            } else if (comment.contentType == ONE) {
                viewHolder.reward.setVisibility(View.GONE);
                viewHolder.score.setVisibility(comment.score > ZERO ? View.VISIBLE : View.GONE);
            } else {
                viewHolder.reward.setVisibility(View.GONE);
                viewHolder.score.setVisibility(View.GONE);
            }
            if (comment.replays.size() > ZERO) {
                viewHolder.replayLayout.setVisibility(View.VISIBLE);
                viewHolder.replyList.setAdapter(new ReplyAdapter(context, comment.replays));
                viewHolder.checkAll.setText(String.format(Locale.getDefault(), context.getString(R.string.look_all), comment.replyCount));
                viewHolder.checkAll.setVisibility(comment.replyCount > TWO ? View.VISIBLE : View.GONE);
            } else {
                viewHolder.replayLayout.setVisibility(View.GONE);
            }
            final SpannableStringBuilder content = new SpannableStringBuilder();
            CharSequence result = TextViewUtil.replaceSpan(comment.content);
            if (comment.contentType == ONE) {
                if (comment.cid == ZERO) {
                    content.append(result);
                } else {
                    content.append(context.getString(R.string.poking_fun));
                    SpannableString titleSpan = new SpannableString("【" + comment.title + "】");
                    titleSpan.setSpan(new TitleClickSpan(comment.wid, comment.cid), ZERO, titleSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    content.append(titleSpan).append("：").append(result);
                }
            }
            if (comment.contentType == TWO) {
                viewHolder.reward.setText(comment.title);
                content.append(result);
            }
            viewHolder.content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    viewHolder.content.setText(TextViewUtil.ellipsize(content, viewHolder.content, THREE));
                    viewHolder.content.getViewTreeObserver().removeOnPreDrawListener(this);
                    return TRUE;
                }
            });
            viewHolder.replyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(context, WorkCommentDetailActivity.class);
                    intent.putExtra("wid", comment.wid);
                    intent.putExtra("id", comment.id);
                    context.startActivity(intent);
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, WorkCommentDetailActivity.class);
                    intent.putExtra("wid", comment.wid);
                    intent.putExtra("id", comment.id);
                    context.startActivity(intent);
                }
            });
            viewHolder.ll_likeCount.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (PlotRead.getAppUser().login()) {
                        doLike(comment);
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    }
                }
            });
            viewHolder.ll_replyCount.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (replyClick != null) {
                        replyClick.onItemClick(holder);
                    }
                }
            });

        }
        if (holder instanceof CommentClassifyViewHolder) {
            CommentClassifyViewHolder viewHolder = (CommentClassifyViewHolder) holder;
            if (position == ZERO) {
                viewHolder.divider.setVisibility(View.GONE);
            } else {
                viewHolder.divider.setVisibility(View.VISIBLE);
            }
            viewHolder.choose.setVisibility(View.VISIBLE);
            viewHolder.iv_arrow.setVisibility(View.VISIBLE);
            viewHolder.choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChooseClick(viewHolder.choose);
                }
            });
            viewHolder.iv_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChooseClick(viewHolder.choose);
                }
            });
            viewHolder.classify.setText(classifies.get(ZERO).classify);
            viewHolder.count.setText(String.format(Locale.getDefault(), "（%d）", classifies.get(ZERO).count));
        }
    }

    @Override
    public int getItemCount() {
        if (positions.isEmpty()) {
            return ONE;
        }
        int count = ZERO;
//        // 精彩评论
//        CommentClassify goodClassify = classifies.get(ZERO);
//        if (goodClassify.comments.size() > ZERO) {
//            count += goodClassify.comments.size() + ONE;
//        }
        // 全部评论
        CommentClassify allClassify = classifies.get(ZERO);
        if (allClassify.comments.size() > ZERO) {
            count += allClassify.comments.size() + ONE;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (positions.isEmpty()) {
            return NONE;
        }
        if (positions.contains(position)) {
            return CLASSIFY;
        }
        return COMMENT;
    }

    /**
     * 获取评论分类item的位置
     */
    private void getClassifyTypePositions() {
        positions.clear();
        // 全部评论
        CommentClassify allClassify = classifies.get(ZERO);
        if (allClassify.comments.size() > ZERO) {
            positions.add(ZERO);
        }
//        // 精彩评论
//        CommentClassify goodClassify = classifies.get(ZERO);
//        if (goodClassify.comments.size() == ZERO) {
//
//        } else {
//            positions.add(ZERO);
//            // 全部评论
//            CommentClassify allClassify = classifies.get(ZERO);
//            if (allClassify.comments.size() > ZERO) {
//                positions.add(goodClassify.comments.size() + ONE);
//            }
//        }
    }

    class CommentClassifyViewHolder extends RecyclerView.ViewHolder {

        public View divider;
        public TextView classify;
        public TextView count;
        public TextView choose;
        public ImageView iv_arrow;

        CommentClassifyViewHolder(View itemView) {
            super(itemView);
            divider = itemView.findViewById(R.id.divider);
            classify = itemView.findViewById(R.id.classify);
            count = itemView.findViewById(R.id.count);
            choose = itemView.findViewById(R.id.choose);
            iv_arrow = itemView.findViewById(R.id.iv_arrow);
        }
    }

    class WorkCommentViewHolder extends RecyclerView.ViewHolder {

        public ImageView head;
        public TextView name;
        public Button iv_start;
        public TextView date;
        public RelativeLayout ll_replyCount;
        public TextView replyCount;


        public RelativeLayout ll_likeCount;
        public ImageView iv_likeCount;
        public TextView likeCount;
        public NiceRatingBar score;
        public TextView reward;
        public FixedTextView content;
        public View replayLayout;
        public WrapListView replyList;
        public TextView checkAll;

        WorkCommentViewHolder(View itemView) {
            super(itemView);
            head = itemView.findViewById(R.id.head);
            name = itemView.findViewById(R.id.name);
            iv_start = itemView.findViewById(R.id.iv_start);
            date = itemView.findViewById(R.id.date);
            replyCount = itemView.findViewById(R.id.replyCount);
            ll_replyCount = itemView.findViewById(R.id.ll_replyCount);
            likeCount = itemView.findViewById(R.id.likeCount);
            ll_likeCount = itemView.findViewById(R.id.ll_likeCount);
            iv_likeCount = itemView.findViewById(R.id.iv_likeCount);
            score = itemView.findViewById(R.id.score);
            reward = itemView.findViewById(R.id.reward);
            content = itemView.findViewById(R.id.content);
            replayLayout = itemView.findViewById(R.id.replyLayout);
            replyList = itemView.findViewById(R.id.replyList);
            checkAll = itemView.findViewById(R.id.checkAll);
            content.setNeedForceEventToParent(TRUE);
            content.setDefaultMovementMethod();
        }
    }

    private void doLike(final Comment comment) {
        if (!PlotRead.getAppUser().login()) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            return;
        }
        NetRequest.workCommentLike(comment.wid, comment.id, comment.isLike == ONE ? TWO : ONE, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        if (comment.isLike == ONE) {
                            comment.isLike = ZERO;
                            comment.likeCount--;
                            PlotRead.toast(PlotRead.SUCCESS, context.getString(R.string.cancel_success));
                        } else {
                            comment.isLike = ONE;
                            comment.likeCount++;
                            PlotRead.toast(PlotRead.SUCCESS, context.getString(R.string.give_like_success));
                        }
                        // 发通知
                        Message message = Message.obtain();
                        message.what = BUS_COMMENT_ADD_LIKE;
                        message.obj = comment;
                        EventBus.getDefault().post(message);
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, context.getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(context, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    private class TitleClickSpan extends FixedClickableSpan {

        private final int wid;
        private final int cid;

        TitleClickSpan(int wid, int cid) {
            super(THEME_COLOR, THEME_COLOR, Color.TRANSPARENT, Color.TRANSPARENT);
            this.wid = wid;
            this.cid = cid;
        }

        @Override
        public void onSpanClick(View widget) {
            Intent intent = new Intent(context, ReadActivity.class);
            Work work = new Work();
            work.wid = wid;
            work.lastChapterId = cid;
            work.toReadType = 1;
            intent.putExtra("work", work);
            CollBookBean mCollBook  = new CollBookBean();
            mCollBook.setTitle(work.title);
            mCollBook.set_id(work.wid+"");
            intent.putExtra(EXTRA_COLL_BOOK, mCollBook);
            context.startActivity(intent);
        }
    }

    /**
     *筛选最新最热
     */
    public void onChooseClick(TextView view) {
        if (managePopup != null && managePopup.isShowing()) {
            managePopup.dismiss();
        } else {
            if (managePopup == null) {
                managePopup = new ManagePopup(context, showIndex, new ManagePopup.OnItemClickListener() {
                    @Override
                    public void onItemClick(LinearLayout linearLayout, int position) {
                        showIndex = position;
                        managePopup.showIn(showIndex);
                        if (position == 1){
                            view.setText(context.getResources().getString(R.string.hottest));
                            mHottestClick.onClick(view);
                        }else{
                            view.setText(context.getResources().getString(R.string.newest));
                            mLatestClick.onClick(view);
                        }
                    }
                });
            }
            managePopup.show(view);
        }
    }
}
