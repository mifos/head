package org.mifos.framework.hibernate.helper;

import java.util.List;

import org.mifos.application.customer.dao.SearchDAO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class TestQueryResultIdSearch extends MifosTestCase {

	public void testCustomerIdSearch() throws SystemException,
			ApplicationException {
		SearchDAO searchDAO = new SearchDAO();
		searchDAO.customerIdSearch("0013-000000012", Short.valueOf("0"));
		List list = ((QueryResultIdSearch) searchDAO.queryResult)
				.customerIdSearch("0013-000000012", Short.valueOf("0"));
		assertEquals(1, list.size());

	}

	/*
	 * public void testCustomerIdSearchForBranchOffice() throws SystemException,
	 * ApplicationException { SearchDAO searchDAO = new SearchDAO(); QueryResult
	 * queryResult =
	 * searchDAO.search(null,"0003-000000003",null,null,null,Short.valueOf("3"));
	 * List list =
	 * ((QueryResultIdSearch)queryResult).customerIdSearch("0003-000000003",Short.valueOf("3"));
	 * assertEquals(1,list.size()); }
	 */

	public void testGet() throws SystemException, ApplicationException {
		SearchDAO searchDAO = new SearchDAO();
		QueryResult queryResult = searchDAO.search(null, "0013-000000012",
				null, null, null, Short.valueOf("0"));
		List list = queryResult.get(1, 1);
		assertEquals(1, list.size());
		/*
		 * CustomerSearch customerSearch = (CustomerSearch) list.get(0);
		 * Collection collection = customerSearch.getLoanGlobalAccountNum();
		 * boolean loanAccountNum = collection.contains("0013-000000012");
		 * assertEquals(true,loanAccountNum);
		 */
	}
	/*
	 * public void testGetSize() throws SystemException, ApplicationException {
	 * SearchDAO searchDAO = new SearchDAO(); QueryResult queryResult =
	 * searchDAO.search(null,"0003-000000003",null,null,null,Short.valueOf("0"));
	 * assertEquals(1,queryResult.getSize()); }
	 */
}
