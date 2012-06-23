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

import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MifosTray extends Tray {

	private String logFile;
	private String mifosURL;

	public MifosTray(String mifosURL, String logFile) {
		super();
		this.logFile = logFile;
		this.mifosURL = mifosURL;
	}

	public void init() {
		init("/mifos-tray.png");
		message("Mifos", "is starting...");
	}

	public void started(boolean openBrowser) {
		if (trayIcon == null)
			return;
		
		PopupMenu popup = new PopupMenu();
		addMenuItem(popup, "Open Mifos", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openURL(mifosURL);
			}
		});
		addMenuItem(popup, "Open Log", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openURL(logFile);
			}
		});
		addMenuItem(popup, "Quit", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quit();
			}
		});
		trayIcon.setPopupMenu(popup);
		
		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openURL(mifosURL);
			}
		});
		
		message("Mifos", "is running");
		if (openBrowser)
			openURL(mifosURL);
	}

	@Override
	public void quit() {
		message("Mifos", "has been stopped");
		super.quit();
	}

}
