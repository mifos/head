package org.mifos.framework.components.customTableTag;
import static junitx.framework.StringAssert.assertContains;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;

import org.mifos.application.office.business.OfficeView;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.TableTagParseException;
import org.mifos.framework.util.helpers.ResourceLoader;

public class TableTagParserTest extends TestCase {

	public void testParserFailure() {
		TableTagParser tableTagParser = new TableTagParser();
		try {
			tableTagParser.parser("org/mifos/framework/struts/util/helpers/struts-config.xml");
		} catch (TableTagParseException ttpe) {
			assertEquals("exception.framework.TableTagParseException", ttpe.getKey());
		}
	}
	public void testParser() throws Exception {
		Table table = TableTagParser.getInstance()
				.parser(ResourceLoader
						.getURI(
								"org\\mifos\\framework\\util\\resources\\customTableTag\\example.xml")
						.toString());
		assertNotNull(table);
		HeaderDetails details = table.getHeaderDetails();
		assertEquals("drawtablerowbold", details.getHeaderStyle());
		StringBuilder builder = new StringBuilder();
		details.getHeaderInfo(builder);
		assertContains("drawtablerowbold", builder.toString());
		Row row = table.getRow();
		assertEquals("true", row.getBottomLineRequired());
		assertEquals("100", row.getTotWidth());
		Column[] columns = row.getColumn();
		for (int i = 0; i < columns.length; i++) {
			if (i == 0) {
				assertEquals("text", columns[i].getColumnType());
				assertEquals("Name", columns[i].getLabel());
				assertEquals("Name", columns[i].getValue());
				assertEquals("method", columns[i].getValueType());
				StringBuilder builder2 = new StringBuilder();
				Locale locale = new Locale("en", "US");
				OfficeView officeView = new OfficeView(Short.valueOf("1"),
						"abcd", Short.valueOf("1"), "branch", Integer
								.valueOf("1"));

				ColumnDetails columnDetails = new ColumnDetails();
				columnDetails.setRowStyle("drawtablerowbold");
				columnDetails.setAlign("Down");
				columns[i].setColumnDetials(columnDetails);
				columns[i].generateTableColumn(builder2, officeView, locale,
						locale, locale);
				assertEquals(
						"<td class=\"drawtablerowbold\"   align=\"Down\" > </td>",
						builder2.toString());

			}
		}

	}

	public void testActionParam() throws Exception{
		ActionParam actionParam = new ActionParam();
		actionParam.setName("officeName");
		actionParam.setValue("officeName");
		StringBuilder stringBuilder = new StringBuilder();
		actionParam.generateParameter(stringBuilder,createOfficeView());
		assertEquals("officeName=abcd",stringBuilder.toString());

	}

	public void testLinkDetails()throws Exception{
		LinkDetails linkDetails = new LinkDetails();
		linkDetails.setAction("Load");
		ActionParam actionParam = new ActionParam();
		actionParam.setName("officeName");
		actionParam.setValue("officeName");
		linkDetails.setActionParam(new ActionParam[]{actionParam});
		StringBuilder stringBuilder = new StringBuilder();
		linkDetails.generateLink(stringBuilder,createOfficeView());
		assertContains("Load", stringBuilder.toString());
		
	}
	public void testRow()throws Exception{
		
		Row row = new Row();
		Column column  =new Column();
		column.setValueType("method");
		column.setColumnType("text");
		column.setLabel("Name");
		column.setValue("Name");
		ColumnDetails columnDetails = new ColumnDetails();
		columnDetails.setRowStyle("drawtablerowbold");
		columnDetails.setAlign("Down");
		column.setColumnDetials(columnDetails);
		row.setColumn(new Column[]{column});
		StringBuilder stringBuilder = new StringBuilder();
		List list =  new ArrayList();
		list.add(createOfficeView());
		Locale locale = new Locale("en", "US");
		row.generateTableRows(stringBuilder,list,locale,locale,locale);
		assertEquals("<tr><td class=\"drawtablerowbold\"   align=\"Down\" > </td></tr>",stringBuilder.toString());
		
		
	}
	private OfficeView createOfficeView(){
		return new OfficeView(Short.valueOf("1"),"abcd",Short.valueOf("1"),"branch",Integer.valueOf("1"));
	}
}
