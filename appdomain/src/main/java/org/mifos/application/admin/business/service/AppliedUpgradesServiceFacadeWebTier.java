package org.mifos.application.admin.business.service;

import java.util.List;

import org.mifos.application.admin.servicefacade.AppliedUpgradesServiceFacade;
import org.mifos.application.admin.system.persistence.AppliedUpgradesDao;
import org.springframework.beans.factory.annotation.Autowired;

public class AppliedUpgradesServiceFacadeWebTier implements AppliedUpgradesServiceFacade {

    private AppliedUpgradesDao appliedUpgradeDao;

    @Autowired
    public AppliedUpgradesServiceFacadeWebTier(AppliedUpgradesDao appliedUpgradesDao){
        this.appliedUpgradeDao = appliedUpgradesDao;

    }

    @Override
    public List<Integer> getAppliedUpgrades() {
        return appliedUpgradeDao.getAppliedUpgrades();
    }

}
