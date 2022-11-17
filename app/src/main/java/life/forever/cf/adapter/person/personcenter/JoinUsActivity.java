package life.forever.cf.adapter.person.personcenter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;
import life.forever.cf.publics.BaseActivity;
import life.forever.cf.publics.tool.AndroidManifestUtil;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class JoinUsActivity extends BaseActivity {

    @BindView(R.id.version)
    TextView mVersion;

    @Override
    protected void initializeView() {
        mTitleBar.setLeftImageResource(R.drawable.ack_icon_gray);
        mTitleBar.setLeftImageViewOnClickListener(onBackClick);
        mTitleBar.setMiddleText(MINE_STRING_SETTING_JOIN_US);
        mLoadingLayout.setVisibility(View.GONE);
        mContentLayout.setVisibility(View.VISIBLE);
        setContentView(R.layout.activity_join_us);
        ButterKnife.bind(this);
        mContentLayout.setOnClickListener(onViewClick);
    }

    @Override
    protected void initializeData() {
        mVersion.setText(String.format(Locale.getDefault(), getString(R.string.version), AndroidManifestUtil.getVersionName()));
    }

    private final View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private final View.OnClickListener onViewClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String email = getString(R.string.EMAIL_URL);
            Uri uri = Uri.parse(String.format(Locale.getDefault(), "mailto:%s", email));
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, ZERO);
            if (list.size() == ZERO) {
                PlotRead.toast(PlotRead.INFO, getString(R.string.mailbox_copied));
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setPrimaryClip(ClipData.newPlainText(null, email));
            } else {
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.author_joined));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.looking_forward_join));
                startActivity(Intent.createChooser(intent, getString(R.string.choose_apply)));
            }
        }
    };

}
