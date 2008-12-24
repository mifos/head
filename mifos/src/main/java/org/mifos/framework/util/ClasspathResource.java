package org.mifos.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.Reader;
import java.net.URL;

/**
 * Load a resource from the classpath. 
 * 
 * Derive this and override the path constant to load resources local to your directory.
 * 
 */

public class ClasspathResource {

    public static ClasspathResource getInstance() {
        return new ClasspathResource();
    }

    protected String path;

    @SuppressWarnings("unchecked")
    public ClasspathResource() {
        this.path = "";
    }
    
    public URL getUrl(String name) {
        String resourcePath = this.path + name;
        return this.getClass().getResource(resourcePath);
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
