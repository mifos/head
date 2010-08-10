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

package org.mifos.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 */
@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class MifosUser implements UserDetails {

    public static final String VIEW_SYSTEM_INFO = "ROLE_VIEW_SYSTEM_INFO";
    public static final String UPDATE_LATENESS_DORMANCY = "ROLE_UPDATE_LATENESS_DORMANCY";
    public static final String CAN_DEFINE_LABELS = "ROLE_CAN_DEFINE_LABELS";
    public static final String ROLE_CAN_DEFINE_PRODUCT_MIX = "ROLE_CAN_DEFINE_PRODUCT_MIX";
    public static final String ROLE_CAN_EDIT_PRODUCT_MIX = "ROLE_CAN_EDIT_PRODUCT_MIX";
    public static final String CAN_EDIT_FUNDS = "ROLE_CAN_EDIT_FUNDS";
    public static final String CAN_CREATE_FUNDS = "ROLE_CAN_CREATE_FUNDS";
    public static final String CAN_DEFINE_ACCEPTED_PAYMENT_TYPES = "ROLE_CAN_DEFINE_ACCEPTED_PAYMENT_TYPES";
    public static final String CAN_DEFINE_HIDDEN_MANDATORY_FIELDS = "ROLE_CAN_DEFINE_HIDDEN_MANDATORY_FIELDS";
    public static final String CAN_OPEN_SHUTDOWN_PAGE = "ROLE_CAN_OPEN_SHUTDOWN_PAGE";
    public static final String CAN_SHUT_DOWN_MIFOS = "ROLE_CAN_SHUT_DOWN_MIFOS";

    private final String username;
    private final byte[] password;
    private final boolean enabled;
    private final boolean accountNonExpired;
    private final boolean credentialsNonExpired;
    private final boolean accountNonLocked;
    private final Collection<GrantedAuthority> authorities;
    private int userId;

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EI_EXPOSE_REP", justification="ignoring for now..")
    public MifosUser(int userId, String username, byte[] password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, Collection<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = authorities;
        this.userId = userId;
    }

    public int getUserId() {
        return this.userId;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return new String(password);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EI_EXPOSE_REP", justification="ignoring for now..")
    public byte[] getPasswordAsBytes() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}