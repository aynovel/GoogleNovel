package life.forever.cf.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.entry.Comment;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.weight.viewtext.FixedClickableSpan;
import life.forever.cf.publics.weight.viewtext.FixedTextView;
import life.forever.cf.publics.weight.viewtext.TextViewUtil;

import java.util.List;


public class ReplyAdapter extends BaseAdapter implements Constant {

    private final Context context;
    private final List<Comment> comments;

    ReplyAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        return Math.min(TWO, comments.size());
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return ZERO;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Comment comment = comments.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment_reply, parent, FALSE);
        final FixedTextView content = view.findViewById(R.id.content);
        TextView date = view.findViewById(R.id.date);
        content.setNeedForceEventToParent(true);
        content.setDefaultMovementMethod();
        SpannableString nameSpan = new SpannableString(comment.nickname + ":");
        nameSpan.setSpan(new NameClickable(), ZERO, nameSpan.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        final SpannableStringBuilder ssb = new SpannableStringBuilder(nameSpan);
        if (comment.relateId == comment.pid) { // 一级回复
            ssb.append(comment.content);
        } else {
            ssb.append(context.getString(R.string.reply))
                    .append(comment.toName)
                    .append(":")
                    .append(comment.content);
        }
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                content.setText(TextViewUtil.ellipsize(ssb, content, ONE));
                content.getViewTreeObserver().removeOnPreDrawListener(this);
                return TRUE;
            }
        });
        date.setText(ComYou.formatTime(comment.addtime));
        return view;
    }

    private class NameClickable extends FixedClickableSpan {

        NameClickable() {
            super(0xFF4399FA, 0xFF4399FA, Color.TRANSPARENT, Color.TRANSPARENT);
        }

        @Override
        public void onSpanClick(View widget) {

        }
    }

}
