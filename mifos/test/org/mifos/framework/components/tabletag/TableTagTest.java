package org.mifos.framework.components.tabletag;

import static junitx.framework.StringAssert.assertContains;
import static junitx.framework.StringAssert.assertNotContains;
import static org.mifos.framework.TestUtils.assertWellFormedFragment;

import java.util.MissingResourceException;

import javax.servlet.jsp.JspException;

import junit.framework.TestCase;

import org.mifos.application.customer.business.CustomerSearch;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.framework.exceptions.TableTagException;
import org.mifos.framework.exceptions.TableTagParseException;
import org.mifos.framework.util.helpers.SearchObject;

public class TableTagTest extends TestCase {
	
	public void testNoResults() throws Exception {
		String html =
			new TableTag("single")
				.noResults("default-office", TableTag.ALL_BRANCHES, "Rock&Roll")
				.getOutput();
		assertContains("No results found", html);
		assertContains("Rock&amp;Roll", html);
		assertContains("All Branches", html);
		assertNotContains("office-one", html);
		assertNotContains("default-office", html);
		assertWellFormedFragment(html);
	}

	public void testNoResultsMultiple() throws Exception {
		SearchObject searchObject = new SearchObject();
		searchObject.addToSearchNodeMap("dummy-search-term-key", "Rock");
		searchObject.addToSearchNodeMap(
			CustomerSearchConstants.CUSTOMER_SEARCH_OFFICE_ID, "office-one");
		String html =
			new TableTag("multiple")
				.noResults("the-office-name", "office-one", "Rock")
				.getOutput();
		assertContains("No results found", html);
		assertContains("Rock", html);
		assertNotContains("All Branches", html);
		assertContains("the-office-name", html);
		assertNotContains("office-one", html);
		assertWellFormedFragment(html);
	}

	public void testNoResultsNotAllBranches() throws Exception {
		SearchObject searchObject = new SearchObject();
		searchObject.addToSearchNodeMap("dummy-search-term-key", "Rock");
		searchObject.addToSearchNodeMap(
			CustomerSearchConstants.CUSTOMER_SEARCH_OFFICE_ID, "");
		String html =
			new TableTag("multiple")
				.noResults("the-office-name", "", "Rock")
				.getOutput();
		assertContains("No results found", html);
		assertContains("Rock", html);
		assertNotContains("All Branches", html);
		assertContains("the-office-name", html);
		assertWellFormedFragment(html);
	}
	
	public void testCreateEndTable(){
		StringBuilder stringBuilder = new StringBuilder();
			new TableTag("single").createEndTable(stringBuilder,true);
			assertContains("<img src=\"pages/framework/images/trans.gif \" width=\"10\" height=\"5\"></td></tr>",stringBuilder.toString());
			new TableTag("single").createEndTable(stringBuilder,false);
			assertContains("<img src=\"pages/framework/images/trans.gif \" width=\"5\" height=\"3\"></td></tr>",stringBuilder.toString());
	}
	public void testGetSingleFileFailure()throws Exception{
		try {
			new TableTag("single").getSingleFile();
			fail();
		} catch (MissingResourceException e) {
			assertTrue(true);
		} catch (JspException e) {
			fail();
		}
	}
	public void testGetSingleFile()throws Exception{
		TableTag tableTag = 	new TableTag("single");
		tableTag.setName("viewUsers");
		assertEquals("org/mifos/framework/util/resources/tabletag/viewUsers.xml",tableTag.getSingleFile());
	}
	
	public void testParser()throws Exception{
		
	  Files files=	TypeParser.getInstance().parser("org/mifos/framework/util/resources/tabletag/type.xml");
	  assertNotNull(files);
	  FileName[] file= files.getFileName();
	  assertNotNull(file);
	  assertEquals("1",file[0].getName());
	  assertEquals("org/mifos/framework/util/resources/tabletag/CustomerClient.xml",file[0].getPath());
	  
	}

	public void testGetDisplayText()throws Exception{
		assertEquals("<span class=\"fontnormalbold\">a</span>,<span class=\"fontnormalbold\">b</span>",Text.getDisplayText(new String[]{"a","b"},"true"));
		assertEquals("",Text.getDisplayText(new String[]{"",""},"true"));
		assertEquals("<span class=\"fontnormal\">a</span>,<span class=\"fontnormal\">b</span>",Text.getDisplayText(new String[]{"a","b"},"false"));

	}
	
	public void testGetImage()throws Exception{
		CustomerSearch customerSearch = new CustomerSearch();
		assertEquals("<span class=\"fontnormal\">&nbsp;<img src=pages/framework/images/status_yellow.gif width=\"8\" height=\"9\"></span><span class=\"fontnormal\">&nbsp;PartialApplication</span>",
				Text.getImage(customerSearch,"1"));
		customerSearch.setCustomerType(Short.valueOf("4"));
		assertEquals("<span class=\"fontnormal\">&nbsp;<img src=pages/framework/images/status_yellow.gif width=\"8\" height=\"9\"></span><span class=\"fontnormal\">&nbsp;Pending Approval</span>",
		Text.getImage(customerSearch,"2"));
		customerSearch.setCustomerType(Short.valueOf("6"));
		assertEquals("<span class=\"fontnormal\">&nbsp;<img src=pages/framework/images/status_yellow.gif width=\"8\" height=\"9\"></span><span class=\"fontnormal\">&nbsp;Partial Application</span>",
		Text.getImage(customerSearch,"13"));
	}
	
	public void testTableTagParser()throws Exception{
	  Table  table= 	TableTagParser.getInstance().parser("org/mifos/framework/util/resources/tabletag/viewUsers.xml");
	  Path path [] = table.getPath();
	  for (int i = 0; i <path.length; i++) {
		  assertEquals("PersonAction.do",path[i].getAction());
		  assertEquals("search_success",path[i].getForwardkey());
		  assertEquals("viewUsers",path[i].getKey());
		
	} 
	  for (Row row : table.getRow()) {
		  assertEquals("false",row.getTdrequired());
		  
		  int i=0;
		for (Column column : row.getColumn()) {
			
			if ( i++==1){
				assertEquals("PersonAction.do",column.getAction());
				assertEquals("true",column.getBoldlabel());
				assertEquals(null,column.getCheckLinkOptionalRequired());
				assertEquals("false",column.getImage());
				assertEquals("false",column.getIsLinkOptional());
				assertEquals("/",column.getLabel());
				assertEquals("string",column.getLabeltype());
				
				DisplayName displayName = column.getDisplayname();
				assertEquals("true",displayName.getBold());
			for (Fragment fragment : displayName.getFragment()) {
				assertEquals("true",fragment.getBold());
				assertEquals("personnelName",fragment.getFragmentName());
				assertEquals("method",fragment.getFragmentType());	
				assertEquals("false",fragment.getItalic());
			}
			
			Parameters parameters = column.getParameters();
			
			int j=0;
			for (Param param : parameters.getParam()) {
				if( j++==1)
				{
					assertEquals("method",param.getParameterName());
					assertEquals("get",param.getParameterValue());
					assertEquals("string",param.getParameterValueType());
				}
			}
			
			}
		}  

	}
	  
	  
	  PageRequirements pageRequirements = table.getPageRequirements();
	  
	  assertEquals("false",pageRequirements.getBlanklinerequired());
	  assertEquals("true",pageRequirements.getBluelineRequired());
	  assertEquals("false",pageRequirements.getBottombluelineRequired());
	  assertEquals("false",pageRequirements.getFlowRequired());
	  assertEquals("false",pageRequirements.getHeadingRequired());
	  assertEquals("true",pageRequirements.getNumbersRequired());
	  assertEquals("true",pageRequirements.getTopbluelineRequired());
	  assertEquals("false",pageRequirements.getValignnumbers());
	  
	}
}
