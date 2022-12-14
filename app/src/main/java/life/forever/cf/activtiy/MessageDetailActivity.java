package life.forever.cf.activtiy;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import life.forever.cf.R;
import life.forever.cf.entry.MsgDetailBean;
import life.forever.cf.internet.NetRequest;
import life.forever.cf.adapter.MessageDetailAdapter;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.net.OkHttpResult;
import life.forever.cf.publics.tool.CustomToast;
import life.forever.cf.publics.tool.DisplayUtil;
import life.forever.cf.publics.tool.GenderDialog;
import life.forever.cf.publics.tool.ImageUtil;
import life.forever.cf.publics.tool.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageDetailActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.layout_sent)
    LinearLayout mLayoutSent;

    @BindView(R.id.et_sent_info)
    EditText mEtSentInfo;
    @BindView(R.id.img_send)
    ImageView mImgSend;
    @BindView(R.id.noneView)
    View mNoneView;

    private MessageDetailAdapter mDiscoverMoreAdapter;
    private int mType;
    private List<MsgDetailBean.ResultData.list> contactList;

    private final int REQUEST_TAKE_PHOTO = ONE_THOUSAND;
    private final int REQUEST_GALLERY = TWO_THOUSAND;
    private final int REQUEST_CROP = THREE_THOUSAND;

    private GenderDialog mGenderDialog;
    private Uri photoUri;

    private File zoom;
    private Uri zoomUri;

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.back_icon);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_message_detail);
        ButterKnife.bind(this);
        mEtSentInfo.addTextChangedListener(textWatcher);
    }

    @Override
    protected void initializeData() {
        String mTitle = getIntent().getStringExtra("title");
        mType = getIntent().getIntExtra("type", ONE);
        mTitleBar.setMiddleText(mTitle);
        LinearLayoutManager EightLayoutManager = new LinearLayoutManager(context);
        EightLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(EightLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutSent.setVisibility(View.GONE);
//        if (mType == TWO) {
//            mLayoutSent.setVisibility(View.GONE);
//        } else {
//            mLayoutSent.setVisibility(View.VISIBLE);
//        }
        msglist();
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String mString = s.toString().trim();
            int count = 0;//??????????????????
            String mNewString = mString.replaceAll("\\s{1,}", " ");
            if (TextUtils.isEmpty(mNewString) || mNewString.equals("") || mNewString.equals(" ")) {
                count = 0;
            } else {
                count = 1;
            }
            for (int i = 0; i < mNewString.length(); i++) {
                char tem = mNewString.charAt(i);
                if (tem == ' ') // ??????
                {
                    count++;
                }
            }
            if (count >= ONE) {
                mImgSend.setImageResource(R.drawable.icon_mes_sented);
            } else {
                mImgSend.setImageResource(R.drawable.icon_mes_sent);
            }
        }
    };

    /**
     * ??????????????????????????????
     */
    private void msglist() {
        showLoading(getString(R.string.loading));
        NetRequest.msglistRequest(mType, new OkHttpResult() {
            @Override
            public void onSuccess(JSONObject data) {
                dismissLoading();
                String serverNo = JSONUtil.getString(data, "ServerNo");
                if (SN000.equals(serverNo)) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(data));

                        String resultString = jsonObject.getString("ResultData");
                        JSONObject listJson = new JSONObject(resultString);
                        String listString = listJson.getString("list");
                        Type listType = new TypeToken<List<MsgDetailBean.ResultData.list>>() {
                        }.getType();
                        Gson gson = new Gson();
                        contactList = gson.fromJson(listString, listType);
                        mDiscoverMoreAdapter = new MessageDetailAdapter(context, contactList);
                        mRecyclerView.setAdapter(mDiscoverMoreAdapter);
                        mRecyclerView.scrollToPosition(mDiscoverMoreAdapter.getItemCount() - 1);
                        switchPageBySize();
                        // ???????????????????????????????????????
                        Message message = Message.obtain();
                        message.what = BUS_MSG_NUM_CHANGE;
                        EventBus.getDefault().post(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(String error) {
                dismissLoading();
                switchPageBySize();
                PlotRead.toast(PlotRead.FAIL, "Request failed,Please try again later???");
            }
        });
    }

    /**
     * ???????????????????????????0?????????????????????
     */
    private void switchPageBySize() {
        if (contactList == null || contactList.size() == ZERO) {
            mNoneView.setVisibility(View.VISIBLE);
        } else {
            mNoneView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.img_photo)
    void onChoosePhoto() {
        chooseFromGallery();
        /*mGenderDialog = new GenderDialog(this, "", R.style.CustomDialog, view -> {
            switch (view.getId()) {
                case R.id.tv_male:
                    mGenderDialog.dismiss();
                    takePhoto();
                    break;
                case R.id.tv_female:
                    mGenderDialog.dismiss();
                    chooseFromGallery();
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
        }*/
    }

    @OnClick(R.id.img_send)
    void onSend() {
        CustomToast.showToast(this, getString(R.string.refused_camera));
    }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                zoomHead();
            }
        } else if (requestCode == REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                dealCrop(MessageDetailActivity.this);
                zoom = new File(zoomUri.getPath());
                if (zoom != null) {
//                    GlideUtil.load(context, zoom.getAbsolutePath(), R.drawable.default_user_logo, mHead);
                }

            }
        } else if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK && data != null) {
                photoUri = data.getData();
                startActivityForResult(crop(MessageDetailActivity.this, photoUri, 200, 200, 1, 1), REQUEST_CROP);
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
    protected void onResume() {
        super.onResume();
    }

    private final View.OnClickListener onBackClick = v -> onBackPressed();


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
