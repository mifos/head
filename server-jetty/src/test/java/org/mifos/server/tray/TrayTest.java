/*
 * Copyright (c) 2005-2012 Software Freedom Conservancy
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
