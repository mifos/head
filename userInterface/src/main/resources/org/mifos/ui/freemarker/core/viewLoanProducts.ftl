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
  <div class="content leftMargin180">
  [@mifos.crumbs breadcrumbs/]
  <p>&nbsp;&nbsp;</p>
  	<form method="POST" action="viewLoanproducts.ftl" name="viewLoanproducts">
    	<p class="marginLeft30 font15 orangeheading">[@spring.message "admin.viewLoanProducts"/]</p>
    	<p class="marginLeft30">[@spring.message "manageLoanProducts.viewLoanProducts.clickonaLoanproductbelowtoviewdetailsandmakechangesor"/] <a href="defineLoanProducts.ftl">[@spring.message "admin.definenewLoanproduct"/]</a></p>
    	<div class="marginTop15">
    	<div class="span-22 marginLeft30"> 
    		<ul>
    		[#list formBean as loanProduct]
    		<li><a href="viewEditLoanProduct.ftl?productId=${loanProduct.prdOfferingId}">${loanProduct.prdOfferingName}</a></li>
    		[/#list]
        	</ul>	
    	</div> 
    </div>
    </form> 
  </div><!--Main Content Ends-->
[/@adminLeftPaneLayout]