package org.mifos.application.admin.system.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;

public class AppliedUpgradesDaoHibernate implements AppliedUpgradesDao {

    private final GenericDao genericDao;


    public AppliedUpgradesDaoHibernate(GenericDao genericDao){
        this.genericDao = genericDao;

    }

    @Override
    public List<Integer> getAppliedUpgrades() {
        Session session =  ((GenericDaoHibernate)genericDao).getHibernateUtil().getSessionTL();
        List<Integer> upgrades = new ArrayList<Integer>();
        try {
            ResultSet rs = session.connection().createStatement().executeQuery(
                    "select upgrade_id from applied_upgrades order by upgrade_id");

            while (rs.next()) {
                upgrades.add(rs.getInt(1));
            }
        } catch (HibernateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return upgrades;
    }
}
