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
<span id="page.id" title="admin"></span>
[@layout.header "maxUploadSizeExceededTitle" /]
[@clientLeftPane "ClientsAndAccounts"]
<div class="content_panel">
<div class="marginLeft30 marginTop20">    
    <h3 id="maxUploadSizeExceededHeading">[@spring.message "maxUploadSizeExceededHeading" /]</h3>
    <p id="maxUploadSizeExceededMessage" class="red">[@spring.message "maxUploadSizeExceededMessage" /]</p>
    <div class="clear">&nbsp;</div>
        <div class ="marginLeft20px">
            [@form.returnToPage  "javascript:history.go(-1)" "button.back" "pageNotFound.button.back"/]
        </div>
   </div>
</div>
[@layout.footer /] 
[/@clientLeftPane]