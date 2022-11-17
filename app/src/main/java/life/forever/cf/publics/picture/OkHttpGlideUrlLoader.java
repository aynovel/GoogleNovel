package life.forever.cf.publics.picture;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

public class OkHttpGlideUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(GlideUrl glideUrl, int width, int height, Options options) {
        return new LoadData<>(glideUrl, new OkHttpFetcher(glideUrl));
    }

    @Override
    public boolean handles(GlideUrl glideUrl) {
        return true;
    }

    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {

        @Override
        public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new OkHttpGlideUrlLoader();
        }

        @Override
        public void teardown() {

        }
    }
}
