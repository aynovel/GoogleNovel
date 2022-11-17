package life.forever.cf.entry;

import android.graphics.Color;
import android.os.Environment;

import life.forever.cf.datautils.FileUtils;

import java.io.Serializable;


public class ISListConfig implements Serializable {

    public boolean needCrop;


    public boolean multiSelect = false;


    public boolean rememberSelected = true;


    public int maxNum = 9;


    public boolean needCamera;

    public int statusBarColor = -1;

    public int backResId = -1;


    public String title;


    public int titleColor;


    public int titleBgColor;

    public String btnText;


    public int btnTextColor;


    public int btnBgColor;

    public String allImagesText;


    public String filePath;


    public int aspectX = 1;
    public int aspectY = 1;
    public int outputX = 500;
    public int outputY = 500;

    public ISListConfig(Builder builder) {
        this.needCrop = builder.needCrop;
        this.multiSelect = builder.multiSelect;
        this.rememberSelected = builder.rememberSelected;
        this.maxNum = builder.maxNum;
        this.needCamera = builder.needCamera;
        this.statusBarColor = builder.statusBarColor;
        this.backResId = builder.backResId;
        this.title = builder.title;
        this.titleBgColor = builder.titleBgColor;
        this.titleColor = builder.titleColor;
        this.btnText = builder.btnText;
        this.btnBgColor = builder.btnBgColor;
        this.btnTextColor = builder.btnTextColor;
        this.allImagesText = builder.allImagesText;
        this.filePath = builder.filePath;
        this.aspectX = builder.aspectX;
        this.aspectY = builder.aspectY;
        this.outputX = builder.outputX;
        this.outputY = builder.outputY;
    }

    public static class Builder implements Serializable {

        private boolean needCrop = false;
        private boolean multiSelect = true;
        private boolean rememberSelected = true;
        private int maxNum = 9;
        private boolean needCamera = true;
        public int statusBarColor = -1;
        private int backResId = -1;
        private String title;
        private int titleColor;
        private int titleBgColor;
        private String btnText;
        private int btnTextColor;
        private int btnBgColor;
        private String allImagesText;
        private String filePath;

        private int aspectX = 1;
        private int aspectY = 1;
        private int outputX = 400;
        private int outputY = 400;

        public Builder() {

            if (FileUtils.isSdCardAvailable())
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Camera";
            else
                filePath = Environment.getRootDirectory().getAbsolutePath() + "/Camera";

            title = "照片";
            titleBgColor = Color.parseColor("#3F51B5");
            titleColor = Color.WHITE;

            btnText = "确定";
            btnBgColor = Color.TRANSPARENT;
            btnTextColor = Color.WHITE;

            allImagesText = "所有图片";

            FileUtils.createDir(filePath);
        }

        public Builder needCrop(boolean needCrop) {
            this.needCrop = needCrop;
            return this;
        }

        public Builder multiSelect(boolean multiSelect) {
            this.multiSelect = multiSelect;
            return this;
        }

        public Builder rememberSelected(boolean rememberSelected) {
            this.rememberSelected = rememberSelected;
            return this;
        }

        public Builder maxNum(int maxNum) {
            this.maxNum = maxNum;
            return this;
        }

        public Builder needCamera(boolean needCamera) {
            this.needCamera = needCamera;
            return this;
        }

        public Builder statusBarColor(int statusBarColor) {
            this.statusBarColor = statusBarColor;
            return this;
        }

        public Builder backResId(int backResId) {
            this.backResId = backResId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder titleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public Builder titleBgColor(int titleBgColor) {
            this.titleBgColor = titleBgColor;
            return this;
        }

        public Builder btnText(String btnText) {
            this.btnText = btnText;
            return this;
        }

        public Builder btnTextColor(int btnTextColor) {
            this.btnTextColor = btnTextColor;
            return this;
        }

        public Builder btnBgColor(int btnBgColor) {
            this.btnBgColor = btnBgColor;
            return this;
        }

        public Builder allImagesText(String allImagesText) {
            this.allImagesText = allImagesText;
            return this;
        }

        private Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder cropSize(int aspectX, int aspectY, int outputX, int outputY) {
            this.aspectX = aspectX;
            this.aspectY = aspectY;
            this.outputX = outputX;
            this.outputY = outputY;
            return this;
        }

        public ISListConfig build() {
            return new ISListConfig(this);
        }
    }
}
