package life.forever.cf.publics.tool;

import static life.forever.cf.activtiy.Cods.EXTRA_COLL_BOOK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import life.forever.cf.activtiy.WerActivity;
import life.forever.cf.entry.DiscoverBean;
import life.forever.cf.entry.InboxBean;
import life.forever.cf.entry.RecList;
import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.activtiy.ReadActivity;

import java.util.List;

public class CustomIntent {
    static String mBid = "";

    public static void intent(Context mContext, List<DiscoverBean.ResultData.list> mList, int position) {
        if (mList != null && mList.size() > 0) {
            Intent intent = new Intent();

            String advertise_type = mList.get(position).advertise_type;
            if ("1".equals(advertise_type)) {
                String readflag = mList.get(position).advertise_data.readflag;
                int wids = Integer.parseInt(mList.get(position).advertise_data.wid);
                if ("1".equals(readflag)) {
                    Work work = new Work();
                    work.wid = wids;
                    intent.setClass(mContext, ReadActivity.class);
                    intent.putExtra("work", work);
                    CollBookBean mCollBook  = new CollBookBean();
                    mCollBook.setTitle(work.title);
                    mCollBook.set_id(work.wid+"");
                    intent.putExtra(EXTRA_COLL_BOOK, mCollBook);
                } else {
                    /*int recids = Integer.parseInt(mList.get(position).advertise_data.rec_id);*/
                    intent.setClass(mContext, WorkDetailActivity.class);
                    intent.putExtra("wid", wids);
                    intent.putExtra("recid", 0);
                }
                mContext.startActivity(intent);
            } else if ("2".equals(advertise_type)) {
                String ht = mList.get(position).advertise_data.ht;
                String path = mList.get(position).advertise_data.path;
                String ps = mList.get(position).advertise_data.ps;
                String is = mList.get(position).advertise_data.is;
                String su = mList.get(position).advertise_data.su;
                String st = mList.get(position).advertise_data.st;
                String ifreash = mList.get(position).advertise_data.ifreash;
                intent.setClass(mContext, WerActivity.class);

                intent.putExtra("index", ht);
                intent.putExtra("path", path);
                intent.putExtra("pagefresh", ps);
                intent.putExtra("share", is);
                intent.putExtra("shareUrl", su);
                intent.putExtra("shareType", st);
                intent.putExtra("sharefresh", ifreash);
                mContext.startActivity(intent);
            } else if ("3".equals(advertise_type)) {
                String url = mList.get(position).advertise_data.url;
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            }
        }
    }

    public static void intentinbox(Context mContext, List<InboxBean.ResultData.Lists.Rec_list> mList, int position) {
        Intent intent = new Intent();
        /*
         * advertise_type: 广告类型 1：作品 2：内部链接 3：外部链接
         * readflag: 0：作品信息 1：阅读
         */
        String advertise_type = mList.get(position).advertise_type;
        if ("1".equals(advertise_type)) {
            String readflag = mList.get(position).advertise_data.readflag;
            int wids = Integer.parseInt(mList.get(position).advertise_data.wid);
            if ("1".equals(readflag)) {
                Work work = new Work();
                work.wid = wids;
                intent.setClass(mContext, ReadActivity.class);
                intent.putExtra("work", work);
                CollBookBean mCollBook  = new CollBookBean();
                mCollBook.setTitle(work.title);
                mCollBook.set_id(work.wid+"");
                intent.putExtra(EXTRA_COLL_BOOK, mCollBook);
            } else {
                /*int recids = Integer.parseInt(mList.get(position).advertise_data.rec_id);*/
                intent.setClass(mContext, WorkDetailActivity.class);
                intent.putExtra("wid", wids);
                intent.putExtra("recid", 0);
            }
            mContext.startActivity(intent);
        } else if ("2".equals(advertise_type)) {
            String ht = mList.get(position).advertise_data.ht;
            String path = mList.get(position).advertise_data.path;
            String ps = mList.get(position).advertise_data.ps;
            String is = mList.get(position).advertise_data.is;
            String su = mList.get(position).advertise_data.su;
            String st = mList.get(position).advertise_data.st;
            String ifreash = mList.get(position).advertise_data.ifreash;
            intent.setClass(mContext, WerActivity.class);

            intent.putExtra("index", ht);
            intent.putExtra("path", path);
            intent.putExtra("pagefresh", ps);
            intent.putExtra("share", is);
            intent.putExtra("shareUrl", su);
            intent.putExtra("shareType", st);
            intent.putExtra("sharefresh", ifreash);
            mContext.startActivity(intent);
        } else if ("3".equals(advertise_type)) {
            String url = mList.get(position).advertise_data.url;
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mContext.startActivity(intent);
        }
    }

    public static void ReadIntent(RecList mRecommentRecList, Activity mActivity) {
        if (mRecommentRecList == null) {
            return;
        }
        Intent intent = new Intent();
        /*
         * advertise_type: 广告类型 1：作品 2：内部链接 3：外部链接
         * readflag: 0：作品信息 1：阅读
         */
        String mTid;
        if (TextUtils.isEmpty(mRecommentRecList.rec_id)) {
            mTid = "0";
        } else {
            mTid = mRecommentRecList.rec_id;
        }
        String advertise_type = mRecommentRecList.advertise_type + "";
        if ("1".equals(advertise_type)) {
            String readflag = mRecommentRecList.advertise_data.readflag;
            int wids = mRecommentRecList.advertise_data.wid;
            mBid = mRecommentRecList.advertise_data.wid + "";
            if ("1".equals(readflag)) {
                Work work = new Work();
                work.wid = wids;
                intent.setClass(mActivity, ReadActivity.class);
                intent.putExtra("work", work);
                CollBookBean mCollBook  = new CollBookBean();
                mCollBook.setTitle(work.title);
                mCollBook.set_id(work.wid+"");
                intent.putExtra(EXTRA_COLL_BOOK, mCollBook);
            } else {
                int recids = mRecommentRecList.advertise_data.rec_id;
                intent.setClass(mActivity, WorkDetailActivity.class);
                intent.putExtra("wid", wids);
                intent.putExtra("recid", recids);
            }
            DeepLinkUtil.addPermanent(mActivity, "event_chapter_block_recommend", "阅读页", "点击推荐书", "", wids+"", "", "", "", "");
            mActivity.startActivity(intent);

        } else if ("2".equals(advertise_type)) {
            mBid = "";
            String ht = mRecommentRecList.advertise_data.ht;
            String path = mRecommentRecList.advertise_data.path;
            String ps = mRecommentRecList.advertise_data.ps;
            String is = mRecommentRecList.advertise_data.is;
            String su = mRecommentRecList.advertise_data.su;
            String st = mRecommentRecList.advertise_data.st;
            String ifreash = mRecommentRecList.advertise_data.ifreash + "";
            intent.setClass(mActivity, WerActivity.class);

            intent.putExtra("index", ht);
            intent.putExtra("path", path);
            intent.putExtra("pagefresh", ps);
            intent.putExtra("share", is);
            intent.putExtra("shareUrl", su);
            intent.putExtra("shareType", st);
            intent.putExtra("sharefresh", ifreash);
            mActivity.startActivity(intent);
        } else if ("3".equals(advertise_type)) {
            mBid = "";
            String url = mRecommentRecList.advertise_data.url;
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            mActivity.startActivity(intent);
        }


    }
}
