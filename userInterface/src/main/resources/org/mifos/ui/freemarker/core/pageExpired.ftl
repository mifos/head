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
<span id="page.id" title="PageExpiredException"></span>
<div class="content_panel">
    <p class="error">[@spring.message "system.pageExpiredException" /]</p>
    <div class="span-8 last" style="text-align:left;">
		<span class="fontnormalbold">
			[@spring.message "system.toContinue"/][@spring.message "label.colon" /]
		</span>
		<table width="80%" border="0" cellspacing="0" cellpadding="3">
			<tr class="fontnormal">
				<td><img src="pages/framework/images/smallarrowleft.gif" width="11" height="11">
					[@spring.message "CustomerSearch.searchontheleft"/]
				</td>
				<td><img src="pages/framework/images/smallarrowtop.gif" width="11" height="11">
					[@spring.message "CustomerSearch.tabsattop"/]
				</td>
			</tr>
		</table>
	</div>
    <br/>
    <br/>
    <div class="stackTrace" style="display:none">
        [#if stackString??]
            <pre>${stackString}</pre>
        [/#if]
    </div>
</div>
[/@adminLeftPaneLayout]