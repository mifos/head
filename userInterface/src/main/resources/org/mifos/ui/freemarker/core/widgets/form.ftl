[#ftl]
[#--
* Copyright (c) 2005-2010 Grameen Foundation USA
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

[#import "/spring.ftl" as spring /]

[#-- FIXME: these macros are copied from newblueprintmacros.ftl. They've been moved here for better organization. Delete from newblueprintmacros.ftl. --]

[#-- 
Renders an HTML select element.

    path        : spring bind path 
    options     : An map of id (option value) to name (option label) or just a simple List of values (id or string).
    selectPrompt: A value to display when the select input is first rendered. For example, "--Select--".
    attributes  : Extra HTML attributes that should be added to the select element. For example, "class=blah" and "id=blah".
--]
[#macro singleSelectWithPrompt path options selectPrompt attributes=""]
    [@spring.bind path/]
    <select id="${spring.status.expression}" name="${spring.status.expression}" ${attributes}>
        <option value="" [@spring.checkSelected ""/]>[@spring.message "${selectPrompt}"/]</option>
        [#if options?is_hash]
            [#list options?keys as value]
            <option value="${value?html}"[@spring.checkSelected value/]>${options[value]?html}</option>
            [/#list]
        [#else]
            [#list options as value]
            <option value="${value?html}"[@spring.checkSelected value/]>${value?html}</option>
            [/#list]
        [/#if]
    </select>
[/#macro]

[#-- 
Display form validation errors in one place.

    springBindPath: The path for Spring bind. For example, "userFormBean.*" 
                    See http://static.springsource.org/spring/docs/1.1.5/taglib/tag/BindTag.html
--]
[#macro errors springBindPath]
    [@spring.bind springBindPath/]
    [#if spring.status.errorMessages?size > 0]
        <div class="validationErrors">
            <ul>
            [#list spring.status.errorMessages as error]
              <li><b>${error}</b></li>
            [/#list]
            </ul>
        </div>
    [/#if]
[/#macro]

[#-- 
Renders a submit button.

    buttonLabel : The value shown on the button.
    webflowEvent: If this button is part of a form that drives webflow, you may specify the event Id here.
--]
[#macro submitButton buttonLabel webflowEvent=""]
    [#if webflowEvent?length == 0]
        [#assign name="" /]
    [#else]
        [#assign name="_eventId_${webflowEvent}" /]
    [/#if]
    <input type="submit" class="submit" value="[@spring.message buttonLabel /]" name="${name}" />
[/#macro]

[#-- 
Renders a cancel button.

    buttonLabel : The value shown on the button.
    webflowEvent: If this button is part of a form that drives webflow, you may specify the event Id here.
--]
[#macro cancelButton buttonLabel webflowEvent=""]
    [#if webflowEvent?length == 0]
        [#assign name="" /]
    [#else]
        [#assign name="_eventId_${webflowEvent}" /]
    [/#if]
    <input type="submit" class="cancel" value="[@spring.message buttonLabel /]" name="${name}" />
[/#macro]
