/**
 * 
 */
package org.mifos.framework.hibernate.helper;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.customer.business.CustomerSearch;
import org.mifos.framework.exceptions.HibernateSearchException;

public class QueryResultsMainSearchImpl extends QueryResultSearchDTOImpl {

	@Override
	public java.util.List get(int position, int noOfObjects)
			throws HibernateSearchException {
		java.util.List returnList = new java.util.ArrayList();
		java.util.List list = new java.util.ArrayList();
		Session session = null;
		try {
			session = getSession();
			Query query = prepareQuery(session,
					queryInputs.getQueryStrings()[1]);
			query.setFirstResult(position);
			query.setMaxResults(noOfObjects);
			list = query.list();
			this.queryInputs.setTypes(query.getReturnTypes());
			dtoBuilder.setInputs(queryInputs);

			Query query1 = session.createQuery(
				"select account.globalAccountNum " +
				"from org.mifos.application.accounts.business.AccountBO account " +
				"where account.customer.customerId=:customerId" +
				" and account.accountType.accountTypeId=:accountTypeId" +
				" and account.accountState.id not in (6,7,10,15,17,18) ");
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					if (buildDTO) {
						Object record = buildDTO((Object[]) list.get(i));
						CustomerSearch cs = ((CustomerSearch) record);
						query1.setInteger("customerId", cs.getCustomerId())
								.setShort("accountTypeId", (short) 1);
						cs.setLoanGlobalAccountNum(query1.list());
						query1.setShort("accountTypeId", (short) 2);
						cs.setSavingsGlobalAccountNum(query1.list());
						returnList.add(cs);
					}
					else {
						if (i < noOfObjects) {
							returnList.add(list.get(i));
						}
					}
				}
			}
			close();
		}
		catch (Exception e) {
			throw new HibernateSearchException(
					HibernateConstants.SEARCH_FAILED, e);
		}
		return returnList;
	}
}
