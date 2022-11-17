package life.forever.cf.publics.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import life.forever.cf.R;

public class CustomToast {

    public static void showToast(Context context, String message) {
        //加载Toast布局
        @SuppressLint("InflateParams")
        View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast, null);
        //初始化布局控件
        TextView mTextView = toastRoot.findViewById(R.id.message);
        ImageView mImageView = toastRoot.findViewById(R.id.imageView);
        //为控件设置属性
        mTextView.setText(message);
        mImageView.setImageResource(R.drawable.logo_novel_star);
        //Toast的初始化
        Toast toastStart = new Toast(context);
        //获取屏幕高度
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        int height = wm.getDefaultDisplay().getHeight();
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        /*toastStart.setGravity(Gravity.TOP, 0, height / 3);*/
        toastStart.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);
        toastStart.setDuration(Toast.LENGTH_SHORT);
        toastStart.setView(toastRoot);
        toastStart.show();
    }
}
