package org.mifos.server.tray;

import org.junit.Test;


public class TrayTest {

	@Test
	public void testEnabledTray() throws InterruptedException {
		final MifosTray testTray = new MifosTray("http://www.mifos.org", "pom.xml");
		testTray.init();
		//Thread.sleep(10000);
		testTray.started(false);
		//Thread.sleep(30000);
		testTray.quit();
	}

	@Test
	public void testDisabledTray() throws InterruptedException {
		final MifosTray testTray = new MifosTray("http://www.mifos.org", "pom.xml") {
			@Override
			protected boolean isWanted() { return false; };
		};
		testTray.init();
		//Thread.sleep(10000);
		testTray.started(false);
		//Thread.sleep(30000);
		testTray.quit();
	}
	
}
