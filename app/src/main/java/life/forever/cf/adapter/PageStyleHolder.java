package life.forever.cf.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import life.forever.cf.R;


public class PageStyleHolder extends ViewHolderImpl<Drawable> {

    private View mReadBg;
    private ImageView mIvChecked;

    @Override
    public void initView() {
        mReadBg = findById(R.id.read_bg_view);
        mIvChecked = findById(R.id.read_bg_iv_checked);
    }

    @Override
    public void onBind(Drawable data, int pos) {
        mReadBg.setBackground(data);
        mIvChecked.setVisibility(View.GONE);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_read_bg;
    }

    public void setChecked(){
        mIvChecked.setVisibility(View.VISIBLE);
    }
}
