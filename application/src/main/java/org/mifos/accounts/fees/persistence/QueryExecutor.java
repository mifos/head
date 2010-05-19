package org.mifos.accounts.fees.persistence;

import java.util.List;

public interface QueryExecutor<T> {
    List<T> execFindQuery(String qryMethodName, Object[] queryArgs);
}
