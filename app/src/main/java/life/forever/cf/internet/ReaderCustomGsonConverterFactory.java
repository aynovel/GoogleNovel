package life.forever.cf.internet;

import static java.nio.charset.StandardCharsets.UTF_8;

import life.forever.cf.entry.MoreBuy;
import life.forever.cf.entry.TaskReword;
import life.forever.cf.entry.ChapterItemBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class ReaderCustomGsonConverterFactory extends Converter.Factory {

    private final Gson gson;


    private ReaderCustomGsonConverterFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    public static ReaderCustomGsonConverterFactory create() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChapterItemBean.class, new JsonDeserializer<ChapterItemBean>() {
                    @Override
                    public ChapterItemBean deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                            throws JsonParseException {
                        if (json.isJsonArray()) {
                            return null;
                        }

                        JsonObject value = json.getAsJsonObject();
                        ChapterItemBean itemBean = new Gson().fromJson(value, ChapterItemBean.class);

                        return itemBean;
                    }
                })
                .registerTypeAdapter(MoreBuy.class, new JsonDeserializer<MoreBuy>() {
                    @Override
                    public MoreBuy deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                            throws JsonParseException {
                        if (json.isJsonArray()) {
                            return null;
                        }

                        JsonObject value = json.getAsJsonObject();
                        MoreBuy itemBean = new Gson().fromJson(value, MoreBuy.class);

                        return itemBean;
                    }
                })
                .registerTypeAdapter(TaskReword.class, new JsonDeserializer<TaskReword>() {
                    @Override
                    public TaskReword deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                            throws JsonParseException {
                        if (json.isJsonArray()) {
                            return null;
                        }

                        JsonObject value = json.getAsJsonObject();
                        TaskReword itemBean = new Gson().fromJson(value, TaskReword.class);

                        return itemBean;
                    }
                }).create();


        return create(gson);
    }

    public static ReaderCustomGsonConverterFactory create(Gson gson) {
        return new ReaderCustomGsonConverterFactory(gson);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new CustomGsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new CustomGsonRequestBodyConverter<>(gson, adapter);
    }

    final class CustomGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private final Charset UTF_8 = Charset.forName("UTF-8");

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        CustomGsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public RequestBody convert(T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }

    final class CustomGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private final Gson gson;
        private final TypeAdapter<T> adapter;

        CustomGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            String response = value.string();
//            BaseResponse baseResponse = gson.fromJson(response, BaseResponse.class);
//            //核心代码:  判断 status 是否是后台定义的正常值
//            if (baseResponse.isCodeInvalid()) {
//                value.close();
//                throw new ApiException(baseResponse.getStatus(), baseResponse.getMessage());
//            }

            MediaType contentType = value.contentType();
            Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;
            InputStream inputStream = new ByteArrayInputStream(response.getBytes());
            Reader reader = new InputStreamReader(inputStream, charset);
            JsonReader jsonReader = gson.newJsonReader(reader);

            try {
                return adapter.read(jsonReader);
            } finally {
                value.close();
            }
        }
    }
}