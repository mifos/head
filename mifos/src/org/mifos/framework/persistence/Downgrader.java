package org.mifos.framework.persistence;

import static org.mifos.framework.persistence.DatabaseVersionPersistence.APPLICATION_VERSION;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.FIRST_NUMBERED_VERSION;

import java.io.PrintStream;
import java.sql.Connection;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.mifos.framework.ApplicationInitializer;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class Downgrader {

	public static void main(String[] args) throws Exception {
		System.out.print("This probably isn't working yet.");
		BasicConfigurator.configure();
		ApplicationInitializer.initializeHibernate();
//		HibernateStartUp.initialize(FilePaths.HIBERNATE_PROPERTIES);
		new Downgrader()
			.run(args, System.out, 
				HibernateUtil.getOrCreateSessionHolder()
					.getSession().connection());
	}

	public void run(String[] args, PrintStream out, Connection connection) 
	throws Exception {
		if (args.length == 0) {
			out.print("Missing argument for what version to downgrade to.\n");
		}
		else if (args.length == 1) {
			String argument = args[0];
			int downgradeTo;
			try {
				downgradeTo = Integer.parseInt(argument);
			}
			catch (NumberFormatException e) {
				out.print("Argument " + argument + " is not a number.\n");
				return;
			}
			run(downgradeTo, out, connection);
		}
		else {
			out.print("Excess argument " + args[1] + ".\n");
		}
	}

	private void run(int downgradeTo, PrintStream out, Connection connection) 
	throws Exception {
		if (downgradeTo < FIRST_NUMBERED_VERSION) {
			out.print("Attempt to downgrade to " + downgradeTo + 
				" which is before " + FIRST_NUMBERED_VERSION + ".\n");
			return;
		}
		
		if (downgradeTo >= APPLICATION_VERSION) {
			out.print("Attempt to downgrade to " + downgradeTo +
				" which is after " + (APPLICATION_VERSION - 1) + ".\n");
			return;
		}

		DatabaseVersionPersistence persistence = 
			new DatabaseVersionPersistence(connection);
		
		int currentVersion = persistence.read();
		if (downgradeTo >= currentVersion) {
			out.print("Already at database version " + currentVersion + ".\n");
			return;
		}

		List<Upgrade> downgrades = 
			persistence.downgrades(downgradeTo, currentVersion);
		for (Upgrade downgrade : downgrades) {
			out.print("Downgrading to " + downgrade.downgradeTo() + "...");
			out.flush();
			downgrade.downgrade(connection);
			out.print("done.\n");
			out.flush();
		}
	}

}
