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
<!--  Main Content Begins-->
<span id="page.id" title="DefineNewCoa"></span>

<div class="content">
	<div>
	     <div class="breadcrumb">
            <a href="AdminAction.do?method=load">
            [@spring.message "admin" /]</a>
            &nbsp;/
            <a href="coaAdmin.ftl">
               [@spring.message "admin.chartofaccounts"  /]
            </a>&nbsp;/
            <span class="fontBold">[@spring.message "admin.defineNewCoa"  /]</span>
        </div>
        <div class="margin20lefttop">
            <p class="font15 margin10topbottom"><span
                    class="orangeheading">[@spring.message "admin.defineNewCoa" /]</span>
            </p>

            <div class="margin5topbottom"><span
                    class="red">* </span>[@spring.message "fieldsmarkedwithanasteriskarerequired" /] </div>

            <form method="post" action="defineNewCoa.ftl" name="formname">
            [@spring.bind "formBean" /]
            [@form.showAllErrors "formBean.*"/]
                <div style="margin-top:25px">
                    <div class="prepend-3 span-22 last margin5bottom">
                    <span class="span-4 rightAlign" style="margin-top:6px;" ><span
                            class="red"> * </span>[@spring.message "coa.coaName" /]</span>
                	<span class="span-4 margin5topbottom">
                	[@spring.bind "formBean.coaName" /]
                    <input type="text" id="coa.input.coaName" name="${spring.status.expression}"
                           value="${spring.status.value?default("")}" size="100"  maxlength="150" />
                	</span>
                    </div>
                    
                    <div class="prepend-3 span-22 last margin5bottom">
                    <span class="span-4 rightAlign" style="margin-top:6px;" ><span
                            class="red"> * </span>[@spring.message "coa.glCode" /]</span>
                	<span class="span-4 margin5topbottom">
                	[@spring.bind "formBean.glCode" /]
                    <input type="text" id="coa.input.glCode" name="${spring.status.expression}"
                           value="${spring.status.value?default("")}" size="50"  maxlength="50" />
                	</span>
                    </div>
                    
                    
				</div>
                <div class="buttonsSubmitCancel">
                    <input class="buttn" type="submit" id="coa.button.preview" name="preview"
                           value="[@spring.message "preview"/]"/>
                    <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
            </form>
        </div>
	</div>
</div>
<!--Main Content Ends-->
[/@adminLeftPaneLayout]