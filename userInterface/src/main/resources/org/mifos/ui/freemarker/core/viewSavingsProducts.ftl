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
	<!--  Main Content Begins-->  
  	<div class=" content">
	  	[@mifos.crumb url="admin.viewSavingsproducts" /]
	  	<p>&nbsp;&nbsp;</p><br/>
	  	<p class="font15 orangeheading">[@spring.message "admin.viewSavingsproducts"/]</p>
		
		<p>[@spring.message "manageLoanProducts.viewSavingsProducts.clickonaSavingsproductbelowtoviewdetailsandmakechangesor"/] <a href="defineSavingsProduct.ftl">[@spring.message "admin.definenewSavingsproduct"/]</a></p>	    
	    [#list products as product]
	        <div>
	        	<img src="pages/framework/images/bullet_circle.gif" width="9" height="11"/> <a href="viewEditSavingsProduct.ftl?productId=${product.prdOfferingId}">${product.prdOfferingName}</a>
	        	[#switch product.prdOfferingStatusId]
                	[#case 2]
                	[#break]
                	[#case 5]
                	<span><img src="pages/framework/images/status_closedblack.gif" />[@spring.message "inactive"/]</span>
                	[#break]
                [/#switch] 
	        </div>
	    [/#list]   
  	</div>
  	<!--Main Content Ends-->
[/@adminLeftPaneLayout]