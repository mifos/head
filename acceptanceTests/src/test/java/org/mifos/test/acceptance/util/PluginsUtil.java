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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

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
            copyFile(new File(plugin), pluginFile);
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
        File pluginFile = new File(configPath + "plugins" + File.separator + pluginName);
        File temp = File.createTempFile(pluginName, ".tmp");

        copyFile(pluginFile,temp);

        pluginFile.delete();

        return temp.getAbsolutePath();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void movePluginFromTemp(String tempFileName) throws Exception {
        File temp = new File(tempFileName);
        File pluginFile = new File(configPath + "plugins" + File.separator + pluginName);

        copyFile(temp,pluginFile);

        temp.delete();
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
}

