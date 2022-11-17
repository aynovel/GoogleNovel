package life.forever.cf.activtiy;

import java.io.File;

public class Cods {




    public static final String FORMAT_BOOK_DATE = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String FORMAT_TIME = "HH:mm";
    public static final String FORMAT_FILE_DATE = "yyyy-MM-dd";




    public static String BOOK_CACHE_PATH = FileUtils.getCachePath()+ File.separator
            + "book_cache"+ File.separator ;

    public static String SD_BOOK_PATH = FileUtils.getNewCachePath()+File.separator
            + PlotRead.getContext().getPackageName() + File.separator;

    public static final String EXTRA_COLL_BOOK = "extra_coll_book";





    public static final int DEFAULT_TEXT_DP_SIZE = 20;
    public static final int DEFAULT_MINI_TEXT_SIZE = ScreenUtils.dpToPx(12);
    public static final int DEFAULT_MAX_TEXT_SIZE = ScreenUtils.dpToPx(30);




    public static final int YYReadCore_JinCou_LeftRightMargin = 0;
    public static final int YYReadCore_Shushi_LeftRightMargin = 10;
    public static final int YYReadCore_Songsan_LeftRightMargin = 15;
    public static final int YYReadCore_Default_LeftRightMargin = 10;

    public static final int YYNativeAd_TopBottom_Height = 22;

    public static final int Reader_DrawBtn_Corner = 22;


    //字体路径
    public static final String READ_FONT_PATH = "fonts/";
    public static final String READ_FONT_PATH_DECOLLATOR = "@&!!";
    public static final String READ_DEFAULT_FONTNAME = "default";
    public static final String READ_DEFAULT_FONTPATH = READ_DEFAULT_FONTNAME + READ_FONT_PATH_DECOLLATOR + READ_DEFAULT_FONTNAME;

    //听书音源
    public static final String SPEAKER_NORMALWOMEN = "0";
    public static final String SPEAKER_NORMALMEN = "1";
    //    private static final String SPEAKER_SPECIALMEN = "2";
    public static final String SPEAKER_EMTIONALMAN = "3";
    public static final String SPEAKER_EMTIONALCHILD = "4";
    public static final String SPEAKER_XQ = "5";



    // TODO: 2021/10/12 1.8.1测试模拟充值
    public static boolean rechargeFlag = false;
    public static boolean testRechargeFlag = true;
}
