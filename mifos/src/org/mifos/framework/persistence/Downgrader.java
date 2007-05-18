package org.mifos.framework.persistence;

import java.io.PrintStream;

import static org.mifos.framework.persistence.DatabaseVersionPersistence.APPLICATION_VERSION;
import static org.mifos.framework.persistence.DatabaseVersionPersistence.FIRST_NUMBERED_VERSION;

public class Downgrader {

	public static void main(String[] args) {
		new Downgrader().run(args, System.out);
	}

	public void run(String[] args, PrintStream out) {
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
			run(downgradeTo, out);
		}
		else {
			out.print("Excess argument " + args[1] + ".\n");
		}
	}

	private void run(int downgradeTo, PrintStream out) {
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
	}

}
