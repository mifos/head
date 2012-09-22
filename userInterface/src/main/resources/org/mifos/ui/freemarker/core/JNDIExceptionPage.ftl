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
       	[#if configureDwDatabase=="true"]
        <h3 id="jndiHeading" class="red">JNDI Error</h3>
        <h5 id="jndiHeadingMessage" class="red">Report cannot be generated because JNDI has not been configured.</h5><br>  
        <p id="jndiMessage">
        	Please follow the instructions below to set up JNDI:
        	<br>		
			Jetty supports JNDI, but by default it is disabled in a standard server. In order to enable JNDI in Jetty a few changes to the start.ini file in Jetty home directory must be made:
			<br><br>
			1) Add 'plus' and 'annotations' to OPTIONS, so that it looks something like this:
			<br><br>
			OPTIONS=Server,jsp,jmx,resources,websocket,ext,plus,annotations
			<br><br>
			2) Enable the plus configuration by adding the following line:
			<br><br>
			etc/jetty-plus.xml
			<br><br>
			at the end of the file next to other similar entries.</p>
		[#else]
		<h3 id="jndiHeading" class="red">DW database Error</h3>
        <h5 id="jndiHeadingMessage" class="red">Report cannot be generated because Your warehouse database is not configured.</h5> 
			<p>If you want configure data warehouse in mifos you must:
		<br>		
			Add new data warehouse database:
	<br><br>
			Connect to your database:<br>
			mysql -u root -p
	<br><br>
			Create database:<br>
			CREATE DATABASE &ltname your warehouse database&gt;
	<br><br>
			Add all permissions for mifos user:<br>
			GRANT ALL on &ltname your warehouse database&gt.* to 'mifos'@'localhost' identified by 'mifos';
	<br><br>
			Reload all permissions in mysql:<br>
			FLUSH PRIVILEGES;
	<br><br>
			Load the database schemas:<br>
			mysql -u mifos -pmifos &ltname your warehouse database&gt < db/sql/load_mifos_datawarehouse.sql<br>
			mysql -u mifos -pmifos &ltname your warehouse database&gt < db/sql/load_mifos_datawarehouse_stored_procedures.sql<br>
			mysql -u mifos -pmifos &ltname your warehouse database&gt < db/sql/load_dw_ppi_survey.sql<br>
			mysql -u mifos -pmifos &ltname your warehouse database&gt < db/sql/load_ppi_poverty_lines.sql<br>
	<br>
		Add a new setting in your local.properties file:<br>
		main.database.dbPentahoDW=&ltname your warehouse database&gt
		
		[/#if]
        <div class="clear">&nbsp;</div>
   		<div class ="marginLeft20px">
    		[@form.returnToPage  "${Request.urlToBackPage}" "button.back" "jndi.button.back"/]
    	</div> 
    </div>
</div>

[@layout.footer /]
[/@adminLeftPaneLayout]