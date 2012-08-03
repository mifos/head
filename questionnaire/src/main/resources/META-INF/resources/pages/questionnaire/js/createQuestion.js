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
        if(selectedOption == "multiSelect" || selectedOption == "singleSelect") {
            $("#choiceDiv").show();
        }else{
            $("#choiceDiv").hide();
        }
        if(selectedOption == "number") {
            $("#numericDiv").show();
        }else{
            $("#numericDiv").hide();
        }
        if(selectedOption == "smartSelect" || selectedOption == "smartSingleSelect") {
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

    $(":regex(id, currentQuestion.currentSmartChoiceTags\\[[0-9]+\\])").bind("focus", function(){
        $(this).keyfilter(/[a-z0-9_]/i);
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
    $("#status").hide();
    $("#currentQuestion\\.type").change();
    $("#currentQuestion\\.currentChoice").keyup();
    $("#currentQuestion\\.currentSmartChoice").keyup();
    $(".numeric").keyfilter(/[\d\-]/);
});