package life.forever.cf.publics.weight.poputil;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import life.forever.cf.R;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.ComYou;
import life.forever.cf.publics.tool.DeepLinkUtil;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class SharePopup extends PopupWindow implements Constant {

    private final Activity activity;
    private CallbackManager callbackManager;
    public SharePopup(Activity activity) {
        this.activity = activity;
        View root = LayoutInflater.from(activity).inflate(R.layout.layout_share_popup, null);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setAnimationStyle(R.style.popup_slide_alpha_bottom_style);
        setOutsideTouchable(false);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        setContentView(root);
        ButterKnife.bind(this, root);
    }


    public void show(View parent) {
        showAtLocation(parent, Gravity.BOTTOM, ZERO, ZERO);
        ComYou.setWindowAlpha(activity, DOT_FIVE);
    }

    @Override
    public void dismiss() {
        ComYou.setWindowAlpha(activity, ONE);
        super.dismiss();
    }

    /**
     * 设置成功的监听
     *
     * @param onSuccessShareListener
     */
    public void setOnSuccessShareListener(OnSuccessShareListener onSuccessShareListener) {

    }

    @OnClick(R.id.com_facebook_share)
    void onFacebookClick() {
        if (callbackManager == null){
            callbackManager = CallbackManager.Factory.create();
        }
        ShareLinkContent content = new ShareLinkContent.Builder()
//        market://details?id=            http://play.google.com/store/apps/details?id
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.bytedance.club&hl=en_US"))

                .build();
        // 对话框
        ShareDialog shareDialog = new ShareDialog(activity);
        // 分享回调
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.e("TAG", "onSuccess");
                DeepLinkUtil.addPermanent(activity,"event_share_facebook","阅读页","点击facebook分享",result.getPostId(),"","","","","");

            }

            @Override
            public void onCancel() {
                Log.e("TAG", "onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("TAG", "onError" + error.toString());
            }
        });
        shareDialog.show(content);
    }

    @OnClick(R.id.com_zalo_share)
    void onWeChatCircleClick() {
    }





    public interface OnSuccessShareListener {
        void onSuccess();
    }

}
