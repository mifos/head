package org.mifos.customers.personnel.persistence;

import java.util.List;

import org.mifos.application.servicefacade.CenterCreation;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelView;

public interface PersonnelDao {

    List<PersonnelView> findActiveLoanOfficersForOffice(CenterCreation centerCreation);

    PersonnelBO findPersonnelById(Short id);
}
