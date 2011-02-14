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

[#macro basic]
[@layout.header "title" /]
[@widget.topNavigationNoSecurity currentTab="Home" /]

<div class="container">
    <br/>
    <div class="content borders span-24">
        <div class="span-1">&nbsp;</div>
        <div class="span-21">
            <br/>
            [#nested]
            <br/>
        </div>
        <div class="span-1 last">&nbsp;</div>
    </div>
</div>

[@layout.footer /]
[/#macro]
