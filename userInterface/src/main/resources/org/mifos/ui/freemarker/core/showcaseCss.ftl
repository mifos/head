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

[@layout.basic]

<style type="text/css">
.css-classname {
    font-family: courier;
    font-weight: bold;
    font-size: 1.0em;
}
.remark {
    color: blue;
}
.code {
    font-family: courier;
    font-weight: bold;
}
hr {
    margin-top: 3em;
}
</style>

<script type="text/javascript">
$(document).ready(function() {
    $('a.source-viewer').click(showSource);
});
function showSource(e) {
    var target = $(this).attr('href');
    var source = $(target).html();
    alert(source);    
    return false;
}
</script>

<h1>CSS Showcase (showcaseCss.ftl)</h1>

<p><span class="css-classname">two-columns</span>:</p><br/>
<span class="remark">Remarks: Applies only to form object. Wrap form elements with a <span class="code">fieldset</span> tag. 
Each label-input pair should be wrapped in a <span class="code">div</span> tag with <span class="code">class="row"</span></span>. 
<!-- <a class="source-viewer" href="#showcase-two-columns-form">View Source</a> -->
</p><br/>
<form class="two-columns" id="showcase-two-columns-form">
    <fieldset>
        <div class="row">
            <label for="input1">label for input 1 goes here:</label>
            <input type="text" name="input1"/>
        </div>
        <div class="row">
            <label for="input2">here is another input:</label>
            <input type="text" name="input2"/>
        </div>
    </fieldset>
    <div class="row">
        [@form.submitButton "widget.form.buttonLabel.continue" /]
        [@form.cancelButton /]
    </div>
</form>
<hr/>

<p><span class="css-classname">product-summary</span><br/>
<span class="remark">Remarks: use this class when content contains "static" information and form input elements.</span></p><br/>
<div class="product-summary">
    <div class="row">
        <div class="attribute">Description:</div>
        <div class="value">Individual Savings account</div>
    </div>
    <div class="row">
        <div class="attribute">Type of Deposit:</div>
        <div class="value">Voluntary</div>
    </div>
    <div class="row">
        <div class="attribute">Max amount per withdrawal:</div>
        <div class="value">100</div>
    </div>
    <div class="row">
        <div class="attribute">Balance used for Interest rate calculation:</div>
        <div class="value">Minimum Balance</div>
    </div>
    <div class="row">
        <div class="attribute">Recommended amount for deposit:</div>
        <div class="value"><input type="text"/></div>
    </div>
</div>
<hr/>

<p><span class="css-classname">preview</span></p>
<span class="remark">Remarks: use this class on the preview step, before the form is saved in the system.</span></p><br/>
<br/>

<div class="preview">
    <div class="row">
        <div class="attribute">Description:</div>
        <div class="value">Individual Savings account</div>
    </div>
    <div class="row">
        <div class="attribute">Type of Deposit:</div>
        <div class="value">Voluntary</div>
    </div>
    <div class="row">
        <div class="attribute">Max amount per withdrawal:</div>
        <div class="value">100</div>
    </div>
    <div class="row">
        <div class="attribute">Balance used for Interest rate calculation:</div>
        <div class="value">Minimum Balance</div>
    </div>
</div>
<hr/>

<p><span class="css-classname">notice</span>:</p><br/>
<div class="notice">You may want to refine your search.</div>
<hr/>

<p><span class="css-classname">validationErrors</span>:</p>
<span class="remark">Remarks: this class will is used by the <span class="code">@form.errors</span> widget. 
You can also use it directly by adding the class to a <span class="code">div</span> to achieve the same result.</span><br/>
<div class="validationErrors">Input value is out of range.</div>
<hr/>

<p><span class="css-classname">error</span>:</p><br/>
<div class="error">A serious problem has occurred.</div>
<hr/>

<p><span class="css-classname">suggestion</span>:</p><br/>
<div class="suggestion">Suggested next steps:</div>
<hr/>

<p><span class="css-classname">standout</span>:</p><br/>
<p>Make text <span class="standout">standout</span>.</p>
<hr/>

<p><span class="css-classname">standout</span> (in <span class="code">&lt;h1&gt;</span>):</p>
<span class="remark">Remarks: make text standout in page title.</span><br/><br/>
<h1>Create Savings Account - <span class="standout">Select a customer</span></h1>
<hr/>

<p><span class="css-classname">account-number</span>:</p><br/>
<p>New account with an ID of <span class="account-number">10000-1234</span> has been created.</p>
<hr/>

[/@layout.basic]