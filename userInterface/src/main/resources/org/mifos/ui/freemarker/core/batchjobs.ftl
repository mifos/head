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
                </div>
                <div class="clear">&nbsp;</div>
                <div class="span-21">
                    <span class="span-3">&nbsp;</span>
                    <span class="span-7">
                        <strong>[@spring.message "systemAdministration.batchjobs.schedulerStatus" /]:</strong>
                        [#if model.scheduler == true]
                            [@spring.message "systemAdministration.batchjobs.active" /]
                        [#else]
                            [@spring.message "systemAdministration.batchjobs.standby" /]
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
                <div class="clear borderbtm">&nbsp;</div>
                [#if model.executedTasks?size > 0]
                    <div class="span-21 borderbtm">
                        <span class="span-1">&nbsp;</span>
                        <span  class="span-10">
                            <p class="font15 orangeheading">[@spring.message "systemAdministration.batchjobs.tasksSendForExecution" /]:</p>
                            <ul>
                                [#list model.executedTasks as task]
                                    <li>${task}</li>
                                [/#list]
                            </ul>
                        </span>
                    </div>
                    <div class="clear">&nbsp;</div>
                [/#if]
                <div class="span-21">
                    <p class="font15"><span class="orangeheading">[@spring.message "systemAdministration.batchjobs.scheduledTasks" /]</span></p>
                </div>
                <div class="span-21 borderbtm">
                    [#list model.batchjobs as batchjobs]
                        <div class="span-21 paddingLeft">
                            <span class="span-1">
                                <input type="checkbox" name="ONDEMAND" value="${batchjobs.name}" />
                            </span>
                            <span class="span-1">${batchjobs_index + 1}.</span>
                            <span class="span-9"><strong>${batchjobs.name}</strong></span>
                            <!-- Secific task pausing to be implemented in the future.
                            <span class="span-8">
                                [@spring.message "systemAdministration.batchjobs.taskStatus" /]:&nbsp;
                                [#if batchjobs.state == 0]
                                    [@spring.message "systemAdministration.batchjobs.normal" /]
                                [#elseif batchjobs.state == 1]
                                    [@spring.message "systemAdministration.batchjobs.paused" /]
                                [/#if]
                            </span>
                            -->
                        </div>
                        <div class="span-21">
                            <span class="span-2">&nbsp;</span>
                            <span class="span-8">
                                [@spring.message "systemAdministration.batchjobs.nextStart" /]:&nbsp;
                                [#if batchjobs.nextStartTime?datetime != model.date0?datetime]
                                    ${batchjobs.nextStartTime?datetime}
                                [#else]
                                    [@spring.message "systemAdministration.batchjobs.unknown" /]
                                [/#if]
                            </span>
                            <span class="span-1">&nbsp;</span>
                            <span class="span-8">
                                [@spring.message "systemAdministration.batchjobs.taskPriority" /]:&nbsp;
                                ${batchjobs.priority}
                            </span>
                        </div>
                        <div class="span-21">
                            <span class="span-2">&nbsp;</span>
                            <span class="span-8">
                                [@spring.message "systemAdministration.batchjobs.previousRunStart" /]:&nbsp;
                                [#if batchjobs.lastStartTime?datetime != model.date0?datetime]
                                    ${batchjobs.lastStartTime?datetime}
                                [#else]
                                    [@spring.message "systemAdministration.batchjobs.unknown" /]
                                [/#if]
                            </span>
                            <span class="span-1">&nbsp;</span>
                            <span class="span-8">
                                [@spring.message "systemAdministration.batchjobs.previousRunStatus" /]:&nbsp;
                                [#if batchjobs.lastRunStatus != ""]
                                    ${batchjobs.lastRunStatus}
                                [#else]
                                    [@spring.message "systemAdministration.batchjobs.unknown" /]
                                [/#if]
                            </span>
                        </div>
                        <div class="span-21">
                            <span class="span-2">&nbsp;</span>
                            <span class="span-8">
                                [@spring.message "systemAdministration.batchjobs.taskType" /]:&nbsp;
                                [#if batchjobs.taskType == ""]
                                    [@spring.message "systemAdministration.batchjobs.unknown" /]
                                [#else]
                                    ${batchjobs.taskType}
                                [/#if]
                            </span>
                            <span class="span-1">&nbsp;</span>
                            <span class="span-8">
                                [#assign cronTrigger = "CronTrigger"]
                                [#assign simpleTrigger = "SimpleTrigger"]
                                [#if batchjobs.taskType == cronTrigger]
                                    [@spring.message "systemAdministration.batchjobs.cronExpression" /]:&nbsp;${batchjobs.frequency}
                                [#elseif batchjobs.taskType == simpleTrigger]
                                    [@spring.message "systemAdministration.batchjobs.repeatInterval" /]:&nbsp;
                                    ${batchjobs.frequency}&nbsp;[@spring.message "systemAdministration.batchjobs.milisec" /]
                                [/#if]
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
                </div>
                <div class="clear">&nbsp;</div>
            </div>
        </div>
    </form>
</div>
<!--Main Content Ends-->
[@mifos.footer/]