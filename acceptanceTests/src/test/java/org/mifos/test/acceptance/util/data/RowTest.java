package org.mifos.test.acceptance.util.data;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@Test
public class RowTest {
    public void sameIsEqual() {
        Row row = new Row(new Table("foo"));
        row.setColumnValue("abc", 123);
        row.setColumnValue("efg", 456);

        Assert.assertEquals(row, row);
    }

    public void equalWhenColumnOrderIsDifferent() {
        Row row1 = new Row(new Table("foo"));
        row1.setColumnValue("abc", 123);
        row1.setColumnValue("efg", 456);

        Row row2 = new Row(new Table("foo"));
        row2.setColumnValue("efg", 456);
        row2.setColumnValue("abc", 123);

        Assert.assertTrue(row1.equals(row2));
    }

    public void rowFromDifferentTableAreNotEqual() {
        Row row1 = new Row(new Table("foo"));
        row1.setColumnValue("abc", 123);
        row1.setColumnValue("efg", 456);

        Row row2 = new Row(new Table("bar"));
        row2.setColumnValue("abc", 123);
        row2.setColumnValue("efg", 456);

        Assert.assertFalse(row1.equals(row2));
    }

    public void rowWithDifferentDataAreNotEqual() {
        Row row1 = new Row(new Table("foo"));
        row1.setColumnValue("abc", 124);
        row1.setColumnValue("efg", 456);

        Row row2 = new Row(new Table("foo"));
        row2.setColumnValue("abc", 123);
        row2.setColumnValue("efg", 456);

        Assert.assertFalse(row1.equals(row2));
    }

    public void seeToSql() {
        Row row = new Row(new Table("foo"));
        row.setColumnValue("abc", 123);
        row.setColumnValue("efg", 456);

        String expectedSql = "insert into foo (abc,efg) values ('123','456');";
        Assert.assertEquals(expectedSql, row.toSql());
    }
}
