package life.forever.cf.publics.richtext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class NetImageGetter implements Html.ImageGetter {
    private final Context context;
    private final TextView contentTextView;
    private int width = 0;

    public NetImageGetter(Context context, TextView contentTextView, int width) {
        this.context = context;
        this.contentTextView = contentTextView;
        this.width = width;
    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();
        d.setBounds(0, 0, 300, 200);
        new LoadImage().execute(source, d);
        return d;
    }

    /*
     * 异步下载图片类
     */
    class LoadImage extends AsyncTask<Object, Void, Bitmap> {
        private LevelListDrawable mDrawable;

        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            mDrawable = (LevelListDrawable) params[1];
            try {
                InputStream is = new URL(source).openStream();
                return BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /*
         * 图片下载完成后执行
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable = getDrawableAdapter(context, mDrawable,
                        bitmap.getWidth(), bitmap.getHeight());
                int w;
                if (bitmap.getWidth() * 2 > 1080) {
                    w = 1000;
                } else {
                    w = bitmap.getWidth() * 2;
                }
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);
                contentTextView.invalidate();
                CharSequence t = contentTextView.getText();
                contentTextView.setText(t);
            }
        }

        public LevelListDrawable getDrawableAdapter(Context context,
                                                    LevelListDrawable drawable, int oldWidth, int oldHeight) {
            LevelListDrawable newDrawable = drawable;
            long newHeight = 0;// 未知数
            int newWidth = width;// 默认屏幕宽
            newHeight = (newWidth * oldHeight) / oldWidth;
            newDrawable.setBounds(0, 0, newWidth, (int) newHeight);
            return newDrawable;
        }
    }

}
