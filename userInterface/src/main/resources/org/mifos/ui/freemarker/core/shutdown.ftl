[#ftl]
[#import "spring.ftl" as spring]
[#import "blueprintmacros.ftl" as mifos]

[@mifos.header "title" /]
[@mifos.topNavigationNoSecurity currentTab="Admin" /]
<div class="sidebar htTotal">
    [#include "adminLeftPane.ftl" /]
</div>
<!--  Main Content Begins-->
<div class=" content leftMargin180">
    <form method="post" name="shutdown" action="shutdown.ftl">
        [@mifos.crumbs breadcrumbs /]
        [@spring.bind "formBean" /]
        [@spring.showErrors "<br>" /]
        <div class="marginLeft30">
            <div class="span-24">
                <div class="clear">&nbsp;</div>
                <div class="span-23 borderbtm">
                    <p class="font15"><span class="orangeheading">[@spring.message "systemAdministration.shutdown.shutdownInformation" /]</span></p>
                    <div class="span-23">
                        <span class="span-11">[@spring.message "systemAdministration.shutdown.welcometotheMifosshutdownmanagementArea" /].</span>
                        [@spring.showErrors "<br>" /]
                    </div>
                    <div class="clear">&nbsp;</div>
                    <div class="span-23">
                        <span class="span-7">[@spring.message "systemAdministration.shutdown.shutdownStatus" /]</span>
    	            <span class="span-11">${model.shutdownStatus}</span>
                        [@spring.showErrors "<br>" /]
                    </div>
                    <div class="span-23">
                        <span class="span-7">[@spring.message "systemAdministration.shutdown.scheduleIn" /]</span>
    	            <span class="span-11">
    	            	[@spring.bind "formBean.timeout" /]
						<input type="text" maxlength="7" id="timeout" name="${spring.status.expression}" value="${formBean.timeout?c}" />&nbsp;[@spring.message "systemAdministration.shutdown.seconds" /]
					</span>
                        [@spring.showErrors "<br>" /]
                    </div>
                    <div class="clear">&nbsp;</div>
                </div>
                <div class="clear">&nbsp;</div>
                <div class="prepend-9">
                    [#if model.submitButtonDisabled]
                    <input class="disabledbuttn"  type="submit" disabled="true" id="START" name="START" value="[@spring.message "systemAdministration.shutdown.startShutdown"/]"/>
                    <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "systemAdministration.shutdown.cancelShutdown"/]"/>
                    [#else]
                    <input class="buttn"  type="submit" id="START" name="START" value="[@spring.message "systemAdministration.shutdown.startShutdown"/]"/>
                    <input class="disabledbuttn" type="submit" disabled="true" id="CANCEL" name="CANCEL" value="[@spring.message "systemAdministration.shutdown.cancelShutdown"/]"/>
                    [/#if]
                    <span class="prepend-1">
                        <input class="buttn"  type="submit" name="REFRESH" value="[@spring.message "systemAdministration.shutdown.refresh"/]"/>
                    </span>
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
        [@mifos.footer/]