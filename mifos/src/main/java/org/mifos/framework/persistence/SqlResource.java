package org.mifos.framework.persistence;

import org.mifos.core.ClasspathResource;

public class SqlResource {

    public static ClasspathResource getInstance() {
        return ClasspathResource.getInstance("/org/mifos/framework/persistence/sql/");
    }

}
