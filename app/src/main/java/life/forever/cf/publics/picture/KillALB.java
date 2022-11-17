package life.forever.cf.publics.picture;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;


@GlideModule
public class KillALB extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        int diskCacheSizeBytes = 1024 * 1024 *1000;

        appRootPath = context.getCacheDir().getPath();
        builder.setDiskCache(
                new InternalCacheDiskCacheFactory( context,getStorageDirectory()+"/aiyecesss", diskCacheSizeBytes )
        );
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpGlideUrlLoader.Factory());
    }

    //外部路径
    private final String sdRootPath = Environment.getExternalStorageDirectory().getPath();
    private String appRootPath = null;

    private String getStorageDirectory(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                sdRootPath : appRootPath;
    }
}