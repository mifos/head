/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.test.acceptance.util;


import org.mifos.framework.util.ConfigurationLocator;
import java.io.File;

public class PluginsUtil {
    private boolean copyPlugin = false;
    private final String pluginName;
    private String configPath = new ConfigurationLocator().getConfigurationDirectory();

    public PluginsUtil(String pluginName) {
        this.pluginName = pluginName;

        if (!configPath.endsWith(File.separator)) {
            configPath += File.separator;
        }
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void loadPlugin() throws Exception {
        String plugin = PluginsUtil.class.getResource("/jars/" + pluginName).getFile();
        File pluginFile = new File(configPath + "plugins" + File.separator + pluginName);
        File folderPlugins = new File(configPath + "plugins");

        if (!folderPlugins.exists()) {
            folderPlugins.mkdir();
        }

        if (!pluginFile.exists()) {
            pluginFile.createNewFile();
            FileUtil.copyFile(new File(plugin), pluginFile);
            copyPlugin = true;
        }
    }

    public void unloadPlugin() {
        File pluginFile = new File(configPath + "plugins" + File.separator + pluginName);

        if (copyPlugin && pluginFile.exists()) {
            pluginFile.delete();
        }
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public String movePluginToTemp() throws Exception {
    	return FileUtil.moveConfigFileToTemp("plugins" + File.separator + pluginName);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void movePluginFromTemp(String tempFileName) throws Exception {
    	FileUtil.moveConfigFileFromTemp("plugins" + File.separator + pluginName, tempFileName);
    }
}

