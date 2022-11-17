package life.forever.cf.datautils;

import android.graphics.Paint;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.tool.DisplayUtil;
import life.forever.cf.publics.tool.SharedPreferencesUtil;

/**
 * 阅读配置
 *
 * @author Haojie.Dai
 */
public class ReadSettings implements Constant {

    public final static int BATTERY_BORDER_WIDTH = DisplayUtil.dp2px(PlotRead.getApplication(), TWENTY);
    final static int BATTERY_BORDER_HEIGHT = DisplayUtil.dp2px(PlotRead.getApplication(), TEN);
    final static int BATTERY_HEADER_WIDTH = DisplayUtil.dp2px(PlotRead.getApplication(), TWO);
    final static int BATTERY_HEADER_HEIGHT = DisplayUtil.dp2px(PlotRead.getApplication(), SIX);

    public final static int WIDTH_MARGIN = DisplayUtil.dp2px(PlotRead.getApplication(), FIFTEEN);
    public final static int HEIGHT_MARGIN = DisplayUtil.dp2px(PlotRead.getApplication(), THIRTEEN);
    final static int TITLE_MARGIN_HEADER = DisplayUtil.dp2px(PlotRead.getApplication(), 110);
    final static int CONTENT_MARGIN_TITLE = DisplayUtil.dp2px(PlotRead.getApplication(), 55);
    final static int CONTENT_MARGIN_BOTTOM = DisplayUtil.dp2px(PlotRead.getApplication(), SIXTY);
    final static int CONTENT_MARGIN_HEADER = DisplayUtil.dp2px(PlotRead.getApplication(), 22);


    final static int REWARD_MARGIN_CONTENT = DisplayUtil.dp2px(PlotRead.getApplication(), THIRTY);
    final static int REWARD_HEIGHT = DisplayUtil.dp2px(PlotRead.getApplication(), EIGHTY);

    /**
     * 画笔
     */
    private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);


    /**
     * 夜间模式
     */
    public static boolean isNightMode = SharedPreferencesUtil.getBoolean(APP, KEY_NIGHT_MODE);

    /**
     * 吐槽开关
     */
    public static boolean isRollable = PlotRead.getConfig().getBoolean(KEY_MESSAGE_ROLL, TRUE);

    /**
     * 字体颜色
     */
    public static int text_color = isNightMode ? color_656667 : color_2F3031;


    /**
     * 设置吐槽开关
     *
     * @param rollable
     */
    public static void setRollable(boolean rollable) {
        if (isRollable != rollable) {
            isRollable = rollable;
            SharedPreferencesUtil.putBoolean(APP, KEY_MESSAGE_ROLL, isRollable);
        }
    }



    /**
     * 内容字体大小sp
     */
    public static int textSize = SharedPreferencesUtil.getSharedPreferences(APP).getInt(KEY_TEXT_SIZE, TWENTY);

    /**
     * 缩小字体
     */
    public static boolean smallerFont() {
        if (textSize <= SIXTEEN) {
            return FALSE;
        }
        textSize--;
        SharedPreferencesUtil.putInt(APP, KEY_TEXT_SIZE, textSize);
        return TRUE;
    }

    /**
     * 放大字体
     */
    public static boolean largerFont() {
        if (textSize >= TWENTY_FIVE) {
            return FALSE;
        }
        textSize++;
        SharedPreferencesUtil.putInt(APP, KEY_TEXT_SIZE, textSize);
        return TRUE;
    }

//    /**
//     * 阅读背景样式
//     */
//    public static Background background = Background.getEnum(SharedPreferencesUtil.getInt(APP, KEY_BACKGROUND));
//    /**
//     * 背景颜色
//     */
//    public static int background_color = isNightMode ? color_000001 : background.getColor();
//
//    /**
//     * 设置夜间模式
//     *
//     * @param nightMode
//     */
//    public static void setNightMode(boolean nightMode) {
//        if (isNightMode != nightMode) {
//            isNightMode = nightMode;
//            SharedPreferencesUtil.putBoolean(APP, KEY_NIGHT_MODE, isNightMode);
//            background_color = nightMode ? color_000001 : background.getColor();
//            text_color = nightMode ? color_656667 : color_2F3031;
//        }
//    }
//    /**
//     * 设置阅读背景索引
//     *
//     * @param index
//     */
//    public static void setBackground(int index) {
//        SharedPreferencesUtil.putInt(APP, KEY_BACKGROUND, index);
//        background = Background.getEnum(index);
//        background_color = background.getColor();
//        text_color = DARK_2;
//        if (isNightMode) {
//            setNightMode(FALSE);
//        }
//    }
//    /**
//     * 翻页方式
//     */
//    public static FlipMode flipMode = FlipMode.getEnum(SharedPreferencesUtil.getInt(APP, KEY_FLIP_MODE));
//
//    /**
//     * 设置翻页方式索引
//     *
//     * @param index
//     */
//    public static void setFlipMode(int index) {
//        if (index != flipMode.getIndex()) {
//            flipMode = FlipMode.getEnum(index);
//            SharedPreferencesUtil.putInt(APP, KEY_FLIP_MODE, index);
//        }
//    }
//
//    /**
//     * 行间距样式
//     */
//    public static LineSpace lineSpace = LineSpace.getEnum(SharedPreferencesUtil.getSharedPreferences(APP).getInt(KEY_LINE_SPACE, ONE));
//
//    /**
//     * 设置行间距索引
//     *
//     * @param index
//     */
//    public static void setLineSpace(int index) {
//        if (index != lineSpace.getIndex()) {
//            lineSpace = LineSpace.getEnum(index);
//            SharedPreferencesUtil.putInt(APP, KEY_LINE_SPACE, index);
//        }
//    }
//
    /**
     * 获取画笔
     *
     * @param paintType
     * @return
     */
    public static Paint getPaint(PaintType paintType) {
//        Typeface typeFaceHeavy = Typeface.createFromAsset(application.getAssets(), "fonts/ssss.ttf");
//        paint.setTypeface(typeFaceHeavy);
        switch (paintType) {
            case HEADER:
                paint.setColor(GRAY_1);
                paint.setTextSize(DisplayUtil.sp2px(PlotRead.getApplication(), ELEVEN));
                break;
            case TITLE:
                paint.setColor(text_color);
                paint.setTextSize(DisplayUtil.sp2px(PlotRead.getApplication(), TWENTY_FIVE));
                break;
            case CONTENT:
                paint.setColor(text_color);
                paint.setTextSize(DisplayUtil.sp2px(PlotRead.getApplication(), textSize));
                break;
            default:
                break;
        }
        return paint;
    }

    public static float BATTERY_BI = ONE;

    final static int BUY_MODE_TIP_TEXT_SIZE = DisplayUtil.sp2px(PlotRead.getApplication(), THIRTEEN);
    public final static int BUY_MODE_TIP_TEXT_COLOR = GRAY_1;
    public final static int BUY_BTN_WIDTH = DisplayUtil.sp2px(PlotRead.getApplication(), 292);
    public final static int BUY_BTN_HEIGHT = DisplayUtil.sp2px(PlotRead.getApplication(), FORTY);
    public final static int BUY_BTN_TEXT_SIZE = DisplayUtil.sp2px(PlotRead.getApplication(), FOURTEEN);
    public final static int BUY_BTN_TEXT_SIZE_TWENTY_FIVE = DisplayUtil.sp2px(PlotRead.getApplication(), TWENTY_FIVE);

    public final static int BUY_BTN_TEXT_SIZE_5 = DisplayUtil.sp2px(PlotRead.getApplication(), FIVE);
    public final static int BUY_BTN_TEXT_SIZE_16 = DisplayUtil.sp2px(PlotRead.getApplication(), SIXTEEN);
    public final static int BUY_BTN_TEXT_SIZE_12 = DisplayUtil.sp2px(PlotRead.getApplication(), TWELVE);
    public final static int BUY_BTN_TEXT_SIZE_10 = DisplayUtil.sp2px(PlotRead.getApplication(), TEN);
    final static String BUY_MODE_TIP_1 = application.getString(R.string.paid_chapter);
    final static String BUY_MODE_TIP_2 = application.getString(R.string.after_purchase_can);
    final static String BUY_MODE_TIP_3 = application.getString(R.string.after_purchase_can_two);

    public final static String BUY_MODE_AUTO_BUY = application.getString(R.string.section_buy_automatically);
//    final static String BUY_MODE_CHAPTER_PRICE = "VIP章节按照每%d字%d书币收费";
//    final static String BUY_MODE_SINGLE_PRICE = "VIP章节按照整本%d书币收费";
    final static String BUY_MODE_USER_MONEY = application.getString(R.string.current_balance_s);
}
