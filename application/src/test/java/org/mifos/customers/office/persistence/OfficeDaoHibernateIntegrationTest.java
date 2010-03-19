package org.mifos.customers.office.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.customers.office.business.OfficeView;
import org.mifos.framework.MifosIntegrationTestCase;

public class OfficeDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    public OfficeDaoHibernateIntegrationTest() throws Exception {
        super();
    }

    // class under test
    private OfficeDao officeDao;

    // collaborators
    private final GenericDao genericDao = new GenericDaoHibernate();

    private final Short existingOfficeId = Short.valueOf("1");

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        officeDao = new OfficeDaoHibernate(genericDao);
    }

    public void testGivenAnOfficeExistsShouldReturnItAsOfficeDto() {

        OfficeDto office = officeDao.findOfficeDtoById(existingOfficeId);

        assertThat(office.getId(), is(Short.valueOf("1")));
        assertThat(office.getName(), is("Mifos HO"));
        assertThat(office.getSearchId(), is("1.1."));
    }

    public void testGivenActiveLevelsExistsShouldReturnThemAsOfficeViews() {

        List<OfficeView> offices = officeDao.findActiveOfficeLevels();

        assertThat(offices.isEmpty(), is(false));
    }
}