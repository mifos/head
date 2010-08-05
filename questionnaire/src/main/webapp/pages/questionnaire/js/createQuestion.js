var CreateQuestion = {};

CreateQuestion.disableSubmitButtonOnEmptyQuestionList = function () {
    var qtable = document.getElementById('questions.table');
    if (qtable != null && qtable.rows.length <= 1) {
        var submitBtn = document.getElementById('_eventId_createQuestions');
        if (submitBtn != null) {
            submitBtn.disabled = true;
            submitBtn.className = 'disabledbuttn';
        }
    }
}

jQuery.expr[':'].regex = function(elem, index, match) {
    var matchParams = match[3].split(','),
        validLabels = /^(data|css):/,
        attr = {
            method: matchParams[0].match(validLabels) ?
                        matchParams[0].split(':')[0] : 'attr',
            property: matchParams.shift().replace(validLabels,'')
        },
        regexFlags = 'ig',
        regex = new RegExp(matchParams.join('').replace(/^\s+|\s+$/g,''), regexFlags);
    return regex.test(jQuery(elem)[attr.method](attr.property));
}

$(document).ready(function(){

    $("a[href*=removeQuestion#]").click(function(event) {
        var questionToDeleteBtn = document.getElementById('_eventId_removeQuestion');
        questionToDeleteBtn.value = $(this).attr("title");
        event.preventDefault();
        questionToDeleteBtn.click();
    });

    $("a[href*=removeChoice#]").click(function(event) {
        var choiceToDeleteBtn = document.getElementById('_eventId_removeChoice');
        choiceToDeleteBtn.value = $(this).attr("choiceIndex");
        event.preventDefault();
        choiceToDeleteBtn.click();
    });

    $("a[href*=removeSmartChoice#]").click(function(event) {
        var choiceToDeleteBtn = document.getElementById('_eventId_removeChoice');
        choiceToDeleteBtn.value = $(this).attr("choiceIndex");
        event.preventDefault();
        choiceToDeleteBtn.click();
    });

    $("a[href*=removeSmartChoiceTag#]").click(function(event) {
        var tagToDeleteBtn = document.getElementById('_eventId_removeChoiceTag');
        tagToDeleteBtn.value = $(this).attr("choiceTagIndex");
        event.preventDefault();
        tagToDeleteBtn.click();
    });

    $(":regex(id, addSmartChoiceTag_[0-9]+)").click(function(event) {
        var addSmartChoiceTagBtn = document.getElementById('_eventId_addSmartChoiceTag');
        addSmartChoiceTagBtn.value = $(this).attr("choiceIndex");
        event.preventDefault();
        addSmartChoiceTagBtn.click();
    });

    $("#currentQuestion\\.type").bind("change keypress click blur", function(){
        var selectedOption = $(this).val();
        if(selectedOption == $("#multiSelect").val() || selectedOption == $("#singleSelect").val()) {
            $("#choiceDiv").show();
        }else{
            $("#choiceDiv").hide();
        }
        if(selectedOption == $("#number").val()) {
            $("#numericDiv").show();
        }else{
            $("#numericDiv").hide();
        }
        if(selectedOption == $("#smartSelect").val()) {
            $("#choiceTagsDiv").show();
        }else{
            $("#choiceTagsDiv").hide();
        }
    });

    $("#currentQuestion\\.currentChoice").bind("past cut keyup blur", function(){
        var val = $(this).val();
        var addChoiceButton = $("#_eventId_addChoice");
        if(val==null||val.match(/^(\s)*$/)) {
            addChoiceButton.attr('disabled', 'disabled');
            addChoiceButton.attr('class', 'disabledbuttn');
        }else{
            addChoiceButton.attr('disabled', '');
            addChoiceButton.attr('class', 'buttn');
        }
    });

    $("#currentQuestion\\.currentSmartChoice").bind("past cut keyup blur", function(){
        var val = $(this).val();
        var addChoiceButton = $("#_eventId_addSmartChoice");
        if(val==null||val.match(/^(\s)*$/)) {
            addChoiceButton.attr('disabled', 'disabled');
            addChoiceButton.attr('class', 'disabledbuttn');
        }else{
            addChoiceButton.attr('disabled', '');
            addChoiceButton.attr('class', 'buttn');
        }
    });

    $(":regex(id, currentQuestion.currentSmartChoiceTags\\[[0-9]+\\])").bind("past cut keyup blur", function(){
        var val = $(this).val();
        var addTagButton = $(this).parent().children()[1];
        if(val==null||val.match(/^(\s)*$/)) {
            addTagButton.setAttribute('disabled', 'disabled');
            addTagButton.setAttribute('class', 'disabledbuttn');
        }else{
            addTagButton.removeAttribute('disabled');
            addTagButton.setAttribute('class', 'buttn');
        }
    });

    CreateQuestion.disableSubmitButtonOnEmptyQuestionList();
    $("#currentQuestion\\.type").change();
    $("#currentQuestion\\.currentChoice").keyup();
    $("#currentQuestion\\.currentSmartChoice").keyup();
    $(".numeric").keyfilter(/[\d\-]/);
});