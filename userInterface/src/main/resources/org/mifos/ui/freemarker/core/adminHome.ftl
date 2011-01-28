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

    <div id="page-content">
      <div id ="adminPageContent">
        <h2 id="adminHome.heading">[@spring.message "administrativeTasks" /]</h2>

            <p>
            [@spring.message "administrativeTasksWelcome" /]
            </p>
            <p>
            Branch id/User id: ${model.request.getSession().getAttribute("UserContext").branchId} / ${model.request.getSession().getAttribute("UserContext").id}
            </p>

            <h3>[@spring.message "manageOrganization" /]</h3>

                <h4>[@spring.message "users" /]</h4>

                    <ul class="navigation-list">
                    <li>[@spring.message "viewUsers" /]</li>
                    <li><a href="createUser.ftl" id="create.user">[@spring.message "defineNewUser" /]</a></li>
                    </ul>

                <h4>[@spring.message "offices" /]</h4>

                    <ul class="navigation-list">
                    <li><a href="viewOffices.ftl" id="view.offices">[@spring.message "viewOffices" /]</a></li>
                    <li>[@spring.message "defineNewOffice" /]</li>
                    </ul>

            <h3>[@spring.message "manageProducts" /]</h3>

                <h4>[@spring.message "manageLoanProducts" /]</h4>

                    <ul class="navigation-list">
                        <li> <a href="viewLoanProducts.ftl" id="view.loan.products">[@spring.message "viewLoanProducts" /]</a> </li>
                        <li> <a href="createLoanProduct.ftl"id="create.loan.product">[@spring.message "defineLoanProduct" /]</a></li>
                    </ul>
        </div>
    </div>
[/@adminLeftPaneLayout]
