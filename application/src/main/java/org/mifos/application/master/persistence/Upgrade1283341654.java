/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.application.master.persistence;

import org.mifos.application.questionnaire.migration.QuestionnaireMigration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.persistence.Upgrade;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Upgrade1283341654 extends Upgrade {
    private QuestionnaireMigration questionnaireMigration;
    private MifosLogger mifosLogger;

    public Upgrade1283341654() {
        super();
    }

    // Should only be used from tests to inject mocks
    public Upgrade1283341654(QuestionnaireMigration questionnaireMigration, MifosLogger mifosLogger) {
        this.questionnaireMigration = questionnaireMigration;
        this.mifosLogger = mifosLogger;
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        migrateSurveys();
        migrateAdditionalFields();
    }

    // Intended to be invoked from MigrateAction for manual migration of surveys
    public List<Integer> migrateSurveys() {
        return questionnaireMigration.migrateSurveys();
    }

    // Intended to be invoked from MigrateAction for manual migration of additional fields
    public List<Integer> migrateAdditionalFields() {
        return questionnaireMigration.migrateAdditionalFields();
    }

    private void initializeDependencies() {
        if (questionnaireMigration == null) questionnaireMigration = (QuestionnaireMigration) upgradeContext.getBean("questionnaireMigration");
        if (mifosLogger == null) mifosLogger = MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER);
    }

    @Override
    public void setUpgradeContext(ApplicationContext upgradeContext) {
        super.setUpgradeContext(upgradeContext);
        initializeDependencies();
    }
}
