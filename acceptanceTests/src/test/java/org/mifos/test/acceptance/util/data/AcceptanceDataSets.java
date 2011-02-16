package org.mifos.test.acceptance.util.data;

import com.google.common.collect.Sets;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;
import org.mifos.framework.util.DbUnitUtilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@SuppressWarnings("PMD")
public class AcceptanceDataSets {
    public static void main(String[] args) throws Exception {
        File directory = new File("acceptanceTests/src/test/resources/dataSets");
        File[] files = directory.listFiles();
        DbUnitUtilities dbUnitUtilities = new DbUnitUtilities();

        Set<Row> union = new HashSet<Row>();
        for (File file : files) {
            if (file.getName().endsWith(".zip")) {
                IDataSet dbUnitDataSet = dbUnitUtilities.getDataSetFromFile(file.getAbsolutePath());
                System.out.println("Constructed dataSet for " + file.getName());

                Set<Row> rows = new HashSet<Row>();
                String[] tables = dbUnitDataSet.getTableNames();
                for (String tableName : tables) {
                    ITable dbUnitTable = dbUnitDataSet.getTable(tableName);
                    Table table = new Table(tableName);
                    ITableMetaData tableMetaData = dbUnitTable.getTableMetaData();
                    Column[] columns = tableMetaData.getColumns();
                    int rowCount = dbUnitTable.getRowCount();
                    for (int i = 0; i < rowCount; i++) {
                        Row row = new Row(table);
                        for (Column column : columns) {
                            Object value = dbUnitTable.getValue(i, column.getColumnName());
                            row.setColumnValue(column.getColumnName(), value);
                        }
                        rows.add(row);
                    }
                }
                System.out.println("Number of items in the current data set: " + rows.size());
                union = Sets.union(rows, union);
                System.out.println("Number of items in the union data set: " + union.size());
            }
        }

        System.out.println("Sorting the union based on table name");
        ArrayList<Row> result = new ArrayList<Row>(union);
        Collections.sort(result, new Comparator<Row>() {
            @Override
            public int compare(Row row1, Row row2) {
                return row1.getTable().getName().compareTo(row2.getTable().getName());
            }
        });
        writeToFile(result);
    }

    private static void writeToFile(ArrayList<Row> union) throws IOException {
        String outputFile = "/home/vivek/projects/mifos/functional-test-data.txt";
        System.out.println("Writing to file " + outputFile);
        FileWriter writer = null;
        try {
            writer = new FileWriter(outputFile);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            for (Row row : union) {
                bufferedWriter.write(row.toSql());
                bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            writer.flush();
        }
        finally {
            if (writer != null) writer.close();
        }
    }
}
