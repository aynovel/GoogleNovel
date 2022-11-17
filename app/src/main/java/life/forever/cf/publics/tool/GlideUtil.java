package life.forever.cf.publics.tool;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.R;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class GlideUtil {

    public static void load(Context context, String url, int placeHolder, ImageView imageView) {
        if (null != context && !ComYou.isDestroy((Activity) context)) {
            RequestOptions options = new RequestOptions()
                    // 内存缓存
                    .skipMemoryCache(false)
                    .apply(RequestOptions.placeholderOf(placeHolder).error(placeHolder))
                    // 磁盘缓存所有图
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context).load(url)
                    .apply(options)
                    .into(imageView);
        }
    }

    public static void load(Context context, int recId, int placeHolder, ImageView imageView) {
        if (null != context && !ComYou.isDestroy((Activity) context)) {
            RequestOptions options = new RequestOptions()
                    // 内存缓存
                    .skipMemoryCache(false)
                    .apply(RequestOptions.placeholderOf(placeHolder).error(placeHolder))
                    // 磁盘缓存所有图
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context).load(recId)
                    .apply(options)
                    .into(imageView);
        }
    }

    public static void load(Context context, Uri uri, int placeHolder, ImageView imageView) {
        if (null != context && !ComYou.isDestroy((Activity) context)) {
            RequestOptions options = new RequestOptions()
                    // 内存缓存
                    .skipMemoryCache(false)
                    // 磁盘缓存所有图
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context).load(uri)
                    .apply(options)
                    .into(imageView);
        }
    }

    public static void loads(Context context, String url, int placeHolder, ImageView imageView) {
        if (null != context && !ComYou.isDestroy((Activity) context)) {
            RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(15))
                    // 内存缓存
                    .skipMemoryCache(false)
                    .apply(RequestOptions.placeholderOf(placeHolder).error(placeHolder))
                    // 磁盘缓存所有图
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context).load(url)
                    .apply(options)
                    .into(imageView);
        }
    }

    /**
     * 榜单分类图标
     * @param context
     * @param url
     * @param standby_url
     * @param placeHolder
     * @param imageView
     */
    public static void rankLoad(Context context, String url,String standby_url, int placeHolder, ImageView imageView) {
        if (null != context && !ComYou.isDestroy((Activity) context)) {
            RequestOptions options = new RequestOptions()
                    // 内存缓存
                    .skipMemoryCache(false)
                    .apply(RequestOptions.placeholderOf(placeHolder).error(placeHolder))
                    // 磁盘缓存所有图
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context).load(url)
                    .apply(options)
                    .error(
                            Glide.with(context)
                                    .load(standby_url).apply(options))
                    .into(imageView);
        }
    }


    public static void shelfPic(Context context,String url, int placeHolder, ImageView imageView) {
        if (null != context && !ComYou.isDestroy((Activity) context)) {
            RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(15))
                    // 内存缓存
                    .skipMemoryCache(false)
                    .apply(RequestOptions.placeholderOf(placeHolder).error(placeHolder))
                    // 磁盘缓存所有图
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context).load(url)
                    .apply(options)
                    .error(
                            Glide.with(context)
                                    .load(url).apply(options))
                    .into(imageView);
        }
    }

    /**
     * 推荐页面
     * @param context
     * @param url
     * @param standby_url
     * @param placeHolder
     */
    public static void loadDetail(Context context,String picId, String url,String standby_url, int placeHolder, View view) {
        if (null != context && !ComYou.isDestroy((Activity) context)) {

            RequestOptions options = new RequestOptions()
                    // 内存缓存
                    .skipMemoryCache(false)
                    // 磁盘缓存所有图
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context).load(url)
                    .apply(options)
                    .addListener(new RequestListener<Drawable>() {

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //图片加载成功后返回

                            //resource：目标图bitmap
                            //model：load方法里面的值
                            //target：ImageView本身
                            //dataSource：图片资源的来源，MEMORY_CACHE、LOCAL、REMOTE
                            //isFirstResource:是否是第一个资源
                            if (!TextUtils.isEmpty(picId)){
                                SharedPreferencesUtil.putString(PlotRead.getConfig(), picId,url);
                            }
                            return false;
                        }

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //图片加载失败后返回
                            Glide.with(context)
                                    .load(standby_url);
                            return false;
                        }

                    })
                    .error(
                            Glide.with(context)
                                    .load(standby_url))
                    .into(new SimpleTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            view.setBackground(resource);
                        }
                    });

        }
    }

    /**
     * 推荐页面毛玻璃效果
     */
    public static void loadDetail(Context mContext,String url,ImageView imageView){
        if (null != mContext && !ComYou.isDestroy((Activity) mContext)) {

            MultiTransformation multi = new MultiTransformation(
                    new BlurTransformation(125, 1)
            );
            RequestOptions options = new RequestOptions().bitmapTransform(multi).override(300, 400);
            Glide.with(mContext)
                    .asBitmap()
                    .apply(options)
                    .apply(RequestOptions.placeholderOf(R.color.gray_0_2_color).error(R.color.gray_0_2_color))
                    .load(url)
                    .transition(withCrossFade(1000))
                    .into(imageView);

        }
    }

    /**
     * 推荐页面
     * @param context
     * @param url
     * @param standby_url
     * @param placeHolder
     * @param imageView
     */
    public static void recomment(Context context,String picId, String url,String standby_url, int placeHolder, ImageView imageView) {
        if (null != context && !ComYou.isDestroy((Activity) context)) {
            RequestOptions options = RequestOptions.bitmapTransform(new RoundedCorners(15))
                    // 内存缓存
                    .skipMemoryCache(false)
                    .apply(RequestOptions.placeholderOf(placeHolder).error(placeHolder))
                    // 磁盘缓存所有图
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(context).load(url)
                    .apply(options)

                    .addListener(new RequestListener<Drawable>() {

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //图片加载成功后返回

                            //resource：目标图bitmap
                            //model：load方法里面的值
                            //target：ImageView本身
                            //dataSource：图片资源的来源，MEMORY_CACHE、LOCAL、REMOTE
                            //isFirstResource:是否是第一个资源
                            if (!TextUtils.isEmpty(picId)){
                                SharedPreferencesUtil.putString(PlotRead.getConfig(), picId,url);
                            }
                            return false;
                        }

                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //图片加载失败后返回
                            Glide.with(context)
                                    .load(standby_url).apply(options);
                            return false;
                        }

                    })
                    .error(
                            Glide.with(context)
                                    .load(standby_url).apply(options))

                    .into(imageView);
        }
    }


    /**
     * 读取图片缓存公共方法
     */
    public static void picCache(Context mContext,String url, String urlCache,int placeHolder, ImageView mCover){
        GlideUtil.recomment(mContext, urlCache, url, url, placeHolder, mCover);
    }

}
