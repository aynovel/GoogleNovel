package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class BookMoreBuyInfoPackage extends BasePackageBean {
    @SerializedName("ResultData")
    public BookMoreAllInfoResult result;

    public class BookMoreAllInfoResult{
        public BookMoreBuyInfoResult info;

        public String status;
        public String msg;
    }
}
