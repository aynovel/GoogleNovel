package life.forever.cf.publics.tool;

import static life.forever.cf.activtiy.Cods.EXTRA_COLL_BOOK;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import life.forever.cf.entry.Work;
import life.forever.cf.entry.CollBookBean;
import life.forever.cf.activtiy.WorkDetailActivity;
import life.forever.cf.activtiy.ReadActivity;

public class BDeepLinkUtil {


    public static boolean getDeepLinkData(Context activityContext, String UriString) {
        Intent mIntent = new Intent();

        boolean isDeepLinkFlag = false;
        if (null != UriString && !TextUtils.isEmpty(UriString)) {


            if (!UriString.contains("book://book.top/")) {
                return isDeepLinkFlag;
            }

            String urls = UriString.replace("book://book.top/", "");
            String[] urlFormat = urls.split("\\?");
            try {
                if (null != urlFormat[0]) {
                    if ("Detail".equals(urlFormat[0])) {
                        String[] param = urlFormat[1].split("&");
                        String[] novelId = param[0].split("=");
                        if (null != novelId[0] && "novelId".equals(novelId[0])) {

                            if(activityContext != null)
                            {
                                mIntent.setClass(activityContext, WorkDetailActivity.class);
                                mIntent.putExtra("wid", Integer.parseInt(novelId[1].trim()));
                                mIntent.putExtra("recid", 0);
                                activityContext.startActivity(mIntent);
                            }

                        }
                    }
                    if ("Chapter".equals(urlFormat[0])) {
                        String[] param = urlFormat[1].split("&");
                        String[] novelId = param[0].split("=");
                        String[] chapterOrder = param[1].split("=");
                        if (null != novelId[0] && "novelId".equals(novelId[0]) &&
                                null != chapterOrder[0] && "index".equals(chapterOrder[0])
                        ) {

                            Work work = new Work();
                            work.wid = Integer.parseInt(novelId[1].trim());
                            int indexChapter = Integer.parseInt(chapterOrder[1].trim());
                            if (indexChapter <= 0) {
                                indexChapter = 0;
                            } else {
                                indexChapter = indexChapter - 1;
                            }
                            work.lastChapterOrder = indexChapter;
                            work.toReadType = 2;
                            if(activityContext != null) {
                                mIntent.setClass(activityContext, ReadActivity.class);
                                mIntent.putExtra("work", work);
//                            mIntent.putExtra("type", ReaderReadActivity.NOT_FROM_SHELF);

                                CollBookBean mCollBook  = new CollBookBean();
                                mCollBook.setTitle(work.title);
                                mCollBook.set_id(work.wid+"");
                                mIntent.putExtra(EXTRA_COLL_BOOK, mCollBook);

                                activityContext.startActivity(mIntent);
                            }
                        }
                    }


                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            isDeepLinkFlag = true;

        }
        return isDeepLinkFlag;
    }


}
