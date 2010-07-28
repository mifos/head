$(document).ready(function(){

    $("#questionGroupId").bind("change keypress click", function(){
        var selectedOption = $(this).val();
        var addChoiceButton = $("#_eventId_selectQuestionnaire");
        if(selectedOption == 'selectOne') {
            addChoiceButton.attr('disabled', 'disabled');
            addChoiceButton.attr('class', 'disabledbuttn');
        }else{
            addChoiceButton.attr('disabled', '');
            addChoiceButton.attr('class', 'buttn');
        }
    });

    $("#questionGroupId").change();
});