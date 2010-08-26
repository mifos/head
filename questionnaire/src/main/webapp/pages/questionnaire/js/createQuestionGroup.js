var CreateQuestionGroup = {};

CreateQuestionGroup.removeSection = function (sectionName){
    var sectionToDeleteBtn = document.getElementById('_eventId_deleteSection');
    sectionToDeleteBtn.value = sectionName;
    sectionToDeleteBtn.click();
}

CreateQuestionGroup.removeQuestion = function (sectionName, questionId){
    var questionToDeleteBtn = document.getElementById('_eventId_deleteQuestion');
    questionToDeleteBtn.value = questionId;
    document.getElementById('questionSection').value = sectionName;
    questionToDeleteBtn.click();
}

$(document).ready(function () {
	$('#txtListSearch').keyup(function(event) {
		var search_text = $('#txtListSearch').val();
		var rg = new RegExp(search_text,'i');
		$('#questionList li label').each(function(){
 			if($.trim($(this).html()).search(rg) == -1) {
				$(this).parent().css('display', 'none');
 				$(this).css('display', 'none');
				$(this).next().css('display', 'none');
				$(this).next().next().css('display', 'none');
			}
			else {
				$(this).parent().css('display', '');
				$(this).css('display', '');
				$(this).next().css('display', '');
				$(this).next().next().css('display', '');
			}
		});
	});

	$("input[name$='addOrSelectFlag']").change(function(event) {
        if ($("input[name$='addOrSelectFlag']:checked").val() == 'true') {
            $("#addQuestionDiv").show();
            $("#selectQuestionsDiv").hide();
        }
        else if ($("input[name$='addOrSelectFlag']:checked").val() == 'false') {
            $("#addQuestionDiv").hide();
            $("#selectQuestionsDiv").show();
        }
        else {
            $("#addQuestionDiv").hide();
            $("#selectQuestionsDiv").hide();
        }
    });

    $("input[name$='addOrSelectFlag']").change();
});
