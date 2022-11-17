package life.forever.cf.activtiy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.RecList;
import life.forever.cf.entry.DataPointBean;
import life.forever.cf.entry.DataPointType;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.CustomIntent;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.adapter.ReadCommendMoreAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RecommendationAlertDialog implements Constant {

    static TextView read_commend_name;
    static TextView read_commend_des;
    static ImageView read_commond_cover;
    static RecyclerView mRecyclerView;
    static LinearLayout ll_one_book;
    static List<RecList> mReadRecLists = new ArrayList<>();
    static List<RecList> mRandomReadRecLists = new ArrayList<>();
    static RecList mRecList = new RecList();
    static Random random;
    static AlertDialog dialog;
    static ReadCommendMoreAdapter mReadCommendMoreAdapter;

    public static void show(Activity context, List<RecList> mRecLists, String style, final View.OnClickListener onNoClick, final View.OnClickListener onYesClick) {
        View root = LayoutInflater.from(context).inflate(R.layout.recommendation_dialog, null, FALSE);
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        adb.setCancelable(FALSE);
        dialog = adb.create();

        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        DeepLinkUtil.addPermanent(context, "event_chapter_block_in", "阅读页", "触发拦截弹窗", "", "", "", "", "", "");
        mReadRecLists = mRecLists;
        read_commend_name = root.findViewById(R.id.read_commend_name);
        read_commend_des = root.findViewById(R.id.read_commend_des);
        mRecyclerView = root.findViewById(R.id.rc_more_comment);
        ll_one_book = root.findViewById(R.id.ll_one_book);
        read_commond_cover = root.findViewById(R.id.read_commond_cover);
        if (mReadRecLists != null && mReadRecLists.size() > 0) {
            set(context, style);
        }
        Window window = dialog.getWindow();
        window.setContentView(root);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayoutManager eightLayoutManager = new LinearLayoutManager(context);
        eightLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(eightLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mReadCommendMoreAdapter = new ReadCommendMoreAdapter(context, mRandomReadRecLists, 3);
        mRecyclerView.setAdapter(mReadCommendMoreAdapter);


        DataPointBean showPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_block_in");
        showPagePoint.setReadDataPoint(null,null
                ,0,0,0,null);


        mReadCommendMoreAdapter.setOnItemClickListener(new ReadCommendMoreAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (mRandomReadRecLists != null && mRandomReadRecLists.size()>0){
                    if (mReadRecLists != null && mReadRecLists.size() > 0 && mReadRecLists.get(position) == null){
                        DeepLinkUtil.addPermanent(context, "event_chapter_block_recommend", "阅读页", "拦截-点击推荐书:mode=" + style, "", mReadRecLists.get(position).wid + "", "", "", "", "");
                    }
                    CustomIntent.ReadIntent(mRandomReadRecLists.get(position), context);
                    dis();


                    DataPointBean showPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_block_recommend");
                    showPagePoint.setReadDataPoint("" + mReadRecLists.get(position).wid,null
                            ,0,0,Integer.parseInt(style.trim()),null);
                }

            }
        });


        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    dialog.dismiss();
                    return TRUE;
                }
                return FALSE;
            }
        });


        dialog.findViewById(R.id.read_commond_cover).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (mRecList != null) {
                    CustomIntent.ReadIntent(mRecList, context);
                    DeepLinkUtil.addPermanent(context, "event_chapter_block_recommend", "阅读页", "拦截-点击推荐书:mode=" + style, "", mRecList.wid + "", "", "", "", "");

                    DataPointBean showPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_block_recommend");
                    showPagePoint.setReadDataPoint("" + mRecList.wid,null
                            ,0,0,Integer.parseInt(style.trim()),null);
                }
            }
        });


        dialog.findViewById(R.id.rl_refresh).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                set(context, style);

                DataPointBean showPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_block_switch");
                showPagePoint.setReadDataPoint(null,null
                        ,0,0,0,null);
            }
        });

        dialog.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onYesClick != null) {
                    DeepLinkUtil.addPermanent(context, "event_chapter_block_contine", "阅读页", "拦截-点击继续阅读", "", "", "", "", "", "");
                    Intent intent = new Intent();
                    if ("1".equals(style)) {
                        if (mRecList != null){
                            intent.setClass(context, WorkDetailActivity.class);
                            intent.putExtra("wid", mRecList.wid);
                            intent.putExtra("recid", mRecList.advertise_data.rec_id);
                            context.startActivity(intent);
                            DeepLinkUtil.addPermanent(context, "event_chapter_block_recommend", "阅读页", "拦截-点击推荐书:mode=" + style, "", mRecList.wid + "", "", "", "", "");

                            DataPointBean showPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_block_contine");
                            showPagePoint.setReadDataPoint(""+mRecList.wid,null
                                    ,0,0,Integer.parseInt(style.trim()),null);

                            DataPointBean readPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_block_read");
                            readPagePoint.setReadDataPoint(""+mRecList.wid,null
                                    ,0,0,Integer.parseInt(style.trim()),null);

                        }
                    } else {
                        if (mRandomReadRecLists != null && mRandomReadRecLists.size()>0 && mRandomReadRecLists.get(0) != null){
                            String advertise_type = mRandomReadRecLists.get(0).advertise_type + "";
                            String novelID = "0";
                            if ("1".equals(advertise_type)) {
                                intent.setClass(context, WorkDetailActivity.class);
                                intent.putExtra("wid", mRandomReadRecLists.get(0).advertise_data.wid);
                                intent.putExtra("recid", mRandomReadRecLists.get(0).advertise_data.rec_id);
                                context.startActivity(intent);
                                DeepLinkUtil.addPermanent(context, "event_chapter_block_recommend", "阅读页", "拦截-点击推荐书:mode=" + style, "", mRecList.wid + "", "", "", "", "");

                                novelID = ""+mRandomReadRecLists.get(0).advertise_data.wid;
                            }

                            DataPointBean showPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_block_contine");
                            showPagePoint.setReadDataPoint(novelID,null
                                    ,0,0,Integer.parseInt(style.trim()),null);

                            DataPointBean readPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_block_read");
                            readPagePoint.setReadDataPoint(novelID,null
                                    ,0,0,Integer.parseInt(style.trim()),null);
                        }

                    }
                    onYesClick.onClick(v);
                }
            }
        });

        dialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onNoClick != null) {
                    DeepLinkUtil.addPermanent(context, "event_chapter_block_quit", "阅读页", "拦截-点击返回上一页", "", "", "", "", "", "");
                    onNoClick.onClick(v);

                    DataPointBean showPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_block_quit");
                    showPagePoint.setReadDataPoint(null,null
                            ,0,0,0,null);
                }
            }
        });

        dialog.findViewById(R.id.img_cancel).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DeepLinkUtil.addPermanent(context, "event_chapter_block_close", "阅读页", "拦截-关闭弹窗", "", "", "", "", "", "");
                dialog.dismiss();

                DataPointBean showPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_chapter_block_close");
                showPagePoint.setReadDataPoint(null,null
                        ,0,0,0,null);
            }
        });

        dialog.findViewById(R.id.read_commend_name).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onNoClick != null) {
                    onNoClick.onClick(v);
                }
            }
        });

        dialog.findViewById(R.id.read_commend_des).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onNoClick != null) {
                    onNoClick.onClick(v);
                }
            }
        });


    }

    public static void dis(){
        if (dialog!= null){
            dialog.dismiss();
        }

    }
    private static void set(Activity context, String style) {
        if (mReadRecLists== null || mReadRecLists.size()==0){
            return;
        }
        if (random == null) {
            random = new Random();
        }
        int o = random.nextInt(mReadRecLists.size());
        if (mReadRecLists.get(o) != null) {
            if (!TextUtils.isEmpty(mReadRecLists.get(o).h_url)) {

                if (style.equals("1")) {
                    ll_one_book.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    mRecList = mReadRecLists.get(o);
                    read_commend_name.setText(mRecList.title);
                    read_commend_des.setText(mRecList.description);

                    GlideUtil.picCache(context,mRecList.h_url,mRecList.wid + "readrec",R.drawable.default_work_cover, read_commond_cover);

//                    String cover = PlotRead.getConfig().getString(mRecList.wid + "readrec", "");
//                    if (TextUtils.isEmpty(cover)) {
//                        GlideUtil.recommentLoad(context, mRecList.wid + "readrec", mRecList.h_url, mRecList.h_url, R.drawable.default_work_cover, read_commond_cover);
//                    } else {
//                        GlideUtil.recommentLoad(context, "", cover, mRecList.h_url, R.drawable.default_work_cover, read_commond_cover);
//                    }

                } else {
                    if (mReadRecLists.size() > 3) {
                        mRandomReadRecLists.clear();
                        mRandomReadRecLists.add(mReadRecLists.get(o));
                        int b = 0;
                        for (int i = 0; i < mReadRecLists.size(); i++) {
                            b = random.nextInt(mReadRecLists.size());
                            if (b != o) {
                                break;
                            }
                        }
                        int c = 0;
                        for (int i = 0; i < mReadRecLists.size(); i++) {
                            c = random.nextInt(mReadRecLists.size());
                            if (c != o && c != b) {
                                break;
                            }
                        }
                        mRandomReadRecLists.add(mReadRecLists.get(b));
                        mRandomReadRecLists.add(mReadRecLists.get(c));
                    } else {
                        mRandomReadRecLists = mReadRecLists;
                    }
                    ll_one_book.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    if (mReadCommendMoreAdapter != null) {
                        mReadCommendMoreAdapter.notifyDataSetChanged();
                    }

                }
            }
        }
    }

}
