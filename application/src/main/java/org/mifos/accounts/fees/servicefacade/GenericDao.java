package org.mifos.accounts.fees.servicefacade;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface GenericDao<T, ID extends Serializable> {
    T getDetails(ID id);
    List<T> getDetailsAll();
    void add(T newInstance);
    ID create(T newInstance);
    void update(T transientObject);
    void delete(T persistentObject);
    void saveOrUpdateAll(Collection<T> c);
}