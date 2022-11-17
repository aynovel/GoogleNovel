package life.forever.cf.publics.banner.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import life.forever.cf.R;
import life.forever.cf.publics.tool.GlideUtil;


public class CustomViewHolder implements BannerViewHolder<String> {

    @SuppressLint("InflateParams")
    @Override
    public View createView(Context context, int position, String data) {
        View view = LayoutInflater.from(context).inflate(R.layout.banner_item, null);
//        TextView title = view.findViewById(R.id.title);
        ImageView iv_bg = view.findViewById(R.id.iv_bg);
        GlideUtil.loads(context, data, R.drawable.default_banne_coverr, iv_bg);

        return view;
    }

}
