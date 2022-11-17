package life.forever.cf.adapter.person.pay;

import org.json.JSONObject;


public interface RechargeCallback {


    void onResult(int channel,int channel_child ,int ruleId, String iapid,double cash, int counts, JSONObject custom);
}
