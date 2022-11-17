package life.forever.cf.entry;

import com.google.gson.annotations.SerializedName;

public class UserDiscountTaskRewardPackage extends BasePackageBean{


    @SerializedName("ResultData")
    private UserDiscountTaskRewardResult result;

    public UserDiscountTaskRewardResult getResult() {
        return result;
    }

    public void setResult(UserDiscountTaskRewardResult result) {
        this.result = result;
    }


}
