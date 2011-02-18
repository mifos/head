package org.mifos.test.acceptance.util.data;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("PMD")
public class DataSet {
    private Map tables = new HashMap();

    public void addTable(Table table) {
        tables.put(table.getName(), table);
    }
}
