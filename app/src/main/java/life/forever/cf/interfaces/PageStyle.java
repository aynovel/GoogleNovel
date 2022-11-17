package life.forever.cf.interfaces;

import androidx.annotation.ColorRes;

import life.forever.cf.R;

public enum PageStyle {




    BG_0(R.color.nb_read_font_1, R.color.color_FEFFFF),
    BG_1(R.color.nb_read_font_1, R.color.color_F3E7CE),
    BG_2(R.color.nb_read_font_1, R.color.color_CBD9E5),
    BG_3(R.color.nb_read_font_1, R.color.color_D6E4CC),
    BG_4(R.color.colorWhite, R.color.nb_read_bg_5),
    NIGHT(R.color.colorWhite, R.color.nb_read_bg_night),;

    private final int fontColor;
    private final int bgColor;

    PageStyle(@ColorRes int fontColor, @ColorRes int bgColor) {
        this.fontColor = fontColor;
        this.bgColor = bgColor;
    }

    public int getFontColor() {
        return fontColor;
    }

    public int getBgColor() {
        return bgColor;
    }


    public int getBatteryColor() {
        return fontColor;
    }

}
