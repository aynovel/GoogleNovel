package life.forever.cf.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

public interface Adapter {

    int getCount();

    TextView getItemView(ViewGroup parent, int position);
}
