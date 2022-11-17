package life.forever.cf.adapter.person.landing;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.AppUser;
import life.forever.cf.entry.BeanParser;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.Constant;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.DeepLinkUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.ScreenUtil;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import life.forever.cf.publics.weight.poputil.LoadingAlertDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements Constant {

    @BindView(R.id.img_back)
    ImageView mImgBack;
    @BindView(R.id.tvGoogleLast)
    TextView mTvGoogleLast;
    @BindView(R.id.tvFaceLast)
    TextView mTvFaceLast;
    protected AlertDialog loadingDialog;

    /**
     * googleLogin
     */
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 0x100;

    public static final int REQUEST_SIGN_IN_LOGIN = 1002;
    /**
     * facebookLogin
     */
    CallbackManager callbackManager;

    public FirebaseAnalytics mFirebaseAnalytics;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mImgBack.setOnClickListener(onBackClick);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        callbackManager = CallbackManager.Factory.create();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ScreenUtil.transparentStatusBar(this);
            ScreenUtil.setStatusBarDark(this, false);
        }
        initializeData();
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    public void initializeData() {
        googleLogin();
     int lastLogin =   SharedPreferencesUtil.getInt(Constant.APP,"lastLogin");

     if (lastLogin == 1){
         //1显示谷歌登陆
         mTvGoogleLast.setVisibility(View.VISIBLE);
     }else if (lastLogin == 2){
         //2显示facebook登陆
         mTvFaceLast.setVisibility(View.VISIBLE);
     }

    }

    /**
     * google
     */
    private GoogleSignInClient mGoogleSignInClient;

    private void googleLogin() {
        DeepLinkUtil.addPermanent(LoginActivity.this, "event_login_ga", "登录", "使用Google登录", "", "", "", "", "", "");
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestProfile()
                .requestEmail()
                .requestIdToken("658116322117-dh60d2h3u18r3gp7ho1jq589tvfma1f9.apps.googleusercontent.com")
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    /**
     * 获取用户信息
     */
    private void fetchUserInfo() {
        showLoading(getString(R.string.geting_info));
        NetRequest.getUserInfo(new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                mFirebaseAnalytics.setUserProperty("favorite_food", "1");
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    String order = JSONUtil.getString(result, "order_discount");
                    if (status == ONE) {
                        // 解析用户信息
                        BeanParser.parseUserInfo(result);
                        // 保存信息
                        AppUser user = PlotRead.getAppUser();
                        SharedPreferences config = user.config;
                        SharedPreferencesUtil.putString(config, KEY_HEAD, user.head);
                        SharedPreferencesUtil.putString(config, KEY_NICKNAME, user.nickName);
                        SharedPreferencesUtil.putInt(config, KEY_SEX, user.sex);
                        SharedPreferencesUtil.putInt(config, KEY_LEVEL, user.level);
                        SharedPreferencesUtil.putInt(config, KEY_VIP, user.vip);
                        SharedPreferencesUtil.putInt(config, KEY_SIGN_DAYS, user.signDays);
                        SharedPreferencesUtil.putString(config, KEY_SIGN_DATE, user.signDate);
                        SharedPreferencesUtil.putInt(config, KEY_MONEY, user.money);
                        SharedPreferencesUtil.putInt(config, KEY_VOUCHER, user.voucher);
                        SharedPreferencesUtil.putInt(config, KEY_MONTH_DATE, user.monthDate);
                        SharedPreferencesUtil.putBoolean(config, KEY_MONTH_VIP, user.monthVip);
                        SharedPreferencesUtil.putBoolean(config, KEY_MESSAGE_TAG, user.messageTag);
                        SharedPreferencesUtil.putInt(config, KEY_MESSAGE_TOTAL, user.messageTotal);
                        SharedPreferencesUtil.putString(config, KEY_BIRTHDAY, user.birthday);
                        SharedPreferencesUtil.putString(config, KEY_SIGNATURE, user.signature);
                        SharedPreferencesUtil.putString(config, KEY_DISCOUNT, order);


                        // 保存系统用户性别
                        SharedPreferencesUtil.putInt(PlotRead.getConfig(), SEX, user.sex);
                        // 发送用户信息请求成功的通知
                        Message message = Message.obtain();
                        message.what = BUS_USER_INFO_SUCCESS;
                        EventBus.getDefault().post(message);
                        // 关闭页面
                        finish();
                    } else {
                        cancelLogin();
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else { // 获取失败
                    cancelLogin();
                    NetRequest.error(LoginActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                cancelLogin();
                PlotRead.toast(PlotRead.FAIL, PlotRead.getApplication().getString(R.string.no_internet));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @OnClick(R.id.ll_google_login)
    void onGoogleClick() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.ll_facebook_login)
    void onFacebookClick() {
        DeepLinkUtil.addPermanent(LoginActivity.this, "event_login_fb", "登录", "使用FB登录", "", "", "", "", "", "");

        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), "facebook_login_success", Toast.LENGTH_SHORT).show();
                getLoginInfo(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "facebook_login_cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "facebook_login_error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.layout_service)
    void onServiceClick() {
        Intent intent = new Intent(getBaseContext(), ServiceTermsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.layout_private)
    void onPolicyClick() {
        Intent intent = new Intent(getBaseContext(), PrivatePolicyActivity.class);
        startActivity(intent);
    }


    public void getLoginInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (object != null) {
                    String id = object.optString("id");   //比如:1565455221565
                    String name = object.optString("name");  //比如：Zhang San
                    String gender = object.optString("gender");  //性别：比如 male （男）  female （女）
                    String emali = object.optString("email");  //邮箱：比如：56236545@qq.com

                    //获取用户头像
                    JSONObject object_pic = object.optJSONObject("picture");
                    JSONObject object_data = object_pic.optJSONObject("data");
                    String photo = object_data.optString("url");

                    //获取地域信息
                    String locale = object.optString("locale");   //zh_CN 代表中文简体
                    NetRequest.GoogleLogin(id, name, gender, photo, emali, "facebook"
                            , facebookAndGoogleCallback);
                    mFirebaseAnalytics.setUserProperty("login_user", "facebook");
                    SharedPreferencesUtil.putInt(Constant.APP,"lastLogin",2);
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,birthday,email,picture,locale,updated_time,timezone,age_range,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }


    private final OkHttpResult facebookAndGoogleCallback = new OkHttpResult() {

        @Override
        public void onSuccess(JSONObject data) {
            dismissLoading();
            String serverNo = JSONUtil.getString(data, "ServerNo");
            if (SN000.equals(serverNo)) {
                JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                int status = JSONUtil.getInt(result, "status");
                if (status == ONE) {
                    PlotRead.toast(PlotRead.SUCCESS, getString(R.string.login_seccess));
                    int uid = JSONUtil.getInt(result, "uid");
                    String token = JSONUtil.getString(result, "token");
                    int tokenTime = JSONUtil.getInt(result, "token_time");
                    String author = JSONUtil.getString(result, "author");
                    SharedPreferencesUtil.putInt(APP, LAST_ID, uid);
                    SharedPreferencesUtil.putString(USER + uid, KEY_TOKEN, token);
                    SharedPreferencesUtil.putInt(USER + uid, KEY_TOKEN_TIME, tokenTime);
                    SharedPreferencesUtil.putBoolean(USER + uid, Constant.KEY_IS_VISITOR, FALSE);
                    SharedPreferencesUtil.putString(USER + uid, KEY_AUTHOR, author);
                    // 保存登录方式
                    SharedPreferencesUtil.putInt(APP, LAST_LOGIN_WAY, TWO);
                    // 设置用户登录信息
                    PlotRead.getAppUser().notifyWhenLogin();
                    // 完善登录
                    PlotRead.toast(PlotRead.SUCCESS, getString(R.string.login_seccess));
                    completeLogin();
                } else {
                    String msg = JSONUtil.getString(result, "msg");
                    PlotRead.toast(PlotRead.FAIL, msg);
                }
            } else {
                NetRequest.error(LoginActivity.this, serverNo);
            }
        }

        @Override
        public void onFailure(String error) {
            dismissLoading();
            PlotRead.toast(PlotRead.FAIL, PlotRead.getApplication().getString(R.string.no_internet));
        }
    };


    /**
     * 获取用户信息完善登录
     */
    private void completeLogin() {
        if (LoginActivity.this == null || isDestroyed() || LoginActivity.this.isFinishing()) {
            return;
        }
        // 获取用户信息
        fetchUserInfo();
        // 发送登录通知
        Message message = Message.obtain();
        message.what = BUS_LOG_IN;
        EventBus.getDefault().post(message);
    }

    /**
     * 用户信息获取失败时取消登录
     */
    private void cancelLogin() {
        SharedPreferencesUtil.clear(PlotRead.getAppUser().config);
        SharedPreferencesUtil.remove(APP, LAST_ID);
        PlotRead.getAppUser().notifyWhenLogin();
        // 发送注销通知
        Message message = Message.obtain();
        message.what = BUS_LOG_OUT;
        EventBus.getDefault().post(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            } else {
                if (callbackManager != null) {
                    callbackManager.onActivityResult(requestCode, resultCode, data);
                }

            }
        }

    }

    /**
     * 谷歌登陆
     * @param result
     */
    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            NetRequest.GoogleLogin(account.getId(), account.getDisplayName(), "", account.getPhotoUrl() + "", account.getEmail(), "google", facebookAndGoogleCallback);
            mFirebaseAnalytics.setUserProperty("login_user", "google");
            SharedPreferencesUtil.putInt(Constant.APP,"lastLogin",1);
        }
    }

    /**
     * 展示loading弹窗
     *
     * @param tip
     */
    public void showLoading(String tip) {
        dismissLoading();
        loadingDialog = LoadingAlertDialog.show(this, tip);
    }

    /**
     * 隐藏loading弹窗
     */
    public void dismissLoading() {
        LoadingAlertDialog.dismiss(loadingDialog);
    }
}
