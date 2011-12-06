package org.mifos.platform.rest.config;

import org.apache.commons.lang.StringUtils;

public class RESTConfigKey {
    public static final String REST_APPROVAL_REQUIRED = "REST.approvalRequired";

    public static final String TRUE = "true";

    public static boolean isApprovalRequired(String value) {
        return TRUE.equals(StringUtils.trim(value));
    }
}
