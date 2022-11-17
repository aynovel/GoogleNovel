package life.forever.cf.publics.tool;

import static life.forever.cf.activtiy.Cods.EXTRA_COLL_BOOK;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.entry.SignBean;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.bookcase.ShelfUtil;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.activtiy.ReadActivity;
import life.forever.cf.publics.Constant;


public class TaskSignDialog extends Dialog implements View.OnClickListener {

    private final Context mContext;
    private final SignBean.ResultData mResultData;
    int wids;

    public TaskSignDialog(final Context context, SignBean.ResultData signbean) {
        super(context, R.style.Theme_Update_Dialog);
        mContext = context;
        mResultData = signbean;
        this.setContentView(R.layout.task_sign_dialog);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        WindowManager.LayoutParams params;
        if (window != null) {
            params = window.getAttributes();
            Resources resources = context.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            if (params != null) {
                params.width = (int) (dm.widthPixels * 0.98);
            }
            window.setAttributes(params);
        }

        //收入
        TextView tvNumber =  findViewById(R.id.tvNumber);

        ImageView mRoundImageView = findViewById(R.id.cover);
        TextView mBookInfo = findViewById(R.id.book_info);
        findViewById(R.id.book_read).setOnClickListener(this);
        findViewById(R.id.img_sign_cancel).setOnClickListener(this);

        SignBean.ResultData.Info.Recommend.Rec_list bean = signbean.info.recommend.rec_list.get(0);
//        String imgurl = bean.h_url;
//        String bookinfo = bean.description;

        GlideUtil.picCache(context, bean.h_url,bean.wid + "sign",R.drawable.default_work_cover, mRoundImageView);

//        String cover = PlotRead.getConfig().getString(bean.wid + "sign", "");
//        if (TextUtils.isEmpty(cover)) {
////            SharedPreferencesUtil.putString(PlotRead.getConfig(), bean.wid+"sign",bean.h_url);
//            GlideUtil.recommentLoad(context, bean.wid + "sign", bean.h_url, bean.h_url, R.drawable.default_work_cover, mRoundImageView);
//        } else {
//            GlideUtil.recommentLoad(context, "", cover, bean.h_url, R.drawable.default_work_cover, mRoundImageView);
//        }
        mBookInfo.setText(bean.description);

        String todaytime = ComYou.timeFormat(ComYou.currentTimeSeconds(), Constant.DATE_FORMATTER_8);

        for (int i = 0;i< signbean.info.sign.size();i++){
            if (signbean.info.sign.get(i).date.equals(todaytime)){
                tvNumber.setText("+"+signbean.info.sign_price.get(i));

            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_sign_cancel:
                this.dismiss();
                break;
            case R.id.book_read:
                this.dismiss();
                if (mResultData != null && mResultData.info.recommend.rec_list.size() > 0) {
                    Intent intent = new Intent();
                    /*
                     * advertise_type: 广告类型 1：作品 2：内部链接 3：外部链接
                     * readflag: 0：作品信息 1：阅读
                     */
                    String mTid = mResultData.info.recommend.rec_list.get(0).rec_id;
                    String advertise_type = mResultData.info.recommend.rec_list.get(0).advertise_type;
                    if ("1".equals(advertise_type)) {
                        String readflag = mResultData.info.recommend.rec_list.get(0).advertise_data.readflag;
                        wids = Integer.parseInt(mResultData.info.recommend.rec_list.get(0).advertise_data.wid);
                        DeepLinkUtil.addPermanent(mContext, "event_sign_recommend", "签到", "签到推荐位点击" + mTid, "", wids + "", "", "", "", "");
                        if ("1".equals(readflag)) {
                            Work work = new Work();
                            work.wid = wids;
                            if (null != mResultData && ShelfUtil.existRecord(wids)) {
                                Work record = ShelfUtil.queryRecord(wids);
                                if (record != null) {
                                    work.lasttime = record.lasttime;
                                    work.lastChapterId = record.lastChapterId;
                                    work.lastChapterOrder = record.lastChapterOrder;
                                    work.lastChapterPosition = record.lastChapterPosition;
                                }
                            }
                            work.toReadType = 2;
                            intent.setClass(mContext, ReadActivity.class);
                            intent.putExtra("work", work);
                            CollBookBean mCollBook  = new CollBookBean();
                            mCollBook.setTitle(work.title);
                            mCollBook.set_id(work.wid+"");
                            intent.putExtra(EXTRA_COLL_BOOK, mCollBook);
                        } else {
                            intent.setClass(mContext, WorkDetailActivity.class);
                            intent.putExtra("wid", wids);
                            intent.putExtra("recid", 0);
                        }
                        mContext.startActivity(intent);
                    }
                }
                break;
        }
    }
}
