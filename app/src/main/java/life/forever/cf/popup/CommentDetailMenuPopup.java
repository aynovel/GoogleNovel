package life.forever.cf.popup;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.Comment;
import life.forever.cf.entry.Work;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.activtiy.CommentReportActivity;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.BaseFragment;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.weight.viewtext.MagnetTextView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CommentDetailMenuPopup extends PopupWindow implements Constant {

    private final BaseActivity activity;
    private BaseFragment gragment;
    private final Comment comment;
    private final Work work;
    @BindView(R.id.delete)
    View delete;

    @BindView(R.id.mt_placed_top)
    MagnetTextView mt_placed_top;

    @BindView(R.id.mt_banned)
    MagnetTextView mt_banned;

    public CommentDetailMenuPopup(BaseActivity activity, Comment comment, Work work) {
        this.activity = activity;
        this.gragment = gragment;
        this.comment = comment;
        this.work = work;

        View root = LayoutInflater.from(activity).inflate(R.layout.layout_comment_detail_menu_popup, null, FALSE);
        setContentView(root);
        ButterKnife.bind(this, root);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_slide_alpha_bottom_style);
        setFocusable(TRUE);
        setOutsideTouchable(FALSE);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        if (comment.is_author_user == 1){
            mt_placed_top.setVisibility(View.VISIBLE);
            mt_banned.setVisibility(View.VISIBLE);
            if (comment.isTop == 1) {
                mt_placed_top.setText(activity.getString(R.string.cancel_mt_placed_top));
            }else{
                mt_placed_top.setText(activity.getString(R.string.mt_placed_top));

            }
            if (comment.isBanUser == 1) {
                mt_banned.setText(activity.getString(R.string.cancel_mt_banned));
            }else{
                mt_banned.setText(activity.getString(R.string.mt_banned));
            }
        }else {
            mt_placed_top.setVisibility(View.GONE);
            mt_banned.setVisibility(View.GONE);
        }

        delete.setVisibility(comment.uid == PlotRead.getAppUser().uid || comment.is_author_user == 1 ? View.VISIBLE : View.GONE);
    }

    public void show(View parent) {
        ComYou.setWindowAlpha(activity, DOT_FIVE);
        showAtLocation(parent, Gravity.BOTTOM, ZERO, ZERO);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        ComYou.setWindowAlpha(activity, ONE);
    }

    @OnClick(R.id.report)
    void onReportClick() {
        Intent intent = new Intent(activity, CommentReportActivity.class);
        intent.putExtra("wid", comment.wid);
        intent.putExtra("cid", comment.id);
        activity.startActivity(intent);
        dismiss();
    }

    @OnClick(R.id.delete)
    void onDeleteClick() {
        setComment(FIVE,activity.getString(R.string.delete_success));
        dismiss();
    }

    @OnClick(R.id.mt_placed_top)
    void onWeChatClick() {
        if (comment.isTop == ONE){
            setComment(TWO,activity.getString(R.string.mt_placed_top_success));
        }else{
            setComment(ONE,activity.getString(R.string.cancel_mt_placed_top_success));
        }

    }

    @OnClick(R.id.mt_banned)
    void onWeChatCircleClick() {
        if (comment.isBanUser == ONE){
            workCommentBan(TWO,activity.getString(R.string.cancel_mt_banned_success));
        }else{
            workCommentBan(ONE,activity.getString(R.string.mt_banned_success));
        }

    }

    /**
     *  @param type  1:置顶 2:取消置顶 3:加精 4:取消加精 5:删除 6:取消删除
     */
    private void setComment(int type,String successHint) {
        activity.showLoading(activity.getString(R.string.deleting));
        NetRequest.workCommentManage(comment.wid, comment.id, type, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                activity.dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {

                        switch (type){
                            case ONE:
                                comment.isTop = 1;
                                mt_placed_top.setText(activity.getString(R.string.cancel_mt_placed_top));
                                commentRefresh();
                                break;
                            case TWO:
                                mt_placed_top.setText(activity.getString(R.string.mt_placed_top));
                                comment.isTop = 2;
                                commentRefresh();
                                break;

                            case FIVE:
                                // 发通知
                                Message message = Message.obtain();
                                message.what = BUS_COMMENT_DELETE;
                                message.obj = comment;
                                EventBus.getDefault().post(message);
                                break;

                        }
                        PlotRead.toast(PlotRead.SUCCESS, successHint);
                        if(type == FIVE){
                            activity.onBackPressed();
                        }
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, activity.getString(R.string.no_internet));
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                activity.dismissLoading();
                PlotRead.toast(PlotRead.FAIL, activity.getString(R.string.no_internet));
            }
        });
    }

    /**
     *  刷新评论
     */
    private void commentRefresh(){
        Message messagePlacedTop = Message.obtain();
        messagePlacedTop.what = BUS_REWARD_SUCCESS;
        messagePlacedTop.obj = comment.wid;
        EventBus.getDefault().post(messagePlacedTop);
    }

    /**
     *  @param type        1禁言   2取消禁言
     */
    private void workCommentBan(int type,String successHint) {
        activity.showLoading(activity.getString(R.string.deleting));
        NetRequest.workCommentBan(comment.wid, comment.id, type, new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                activity.dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {

                        switch (type){
                            case ONE:
                                comment.isBanUser = 1;
                                mt_banned.setText(activity.getString(R.string.cancel_mt_banned));
                                break;
                            case TWO:
                                comment.isBanUser = 2;
                                mt_banned.setText(activity.getString(R.string.mt_banned));
                                break;

                            case FIVE:
                                // 发通知
                                Message message = Message.obtain();
                                message.what = BUS_COMMENT_DELETE;
                                message.obj = comment;
                                EventBus.getDefault().post(message);
                                break;

                        }
                        PlotRead.toast(PlotRead.SUCCESS, successHint);
                        if(type == FIVE){
                            activity.onBackPressed();
                        }
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, activity.getString(R.string.no_internet));
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                activity.dismissLoading();
                PlotRead.toast(PlotRead.FAIL, activity.getString(R.string.no_internet));
            }
        });
    }

}
