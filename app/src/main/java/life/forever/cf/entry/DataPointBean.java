package life.forever.cf.entry;

import android.os.Bundle;
import android.text.TextUtils;

import life.forever.cf.manage.DataPointUploadManager;

public class DataPointBean {
    private  DataPointType pointType;

    private  String pointActionStr;


    public DataPointBean(DataPointType pointType, String pointActionStr) {
        this.pointType = pointType;
        this.pointActionStr = pointActionStr;
    }


    public void setReadDataPoint(String novelid, String chapterid,
                                 int chpternum,int modeid, int mode, String source)
    {
        if(this.pointActionStr != null)
        {
            Bundle bundleEvent = new Bundle();
            switch (this.pointActionStr)
            {
                case "event_chapter_read"://阅读
                {
                    int a = 0;
                    if (!TextUtils.isEmpty(novelid)) {
                        bundleEvent.putString("novelid",novelid);
                    }
                    if (!TextUtils.isEmpty(chapterid)) {
                        bundleEvent.putString("chapterid",chapterid);
                    }
                }
                break;
                case "event_chapter_click"://翻页
                {
                    int a = 1;
                    if (!TextUtils.isEmpty(novelid)) {
                        bundleEvent.putString("novelid",novelid);
                    }
                    if (!TextUtils.isEmpty(chapterid)) {
                        bundleEvent.putString("chapterid",chapterid);
                    }
                }
                break;
                case "event_chapter_recommend"://阅读推荐位
                {
                    int a = 2;
                    if (!TextUtils.isEmpty(novelid)) {
                        bundleEvent.putString("novelid",novelid);
                    }
                }
                break;
                case "event_buy_chapter"://章节订阅
                {
                    int a = 3;
                    if (!TextUtils.isEmpty(novelid)) {
                        bundleEvent.putString("novelid",novelid);
                    }
                    if (!TextUtils.isEmpty(chapterid)) {
                        bundleEvent.putString("chapterid",chapterid);
                    }
                }
                break;
                case "event_chapter_download"://下载
                {
                    int a = 4;
                    if (!TextUtils.isEmpty(novelid)) {
                        bundleEvent.putString("novelid",novelid);
                    }
                    if (!TextUtils.isEmpty(chapterid)) {
                        bundleEvent.putString("chapterid",chapterid);
                    }
                    bundleEvent.putInt("chpternum",chpternum);
                }
                break;
                case "event_menu_activation"://激活菜单
                {
                    int a = 5;
                }
                break;
                case "event_menu_catalog"://点击目录
                {
                    int a = 6;
                }
                break;
                case "event_menu_setting"://点击设置
                {
                    int a = 7;
                    //modeid=1，2、3分别表示滚动、翻页、平滑的阅读模式
                    bundleEvent.putInt("modeid",modeid);
                }
                break;
                case "event_menu_comment"://点击评论
                {
                    int a = 8;
                }
                break;
                case "event_chapter_block_in"://触发拦截弹窗
                {
                    int a = 9;
                }
                break;
                case "event_chapter_block_contine"://拦截-点击继续阅读
                {
                    int a = 10;
                    //计数，mode=1、2，表示推荐位样式；
                    //novelid=小说ID，无小说则传0
                    bundleEvent.putInt("mode",mode);
                    if (!TextUtils.isEmpty(novelid)) {
                        bundleEvent.putString("novelid",novelid);
                    }
                }
                break;
                case "event_chapter_block_close"://拦截-关闭弹窗
                {
                    int a = 12;
                }
                break;
                case "event_chapter_block_quit"://拦截-点击返回上一页
                {
                    int a = 13;
                }
                break;
                case "event_chapter_block_recommend"://拦截-点击推荐书
                {
                    int a = 14;
                    //mode=1、2，表示推荐位样式；
                    bundleEvent.putInt("mode",mode);
                    if (!TextUtils.isEmpty(novelid)) {
                        bundleEvent.putString("novelid",novelid);
                    }
                }
                break;
                case "event_chapter_block_read"://拦截-点击阅读下一本书
                {
                    int a = 15;
                    //mode=1、2，表示推荐位样式；
                    bundleEvent.putInt("mode",mode);
                    if (!TextUtils.isEmpty(novelid)) {
                        bundleEvent.putString("novelid",novelid);
                    }
                }
                break;
                case "event_chapter_block_switch"://拦截-点击更换按钮
                {
                    int a = 16;
                }
                break;
                case "event_chapter_addshelf"://加入书架
                {
                    int a = 17;
                    if (!TextUtils.isEmpty(novelid)) {
                        bundleEvent.putString("novelid",novelid);
                    }
                }
                break;
                case "event_chapter_note"://5秒后弹窗加入书架提示
                {
                    int a = 18;
                    //souce=addshelf、close、null
                    //分别表示为：加入书架操作、关闭弹窗操作、无操作；
                    if (!TextUtils.isEmpty(source)) {
                        bundleEvent.putString("source",source);
                    }
                }
                break;
                case "event_chapter_quite_addshelf"://退出拦截加入书架弹窗
                {
                    int a = 19;
                    //计数，
                    //souce=addshelf、cancel
                    //分别表示为：加入书架、取消操作
                    if (!TextUtils.isEmpty(source)) {
                        bundleEvent.putString("source",source);
                    }
                }
                break;
                case "event_chapter_last_click"://章节末页推荐位
                {
                    int a = 20;
                    if (!TextUtils.isEmpty(novelid)) {
                        bundleEvent.putString("novelid",novelid);
                    }
                }
                break;
                case "event_chapter_last"://进入作品末页
                {
                    int a = 21;
                }
                break;
                case "event_last_click"://作品末页推荐位
                {
                    int a = 22;
                    if (!TextUtils.isEmpty(novelid)) {
                        bundleEvent.putString("novelid",novelid);
                    }
                }
                break;
            }

            if(bundleEvent != null)
            {
                reportDataPointBean(this.pointActionStr,bundleEvent);
            }
        }
    }


    public void reportDataPointBean(String actionName, Bundle reportIntent)
    {
        DataPointUploadManager.getInstance().reportDataBean(actionName,reportIntent);
    }
}
