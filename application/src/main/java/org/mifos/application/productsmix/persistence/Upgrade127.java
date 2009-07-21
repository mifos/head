/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.productsmix.persistence;

import static org.mifos.framework.persistence.DatabaseVersionPersistence.ENGLISH_LOCALE;
import static org.mifos.framework.security.AddActivity.changeActivityMessage;
import static org.mifos.framework.security.AddActivity.reparentActivity;
import static org.mifos.framework.security.util.SecurityConstants.CAN_DEFINE_PRODUCT_MIX;
import static org.mifos.framework.security.util.SecurityConstants.CAN_EDIT_PRODUCT_MIX;
import static org.mifos.framework.security.util.SecurityConstants.PRODUCT_DEFINITION;
import static org.mifos.framework.security.util.SecurityConstants.PRODUCT_MIX;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;

public class Upgrade127 extends Upgrade {

    public Upgrade127() {
        super(127);
    }

    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
        reparentActivity(connection, PRODUCT_MIX, PRODUCT_DEFINITION);

        changeActivityMessage(connection, PRODUCT_MIX, ENGLISH_LOCALE, "Product Mix");
        changeActivityMessage(connection, CAN_DEFINE_PRODUCT_MIX, ENGLISH_LOCALE, "Can define product mix");
        changeActivityMessage(connection, CAN_EDIT_PRODUCT_MIX, ENGLISH_LOCALE, "Can edit product mix");

        execute(connection, "ALTER TABLE PRD_OFFERING ADD COLUMN PRD_MIX_FLAG SMALLINT");

        execute(connection, "DROP TABLE IF EXISTS PRD_OFFERING_MIX");
        execute(connection, "CREATE TABLE PRD_OFFERING_MIX ("
                + "  PRD_OFFERING_MIX_ID  INTEGER AUTO_INCREMENT NOT NULL," + "  PRD_OFFERING_ID SMALLINT NOT NULL ,"
                + "  PRD_OFFERING_NOT_ALLOWED_ID SMALLINT NOT NULL ," + "  CREATED_BY SMALLINT ,"
                + "  CREATED_DATE date ," + "  UPDATED_BY SMALLINT," + "  UPDATED_DATE date ,"
                + "  VERSION_NO INTEGER," + "  PRIMARY KEY  (PRD_OFFERING_MIX_ID),"
                + "  CONSTRAINT PRD_OFFERING_MIX_PRD_OFFERING_ID_1" + "  FOREIGN KEY (PRD_OFFERING_ID) "
                + "  REFERENCES PRD_OFFERING (PRD_OFFERING_ID)" + "  ON DELETE NO ACTION " + "  ON UPDATE NO ACTION,"
                + "  CONSTRAINT PRD_OFFERING_MIX_PRD_OFFERING_ID_2" + "  FOREIGN KEY (PRD_OFFERING_NOT_ALLOWED_ID) "
                + "  REFERENCES PRD_OFFERING (PRD_OFFERING_ID) " + "  ON DELETE NO ACTION " + "  ON UPDATE NO ACTION"
                + ") ENGINE=InnoDB CHARACTER SET utf8");
        upgradeVersion(connection);
    }

}
