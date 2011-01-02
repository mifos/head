package org.mifos.server;

import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * WebAppContext allowing to register additional Configurations.
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=330189
 * 
 * @author Michael Vorburger
 */
public class WebContextWithExtraConfigurations extends WebAppContext {

	public WebContextWithExtraConfigurations(String webApp,String contextPath) {
		super(webApp, contextPath);
	}

	public <T extends Configuration> void replaceConfiguration(Class<T> toReplace, Configuration newConfiguration) throws Exception {
		loadConfigurations(); // Force loading of default configurations
		final Configuration[] configs = getConfigurations();
		for (int i = 0; i < configs.length; i++) {
			if (configs[i].getClass().equals(toReplace)) {
				configs[i] = newConfiguration;
				return;
			}
		}
		throw new IllegalStateException(toReplace.toString() + " not found");
	}
	
	public void addConfiguration(Configuration configuration) throws Exception {
		loadConfigurations(); // Force loading of default configurations
		final Configuration[] configs = getConfigurations();
		final Configuration[] newConfig = new Configuration[configs.length + 1];
		newConfig[configs.length] = configuration;
		setConfigurations(newConfig);
	}

}
