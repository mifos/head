package org.mifos.servicefacades;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.datasetup.repositories.TestClients;
import org.mifos.datasetup.repositories.TestOffices;
import org.mifos.datasetup.repositories.TestPersonnels;
import org.mifos.framework.hibernate.helper.AuditInterceptorFactory;
import org.mifos.framework.hibernate.helper.DatabaseDependentTest;
import org.mifos.framework.util.helpers.Money;
import org.mifos.hibernate.DataSetupSession;
import org.mifos.security.MifosUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/org/mifos/config/resources/propertyConfigurer.xml",
        "classpath:/org/mifos/config/resources/queryInterceptorContext.xml", "classpath:META-INF/spring/QuestionnaireContext.xml",
        "classpath:/org/mifos/config/resources/applicationContext.xml", "classpath:/org/mifos/testContext.xml"})
public abstract class ServiceFacadeTest {
    protected MifosUser mifosUser;

    protected Session session;

    @Autowired
    private SessionFactory sessionFactory;

    @Before
    public void beforeServiceFacadeTest() {
        session = new DataSetupSession(DatabaseDependentTest.beforeWithoutInitialization(new AuditInterceptorFactory(), sessionFactory));
        Money.setDefaultCurrency(new MifosCurrency((short) 2, "RUPEE", BigDecimal.valueOf(1.0), "INR"));
        mifosUser = new MifosUser(1, (short) 1, (short) 1, new ArrayList<Short>(), "mifos", null, true, true, true, true, new ArrayList<GrantedAuthority>());
    }

    @After
    public void afterServiceFacadeTest() {
        DatabaseDependentTest.after(new AuditInterceptorFactory(), sessionFactory);
    }

    protected OfficeBO addOffice(String shortName) {
        return new TestOffices(session).addAny(shortName);
    }

    protected PersonnelBO addPersonnel(String name) {
        return new TestPersonnels(session).add(name);
    }

    protected ClientBO addClient(String name, OfficeBO office, PersonnelBO personnel) {
        return new TestClients(session).addAny(name, office, personnel);
    }
}
