package org.mifos.application.admin.business.service;

import java.util.List;

import org.mifos.application.admin.servicefacade.AppliedUpgradesServiceFacade;
import org.mifos.db.upgrade.ChangeSetInfo;
import org.mifos.db.upgrade.DatabaseUpgradeSupport;
import org.mifos.db.upgrade.UnRunChangeSetInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class AppliedUpgradesServiceFacadeWebTier implements AppliedUpgradesServiceFacade {

    private DatabaseUpgradeSupport databaseUpgradeSupport;

    @Autowired
    public AppliedUpgradesServiceFacadeWebTier(DatabaseUpgradeSupport databaseUpgradeSupport){
        this.databaseUpgradeSupport = databaseUpgradeSupport;
    }

    @Override
    public List<ChangeSetInfo> getAppliedUpgrades() {
        return  databaseUpgradeSupport.listRanUpgrades();
    }

    @Override
    public List<UnRunChangeSetInfo> getUnRunChangeSets() {
        return  databaseUpgradeSupport.listUnRunChangeSets();
    }
}
