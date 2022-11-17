package life.forever.cf.adapter;

import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class BoolTypeAdapter extends TypeAdapter<Boolean> {
    @Override
    public void write(JsonWriter out, Boolean value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override
    public Boolean read(JsonReader in) throws IOException {
        JsonToken peek = in.peek();
        switch (peek) {
            case BOOLEAN:
                return in.nextBoolean();
            case NULL:
                in.nextNull();
                return null;
            case NUMBER:
                return in.nextInt() != 0;
            case STRING:
                return toBoolean(in.nextString());
            default:
                throw new JsonParseException("Expected BOOLEAN or NUMBER but was " + peek);
        }
    }

    /**
     * true  TURE 都为true
     * "0" 为 false
     *
     * @param name
     * @return
     */
    public static boolean toBoolean(String name) {
        return (!TextUtils.isEmpty(name))
                &&
                (name.equalsIgnoreCase("true") || !name.equals("0"));
    }

}
