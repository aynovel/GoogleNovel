package life.forever.cf.datautils;


import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import life.forever.cf.publics.tool.BDeepLinkUtil;

public class CustomWebChromeClient extends WebViewClient {

    private Context mActivityContext;

    public CustomWebChromeClient(Context mActivityContext) {
        this.mActivityContext = mActivityContext;
    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (BDeepLinkUtil.getDeepLinkData(mActivityContext,url)) {
            return true;
        }

        return super.shouldOverrideUrlLoading(view, url);

    }

//    /*
//     * facebook 深链
//     * @param UriString
//     */
//    private boolean getCustomDeepLinkData(String UriString) {
//        Intent mIntent = new Intent();
//        boolean deeplinkFlag = false;
//
//
//        Log.e("deeplink", "deepLink -=====" + UriString);
//
////        if(true)
////        {
////            return deeplinkFlag;
////        }
//
//        if (null != UriString && !TextUtils.isEmpty(UriString)) {
//
//
//            if (!UriString.contains("Reader://Reader.top/")) {
//                return deeplinkFlag;
//            }
//            String urls = UriString.replace("Reader://Reader.top/", "");
//            String[] urlFormat = urls.split("\\?");
//            if (null != urlFormat[0]) {
//                if ("novelDetail".equals(urlFormat[0])) {
//                    String[] param = urlFormat[1].split("&");
//                    String[] novelId = param[0].split("=");
//                    if (null != novelId[0] && "novelId".equals(novelId[0])) {
//                        if (mActivityContext != null) {
//                            mIntent.setClass(mActivityContext, WorkDetailActivity.class);
//                            mIntent.putExtra("wid", Integer.parseInt(novelId[1].trim()));
//                            mIntent.putExtra("recid", 0);
//                            mActivityContext.startActivity(mIntent);
//                        }
//                    }
//                }
//                if ("readChapter".equals(urlFormat[0])) {
//                    String[] param = urlFormat[1].split("&");
//                    String[] novelId = param[0].split("=");
//                    String[] chapterOrder = param[1].split("=");
//                    if (null != novelId[0] && "novelId".equals(novelId[0]) &&
//                            null != chapterOrder[0] && "index".equals(chapterOrder[0])
//                    ) {
//
//                        Work work = new Work();
//                        work.wid = Integer.parseInt(novelId[1].trim());
//                        int indexChapter = Integer.parseInt(chapterOrder[1].trim());
//                        if (indexChapter <= 0) {
//                            indexChapter = 0;
//                        } else {
//                            indexChapter = indexChapter - 1;
//                        }
//                        work.lastChapterOrder = indexChapter;
//                        if (mActivityContext != null) {
//                            mIntent.setClass(mActivityContext, ReadActivity.class);
//                            mIntent.putExtra("work", work);
//                            mIntent.putExtra("type", ReadActivity.NOT_FROM_SHELF);
//                            mIntent.putExtra("push", true);
//                            mActivityContext.startActivity(mIntent);
//                        }
//                    }
//                }
//
//                if ("promotion".equals(urlFormat[0])) {
//                    String[] param = urlFormat[1].split("&");
//                    String[] param_url = param[0].split("=");
//                    if (null != param_url[0] && "url".equals(param_url[0])) {
//                        String url = param_url[1];
//
//
//                        if (url != null) {
//                            String paramStr = new String(Base64.decode(url, Base64.DEFAULT));
//                            JSONObject object = JSONUtil.newJSONObject(paramStr);
//                            String index = JSONUtil.getString(object, "url");
//                            String path = JSONUtil.getString(object, "path");
//                            int pagefresh = JSONUtil.getInt(object, "pagefresh");
//                            int share = JSONUtil.getInt(object, "share");
//                            int sharefresh = JSONUtil.getInt(object, "sharefresh");
//                            int shareType = JSONUtil.getInt(object, "type");
//                            String title = JSONUtil.getString(object, "title");
//                            String desc = JSONUtil.getString(object, "desc");
//                            String img = JSONUtil.getString(object, "image");
//                            String shareurl = JSONUtil.getString(object, "shareurl");
//
//
//                            if(mActivityContext  !=null )
//                            {
//                                if (API.H5_RECHARGE_MONTH_VIP.equals(path)) {
//                                    //            intent.setClass(activity, MonthVipTopUpActivity.class);
//                                } else {
//                                    mIntent.setClass(mActivityContext, WerActivity.class);
//                                    mIntent.putExtra("index", index);
//                                    mIntent.putExtra("path", path);
//                                    mIntent.putExtra("pagefresh", pagefresh == 0);
//                                    mIntent.putExtra("share", share == 1);
//                                    mIntent.putExtra("sharefresh", sharefresh == 1);
//                                    mIntent.putExtra("shareType", shareType);
//                                    mIntent.putExtra("shareTitle", title);
//                                    mIntent.putExtra("shareDesc", desc);
//                                    mIntent.putExtra("shareImg", img);
//                                    mIntent.putExtra("shareUrl", shareurl);
//                                }
//                                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                mActivityContext.startActivity(mIntent);
//                            }
//                        }
//                    }
//                }
//
//                if ("browser".equals(urlFormat[0])) {
//                    String[] param = urlFormat[1].split("&");
//                    String[] param_url = param[0].split("=");
//                    if (null != param_url[0] && "url".equals(param_url[0])) {
//                        String url = param_url[1];
//
//                        if (mActivityContext != null) {
//                            mIntent.setAction(Intent.ACTION_VIEW);
//                            mIntent.setData(Uri.parse(url));
//                            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                            mActivityContext.startActivity(mIntent);
//                        }
//                    }
//                }
//
//                deeplinkFlag = true;
//            }
//
//        }
//
//        return deeplinkFlag;
//    }

}
