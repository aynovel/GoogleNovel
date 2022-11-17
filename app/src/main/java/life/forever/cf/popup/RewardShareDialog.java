package life.forever.cf.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.entry.Work;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.GlideUtil;


public class RewardShareDialog implements Constant {

    public static void show(final Activity activity, final Work work, final int cid, String description) {
        View root = LayoutInflater.from(activity).inflate(R.layout.layout_reward_share_dialog, null, FALSE);
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        final AlertDialog dialog = adb.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView cover = window.findViewById(R.id.cover);
        TextView reward = window.findViewById(R.id.reward);
        GlideUtil.load(activity, work.cover, R.drawable.default_work_cover, cover);
        reward.setText(description);
        // 关闭
        window.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // 微信分享
        window.findViewById(R.id.wechat).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (UMShareAPI.get(activity).isInstall(activity, SHARE_MEDIA.WEIXIN)) {
//                    UMWeb umWeb = getUmWeb(activity, work, cid);
//                    ShareListener shareListener = new ShareListener(TWO, work.wid, umWeb.toUrl());
//                    new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN).withMedia(umWeb).setCallback(shareListener).share();
//                } else {
//                    PlotRead.toast(PlotRead.INFO, "您未安装微信！");
//                }
            }
        });
        // 朋友圈分享
        window.findViewById(R.id.wechatCircle).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (UMShareAPI.get(activity).isInstall(activity, SHARE_MEDIA.WEIXIN)) {
//                    UMWeb umWeb = getUmWeb(activity, work, cid);
//                    ShareListener shareListener = new ShareListener(THREE, work.wid, umWeb.toUrl());
//                    new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).withMedia(umWeb).setCallback(shareListener).share();
//                } else {
//                    PlotRead.toast(PlotRead.INFO, "您未安装微信！");
//                }
            }
        });
        // QQ分享
        window.findViewById(R.id.qq).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (UMShareAPI.get(activity).isInstall(activity, SHARE_MEDIA.QQ)) {
//                    UMWeb umWeb = getUmWeb(activity, work, cid);
//                    ShareListener shareListener = new ShareListener(FIVE, work.wid, umWeb.toUrl());
//                    new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ).withMedia(umWeb).setCallback(shareListener).share();
//                } else {
//                    PlotRead.toast(PlotRead.INFO, "您未安装QQ！");
//                }
            }
        });
        // 空间分享
        window.findViewById(R.id.qzone).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (UMShareAPI.get(activity).isInstall(activity, SHARE_MEDIA.QQ)) {
//                    UMWeb umWeb = getUmWeb(activity, work, cid);
//                    ShareListener shareListener = new ShareListener(FOUR, work.wid, umWeb.toUrl());
//                    new ShareAction(activity).setPlatform(SHARE_MEDIA.QZONE).withMedia(umWeb).setCallback(shareListener).share();
//                } else {
//                    PlotRead.toast(PlotRead.INFO, "您未安装QQ！");
//                }
            }
        });
        // 微博分享
        window.findViewById(R.id.sina).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                UMWeb umWeb = getUmWeb(activity, work, cid);
//                ShareListener shareListener = new ShareListener(ONE, work.wid, umWeb.toUrl());
//                new ShareAction(activity).setPlatform(SHARE_MEDIA.SINA).withMedia(umWeb).setCallback(shareListener).share();
            }
        });

    }

//    private static UMWeb getUmWeb(Activity activity, Work work, int cid) {
//        String url = API.H5_INDEX + String.format(Locale.getDefault(), API.SHARE_WORK_READ, work.wid, cid);
//        UMWeb umWeb = new UMWeb(url);
//        umWeb.setTitle(String.format(Locale.getDefault(), "%s送你%d书豆，快来领取！", activity.getString(R.string.app_name), FIFTY));
//        umWeb.setThumb(new UMImage(activity, work.cover));
//        umWeb.setDescription("这本书超级精彩，一起来支持吧！");
//        return umWeb;
//    }

//    private static class ShareListener implements UMShareListener {
//
//        private int channel;
//        private int wid;
//        private String url;
//
//        ShareListener(int channel, int wid, String url) {
//            this.channel = channel;
//            this.wid = wid;
//            this.url = url;
//        }
//
//        @Override
//        public void onStart(SHARE_MEDIA share_media) {
//
//        }
//
//        @Override
//        public void onResult(SHARE_MEDIA share_media) {
//            PlotRead.toast(PlotRead.SUCCESS, "分享成功");
//            NetRequest.shareUpload(wid, ShareType.READ_ACTIVITY, channel, url, null);
//        }
//
//        @Override
//        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//            PlotRead.toast(PlotRead.FAIL, "分享失败");
//        }
//
//        @Override
//        public void onCancel(SHARE_MEDIA share_media) {
//            PlotRead.toast(PlotRead.INFO, "分享取消");
//        }
//    }

}
