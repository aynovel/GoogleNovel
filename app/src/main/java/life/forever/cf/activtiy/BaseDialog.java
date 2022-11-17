package life.forever.cf.activtiy;


import static life.forever.cf.interfaces.PageMode.COVER;
import static life.forever.cf.interfaces.PageMode.SIMULATION;
import static life.forever.cf.interfaces.PageMode.VERTICAL_COVER;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.DataPointBean;
import life.forever.cf.entry.DataPointType;
import life.forever.cf.adapter.PageStyleAdapter;
import life.forever.cf.weight.PageLoader;
import life.forever.cf.interfaces.PageMode;
import life.forever.cf.interfaces.PageStyle;
import life.forever.cf.manage.ReadSettingManager;

import com.google.android.material.appbar.AppBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BaseDialog extends Dialog {

    @BindView(R.id.layout_setting)
    LinearLayout layout_setting;

    @BindView(R.id.smallerFont)
    ImageView smallerFont;
    @BindView(R.id.largerFont)
    ImageView largerFont;
    @BindView(R.id.line_small)
    ImageView line_small;
    @BindView(R.id.line_larger)
    ImageView line_larger;

    @BindView(R.id.bgColor1)
    TextView bgColor1;
    @BindView(R.id.bgColor2)
    TextView bgColor2;
    @BindView(R.id.bgColor3)
    TextView bgColor3;
    @BindView(R.id.bgColor4)
    TextView bgColor4;

    @BindView(R.id.read_setting_iv_brightness_minus)
    ImageView mIvBrightnessMinus;
    @BindView(R.id.read_setting_sb_brightness)
    SeekBar mSbBrightness;
    @BindView(R.id.read_setting_iv_brightness_plus)
    ImageView mIvBrightnessPlus;
    @BindView(R.id.read_setting_cb_brightness_auto)
    CheckBox mCbBrightnessAuto;
    @BindView(R.id.read_setting_typeface_a)
    RadioButton read_setting_typeface_a;
    @BindView(R.id.read_setting_typeface_b)
    RadioButton read_setting_typeface_b;

    @BindView(R.id.read_setting_rg_page_mode)
    RadioGroup mRgPageMode;

    @BindView(R.id.read_setting_rb_simulation)
    RadioButton mRbSimulation;
    @BindView(R.id.read_setting_rb_cover)
    RadioButton mRbCover;
    @BindView(R.id.read_setting_rb_slide)
    RadioButton mRbSlide;
    @BindView(R.id.read_setting_rb_scroll)
    RadioButton mRbScroll;
    @BindView(R.id.read_setting_rb_none)
    RadioButton mRbNone;
    @BindView(R.id.read_setting_rv_bg)
    RecyclerView mRvBg;

    /************************************/
    private PageStyleAdapter mPageStyleAdapter;
    private ReadSettingManager mSettingManager;
    private final PageLoader mPageLoader;
    private final LinearLayout mLlCatalog;
    private final LinearLayout mHeader;
    private final View mLine;
    private final AppBarLayout mAblTopMenu;
    private final LinearLayout mLlBottomMenu;
    private final Activity mActivity;

    private PageMode mPageMode;
    private PageStyle mPageStyle;

    private int mBrightness;
    private int mTextSize;

    private boolean isBrightnessAuto;
    private boolean isTextDefault;


    public BaseDialog(@NonNull Activity activity, PageLoader mPageLoader, LinearLayout ll_catalog, LinearLayout header
            , View v_line, AppBarLayout toolbar, LinearLayout LlBottomMenu) {
        super(activity, R.style.ReadSettingDialog);
        mActivity = activity;
        this.mPageLoader = mPageLoader;
        this.mLlCatalog = ll_catalog;
        this.mHeader = header;
        this.mLine = v_line;
        this.mAblTopMenu = toolbar;
        this.mLlBottomMenu = LlBottomMenu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_read_setting);
        ButterKnife.bind(this);
        setUpWindow();
        initData();
        initWidget();
        initClick();
    }

    //设置Dialog显示的位置
    private void setUpWindow() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    private void initData() {
        mSettingManager = ReadSettingManager.getInstance();
        isBrightnessAuto = mSettingManager.isBrightnessAuto();
        mBrightness = mSettingManager.getBrightness();
        mTextSize = mSettingManager.getTextSize();
        isTextDefault = mSettingManager.isDefaultTextSize();
        mPageMode = mSettingManager.getPageMode();
        mPageStyle = mSettingManager.getPageStyle();
    }

    private void initWidget() {
        mSbBrightness.setProgress(mBrightness);
        mCbBrightnessAuto.setChecked(isBrightnessAuto);
        initPageMode();
        if (mSettingManager.getPageTypefaceMode() == 1) {
            setRoboto();
        }else{
            setMerriweatherr();
        }
        if ( ((ReadActivity)mActivity).isNightMode ){
            setBGColor(4);
        }else{
            setBGColor(mPageStyle.ordinal());
        }

        read_setting_typeface_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMerriweatherr();
                ReadSettingManager.getInstance().setPageTypefaceMode(0);
                mPageLoader.setReadFont();

            }
        });
        read_setting_typeface_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRoboto();
                ReadSettingManager.getInstance().setPageTypefaceMode(1);
                mPageLoader.setReadFont();

            }
        });
        mRbCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSmoothness();
                mPageMode = COVER;
                mPageLoader.setPageMode(mPageMode);

                DataPointBean settingPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_menu_setting");
                settingPagePoint.setReadDataPoint(null,null
                        ,0,3,0,null);

            }
        });
        mRbScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setScroll();
                mPageMode = VERTICAL_COVER;
                mPageLoader.setPageMode(mPageMode);

                DataPointBean settingPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_menu_setting");
                settingPagePoint.setReadDataPoint(null,null
                        ,0,1,0,null);

            }
        });
        mRbSimulation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setSimulate();
                mPageMode = SIMULATION;
                mPageLoader.setPageMode(mPageMode);

                DataPointBean settingPagePoint = new DataPointBean(DataPointType.ReadingAciton,"event_menu_setting");
                settingPagePoint.setReadDataPoint(null,null
                        ,0,2,0,null);

            }
        });
    }

    private void initPageMode() {
        switch (mSettingManager.getPageMode()) {
            case SIMULATION:
                setSimulate();
                break;
            case COVER:
                setSmoothness();
                break;
            case VERTICAL_COVER:
                setScroll();
                break;
        }
    }

    private Drawable getDrawable(int drawRes) {
        return ContextCompat.getDrawable(getContext(), drawRes);
    }


    @SuppressLint("ResourceAsColor")
    private void initClick() {
        //亮度调节
        mIvBrightnessMinus.setOnClickListener(
                (v) -> {
                    if (mCbBrightnessAuto.isChecked()) {
                        mCbBrightnessAuto.setChecked(false);
                    }
                    int progress = mSbBrightness.getProgress() - 1;
                    if (progress < 0) return;
                    mSbBrightness.setProgress(progress);
                    BrightnessUtils.setBrightness(mActivity, progress);
                }
        );
        mIvBrightnessPlus.setOnClickListener(
                (v) -> {
                    if (mCbBrightnessAuto.isChecked()) {
                        mCbBrightnessAuto.setChecked(false);
                    }
                    int progress = mSbBrightness.getProgress() + 1;
                    if (progress > mSbBrightness.getMax()) return;
                    mSbBrightness.setProgress(progress);
                    BrightnessUtils.setBrightness(mActivity, progress);
                    //设置进度
                    ReadSettingManager.getInstance().setBrightness(progress);
                }
        );

        mSbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (mCbBrightnessAuto.isChecked()) {
                    mCbBrightnessAuto.setChecked(false);
                }
                //设置当前 Activity 的亮度
                BrightnessUtils.setBrightness(mActivity, progress);
                //存储亮度的进度条
                ReadSettingManager.getInstance().setBrightness(progress);
            }
        });

        mCbBrightnessAuto.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        //获取屏幕的亮度
                        BrightnessUtils.setBrightness(mActivity, BrightnessUtils.getScreenBrightness(mActivity));
                    } else {
                        //获取进度条的亮度
                        BrightnessUtils.setBrightness(mActivity, mSbBrightness.getProgress());
                    }
                    ReadSettingManager.getInstance().setAutoBrightness(isChecked);
                }
        );


    }

    //字体变小
    @OnClick(R.id.smallerFont)
    void onSmallerClick() {
        int fontSize = mSettingManager.getTextSize();
        largerFont.setImageResource(R.drawable.add_font);
        smallerFont.setImageResource(R.drawable.reduce_the_font);
        if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(14)) {
            smallerFont.setImageResource(R.drawable.reduce_the_font_black);
            return;
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(16)) {
            fontSize = ScreenUtils.dpToPx(14);
            mPageLoader.setLineSize(22);
            smallerFont.setImageResource(R.drawable.reduce_the_font_black);
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(18)) {
            fontSize = ScreenUtils.dpToPx(16);
            mPageLoader.setLineSize(24);
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(20)) {
            fontSize = ScreenUtils.dpToPx(18);
            mPageLoader.setLineSize(27);
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(22)) {
            fontSize = ScreenUtils.dpToPx(20);
            mPageLoader.setLineSize(30);
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(24)) {
            fontSize = ScreenUtils.dpToPx(22);
            mPageLoader.setLineSize(33);
        }
        mPageLoader.setTextSize(fontSize);
    }

    //字体变大
    @OnClick(R.id.largerFont)
    void onLineLargerClick() {
        largerFont.setImageResource(R.drawable.add_font);
        smallerFont.setImageResource(R.drawable.reduce_the_font);
        int fontSize = mSettingManager.getTextSize();
        if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(14)) {
            fontSize = ScreenUtils.dpToPx(16);
            mPageLoader.setLineSize(25);

        }else if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(16)) {
            fontSize = ScreenUtils.dpToPx(18);
            mPageLoader.setLineSize(27);
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(18)) {
            fontSize = ScreenUtils.dpToPx(20);
            mPageLoader.setLineSize(30);
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(20)) {
            fontSize = ScreenUtils.dpToPx(22);
            mPageLoader.setLineSize(33);
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(22)) {
            fontSize = ScreenUtils.dpToPx(24);
            mPageLoader.setLineSize(36);
            largerFont.setImageResource(R.drawable.add_font_black);
        }
        else
            if (mSettingManager.getTextSize() ==  ScreenUtils.dpToPx(24)) {
            largerFont.setImageResource(R.drawable.add_font_black);
            return;
        }
        mPageLoader.setTextSize(fontSize);
    }

    //行间距变小
    @OnClick(R.id.line_small)
    void onLineSmallClick() {
        //最小值
        largerFont.setImageResource(R.drawable.add_font);
        smallerFont.setImageResource(R.drawable.reduce_the_font);
        line_small.setImageResource(R.drawable.line_jian);
        line_larger.setImageResource(R.drawable.line_add);
        int minimum = 20;
        if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(13)) {
            minimum = 20;

        }else if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(15)) {
            minimum = 22;
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(17)) {
            minimum = 25;
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(19)) {
            minimum = 28;
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(21)) {
            minimum = 31;
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(23)) {
            minimum = 34;
        }
        int mLineSpacing =  mSettingManager.getLineSize() - 1;
        if (mLineSpacing < minimum){
            line_small.setImageResource(R.drawable.line_jian_black);
            return;
        }
        if (mLineSpacing == minimum){
            line_small.setImageResource(R.drawable.line_jian_black);
        }

        mPageLoader.setLineSize(mLineSpacing);
    }


    //行间距变大
    @OnClick(R.id.line_larger)
    void onLargerClick(){
        largerFont.setImageResource(R.drawable.add_font);
        smallerFont.setImageResource(R.drawable.reduce_the_font);
        line_small.setImageResource(R.drawable.line_jian);
        line_larger.setImageResource(R.drawable.line_add);
        //最大值
        int maximum = 38;
        if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(13)) {
            maximum = 24;

        }else if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(15)) {
            maximum = 26;
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(17)) {
            maximum = 29;
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(19)) {
            maximum = 32;
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(21)) {
            maximum = 35;
        }
        else if (mSettingManager.getTextSize() ==  ScreenUtils.spToPx(23)) {
            maximum = 38;
        }
        int mLineSpacing =  mSettingManager.getLineSize() + 1;
        if (mLineSpacing > maximum) {
            line_larger.setImageResource(R.drawable.line_add_black);
            return;
        }
        if (mLineSpacing == maximum) {
            line_larger.setImageResource(R.drawable.line_add_black);
        }
        mPageLoader.setLineSize(mLineSpacing);
    }


    //背景切换
    @OnClick(R.id.bgColor1)
    void onBgColor1Click() {
        setBGColor(0);
    }

    @OnClick(R.id.bgColor2)
    void onBgColor2Click() {
        setBGColor(1);
    }

    @OnClick(R.id.bgColor3)
    void onBgColor3Click() {
        setBGColor(2);
    }

    @OnClick(R.id.bgColor4)
    void onBgColor4Click() {
        setBGColor(3);
    }

    public boolean isBrightFollowSystem() {
        if (mCbBrightnessAuto == null) {
            return false;
        }
        return mCbBrightnessAuto.isChecked();
    }


    /**
     * 设置阅读器滑动模式-平移
     */
    private void setSmoothness() {
        mRbCover.setBackgroundResource(R.drawable.shape_theme_corner_20dp);
        mRbCover.setTextColor(mActivity.getResources().getColor(R.color.theme_color));
        mRbScroll.setBackgroundResource(R.drawable.shape_ebeced_corner_20dp);
        mRbSimulation.setBackgroundResource(R.drawable.shape_ebeced_corner_20dp);
        setnightModeTextColor(mRbScroll);
        setnightModeTextColor(mRbSimulation);
    }

    /**
     * 设置阅读器滑动模式-垂直滑动
     */
    private void setScroll() {
        mRbScroll.setBackgroundResource(R.drawable.shape_theme_corner_20dp);
        mRbScroll.setTextColor(mActivity.getResources().getColor(R.color.theme_color));
        mRbCover.setBackgroundResource(R.drawable.shape_ebeced_corner_20dp);
        mRbSimulation.setBackgroundResource(R.drawable.shape_ebeced_corner_20dp);
        setnightModeTextColor(mRbCover);
        setnightModeTextColor(mRbSimulation);
    }

    /**
     * 设置阅读器滑动模式-模拟翻页
     */
    private void setSimulate() {
        mRbSimulation.setBackgroundResource(R.drawable.shape_theme_corner_20dp);
        mRbSimulation.setTextColor(mActivity.getResources().getColor(R.color.theme_color));
        mRbCover.setBackgroundResource(R.drawable.shape_ebeced_corner_20dp);
        mRbScroll.setBackgroundResource(R.drawable.shape_ebeced_corner_20dp);
        setnightModeTextColor(mRbCover);
        setnightModeTextColor(mRbScroll);
    }

    /**
     * 设置阅读器字体-merriweatherr
     */
    private void setMerriweatherr() {
        read_setting_typeface_a.setBackgroundResource(R.drawable.shape_theme_corner_20dp);
        read_setting_typeface_a.setTextColor(mActivity.getResources().getColor(R.color.theme_color));
        read_setting_typeface_b.setBackgroundResource(R.drawable.shape_ebeced_corner_20dp);
        setnightModeTextColor(read_setting_typeface_b);
    }

    /**
     * 设置阅读器滑动模式-roboto
     */
    private void setRoboto() {
        read_setting_typeface_b.setBackgroundResource(R.drawable.shape_theme_corner_20dp);
        read_setting_typeface_b.setTextColor(mActivity.getResources().getColor(R.color.theme_color));
        read_setting_typeface_a.setBackgroundResource(R.drawable.shape_ebeced_corner_20dp);
        setnightModeTextColor(read_setting_typeface_a);
    }

    /**
     * 根据夜间模式判断夜间模式
     * @param mTextView
     */
     public void setnightModeTextColor(TextView mTextView){
         if (mSettingManager.isNightMode()){
             mTextView.setTextColor(mActivity.getResources().getColor(R.color.color_656667));
         }else{
             mTextView.setTextColor(mActivity.getResources().getColor(R.color.color_000001));
         }
    }
    /**
     * 设置阅读器背景
     */
    public void setBGColor(int type) {
        if (mIvBrightnessMinus == null){
            show();
            dismiss();
        }

        mIvBrightnessMinus.setImageResource(R.drawable.hhdf);
        mIvBrightnessPlus.setImageResource(R.drawable.rtth);
        if (mPageMode == COVER){
            mRbCover.setTextColor(mActivity.getResources().getColor(R.color.theme_color));
            mRbScroll.setTextColor(mActivity.getResources().getColor(R.color.color_000001));
            mRbSimulation.setTextColor(mActivity.getResources().getColor(R.color.color_000001));
        }else if (mPageMode == VERTICAL_COVER){
            mRbCover.setTextColor(mActivity.getResources().getColor(R.color.color_000001));
            mRbScroll.setTextColor(mActivity.getResources().getColor(R.color.theme_color));
            mRbSimulation.setTextColor(mActivity.getResources().getColor(R.color.color_000001));
        }else if(mPageMode == SIMULATION) {
            mRbCover.setTextColor(mActivity.getResources().getColor(R.color.color_000001));
            mRbScroll.setTextColor(mActivity.getResources().getColor(R.color.color_000001));
            mRbSimulation.setTextColor(mActivity.getResources().getColor(R.color.theme_color));
        }

        if (mSettingManager.getPageTypefaceMode() == 0){
            read_setting_typeface_a.setTextColor(mActivity.getResources().getColor(R.color.theme_color));
            read_setting_typeface_b.setTextColor(mActivity.getResources().getColor(R.color.color_000001));
        }else{
            read_setting_typeface_b.setTextColor(mActivity.getResources().getColor(R.color.theme_color));
            read_setting_typeface_a.setTextColor(mActivity.getResources().getColor(R.color.color_000001));
        }
        switch (type){

            case 0:
                mPageLoader.setNightMode(false);
                ((ReadActivity)mActivity).isNightMode = false;
                ((ReadActivity)mActivity).setNightMode(false);
                bgColor1.setBackgroundResource(R.drawable.shape_theme_corner_12dp);
                bgColor2.setBackgroundResource(R.drawable.shape_white_no_corner_f3e7ce_12dp);
                bgColor3.setBackgroundResource(R.drawable.shape_white_no_corner_cbd9e5_12dp);
                bgColor4.setBackgroundResource(R.drawable.shape_white_no_corner_d6e4cc_12dp);
                mPageLoader.setPageStyle(PageStyle.values()[0]);
                mLlCatalog.setBackgroundResource(R.color.color_FEFFFF);
                mLlBottomMenu.setBackgroundResource(R.color.color_FEFFFF);
                mAblTopMenu.setBackgroundResource(R.color.color_FEFFFF);
                layout_setting.setBackgroundResource(R.color.color_FEFFFF);
                mHeader.setBackgroundResource(R.color.color_FCF0D5);
                mLine.setBackgroundResource(R.color.color_FCF0D5);
                break;
            case 1:

                mPageLoader.setNightMode(false);
                ((ReadActivity)mActivity).isNightMode = false;

                ((ReadActivity)mActivity).setNightMode(false);
                bgColor1.setBackgroundResource(R.drawable.shape_no_theme_corner_12dp);
                bgColor2.setBackgroundResource(R.drawable.shape_white_corner_f3e7ce_12dp);
                bgColor3.setBackgroundResource(R.drawable.shape_white_no_corner_cbd9e5_12dp);
                bgColor4.setBackgroundResource(R.drawable.shape_white_no_corner_d6e4cc_12dp);
                mPageLoader.setPageStyle(PageStyle.values()[1]);
                mLlCatalog.setBackgroundResource(R.color.color_FCF0D5);
                mAblTopMenu.setBackgroundResource(R.color.color_FCF0D5);
                mLlBottomMenu.setBackgroundResource(R.color.color_FCF0D5);
                layout_setting.setBackgroundResource(R.color.color_FCF0D5);
                mHeader.setBackgroundResource(R.color.color_F3E7CE);
                mLine.setBackgroundResource(R.color.color_F3E7CE);
                break;
            case 2:
                mPageLoader.setNightMode(false);
                ((ReadActivity)mActivity).isNightMode = false;
                ((ReadActivity)mActivity).setNightMode(false);
                bgColor1.setBackgroundResource(R.drawable.shape_no_theme_corner_12dp);
                bgColor2.setBackgroundResource(R.drawable.shape_white_no_corner_f3e7ce_12dp);
                bgColor3.setBackgroundResource(R.drawable.shape_white_corner_cbd9e5_12dp);
                bgColor4.setBackgroundResource(R.drawable.shape_white_no_corner_d6e4cc_12dp);
                mPageLoader.setPageStyle(PageStyle.values()[2]);
                mLlCatalog.setBackgroundResource(R.color.color_CBD9E5);
                mAblTopMenu.setBackgroundResource(R.color.color_CBD9E5);
                mLlBottomMenu.setBackgroundResource(R.color.color_CBD9E5);
                layout_setting.setBackgroundResource(R.color.color_CBD9E5);
                mHeader.setBackgroundResource(R.color.color_D5E1EC);
                mLine.setBackgroundResource(R.color.color_D5E1EC);
                break;
            case 3:
                mPageLoader.setNightMode(false);
                ((ReadActivity)mActivity).isNightMode = false;
                ((ReadActivity)mActivity).setNightMode(false);
                bgColor1.setBackgroundResource(R.drawable.shape_no_theme_corner_12dp);
                bgColor2.setBackgroundResource(R.drawable.shape_white_no_corner_f3e7ce_12dp);
                bgColor3.setBackgroundResource(R.drawable.shape_white_no_corner_cbd9e5_12dp);
                bgColor4.setBackgroundResource(R.drawable.shape_white_corner_d6e4cc_12dp);
                mPageLoader.setPageStyle(PageStyle.values()[3]);
                mLlCatalog.setBackgroundResource(R.color.color_E1EDD7);
                mAblTopMenu.setBackgroundResource(R.color.color_E1EDD7);
                mLlBottomMenu.setBackgroundResource(R.color.color_E1EDD7);
                layout_setting.setBackgroundResource(R.color.color_E1EDD7);
                mHeader.setBackgroundResource(R.color.color_D6E4CC);
                mLine.setBackgroundResource(R.color.color_D6E4CC);
                break;
            case 4:
                if (mPageMode == COVER){
                    setSmoothness();
                }else if (mPageMode == VERTICAL_COVER) {
                    setScroll();
                }else if (mPageMode == SIMULATION)
                {
                    setSimulate();
                }
                if (mSettingManager.getPageTypefaceMode() == 0){
                    read_setting_typeface_b.setTextColor(mActivity.getResources().getColor(R.color.color_656667));
                }else{
                    read_setting_typeface_a.setTextColor(mActivity.getResources().getColor(R.color.color_656667));
                }
                mIvBrightnessMinus.setImageResource(R.drawable.brightness_dark_black);
                mIvBrightnessPlus.setImageResource(R.drawable.hhdfd);
                mLlCatalog.setBackgroundResource(R.color.color_000001);
                mAblTopMenu.setBackgroundResource(R.color.color_000001);
                mLlBottomMenu.setBackgroundResource(R.color.color_000001);
                layout_setting.setBackgroundResource(R.color.color_000001);
                mHeader.setBackgroundResource(R.color.color_1E1F1F);
                mLine.setBackgroundResource(R.color.color_1E1F1F);
                break;

        }

        ((ReadActivity)mActivity).changeRootBackBG();

    }




}
