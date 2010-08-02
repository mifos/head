$(document).ready(function(){

    $("a[href*=editQuestionnaire#]").click(function(event) {
        var editQuestionGroupBtn = document.getElementById('_eventId_questionnaire');
        editQuestionGroupBtn.value = $(this).attr("questionGroupInstanceDetailIndex");
        event.preventDefault();
        editQuestionGroupBtn.click();
    });

});