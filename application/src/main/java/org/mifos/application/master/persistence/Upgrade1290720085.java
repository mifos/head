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
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.DateTimeService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Upgrade1290720085 extends Upgrade {
    private QuestionnaireMigration questionnaireMigration;

    public Upgrade1290720085() {
        super();
    }

    // Should only be used from tests to inject mocks
    public Upgrade1290720085(QuestionnaireMigration questionnaireMigration) {
        this.questionnaireMigration = questionnaireMigration;
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        long time1 = new DateTimeService().getCurrentDateTime().getMillis();
        logMessage(" - migrating surveys...");
        migrateSurveys();
        logMessage("    - took " + (new DateTimeService().getCurrentDateTime().getMillis() - time1) + " msec");

        time1 = new DateTimeService().getCurrentDateTime().getMillis();
        logMessage(" - migrating additional fields...");
        migrateAdditionalFields();
        logMessage("    - took " + (new DateTimeService().getCurrentDateTime().getMillis() - time1) + " msec");
    }

    // Intended to be invoked from MigrateAction for manual migration of surveys
    public List<Integer> migrateSurveys() {
        return questionnaireMigration.migrateSurveys();
    }

    // Intended to be invoked from MigrateAction for manual migration of additional fields
    public void migrateAdditionalFields() {
        questionnaireMigration.migrateAdditionalFields();
    }

    private void initializeDependencies() {
        if (questionnaireMigration == null) {
            questionnaireMigration = (QuestionnaireMigration) upgradeContext.getBean("questionnaireMigration");
        }
    }

    @Override
    public void setUpgradeContext(ApplicationContext upgradeContext) {
        if (upgradeContext == null) {
            // workaround for LatestTestAfterCheckpointIntegrationTest
            upgradeContext = new ClassPathXmlApplicationContext(
                                    "classpath:/org/mifos/config/resources/applicationContext.xml",
                                    "classpath:META-INF/spring/QuestionnaireContext.xml",
                                    "classpath:/org/mifos/config/resources/apponly-services.xml");
        }
        super.setUpgradeContext(upgradeContext);
        initializeDependencies();
    }

    private void logMessage(String finalMessage) {
        System.out.println(finalMessage);
        getLogger().info(finalMessage);
    }
}
