package life.forever.cf.entry;

import android.os.Environment;

import life.forever.cf.datautils.FileUtils;

import java.io.Serializable;


public class ISCameraConfig implements Serializable {


    public boolean needCrop;


    public String filePath;


    public int aspectX = 1;
    public int aspectY = 1;
    public int outputX = 500;
    public int outputY = 500;

    public ISCameraConfig(Builder builder) {
        this.needCrop = builder.needCrop;
        this.filePath = builder.filePath;
        this.aspectX = builder.aspectX;
        this.aspectY = builder.aspectY;
        this.outputX = builder.outputX;
        this.outputY = builder.outputY;
    }

    public static class Builder implements Serializable {

        private boolean needCrop = false;
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

            FileUtils.createDir(filePath);
        }

        public Builder needCrop(boolean needCrop) {
            this.needCrop = needCrop;
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

        public ISCameraConfig build() {
            return new ISCameraConfig(this);
        }
    }
}
