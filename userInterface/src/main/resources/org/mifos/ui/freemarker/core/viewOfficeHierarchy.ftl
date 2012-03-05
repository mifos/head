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
<span id="page.id" title="view_office_hierarchy"></span>
<div class="content">
[@widget.crumbs breadcrumbs/]
        <form method="post" name="viewofficehierarchy" action="viewOfficeHierarchy.ftl">
            <div class="margin20lefttop">
                <div class="fontBold"><span class="orangeheading">[@spring.message "admin.viewofficehierarchy"/]</span>
                </div>
                <p class="padding5topbottom"><span>[@spring.message "viewOfficeHierarchy.theofficehierarchycanhaveminimumtwoandmaximumfivelevels"/]</span></p>
            [@spring.bind "formBean" /]
            [@spring.showErrors "<li>" "fontnormalRedBold" /]
                <p class="padding5topbottom"><span>[@spring.message "viewOfficeHierarchy.checkthelevelstobeincluded"/]. </span></p>

                <div><span
                        class="fontBold">[@spring.message "viewOfficeHierarchy.note"/]:&nbsp;</span><span>[@spring.message "viewOfficeHierarchy.thehighestandlowesthierarchylevelscannotberemovedfromthesystem"/]</span>
                </div>
                <div class="margin10topbottom">
                    <div class="prepend-2">

    <span>
    [@spring.formCheckbox "formBean.headOffice" "disabled=disabled"/]
    [@spring.message "ftlDefinedLabels.viewOfficeHierarchy.headOffice"  /]
    </span><br/>

    <span>
    [@spring.formCheckbox "formBean.regionalOffice" /]
    [@spring.message "ftlDefinedLabels.viewOfficeHierarchy.regionalOffice"  /]
    </span><br/>

     <span>
     [@spring.formCheckbox "formBean.subRegionalOffice" /]
     [@spring.message "ftlDefinedLabels.viewOfficeHierarchy.divisionalOffice" /]
     </span><br/>

     <span>
     [@spring.formCheckbox "formBean.areaOffice" /]
     [@spring.message "ftlDefinedLabels.viewOfficeHierarchy.areaOffice" /]
     </span><br/>

     <span>
     [@spring.formCheckbox "formBean.branchOffice" "disabled" /]
     [@spring.message "ftlDefinedLabels.viewOfficeHierarchy.branchOffice" /]
     </span><br/>

                    </div>
                </div>
                <div class="clear">&nbsp;</div>

                <div class="buttonsSubmitCancel">
                    <input class="buttn" type="submit" name="submit" value="[@spring.message "submit"/]"/>
                    <input class="buttn2" type="submit" id="CANCEL" name="CANCEL" value="[@spring.message "cancel"/]"/>
                </div>
            </div>
        </form>

</div><!--Main Content Ends-->

[/@adminLeftPaneLayout]