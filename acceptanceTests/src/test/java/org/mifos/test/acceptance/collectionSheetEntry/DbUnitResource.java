package org.mifos.test.acceptance.collectionSheetEntry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DbUnitResource {

    public static DbUnitResource getInstance() {
        return new DbUnitResource();
    }

    protected String path;

    public DbUnitResource() {
        this.path = "";
    }
    
    public URL getUrl(String name) {
        String resourcePath = this.path + name;
        return this.getClass().getResource(resourcePath);
    }
    
    public InputStream getAsStream(String name) throws IOException {
        return getUrl(name).openStream();
    }
}