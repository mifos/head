function setDisable() {
       var disabled = true;
       var value = document.genericActionForm.answerType.value;
       if (value == '1' || value == '4') {
           disabled = false;
       }
       document.getElementById('choice').disabled = disabled;
       document.getElementById('AddButton').disabled = disabled;
}

function clickCancel() {
	form = document.forms['genericActionForm'];
	form.action='adminAction.do?method=load';
	form.submit();
}


function submitQuestionForm(method) {
	form = document.forms['genericActionForm'];
	form.action='questionsAction.do?method=' + method;
	form.submit();
}

function submitForm(form, action) {
	form = document.forms[form];
	form.action=action;
	form.submit();
}

	
function submitSurveyForm(method) {
	form = document.forms['genericActionForm'];
	form.action='surveysAction.do?method=' + method;
	form.submit();
}

function submitSurveyInstanceForm(method) {
	form = document.forms['genericActionForm'];
	form.action='surveyInstanceAction.do?method=' + method;
	form.submit();
}
	
function disableSubmitButtonOnEmptyQuestionList() {
    var qtable = document.getElementById('questions.table');
    if (qtable != null && qtable.rows.length <= 2) {
        var submitBtn = document.getElementById('_eventId_createQuestions');
        if (submitBtn != null) {
            submitBtn.disabled = true;
            submitBtn.className = 'disabledbuttn';
        }
    }
}

function removeSection(sectionName){
    var sectionToDeleteBtn = document.getElementById('_eventId_deleteSection');
    sectionToDeleteBtn.value = sectionName;
    sectionToDeleteBtn.click();
}

function removeQuestion(sectionName, questionId){
    var questionToDeleteBtn = document.getElementById('_eventId_deleteQuestion');
    questionToDeleteBtn.value = questionId;
    document.getElementById('questionSection').value = sectionName;
    questionToDeleteBtn.click();
}
