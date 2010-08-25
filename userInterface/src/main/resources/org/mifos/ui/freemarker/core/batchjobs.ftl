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
    <form method="post" name="batchjobs" action="batchjobs.ftl">
        [@mifos.crumbs breadcrumbs /]
        [@spring.showErrors "<br>" /]
        <div class="marginLeft30">
            <div class="span-21">
                <div class="clear">&nbsp;</div>
                <p class="font15"><span class="orangeheading">[@spring.message "systemAdministration.batchjobs.batchjobsInformation" /]</span></p>
                <div class="span-21">
                    <span class="span-11">[@spring.message "systemAdministration.batchjobs.welcometotheMifosbatchjobsArea" /].</span>
                    [@spring.showErrors "<br>" /]
                </div>
                <div class="clear">&nbsp;</div>
                <div>
                    <span class="span-3">&nbsp;</span>
                    <span class="span-7">[@spring.message "systemAdministration.batchjobs.taskStatus" /]: 
                        [#if model.scheduler == true]
                            [@spring.message "systemAdministration.batchjobs.active" /]
                        [#else]
                            [@spring.message "systemAdministration.batchjobs.inactive" /]
                        [/#if]
                    </span>
                    <span class="span-3">
                        [#if model.scheduler == true]
                            <input class="buttn"  type="submit" id="SUSPEND" name="SUSPEND" value="[@spring.message "systemAdministration.batchjobs.suspend"/]"/>
                        [#else]
                            <input class="buttn"  type="submit" id="SUSPEND" name="SUSPEND" value="[@spring.message "systemAdministration.batchjobs.activate"/]"/>
                        [/#if]
                    </span>
                </div>
                <div class="clear">&nbsp;</div>
                <div class="span-21 borderbtm">
                    <p class="font15"><span class="orangeheading">[@spring.message "systemAdministration.batchjobs.scheduledTasks" /]</span></p>
                </div>
                <div class="span-21 borderbtm">
                    [#list model.batchjobs as batchjobs]
                    <div class="span-21 paddingLeft">
                        <span class="span-1">
                            <input type="checkbox" name="ONDEMEND" value="yes" />
                        </span>
                        <span class="span-1">${batchjobs_index + 1}.</span>
                        <span class="span-10"><strong>${batchjobs.name}</strong></span>
                        <span class="span-2">[@spring.message "systemAdministration.batchjobs.taskStatus" /]</span>
                        <span class="span-3">
                            <select class="span-2" name="ACTIVE">
                                [#if batchjobs.state == 0]
                                    <option value="0" SELECTED>[@spring.message "systemAdministration.batchjobs.normal" /]</option>
                                    <option value="1">[@spring.message "systemAdministration.batchjobs.paused" /]</option>
                                [#elseif batchjobs.state == 1]
                                    <option value="0">[@spring.message "systemAdministration.batchjobs.normal" /]</option>
                                    <option value="1" SELECTED>[@spring.message "systemAdministration.batchjobs.paused" /]</option>
                                [/#if]
                            </select>
                        </span>
                    </div>
                    <div class="span-21">
                        <span class="span-2">&nbsp;</span>
                        <span class="span-6">[@spring.message "systemAdministration.batchjobs.previouslyStarted" /]:<br />
                            [#if batchjobs.lastStartTime?datetime != model.date0?datetime]
                                ${batchjobs.lastStartTime?datetime}&nbsp;
                            [#else]
                                [@spring.message "systemAdministration.batchjobs.unknown" /]&nbsp;
                            [/#if]
                        </span>
                        <span class="span-5">[@spring.message "systemAdministration.batchjobs.nextStart" /]:<br />
                            [#if batchjobs.nextStartTime?datetime != model.date0?datetime]
                                ${batchjobs.nextStartTime?datetime}&nbsp;
                            [#else]
                                [@spring.message "systemAdministration.batchjobs.unknown" /]&nbsp;
                            [/#if]
                        </span>
                        <span class="span-6">[@spring.message "systemAdministration.batchjobs.previousRunStatus" /]:<br />
                            [#if batchjobs.lastRunStatus != ""]
                                ${batchjobs.lastRunStatus}&nbsp;
                            [#else]
                                [@spring.message "systemAdministration.batchjobs.unknown" /]&nbsp;
                            [/#if]
                        </span>
                    </div>
                    <div class="span-21">
                        <span class="span-2">&nbsp;</span>
                        <span class="span-6">
                            [@spring.message "systemAdministration.batchjobs.changeCronExpression" /]:<br />
                            [#if batchjobs.cron != ""]
                                <input type="text" id="cron" name="CRON" value="${batchjobs.cron}" />
                            [#else]
                                <input type="text" id="cron" name="CRON" value="N/A" DISABLED/>
                            [/#if]
                        </span>
                        <span class="span-5">
                            [@spring.message "systemAdministration.batchjobs.changePriority" /]:<br />
                            <select class="span-3" name="PRIORITY">
                                [#list 1..10 as i]
                                    [#if batchjobs.priority == i]
                                        <option value="${i}" SELECTED>[@spring.message "systemAdministration.batchjobs.priority" /] ${i}</option>
                                    [#else]
                                        <option value="${i}">[@spring.message "systemAdministration.batchjobs.priority" /] ${i}</option>
                                    [/#if]
                                [/#list]
                            </select>
                        </span>
                        <span class="span-6">
                            [@spring.message "systemAdministration.batchjobs.taskDependsOn" /]:<br />
                            <input type="text" id="depend" name="DEPEND" value="${batchjobs.taskDependency}" />
                        </span>
                    </div>
                    [/#list]
                </div>
                <div class="clear">&nbsp;</div>
                <div class="span-21">
                    <span class="span-1">&nbsp;</span>
                    <span class="span-4">
                        <input class="buttn"  type="submit" id="RUN" name="RUN" value="[@spring.message "systemAdministration.batchjobs.runSelectedTasks"/]"/>
                    </span>
                    <span class="prepend-9">
                        <input class="buttn"  type="submit" id="SAVE" name="SAVE" value="[@spring.message "systemAdministration.batchjobs.saveChanges"/]"/>
                        <input class="buttn"  type="submit" id="RELOAD" name="RELOAD" value="[@spring.message "systemAdministration.batchjobs.reload"/]"/>
                    </span>
                </div>
                <div class="clear">&nbsp;</div>
            </div>
        </div>
    </form>
</div>
<!--Main Content Ends-->
[@mifos.footer/]