package org.mifos.framework.util;

import java.io.File;

public class ConfigurationLocatorHelper {

    public File getFile(String directory) {
        return new File (directory);
    }

    public String getHomeProperty(String homePropertyName) {
      return System.getProperty(homePropertyName);
    }

}
