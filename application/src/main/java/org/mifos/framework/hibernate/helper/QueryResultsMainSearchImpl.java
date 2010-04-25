/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.framework.hibernate.helper;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.customers.business.CustomerSearchDto;
import org.mifos.framework.exceptions.HibernateSearchException;

public class QueryResultsMainSearchImpl extends QueryResultSearchDTOImpl {

    @Override
    public java.util.List get(int position, int noOfObjects) throws HibernateSearchException {
        java.util.List returnList = new java.util.ArrayList();
        java.util.List list = new java.util.ArrayList();
        Session session = null;
        try {
            session = getSession();
            Query query = prepareQuery(session, queryInputs.getQueryStrings()[1]);
            query.setFirstResult(position);
            query.setMaxResults(noOfObjects);
            list = query.list();
            this.queryInputs.setTypes(query.getReturnTypes());
            dtoBuilder.setInputs(queryInputs);

            Query query1 = session.createQuery("select account.globalAccountNum "
                    + "from org.mifos.accounts.business.AccountBO account "
                    + "where account.customer.customerId=:customerId"
                    + " and account.accountType.accountTypeId=:accountTypeId"
                    + " and account.accountState.id not in (6,7,10,15,17,18) ");
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    if (buildDTO) {
                        Object record = buildDTO((Object[]) list.get(i));
                        CustomerSearchDto cs = ((CustomerSearchDto) record);
                        query1.setInteger("customerId", cs.getCustomerId()).setShort("accountTypeId", (short) 1);
                        cs.setLoanGlobalAccountNum(query1.list());
                        query1.setShort("accountTypeId", (short) 2);
                        cs.setSavingsGlobalAccountNum(query1.list());
                        returnList.add(cs);
                    } else {
                        if (i < noOfObjects) {
                            returnList.add(list.get(i));
                        }
                    }
                }
            }
            close();
        } catch (Exception e) {
            throw new HibernateSearchException(HibernateConstants.SEARCH_FAILED, e);
        }
        return returnList;
    }
}
