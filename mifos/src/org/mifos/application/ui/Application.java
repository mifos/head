package org.mifos.application.ui;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

/**
 * Provide a way to run servlets while bypassing much of the slow
 * deploy->unpack war->start application cycle
 * 
 * We don't yet try to deal with databases, hibernate, jsps,
 * database upgrades, and various other complications.  Maybe someday.
 * Right now useful for working on {@link Dispatcher}.
 */
public class Application {
	
	public static void main(String[] args) throws Exception {
		Server server = new Server(8888);

		Context root = new Context(server, "/", Context.SESSIONS);
		root.addServlet(new ServletHolder(new Dispatcher()), "/*");

		server.start();
	}

}
