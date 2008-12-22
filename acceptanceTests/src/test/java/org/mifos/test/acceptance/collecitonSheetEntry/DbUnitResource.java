package org.mifos.test.acceptance.collecitonSheetEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.net.URL;


// TODO: Refactor me later (see SqlResource.java)
public class DbUnitResource {

    public static DbUnitResource getInstance() {
        return new DbUnitResource();
    }
    
    public URL getUrl(String name) {
        String sqlResourcePath = "dbunit/" + name;
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
