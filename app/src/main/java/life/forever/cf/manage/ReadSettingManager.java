package life.forever.cf.manage;

import life.forever.cf.weight.LayoutMode;
import life.forever.cf.interfaces.PageMode;
import life.forever.cf.interfaces.PageStyle;
import life.forever.cf.activtiy.Cods;
import life.forever.cf.activtiy.ScreenUtils;
import life.forever.cf.activtiy.SharedPreUtils;

public class ReadSettingManager {
    /*************实在想不出什么好记的命名方式。。******************/
    public static final int READ_BG_DEFAULT = 0;
    public static final int READ_BG_1 = 1;
    public static final int READ_BG_2 = 2;
    public static final int READ_BG_3 = 3;
    public static final int READ_BG_4 = 4;
    public static final int NIGHT_MODE = 5;

    public static final String SHARED_READ_BG = "shared_read_bg";
    public static final String SHARED_READ_BRIGHTNESS = "shared_read_brightness";
    public static final String SHARED_READ_IS_BRIGHTNESS_AUTO = "shared_read_is_brightness_auto";
    public static final String SHARED_READ_TEXT_SIZE = "shared_read_text_size";
    public static final String SHARED_READ_IS_TEXT_DEFAULT = "shared_read_text_default";
    public static final String SHARED_READ_PAGE_MODE = "shared_read_mode";
    public static final String SHARED_READ_NIGHT_MODE = "shared_night_mode";
    public static final String SHARED_READ_VOLUME_TURN_PAGE = "shared_read_volume_turn_page";
    public static final String SHARED_READ_FULL_SCREEN = "shared_read_full_screen";
    public static final String SHARED_READ_CONVERT_TYPE = "shared_read_convert_type";
    public static final String SHARED_READ_IS_BRIGHTNESS_ALWAYS = "shared_read_is_brightness_always";
    public static final String SHARED_BOOK_SPEECH_SPEAKER = "share_book_speech_speaker";
    public static final String SHARED_BOOK_SPEECH_SPEED = "share_book_speech_speed";
    public static final String SHARED_BOOK_SPEECH_PITCH = "share_book_speech_pitch";
    public static final String SHARED_BOOK_SPEECH_VOLUME = "share_book_speech_volume";
    public static final String SHARED_LASTSET_READSPEED = "share_last_set_readspeed";
    public static final String SHARED_SELECTED_FONT = "share_selected_font";
    public static final String SHARED_READ_LINE_SIZE = "shared_read_line_size";

    public static final String SHARED_READ_TYPEFACE_MODE = "typeface_read_mode";

    public static final String SHARED_READ_LAYOUT = "shared_read_layout_mode";

    private static volatile ReadSettingManager sInstance;

    private SharedPreUtils sharedPreUtils;

    public static ReadSettingManager getInstance() {
        if (sInstance == null) {
            synchronized (ReadSettingManager.class) {
                if (sInstance == null) {
                    sInstance = new ReadSettingManager();
                }
            }
        }
        return sInstance;
    }

    private ReadSettingManager() {
        sharedPreUtils = SharedPreUtils.getInstance();
    }



    public void setLayoutMode(LayoutMode layoutMode){
        sharedPreUtils.putInt(SHARED_READ_LAYOUT, layoutMode.ordinal());
    }

    public LayoutMode getLayoutMode(){
        int layoutMode = sharedPreUtils.getInt(SHARED_READ_LAYOUT, LayoutMode.DefaultMode.ordinal());
        return LayoutMode.values()[layoutMode];
    }

    public void setPageStyle(PageStyle pageStyle) {
        sharedPreUtils.putInt(SHARED_READ_BG, pageStyle.ordinal());
    }

    public void setBrightness(int progress) {
        sharedPreUtils.putInt(SHARED_READ_BRIGHTNESS, progress);
    }

    public void setAutoBrightness(boolean isAuto) {
        sharedPreUtils.putBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, isAuto);
    }

    public void setAlwaysBrightness(boolean isAlways) {
        sharedPreUtils.putBoolean(SHARED_READ_IS_BRIGHTNESS_ALWAYS, isAlways);
    }

    public void setDefaultTextSize(boolean isDefault) {
        sharedPreUtils.putBoolean(SHARED_READ_IS_TEXT_DEFAULT, isDefault);
    }

    public void setTextSize(int textSize) {
        sharedPreUtils.putInt(SHARED_READ_TEXT_SIZE, textSize);
    }

    public void setPageMode(PageMode mode) {
        sharedPreUtils.putInt(SHARED_READ_PAGE_MODE, mode.ordinal());
    }

    public void setNightMode(boolean isNight) {
        sharedPreUtils.putBoolean(SHARED_READ_NIGHT_MODE, isNight);
    }

    public int getBrightness() {
        return sharedPreUtils.getInt(SHARED_READ_BRIGHTNESS, 40);
    }

    public boolean isBrightnessAuto() {
        return sharedPreUtils.getBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, true);
    }

    public boolean isBrightnessAlways() {
        return sharedPreUtils.getBoolean(SHARED_READ_IS_BRIGHTNESS_ALWAYS, false);
    }

    public int getTextSize() {
        return sharedPreUtils.getInt(SHARED_READ_TEXT_SIZE, ScreenUtils.dpToPx(Cods.DEFAULT_TEXT_DP_SIZE));
    }

    public boolean isDefaultTextSize() {
        return sharedPreUtils.getBoolean(SHARED_READ_IS_TEXT_DEFAULT, false);
    }

    public PageMode getPageMode() {
        int mode = sharedPreUtils.getInt(SHARED_READ_PAGE_MODE, PageMode.COVER.ordinal());
        return PageMode.values()[mode];
    }

    public PageStyle getPageStyle() {
        int style = sharedPreUtils.getInt(SHARED_READ_BG, PageStyle.BG_0.ordinal());
        return PageStyle.values()[style];
    }

    public boolean isNightMode() {
        return sharedPreUtils.getBoolean(SHARED_READ_NIGHT_MODE, false);
    }

    public void setVolumeTurnPage(boolean isTurn) {
        sharedPreUtils.putBoolean(SHARED_READ_VOLUME_TURN_PAGE, isTurn);
    }

    public boolean isVolumeTurnPage() {
        return sharedPreUtils.getBoolean(SHARED_READ_VOLUME_TURN_PAGE, false);
    }

    public void setFullScreen(boolean isFullScreen) {
        sharedPreUtils.putBoolean(SHARED_READ_FULL_SCREEN, isFullScreen);
    }

    public boolean isFullScreen() {
        return sharedPreUtils.getBoolean(SHARED_READ_FULL_SCREEN, true);
    }

    public void setConvertType(int convertType) {
        sharedPreUtils.putInt(SHARED_READ_CONVERT_TYPE, convertType);
    }

    public int getConvertType() {
        return sharedPreUtils.getInt(SHARED_READ_CONVERT_TYPE, 0);
    }

    public String getSpeechSpeaker() {
        return sharedPreUtils.getString(SHARED_BOOK_SPEECH_SPEAKER);
    }

    public void setSpeechSpeaker(String speaker) {
        sharedPreUtils.putString(SHARED_BOOK_SPEECH_SPEAKER, speaker);
    }

    public String getSpeechSpeed() {
        return sharedPreUtils.getString(SHARED_BOOK_SPEECH_SPEED);
    }

    public void setSpeechSpeed(String speed) {
        sharedPreUtils.putString(SHARED_BOOK_SPEECH_SPEED, speed);
    }

//    public String getSpeechVolume() {
//        return sharedPreUtils.getString(SHARED_BOOK_SPEECH_VOLUME);
//    }
//
//    public void setSpeechVolume(String volume) {
//        sharedPreUtils.putString(SHARED_BOOK_SPEECH_VOLUME, volume);
//    }
//
//    public String getSpeechPitch() {
//        return sharedPreUtils.getString(SHARED_BOOK_SPEECH_PITCH);
//    }
//
//    public void setSpeechPitch(String pitch) {
//        sharedPreUtils.putString(SHARED_BOOK_SPEECH_PITCH, pitch);
//    }

    public int getAutoReadSpeed() {
        return sharedPreUtils.getInt(SHARED_LASTSET_READSPEED,15);
    }

    public void setAutoReadSpeed(int speed) {
        sharedPreUtils.putInt(SHARED_LASTSET_READSPEED, speed);
    }

    /****************字体设置****************/

    public boolean setSelectedFontWithUrl(String fontUrl) {
//        if (StringUtils.isNotBlank(fontUrl) || fontUrl.equals(Constant.READ_DEFAULT_FONTNAME) || ReadFontManager.getmInstance().isFontExist(fontUrl)) {
//            sharedPreUtils.putString(SHARED_SELECTED_FONT, fontUrl);
//            return true;
//        } else {
//            return false;
//        }
        return false;
    }

    public String getSelectedFontPath() {

//        String selectedFontUrl = sharedPreUtils.getString(SHARED_SELECTED_FONT);
//        if (!SystemUtils.isAdRemove()) {
//            if (NStringUtils.isNotBlank(selectedFontUrl) && !selectedFontUrl.equals(Constant.READ_DEFAULT_FONTNAME)){
//                setSelectedFontWithUrl(Constant.READ_DEFAULT_FONTNAME);
//            }
//            return Constant.READ_DEFAULT_FONTNAME;
//        }
//        if (NStringUtils.isBlank(selectedFontUrl) || selectedFontUrl.equals(Constant.READ_DEFAULT_FONTNAME)|| !ReadFontManager.getmInstance().isFontExist(selectedFontUrl)) {
//            setSelectedFontWithUrl(Constant.READ_DEFAULT_FONTNAME);
//            return Constant.READ_DEFAULT_FONTNAME;
//        }else {
//            String selectedFontPath = ReadFontManager.getmInstance().getFontFilePath(selectedFontUrl);
//            if (NStringUtils.isNotBlank(selectedFontPath)) {
//                return selectedFontPath;
//            }else {
//                return Constant.READ_DEFAULT_FONTNAME;
//            }
//        }

        return "default";
    }

    public String getSelectedFontUrl() {
//        if (!SystemUtils.isAdRemove()) {
//            return Constant.READ_DEFAULT_FONTNAME;
//        }
//        String selectedFontUrl = sharedPreUtils.getString(SHARED_SELECTED_FONT);
//        if (NStringUtils.isBlank(selectedFontUrl) || !ReadFontManager.getmInstance().isFontExist(selectedFontUrl)) {
//            selectedFontUrl = Constant.READ_DEFAULT_FONTNAME;
//            setSelectedFontWithUrl(selectedFontUrl);
//        }
//        return selectedFontUrl;

        return "default";
    }

    public void setPageTypefaceMode(int mTypeface) {
        sharedPreUtils.putInt(SHARED_READ_TYPEFACE_MODE, mTypeface);
    }



    public int getPageTypefaceMode() {
        return sharedPreUtils.getInt(SHARED_READ_TYPEFACE_MODE, 0);
    }

    public void setLineSize(int lineSize) {
        sharedPreUtils.putInt(SHARED_READ_LINE_SIZE, lineSize);
    }

    public int getLineSize() {
        return  sharedPreUtils.getInt(SHARED_READ_LINE_SIZE, 30);
    }


}