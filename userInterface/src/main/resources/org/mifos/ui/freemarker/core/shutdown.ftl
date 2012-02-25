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
[@adminLeftPaneLayout]<!--Container Begins-->
<!--  Main Content Begins-->
<div class="content">
    <form method="post" name="shutdown" action="shutdown.ftl">
    [@i18n.formattingInfo /]
        [@widget.crumbs breadcrumbs /]
        [@spring.bind "shutdownFormBean" /]
        
        <div class="marginLeft30">
            <div class="span-24">
                <div class="clear">&nbsp;</div>
                <div class="span-23 borderbtm">
                    <p class="font15"><span class="orangeheading">[@spring.message "systemAdministration.shutdown.shutdownInformation" /]</span></p>
                    <div class="span-23">
                        <span class="span-11">[@spring.message "systemAdministration.shutdown.welcometotheMifosshutdownmanagementArea" /].</span>
                    </div>
                    [@form.showAllErrors "shutdownFormBean.*"/]
                    <div class="clear">&nbsp;</div>
                    <div class="span-23">
                        <span class="span-7">[@spring.message "systemAdministration.shutdown.shutdownStatus" /]</span>
                    <span class="span-11">${model.shutdownStatus}</span>
                    </div>
                    <div class="span-23">
                        <span class="span-7">[@spring.message "systemAdministration.shutdown.scheduleIn" /]</span>
                    <span class="span-11">
                        [@spring.bind "shutdownFormBean.timeout" /]
                        
                        [#if shutdownFormBean.timeout??]
                        <input type="text" maxlength="7" id="timeout" name="${spring.status.expression}" value="${shutdownFormBean.timeout?c}" class="separatedNumber"/>&nbsp;[@spring.message "systemAdministration.shutdown.seconds" /]
                        [#else]
                        <input type="text" maxlength="7" id="timeout" name="${spring.status.expression}" value="" class="separatedNumber" />&nbsp;[@spring.message "systemAdministration.shutdown.seconds" /]
                        [/#if]
                    </span>
                    </div>
                    <div class="clear">&nbsp;</div>
                </div>
                <div class="clear">&nbsp;</div>
                <div class="prepend-9">
                    [#if model.submitButtonDisabled]
                    <input class="disabledbuttn2 submit"  type="submit" disabled="true" id="START" name="START" value="[@spring.message "systemAdministration.shutdown.startShutdown"/]"/>
                    <input class="cancelbuttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "systemAdministration.shutdown.cancelShutdown"/]"/>
                    [#else]
                    <input class="buttn submit"  type="submit" id="START" name="START" value="[@spring.message "systemAdministration.shutdown.startShutdown"/]"/>
                    <input class="disabledbuttn2" type="submit" disabled="true" id="CANCEL" name="CANCEL" value="[@spring.message "systemAdministration.shutdown.cancelShutdown"/]"/>
                    [/#if]
                    <input class="buttn"  type="submit" name="REFRESH" value="[@spring.message "systemAdministration.shutdown.refresh"/]"/>
                </div>
                <div class="span-23 borderbtm">
                    <p class="font15"><span class="orangeheading">[@spring.message "systemAdministration.shutdown.activeSessions" /]</span></p>
                </div>
                [#list model.activeSessions as activeSession]
                <div class="span-24 paddingLeft">
                    <span class="span-1 "><strong>${activeSession_index + 1}.</strong></span>
                    <span class="span-15 ">${activeSession.offices}/&nbsp;<strong>${activeSession.names}</strong></span>
                </div>
                <div class="span-24 paddingLeft">
                    <span class="span-1 ">&nbsp;</span>
                    <span class="span-15 "><strong>${activeSession.activityTime}</strong>&nbsp;${activeSession.activityContext}</span>
                </div>
                [/#list]
            </div>
        </div>
    </form>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]