package org.mifos.datasetup.repositories;

import org.hibernate.Session;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;

public class TestPersonnels extends TestEntities<PersonnelBO> {
    public TestPersonnels(Session session) {
        super(session, PersonnelBO.class);
    }

    public PersonnelBO add(String name) {
        PersonnelBO personnelBO = new PersonnelBO(name, name, (PersonnelLevelEntity) any(PersonnelLevelEntity.class), 1);
        session.saveOrUpdate(personnelBO);
        return personnelBO;
    }
}
