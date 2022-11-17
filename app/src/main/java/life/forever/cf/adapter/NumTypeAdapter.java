package life.forever.cf.adapter;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;

public class NumTypeAdapter extends TypeAdapter<Number> {
    @Override
    public void write(JsonWriter out, Number value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override
    public Number read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        if (in.peek() == JsonToken.STRING) {
            try{
                String value = in.nextString();
                if(value==null || value.length()==0){
                    return null;
                }else{
                    Integer result;
                    try {
                        result = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                        double asDouble = Double.parseDouble(value); // don't catch this NumberFormatException
                        result = (int) asDouble;
                        if (result != asDouble) {
                            throw new NumberFormatException("Expected an int but was " + value);
                        }
                    }
                    if (result >= 1L && value.startsWith("0")) {
                        throw new MalformedJsonException("JSON forbids octal prefixes: " + value);
                    }
                    return result;
                }
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }else{
            try {
                return in.nextInt();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }
    }
}
