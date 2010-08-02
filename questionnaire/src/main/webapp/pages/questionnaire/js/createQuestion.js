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
    });

    $("#currentQuestion\\.choice").bind("past cut keyup blur", function(){
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

    CreateQuestion.disableSubmitButtonOnEmptyQuestionList();
    $("#currentQuestion\\.type").change();
    $("#currentQuestion\\.choice").keyup();
    $(".numeric").keyfilter(/[\d\-]/);
});