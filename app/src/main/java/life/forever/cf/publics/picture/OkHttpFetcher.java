package life.forever.cf.publics.picture;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.util.ContentLengthInputStream;
import life.forever.cf.activtiy.PlotRead;
import life.forever.cf.publics.tool.LOG;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpFetcher implements DataFetcher<InputStream> {

    private final GlideUrl url;
    private InputStream stream;
    private ResponseBody responseBody;
    private volatile boolean isCancelled;

    public OkHttpFetcher(GlideUrl url) {
        this.url = url;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
        try {
            if (isCancelled) {
                callback.onDataReady(null);
            } else {
                Request.Builder requestBuilder = new Request.Builder()
                        .url(url.toStringUrl());
                for (Map.Entry<String, String> headerEntry : url.getHeaders().entrySet()) {
                    String key = headerEntry.getKey();
                    requestBuilder.addHeader(key, headerEntry.getValue());
                }
                requestBuilder.addHeader("Glide", "LoadByOkHttp");
                Request request = requestBuilder.build();
                Response response = PlotRead.getOkHttpClient().newCall(request).execute();
                responseBody = response.body();
                if (response.isSuccessful() && responseBody != null) {
                    stream = ContentLengthInputStream.obtain(responseBody.byteStream(),
                            responseBody.contentLength());
                    callback.onDataReady(stream);
                } else {
                    callback.onLoadFailed(new Exception("Request failed with code: " + response.code()));
                }
            }
        } catch (IOException e) {
            callback.onLoadFailed(e);
        }
    }

    @Override
    public void cleanup() {
        try {
            if (stream != null) {
                stream.close();
            }
            if (responseBody != null) {
                responseBody.close();
            }
        } catch (IOException e) {
            LOG.e(this.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void cancel() {
        isCancelled = true;
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
