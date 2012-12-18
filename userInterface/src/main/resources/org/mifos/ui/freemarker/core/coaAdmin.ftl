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

[@adminLeftPaneLayout]
  <!--  Main Content Begins-->
  <div class=" content">

	  <script type="text/javascript">
		function getCoaList(id) {
			var itemId = 'item' + id;
			var listId = 'list' + id;
			var slideSpeed = 200;
			if ($('#' + listId).length > 0) {
				if ($('#' + listId).is(':visible')) {
			        $('#' + listId).slideUp(slideSpeed);
			    } else {
			         $('#' + listId).slideDown(slideSpeed);
			    }
				return ;
			}
			$.get("coaAdminAjax.ftl", { id: id }, function(data) {
					$('#' + itemId).append('<ul id="' + listId + '">');
					$('#' + listId).hide();
					var canModify = data.modifiable;
					$.each(data.coaList, function(key, val) {
						
						var item = '<li id="item' + val.accountId + '"  class="coaItem">';
						item += '<a class="coaA" onclick="getCoaList(' + val.accountId + ')">';
						item += val.glCodeString + ': ' + val.accountName + ' </a>';
						if (canModify) {
							item += '<a href="defineNewCoa.ftl?parentId=' + val.accountId + '" class="coaAction"><b> Add </b></a>'; 
							
							if (val.modifiable) {
								item += '<a href="modifyCoa.ftl?id=' + val.accountId + '" class="coaAction"><b> Modify </b></a>'; 
								item += '<a href="deleteCoa.ftl?id=' + val.accountId + '" class="coaAction"><b> Delete </b></a>'; 
							}
						}
						item += '</li>';
						$('#' + listId).append(item);

					});
					$('#' + itemId).append('</ul>');
					$('#' + listId).slideDown(slideSpeed);
		    	});

		}

	  </script>
	  
      <span id="page.id" title="coaAdmin"></span>
      [@widget.crumb url="admin.chartofaccounts" /]
    <div class="margin20lefttop">
        <p class="font15 orangeheading margin5top10bottom">[@spring.message "coa.coa"/]</p>
      <ul id="coaMenu">
	      [#list COAlist as coa]
	      	<li id="item${coa.accountId}"  class="coaItem">
	      		<a class="coaA"  onclick="getCoaList(${coa.accountId})"> ${coa.glCodeString}: ${coa.accountName} </a> 
	      		[#if canModifyCOA] 
	      			<a href="defineNewCoa.ftl?parentId=${coa.accountId}" class="coaAction"><b>Add</b></a>
	      		[/#if]
	      	</li>
	      [/#list]
      </ul>
	</div>
  </div><!--Main Content Ends-->
[/@adminLeftPaneLayout]