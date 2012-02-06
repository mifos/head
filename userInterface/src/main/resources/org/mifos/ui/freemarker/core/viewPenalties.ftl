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
<div class=" content">
    <span id="page.id" title="viewPenalties"></span>
[@widget.crumb "penalty.viewPenalties"/]
    <div class="margin20lefttop">
        <p class="font15 orangeheading margin5top10bottom">[@spring.message "penalty.viewPenalties"/]</p>
        <p>[@spring.message "Penalties.viewPenaltiesInstruction"/] <a id="define.new.penalty" href="defineNewPenalty.ftl">[@spring.message "Penalties.smallDefineNewPenalty"/]</a></p>
        <div class="clear">&nbsp;</div>
        
        <div id="loan.penalties" class="span-15 width80prc" id="loanPenaltiesList">
            <p class="fontnormalbold">[@spring.message "Penalties.loanPenalties"/]</p>
            [#list loanPenalties as penalty]
                <span><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></span>
                <a href="viewPenalty.ftl?penaltyId=${penalty.id}">${penalty.name}</a>
                [#if penalty.status.id == 2]
                    <span>
                        <img src="pages/framework/images/status_closedblack.gif"/>[@spring.message "inactive"/]
                    </span>
                [/#if]
                <br/>
            [/#list]
        </div>
        <div class="clear">&nbsp;</div>
        
        <div id="saving.penalties" class="span-15 width80prc" id="savingPenaltiesList">
            <p class="fontnormalbold">[@spring.message "Penalties.savingPenalties"/]</p>
            [#list savingPenalties as penalty]
                <span><img src="pages/framework/images/bullet_circle.gif" width="9" height="11"></span>
                <a href="viewPenalty.ftl?penaltyId=${penalty.id}">${penalty.name}</a>
                [#if penalty.status.id == 2]
                    <span>
                        <img src="pages/framework/images/status_closedblack.gif"/>[@spring.message "inactive"/]
                    </span>
                [/#if]
                <br/>
            [/#list]
        </div>
    </div>
</div><!--Main Content Ends-->
[/@adminLeftPaneLayout]