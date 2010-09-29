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

package org.mifos.domain.builders;

import java.util.ArrayList;
import java.util.Collection;

import org.mifos.security.MifosUser;
import org.springframework.security.core.GrantedAuthority;

public class MifosUserBuilder {

    private Integer userId = Integer.valueOf(1);
    private Short branchId = Short.valueOf("1");
    private String username = "mifos";
    private byte[] password = "testmifos".getBytes();
    private boolean enabled  = true;
    private boolean accountNonExpired = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonLocked = true;
    private Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

    public MifosUser build() {
        return new MifosUser(userId, branchId, username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

}
