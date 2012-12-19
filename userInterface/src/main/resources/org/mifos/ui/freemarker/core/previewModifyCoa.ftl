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
[@adminLeftPaneLayout] <!--  Main Content Begins-->
<span id="page.id" title="PreviewModifyCoa"></span>
<div class="content">
	<div>
	     <div class="breadcrumb">
            <a href="AdminAction.do?method=load">
            [@spring.message "admin" /]</a>
            &nbsp;/
            <a href="coaAdmin.ftl">
               [@spring.message "admin.chartofaccounts"  /]
            </a>&nbsp;/
            <span class="fontBold">[@spring.message "admin.modifyCoa"  /]</span>
        </div>
    <div class="span-24">
    
        <div class="margin20lefttop">
            <p class="font15"><span
                    class="orangeheading">[@spring.message "admin.modifyCoa" /]</span></p>
                    
             [@form.showAllErrors "formBean.*"/]
             <ul class="error">
	             [#if error?exists]
	             	<li><b>[@spring.message error?default("")/] </b></li>
	             [/#if]
             </ul>
             
            <div class="span-20">
            	<span class="span-6 fontBold">[@spring.message "coa.coaName" /]</span>
            	<span class="span-4">${formBean.coaName}</span>
            </div>
			<br />
			<div class="span-20">
            	<span class="span-6 fontBold">[@spring.message "coa.glCode" /]</span>
            	<span class="span-4">${formBean.glCode}</span>
            </div>
            <br />
            <div class="span-20">
            	<span class="span-6 fontBold">[@spring.message "coa.parentGlCode" /]</span>
            	<span class="span-4">${formBean.parentGlCode}</span>
            </div>

			<div class="clear">&nbsp;</div>	
            <form method="post" action="previewModifyCoa.ftl" name="formname">
            [@spring.bind "formBean.coaName" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
            [@spring.bind "formBean.glCode" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
			[@spring.bind "formBean.accountId" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>	
			[@spring.bind "formBean.parentGlCode" /]
                <input type="hidden" name="${spring.status.expression}" value="${spring.status.value?default("")}"/>
                
               <input type="submit" class="buttn2" style="height:15px; font-size:8pt;" name="EDIT"
                           value="[@spring.message "coa.edit"/]"/>
                
                <div class="buttonsSubmitCancel marginTop15 width95prc">
                    <input class="buttn" type="submit" id="coa.button.submit" name="submit"
                           value="[@spring.message "submit"/]"/>
                    <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
            </form>
        </div>
                </div>
    </div>
</div>
[/@adminLeftPaneLayout]