package life.forever.cf.publics.weight;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int space ;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //不是第一个格子都设左边和底部的边距
        outRect.left = space;
        outRect.bottom = space;

        if (parent.getChildLayoutPosition(view) %3 == 0 ){
            outRect.left = 0;
        }

    }

}
