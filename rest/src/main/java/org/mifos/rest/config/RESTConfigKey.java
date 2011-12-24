package org.mifos.rest.config;

import org.apache.commons.lang.StringUtils;
import org.mifos.config.servicefacade.ConfigurationServiceFacade;

public class RESTConfigKey {
    public static final String REST_APPROVAL_REQUIRED = "REST.approvalRequired";

    public static final String TRUE = "true";

    public static boolean isApprovalRequired(ConfigurationServiceFacade configurationServiceFacade) {
        String approvalConfigValue = configurationServiceFacade.getConfig(RESTConfigKey.REST_APPROVAL_REQUIRED);
        return TRUE.equals(StringUtils.trim(approvalConfigValue));
    }
}
