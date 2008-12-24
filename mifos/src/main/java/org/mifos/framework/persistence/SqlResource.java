package org.mifos.framework.persistence;

import org.mifos.framework.util.ClasspathResource;

public class SqlResource extends ClasspathResource {

    public static SqlResource getInstance() {
        return new SqlResource();
    }

    public SqlResource() {
        this.path = "sql/";
    }
    
}
