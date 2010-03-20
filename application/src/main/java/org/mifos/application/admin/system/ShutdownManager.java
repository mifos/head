/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.application.admin.system;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.mifos.config.ConfigurationManager;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.FilePaths;

public class ShutdownManager implements Serializable {
    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ROOTLOGGER);
    private Long shutdownTime;
    private final Map<String, HttpSession> activeSessions = new HashMap<String, HttpSession>();

    public synchronized String getStatus(Locale locale) {
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.ADMIN_UI_PROPERTY_FILE, locale);
        if (isShutdownInProgress()) {
            String inProgressString = resources.getString("admin.shutdown.status.inprogress");
            long timeLeft = shutdownTime - System.currentTimeMillis();
            if (timeLeft < 1) {
                timeLeft = 1;
            }
            long hours, minutes, seconds;
            hours = timeLeft / 3600000;
            timeLeft %= 3600000;
            minutes = timeLeft / 60000;
            timeLeft %= 60000;
            seconds = timeLeft / 1000;
            inProgressString = inProgressString.replace("{0}", Long.toString(hours));
            inProgressString = inProgressString.replace("{1}", Long.toString(minutes));
            inProgressString = inProgressString.replace("{2}", Long.toString(seconds));
            return inProgressString;
        }
        return resources.getString("admin.shutdown.status.none");
    }

    public synchronized boolean isShutdownInProgress() {
        return shutdownTime != null;
    }

    public synchronized boolean isShutdownDone() {
        return shutdownTime != null && shutdownTime <= System.currentTimeMillis();
    }

    public synchronized boolean isInShutdownCountdownNotificationThreshold() {
        return shutdownTime != null
                && shutdownTime <= System.currentTimeMillis() + getShutdownCountdownNotificationThreshold();
    }

    public synchronized void scheduleShutdown(long shutdownTimeout) {
        if (shutdownTime != null) {
            return;
        }
        shutdownTime = new DateTimeService().getCurrentJavaDateTime().getTime() + shutdownTimeout;
        logger.warn(computeInterval(shutdownTimeout));
    }

    public synchronized void cancelShutdown() {
        shutdownTime = null;
    }

    public long getShutdownCountdownNotificationThreshold() {
        return ConfigurationManager.getInstance().getLong("GeneralConfig.ShutdownCountdownNotificationThreshold", 1800) * 1000;
    }

    private String computeInterval(long milliseconds) {
        long hours, minutes, seconds, l = milliseconds;
        hours = l / 3600000;
        l %= 3600000;
        minutes = l / 60000;
        l %= 60000;
        seconds = l / 1000;
        l %= 1000;
        // only used for a warning log message, so not externalized
        return String.format("Mifos will be shutting down in %d hours, %d minutes, %d seconds.", hours, minutes,
                seconds);
    }

    public synchronized void sessionCreated(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        activeSessions.put(session.getId(), session);
    }

    public synchronized void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        activeSessions.remove(httpSessionEvent.getSession().getId());
    }

    public synchronized Collection<HttpSession> getActiveSessions() {
        return activeSessions.values();
    }
}
