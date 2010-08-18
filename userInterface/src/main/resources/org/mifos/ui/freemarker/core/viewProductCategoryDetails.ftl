[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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
  <div class=" content">
   	<form method="" action="" name="formname">
    <div class="span-21">
  		[@mifos.crumbs breadcrumbs/]
        <div class="clear">&nbsp;</div>
    	<div class="span-20">
        	<div class="span-19">
            	<span class="orangeheading">${detailsDto.productCategoryName}</span><br />
                <span>
                [#switch detailsDto.productCategoryStatusId]
                	[#case 1]
                		<span><img src="pages/framework/images/status_activegreen.gif" /></span>&nbsp;<span>Active</span>
                	[#break]
                	[#case 2]
                		<span><img src="pages/framework/images/status_closedblack.gif" /></span>&nbsp;<span>Inactive</span>
                	[#break]
                [/#switch]
                </span><br />
                <span><span>[@spring.message "manageProducts.defineNewCategory.productType"/]</span>&nbsp;:
                	  <span>
                	  		[#list typeDto as type]
                	  			[#if type.productTypeID == detailsDto.productTypeId]
                	  				${type.productName}
                	  			[/#if]
                	  		[/#list]
                	  </span>
                </span>
                <span class="span-19 rightAlign"><a href="editCategoryInformation.ftl?globalPrdCategoryNum=${globalPrdCategoryNum}">[@spring.message "manageProducts.editCategory.editcategoryinformation"/]</a></span>                
        	</div>        	
        </div>        
        <div class="clear">&nbsp;</div>
        <p class="span-20 ">
        [#if detailsDto.productCategoryDesc?exists && detailsDto.productCategoryDesc != "null" && detailsDto.productCategoryDesc!='']
		${detailsDto.productCategoryDesc}
		[/#if]
		</span>
		<span class="fontnormal"><br>		
		<br />
        </p>
	</div>
   	</form> 
  </div><!--Main Content Ends--> 
[/@adminLeftPaneLayout]