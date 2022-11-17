package life.forever.cf.linstener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public class OnClickRecyclerViewListener extends RecyclerView.SimpleOnItemTouchListener {

    private final OnClickListener onClickListener;
    private final GestureDetector mGestureDetector;

    public interface OnClickListener {
        void onClick(View view);
    }

    public OnClickRecyclerViewListener(RecyclerView recyclerView, OnClickListener listener) {
        onClickListener = listener;

        mGestureDetector = new GestureDetector(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        if (view != null && onClickListener != null && mGestureDetector.onTouchEvent(e)) {
            onClickListener.onClick(view);
            return true;
        }
        return false;
    }

}
