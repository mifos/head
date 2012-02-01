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
<span id="page.id" title="Exception"></span>
<script src="pages/js/uncaughtException.js" type="text/javascript"></script>
<div class="content_panel">
    <p class="error">[@spring.message "system.unhandledErrorHeading" /]</p>
    [#if stackString??]
        <a class="stackTrace" href="javascript:void();">View stack trace</a>
        <a class="stackTrace" href="javascript:void();" style="display:none">Hide stack trace</a>
    [/#if]
    <br/>
    <br/>
    <div class="stackTrace" style="display:none">
        [#if stackString??]
            <pre>${stackString}</pre>
        [/#if]
    </div>
</div>
[/@adminLeftPaneLayout]
