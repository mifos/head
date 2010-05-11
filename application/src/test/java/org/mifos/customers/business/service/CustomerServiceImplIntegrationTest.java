package org.mifos.customers.business.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelperForStaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceImplIntegrationTest extends MifosIntegrationTestCase{


    // class under test
    private CustomerService customerService;

    // collaborators
    @Mock
    private CustomerDao customerDao;

    @Mock
    private PersonnelDao personnelDao;

    @Mock
    private OfficeDao officeDao;

    @Mock
    private HolidayDao holidayDao;

    private static MifosCurrency oldCurrency;

    public CustomerServiceImplIntegrationTest() throws Exception {
        super();
    }

    @BeforeClass
    public static void setupDefaultCurrency() {
        oldCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
    }

    @AfterClass
    public static void resetDefaultCurrency() {
        Money.setDefaultCurrency(oldCurrency);
    }

    @Before
    public void setupAndInjectDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, new HibernateTransactionHelperForStaticHibernateUtil());
    }

    @Test
    public void givenGroupHasActiveAccountsGroupTransferToCenterShouldFailValidation() {
        // setup

        CenterBO center = new CenterBuilder().withName("newCenter").build();
        GroupBO group = new GroupBuilder().build();

        SavingsBO savingsAccount = new SavingsAccountBuilder().withCustomer(group).voluntary().build();

        group.addAccount(savingsAccount);

        // exercise test
        try {
            customerService.transferGroupTo(group, center);
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_HAS_ACTIVE_ACCOUNT));
        }
    }

    @Test
    public void givenChildrenOfGroupHasActiveAccountsGroupTransferToCenterShouldFailValidation() {
        // setup

        CenterBO center = new CenterBuilder().withName("newCenter").build();
        GroupBO group = new GroupBuilder().build();
        ClientBO client = new ClientBuilder().withParentCustomer(group).active().buildForUnitTests();

        SavingsBO savingsAccount = new SavingsAccountBuilder().withCustomer(group).voluntary().build();
        client.addAccount(savingsAccount);
        group.getChildren().add(client);

        // exercise test
        try {
            customerService.transferGroupTo(group, center);
            fail("should fail validation");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT));
        }
    }
}
