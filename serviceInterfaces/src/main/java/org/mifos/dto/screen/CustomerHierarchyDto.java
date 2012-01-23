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
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD")
public class CustomerHierarchyDto implements Serializable {
    
    private static final long serialVersionUID = 7475645034919589243L;

    private int size;
	
	private List<ClientSearchResultDto> clients = new ArrayList<ClientSearchResultDto>();
	private List<GroupSearchResultDto> groups = new ArrayList<GroupSearchResultDto>();
	private List<CenterSearchResultDto> centers = new ArrayList<CenterSearchResultDto>();
	
	
	public List<ClientSearchResultDto> getClients() {
		return clients;
	}
	public void setClients(List<ClientSearchResultDto> clients) {
		this.clients = clients;
	}
	public List<GroupSearchResultDto> getGroups() {
		return groups;
	}
	public void setGroups(List<GroupSearchResultDto> groups) {
		this.groups = groups;
	}
	public List<CenterSearchResultDto> getCenters() {
		return centers;
	}
	public void setCenters(List<CenterSearchResultDto> centers) {
		this.centers = centers;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}
