package org.mifos.test.acceptance.util.data;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Iterator;

@SuppressWarnings("PMD")
public class Row {
    private Table table;
    private HashMap data = new HashMap();

    public Row(Table table) {
        this.table = table;
    }

    public void setColumnValue(String columnName, Object value) {
        data.put(columnName, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Row row = (Row) o;

        if (data != null ? !data.equals(row.data) : row.data != null) return false;
        if (table != null ? !table.equals(row.table) : row.table != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (table != null ? table.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("table=%s, %s", table, data);
    }

    public Table getTable() {
        return table;
    }

    public String toSql() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("insert into ").append(table.getName()).append(" (");
        buffer.append(StringUtils.join(data.keySet().toArray(), ','));
        buffer.append(") values (");

        Iterator iterator = data.keySet().iterator();
        while (iterator.hasNext()) {
            buffer.append("'").append(data.get(iterator.next())).append("'");
            if (iterator.hasNext()) buffer.append(",");
        }
        buffer.append(");");

        return buffer.toString();
    }
}
