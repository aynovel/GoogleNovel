package life.forever.cf.activtiy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class IOUtils {

    public static void close(Closeable closeable){
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
            //close error
        }
    }





    public static BufferedReader StringToBufferedReader(String source){
        if(source != null)
        {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(source.getBytes());
            InputStream inputStream = byteArrayInputStream;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader;
        }
       return null;
    }

}
