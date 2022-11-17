package life.forever.cf.publics.fresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import life.forever.cf.R;
import life.forever.cf.publics.fresh.weight.BaseFooterView;
import life.forever.cf.publics.tool.GlideUtil;

public class LoadFooterView extends BaseFooterView {

    ImageView loadingView;
    TextView textView;
    Context mContext;
    public LoadFooterView(Context context) {
        this(context, null);
        mContext = context;
    }

    public LoadFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public LoadFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(getContext()).inflate(R.layout.view_refresh_header, this, true);
        loadingView = findViewById(R.id.loading);
        textView = findViewById(R.id.textView);
        GlideUtil.load(context, R.drawable.bload, 0, loadingView);
    }

    @Override
    public float getSpanHeight() {
        return getHeight();
    }

    @Override
    protected void onStateChange(int state) {
        switch (state) {
            case NONE:
                break;
            case PULLING:
                textView.setText(mContext.getString(R.string.pull_on_loading));
                break;
            case LOOSENT_O_LOAD:
                textView.setText(mContext.getString(R.string.loosen_the_load));
                break;
            case LOADING:
                textView.setText(mContext.getString(R.string.being_loaded));
                break;
            case LOAD_CLONE:
                textView.setText(mContext.getString(R.string.pull_loaded));
                break;
        }
    }
}
