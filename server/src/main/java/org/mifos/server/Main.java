package org.mifos.server;

/**
 * Main class.
 * 
 * This is simple, and useful e.g. within Eclipse.
 * A real stand-alone server will probably use the Launcher.
 * 
 * @see ch.vorburger.modudemo.server.launcher.Launcher
 * 
 * @author Michael Vorburger
 */
public class Main {
	
	// TODO Read a conf/server.properties, set to tmp/, configure a logs/ etc.
	
	public static void main(String[] args) throws Exception {
		main();
	}
	
	public static void main() throws Exception {
		final ServerLauncher serverLauncher = new ServerLauncher();
		serverLauncher.startServer();
		// System.exit(r);
	}
}
