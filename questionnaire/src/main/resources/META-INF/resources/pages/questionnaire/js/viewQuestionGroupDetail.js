$(document).ready(function(){

    $("a[href*=editQuestionGroup#]").click(function(event) {
        var editQuestionGroupBtn = document.getElementById('_eventId_editQuestionGroup');
        editQuestionGroupBtn.value = $(this).val();
        event.preventDefault();
        editQuestionGroupBtn.click();
    });

});