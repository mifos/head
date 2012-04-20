[#ftl]
[#--
* Copyright (c) 2005-2011 Grameen Foundation USA
*  All rights reserved.
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
*  implied. See the License for the specific language governing
*  permissions and limitations under the License.
*
*  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
*  explanation of the license and how it is applied.
--]
[#include "layout.ftl"]
[@clientLeftPane "ClientsAndAccounts"]
[@widget.mainCustomerSearchResultDataTable /]
<script>
	$(document).ready(function(){
		var searchString = $('input[name="searchString"]').val();
		var searchResultTable = $('#mainCustomerSearchResultDataTable').dataTable();
		
		$('input[name="searchString"]').keyup(function(){
		    if ( searchString.length > 0 ){
		    	searchResultTable.fnFilter("");
		    }   
		});
		$('select[name="officeId"]').change(function(){
		    searchResultTable.fnFilter("");  
		});
	});
</script>
<span id="page.id" title="MainSearchResults"></span>
<div class="content">
	<div class="fontnormal">
		<form method="GET" action="searchResult.ftl">
			[@form.showAllErrors "customerSearch.*"/]
			[@spring.message "CustomerSearch.searchFor" /]:
			[@spring.bind "customerSearch.searchString" /]
			<input type="text" name="${spring.status.expression}" value="${spring.status.value?if_exists}" maxlength="200"/>
			[@form.singleSelectWithNested "customerSearch.officeId", customerSearch.offices ]
				<option value="0">
					[@spring.message "CustomerSearch.all" /]&nbsp; 
					[@spring.message "${ConfigurationConstants.BRANCHOFFICE}" /]
					[@spring.message "CustomerSearch.s" /]
				</option>
			[/@form.singleSelectWithNested]
			<input type="submit" value="Search" class="buttn"/>
		</form>
	</div>
	<div>
		<img src="pages/framework/images/trans.gif" width="15" height="15">
	</div>
	<div class="search-results">
		<table id="mainCustomerSearchResultDataTable" class="datatable">
			<thead>
				<tr>
					<th style="display: none;">Index</th>
					<th style="display: none;">Customer</th>
				</tr>
			</thead>
		</table>
	</div>
</div>
[/@clientLeftPane]
