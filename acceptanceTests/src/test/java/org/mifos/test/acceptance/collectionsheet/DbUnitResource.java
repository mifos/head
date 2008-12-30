package org.mifos.test.acceptance.collectionsheet;

import org.mifos.core.ClasspathResource;

public class DbUnitResource {

    public static ClasspathResource getInstance() {
        return ClasspathResource.getInstance("/dataSets/");
    }


}