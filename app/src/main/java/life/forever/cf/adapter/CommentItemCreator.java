package life.forever.cf.adapter;

import static life.forever.cf.activtiy.Cods.EXTRA_COLL_BOOK;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.Comment;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.activtiy.WorkCommentDetailActivity;
import life.forever.cf.sql.NiceRatingBar;
import life.forever.cf.adapter.person.landing.LoginActivity;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.RadiusImageView;
import life.forever.cf.publics.weight.viewtext.FixedClickableSpan;
import life.forever.cf.publics.weight.viewtext.FixedTextView;
import life.forever.cf.publics.weight.viewtext.TextViewUtil;
import life.forever.cf.activtiy.ReadActivity;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;


public class CommentItemCreator implements Constant {
    public FirebaseAnalytics mFirebaseAnalytics;

    private final Activity context;
    private final ViewGroup parent;
    private TextView like;
    private ImageView iv_likeCount;
    @SuppressLint("MissingPermission")
    public CommentItemCreator(Activity baseActivity, LinearLayout parent) {
        this.context = baseActivity;
        this.parent = parent;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public View createView(final Comment comment) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_work_comment, parent, FALSE);
        RadiusImageView head = view.findViewById(R.id.head);
        TextView name = view.findViewById(R.id.name);
        TextView date = view.findViewById(R.id.date);
        Button iv_start = view.findViewById(R.id.iv_start);
        TextView reply = view.findViewById(R.id.replyCount);
        RelativeLayout ll_likeCount = view.findViewById(R.id.ll_likeCount);
        like = view.findViewById(R.id.likeCount);
        iv_likeCount = view.findViewById(R.id.iv_likeCount);
        NiceRatingBar score = view.findViewById(R.id.score);
        TextView reward = view.findViewById(R.id.reward);
        final FixedTextView content = view.findViewById(R.id.content);
        content.setDefaultMovementMethod();
        content.setNeedForceEventToParent(TRUE);

        GlideUtil.load(context, comment.head, R.drawable.default_user_logo, head);
        name.setText(comment.nickname);


        if(comment.is_author_comment == 1){
            iv_start.setVisibility(View.VISIBLE);
        }else{
            iv_start.setVisibility(View.GONE);
        }

        reply.setText(comment.replyCount > ZERO ? String.valueOf(comment.replyCount) : ZERO+"");
        like.setText(comment.likeCount > ZERO ? String.valueOf(comment.likeCount) : ZERO+"");
        if (comment.isLike == ONE) {
            iv_likeCount.setImageResource(R.drawable.praised);
            like.setTextColor(context.getResources().getColor(R.color.theme_color));
        }else{
            iv_likeCount.setImageResource(R.drawable.praise);
            like.setTextColor(context.getResources().getColor(R.color.color_999999));
        }

//        reply.setText(comment.replyCount > ZERO ? String.valueOf(comment.replyCount) : context.getString(R.string.reply));
//        like.setText(comment.likeCount > ZERO ? String.valueOf(comment.likeCount) : context.getString(R.string.like));
//        like.setCompoundDrawablesWithIntrinsicBounds(comment.isLike == ONE ? R.drawable.aiye_like_icon : R.drawable.aiye_unlike_icon, ZERO, ZERO, ZERO);
        date.setText(ComYou.formatTime(comment.addtime));
        score.setRight(comment.score);
        if (comment.contentType == TWO) {
            reward.setVisibility(View.VISIBLE);
            score.setVisibility(View.GONE);
        } else if (comment.contentType == ONE) {
            reward.setVisibility(View.GONE);
            score.setVisibility(comment.score > ZERO ? View.VISIBLE : View.GONE);
        } else {
            reward.setVisibility(View.GONE);
            score.setVisibility(View.GONE);
        }
        // 评论内容
        final SpannableStringBuilder ssb = new SpannableStringBuilder();
        CharSequence result = TextViewUtil.replaceSpan(comment.content);
        if (comment.contentType == ONE) {
            if (comment.cid == ZERO) {
                ssb.append(result);
            } else {
                ssb.append(context.getString(R.string.reply));
                SpannableString titleSpan = new SpannableString("【" + comment.title + "】");
                titleSpan.setSpan(new TitleClickSpan(comment.wid, comment.cid), ZERO, titleSpan.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                ssb.append(titleSpan).append("：").append(result);
            }
        }
        if (comment.contentType == TWO) {
            reward.setText(comment.title);
            ssb.append(result);
        }
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                content.setText(TextViewUtil.ellipsize(ssb, content, THREE));
                content.getViewTreeObserver().removeOnPreDrawListener(this);
                return TRUE;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DeepLinkUtil.addPermanent(context,"event_details_read_comment"
                        ,"详情页","详情页评论","","","","","","");

                Intent intent = new Intent(context, WorkCommentDetailActivity.class);
                intent.putExtra("wid", comment.wid);
                intent.putExtra("id", comment.id);
                context.startActivity(intent);
            }
        });
        ll_likeCount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doLike(comment);
            }
        });

        return view;
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
                            like.setText(comment.likeCount > ZERO ? String.valueOf(comment.likeCount) : ZERO+"");
                            like.setTextColor(context.getResources().getColor(R.color.color_999999));
                            iv_likeCount.setImageResource(R.drawable.praise);
                            PlotRead.toast(PlotRead.SUCCESS, context.getString(R.string.cancel_success));
                        } else {
                            comment.isLike = ONE;
                            comment.likeCount++;
                            like.setTextColor(context.getResources().getColor(R.color.theme_color));
                            iv_likeCount.setImageResource(R.drawable.praised);
                            like.setText(comment.likeCount > ZERO ? String.valueOf(comment.likeCount) : ZERO+"");
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
            intent.setClass(context, ReadActivity.class);
            intent.putExtra("work", work);
            CollBookBean mCollBook  = new CollBookBean();
            mCollBook.setTitle(work.title);
            mCollBook.set_id(work.wid+"");
            intent.putExtra(EXTRA_COLL_BOOK, mCollBook);
            context.startActivity(intent);
        }
    }
}
