package org.mifos.test.acceptance.util.data;

@SuppressWarnings("PMD")
public class Table {
    private String tableName;

    public Table(String tableName) {
        this.tableName = tableName;
    }

    public String getName() {
        return tableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Table table = (Table) o;

        if (!tableName.equals(table.tableName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tableName.hashCode();
    }

    @Override
    public String toString() {
        return tableName;
    }
}
