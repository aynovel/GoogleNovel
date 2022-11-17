package life.forever.cf.publics.net;

import org.json.JSONObject;


public interface OkHttpResult {


    void onSuccess(JSONObject data);

    void onFailure(String error);
}
