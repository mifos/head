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

package org.mifos.dto.screen;

import java.util.List;

import org.mifos.dto.domain.UserDetailDto;

public class SystemUserSearchResultsDto {

    private final Integer totalCount;
    private final int firstResult;
    private final Integer page;
    private final Integer pageSize;
    private final List<UserDetailDto> pagedUserDetails;

    public SystemUserSearchResultsDto(Integer totalCount, int firstResult, Integer page, Integer pageSize, List<UserDetailDto> pagedUserDetails) {
        this.totalCount = totalCount;
        this.firstResult = firstResult;
        this.page = page;
        this.pageSize = pageSize;
        this.pagedUserDetails = pagedUserDetails;
    }

    public String getCurrentRange() {

        int upperRange = this.firstResult + this.pageSize;

        if (upperRange > totalCount) {
            upperRange = totalCount;
        }

        return this.firstResult + "-" + upperRange;
    }

    public boolean isNotLastPage() {
        return firstResult+pageSize < (totalCount);
    }

    public Integer getTotalCount() {
        return this.totalCount;
    }

    public int getFirstResult() {
        return this.firstResult;
    }

    public Integer getPage() {
        return this.page;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public List<UserDetailDto> getPagedUserDetails() {
        return this.pagedUserDetails;
    }
}