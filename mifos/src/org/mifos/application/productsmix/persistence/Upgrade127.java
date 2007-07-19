package org.mifos.application.productsmix.persistence;

import static org.mifos.framework.persistence.DatabaseVersionPersistence.ENGLISH_LOCALE;
import static org.mifos.framework.security.AddActivity.changeActivityMessage;
import static org.mifos.framework.security.AddActivity.reparentActivity;
import static org.mifos.framework.security.util.resources.SecurityConstants.CAN_DEFINE_PRODUCT_MIX;
import static org.mifos.framework.security.util.resources.SecurityConstants.CAN_EDIT_PRODUCT_MIX;
import static org.mifos.framework.security.util.resources.SecurityConstants.PRODUCT_DEFINITION;
import static org.mifos.framework.security.util.resources.SecurityConstants.PRODUCT_MIX;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.mifos.framework.persistence.Upgrade;

public class Upgrade127 extends Upgrade {

	public Upgrade127() {
		super(127);
	}

	@Override
	public void upgrade(Connection connection) 
	throws IOException, SQLException {
		reparentActivity(connection, PRODUCT_MIX, PRODUCT_DEFINITION);

		changeActivityMessage(connection, PRODUCT_MIX, 
			ENGLISH_LOCALE, "Product Mix");
		changeActivityMessage(connection, CAN_DEFINE_PRODUCT_MIX, 
			ENGLISH_LOCALE, "Can define product mix");
		changeActivityMessage(connection, CAN_EDIT_PRODUCT_MIX, 
			ENGLISH_LOCALE, "Can edit product mix");
		
		execute(connection,
			"ALTER TABLE PRD_OFFERING ADD COLUMN PRD_MIX_FLAG SMALLINT");

		execute(connection,
			"DROP TABLE IF EXISTS PRD_OFFERING_MIX");
		execute(connection,
			"CREATE TABLE PRD_OFFERING_MIX (" +
			"  PRD_OFFERING_MIX_ID  INTEGER AUTO_INCREMENT NOT NULL," +
			"  PRD_OFFERING_ID SMALLINT NOT NULL ," +
			"  PRD_OFFERING_NOT_ALLOWED_ID SMALLINT NOT NULL ," +
			"  CREATED_BY SMALLINT ," +
			"  CREATED_DATE date ," +
			"  UPDATED_BY SMALLINT," +
			"  UPDATED_DATE date ," +
			"  VERSION_NO INTEGER," +
			"  PRIMARY KEY  (PRD_OFFERING_MIX_ID)," +
			"  CONSTRAINT PRD_OFFERING_MIX_PRD_OFFERING_ID_1" +
			"  FOREIGN KEY (PRD_OFFERING_ID) " +
			"  REFERENCES PRD_OFFERING (PRD_OFFERING_ID)" + 
			"  ON DELETE NO ACTION " +
			"  ON UPDATE NO ACTION," +
			"  CONSTRAINT PRD_OFFERING_MIX_PRD_OFFERING_ID_2" + 
			"  FOREIGN KEY (PRD_OFFERING_NOT_ALLOWED_ID) " +
			"  REFERENCES PRD_OFFERING (PRD_OFFERING_ID) " +
			"  ON DELETE NO ACTION " +
			"  ON UPDATE NO ACTION" +
			") ENGINE=InnoDB CHARACTER SET utf8");
		upgradeVersion(connection);
	}

	@Override
	public void downgrade(Connection connection) 
	throws IOException, SQLException {
		execute(connection, "DROP TABLE PRD_OFFERING_MIX");
		
		execute(connection, 
			"ALTER TABLE PRD_OFFERING DROP COLUMN PRD_MIX_FLAG");

		changeActivityMessage(connection, CAN_EDIT_PRODUCT_MIX, 
			ENGLISH_LOCALE, "Can Edit product mix");
		changeActivityMessage(connection, CAN_DEFINE_PRODUCT_MIX, 
			ENGLISH_LOCALE, "Can Define product mix");
		changeActivityMessage(connection, PRODUCT_MIX, 
			ENGLISH_LOCALE, "Product mix");

		reparentActivity(connection, PRODUCT_MIX, null);

		downgradeVersion(connection);
	}

}
