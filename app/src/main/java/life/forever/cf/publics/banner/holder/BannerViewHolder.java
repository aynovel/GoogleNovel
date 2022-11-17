package life.forever.cf.publics.banner.holder;

import android.content.Context;
import android.view.View;

public interface BannerViewHolder<T> {

    View createView(Context context, int position, T data);

}
