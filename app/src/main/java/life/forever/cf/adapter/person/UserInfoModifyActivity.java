package life.forever.cf.adapter.person;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.entry.AppUser;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.CustomToast;
import life.forever.cf.publics.tool.DisplayUtil;
import life.forever.cf.publics.tool.GenderDialog;
import life.forever.cf.publics.tool.GlideUtil;
import life.forever.cf.publics.tool.ImageUtil;
import life.forever.cf.publics.tool.JSONUtil;
import life.forever.cf.publics.tool.OssUtil;
import life.forever.cf.publics.tool.SharedPreferencesUtil;
import life.forever.cf.publics.weight.RadiusImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserInfoModifyActivity extends BaseActivity {

    private final int REQUEST_TAKE_PHOTO = ONE_THOUSAND;
    private final int REQUEST_GALLERY = TWO_THOUSAND;
    private final int REQUEST_CROP = THREE_THOUSAND;

    @BindView(R.id.head)
    RadiusImageView mHead;
    @BindView(R.id.nickName)
    EditText mNickName;
    @BindView(R.id.sex)
    TextView mSex;
    @BindView(R.id.birthday)
    TextView mBirthday;

    private GenderDialog mGenderDialog;

    private final Calendar calendar = Calendar.getInstance();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private Uri photoUri;
    private File zoom;
    private Uri zoomUri;

    private String head;
    private int sex;

    @Override
    protected void initializeView() {
        setContentView(R.layout.activity_user_info_modify);
        ButterKnife.bind(this);
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(MINE_STRING_PERSONAL_INFO);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initializeData() {
        EventBus.getDefault().register(this);
        PlotRead.getAppUser().fetchUserInfo(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Message message) {
        switch (message.what) {
            case BUS_USER_INFO_SUCCESS:
                AppUser user = PlotRead.getAppUser();
                head = user.head;
                sex = user.sex;

                GlideUtil.load(context, head, R.drawable.logo_default_user, mHead);
                mNickName.setText(user.nickName);
                switch (sex) {
                    case ZERO:
                        mSex.setText(getString(R.string.unknown));
                        break;
                    case ONE:
                        mSex.setText(getString(R.string.boy_n));
                        break;
                    case TWO:
                        mSex.setText(getString(R.string.girl_n));
                        break;
                }
                if (!TextUtils.isEmpty(user.birthday)) {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    try {
                        Date date = sdf.parse(user.birthday);
                        if (date != null) {
                            String dateStr = format.format(date);
                            mBirthday.setText(dateStr);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    mBirthday.setText(user.birthday);
                }
//                mSignature.setText(user.signature);
                break;
            case BUS_USER_INFO_FAILURE:
                break;

        }
    }

    private final View.OnClickListener onBackClick = v -> onBackPressed();

    @OnClick(R.id.headItem)
    void onHeadItemClick() {
        infodialog("head");
    }

    @OnClick(R.id.sexItem)
    void onSexItemClick() {
        infodialog("gender");
    }

    @OnClick(R.id.birthdayItem)
    void onBirthdayItemClick() {
        final AlertDialog timedialog = new AlertDialog.Builder(this).create();
        timedialog.show();
        Window window = timedialog.getWindow();
        // *** ??????????????????????????????????????????.
        // ???????????????????????????,shrew_exit_dialog.xml???????????????view??????
        if (window != null) {
            window.setContentView(R.layout.time_dialog);
            // ???????????????????????????,????????????????????????
            DatePicker dp = window.findViewById(R.id.dpPicker);
            // ????????????View
            ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.VISIBLE);
            dp.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), (view, year, monthOfYear, dayOfMonth) -> {
                // ???????????????????????????????????????????????????????????????
                calendar.set(year, monthOfYear, dayOfMonth);

            });
            TextView mTvConfirm = window.findViewById(R.id.tv_confirm);
            mTvConfirm.setOnClickListener(v -> {
                String datea = format.format(calendar.getTime());
                mBirthday.setText(datea);
                timedialog.cancel();
            });
            // ??????alert????????????
            TextView mTvCancle = window.findViewById(R.id.tv_cancle);
            mTvCancle.setOnClickListener(v -> timedialog.cancel());
        }
    }

//    @OnClick(R.id.signatureItem)
//    void onSignatureItemClick() {
//        aiyeEditorDialog.show(context, mSignature.getText().toString(), onSignatureEditListener);
//    }

    @OnClick(R.id.submit)
    void onSubmitClick() {
        AppUser user = PlotRead.getAppUser();
        if (zoom == null && // ???????????????
                mNickName.getText().toString().equals(user.nickName) && // ????????????
                sex == user.sex && // ????????????
                mBirthday.getText().toString().equals(user.birthday)) {  // ????????????
            PlotRead.toast(PlotRead.INFO, getString(R.string.personal_particulars_unchanged));
            return;
        }
        if (zoom != null) { // ????????????
            showLoading(getString(R.string.photo_uploading));
            OssUtil.with(this).post(zoom.getAbsolutePath(), getHeadOssUrl(), ossCallback);
        } else { // ????????????
            submitModify();
        }
    }

    private void infodialog(String type) {
        mGenderDialog = new GenderDialog(this, type, R.style.CustomDialog, view -> {
            switch (view.getId()) {
                case R.id.tv_male:
                    mGenderDialog.dismiss();
                    if ("gender".equals(type)) {
                        mSex.setText(getString(R.string.boy_n));
                        sex = 1;
                    } else {
                        takePhoto();
                    }

                    break;
                case R.id.tv_female:
                    mGenderDialog.dismiss();
                    if ("gender".equals(type)) {
                        mSex.setText(getString(R.string.girl_n));
                        sex = 2;
                    } else {
                        chooseFromGallery();
                    }

                    break;
                case R.id.tv_cancel:
                    mGenderDialog.dismiss();
                    break;
            }
        });

        mGenderDialog.show();
        Window dialogWindow = mGenderDialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams params = mGenderDialog.getWindow().getAttributes();
            //??????dialog???????????????????????????,????????????????????????!!
            mGenderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mGenderDialog.getWindow().setAttributes(params);
        }

    }

    /**
     * ??????????????????
     */
    private void submitModify() {
        showLoading(getString(R.string.info_update));
        String Times = mBirthday.getText().toString();
        String Birthday = Times.replaceAll("[[\\s-:punct:]]", "");
        NetRequest.modifyUserInfo(head, mNickName.getText().toString(), sex, Birthday, "", new OkHttpResult() {

            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    JSONObject result = JSONUtil.getJSONObject(data, "ResultData");
                    int status = JSONUtil.getInt(result, "status");
                    if (status == ONE) {
                        JSONObject base = JSONUtil.getJSONObject(result, "base");
                        AppUser user = PlotRead.getAppUser();
                        // ????????????????????????
                        user.head = JSONUtil.getString(base, "avatar_url");
                        user.nickName = JSONUtil.getString(base, "nickname");
                        user.sex = JSONUtil.getInt(base, "sex");
                        user.birthday = JSONUtil.getString(base, "birthday");
                        user.signature = JSONUtil.getString(base, "signature");
                        // ????????????
                        SharedPreferencesUtil.putString(user.config, KEY_HEAD, user.head);
                        SharedPreferencesUtil.putString(user.config, KEY_NICKNAME, user.nickName);
                        SharedPreferencesUtil.putInt(user.config, KEY_SEX, user.sex);
                        SharedPreferencesUtil.putString(user.config, KEY_BIRTHDAY, user.birthday);
                        SharedPreferencesUtil.putString(user.config, KEY_SIGNATURE, user.signature);
                        // ????????????????????????
                        SharedPreferencesUtil.putInt(PlotRead.getConfig(), SEX, user.sex);
                        // ????????????
                        Message message = Message.obtain();
                        message.what = BUS_MODIFY_INFO_SUCCESS;
                        EventBus.getDefault().post(message);
                        PlotRead.toast(PlotRead.SUCCESS, getString(R.string.updata_success));
                        onBackPressed();
                    } else {
                        String msg = JSONUtil.getString(result, "msg");
                        PlotRead.toast(PlotRead.INFO, getString(R.string.no_internet));
                    }
                } else {
                    NetRequest.error(UserInfoModifyActivity.this, serverNo);
                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                PlotRead.toast(PlotRead.FAIL, context.getString(R.string.no_internet));
            }
        });
    }

    /*
     * ????????????????????????????????????
     * @return
     */
    private String getHeadOssUrl() {
        int uid = PlotRead.getAppUser().uid;
        int mole = uid % TEN;
        Random ran = new Random(System.currentTimeMillis());
        int i = ran.nextInt(ONE_THOUSAND);
        String path = "upload/logo/";
        path += mole + "/logo_" + uid + "_" + i + ".jpg";
        return path;
    }

    /*
     * ???????????????
     */
    private final OssUtil.OssCallback ossCallback = new OssUtil.OssCallback() {

        @Override
        public void onSuccess(String url) {
            head = url;
            dismissLoading();
            PlotRead.toast(PlotRead.SUCCESS, getString(R.string.upload_success));
            // ????????????
            submitModify();
        }

        @Override
        public void onFailure() {
            dismissLoading();
            PlotRead.toast(PlotRead.FAIL, getString(R.string.upload_fail));
        }
    };


    /*
     * ????????????????????????
     */
//    private aiyeEditorDialog.OnEditCompletedListener onSignatureEditListener = new aiyeEditorDialog.OnEditCompletedListener() {
//
//        @Override
//        public void onCompleted(String result) {
//            result = TextCheckUtil.clearFeed(result);
//            mSignature.setText(result);
//        }
//    };

    /*
     * ??????
     */
    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
            } else {
                openCamera();
            }
        } else {
            openCamera();
        }
    }

    /*
     * ????????????
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(getPictureCacheDir(), System.currentTimeMillis() + ".jpg");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) { // 7.0????????????FileProvider
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photo);
        } else {
            photoUri = Uri.fromFile(photo);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    /*
     * ????????????
     */
    @SuppressLint("IntentReset")
    private void chooseFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    /*
     * ????????????
     */
    private void zoomHead() {
        Intent intent = new Intent("com.android.camera.action.CROP"); //??????
        intent.setDataAndType(photoUri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // ??????
        intent.putExtra("crop", TRUE);
        intent.putExtra("scale", TRUE);
        intent.putExtra("return-data", FALSE);
        // ??????????????????
        intent.putExtra("aspectX", ONE);
        intent.putExtra("aspectY", ONE);
        // ????????????????????????
        intent.putExtra("outputX", DisplayUtil.dp2px(this, THIRTY));
        intent.putExtra("outputY", DisplayUtil.dp2px(this, THIRTY));

        zoom = new File(getPictureCacheDir(), System.currentTimeMillis() + ".jpg");
        zoomUri = Uri.parse("file://" + zoom.getAbsolutePath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, zoomUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, REQUEST_CROP); //?????????????????????????????????ImageView
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA) {
            if (null != grantResults && grantResults.length > 0 && grantResults[ZERO] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                CustomToast.showToast(this, getString(R.string.refused_camera));
//                PlotRead.toast(PlotRead.INFO, getString(R.string.refused_camera));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                zoomHead();
            }
        } else if (requestCode == REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                dealCrop(UserInfoModifyActivity.this);
                zoom = new File(zoomUri.getPath());
                if (zoom != null) {
                    GlideUtil.load(context, zoom.getAbsolutePath(), R.drawable.default_user_logo, mHead);
                }

            }
        } else if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK && data != null) {
                photoUri = data.getData();
                startActivityForResult(crop(UserInfoModifyActivity.this, photoUri, 200, 200, 1, 1), REQUEST_CROP);
            }
        }

    }

    /*
     * ????????????????????????100*100?????????????????????????????????1:1
     * @param activity Activity
     * @param uri      ?????????uri
     * @param w        ?????????
     * @param h        ?????????
     * @param aspectX  ?????????
     * @param aspectY  ?????????
     */
    public Intent crop(Activity activity, Uri uri, int w, int h, int aspectX, int aspectY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        if (aspectX != 0 && aspectX == aspectY) {
            /*??????????????????????????????????????????????????????????????????????????????????????????????????????*/
            if ("HUAWEI".equals(Build.MANUFACTURER)) {
                aspectX = 9998;
                aspectY = 9999;
            }
        }
        if (w != 0 && h != 0) {
            intent.putExtra("outputX", w);
            intent.putExtra("outputY", h);
        }
        if (aspectX != 0 || aspectY != 0) {
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
        }

        /*???????????????????????????*/
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);

        /*?????????????????????????????????????????????????????????*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        /*????????????miui????????????????????????????????????camera.action.CROP????????????????????????app?????????*/
        String pathName = new StringBuffer().append("file:///").append(ImageUtil.getImageCacheDir(activity)).append(File.separator)
                .append(System.currentTimeMillis()).append(".jpg").toString();
        zoomUri = Uri.parse(pathName);

//        zoom = new File(getPictureCacheDir(), pathName);

//        zoom = new File(zoomUri.getEncodedPath());
//        zoomUri = Uri.parse("file://" + zoom.getAbsolutePath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, zoomUri);//????????????(????????????????????????)
        // ????????????
        intent.putExtra("outputFormat", "JPEG");
        // ?????????????????????
        intent.putExtra("noFaceDetection", true);
        //????????????????????????Bitmap?????????
        intent.putExtra("return-data", false);
        return intent;
    }

    /*
     * ???????????????????????????????????????
     */
    public Bitmap dealCrop(Context context) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(zoomUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /*
     * ????????????????????????
     * @return
     */
    private File getPictureCacheDir() {
        File externalCacheDir = getExternalCacheDir();
        if (externalCacheDir == null) {
            externalCacheDir = getCacheDir();
        }
        return externalCacheDir;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
