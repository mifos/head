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
<html>
<body>
    <div class="left-pane">
      <div class="left-pane-header">[@spring.message "quickStart" /]</div>
      <div class="left-pane-content">
          <h2>[@spring.message "manageClients" /]</h2>
          <div class="left-pane-heading-group">
             [@spring.message "createNewGroup" /]<br/>
             <a href="createClient.ftl" >[@spring.message "createNewClient" /]</a>
          </div>
          <h2>[@spring.message "manageAccounts" /]</h2>
          <div class="left-pane-heading-group">
             <a href="createLoan.ftl" id="left.pane.create.loan">[@spring.message "openNewLoanAccount" /]</a>
          </div>
         </div>
    </div>
    </body>
</html>