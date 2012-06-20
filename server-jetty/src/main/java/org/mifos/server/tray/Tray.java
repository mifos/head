package org.mifos.server.tray;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URL;
import java.util.Locale;

/**
 * System Tray helper class.
 * 
 * @author Michael Vorburger
 */
public class Tray {

	private SystemTray systemTray;
	protected TrayIcon trayIcon;

	/**
	 * Intentionally just silently fails if Tray could not be set up.
	 */
	public void init(String imageResourcePath) {
		try {
			initWhichThrowsAllProblems(imageResourcePath);
		} catch (Throwable t) {
			quit();
			systemTray = null;
		}
	}

	protected void initWhichThrowsAllProblems(final String imageResourcePath) throws Throwable {
		if (!isWanted())
			return;
		
		if (!SystemTray.isSupported())
			return;

		EventQueue.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				systemTray = SystemTray.getSystemTray();

				URL imageURL = getClass().getResource(imageResourcePath);
				if (imageURL == null)
					return; // throw new IOException("Could not find image resource on classpath: " + imageResourcePath);
				Image image = Toolkit.getDefaultToolkit().createImage(imageURL);
				trayIcon = new TrayIcon(image);
				trayIcon.setImageAutoSize(true);
				
				try {
					systemTray.add(trayIcon);
				} catch (AWTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Allows to customize if the Tray is active/used. Default implementation is
	 * true on Windows, and false on Linux, because of the following JVM crash
	 * which appears regularly, but not reliably reproducibly, on a Ubuntu 11.10
	 * Desktop Linux heroica 3.0.0-20-generic #34-Ubuntu SMP.
	 * 
	 * <p>
	 * <code>The program 'java' received an X Window System error.
	 * This probably reflects a bug in the program.
	 * The error was 'BadMatch (invalid parameter attributes)'.
	 * (Details: serial 235 error_code 8 request_code 73 minor_code 0)
	 * (Note to programmers: normally, X errors are reported asynchronously;
	 * that is, you will receive the error a while after causing it.
	 * To debug your program, run it with the --sync command line
	 * option to change this behavior. You can then get a meaningful
	 * backtrace from your debugger if you break on the gdk_x_error() function.)</code>
	 * </p>
	 * 
	 * @return true is Tray is wanted, false if not.
	 */
	protected boolean isWanted() {
		String os = System.getProperty("os.name").toLowerCase(Locale.US);
		if (os != null & os.contains("windows"))
			return true;
		else
			return false;
	}

	public void message(String caption, String text) {
		if (trayIcon != null) {
			trayIcon.setToolTip(caption + " " + text);
			trayIcon.displayMessage(caption, text, MessageType.INFO);
		}
	}

	public void quit() {
		if (systemTray != null && trayIcon != null)
			systemTray.remove(trayIcon);
	}
	
	protected void openURL(String uri) {
		try {
			java.awt.Desktop.getDesktop().browse(new URI(uri));
		} catch (Throwable t) {
		}
	}

	protected static void addMenuItem(PopupMenu popup, String label, ActionListener actionListener) {
		MenuItem item = new MenuItem(label);
		item.addActionListener(actionListener);
		popup.add(item);
	}

}
