package org.mifos.test.acceptance.collectionSheetEntry;

import org.mifos.core.ClasspathResource;

public class DbUnitResource {

    public static ClasspathResource getInstance() {
        return ClasspathResource.getInstance("/org/mifos/test/acceptance/collectionSheetEntry/dbUnit");
    }


}