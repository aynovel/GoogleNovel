package life.forever.cf.activtiy;

import android.widget.Toast;


public class ToastUtils {

    public static void show(String msg){
        Toast.makeText(PlotRead.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
