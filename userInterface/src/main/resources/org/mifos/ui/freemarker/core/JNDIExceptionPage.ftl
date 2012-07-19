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
<span id="page.id" title="jndiException"></span>
[@widget.crumbs accessDeniedBreadcrumbs /]
<div class="content_panel">
    <div class="marginLeft30 marginTop20">
        <h3 id="jndiHeading">JNDI Error</h3>
        <p id="jndiMessage">Report cannot be generated because JNDI has not been configured. Please follow the instructions below to set up JNDI:

Jetty supports JNDI, but by default it is disabled in a standard server. In order to enable JNDI in Jetty a few changes to the start.ini file in Jetty home directory must be made:

1) Add 'plus' and 'annotations' to OPTIONS, so that it looks something like this:

OPTIONS=Server,jsp,jmx,resources,websocket,ext,plus,annotations

2) Enable the plus configuration by adding the following line:

etc/jetty-plus.xml

at the end of the file next to other similar entries.</p>
        <div class="clear">&nbsp;</div>
   		<div class ="marginLeft20px">
    		[@form.returnToPage  "${Request.urlToBackPage}" "button.back" "jndi.button.back"/]
    	</div> 
    </div>
</div>

[@layout.footer /]
[/@adminLeftPaneLayout]
test