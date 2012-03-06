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

[#-- 
Renders an HTML input element.

    path       : spring bind path
    id         : ID of the element
    attributes : any additional attributes for the element (such as class, size, etc)
    fieldType  : Field type: text, hidden 
--]
[#macro input path id="" attributes="" fieldType="text"]
[@spring.bind path /]
    <input id="${id}" type="${fieldType}" name="${spring.status.expression}" value="${spring.status.value?default("")}" ${attributes} />
[/#macro]

[#-- 
Renders an HTML select element.

    path        : spring bind path 
    options     : An map of id (option value) to name (option label) or just a simple List of values (id or string).
    selectPrompt: A value to display when the select input is first rendered. For example, "--Select--".
    attributes  : Extra HTML attributes that should be added to the select element. For example, "class=blah" and "id=blah".
--]
[#macro singleSelectWithPrompt path options id="" selectPrompt="" attributes=""]
    [@spring.bind path/]
    <select id="${id}" name="${spring.status.expression}" ${attributes}>
    	[#if selectPrompt?length > 0]
        	<option value="" [@spring.checkSelected ""/]>[@spring.message "${selectPrompt}"/]</option>
        [/#if]
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
Renders an HTML select element with nested option tags (if any).

    path        : spring bind path 
    options     : An map of id (option value) to name (option label) or just a simple List of values (id or string).
    attributes  : Extra HTML attributes that should be added to the select element. For example, "class=blah" and "id=blah".
--]
[#macro singleSelectWithNested path options id="" attributes=""]
    [@spring.bind path/]
    <select id="${id}" name="${spring.status.expression}" ${attributes}>
	    [#nested]
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
              <li><span class="standout">${error}</span></li>
            [/#list]
            </ul>
        </div>
    [/#if]
[/#macro]

[#-- 
Renders a HTML label tag. Handy when building a mandatory field label.

    for            : the input element this label applies to. has the same meaning as <label for="">
    required       : indicate whether the label applies to a required/mandatory field
    htmlAttributes : any extra HTML attributes that should be included, such as ID, CLASS
 --]
[#macro label for required=false htmlAttributes=""]
    <label for="${for}" ${htmlAttributes}>[#if required]<span class="mandatory">*</span>[/#if][#nested]</label>
[/#macro]

[#-- 
Renders a submit button.

    buttonLabel : The value shown on the button.
    webflowEvent: If this button is part of a form that drives webflow, you may specify the event Id here.
--]
[#macro submitButton label="widget.form.buttonLabel.submit" id="" webflowEvent=""]
    [#if webflowEvent?length == 0]
        [#assign name="" /]
    [#else]
        [#assign name="_eventId_${webflowEvent}" /]
    [/#if]
    <input id="${id}" type="submit" class="submit" value="[@spring.message label /]" name="${name}" />
[/#macro]

[#-- 
Renders a cancel button.

    buttonLabel : The value shown on the button.
    webflowEvent: If this button is part of a form that drives webflow, you may specify the event Id here.
--]
[#macro cancelButton label="widget.form.buttonLabel.cancel" id="" webflowEvent=""]
    [#if webflowEvent?length == 0]
        [#assign name="" /]
    [#else]
        [#assign name="_eventId_${webflowEvent}" /]
    [/#if]
    <input id="${id}" type="submit" class="cancel" value="[@spring.message label /]" name="${name}" />
[/#macro]
[#--]]
Renders a disabled submit button.

    buttonLabel : The value shown on the button.
--]
[#macro disabledButton label="widget.form.buttonLabel.submit" id=""]
	<input id="${id}" type="submit" class="disabledbuttn" value="[@spring.message label /]"
		disabled="disabled" />
[/#macro]

[#-- 
Render a group of checkboxes, using the given separator to separate them. 
(Copied from macros.ftl)

    path       : spring bind path
    options    : checkbox values. values can be given as a hash (value => label) or a simple sequence
    separator  : HTML fragment that gets inserted between each checkbox 
    attributes : extra HTML attributes to be added to each checkbox
--]
[#macro formCheckboxes path options separator attributes=""]
    [@spring.bind path /]
    [#if options?is_hash]
        [#list options?keys as value]
        [#assign id="${spring.status.expression}${value_index}"]
        [#assign isSelected = spring.contains(spring.status.value?default([""]), value)]
        <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
        <label for="${id}" style="float:none;">${options[value]?html}</label>${separator}
        [/#list]
    [#else]
        [#list options as value]
        [#assign id="${spring.status.expression}${value_index}"]
        [#assign isSelected = spring.contains(spring.status.value?default([""]), value)]
        <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
        <label for="${id}" style="float:none;">${value?html}</label>${separator}
        [/#list]
    [/#if]
    <input type="hidden" name="_${spring.status.expression}" value="on"/>
[/#macro]

[#-- 
Render a group of checkboxes, using the given separator to separate them. Used by questionnaire widget. For general purpose,
use "checkboxes" macro.
(Copied from macros.ftl)

    path       : spring bind path
    options    : checkbox values. values can be given as a hash (value => label) or a simple sequence
    separator  : HTML fragment that gets inserted between each checkbox 
    attributes : extra HTML attributes to be added to each checkbox
--]
[#macro checkboxesWithTags path options separator="" attributes=""]
    [@spring.bind path /]
    [#list options as option]
        [#if option.tags?exists && option.tags?size > 0]
            [#list option.tags as tag]
                [#assign id="${spring.status.expression}${option_index}${tag_index}"]
                [#assign isSelected = spring.contains(spring.status.value?default([""]), option.value + ":" + tag)]
                <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${option.value?html}:${tag?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
                <label for="${id}" choice="${option.value}" tag="${tag}">${option.value?html}&nbsp;:&nbsp;${tag?html}</label>${separator}
            [/#list]
        [#else]
            [#assign id="${spring.status.expression}${option_index}"]
            [#assign isSelected = spring.contains(spring.status.value?default([""]), option.value)]
            <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${option.value?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
            <label for="${id}" choice="${option.value}" tag="">${option.value?html}</label>${separator}
        [/#if]
    [/#list]
    <input type="hidden" name="_${spring.status.expression}" value="on"/>
[/#macro]

[#--
Renders a group of radio buttons.

    path: spring bind path
    options: an array of values. TODO add support for hashes.
    separator  : HTML fragment that gets inserted between each radio button 
    attributes : extra HTML attributes to be added to each radio button
--]
[#macro radioButtons path options separator attributes=""]
    [@spring.bind path /]
    [#list options as value]
    [#assign id="${spring.status.expression}${value_index}"]
    <input type="radio" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if spring.stringStatusValue == value] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
    <label for="${id}">${value?html}</label>${separator}
    [/#list]
[/#macro]


[#macro showAllErrors path]
    [@spring.bind path/]
    [#if spring.status.errorMessages?size > 0]
    <div id="error.messages" class="marginLeft30">
        <ul class="error">
         [#list spring.status.errorMessages as error]
          <li><b>${error}</b></li>
         [/#list]
        </ul>
    </div>
    [/#if]
[/#macro]

[#macro formSingleSelectWithPrompt path options selectPrompt attributes=""]
    [@spring.bind path/]
    <select id="${spring.status.expression}" name="${spring.status.expression}" ${attributes}>
        <option value="" [@spring.checkSelected ""/]>${selectPrompt}</option>
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


[#macro formMultiSelect path options attributes=""]
    [@spring.bind path/]
    <input type="hidden" name="_${spring.status.expression}" value="true"/>
    <select multiple="multiple" id="${spring.status.expression}" name="${spring.status.expression}" ${attributes}>
        [#list options?keys as value]
        [#assign isSelected = spring.contains(spring.status.value?default([""]), value)]
        <option value="${value?html}"[#if isSelected] selected="selected"[/#if]>${options[value]?html}</option>
        [/#list]
    </select>
[/#macro]

[#macro formCheckbox path attributes=""]
    [@spring.bind path /]
    [#assign id="${spring.status.expression}"]
    [#assign isSelected = spring.status.value?? && spring.status.value?string=="true"]
    <input type="hidden" name="_${id}" value="true"/>
    <input type="checkbox" id="${id}" name="${id}"[#if isSelected] checked="checked"[/#if] ${attributes}/>
[/#macro]

[#macro formCheckboxes path options separator attributes=""]
    [@spring.bind path /]
    [#if options?is_hash]
        [#list options?keys as value]
        [#assign id="${spring.status.expression}${value_index}"]
        [#assign isSelected = spring.contains(spring.status.value?default([""]), value)]
        <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
        <label for="${id}" style="float:none;">${options[value]?html}</label>${separator}
        [/#list]
    [#else]
        [#list options as value]
        [#assign id="${spring.status.expression}${value_index}"]
        [#assign isSelected = spring.contains(spring.status.value?default([""]), value)]
        <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
        <label for="${id}" style="float:none;">${value?html}</label>${separator}
        [/#list]
    [/#if]
    <input type="hidden" name="_${spring.status.expression}" value="on"/>
[/#macro]

[#macro formCheckboxesWithTags path options separator attributes=""]
    [@spring.bind path /]
    [#list options as option]
        [#if option.tags?exists && option.tags?size > 0]
            [#list option.tags as tag]
                [#assign id="${spring.status.expression}${option_index}${tag_index}"]
                [#assign isSelected = spring.contains(spring.status.value?default([""]), option.value + ":" + tag)]
                <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${option.value?html}:${tag?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
                <label for="${id}" choice="${option.value}" tag="${tag}" style="float:none;">${option.value?html}&nbsp;:&nbsp;${tag?html}</label>${separator}
            [/#list]
        [#else]
            [#assign id="${spring.status.expression}${option_index}"]
            [#assign isSelected = spring.contains(spring.status.value?default([""]), option.value)]
            <input type="checkbox" id="${id}" name="${spring.status.expression}" value="${option.value?html}"[#if isSelected] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
            <label for="${id}" choice="${option.value}" tag="" style="float:none;">${option.value?html}</label>${separator}
        [/#if]
    [/#list]
    <input type="hidden" name="_${spring.status.expression}" value="on"/>
[/#macro]

[#macro formRadioButtons path options separator attributes=""]
    [@spring.bind path /]
    [#list options as value]
    [#assign id="${spring.status.expression}${value_index}"]
    <input type="radio" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if spring.stringStatusValue == value] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
    <label for="${id}" style="float:none;">${value?html}</label>${separator}
    [/#list]
[/#macro]

[#macro boolRadioButtons path options separator attributes=""]
    [@spring.bind path/]
    [#list options?keys as value]
    [#assign id="${spring.status.expression}${value_index}"]
    <input type="radio" id="${id}" name="${spring.status.expression}" value="${value?html}"[#if spring.stringStatusValue == value] checked="checked"[/#if] ${attributes}[@spring.closeTag/]
    <label for="${id}" style="float:none;">${options[value]?html}</label>${separator}
    [/#list]
[/#macro]
