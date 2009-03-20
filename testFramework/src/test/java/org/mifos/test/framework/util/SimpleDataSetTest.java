/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.test.framework.util;

import java.io.IOException;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = { "unit" })
public class SimpleDataSetTest {

    private SimpleDataSet simpleDataSet; 
    
    @BeforeMethod
    public void setUp() {
        this.simpleDataSet = new SimpleDataSet();
    }
    
	public void testEmptyDataSet() {
	    String expectedOutput = "<dataset>\n</dataset>";
	    Assert.assertEquals(this.simpleDataSet.toString(), expectedOutput);
	}

    public void testOneEmptyTable() {
        String expectedOutput = "<dataset>\n<firstTable />\n</dataset>";
        this.simpleDataSet.row("firstTable");
        Assert.assertEquals(expectedOutput, this.simpleDataSet.toString());
    }

    public void testTwoEmptyTables() {
        String expectedOutput = "<dataset>\n<firstTable />\n<secondTable />\n</dataset>";
        this.simpleDataSet.row("firstTable");
        this.simpleDataSet.row("secondTable");
        Assert.assertEquals(expectedOutput, this.simpleDataSet.toString());
    }

    public void testTwoEmptyTablesWithUnderscores() {
        String expectedOutput = "<dataset>\n<TABLE_FIRST />\n<TABLE_SECOND />\n</dataset>";
        this.simpleDataSet.row("TABLE_FIRST");
        this.simpleDataSet.row("TABLE_SECOND");
        Assert.assertEquals(expectedOutput, this.simpleDataSet.toString());
    }

    public void testOneTableOneColumn() {
        String expectedOutput = "<dataset>\n<firstTable name=\"value\" />\n</dataset>";
        this.simpleDataSet.row("firstTable", "name=value");
        Assert.assertEquals(expectedOutput, this.simpleDataSet.toString());
    }

    public void testOneTableTwoColumns() {
        String expectedOutput = "<dataset>\n<firstTable name1=\"value1\" name2=\"value2\" />\n</dataset>";
        this.simpleDataSet.row("firstTable", "name1=value1", "name2=value2");
        Assert.assertEquals(expectedOutput, this.simpleDataSet.toString());
    }

    public void testOneTableNumericData() {
        String expectedOutput = "<dataset>\n<firstTable name1=\"10\" name2=\"3.145\" />\n</dataset>";
        this.simpleDataSet.row("firstTable", "name1=10", "name2=3.145");
        Assert.assertEquals(expectedOutput, this.simpleDataSet.toString());
    }

    public void testOneTableTwoRows() {
        String expectedOutput = "<dataset>\n" +
                "<firstTable name1=\"value00\" name2=\"value01\" />\n" +
                "<firstTable name1=\"value10\" name2=\"[null]\" />\n" +
                "</dataset>";
        this.simpleDataSet.row("firstTable", "name1=value00", "name2=value01");
        this.simpleDataSet.row("firstTable", "name1=value10", "name2=[null]");
        Assert.assertEquals(expectedOutput, this.simpleDataSet.toString());
    }

    public void testClearTable() {
        String expectedOutput = "<dataset>\n<firstTable />\n</dataset>";
        this.simpleDataSet.clearTable("firstTable");
        Assert.assertEquals(expectedOutput, this.simpleDataSet.toString());
    }

    public void testClearTableAfterAddingRow() {
        String expectedOutput = "<dataset>\n<firstTable name1=\"value1\" name2=\"value2\" />\n<secondTable />\n</dataset>";
        this.simpleDataSet.row("firstTable", "name1=value1", "name2=value2");
        this.simpleDataSet.clearTable("secondTable");
        Assert.assertEquals(expectedOutput, this.simpleDataSet.toString());
    }

    public void testTwoTablesTwoRows() {
        String expectedOutput = "<dataset>\n" +
                "<firstTable name1=\"value00\" name2=\"value01\" />\n" +
                "<firstTable name1=\"value10\" name2=\"2.178\" />\n" +
                "<secondTable fooName1=\"10.00001\" barName=\"someValue\" />\n" +
                "</dataset>";
        this.simpleDataSet.row("firstTable", "name1=value00", "name2=value01");
        this.simpleDataSet.row("firstTable", "name1=value10", "name2=2.178");
        this.simpleDataSet.row("secondTable", "fooName1=10.00001", "barName=someValue");
        Assert.assertEquals(expectedOutput, this.simpleDataSet.toString());
    }

    public void testGetDataSet() throws DataSetException, IOException {
        this.simpleDataSet.row("firstTable", "column1=value1", "column2=[null]");
        IDataSet actualDataSet = this.simpleDataSet.getDataSet();
        Assert.assertNotNull(actualDataSet);
        Assert.assertEquals("value1", actualDataSet.getTable("firstTable").getValue(0, "column1"));
        Assert.assertEquals(null, actualDataSet.getTable("firstTable").getValue(0, "column2"));
    }

}
