package org.mifos.application.admin.system.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/messageSourceBean.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml"})
public class AppliedUpgradesDaoHibernateIntegrationTest {

    @Autowired
    private AppliedUpgradesDao appliedUpgradesDao;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetAppliedUpgrades() throws SQLException {
        List<Integer> upgrades = appliedUpgradesDao.getAppliedUpgrades();

        StaticHibernateUtil.initialize();
        Connection connection = StaticHibernateUtil.getSessionTL().connection();
        ResultSet rs = connection.createStatement().executeQuery("select count(upgrade_id) from applied_upgrades");
        rs.first();
        int expected = rs.getInt(1);

        Assert.assertEquals(expected, upgrades.size());
    }

}
