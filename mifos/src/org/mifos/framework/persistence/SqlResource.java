package org.mifos.framework.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.net.URL;

public class SqlResource {

    public static SqlResource getInstance() {
        return new SqlResource();
    }
    
    public URL getUrl(String name) {
        String sqlResourcePath = "sql/" + name;
        return getClass().getResource(sqlResourcePath);
    }
    
    public InputStream getAsStream(String name) throws IOException {
        return getUrl(name).openStream();
    }

    public Reader getAsReader(String name) {
       try {
         return new BufferedReader(new InputStreamReader(this.getAsStream(name)));
       } catch (IOException e) {
         throw new RuntimeException(e);
       }
    }
}
