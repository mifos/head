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

import java.io.Serializable;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID"}, justification="should disable at filter level and also for pmd - not important for us")
public class SearchDetailsDto implements Serializable {

	private final Integer totalCount;
	private final int firstResult;
	private final Integer page;
	private final Integer pageSize;
	
	public SearchDetailsDto(Integer totalCount, int firstResult, Integer page, Integer pageSize) {
        this.totalCount = totalCount;
        this.firstResult = firstResult;
        this.page = page;
        this.pageSize = pageSize;
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
		return totalCount;
	}

	public int getFirstResult() {
		return firstResult;
	}

	public Integer getPage() {
		return page;
	}

	public Integer getPageSize() {
		return pageSize;
	}
}